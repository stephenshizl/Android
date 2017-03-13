#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/********************************************
* Description: 
* Always set the device screen and not lock screen
*
* Input parameters:
* out param: 

* 
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_awake_test_enable()
{
    int ret = -1;
    ret = system("input keyevent 225");
    if(ret == -1)
    {
        FT_LOG("[%s,%d] Error set awake enable failed\n",__func__,__LINE__);
        return -1;
    }
    return 0;
}

/********************************************
* Description: 
* Set the device normal
*
* Input parameters:
* out param: 

* 
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_awake_test_disable()
{
    int ret = -1;
    ret = system("input keyevent 226");
    if(ret == -1)
    {
        FT_LOG("[%s,%d] Error set awake disable failed\n",__func__,__LINE__);
        return -1;
    }
    return 0;
}

/********************************************
* Description: 
* Set the system to sleep or awake
*
* Input parameters:
* out param: 

* 
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_awake_test_sleep(int ctrl)
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    if(ctrl == 0)
    {
      //  ret = system("echo 'mem' > /sys/power/state");
        ret = system("input keyevent 26");
        if(ret != 0)
        {
            FT_LOG("[%s,%d] Set 'mem' failed\n", __func__, __LINE__);
            return -1;
        }
    }
    else if(ctrl == 1)
    {
        ret = ft_backlight_setLevel(255);
        if(ret != 0)
        {
            FT_LOG("[%s,%d] Set Backlight failed\n", __func__, __LINE__);
            return -1;
        }
       // ret = system("echo 'on' > /sys/power/state");
        ret = system("input keyevent 26");
        if(ret !=0)
        {
            FT_LOG("[%s,%d] Set 'on' failed\n", __func__, __LINE__);
            return -1;
        }
    }
    else
    {
        FT_LOG("[%s,%d] Invaild params!\n", __func__, __LINE__);
        return -1;
    }
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
