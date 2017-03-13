#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

#define  CALIREAD2CM   "/data/caliread2cm.txt"
#define  CALIREAD4CM   "/data/caliread4cm.txt"
#define  CALINEARFLAG   "/data/calinearflag.txt"
#define  CALIFARFLAG   "/data/califarflag.txt"
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
int ft_psensor_open()
{
    int ret = -1;
    int fd, n;
    char enable[10]={0};
    char enable_cmd[128];

    int num = 0;
    char sensorPath[100] = {0};

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);
#if 0
    num = find_inputNum(P_SENSOR_NAME);
    FT_LOG("[%s,%d] the input number is:%d\n",__func__,__LINE__,num);
    if(num >= 16)
    {
        FT_LOG("[%s,%d],Can not find input event\n",__func__,__LINE__);
        return -1;
    }
    sprintf(sensorPath,"%s%d/%s",SENSOR_PATH,num,SENSOR_ENABLE);
    FT_LOG("[%s,%d] Sensor input path is:%s\n",__func__,__LINE__,sensorPath);
    fd = open(sensorPath, O_RDWR);
#endif
    fd = open(P_SENSOR_ENABLE, O_RDWR);
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
        FT_LOG("[%s:%d]Error set l-sensor enable\n",__func__,__LINE__);
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
*      value:pointer to return sensor ADC value and threshold value
*
* Return value:
* 1: success
* 0: failed
********************************************/
int ft_psensor_get_value(int *value)
{
   int ret = -1;
    int fd, n ;
    char file[64] = {0};
    char info[256] = {0};
    int num = 0;
    char sensorPath[100] = {0};

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);
#if 0
    num = find_inputNum(P_SENSOR_NAME);
    FT_LOG("[%s,%d] the input number is:%d\n",__func__,__LINE__,num);
    if(!num)
    {
        FT_LOG("[%s,%d],Can not find input event\n",__func__,__LINE__);
        return -1;
    }
    sprintf(sensorPath,"%s%d/%s",SENSOR_PATH,num,L_SENSOR_DATA);
    FT_LOG("[%s,%d] Sensor input path is:%s\n",__func__,__LINE__,sensorPath);
    fd = open(sensorPath, O_RDONLY);
#endif
    fd = open(P_SENSOR_VALUE, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n",__func__,__LINE__);
        return -1;
    }
    memset(info, 0, 256);
    n = read(fd, info, 256);
    if (n< 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    ret = atoi(info);
    FT_LOG("[%s:%d]The lsensors value is %d: \n",__func__,__LINE__, ret);
    *value = ret;
    close(fd);
    FT_LOG("[%s:%d]ret=%d\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d]end...\n",__func__,__LINE__);
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
int ft_psensor_test_cal(char *sensor,int type)
{
    int ret = -1;
    return ret;
}
#endif

int ft_plsensor_calibration()
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    system("input tap 337 590");
    ret = system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}
#define READ_PLSENSOR_DATA "sensors_test 5 l"
#define READ_GSENSOR_DATA "sensors_test 5 a"
#define READ_EFUSE_CHECK "cd proc;cat cmdline"
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
int ft_get_lsensor_data(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_PLSENSOR_DATA, result, sizeof(result));

    FT_LOG("%s result: %s\n", READ_PLSENSOR_DATA,result);


   char*str1=strstr(result,"Light Data, x:");
       FT_LOG("%s yhxde result: %s\n", READ_PLSENSOR_DATA,str1);

   strlcpy(addr, str1,306);
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

    FT_LOG("%s result p: %s\n", READ_PLSENSOR_DATA,result);


   char*str1=strstr(result,"Prox data:");
       FT_LOG("%s yhxde Prox data: result: %s\n", READ_PLSENSOR_DATA,str1);

   strlcpy(addr, str1,245);
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

    FT_LOG("%s result: %s\n", READ_GSENSOR_DATA,result);


   char*str1=strstr(result,"Accel X:");
   FT_LOG("%s yhxde Accel X: result: %s\n", READ_GSENSOR_DATA,str1);

   strlcpy(addr, str1,296);
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

    FT_LOG("%s result: %s\n", READ_EFUSE_CHECK,result);


   char*str1=strstr(result,"androidboot.efuse=0x0");
   FT_LOG("%s yhxde androidboot.efuse=0x0 result: %s\n", READ_EFUSE_CHECK,str1);

   strlcpy(addr, str1,296);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;


}

int ft_plsensor_calibration_read2cm(char *addr)
{
    static char result[10240];
    char *p = result;
    int i = 0;
    FILE *fp;
    FT_LOG("--->>> %s()\n", __func__);
    if ((fp = fopen(CALIREAD2CM, "rb")) == NULL) //"/data/caliread2cm.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return -1;
    }
    //system("chmod 777 /data/caliread2cm.txt");
    while(!feof(fp))
    {
     result[i++]=getc(fp);
    }
    fclose(fp);
    strlcpy(addr, p,100);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;


}
int ft_plsensor_calibration_read4cm(char *addr)
{
    static char result[10240];
    char *p = result;
    int i = 0;
    int j=0;
    FILE *fp;
    FT_LOG("--->>> %s()\n", __func__);
    if ((fp = fopen(CALIREAD4CM, "r")) == NULL) // "/data/caliread4cm.txt"
    {
    FT_LOG("%s yhxde fopen fail\n");
    printf("open file error\n");
    return -1;
    }
    //system("chmod 777 /data/caliread4cm.txt");
    while(!feof(fp))
    {
    result[i++]=getc(fp);
    }
    fclose(fp);
    strlcpy(addr, p,100);
    FT_LOG("<<<---yhxde1 %s(): %s\n", __func__, addr);
    return 1;
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
    FT_LOG("%s result y: %s\n", READ_PLSENSOR_DATA,result);

    char*str1=strstr(result,"y:");
    FT_LOG("%s yhxde str1 y: result: %s\n", READ_PLSENSOR_DATA,str1);
    strlcpy(result1, str1,40);//
    strlcpy(addr, str1+3,4);
    strlcpy(resulttemp, str1+3,4);
    FT_LOG("%s yhxde addr: %s\n", READ_PLSENSOR_DATA,addr);
    FT_LOG("%s yhxde result1: %s\n", READ_PLSENSOR_DATA,result1);//
    if ((fp = fopen("/data/caliresult.txt", "w+")) == NULL)
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return -1;
    }
    else
    {
        if(strlen(resulttemp)==2)
        {
        fprintf(fp,"00%s",addr);
        }
        else if (strlen(resulttemp)==3)
        {
        fprintf(fp,"0%s",addr);
        }
        else if (strlen(resulttemp)==1)
        {
        fprintf(fp,"000%s",addr);
        }
        else
        {
        fprintf(fp,"%s",addr);
        }
    }
    //ft_result_creat(result);
    //fwrite(addr,strlen(addr),1,fp);
    //fprintf(fp,"%s",addr+3);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    FT_LOG("<<<---yhxde result1 %s(): %s\n", __func__, result1);
   system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");
   fclose(fp);

/*    system("input tap 475 145");//press regis

system("input tap 34,234"); //item 调出数字键盘
system("input tap 34,234");
system("input tap 138,521");  // 103
system("input tap 248,758");
system("input tap 351,516");

system("input tap 351,516");//lengh
system("input tap 242,511");// 2

system("input tap 105,376");  // value
    //如果字串长度等于2，则补两个零

if (strlen(resulttemp)==2)
{
system("input tap 39,715");//123jian
system("input tap 248,758");
system("input tap 248,758");

    if ((fp = fopen("/data/calinearresult.txt", "w+")) == NULL)
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return -1;
    }
    while((c=fgetc(fp))!=EOF)
        {

switch(c){

case '0':
    system("input tap 39,715");//123jian
    system("input tap 248,758");
    system("input tap 442,754");
    break;

case '1':
    system("input tap 39,715");//123jian
    system("input tap 138,521");
    system("input tap 442,754");
    break;
case '2':
        system("input tap 39,715");//123jian
    system("input tap 249,522");
    system("input tap 442,754");
    break;
case '3':
        system("input tap 39,715");//123jian
    system("input tap 351,516");
    system("input tap 442,754");
    break;
case '4':
        system("input tap 39,715");//123jian
    system("input tap 150,590");
    system("input tap 442,754");
    break;
case '5':
        system("input tap 39,715");//123jian
    system("input tap 242,587");
    system("input tap 442,754");
    break;
case '6':
        system("input tap 39,715");//123jian
    system("input tap 338,598");
    system("input tap 442,754");
    break;
case '7':
        system("input tap 39,715");//123jian
    system("input tap 141,677");
    system("input tap 442,754");
    break;
case '8':
        system("input tap 39,715");//123jian
    system("input tap 238,673");
    system("input tap 442,754");
    break;
case '9':
        system("input tap 39,715");//123jian
    system("input tap 340,678");
    system("input tap 442,754");
    break;
case 'a':
    system("input tap 45,589");
    break;
case 'b':
        system("input tap 285,666");
    break;
case 'c':
        system("input tap 188,666");
    break;
case 'd':
        system("input tap 145,589");
    break;
case 'e':
        system("input tap 121,507");
    break;
case 'f':
        system("input tap 183,589");
    break;
case 'g':
        system("input tap 243,589");
    break;
case 'h':
        system("input tap 280,589");
    break;
case 'i':
        system("input tap 357,507");
    break;
case 'j':
        system("input tap 334,589");
    break;
case 'k':
        system("input tap 382,589");
    break;
case 'l':
        system("input tap 436,589");
    break;
case 'm':
        system("input tap 370,666");
    break;
case 'n':
        system("input tap 334,666");
    break;
case 'o':
        system("input tap 405,507");
    break;
case 'p':
        system("input tap 45,589");
    break;
case 'q':
        system("input tap 448,507");
    break;
case 'r':
        system("input tap 170,507");
    break;
case 's':
        system("input tap 102,589");
    break;
case 't':
        system("input tap 212,507");
    break;
case 'u':
        system("input tap 307,507");
    break;
case 'v':
        system("input tap 238,666");
    break;
case 'w':
        system("input tap 74,507");
    break;
case 'x':
        system("input tap 141,666");
    break;
case 'y':
        system("input tap 264,507");
    break;
case 'z':
        system("input tap 98,665");
    break;
default:
    break;

}


    }


}
else
    {
system("input tap 39,715");//123jian
system("input tap 248,758");

    if ((fp = fopen("/data/calinearresult.txt", "w+")) == NULL)
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return -1;
    }
    while((c=fgetc(fp))!=EOF)
        {

switch(c){

case '0':
    system("input tap 39,715");//123jian
    system("input tap 248,758");
    system("input tap 442,754");
    break;

case '1':
    system("input tap 39,715");//123jian
    system("input tap 138,521");
    system("input tap 442,754");
    break;
case '2':
        system("input tap 39,715");//123jian
    system("input tap 249,522");
    system("input tap 442,754");
    break;
case '3':
        system("input tap 39,715");//123jian
    system("input tap 351,516");
    system("input tap 442,754");
    break;
case '4':
        system("input tap 39,715");//123jian
    system("input tap 150,590");
    system("input tap 442,754");
    break;
case '5':
        system("input tap 39,715");//123jian
    system("input tap 242,587");
    system("input tap 442,754");
    break;
case '6':
        system("input tap 39,715");//123jian
    system("input tap 338,598");
    system("input tap 442,754");
    break;
case '7':
        system("input tap 39,715");//123jian
    system("input tap 141,677");
    system("input tap 442,754");
    break;
case '8':
        system("input tap 39,715");//123jian
    system("input tap 238,673");
    system("input tap 442,754");
    break;
case '9':
    system("input tap 39,715");//123jian
    system("input tap 340,678");
    system("input tap 442,754");
    break;
case 'a':
    system("input tap 45,589");
    break;
case 'b':
        system("input tap 285,666");
    break;
case 'c':
        system("input tap 188,666");
    break;
case 'd':
        system("input tap 145,589");
    break;
case 'e':
        system("input tap 121,507");
    break;
case 'f':
        system("input tap 183,589");
    break;
case 'g':
        system("input tap 243,589");
    break;
case 'h':
        system("input tap 280,589");
    break;
case 'i':
        system("input tap 357,507");
    break;
case 'j':
        system("input tap 334,589");
    break;
case 'k':
        system("input tap 382,589");
    break;
case 'l':
        system("input tap 436,589");
    break;
case 'm':
        system("input tap 370,666");
    break;
case 'n':
        system("input tap 334,666");
    break;
case 'o':
        system("input tap 405,507");
    break;
case 'p':
        system("input tap 45,589");
    break;
case 'q':
        system("input tap 448,507");
    break;
case 'r':
        system("input tap 170,507");
    break;
case 's':
        system("input tap 102,589");
    break;
case 't':
        system("input tap 212,507");
    break;
case 'u':
        system("input tap 307,507");
    break;
case 'v':
        system("input tap 238,666");
    break;
case 'w':
        system("input tap 74,507");
    break;
case 'x':
        system("input tap 141,666");
    break;
case 'y':
        system("input tap 264,507");
    break;
case 'z':
        system("input tap 98,665");
    break;
default:
    break;

}
    }
}
    system("input tap 209 545");//write button
    fclose(fp);*/
    return 1;


}

int ft_plsensor_calibration_2cm(char *first,char *second,char *third,char *forth)
{
    int ret = 1;
    char chfirst,chsecond,chthird,chforth;
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%d,%d,%d,%d] ft_plsensor_calibration_2cm...\n",(int)first,(int)second,(int)third,(int)forth);

        if((int)first==0) chfirst='0';
    if((int)first==1) chfirst='1';
    if((int)first==2) chfirst='2';
    if((int)first==3) chfirst='3';
    if((int)first==4) chfirst='4';
    if((int)first==5) chfirst='5';
    if((int)first==6) chfirst='6';
    if((int)first==7) chfirst='7';
    if((int)first==8) chfirst='8';
    if((int)first==9) chfirst='9';
    if((int)first==10) chfirst = 'a';
    if((int)first==11) chfirst = 'b';
    if((int)first==12) chfirst = 'c';
    if((int)first==13) chfirst = 'd';
    if((int)first==14) chfirst = 'e';
    if((int)first==15) chfirst = 'f';
        FT_LOG("[%c]chsecond ft_plsensor_calibration_2cm...\n",chsecond);
        if((int)second==0) chsecond='0';
    if((int)second==1) chsecond='1';
    if((int)second==2) chsecond='2';
    if((int)second==3) chsecond='3';
    if((int)second==4) chsecond='4';
    if((int)second==5) chsecond='5';
    if((int)second==6) chsecond='6';
    if((int)second==7) chsecond='7';
    if((int)second==8) chsecond='8';
    if((int)second==9) chsecond='9';
    if((int)second==10) chsecond = 'a';
    if((int)second==11) chsecond = 'b';
    if((int)second==12) chsecond = 'c';
    if((int)second==13) chsecond = 'd';
    if((int)second==14) chsecond = 'e';
    if((int)second==15) chsecond = 'f';
        FT_LOG("[%c]chsecond ft_plsensor_calibration_2cm...\n",chsecond);
    if((int)third==0) chthird='0';
    if((int)third==1) chthird='1';
    if((int)third==2) chthird='2';
    if((int)third==3) chthird='3';
    if((int)third==4) chthird='4';
    if((int)third==5) chthird='5';
    if((int)third==6) chthird='6';
    if((int)third==7) chthird='7';
    if((int)third==8) chthird='8';
    if((int)third==9) chthird='9';
    if((int)third==10) chthird = 'a';
    if((int)third==11) chthird = 'b';
    if((int)third==12) chthird = 'c';
    if((int)third==13) chthird = 'd';
    if((int)third==14) chthird = 'e';
    if((int)third==15) chthird = 'f';
    FT_LOG("[%c] chthird ft_plsensor_calibration_2cm...\n",chthird);
        if((int)forth==0) chforth='0';
    if((int)forth==1) chforth='1';
    if((int)forth==2) chforth='2';
    if((int)forth==3) chforth='3';
    if((int)forth==4) chforth='4';
    if((int)forth==5) chforth='5';
    if((int)forth==6) chforth='6';
    if((int)forth==7) chforth='7';
    if((int)forth==8) chforth='8';
    if((int)forth==9) chforth='9';
    if((int)forth==10) chforth = 'a';
    if((int)forth==11) chforth = 'b';
    if((int)forth==12) chforth = 'c';
    if((int)forth==13) chforth = 'd';
    if((int)forth==14) chforth = 'e';
    if((int)forth==15) chforth = 'f';
    FT_LOG("[%c] chforth ft_plsensor_calibration_2cm...\n",chforth);

    FILE *fp;
    int c;
    int fd = -1;
    FT_LOG("--->>> %s()\n", __func__);
       if ((fp = fopen(CALINEARFLAG, "w+")) == NULL) //"/data/calinearflag.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return -1;
    }
    else
    {
        fprintf(fp,"%c%c%c%c",chfirst,chsecond,chthird,chforth);
    }
        fclose(fp);
    system("chmod 777 /data/calinearflag.txt");
        system("input keyevent 4");
    system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");

    FT_LOG("[%d] ret...\n",ret);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}

int ft_plsensor_calibration_4cm(char* first,char* second,char* third,char* forth)
{
    int ret = 1;
    char chfirst,chsecond,chthird,chforth;
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%d,%d,%d,%d] ft_plsensor_calibration_4cm...\n",(int)first,(int)second,(int)third,(int)forth);

        if((int)first==0) chfirst='0';
    if((int)first==1) chfirst='1';
    if((int)first==2) chfirst='2';
    if((int)first==3) chfirst='3';
    if((int)first==4) chfirst='4';
    if((int)first==5) chfirst='5';
    if((int)first==6) chfirst='6';
    if((int)first==7) chfirst='7';
    if((int)first==8) chfirst='8';
    if((int)first==9) chfirst='9';
    if((int)first==10) chfirst = 'a';
    if((int)first==11) chfirst = 'b';
    if((int)first==12) chfirst = 'c';
    if((int)first==13) chfirst = 'd';
    if((int)first==14) chfirst = 'e';
    if((int)first==15) chfirst = 'f';
        FT_LOG("[%c]chsecond ft_plsensor_calibration_4cm...\n",chsecond);
        if((int)second==0) chsecond='0';
    if((int)second==1) chsecond='1';
    if((int)second==2) chsecond='2';
    if((int)second==3) chsecond='3';
    if((int)second==4) chsecond='4';
    if((int)second==5) chsecond='5';
    if((int)second==6) chsecond='6';
    if((int)second==7) chsecond='7';
    if((int)second==8) chsecond='8';
    if((int)second==9) chsecond='9';
    if((int)second==10) chsecond = 'a';
    if((int)second==11) chsecond = 'b';
    if((int)second==12) chsecond = 'c';
    if((int)second==13) chsecond = 'd';
    if((int)second==14) chsecond = 'e';
    if((int)second==15) chsecond = 'f';
        FT_LOG("[%c]chsecond ft_plsensor_calibration_4cm...\n",chsecond);
    if((int)third==0) chthird='0';
    if((int)third==1) chthird='1';
    if((int)third==2) chthird='2';
    if((int)third==3) chthird='3';
    if((int)third==4) chthird='4';
    if((int)third==5) chthird='5';
    if((int)third==6) chthird='6';
    if((int)third==7) chthird='7';
    if((int)third==8) chthird='8';
    if((int)third==9) chthird='9';
    if((int)third==10) chthird = 'a';
    if((int)third==11) chthird = 'b';
    if((int)third==12) chthird = 'c';
    if((int)third==13) chthird = 'd';
    if((int)third==14) chthird = 'e';
    if((int)third==15) chthird = 'f';
        FT_LOG("[%c] chthird ft_plsensor_calibration_4cm...\n",chthird);
    if((int)forth==0) chforth='0';
    if((int)forth==1) chforth='1';
    if((int)forth==2) chforth='2';
    if((int)forth==3) chforth='3';
    if((int)forth==4) chforth='4';
    if((int)forth==5) chforth='5';
    if((int)forth==6) chforth='6';
    if((int)forth==7) chforth='7';
    if((int)forth==8) chforth='8';
    if((int)forth==9) chforth='9';
    if((int)forth==10) chforth = 'a';
    if((int)forth==11) chforth = 'b';
    if((int)forth==12) chforth = 'c';
    if((int)forth==13) chforth = 'd';
    if((int)forth==14) chforth = 'e';
    if((int)forth==15) chforth = 'f';
    FT_LOG("[%c] chforth ft_plsensor_calibration_4cm...\n",chforth);

    FILE *fp;
    int c;
    int fd = -1;
    FT_LOG("--->>> %s()\n", __func__);

    if ((fp = fopen(CALIFARFLAG, "w+")) == NULL)//"/data/califarflag.txt"
    {
        FT_LOG("%s yhxde fopen fail\n");
        printf("open file error\n");
        return -1;
    }
    else
    {
        fprintf(fp,"%c%c%c%c",chfirst,chsecond,chthird,chforth);
    }
        fclose(fp);
    system("chmod 777 /data/califarflag.txt");
        system("input keyevent 4");
    system("am start -n com.qualcomm.sensors.qsensortest/com.qualcomm.sensors.qsensortest.TabControl");

    FT_LOG("[%d] ret...\n",ret);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}

int ft_plsensor_calibration_delete()
{

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    system("rm -rf /data/califarflag.txt");
    system("rm -rf  /data/calinearflag.txt");
    system("rm -rf  /data/caliread2cm.txt");
    system("rm -rf  /data/caliread4cm.txt");
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
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
    int ret = -1;
    int fd, n;
    char enable[]="";
    char enable_cmd[128];
    int num = 0;
    char sensorPath[100] = {0};

    FT_LOG("[%s:%d]start...\n",__func__,__LINE__);
#if 0
    num = find_inputNum(P_SENSOR_NAME);
    FT_LOG("[%s,%d] the input number is:%d\n",__func__,__LINE__,num);
    if(num >= 16)
    {
        FT_LOG("[%s,%d],Can not find input event\n",__func__,__LINE__);
        return -1;
    }
    sprintf(sensorPath,"%s%d/%s",SENSOR_PATH,num,SENSOR_ENABLE);
    FT_LOG("[%s,%d] Sensor input path is:%s\n",__func__,__LINE__,sensorPath);
    fd = open(sensorPath, O_RDWR);
#endif
    fd = open(P_SENSOR_ENABLE, O_RDWR);
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
    sprintf(enable_cmd, "echo 0 > %s", sensorPath);
    ret = system(enable_cmd);
*/
    ret = write(fd,SENSOR_OFF,strlen(SENSOR_OFF));
    if (ret == -1)
    {
        FT_LOG("[%s:%d]Error close l-sensor \n",__func__,__LINE__);
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
