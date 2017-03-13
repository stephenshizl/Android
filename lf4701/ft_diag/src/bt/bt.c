/*
 * Copyright (c) 2012, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "include/ft.h"

#define TEST_ENABLE                "bttest enable"
#define IS_ENABLE                    "bttest is_enabled"
#define IS_ENABLE_RESULT        "is_enabled\n= 1\n"
#define HCICONFIG_UP            "hciconfig hci0 up"
#define HCITOOL_DEV                "hcitool dev"
//#define HCITOOL_DEV_RESULT    "Devices:\n"
#define HCITOOL_DEV_RESULT    "BD ADDRESS:"
//#define MAC_ADDR_RESULT        "        hci0    "
#define HCICONFIG_DOWN        "hciconfig hci0 down"
#define TEST_DISABLE                "bttest disable"

#define READ_BD_ADDR "btconfig /dev/smd3 rba"

//In sure the "result" large enough.
static void exe_cmd(const char * cmd, char* result, int size)
{
    static char pr[1024];
    char *p;
    FILE *pf;

    result[0] = 0;
    if((pf=popen(cmd, "r"))!=NULL){
        while(fgets(pr, sizeof(pr), pf)!=NULL){
            FT_LOG("exe_cmd: %s\n",pr);
            for(p=pr; size>1 && (*result = *p); size--,result++,p++)
                /**/; 
            result[0] = 0;
        }
        pclose(pf);
    }else{
        FT_LOG("popen(%s) error\n", cmd);
    }
}

/*
discription:
    get the BT MAC address from chipset.
parameter:
    addr: [OUT] return bt address format as: XX:XX:XX:XX:XX:XX
return value:
    0: success
    N: error code.
*/
int bt_get_addr_hci(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_BD_ADDR, result, sizeof(result));

    FT_LOG("%s result: %s\n", READ_BD_ADDR,result);
//yhx add for find bt mac,find third :in btconfig /dev/smd3 rba,and print the mac
    do{
         //      FT_LOG("yhx *p=%c\n",*p);
        p++;
        if(*p ==':' )
        {
            count++;
            if(count==3)
            {
                strlcpy(addr, p-2,18);
            }
        }
   }while(count<=3);
    
    FT_LOG("<<<--- %s(): %s\n", __func__, addr);
    return 1;
//yhx end
#if 0
    system(TEST_ENABLE);

    exe_cmd(IS_ENABLE, result, sizeof(result));
    if(! strcmp(result, IS_ENABLE_RESULT)){
        FT_LOG("now bluetooth is enabled\n");
    }else{
        FT_LOG("<<<--- %s() cannot enable bluetooth, fail!!\n", __func__);
        return -1;
    }

    system(HCICONFIG_UP);

    exe_cmd(HCITOOL_DEV, result, sizeof(result));

    FT_LOG("%s result: %s\n", HCITOOL_DEV,result);

    if(strncmp(p, HCITOOL_DEV_RESULT, strlen(HCITOOL_DEV_RESULT)) ){
        FT_LOG("<<<--- %s() invalid %s result !!\n", __func__, HCITOOL_DEV);
        return -2;
    }

    p += strlen(HCITOOL_DEV_RESULT);
    while(*p == ' ' | *p== '\t') p++;
    if(strncmp(p, "hci0", strlen("hci0")) != 0){
//    if(strncmp(p, MAC_ADDR_RESULT, strlen(MAC_ADDR_RESULT))){
        FT_LOG("<<<--- %s() no  mac result !!\n", __func__);
        return -2;
    }

//    p += strlen(MAC_ADDR_RESULT);
    p+= strlen("hci0");
    while(*p == ' ' | *p== '\t') p++;
    strlcpy(addr, p,18);

    system(HCICONFIG_DOWN);

    system(TEST_DISABLE);
    FT_LOG("<<<--- %s(): %s\n", __func__, addr);
    return 1;
#endif

}

int ft_bt_test_init()
{
    int ret = -1;
    static char result[1024] = {0};

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    ret = system("ftmdaemon&");
    if(ret != 0)
    {
        FT_LOG("[%s,%d] Error,start ftmdaemon server fail\n",__func__,__LINE__);
        return -1;
    }
//exe_cmd("ftmdaemon",result,sizeof(result));
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return 0;
}

