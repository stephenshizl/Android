#include "include/ft.h"
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dirent.h>
#include <cutils/android_reboot.h>
#include <unistd.h>
static int ft_file_detect(char *fileName)
{
    int ret =  - 1;
    ret = access(fileName, F_OK);
    if (ret < 0)
    {
        FT_LOG("[%s,%d] Error, %s file is not exist", __func__, __LINE__, fileName);
    }
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
void *masterclear()
{
    int ret =  - 1;
    int fd =  - 1;
    int len = 0;
    char *cmdpath = "/cache/recovery/command";
    char *wipecmd = "--wipe_data";
    char readData[50] = {0};
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    system("mkdir /cache/recovery");
    fd = open(cmdpath, O_RDWR | O_CREAT, 0777);
    if (fd < 0)
    {
        FT_LOG("[%s,%d] Error,open %s failed\n", __func__, __LINE__, cmdpath);
        return (void*) - 1;
    }
    len = strlen(wipecmd);
    ret = write(fd, wipecmd, len);
    FT_LOG("[%s,%d]Write ret:%d,len:%d\n", __func__, __LINE__, ret, len);
    if ((ret ==  - 1) || (ret != len))
    {
        FT_LOG("[%s,%d] Error,write data to %s failed\n", __func__, __LINE__, cmdpath);
        return (void*) - 1;
    }
    lseek(fd, 0, SEEK_SET);
    ret = read(fd, readData, len);
    FT_LOG("[%s,%d]Read ret:%d,len:%d\n", __func__, __LINE__, ret, len);
    if ((ret ==  - 1) || (ret != len))
    {
        FT_LOG("[%s,%d] Error,read data from %s failed\n", __func__, __LINE__, cmdpath);
        return (void*) - 1;
    }
    close(fd);
    if (strcmp(wipecmd, readData) != 0)
    {
        FT_LOG("[%s,%d] Error,readData and wipecmd are different\n", __func__,__LINE__);
        return (void*) - 1;
    }
    //system("sleep 5;reboot recovery");
    FT_LOG("[%s,%d] Change system func to android_reboot", __func__, __LINE__);
    sleep(5);
    android_reboot(ANDROID_RB_RESTART2, 0, "recovery");
    return (void*)0;
}
/*-----------------------------------------------------------------
 * Clear the test data
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Clear flash, but can't delete the preload data in flash,
 * such as the preload PC tools
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_clear_flash(char *dir)
{
    /*
    int ret = -1;
    ret = system("./system/bin/vdc volume unmount /storage/sdcard0 force; ./system/bin/vdc volume format /storage/sdcard0");
    return ret;
     */
    DIR *pd;
    struct dirent *pDirent;
    char path[PATH_LEN] = {0};
    unsigned char dirLen;
    unsigned char nameMaxLen;
    FT_LOG("[%s,%d] Start... \n", __func__, __LINE__);
    // check input path
    if (dir == NULL ||  *dir == '\0')
    {
        FT_LOG("[%s,%d] CleanFlash:-----empty path\n", __func__, __LINE__);
        //yhx return -1;
        return 0;
    }
    // open dir
    if ((pd = opendir(dir)) == NULL)
    {
        FT_LOG("[%s,%d] CleanFlash:-----can't open %s\n", __func__, __LINE__, dir);
        //yhx return -1;
        return 0;
    }
    dirLen = strlen(dir);
    nameMaxLen = PATH_LEN - dirLen;
    strcpy(path, dir);
    // delete files in folder
    while ((pDirent = readdir(pd)) != NULL)
    {
        //        FT_LOG("[%s,%d] d_ino : %lu,d_off : %lu,d_reclen : %u,d_type : %u,d_name : %s\n", __func__, __LINE__,
        //                        pDirent->d_ino, pDirent->d_off , pDirent->d_reclen, pDirent->d_type, pDirent->d_name);
        if (pDirent->d_type == DT_REG)
        {
            // delete if it is a file
            memset(path + dirLen, 0, nameMaxLen);
            strcat(path, "/");
            strcat(path, pDirent->d_name);
            FT_LOG("[%s,%d] Delete DTREG file : %s", __func__, __LINE__, path);
            remove(path);
        }
        else if ((pDirent->d_type == DT_DIR) && ((strcmp(pDirent->d_name, ".") != 0)
                        &&(strcmp(pDirent->d_name, "..") != 0)) 
                        && (strcmp(pDirent->d_name, FLASH_SAVE_DIR_MOVIES) != 0) 
                        && (strcmp(pDirent->d_name, FLASH_SAVE_DIR_MUSIC) != 0) 
                        && (strcmp(pDirent->d_name, FLASH_SAVE_DIR_PICTURES) != 0))
        {
            //recursive if it is not a file
            memset(path + dirLen, 0, nameMaxLen);
            strcat(path, "/");
            strcat(path, pDirent->d_name);
            ft_clear_flash(path);
        }
    }
    //remove empty folder
    FT_LOG("[%s,%d] DT_DIR file:%s\n", __func__, __LINE__, dir);
    if (strcmp(dir, "/storage/sdcard0") != 0)
    {
        FT_LOG("[%s,%d] Delete DT_DIR file:%s\n", __func__, __LINE__, dir);
        rmdir(dir);
    }
    fsync(pd);
    // close dir
    closedir(pd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 1;
}
/********************************************
 * Description:
 * Restore the settings, could directly format /data and /cache
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
pthread_t mstclr_thread;
int ft_clear_facRestore()
{
    pthread_create(&mstclr_thread, NULL, masterclear, NULL);
    return 1;
}
/********************************************
 * Description:
 * Delete /cache/recovery/last_log
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_lastlog_delete()
{
    int ret =  - 1;
    DIR *pd;
    struct dirent *pDirent;
    char filePath[50] = {0};
    char fileName[50] = {0};
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    sprintf(fileName, "%s/%s", LAST_LOG_PATH, LAST_LOG_NAME);
    ret = ft_file_detect(fileName);
    if (ret < 0)
    {
        FT_LOG("[%s,%d] There is no last_log files, no need to delete %s \n",__func__, __LINE__, fileName);
        return 0;
    }
    if ((pd = opendir(LAST_LOG_PATH)) == NULL)
    {
        FT_LOG("[%s,%d] Can not open %s\n", __func__, __LINE__, LAST_LOG_PATH);
        //yhx return -1;
        return 0;
    }
    while ((pDirent = readdir(pd)) != NULL)
    {
        if (pDirent->d_type == DT_REG && (strcmp(pDirent->d_name, LAST_LOG_NAME) == 0))
        {
            // delete if it is a file
            sprintf(filePath, "%s/%s", LAST_LOG_PATH, LAST_LOG_NAME);
            FT_LOG("[%s,%d] Find %s files, Delete it\n", __func__, __LINE__,filePath);
            ret = remove(filePath);
            break;
        }
    }
    if (ret < 0)
    {
        FT_LOG("[%s,%d] The last_log file not exist or remove last_log failed\n", __func__, __LINE__);
    }
    fsync(pd);
    closedir(pd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Check if there is last_log files after factory reset
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_lastlog_detect()
{
    //yhx  int ret = -1;
    int ret = 0;
    char fileName[50] = {0};
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    sprintf(fileName, "%s/%s", LAST_LOG_PATH, LAST_LOG_NAME);
    ret = ft_file_detect(fileName);
    if (ret < 0)
    {
        FT_LOG("[%s,%d] Can not find %s \n", __func__, __LINE__, fileName);
    }
    else
    {
        FT_LOG("[%s,%d] Find the %s \n", __func__, __LINE__, fileName);
    }
    FT_LOG("[%s, %d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Create a file in data partition for data flag
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_dataflag_creat()
{
    //yhx  int ret = -1;
    int ret = 0;
    int fd =  - 1;
    char fileName[50] = {0};
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    sprintf(fileName, "%s", DATA_FLAG_PATH);
    ret = ft_file_detect(fileName);
    if (ret >= 0)
    {
        FT_LOG("[%s,%d] data partitin flag exist,exit\n", __func__, __LINE__);
        return 0;
    }
    FT_LOG("[%s,%d] flag files not exist,creat it\n", __func__, __LINE__);
    fd = open(DATA_FLAG_PATH, O_RDWR | O_CREAT, 0777);
    if (fd < 0)
    {
        FT_LOG("[%s,%d] Creat data partition failed", __func__, __LINE__);
        //    return -1;
        return 0;
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
/********************************************
 * Description:
 * Check if the data partition flag exist
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_dataflag_detect()
{
    //yhx  int ret = -1;
    int ret = 0;
    char fileName[50] = {0};
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    sprintf(fileName, "%s", DATA_FLAG_PATH);
    ret = ft_file_detect(fileName);
    if (ret < 0)
    {
        FT_LOG("[%s,%d] Can not find %s \n", __func__, __LINE__, fileName);
    }
    else
    {
        FT_LOG("[%s,%d] Find the %s \n", __func__, __LINE__, fileName);
    }
    FT_LOG("[%s, %d] End...\n", __func__, __LINE__);
    return ret;
}
