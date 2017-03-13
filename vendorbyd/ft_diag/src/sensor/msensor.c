#include "include/ft.h"
#include "include/ft_sys.h"
#include "include/public.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
/********************************************
 * Description:
 * Enable light sensor
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_get_msensor_data(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_MSENSOR_DATA, result, sizeof(result));
    FT_LOG("%s result: %s\n", READ_MSENSOR_DATA, result);
    strlcpy(addr, result, 296);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;
}


/********************************************
 * Description:
 * Enable m sensor
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_msensor_open()
{
    int ret =  - 1;
    int fd, n;
    char enable[10] = {0};
    char enable_cmd[128];
    int num = 0;
    char sensorPath[100] = {0};
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    fd = open(M_SENSOR_ENABLE, O_RDWR);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n", __func__, __LINE__);
        return  - 1;
    }
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s:%d]the initial enable value is %c\n", __func__, __LINE__, enable[0]);
    ret = write(fd, SENSOR_ON, strlen(SENSOR_ON));
    if (ret ==  - 1)
    {
        FT_LOG("[%s:%d]Error set m-sensor enable\n", __func__, __LINE__);
        return ret;
    }
    lseek(fd, 0, SEEK_SET);
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s:%d]the modified enable values is %c\n", __func__, __LINE__, enable[0]);
    close(fd);
    ret = 0;
    FT_LOG("[%s:%d]ret=%d...\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d]end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Get msensor value that sensor detected
 *
 * Return value:
 * >= 0: the m value
 * -1: failed
 ********************************************/
int ft_msensor_get_value(int *x, int *y, int *z)
{
    int ret = -1;
    int fd, n,value_len;
    char value[128] = {' '};
    char num[128];
    int i, j, k;
    int evnum = 0;
    char sensorPath[100] = {0};
    int tmpx , tmpy , tmpz ;

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);

    fd = open(M_SENSOR_VALUE, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n",__func__,__LINE__);
        return -1;
    }
    n = read(fd, value, 16);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    FT_LOG("[%s:%d]the  value is %s\n",__func__,__LINE__, value);

    j = k = i = 0;
    memset(num, 0, sizeof(num));
    while (value[i] != '\0')
    {
        if(value[i] == ' ')
        {
            j++;
            if(j == 1)
            {
                tmpx = atoi(num);
                memset(num, 0, sizeof(num));
            }
            else if(j == 2)
            {
                tmpy = atoi(num);
                memset(num, 0, sizeof(num));
            }
            k = 0;
        }
        else
        {
            num[k] = value[i];
            k++;
        }
        if(value[i + 1] == '\0')
        {
            tmpz = atoi(num);
            memset(num, 0, sizeof(num));
        }
        i++;
    }
    *x = tmpx;
    *y = tmpy;
    *z = tmpz;
    FT_LOG("[%s:%d]num is %d,%d,%d\n",__func__,__LINE__,*x, *y, *z);
    close(fd);
    ret = 0;
    FT_LOG("[%s:%d]ret=%d...\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d]end...\n",__func__,__LINE__);
    return ret;
}
/********************************************
 * Description:
 * Disable m sensor
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_msensor_close()
{
    int ret =  - 1;
    int fd, n;
    char enable[] = "";
    char enable_cmd[128];
    int num = 0;
    char sensorPath[100] = {0};
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    fd = open(M_SENSOR_ENABLE, O_RDWR);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n", __func__, __LINE__);
        return  - 1;
    }
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s:%d]the initial enable value is %c\n", __func__, __LINE__, enable[0]);
    /*
    memset(enable_cmd, 0, 128);
    sprintf(enable_cmd, "echo 0 > %s", sensorPath);
    ret = system(enable_cmd);
     */
    ret = write(fd, SENSOR_OFF, strlen(SENSOR_OFF));
    if (ret ==  - 1)
    {
        FT_LOG("[%s:%d]Error close M-sensor \n", __func__, __LINE__);
        return ret;
    }
    lseek(fd, 0, SEEK_SET);
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s:%d]the modified enable values is %c\n", __func__, __LINE__, enable[0]);
    close(fd);
    ret = 0;
    FT_LOG("[%s:%d]ret=%d...\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d]end...\n", __func__, __LINE__);
    return ret;
}

