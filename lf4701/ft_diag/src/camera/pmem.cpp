/******************************************************************************
  @file  fbtest.c
  @brief This file contains test code to verify all functionalities of msm_fb

  DESCRIPTION
  fbtest is msm framebuffer test program.  It opens all frambuffers (/dev/fb*)
  and executes the msm specific fb ioctls as well as the standard linux fb
  ioctls.

  INITIALIZATION AND SEQUENCING REQUIREMENTS

 -----------------------------------------------------------------------------
 Copyright (c) 2007 QUALCOMM Incorporated
 All Rights Reserved. QUALCOMM Proprietary and Confidential
 -----------------------------------------------------------------------------

******************************************************************************/
#undef LOG_TAG
#define LOG_TAG "FT_CAMERA_PMEM"

#include <utils/Log.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>

#include <linux/fb.h>
#include <linux/msm_mdp.h>
#include "pmem.h"

#define BYD_FTPMEM_TRACE  ALOGE

struct pmemDev *PMEM;

static int PMEMinit = 0;

/*******************************************************/
// Function Prototypes
/******************************************************/
static int openPMEMDev(struct pmemDev *);
static int allocPMEM(struct pmemDev *, unsigned int);


/****************************************************************/

static int openPMEMDev(struct pmemDev *thisPMEM)
{

    BYD_FTPMEM_TRACE("\n openPMEMDev() \n");
    
#ifdef    USE_ION

    thisPMEM->ion_device_fd = open (thisPMEM->pmem_name, (O_RDONLY | O_DSYNC));
    if (thisPMEM->ion_device_fd < 0) {
        
        BYD_FTPMEM_TRACE(" %s() : L( %d ) ,Can't open pmem %s !!! \n",__func__,__LINE__,thisPMEM->pmem_name);
        
        return -1;
        
    }else{
        
        BYD_FTPMEM_TRACE(" %s() : L( %d ) ,opened PMEM successfully!",__func__,__LINE__,thisPMEM->pmem_name);
        
    }
    
#else

    thisPMEM->pmem_fd = open(thisPMEM->pmem_name, O_RDWR);
    if (thisPMEM->pmem_fd < 0) {
        
        BYD_FTPMEM_TRACE(" %s() : L( %d ) ,Can't open pmem %s  !!! \n",__func__,__LINE__,thisPMEM->pmem_name);
        return -1;
    }else{
        
        BYD_FTPMEM_TRACE(" %s() : L( %d ) ,opened PMEM successfully!",__func__,__LINE__,thisPMEM->pmem_name);
        
    }
    
#endif

    return 0;
}

static int allocPMEM(struct pmemDev *thisPMEM, unsigned int size)
{

#ifdef    USE_ION

       int ret = 0;

    BYD_FTPMEM_TRACE("\n allocPMEM() \n");
    
    thisPMEM->ion_alloc_display_data.len = (size + 4095) & (~4095);
//yhx    thisPMEM->ion_alloc_display_data.flags = ION_HEAP(ION_CAMERA_HEAP_ID);
    thisPMEM->ion_alloc_display_data.align = 4096;
    
    ret = ioctl(thisPMEM->ion_device_fd,ION_IOC_ALLOCATE,&thisPMEM->ion_alloc_display_data);
    if(ret || !thisPMEM->ion_alloc_display_data.handle) {

        BYD_FTPMEM_TRACE(" %s() : L( %d ) , ION ALLOC memory failed !",__func__,__LINE__);
              close(thisPMEM->ion_device_fd);            
              thisPMEM->ion_alloc_display_data.handle=NULL;
              return -1;
        }

    thisPMEM->ion_alloc_fd.handle = thisPMEM->ion_alloc_display_data.handle;
    ret = ioctl(thisPMEM->ion_device_fd,ION_IOC_MAP,&thisPMEM->ion_alloc_fd);
    if(ret) {
        
        BYD_FTPMEM_TRACE(" %s() : L( %d ) , ION MAP failed  !",__func__,__LINE__);        
        if(ioctl(thisPMEM->ion_device_fd,ION_IOC_FREE,&thisPMEM->ion_alloc_display_data.handle)){
            BYD_FTPMEM_TRACE(" %s() : L( %d ) , ION: free failed  !",__func__,__LINE__);
        }
        close(thisPMEM->ion_device_fd);
              thisPMEM->ion_device_fd = -1;
              thisPMEM->ion_alloc_display_data.handle=NULL;
              thisPMEM->ion_alloc_fd.fd =-1;
              return -1;
    }

    thisPMEM->pmem_fd = thisPMEM->ion_alloc_fd.fd;
    thisPMEM->pmem_size = thisPMEM->ion_alloc_display_data.len ;
    
    BYD_FTPMEM_TRACE(" %s() : L( %d ) , opened /dev/ion  devfd : %d , mapfd : %d , size : %d",__func__,__LINE__,
                       thisPMEM->ion_device_fd, thisPMEM->ion_alloc_fd.fd, size);

    thisPMEM->pmem_buf = mmap(NULL,thisPMEM->pmem_size, PROT_READ | PROT_WRITE,MAP_SHARED, thisPMEM->pmem_fd, 0);
    if (thisPMEM->pmem_buf == MAP_FAILED) 
    {
        BYD_FTPMEM_TRACE(" %s() : L( %d ) , error mmap failed : %d(  %s )",__func__,__LINE__,errno,strerror(errno));
        close(thisPMEM->pmem_fd);
        thisPMEM->pmem_fd = -1;

            if(ioctl(thisPMEM->ion_device_fd,ION_IOC_FREE, &thisPMEM->ion_alloc_display_data.handle)) {
                  BYD_FTPMEM_TRACE(" %s() : L( %d ) , ion recon buffer free failed",__func__,__LINE__);
            }
            thisPMEM->ion_alloc_display_data.handle = NULL;
            thisPMEM->ion_alloc_fd.fd =-1;
            close(thisPMEM->ion_device_fd);
            thisPMEM->ion_device_fd =-1;
        return -1;
    }

        BYD_FTPMEM_TRACE(" %s() : L( %d ) ,phys lookup success virt=0x%x -- FD=%d -- size - %d",__func__,__LINE__,
        thisPMEM->pmem_buf,thisPMEM->pmem_fd, thisPMEM->pmem_size);  
    
#else

    if ((unsigned int)thisPMEM->pmem_size >= size && thisPMEM->pmem_buf) {
        memset(thisPMEM->pmem_buf, 0x00, thisPMEM->pmem_size);
        BYD_FTPMEM_TRACE(" %s() : L( %d ) ,PMEM reuse successful (%d bytes at %p)\n",__func__,__LINE__, 
                        thisPMEM->pmem_size, thisPMEM->pmem_buf);
        return 0;
    }

    // First Allocate PMEM
    thisPMEM->pmem_page_size = sysconf(_SC_PAGESIZE);
    thisPMEM->pmem_size = size;
    thisPMEM->pmem_size = (thisPMEM->pmem_size + thisPMEM->pmem_page_size - 1) & (~(thisPMEM->pmem_page_size - 1));
    if (ioctl(thisPMEM->pmem_fd, PMEM_ALLOCATE, thisPMEM->pmem_size) < 0) {
        BYD_FTPMEM_TRACE(" %s() : L( %d ) , ERROR! PMEM_ALLOCATE failed.\n",__func__,__LINE__);
        // Close PMEM Dev <----------------------------------------------
        return -1;
    } else {
        thisPMEM->pmem_buf = mmap(NULL, thisPMEM->pmem_size, PROT_READ | PROT_WRITE, MAP_SHARED, thisPMEM->pmem_fd, 0);
        if (thisPMEM->pmem_buf == MAP_FAILED) {
            BYD_FTPMEM_TRACE(" %s() : L( %d ) , ERROR: PMEM MMAP failed!\n",__func__,__LINE__);
            // Deallocate <------------------------------------------
            return -1;
        }
        memset(thisPMEM->pmem_buf, 0x00, thisPMEM->pmem_size);
        BYD_FTPMEM_TRACE(" %s() : L( %d ) , PMEM Allocation successful (%d bytes at %p)\n",__func__,__LINE__,
                            thisPMEM->pmem_size, thisPMEM->pmem_buf);
    }
    
#endif

    return 0;

}

int initPmem()
{
    
    BYD_FTPMEM_TRACE("\n initPmem() \n");
    if(PMEMinit){
        return 0;
    }
    PMEM = (struct pmemDev *)malloc(sizeof(struct pmemDev));
    if(NULL == PMEM){
        BYD_FTPMEM_TRACE("\n %s() : L( %d ) ,Pmem alloc failed !!!!! \n",__func__,__LINE__);
        return -1;
    }
    memset(PMEM, 0, sizeof(struct pmemDev));
    strcpy(PMEM->pmem_name, PMEM_NAME_STR);
    
    if (openPMEMDev(PMEM)){

        BYD_FTPMEM_TRACE("\n %s() : L( %d ) , openPMEMDev failed !!!!! \n",__func__,__LINE__);
        return -1;
        
    }
    if (allocPMEM(PMEM, 480 * 800 * 4)){

        BYD_FTPMEM_TRACE("\n %s() : L( %d ) ,open allocPMEM failed !!!!! \n",__func__,__LINE__);
        return -1;
        
    }
    
    PMEMinit = 1;
    return 0;
    
}

