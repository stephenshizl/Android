#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#define CALIREAD2CM   "/userstore/plsensor_calibration_2cm"
#define CALIREAD4CM   "/userstore/plsensor_calibration_4cm"
#define CALINEARFLAG   "/data/calinearflag.txt"
#define CALIFARFLAG   "/data/califarflag.txt"
int base2CM = -1;
int base4CM = -1;
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
int isSecondVersion(){
    FILE *fp;
    FT_LOG("[%s:%d]isSecondVersion enter...\n", __func__, __LINE__);
    if ((fp = fopen(P_SENSOR_VALUE, "r")) == NULL) {
      FT_LOG("[%s:%d]isSecondVersion return 1...\n", __func__, __LINE__);
      return 1;
   }
    FT_LOG("[%s:%d]isSecondVersion return 0...\n", __func__, __LINE__);
   fclose(fp);
   return 0;
}
int ft_psensor_open()
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
        FT_LOG("[%s,%d] Sensor input path is:%s\n", __func__, __LINE__, sensorPath);
        fd = open(sensorPath, O_RDWR);
    #endif
    if(isSecondVersion() == 1) {
      fd = open(P_SENSOR_ENABLE1, O_RDWR);
    } else {
      fd = open(P_SENSOR_ENABLE, O_RDWR);
    }
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
 * Proximity Sensor test
 *
 * Onput parameters:
 * out param:
 *      value:pointer to return sensor ADC value and threshold value
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_psensor_get_value(int *value)
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
    int isVersion = isSecondVersion();
    if( isVersion == 1){
      fd = open(P_SENSOR_VALUE1, O_RDONLY);
    } else {
      fd = open(P_SENSOR_VALUE, O_RDONLY);
    }
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
    FT_LOG("[%s:%d]The psensors value info is %s: \n", __func__, __LINE__, info);
    
    int i =0;
    int j = 0;
    int len = strlen(info);
    char adc[8] = {0};
    int flag = 0;
    if(isVersion == 0) {
      for(i = 0; i < len; i++) {
         if(info[i] == '[') {flag = 1;}
         else if(info[i] == ']') {flag = 0;break;}
         else if(flag == 1) {adc[j++] = info[i];}
      }
      ret = atoi(adc);
    } else {
      ret = atoi(info);
    }
    FT_LOG("[%s:%d]The psensors value adc is %s: \n", __func__, __LINE__, adc);
    //ret = atoi(adc);
    FT_LOG("[%s:%d]The psensors value is %d: \n", __func__, __LINE__, ret);
    *value = ret;
    FT_LOG("[%s:%d]The psensors return value is %s: \n", __func__, __LINE__, value);
    close(fd);
    FT_LOG("[%s:%d]ret=%d\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d]end...\n", __func__, __LINE__);
    return ret;
}
#if 0
    /********************************************
     * Description:
     * Proximity Sensor test
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_psensor_test_cal(char *sensor, int type)
    {
        int ret =  - 1;
        return ret;
    }
#endif
char compsensor[2048];
void *thread_psensor(void *c)
{
    FT_LOG("[%s: %d] *thread_audio start %s...\n", __func__, __LINE__, c);
    //char com[80];
    //sprintf(com, "echo %s > /sys/class/i2c-adapter/i2c-0/0-001e/phthres", c);
    FT_LOG("[%s: %d] *thread_audio start com %s...\n", __func__, __LINE__, compsensor);
    int i = system(compsensor);
    return (void *)0;
}

int ft_plsensor_calibration()
{
   int fd = -1;
   char com[80] = {0};
   char thd2cm[10] = {0};
   char thd4cm[10] ={0};
   fd = open(CALIREAD2CM, O_RDONLY);
   if ( fd >= 0) {
      FT_LOG("+++++++++read CALIREAD2CM+++++++++\n");
      read(fd, thd2cm, sizeof(thd2cm));
      close(fd);
   } else if ( fd < 0){
      FT_LOG("read CALIREAD2CM fail");
      close(fd);
      return -1;
   }
   FT_LOG("[ft_plsensor_calibration CALIREAD2CM:%s...\n", thd2cm);
   fd = open(CALIREAD4CM, O_RDONLY);
   if ( fd >= 0) {
      FT_LOG("+++++++++read CALIREAD4CM+++++++++\n");
      read(fd, thd4cm, sizeof(thd4cm));
      close(fd);
   } else if ( fd < 0){
      FT_LOG("read CALIREAD4CM fail");
      close(fd);
      return -1;
   }
   FT_LOG("[ft_plsensor_calibration CALIREAD4CM:%s...\n", thd4cm);
   int i = 0;
   for(i = 0; i < strlen(thd2cm); i++) {
      if(thd2cm[i] == '\n') thd2cm[i] = ' ';
      FT_LOG("for thd2cm %d:%c....\n", i, thd2cm[i]);
   }
   for(i = 0; i < strlen(thd4cm); i++) {
      if(thd4cm[i] == '\n') thd4cm[i] = ' ';
      FT_LOG("for thd4cm %d:%c....\n", i, thd4cm[i]);
   }
   int ret = -1;
   if(isSecondVersion() == 1) {
      sprintf(com, "echo %s> /sys/class/i2c-adapter/i2c-0/0-001e/plthres", thd2cm);
      sprintf(compsensor, "echo %s> /sys/class/i2c-adapter/i2c-0/0-001e/phthres", thd4cm);
      ret = system(com);
      pthread_t ntid;
      pthread_create(&ntid, NULL, thread_psensor, (void *)thd4cm);
   } else {
      sprintf(com, "echo %s%s> /sys/class/capella_sensors/proximity/ps_thd", thd2cm, thd4cm);
      ret = system(com);
   }   
   FT_LOG("[ft_plsensor_calibration command:%s...thd2cm1:%s....thd4cm1:%s...\n", com, thd2cm, thd4cm);   
   if(ret>=0){
       ret=1;
   }
   else {
       ret=0;
   }

   return ret;
    /*int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    system("input tap 337 590");
    ret = system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;*/
}

//#define READ_PLSENSOR_DATA "sensors_test 5 l"
//#define READ_GSENSOR_DATA "sensors_test 5 a"
#define READ_EFUSE_CHECK "cd proc;cat cmdline"

int ft_get_lsensor_data(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_PLSENSOR_DATA, result, sizeof(result));
    FT_LOG("%s result: %s\n", READ_PLSENSOR_DATA, result);
    char *str1 = strstr(result, "Light Data, x:");
    FT_LOG("%s yhxde result: %s\n", READ_PLSENSOR_DATA, str1);
    strlcpy(addr, str1, 306);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;
}

int ft_get_psensor_data(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_PLSENSOR_DATA, result, sizeof(result));
    FT_LOG("%s result p: %s\n", READ_PLSENSOR_DATA, result);
    char *str1 = strstr(result, "Prox data:");
    FT_LOG("%s yhxde Prox data: result: %s\n", READ_PLSENSOR_DATA, str1);
    strlcpy(addr, str1, 245);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;
}
int ft_get_gsesnor_data(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_GSENSOR_DATA, result, sizeof(result));
    FT_LOG("%s result: %s\n", READ_GSENSOR_DATA, result);
 //   char *str1 = strstr(result, "Accel X:");
 //   FT_LOG("%s yhxde Accel X: result: %s\n", READ_GSENSOR_DATA, str1);
  //  strlcpy(addr, str1, 296);
    strlcpy(addr, result, 296);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;
}
int ft_efuse_check(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_EFUSE_CHECK, result, sizeof(result));
    FT_LOG("%s result: %s\n", READ_EFUSE_CHECK, result);
    char *str1 = strstr(result, "androidboot.efuse=0x0");
    FT_LOG("%s yhxde androidboot.efuse=0x0 result: %s\n", READ_EFUSE_CHECK,str1);
    strlcpy(addr, str1, 296);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;
}
int ft_plsensor_calibration_read2cm(char *addr)
{
    int result = 0;
    int value = 0;
    char data[50] = {0};
    result = ft_psensor_get_value(&value);
    FT_LOG("ft_plsensor_calibration_2cm base4CM:%d...value:%d...", base4CM, value);
    if(base4CM != -1) {
      int disData = value - base4CM;
      if(disData <= 50) {
         FT_LOG("ft_plsensor_calibration_4cm value - base4CM:%d...", disData);
         return -1;
      }
    }
    base2CM = value;
    if(isSecondVersion() == 0) {
      sprintf(data, "echo 0x%x > /userstore/plsensor_calibration_2cm", value);
    } else {
      sprintf(data, "echo %d > /userstore/plsensor_calibration_2cm", value);
    }
    FT_LOG("ft_plsensor_calibration_2cm data:%s...", data);
    system(data);
    system("chmod 777 /userstore/plsensor_calibration_2cm");
    addr = result;
    if (result >= 0){
      result = 1;
    } else {
      result = -1;
    }
  
    return result;
    /*static char result[10240];
    char *p = result;
    int i = 0;
    FILE *fp;
    FT_LOG("--->>> %s()\n", __func__);
    if ((fp = fopen(CALIREAD2CM, "rb")) == NULL)
    //"/data/caliread2cm.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return  - 1;
    }
    //system("chmod 777 /data/caliread2cm.txt");
    while (!feof(fp))
    {
        result[i++] = getc(fp);
    }
    fclose(fp);
    strlcpy(addr, p, 100);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;*/
}

int ft_plsensor_calibration_read4cm(char *addr)
{
    int result = 0;
    int value = 0;
    char data[50] = {0};
    result = ft_psensor_get_value(&value);  
    FT_LOG("ft_plsensor_calibration_4cm base2CM:%d...value:%d..." , base2CM, value);
    if(base2CM != -1) {
      int disData = base2CM - value;
      if(base2CM - value <= 50) {
         FT_LOG("ft_plsensor_calibration_4cm base2CM - value:%d...", disData);
         return -1;
      }
    }
    base4CM = value;
    if(isSecondVersion() == 0) {
      sprintf(data, "echo 0x%x > /userstore/plsensor_calibration_4cm", value);
    } else {
      sprintf(data, "echo %d > /userstore/plsensor_calibration_4cm", value);
    }
    FT_LOG("ft_plsensor_calibration_4cm data:%s...", data);
    system(data);
    system("chmod 777 /userstore/plsensor_calibration_4cm");
    addr = result;
    if (result >= 0){
      result = 1;
    } else {
      result = -1;
    }
  
    return result;
    /*static char result[10240];
    char *p = result;
    int i = 0;
    int j = 0;
    FILE *fp;
    FT_LOG("--->>> %s()\n", __func__);
    if ((fp = fopen(CALIREAD4CM, "r")) == NULL)
    // "/data/caliread4cm.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return  - 1;
    }
    //system("chmod 777 /data/caliread4cm.txt");
    while (!feof(fp))
    {
        result[i++] = getc(fp);
    }
    fclose(fp);
    strlcpy(addr, p, 100);
    FT_LOG("<<<---yhxde1 %s(): %s\n", __func__, addr);
    return 1;*/
}

int ft_plsensor_calibration_auto(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FILE *fp;
    int c;
    static char result1[100];
    static char resulttemp[5];
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_PLSENSOR_DATA, result, sizeof(result));
    FT_LOG("%s result y: %s\n", READ_PLSENSOR_DATA, result);
    char *str1 = strstr(result, "y:");
    FT_LOG("%s yhxde str1 y: result: %s\n", READ_PLSENSOR_DATA, str1);
    strlcpy(result1, str1, 40); //
    strlcpy(addr, str1 + 3, 4);
    strlcpy(resulttemp, str1 + 3, 4);
    FT_LOG("%s yhxde addr: %s\n", READ_PLSENSOR_DATA, addr);
    FT_LOG("%s yhxde result1: %s\n", READ_PLSENSOR_DATA, result1); //
    if ((fp = fopen("/data/caliresult.txt", "w+")) == NULL)
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return  - 1;
    }
    else
    {
        if (strlen(resulttemp) == 2)
        {
            fprintf(fp, "00%s", addr);
        }
        else if (strlen(resulttemp) == 3)
        {
            fprintf(fp, "0%s", addr);
        }
        else if (strlen(resulttemp) == 1)
        {
            fprintf(fp, "000%s", addr);
        }
        else
        {
            fprintf(fp, "%s", addr);
        }
    }
    //ft_result_creat(result);
    //fwrite(addr,strlen(addr),1,fp);
    //fprintf(fp,"%s",addr+3);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    FT_LOG("<<<---yhxde result1 %s(): %s\n", __func__, result1);
    system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");
    fclose(fp);
    return 1;
}
//int ft_plsensor_calibration_2cm(char *first,char *second,char *third,char *forth)
int ft_plsensor_calibration_2cm(int first, int second, int third, int forth)
{
    int ret = 1;
    char chfirst, chsecond, chthird, chforth;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%d,%d,%d,%d] ft_plsensor_calibration_2cm...\n", first, second,third, forth);
    if (first == 0)
    {
        chfirst = '0';
    }
    if (first == 1)
    {
        chfirst = '1';
    }
    if (first == 2)
    {
        chfirst = '2';
    }
    if (first == 3)
    {
        chfirst = '3';
    }
    if (first == 4)
    {
        chfirst = '4';
    }
    if (first == 5)
    {
        chfirst = '5';
    }
    if (first == 6)
    {
        chfirst = '6';
    }
    if (first == 7)
    {
        chfirst = '7';
    }
    if (first == 8)
    {
        chfirst = '8';
    }
    if (first == 9)
    {
        chfirst = '9';
    }
    if (first == 10)
    {
        chfirst = 'a';
    }
    if (first == 11)
    {
        chfirst = 'b';
    }
    if (first == 12)
    {
        chfirst = 'c';
    }
    if (first == 13)
    {
        chfirst = 'd';
    }
    if (first == 14)
    {
        chfirst = 'e';
    }
    if (first == 15)
    {
        chfirst = 'f';
    }
    FT_LOG("[%c]chsecond ft_plsensor_calibration_2cm...\n", chsecond);
    if (second == 0)
    {
        chsecond = '0';
    }
    if (second == 1)
    {
        chsecond = '1';
    }
    if (second == 2)
    {
        chsecond = '2';
    }
    if (second == 3)
    {
        chsecond = '3';
    }
    if (second == 4)
    {
        chsecond = '4';
    }
    if (second == 5)
    {
        chsecond = '5';
    }
    if (second == 6)
    {
        chsecond = '6';
    }
    if (second == 7)
    {
        chsecond = '7';
    }
    if (second == 8)
    {
        chsecond = '8';
    }
    if (second == 9)
    {
        chsecond = '9';
    }
    if (second == 10)
    {
        chsecond = 'a';
    }
    if (second == 11)
    {
        chsecond = 'b';
    }
    if (second == 12)
    {
        chsecond = 'c';
    }
    if (second == 13)
    {
        chsecond = 'd';
    }
    if (second == 14)
    {
        chsecond = 'e';
    }
    if (second == 15)
    {
        chsecond = 'f';
    }
    FT_LOG("[%c]chsecond ft_plsensor_calibration_2cm...\n", chsecond);
    if (third == 0)
    {
        chthird = '0';
    }
    if (third == 1)
    {
        chthird = '1';
    }
    if (third == 2)
    {
        chthird = '2';
    }
    if (third == 3)
    {
        chthird = '3';
    }
    if (third == 4)
    {
        chthird = '4';
    }
    if (third == 5)
    {
        chthird = '5';
    }
    if (third == 6)
    {
        chthird = '6';
    }
    if (third == 7)
    {
        chthird = '7';
    }
    if (third == 8)
    {
        chthird = '8';
    }
    if (third == 9)
    {
        chthird = '9';
    }
    if (third == 10)
    {
        chthird = 'a';
    }
    if (third == 11)
    {
        chthird = 'b';
    }
    if (third == 12)
    {
        chthird = 'c';
    }
    if (third == 13)
    {
        chthird = 'd';
    }
    if (third == 14)
    {
        chthird = 'e';
    }
    if (third == 15)
    {
        chthird = 'f';
    }
    FT_LOG("[%c] chthird ft_plsensor_calibration_2cm...\n", chthird);
    if (forth == 0)
    {
        chforth = '0';
    }
    if (forth == 1)
    {
        chforth = '1';
    }
    if (forth == 2)
    {
        chforth = '2';
    }
    if (forth == 3)
    {
        chforth = '3';
    }
    if (forth == 4)
    {
        chforth = '4';
    }
    if (forth == 5)
    {
        chforth = '5';
    }
    if (forth == 6)
    {
        chforth = '6';
    }
    if (forth == 7)
    {
        chforth = '7';
    }
    if (forth == 8)
    {
        chforth = '8';
    }
    if (forth == 9)
    {
        chforth = '9';
    }
    if (forth == 10)
    {
        chforth = 'a';
    }
    if (forth == 11)
    {
        chforth = 'b';
    }
    if (forth == 12)
    {
        chforth = 'c';
    }
    if (forth == 13)
    {
        chforth = 'd';
    }
    if (forth == 14)
    {
        chforth = 'e';
    }
    if (forth == 15)
    {
        chforth = 'f';
    }
    FT_LOG("[%c] chforth ft_plsensor_calibration_2cm...\n", chforth);
    FILE *fp;
    int c;
    int fd =  - 1;
    FT_LOG("--->>> %s()\n", __func__);
    if ((fp = fopen(CALINEARFLAG, "w+")) == NULL)
    //"/data/calinearflag.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return  - 1;
    }
    else
    {
        fprintf(fp, "%c%c%c%c", chfirst, chsecond, chthird, chforth);
    }
    fclose(fp);
    system("chmod 777 /data/calinearflag.txt");
    system("input keyevent 4");
    system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");
    FT_LOG("[%d] ret...\n", ret);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
int ft_plsensor_calibration_4cm(int first, int second, int third, int forth)
{
    int ret = 1;
    char chfirst, chsecond, chthird, chforth;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%d,%d,%d,%d] ft_plsensor_calibration_4cm...\n", first, second,third, forth);
    if (first == 0)
    {
        chfirst = '0';
    }
    if (first == 1)
    {
        chfirst = '1';
    }
    if (first == 2)
    {
        chfirst = '2';
    }
    if (first == 3)
    {
        chfirst = '3';
    }
    if (first == 4)
    {
        chfirst = '4';
    }
    if (first == 5)
    {
        chfirst = '5';
    }
    if (first == 6)
    {
        chfirst = '6';
    }
    if (first == 7)
    {
        chfirst = '7';
    }
    if (first == 8)
    {
        chfirst = '8';
    }
    if (first == 9)
    {
        chfirst = '9';
    }
    if (first == 10)
    {
        chfirst = 'a';
    }
    if (first == 11)
    {
        chfirst = 'b';
    }
    if (first == 12)
    {
        chfirst = 'c';
    }
    if (first == 13)
    {
        chfirst = 'd';
    }
    if (first == 14)
    {
        chfirst = 'e';
    }
    if (first == 15)
    {
        chfirst = 'f';
    }
    FT_LOG("[%c]chsecond ft_plsensor_calibration_4cm...\n", chsecond);
    if (second == 0)
    {
        chsecond = '0';
    }
    if (second == 1)
    {
        chsecond = '1';
    }
    if (second == 2)
    {
        chsecond = '2';
    }
    if (second == 3)
    {
        chsecond = '3';
    }
    if (second == 4)
    {
        chsecond = '4';
    }
    if (second == 5)
    {
        chsecond = '5';
    }
    if (second == 6)
    {
        chsecond = '6';
    }
    if (second == 7)
    {
        chsecond = '7';
    }
    if (second == 8)
    {
        chsecond = '8';
    }
    if (second == 9)
    {
        chsecond = '9';
    }
    if (second == 10)
    {
        chsecond = 'a';
    }
    if (second == 11)
    {
        chsecond = 'b';
    }
    if (second == 12)
    {
        chsecond = 'c';
    }
    if (second == 13)
    {
        chsecond = 'd';
    }
    if (second == 14)
    {
        chsecond = 'e';
    }
    if (second == 15)
    {
        chsecond = 'f';
    }
    FT_LOG("[%c]chsecond ft_plsensor_calibration_4cm...\n", chsecond);
    if (third == 0)
    {
        chthird = '0';
    }
    if (third == 1)
    {
        chthird = '1';
    }
    if (third == 2)
    {
        chthird = '2';
    }
    if (third == 3)
    {
        chthird = '3';
    }
    if (third == 4)
    {
        chthird = '4';
    }
    if (third == 5)
    {
        chthird = '5';
    }
    if (third == 6)
    {
        chthird = '6';
    }
    if (third == 7)
    {
        chthird = '7';
    }
    if (third == 8)
    {
        chthird = '8';
    }
    if (third == 9)
    {
        chthird = '9';
    }
    if (third == 10)
    {
        chthird = 'a';
    }
    if (third == 11)
    {
        chthird = 'b';
    }
    if (third == 12)
    {
        chthird = 'c';
    }
    if (third == 13)
    {
        chthird = 'd';
    }
    if (third == 14)
    {
        chthird = 'e';
    }
    if (third == 15)
    {
        chthird = 'f';
    }
    FT_LOG("[%c] chthird ft_plsensor_calibration_4cm...\n", chthird);
    if (forth == 0)
    {
        chforth = '0';
    }
    if (forth == 1)
    {
        chforth = '1';
    }
    if (forth == 2)
    {
        chforth = '2';
    }
    if (forth == 3)
    {
        chforth = '3';
    }
    if (forth == 4)
    {
        chforth = '4';
    }
    if (forth == 5)
    {
        chforth = '5';
    }
    if (forth == 6)
    {
        chforth = '6';
    }
    if (forth == 7)
    {
        chforth = '7';
    }
    if (forth == 8)
    {
        chforth = '8';
    }
    if (forth == 9)
    {
        chforth = '9';
    }
    if (forth == 10)
    {
        chforth = 'a';
    }
    if (forth == 11)
    {
        chforth = 'b';
    }
    if (forth == 12)
    {
        chforth = 'c';
    }
    if (forth == 13)
    {
        chforth = 'd';
    }
    if (forth == 14)
    {
        chforth = 'e';
    }
    if (forth == 15)
    {
        chforth = 'f';
    }
    FT_LOG("[%c] chforth ft_plsensor_calibration_4cm...\n", chforth);
    FILE *fp;
    int c;
    int fd =  - 1;
    FT_LOG("--->>> %s()\n", __func__);
    if ((fp = fopen(CALIFARFLAG, "w+")) == NULL)
    //"/data/califarflag.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return  - 1;
    }
    else
    {
        fprintf(fp, "%c%c%c%c", chfirst, chsecond, chthird, chforth);
    }
    fclose(fp);
    system("chmod 777 /data/califarflag.txt");
    system("input keyevent 4");
    system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");
    FT_LOG("[%d] ret...\n", ret);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
int ft_plsensor_calibration_delete()
{
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    system("rm -rf /data/califarflag.txt");
    system("rm -rf  /data/calinearflag.txt");
    system("rm -rf  /data/caliread2cm.txt");
    system("rm -rf  /data/caliread4cm.txt");
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 1;
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
int ft_psensor_close()
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
    if(isSecondVersion() == 1) {
      fd = open(P_SENSOR_ENABLE1, O_RDWR);
    } else {
      fd = open(P_SENSOR_ENABLE, O_RDWR);
    }
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
