#include "include/ft.h"
#include "include/ft_sys.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <pthread.h>
#include <stdlib.h>

char cmd[2048];
void *thread_audio(void *c)
{
    FT_LOG("[%s: %d] *thread_audio start %s...\n", __func__, __LINE__, c);
    int i = system((char *)c);
    return (void *)0;
}

/********************************************
* Description:
* Start speaker test.
*
* Input parameters:
*   duration: set play times;
*   volume: set play volume;
*   frequence: set play frequence;
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_speaker_test(int duration, int volume, int frequence)
{
    int ret = -1;
    char cmd1[50];
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d, frequence:%d\n", __func__, __LINE__, duration, volume, frequence);

    pthread_t ntid;
    pthread_t ntid1;
    memset(cmd, 0, sizeof(cmd));
    memset(cmd1, 0, sizeof(cmd1));

    volume = volume*10;
    sprintf(cmd1, "tinymix 'Speaker Driver Playback Volume' %2d", volume);
    if(frequence == 8)
    {
        frequence = frequence*100;
        sprintf(cmd, "/system/bin/mm-audio-ftm -tc 8 -c /system/etc/ftm_test_config -d %d -v %2d -fl %3d -fh %3d", duration, volume, frequence, frequence);
    }
    else
    {
        frequence = frequence*1000;
        sprintf(cmd, "/system/bin/mm-audio-ftm -tc 8 -c /system/etc/ftm_test_config -d %d -v %2d -fl %4d -fh %4d", duration, volume, frequence, frequence);
    }
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    sleep(3);
    int ret1 = pthread_create(&ntid1, NULL, thread_audio, (void *)cmd1);
    FT_LOG("[%s,%d] cmd1:%s\n", __func__, __LINE__, cmd1);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Speaker test failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}

/********************************************
* Description:
* Test headsetMIC to Headset receiver loop.
*
* Input parameters:
*   duration: set play times;
*   volume: set play volume;
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_headsetMic_to_headsetRecv(int duration, int volume)
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume = volume*10;
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 15 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test headsetMic to headsetRecv Failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
int ft_audio_headsetMic_to_Speaker(int duration, int volume)
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume = volume*10;
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 18 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test headsetMic to headsetRecv Failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
int ft_audio_headsetMic_to_Receiver(int duration, int volume)
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume = volume*10;
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 14 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test headsetMic to headsetRecv Failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
int ft_audio_mic1_to_headset(int duration, int volume)
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume = volume*10;
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 21 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test headsetMic to headsetRecv Failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
int ft_audio_mic2_to_headset(int duration, int volume)
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume = volume*10;
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 16 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test headsetMic to headsetRecv Failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
* Description:
* Start phone receiver test.
*
* Input parameters:
*   duration: set play times;
*   volume: set play volume;
*   frequence: set play frequence;
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_receiver_test(int duration, int volume, int frequence)
{
    int ret = -1;

    FT_LOG("[%s,%d] duration:%d, volume:%d, frequence:%d\n", __func__, __LINE__, duration, volume, frequence);
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume = volume*10;
    if(frequence == 8)
    {
        frequence = frequence*100;
        sprintf(cmd, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config -d %d -v %2d -fl %3d -fh %3d", duration, volume, frequence, frequence);
    }
    else
    {
        frequence = frequence*1000;
        sprintf(cmd, "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config -d %d -v %2d -fl %4d -fh %4d", duration, volume, frequence, frequence);
    }
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Receiver test failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}

/********************************************
* Description:
* Test phoneMIC to headset receiver loop.
*
* Input parameters:
*   duration: set play times;
*   volume: set play volume;
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_phoneMic_headsetRecv(int duration,int volume,int frequence)
{
    int ret = -1;


    FT_LOG("[%s,%d] Start...\n",__func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume= volume*10;
   if(frequence == 8)
    {
        frequence = frequence*100;
        sprintf(cmd, "/system/bin/mm-audio-ftm -tc 57 -c /system/etc/ftm_test_config -d %d -v %2d -fl %3d -fh %3d", duration, volume, frequence, frequence);
    }
    else
    {
        frequence = frequence*1000;
        sprintf(cmd, "/system/bin/mm-audio-ftm -tc 57 -c /system/etc/ftm_test_config -d %d -v %2d -fl %4d -fh %4d", duration, volume, frequence, frequence);
    }
    //sprintf(cmd, "/system/bin/mm-audio-ftm -tc 55 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test phoneMic to headsetRecv failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
* Description:
* Test MainMic to speaker loop.
*
* Input parameters:
*   duration: set play times;
*   volume: set play volume;
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_mainMic_speaker(int duration,int volume)
{
    int ret = -1;


    FT_LOG("[%s,%d] Start...\n",__func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume= volume*10;
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 13 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test ft_audio_mainMic_speaker failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
* Description:
* Test secondMic to speaker loop.
*
* Input parameters:
*   duration: set play times;
*   volume: set play volume;
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_secondMic_speaker(int duration,int volume)
{
    int ret = -1;


    FT_LOG("[%s,%d] Start...\n",__func__, __LINE__);
    FT_LOG("[%s,%d] duration:%d, volume:%d\n", __func__, __LINE__, duration, volume);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    volume= volume*10;
   // sprintf(cmd, "/system/bin/mm-audio-ftm -tc 19 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    sprintf(cmd, "/system/bin/mm-audio-ftm -tc 53 -c /system/etc/ftm_test_config -d %d -v %2d", duration, volume);
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test ft_audio_secondMic_speaker failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}

/********************************************
* Desription:
* restore audio access state.
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_clean_status()
{
    int ret = -1;


    FT_LOG("[%s,%d] Start...\n",__func__, __LINE__);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    sprintf(cmd, "stop media; start media");
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, Test ft_audio_clean_status failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}


/********************************************
* Desription:
* Break off audio access
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_audio_breakoff_audio_access()
{
    int ret = -1;


    FT_LOG("[%s,%d] Start...\n",__func__, __LINE__);

    pthread_t ntid;
    memset(cmd, 0, sizeof(cmd));

    //sprintf(cmd, "busybox killall mm-audio-ftm");
    sprintf(cmd, "busybox killall mm-audio-ftm;sleep 1;/system/bin/mm-audio-ftm -tc 54 -c /system/etc/ftm_test_config -d 0");
    FT_LOG("[%s,%d] cmd:%s\n", __func__, __LINE__, cmd);

    ret = pthread_create(&ntid, NULL, thread_audio, (void *)cmd);
    if (ret < 0) {
        FT_LOG("[%s:%d] Error, break off audio access failed!\n", __func__, __LINE__);
        return ret;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);

    return ret;
}
