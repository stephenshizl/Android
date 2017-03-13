/****************************************************************************
 * fm.c
 ***************************************************************************/
 
/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who          what, where, why
--------  ------       ------------------------------------------------------
20100817  Guo Zhangku  Initial framework.
20130814  Li Tao        add fm interface for auto CIT
===========================================================================*/

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <linux/videodev2.h>
#include <sys/ioctl.h>
#include "include/fm.h"
#include <stdarg.h>

#if 0
#define FM_DEV "/dev/radio0"
#define TUNE_MULT 16000
#define V4L2_AUDIO_MUTE ((0x980000 | 0x900) + 9) //control id,refrence fmRxControl.java

int fm_fd = -1;

int fm_open()
{
    fm_fd = open(FM_DEV, O_RDONLY, O_NONBLOCK);
    if (fm_fd < 0)
    {
        printf("Open FM devices failed!");
        return -FM_ERR_OPEN_FAILED;
    }

    return fm_set_control(V4L2_AUDIO_MUTE, 0);
}

int fm_close()
{
    if(fm_fd < 0)
        return -FM_ERR_NOT_OPEN;
    close(fm_fd);
    fm_fd = -1;
    
    return FM_SUCCESS;
}

int fm_get_frequent()
{
    int err = 0;

    if(fm_fd < 0)
        return -FM_ERR_NOT_OPEN;

    struct v4l2_frequency freq;
    freq.type = V4L2_TUNER_RADIO;
    err = ioctl(fm_fd, VIDIOC_G_FREQUENCY, &freq);
    if(err < 0){
      return FM_FAILURE;
    }
    return ((freq.frequency*1000)/TUNE_MULT);
}

int fm_set_frequent(char* freq)
{
    int err = 0;
    double tune;
    int frequency=0;

    if(fm_fd < 0)
        return -FM_ERR_NOT_OPEN;

    struct v4l2_frequency freq_struct;
    freq_struct.type = V4L2_TUNER_RADIO;
    if(*freq!='\0' && *(freq+1)!='\0'
    frequency=(*freq-'0')*10+*(freq+1)-'0';
    freq_struct.frequency = (frequency*TUNE_MULT/1000);
    err = ioctl(fm_fd, VIDIOC_S_FREQUENCY, &freq_struct);
    if(err < 0){
            return FM_FAILURE;
    }
    return FM_SUCCESS;
}

int fm_set_control(int id, int value)
{
    struct v4l2_control control;
    
    int i = 0;
    int err = -1;

    if(fm_fd < 0)
        return -FM_ERR_NOT_OPEN;

    control.value = value;
    control.id = id;
    for(i=0;i<3;i++) {
        err = ioctl(fm_fd,VIDIOC_S_CTRL,&control);
        if(err >= 0){
            return FM_SUCCESS;
        }
    }

    return FM_FAILURE;
}

int fm_get_control(int id)
{
    struct v4l2_control control;
    int err = -1;

    if(fm_fd < 0)
        return -FM_ERR_NOT_OPEN;

    control.id = id;
    err = ioctl(fm_fd,VIDIOC_G_CTRL,&control);
    if(err < 0){
        return FM_FAILURE;
    }
    return control.value;
}


static void fm_send_cmd(const char *fmt, ...)
{    
    return ;
}
#endif

int FM_Enable()
//int fm_open()
{
//    system("setprop fm.ft-test-enable true");
  //  system("am start -n com.quicinc.fmradio/com.quicinc.fmradio.FMRadio");
    system("am start -n com.caf.fmradio/com.caf.fmradio.FMRadio");

    return 0;
}

int FM_Disable()
//int fm_close()
{
//    system("setprop fm.ft-test-enable false");
    system("input keyevent 8");
    return 0;
}
/*
int fm_get_frequent()
{
    return 0;
}
*/

int Enable_FM_Stereo()
{
    system("input keyevent 9");
    return 0;
}

int Disable_FM_Stereo()
{
    system("input keyevent 10");
    return 0;
}

int FM_Set_Channel(int freq)
//int fm_set_frequent(int freq)
{
    switch (freq+11)
    {
        case 11:
            system("input keyevent 11");
            break;
        case 12:
            system("input keyevent 12");
            break;
        case 13:
            system("input keyevent 13");
            break;
        case 14:
            system("input keyevent 14");
            break;
        case 15:
            system("input keyevent 15");
            break;
    }
    
    return 0;
}

int FM_Set_Volume(int volume)
//int fm_adjust_volume(int volume)
{
    switch (volume + 29)
    {
        case 29:
            system("input keyevent 29");
            break;
        case 30:
            system("input keyevent 30");
            break;
        case 31:
            system("input keyevent 31");
            break;
        case 32:
            system("input keyevent 32");
            break;
        case 33:
            system("input keyevent 33");
            break;
        case 34:
            system("input keyevent 34");
            break;
        case 35:
            system("input keyevent 35");
            break;
        case 36:
            system("input keyevent 36");
            break;
            /*
        case 37:
            system("input keyevent 37");
            break;
        case 38:
            system("input keyevent 38");
            break;
        case 39:
            system("input keyevent 39");
            break;
        case 40:
            system("input keyevent 40");
            break;
        case 41:
            system("input keyevent 41");
            break;
        case 42:
            system("input keyevent 42");
            break;
        case 43:
            system("input keyevent 43");
            break;
            */
    }

    return 0;
}

