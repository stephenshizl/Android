#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/input.h>
#include <errno.h>
#include <ctype.h>
#include <sys/vfs.h>
#include <sys/mount.h>
#include "include/ft.h"
#include "include/ft_sys.h"
#define SD_IO_MAX_W 2048
/*-----------------------------------------------------------------
 * SD card test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get SD card information
 *
 * Input parameters:
 * out param:
 *      isInsert: if the SD card is insert, 1: insert, 0: not insert
 *      totalSpace: total space(MB) of SD card, 0 if no SD card
 *      freeSpace: free space(MB) of SD card, 0 if no SD card
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_sdcard_get_info(int *isInsert, int *totalSpace, int *freeSpace)
{
    int ret =  - 1;
    struct statfs st;
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    if (!isInsert || !totalSpace || !freeSpace)
    {
        FT_LOG("[%s:%d]NULL pointer input\n", __func__, __LINE__);
    }
    /*no blk1*/
    if (access(SDCARD_PATH, F_OK) ==  - 1)
    {
        perror("[SDCARD]: No external sdcard insert");
        FT_LOG("[%s:%d][SDCARD]: No external sdcard insert\n", __func__,__LINE__);
        *isInsert = 0;
        *totalSpace = 0;
        *freeSpace = 0;
        return 0;
    }
    *isInsert = 1;
    if (statfs(SDCARD_EXT, &st) ==  - 1)
    {
        perror("[SDCARD]: Failed to get sdcard information");
        FT_LOG("[%s:%d][SDCARD]: Failed to get sdcard information\n", __func__,__LINE__);
        goto get_info_err;
    }
    else
    {
        *totalSpace = (st.f_blocks *st.f_bsize) >> 20;
        *freeSpace = (st.f_bfree *st.f_bsize) >> 20;
        ret = 0;
    }
    FT_LOG("[%s:%d]st.f_blocks = %lld, st.f_bfree = %lld, st.f_bsize = %d\n",
           __func__, __LINE__, st.f_blocks, st.f_bfree, st.f_bsize);
    get_info_err: FT_LOG("[%s,%d] Failed to read sdcard information\n",__func__, __LINE__);
    FT_LOG("[%s:%d]end... ret=%d\n", __func__, __LINE__, ret);
    return ret;
}
/********************************************
 * Description:
 * Test SD Card IO function
 *
 * Input parameters:
 * out param:
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_sdcard_IO_test()
{
    int ret =  - 1;
    int fd =  - 1;
    char testFile[100] =
    {
        0
    };
    int i;
    char *ptestData = "7";
    char recvData[2048] =
    {
        0
    };
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    if (access(SDCARD_PATH, F_OK) ==  - 1)
    {
        perror("[SDCARD]: No external sdcard insert");
        FT_LOG("[%s:%d][SDCARD]: No external sdcard insert\n", __func__,__LINE__);
        return  - 1;
    }
    sprintf(testFile, "%s/sd_IO_test.txt", SDCARD_EXT);
    FT_LOG("[%s,%d] ------%s\n", __func__, __LINE__, testFile);
    fd = open(testFile, O_RDWR | O_CREAT, 0777);
    for (i = 0; i < SD_IO_MAX_W; i++)
    {
        if ((write(fd, ptestData, 1)) ==  - 1)
        {
            FT_LOG("[%s,%d] Error,write data to %s failed,errno:%d", __func__,__LINE__, testFile, errno);
            return  - 1;
        }
    }
    lseek(fd, 0, SEEK_SET);
    ret = read(fd, recvData, SD_IO_MAX_W);
    if (ret ==  - 1)
    {
        FT_LOG("[%s,%d] Error,read data failed\n", __func__, __LINE__);
        return ret;
    }
    close(fd);
    //FT_LOG("[%s,%d] After read,recvData:%s\n",__func__,__LINE__,recvData);
    for (i = 0; i < SD_IO_MAX_W; i++)
    {
        if ((strncmp(&recvData[i], ptestData, 1)) != 0)
        {
            FT_LOG("[%s,%d] Error,read data is not same  with write\n",__func__, __LINE__);
            return  - 1;
        }
        //FT_LOG("=================&recvData[%d]:%s\n",i,&recvData[i]);
    }
    FT_LOG("[%s,%d] Read data is same with write\n", __func__, __LINE__);
    if ( - 1 == remove(testFile))
    {
        perror("[SDCARD]: Failed to remove test file.");
        FT_LOG("[%s:%d][SDCARD]: Failed to remove test file.\n", __func__,__LINE__);
    }
    FT_LOG("[%s:%d]end...\n", __func__, __LINE__);
    return 0;
}

/*-----------------------------------------------------------------
 *Internal SD card test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get internal SD card information
 *
 * Input parameters:
 * out param:
 *      totalSpace: total space(GB) of Internal SD card
 *
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_internal_sdcard_get_info(double *totalSpace)
{
    int ret =  - 1;
    struct statfs st;
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    if (!totalSpace)
    {
        FT_LOG("[%s:%d]NULL pointer input\n", __func__, __LINE__);
    }
    if (statfs(INTERNAL_SDCARD_PATH, &st) ==  - 1)
    {
        perror("[EMMC]: Failed to get EMMC information");
        FT_LOG("[%s:%d][EMMC]: Failed to get EMMC information\n", __func__,__LINE__);
        goto get_info_err;
    }
    else
    {
        *totalSpace = (st.f_blocks *st.f_bsize * 1.0) / (1024 *1024 * 1024);
        ret = 0;
    }
    FT_LOG("[%s:%d]st.f_blocks = %lld, st.f_bsize = %d, totalSpace = %.2f\n",
           __func__, __LINE__, st.f_blocks, st.f_bsize,  *totalSpace);
    goto end;
get_info_err: 
    FT_LOG("[%s,%d] Failed to read EMMC information\n", __func__,__LINE__);
end:
    FT_LOG("[%s:%d]end... ret=%d\n", __func__, __LINE__, ret);
    return ret;
}
