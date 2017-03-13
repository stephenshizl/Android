/****************************************************************************
 * hwinfo.c
 *     get hardware info include CPU model name and memory size.
 ***************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <dlfcn.h>
#include <string.h>
#include <fcntl.h>
#include "include/ft.h"
#include "include/ft_sys.h"
/*-----------------------------------------------------------------
 * memory test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get internal SD card information
 *
 * Input parameters:
 * out param:
 *      response_data: total space(MB) of internal SD card
 *
 * Return value:
 * 1: success
 * -1: failed
 ********************************************/
int hw_get_meminfo(char *response_data)
{
    int size = 0;
    FILE *fp = NULL;
    char meminfo[256] = {0};
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    if (access(MEMORY_INFO, F_OK) == 0)
    {
        fp = fopen(MEMORY_INFO, "r");
        if (NULL == fp)
        {
            FT_LOG("[%s:%d]memory info read failed.\n", __func__, __LINE__);
            return  - 1;
        }
    }
    else
    {
        FT_LOG("[%s:%d]memory info node don't exist.\n", __func__, __LINE__);
        return  - 1;
    }
    memset(response_data, 0, sizeof(response_data));
    fseek(fp, 0, SEEK_SET);
    if (fscanf(fp, "%[^\n]", response_data) !=  - 1)
    {
        fclose(fp);
        sscanf(response_data, "%*s%s", response_data);
        size = atoi(response_data) >> 10;
        memset(response_data, 0, sizeof(response_data));
        sprintf(response_data, "%d", size);
    }
    else
    {
        FT_LOG("[%s:%d] read failed\n", __func__, __LINE__);
        fclose(fp);
        return  - 1;
    }
    FT_LOG("[%s:%d]response_data = %s, size = %d\n", __func__, __LINE__,response_data, size);
    FT_LOG("[%s:%d]end...\n", __func__, __LINE__);
    return 1;
}
/*-----------------------------------------------------------------
 * CPU info test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get CPU model name
 *
 * Input parameters:
 * out param:
 *      response_data: CPU model name
 *
 * Return value:
 * 1: success
 * -1: failed
 ********************************************/
int hw_get_cpuinfo(char *response_data)
{
    FILE *fp = NULL;
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    if (access(CUP_INFO, F_OK) == 0)
    {
        fp = fopen(CUP_INFO, "r");
        if (NULL == fp)
        {
            FT_LOG("[%s:%d]CPU info read failed.\n", __func__, __LINE__);
            return  - 1;
        }
    }
    else
    {
        FT_LOG("[%s:%d]CPU info node don't exist.\n", __func__, __LINE__);
        return  - 1;
    }
    memset(response_data, 0, sizeof(response_data));
    fseek(fp, 0, SEEK_SET);
    fscanf(fp, "%*[^\n]%*c"); //Ignore the first line
    if (fscanf(fp, "%[^\n]", response_data) !=  - 1)
    {
        FT_LOG("[%s:%d] Get CPU info success.\n", __func__, __LINE__);
        fclose(fp);
        return 1;
    }
    else
    {
        FT_LOG("[%s:%d] Get CPU info failed\n", __func__, __LINE__);
        fclose(fp);
        return  - 1;
    }
}
