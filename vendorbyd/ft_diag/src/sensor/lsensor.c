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
 * Enable light sensor
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_lsensor_open()
{
    int ret =  - 1;
    int fd, n;
    char enable[10] = {0};
    char enable_cmd[128];
    int num = 0;
    char sensorPath[100] = {0};
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    #if 0
        num = find_inputNum(P_SENSOR_NAME);
        FT_LOG("[%s,%d] the input number is:%d\n", __func__, __LINE__, num);
        if (num >= 16)
        {
            FT_LOG("[%s,%d],Can not find input event\n", __func__, __LINE__);
            return  - 1;
        }
        sprintf(sensorPath, "%s%d/%s", SENSOR_PATH, num, SENSOR_ENABLE);
        FT_LOG("[%s,%d] Sensor input path is:%s\n", __func__, __LINE__,sensorPath);
        fd = open(sensorPath, O_RDWR);
    #endif
    fd = open(L_SENSOR_ENABLE, O_RDWR);
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
    sprintf(enable_cmd, "echo 1 > %s", sensorPath);
    ret = system(enable_cmd);
     */
    ret = write(fd, SENSOR_ON, strlen(SENSOR_ON));
    if (ret ==  - 1)
    {
        FT_LOG("[%s:%d]Error set l-sensor enable\n", __func__, __LINE__);
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
 * Get light value that sensor detected
 *
 * Return value:
 * >= 0: the light value
 * -1: failed
 ********************************************/
 
int translat(char c){
   if(c<='9'&&c>='0') return c-'0';
   if(c>='a' && c<='f') return c-87;
   if(c>='A' && c<='F') return c-55;
   return -1;
}

int Htoi(char *str){
   int length = strlen(str);
   if(length==0) return 0;
   int i,n=0,stat;
   for(i=0;i<length;i++) {
      stat=translat(str[i]);
      if(stat>=0) n=n*16+stat;
   }
   return n;
}
int ft_lsensor_get_value(int *value)
{
    int ret =  - 1;
    int fd, n;
    char file[64] = {0};
    char info[256] = {0};
    int num = 0;
    char sensorPath[100] = {0};
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    #if 0
        num = find_inputNum(P_SENSOR_NAME);
        FT_LOG("[%s,%d] the input number is:%d\n", __func__, __LINE__, num);
        if (!num)
        {
            FT_LOG("[%s,%d],Can not find input event\n", __func__, __LINE__);
            return  - 1;
        }
        sprintf(sensorPath, "%s%d/%s", SENSOR_PATH, num, L_SENSOR_DATA);
        FT_LOG("[%s,%d] Sensor input path is:%s\n", __func__, __LINE__,sensorPath);
        fd = open(sensorPath, O_RDONLY);
    #endif
    fd = open(L_SENSOR_VALUE, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n", __func__, __LINE__);
        return  - 1;
    }
    memset(info, 0, 256);
    n = read(fd, info, 256);
    if (n < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s:%d]The lsensors info value is %s: \n", __func__, __LINE__, info);
    int i =0;
    int j = 0;
    int len = strlen(info);
    char adc[8] = {0};
    int flag = 0;
    for(i = 0; i < len; i++) {
      if(info[i] == '[') {flag = 1; i = i + 2;}
      else if(info[i] == ']') {flag = 0;break;}
      else if(flag == 1) {adc[j++] = info[i];}
    }
    FT_LOG("[%s:%d]The lsensors adc value is %s: \n", __func__, __LINE__, adc);
    ret = Htoi(adc);
    FT_LOG("[%s:%d]The lsensors adc is %d: \n", __func__, __LINE__, ret);
    *value = ret;
    FT_LOG("[%s:%d]The lsensors return value is %d: \n", __func__, __LINE__, *value);
    close(fd);
    FT_LOG("[%s:%d]ret=%d\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d]end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Disable light sensor
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_lsensor_close()
{
    int ret =  - 1;
    int fd, n;
    char enable[] = "";
    char enable_cmd[128];
    int num = 0;
    char sensorPath[100] = {0};
    FT_LOG("[%s:%d]start...\n", __func__, __LINE__);
    #if 0
        num = find_inputNum(P_SENSOR_NAME);
        FT_LOG("[%s,%d] the input number is:%d\n", __func__, __LINE__, num);
        if (num >= 16)
        {
            FT_LOG("[%s,%d],Can not find input event\n", __func__, __LINE__);
            return  - 1;
        }
        sprintf(sensorPath, "%s%d/%s", SENSOR_PATH, num, SENSOR_ENABLE);
        FT_LOG("[%s,%d] Sensor input path is:%s\n", __func__, __LINE__,sensorPath);
        fd = open(sensorPath, O_RDWR);
    #endif
    fd = open(L_SENSOR_ENABLE, O_RDWR);
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
        FT_LOG("[%s:%d]Error close l-sensor \n", __func__, __LINE__);
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
