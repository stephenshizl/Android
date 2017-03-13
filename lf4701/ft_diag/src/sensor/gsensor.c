#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

/********************************************
* Description: 
* Open Gsensor 
*
* Onput parameters:
*
*
* Return value:
* 1: success
* 0: failed
********************************************/
int ft_gsensor_open()
{
    int ret = -1;
    int fd, n;
    char enable[10]={0};
    char enable_cmd[128] = {0};
    int num = 0;
    char sensorPath[100] = {0};

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);

    num = find_inputNum(G_SENSOR_NAME);
    FT_LOG("[%s,%d] the input number is:%d\n",__func__,__LINE__,num);

    if(!num)
    {
        FT_LOG("[%s,%d],Can not find input event\n",__func__,__LINE__);
        return -1;
    }

    sprintf(sensorPath,"%s%d/%s",SENSOR_PATH,num,SENSOR_ENABLE);
    FT_LOG("[%s,%d] Sensor input path is:%s\n",__func__,__LINE__,sensorPath);

    fd = open(sensorPath,O_RDWR);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n",__func__,__LINE__);
        return -1;
    }

    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    FT_LOG("[%s:%d]the initial enable value is %c\n",__func__,__LINE__, enable[0]);
/*
    memset(enable_cmd, 0, 128);
    sprintf(enable_cmd, "echo 1 > %s", sensorPath);
    ret = system(enable_cmd);
*/
    ret = write(fd,SENSOR_ON,strlen(SENSOR_ON));
    if (ret == -1)
    {
        FT_LOG("[%s:%d]Error set g-sensor enable\n",__func__,__LINE__);
        return ret;
    }

    lseek(fd, 0, SEEK_SET);
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    FT_LOG("[%s:%d]the modified enable values is %c\n",__func__,__LINE__, enable[0]);
    
    close(fd);
    ret = 0;
    FT_LOG("[%s:%d]ret=%d...\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d]end...\n",__func__,__LINE__);
    return ret;

}

/********************************************
* Description: 
* Proximity Sensor test
*
* Onput parameters:
* out param: 
*      x: x value
*      y: y value
*      z: z value
*
* Return value:
* 1: success
* 0: failed
********************************************/
int ft_gsensor_get_value(float *x, float *y, float *z)
{
    int ret = -1;
    int fd, n,value_len;
    char value[64] = {' '};
    char num[64];
    int i, j, k;
    int evnum = 0;
    char sensorPath[100] = {0};
    float tmpx , tmpy , tmpz ;

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);
    evnum = find_inputNum(G_SENSOR_NAME);
    FT_LOG("[%s,%d] the input number is:%d\n",__func__,__LINE__,evnum);
    if(!evnum)
    {
        FT_LOG("[%s,%d],Can not find input event\n",__func__,__LINE__);
        return -1;
    }
    sprintf(sensorPath,"%s%d/%s",SENSOR_PATH,evnum,SENSOR_VALUE);
    FT_LOG("[%s,%d] Sensor input path is:%s\n",__func__,__LINE__,sensorPath);
    fd = open(sensorPath, O_RDONLY);
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
#if 0
    *x = -(*x/255)*9.8;
    *y = -(*y/255)*9.8;
    *z = -(*z/255)*9.8;
#endif
    *x = (tmpy/256)*9.8*4;
    *y = (tmpx/256)*9.8*4;
    *z = -(tmpz/256)*9.8*4;
    FT_LOG("[%s:%d]num is %f,%f,%f\n",__func__,__LINE__,*x, *y, *z);
    close(fd);
    ret = 0;
    FT_LOG("[%s:%d]ret=%d...\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d]end...\n",__func__,__LINE__);
    return ret;
}

/********************************************
* Description: 
* Close Gsensor 
*
* Onput parameters:
*
*
* Return value:
* 1: success
* 0: failed
********************************************/
int ft_gsensor_close()
{
    int ret = -1;
    int fd, n;
    char enable[10]={0};
    char enable_cmd[128] = {0};
    int num = 0;
    char sensorPath[100] = {0};

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);
    num = find_inputNum(G_SENSOR_NAME);
    FT_LOG("[%s,%d] the input number is:%d\n",__func__,__LINE__,num);
    if(!num)
    {
        FT_LOG("[%s,%d],Can not find input event\n",__func__,__LINE__);
        return -1;
    }
    sprintf(sensorPath,"%s%d/%s",SENSOR_PATH,num,SENSOR_ENABLE);
    FT_LOG("[%s,%d] Sensor input path is:%s\n",__func__,__LINE__,sensorPath);
    fd = open(sensorPath,O_RDWR);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n",__func__,__LINE__);
        return -1;
    }
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    FT_LOG("[%s:%d]the initial enable value is %c\n",__func__,__LINE__, enable[0]);
/*
    memset(enable_cmd, 0, 128);
    sprintf(enable_cmd, "echo 1 > %s", sensorPath);
    ret = system(enable_cmd);
*/
    ret = write(fd,SENSOR_OFF,strlen(SENSOR_OFF));
    if (ret == -1)
    {
        FT_LOG("[%s:%d]Error set g-sensor enable\n",__func__,__LINE__);
        return ret;
    }
    lseek(fd, 0, SEEK_SET);
    n = read(fd, enable, 1);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    FT_LOG("[%s:%d]the modified enable values is %c\n",__func__,__LINE__, enable[0]);
    close(fd);
    ret = 0;
    FT_LOG("[%s:%d]ret=%d...\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d]end...\n",__func__,__LINE__);
    return ret;



}