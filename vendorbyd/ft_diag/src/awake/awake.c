#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
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

pthread_t mtid;

void* check_backlight_status()
{
    int fd = -1;
    int ret = -1;
    char backlightValue[10] = {0};

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);

    fd = open(SYS_BACKLIGHT_PATH, O_RDONLY);
    if(fd < 0)
    {
        FT_LOG("[%s,%d] Error, open %s failed\n", __func__, __LINE__, SYS_BACKLIGHT_PATH);
        return (void*)0;
    }

    ret = read(fd, backlightValue, sizeof(backlightValue));
    close(fd);
    if( ret <= 0)
    {
        FT_LOG("[%s,%d] Error, Read backlight value failed\n", __func__, __LINE__);
        return (void*)0;
    }

    if((atoi(backlightValue)) ==0)
    {
        FT_LOG("[%s,%d] Device sleep, need press power key\n", __func__, __LINE__);
        system("input keyevent 26");
    }
    else
    {
        FT_LOG("[%s,%d] Device not sleep, not need to press power key\n", __func__, __LINE__);
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return (void*)0;
}

int ft_awake_test_enable()
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = system("input keyevent 260");
    if (ret ==  - 1)
    {
        FT_LOG("[%s,%d] Error set awake enable failed\n", __func__, __LINE__);
        return  - 1;
    }
    ret = pthread_create(&mtid, NULL, check_backlight_status, NULL);
    if(ret < 0)
    {
        FT_LOG("[%s,%d]pthread_create error, ret: %d, errno: %d\n", __func__, __LINE__, ret, errno);
        return -1;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
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
pthread_t ntid1;
void* awake_test()
{
    FT_LOG("[%s,%d] awake_test start...\n", __func__, __LINE__);
    system("am broadcast -a COM.FTDIAG_TOOLS.AWAKE");
    FT_LOG("[%s,%d] awake_test end...\n", __func__, __LINE__);
    return (void*)0;
}

int ft_awake_test_disable()
{
    int ret =  - 1;
    ret = system("input keyevent 82");
    if (ret ==  - 1)
    {
        FT_LOG("[%s,%d] Error set awake disable failed\n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s,%d] am broadcast -a COM.FTDIAG_TOOLS.AWAKE BEFORE\n", __func__, __LINE__);
    ret = pthread_create(&ntid1, NULL, awake_test, NULL);
    //ret = system("am broadcast -a COM.FTDIAG_TOOLS.AWAKE");
    FT_LOG("[%s,%d] am broadcast -a COM.FTDIAG_TOOLS.AWAKE AFTER\n", __func__, __LINE__);
    if (ret ==  - 1)
    {
        FT_LOG("[%s,%d] Error set awake disable failed\n", __func__, __LINE__);
        return  - 1;
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
/*int ft_awake_test_sleep(int ctrl)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    if (ctrl == 0)
    {
        //  ret = system("echo 'mem' > /sys/power/state");
        ret = system("input keyevent 26");
        if (ret != 0)
        {
            FT_LOG("[%s,%d] Set 'mem' failed\n", __func__, __LINE__);
            return  - 1;
        }
    }
    else if (ctrl == 1)
    {
        ret = ft_backlight_setLevel(255);
        if (ret != 0)
        {
            FT_LOG("[%s,%d] Set Backlight failed\n", __func__, __LINE__);
            return  - 1;
        }
        // ret = system("echo 'on' > /sys/power/state");
        ret = system("input keyevent 26");
        if (ret != 0)
        {
            FT_LOG("[%s,%d] Set 'on' failed\n", __func__, __LINE__);
            return  - 1;
        }
    }
    else
    {
        FT_LOG("[%s,%d] Invaild params!\n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}*/
pthread_t ntid;
void* sleep_test()
{
    FT_LOG("[%s,%d] sleep_test start...\n", __func__, __LINE__);
    system("am broadcast -a COM.FTDIAG_TOOLS.SLEEP");
    FT_LOG("[%s,%d] sleep_test end...\n", __func__, __LINE__);
    return (void*)0;
}

int ft_awake_test_sleep()
{
    int ret = -1;
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    ret = system("input keyevent 26");
    if(ret < 0 ) {
        return -1;
    }
    ret = pthread_create(&ntid, NULL, sleep_test, NULL);
    if(ret < 0)
    {
        FT_LOG("[%s,%d]pthread_create error, ret: %d, errno: %d\n", __func__, __LINE__, ret, errno);
        return -1;
    }

    FT_LOG("[%s,%d] End...%d\n",__func__,__LINE__, ret);
    return ret;
}
