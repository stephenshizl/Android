#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <errno.h>
/********************************************
 * Description:
 * power off test
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
pthread_t ntid;
int power_off()
{
    return system("reboot -p");
}
int ft_power_off_test()
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = pthread_create(&ntid, NULL, power_off, NULL);
    if (ret != 0)
    {
        FT_LOG("[%s,%d] Error, power off failed, ret: %d, errno: %d\n",__func__, __LINE__, ret, errno);
        return  - 1;
    }
    FT_LOG("[%s,%d] End...%d\n", __func__, __LINE__, ret);
    return ret;
}
