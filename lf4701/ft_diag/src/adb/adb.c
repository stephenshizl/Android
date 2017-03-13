#include "include/ft.h"
#include "include/ft_sys.h"
#include <string.h>
#include <cutils/properties.h>

/********************************************
* Description: 
* Get LCD Serial Number
*
* Input parameters:
* 
* Return value:
* 1: success
* 0: failed
********************************************/
int ft_adb_set_enable()
{

    int ret = -1;
    char config[100] = {0};
    char *adb = ",adb";
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    ret = system("am broadcast -a com.auto.cit.ADB_ENABLE");
    ret = property_set("service.adb.root","1");//set adb root
    if(ret < 0)
    {
        FT_LOG("[%s,%d] Error,fail to set adb root\n",__func__,__LINE__);
        return -1;
    }
    ret = property_get(SYS_USB_CONFIG,config,"NULL");
    FT_LOG("[%s,%d] Get sys.usb.config:%s\n",__func__,__LINE__,&config);
    strcat(config,adb);
    FT_LOG("[%s,%d] Add adb to sys.usb.config:%s\n",__func__,__LINE__,&config);

    ret = property_set(SYS_USB_CONFIG,config);
    if(ret < 0)
    {
        FT_LOG("[%s,%d] Error,fail to set sys.usb.config\n",__func__,__LINE__);
        return -1;
    }
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret; // property_set success return 0 ,fail return <0

    //system("mount -o remount -rw /system");
    //return 1;
}

/********************************************
* Description: 
* Get LCD Serial Number
*
* Input parameters:
* 
* Return value:
* 1: success
* 0: failed
********************************************/
int ft_adb_set_disable()
{
    int ret = -1;
    char config[100] = {0};
    char tmp[100] = {0};
    int i = 0;
    int j = 0;
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);

    ret = property_get(SYS_USB_CONFIG,tmp,"NULL");
    FT_LOG("[%s,%d] Get sys.usb.config:%s\n",__func__,__LINE__,&tmp);
    for(i = 0 ; i < strlen(tmp) ; i++)
    {
        if((tmp[i]==',')&&(tmp[i+1]=='a')&&(tmp[i+2]=='d')&&(tmp[i+3]=='b'))
        {
             i += 4;
             continue;
        }
        config[j] =tmp[i]; 
        j++;
    }
    FT_LOG("[%s,%d] config:%s\n",__func__,__LINE__,&config);
    ret = property_set(SYS_USB_CONFIG,"none");
    if(ret < 0)
    {
        FT_LOG("[%s,%d] Error,fail to set sys.usb.config=none\n",__func__,__LINE__);
        return -1;
    }

    ret = property_set(SYS_USB_CONFIG,config);
    if(ret < 0)
    {
        FT_LOG("[%s,%d] Error,fail to set sys.usb.config=none\n",__func__,__LINE__);
        return -1;
    }

    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}

int ft_sceen_wake()
{
    int ret = -1;
    ret = system("input keyevent 26");
    return ret;
}
int ft_unlock_screen()
{
    int ret = -1;

    ret = system("input swipe 360 800 360 400");
    return ret;
}

int ft_awake_test_enable()
{
    int ret = -1;
    ret = system("am broadcast -a com.auto.cit.SCREEN_ON");
    if(ret == -1)
    {
        FT_LOG("[%s,%d] Error set awake enable failed\n",__func__,__LINE__);
        return -1;
    }
    return 0;
}