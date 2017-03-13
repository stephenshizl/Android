#include "include/ft.h"
#include "include/ft_sys.h"
#include<pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

/*-----------------------------------------------------------------
* Flash test
-----------------------------------------------------------------*/
static int isLoop = 0;

void * flash_loop()
{
    while(isLoop)
    {
        ft_flash_control(FLASH_LIGHT_FLASH);
        sleep(1);
    }
    return (void*)0;
}

/********************************************
* Description: 
* Control the flash light flash torch or off
* 
* Return value:
*  0: success
* -1: failed 
********************************************/
int ft_flash_control(char* comm)
{
    int fd = -1;
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%s,%d] Command is %s\n",__func__,__LINE__,comm);
    fd = open(FLASH_LIGHT_PATH,O_RDWR);
    if(fd < 0)
    {
        FT_LOG("[%s,%d] Open %s failed!\n",__func__,__LINE__,FLASH_LIGHT_PATH);
        return -1;
    }
    ret  = write(fd,comm,strlen(comm));
    if(ret < 0)
    {
        FT_LOG("[%s,%d] Write torch command failed!\n",__func__,__LINE__);
        ret = -1;
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}



/********************************************
* Description: 
* Check if the flash can flash normally
*
* 
* Return value:
* 0: one key is pressed
* -1: failed 
********************************************/

pthread_t flash_thread;
int ft_flash_test_flash()
{
    isLoop = 1;
    pthread_create(&flash_thread,NULL,flash_loop,NULL);
    return 1;
}

/********************************************
* Description: 
* Check if the flash can torch
*
* 
* Return value:
*   0: success 
* -1: failed 
********************************************/
int ft_flash_test_torch()
{
    int ret = -1;
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    isLoop = 0;
    ret = ft_flash_control(FLASH_LIGHT_TORCH);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}

/********************************************
* Description: 
* shut down the flash light
*
* 
* Return value:
*   0: success
* -1: failed 
********************************************/
int ft_flash_test_off()
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    isLoop = 0;
    ret = ft_flash_control(FLASH_LIGHT_OFF);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}
