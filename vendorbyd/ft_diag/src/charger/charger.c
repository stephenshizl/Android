#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
/********************************************
 * Description:
 * Get the battery's capacity
 *
 * Input parameters:
 * out param:
 *     cap: return the battery's capacity
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_cap(int *cap)
{
    int ret =  - 1;
    int cur_val = 0, vol_val = 0, len = 0;
    int fd = 0;
    char tmp[10] = {0};
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    fd = open(BATTERY_CAP_PATH, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening  the current_now file\n", __func__,__LINE__);
        return  - 1;
    }
    else
    {
        memset(tmp, 0, 10);
        len = read(fd, tmp, 10);
        tmp[len] = '\0';
        *cap = atoi(tmp);
        FT_LOG("[%s:%d]The battery's capacity: %d \n", __func__, __LINE__, *cap);
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
/********************************************
 * Description:
 * Get the battery's voltage
 *
 * Input parameters:
 * out param:
 *     vol: return the battery's voltage
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_voltage(float *vol)
{
    int ret =  - 1;
    int len = 0;
    int fd = 0;
    char tmp[10] = {0};
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    fd = open(BATTERY_VOL_PATH, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening  the voltage file\n", __func__, __LINE__);
        return  - 1;
    }
    else
    {
        memset(tmp, 0, 10);
        len = read(fd, tmp, 10);
        tmp[len] = '\0';
        *vol = atoi(tmp) *1.0 / 1000000;
        FT_LOG("[%s:%d]The battery's voltage: %f \n", __func__, __LINE__,  *vol);
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
/*add by wu.xiaobo@byd.com for get the battery current and temperature at 20141125*/
/********************************************
 * Description:
 * Get the battery's current
 *
 * Input parameters:
 * out param:
 *     vol: return the battery's current
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_current(long *current)
{
    int ret =  - 1;
    int len = 0;
    int fd = 0;
    char tmp[11] = {0};
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    fd = open(BATTERY_CURRENT_PATH, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening  the voltage file\n", __func__, __LINE__);
        return ret;
    }
    else
    {
        memset(tmp, 0, 10);
        len = read(fd, tmp, 10);
        tmp[len] = '\0';
        *current = atoi(tmp) / 1000;
        FT_LOG("[%s:%d]The battery's voltage: %d \n", __func__, __LINE__, *current);
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
/********************************************
 * Description:
 * control the charger function
 *
 * Input parameters:
 *     val: 0: discharging, 1: charging
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_charger_control(int val)
{
    int fd =  - 1;
    int ret =  - 1;
    char mod[10] = {0};
    FT_LOG("[%s,%d] Start...val = %d\n", __func__, __LINE__, val);
    fd = open(BATTERY_CHARGING_ENABLE_PATH, O_WRONLY);
    if (fd < 0)
    {
        FT_LOG("[%s,%d] Error,open %s failed\n", __func__, __LINE__,BATTERY_CHARGING_ENABLE_PATH);
        return  - 1;
    }
    sprintf(mod, "%d", val);
    ret = write(fd, mod, 1);
    close(fd);
    if (ret != 1)
    {
        FT_LOG("[%s,%d] Write val failed\n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
/********************************************
 * Description:
 * Get the battery's temperature.
 *
 * Input parameters:
 * out param:
 *     temp: return the battery's temperature
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_temp(float *temp)
{
    int ret =  - 1;
    int len = 0;
    int fd = 0;
    char tmp[11] = {0};
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    fd = open(BATTERY_TEMP_PATH, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening  the temp file\n", __func__, __LINE__);
        return  - 1;
    }
    else
    {
        memset(tmp, 0, 10);
        len = read(fd, tmp, 10);
        tmp[len] = '\0';
        *temp = atoi(tmp) *1.0 / 10;
        FT_LOG("[%s:%d]The battery's temp: %f \n", __func__, __LINE__,  *temp);
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}

 /*end by wuxiaobo*/
