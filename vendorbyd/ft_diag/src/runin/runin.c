/*
 * Copyright (c) 2012 Intel Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include "include/ft.h"
#include "include/ft_sys.h"

int ft_runin_get_result(char *value)
{
    FILE *fp;
    char str[5];
    if ((fp = fopen("/data/runinflag.txt", "r")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    fgets(str,5,fp);
    FT_LOG("liutao [%s]:runin result\n",str);
    printf("-------------------%s\n",str);
    strcpy(value, str);
    return 1;
}

int ft_runin_get_data(int item, char *dret)
{
    int ret =  - 1;
    FILE *fp = NULL;
    char get_data[150] = {'0'};
    char data[30] = {'0'};
    FT_LOG("[%s:%d] get_data by type start...\n", __func__, __LINE__);
    if ( - 1 == access(RUNIN_LOG_PATH, F_OK))
    {
        FT_LOG("[%s:%d] There's no runin log file\n", __func__, __LINE__);
        return ret;
    }
    switch (item)
    {
        case 0:
            // get total time
            sprintf(get_data, "grep %s %s | busybox sed 's/%s//g'", "TotalTime", RUNIN_LOG_PATH, "TotalTime(H): ");
            break;
        case 1:
            // get test items
            sprintf(get_data, "grep %s %s | busybox sed 's/%s//g'",  \
                "TestItems", RUNIN_LOG_PATH, "TestItems: ");
            break;
        case 2:
            // get cycle times
            sprintf(get_data, "grep %s %s | busybox sed 's/%s//g'",  \
                "CycleTimes", RUNIN_LOG_PATH, "CycleTimes: ");
            break;
        case 3:
            // get battery target
            sprintf(get_data, "grep %s %s | busybox sed 's/%s//g'",  \
                "BatteryTarget", RUNIN_LOG_PATH, "BatteryTarget(%): ");
            break;
        case 4:
            // get video time
            sprintf(get_data, "grep %s %s | busybox sed 's/%s//g'",  \
                "VideoTime", RUNIN_LOG_PATH, "VideoTime(min): ");
            break;
        case 5:
            // get summary of results
            sprintf(get_data, "grep %s %s", "PASSED", RUNIN_LOG_PATH);
            break;
        default:
            return ret;
            break;
    }
    FT_LOG("getdata: %s", get_data);
    fp = popen(get_data, "r");
    if (NULL == fp)
    {
        FT_LOG("[%s:%d] Error to open log file\n", __func__, __LINE__);
        return ret;
    }
    else
    {
        fgets(data, sizeof(data) - 1, fp);
        FT_LOG("[%s:%d] data = %s\n", __func__, __LINE__, data);
        //strtrim(data, '\n');
        memset(dret, 0, sizeof(dret));
        strcpy(dret, data);
        FT_LOG("[%s:%d] dret = %s\n", __func__, __LINE__, dret);
        ret = 0;
    }
    return ret;
}
int ft_runin_get_failed_item(int item, char *finfo)
{
    int ret =  - 1;
    FILE *fp = NULL;
    char get_failed_info[150] = {'0'};
    char failed_info[30] = {'0'};
    FT_LOG("[%s:%d] get_failed_info by type start...\n", __func__, __LINE__);
    if ( - 1 == access(RUNIN_LOG_PATH, F_OK))
    {
        FT_LOG("[%s:%d] There's no runin log file\n", __func__, __LINE__);
        return ret;
    }
    switch (item)
    {
        case 0:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "INFO: ", RUNIN_LOG_PATH, "INFO: ");
            break;
        case 1:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "WIFI: ", RUNIN_LOG_PATH, "WIFI: ");
            break;
        case 2:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "GPS: ", RUNIN_LOG_PATH, "GPS: ");
            break;
        case 3:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "BT: ", RUNIN_LOG_PATH, "BT: ");
            break;
        case 4:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "CAMERA: ", RUNIN_LOG_PATH, "CAMERA: ");
            break;
        case 5:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "SLEEP: ", RUNIN_LOG_PATH, "SLEEP: ");
            break;
        case 6:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "VIDEO: ", RUNIN_LOG_PATH, "VIDEO: ");
            break;
        case 7:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "REBOOT: ", RUNIN_LOG_PATH, "REBOOT: ");
            break;
        case 8:
            sprintf(get_failed_info, "grep %s %s | busybox sed 's/%s//g'",  \
                "BATTERY: ", RUNIN_LOG_PATH, "BATTERY: ");
            break;
        default:
            return ret;
            break;
    }
    fp = popen(get_failed_info, "r");
    if (NULL == fp)
    {
        FT_LOG("[%s:%d] Error to open log file\n", __func__, __LINE__);
        return ret;
    }
    else
    {
        fgets(failed_info, sizeof(failed_info) - 1, fp);
        //strtrim(failed_info, '\n');
        strcpy(finfo, failed_info);
        FT_LOG("[%s:%d] failed times = %s\n", __func__, __LINE__, finfo);
        ret = 0;
    }
    return ret;
}
