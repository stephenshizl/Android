#include "include/ft.h"
#include "include/ft_sys.h"
#include <string.h>
#include <pthread.h>
#include <stdio.h>
#define EAR_AC1_TO_RECEIVER 1
#define EAR_AC2_TO_RECEIVER 2
#define TOP_AC1_TO_RECEIVER 3
#define TOP_AC2_TO_RECEIVER 4
#define BUTTOM_AC1_TO_RECEIVER 5
#define BUTTOM_AC2_TO_RECEIVER 6
#define TOP_SPEAKER 7
#define BUTTOM_SPEAKER 8
#define PINK_NOISE 9

#define PS_FTM_CMD "ps mm-audio-ftm"
#define MAX_CHAR 512
static int dur_value = 0;
static int vol_value = 0;
static int fre_value = 0;

void* ft_ac1_to_receiver(){
    char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 52 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/test.wav",dur_value,vol_value);
    system(cmd_buf);
    return (void*)0;
}

void* ft_ac2_to_receiver(){
    char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 15 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/test.wav",dur_value,vol_value); 
    system(cmd_buf);
    return (void*)0;
}

void* ft_top_mic_ac1(){
    char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 54 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/test.wav",dur_value,vol_value);
    system(cmd_buf);
    return (void*)0;
}

void* ft_top_mic_ac2(){
   char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 16 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/test.wav",dur_value,vol_value); 
    system(cmd_buf);
    return (void*)0;
}

void* ft_buttom_mic_ac1(){
    char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 53 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/test.wav",dur_value,vol_value);
    system(cmd_buf);
    return (void*)0;
}

void* ft_buttom_mic_ac2(){
    char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 55 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/test.wav",dur_value,vol_value);
    system(cmd_buf);
    return (void*)0;
}

void* ft_top_speaker(){
    char cmd_buf[300] = {0};
    if(fre_value == 1)
    {
        sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/R_800Hz.wav",dur_value,vol_value);
    }
    else if(fre_value == 2)
    {
        sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/R_1KHz.wav",dur_value,vol_value);
    }
    else if(fre_value == 3)
    {
        sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/R_2KHz.wav",dur_value,vol_value);
    }
    system(cmd_buf);
    return (void*)0;
}

void* ft_buttom_speaker(){
    char cmd_buf[300] = {0};
    if(fre_value == 1)
    {
        sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/L_800Hz.wav",dur_value,vol_value);
    }
    else if(fre_value == 2)
    {
        sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/L_1KHz.wav",dur_value,vol_value);
    }
    else if(fre_value == 3)
    {
        sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d %d -v %d -file /system/audio_cit/L_2KHz.wav",dur_value,vol_value);
    }
    system(cmd_buf);
    return (void*)0;
}

void* ft_pink_noise(){
    char cmd_buf[300] = {0};
    sprintf(cmd_buf, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d 20 -v 21 -file /system/audio_cit/PinkNoise.wav");
    system(cmd_buf);
    return (void*)0;
}
int ft_speaker_mic(int type, int dur, int vol, int fre)
{    
    dur_value = dur;
    vol_value = vol;
    fre_value = fre;
    printf("******************** duration:%d volume:%d frequence:%d\n", dur_value,vol_value,fre_value);
    pthread_t audio_thread[10];
    int ret = -1;
    kill_mm_audio();
    switch(type)
    {
        case EAR_AC1_TO_RECEIVER:
            pthread_create(&audio_thread[1],NULL,&ft_ac1_to_receiver,NULL);
            break;
        case EAR_AC2_TO_RECEIVER:
            pthread_create(&audio_thread[2],NULL,&ft_ac2_to_receiver,NULL);
            break;
        case TOP_AC1_TO_RECEIVER:
            pthread_create(&audio_thread[3],NULL,&ft_top_mic_ac1,NULL);
            break;
        case TOP_AC2_TO_RECEIVER:
            pthread_create(&audio_thread[4],NULL,&ft_top_mic_ac2,NULL);
            break;
        case BUTTOM_AC1_TO_RECEIVER:
            pthread_create(&audio_thread[5],NULL,&ft_buttom_mic_ac1,NULL);
            break;
        case BUTTOM_AC2_TO_RECEIVER:
            pthread_create(&audio_thread[6],NULL,&ft_buttom_mic_ac2,NULL);
            break;
        case TOP_SPEAKER:
            pthread_create(&audio_thread[7],NULL,&ft_top_speaker,NULL);
            break;
        case BUTTOM_SPEAKER:
            pthread_create(&audio_thread[8],NULL,&ft_buttom_speaker,NULL);
            break;
        case PINK_NOISE:
            pthread_create(&audio_thread[9],NULL,&ft_pink_noise,NULL);
            break;
    }
    printf("ft_speaker_mic------The audio test is finished!");
    if(ret == -1)
    {
        FT_LOG("[%s,%d] Error set awake enable failed\n",__func__,__LINE__);
        return -1;
    }
    return 0;
}

int kill_mm_audio(){
    char buf[MAX_CHAR]; 
    FILE *fp;
    char bla[] = " ";
    char *str;
    int pid;
    char cmd[25] = {0};
    int i = 0, j = 0;
    printf("start kill mm_audio\n");
    if((fp=popen(PS_FTM_CMD, "r"))!=NULL){
        printf("open pipe successfully!\n");
        while(fgets(buf,MAX_CHAR,fp) != NULL)
        {
            if(j == 1)
            {
                break;
             }
             j++;
        } 
        str = strtok(buf, bla);
        while(str != NULL){
            if (i == 1)
            {
                break;
            }
            str = strtok(NULL, bla);
            i++;
        }
        pid = atoi(str);
        if (pid != 0)
        {
            printf("ftm pid = %d\n",pid);
            sprintf(cmd,"%s%d","kill ",pid); 
            printf("ftm cmd = %s\n",cmd);
            system(cmd); 
        }
    }
    else
    {
        printf("open pipe failed\n");
    }
    return 0;
}

int ft_speaker_pink(char *value)
{  
    FILE *fp1;
    FILE *fp2;
    int flag = 0;
    if ((fp1 = fopen("/data/audio_test1", "r")) == NULL)
    {
    }
    else
    {
        flag = flag + 1;
    }
    if ((fp1 = fopen("/data/audio_test2", "r")) == NULL)
    { 
    }
    else
    {
        flag = flag + 2;
    }
    switch(flag)
    {
        case 0:
            strcpy(value, "00");
            break;
        case 1:
            strcpy(value, "10");
            break;
        case 2:
            strcpy(value, "01");
            break;
        case 3:
            strcpy(value, "11");
            break;
    }
    return 1;
}

int ft_speaker_f0_test1(char *value)
{  
    FILE *fp;
    char buf[MAX_CHAR];
    if ((fp = fopen("/data/audio_test1", "r")) == NULL)
    {
        strcpy(value, "1");
    }
    else
    {
        fgets(buf,MAX_CHAR,fp);
        FT_LOG("[%s,%d] audio_test1:%s\n",__func__,__LINE__,&buf);
    }
    fclose(fp);
    strcpy(value, buf);
    return 1;
}

int ft_speaker_f0_test2(char *value)
{  
    FILE *fp;
    char buf[MAX_CHAR];
    if ((fp = fopen("/data/audio_test2", "r")) == NULL)
    { 
        strcpy(value, "1");
    }
    else
    {
       fgets(buf,MAX_CHAR,fp);
       FT_LOG("[%s,%d] audio_test2:%s\n",__func__,__LINE__,&buf);
    }
    fclose(fp);
    strcpy(value, buf);    
    return 1;
}
