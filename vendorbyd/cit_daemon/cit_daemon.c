
#ifdef __cplusplus
extern "C" {
#endif

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <pthread.h>
#include <utils/Log.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_ERROR , "cit_daemon", __VA_ARGS__)
static char* command[] = {"",
"/system/bin/mm-audio-ftm -tc 55 -c /system/etc/ftm_test_config -d 10 -v 70",
"/system/bin/mm-audio-ftm -tc 56 -c /system/etc/ftm_test_config -d 10 -v 70",
"/system/bin/mm-audio-ftm -tc 57 -c /system/etc/ftm_test_config -d 10 -v 70",
"/system/bin/mm-audio-ftm -tc 15 -c /system/etc/ftm_test_config -d 10 -v 70",
"/system/bin/mm-audio-ftm -tc 13 -c /system/etc/ftm_test_config -d 10 -v 90",
"/system/bin/mm-audio-ftm -tc 55 -c /system/etc/ftm_test_config_mtp -d 100 -v 73 -file /system/audio_cit/test.wav",
"/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config -d 20 -v 65 -fl 2000 -fh 2000",
"/system/bin/mm-audio-ftm -tc 8 -c /system/etc/ftm_test_config -d 20 -v 65 -fl 2000 -fh 2000",
"/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d 8 -v 21 -file /system/audio_cit/PinkNoise.wav",
"echo 0 > /sys/class/leds/lcd-backlight/brightness",
"echo 250 > /sys/class/leds/lcd-backlight/brightness",
"echo 1 > /sys/class/leds/red/brightness",
"echo 0 > /sys/class/leds/red/brightness",
"echo 1 > /sys/class/leds/green/brightness",
"echo 0 > /sys/class/leds/green/brightness",
"",
"",
"",
"btconfig /dev/smd3 reset && btconfig /dev/smd3 rawcmd 0x06, 0x03 && btconfig /dev/smd3 rawcmd 0x03, 0x05, 0x02, 0x00, 0x02 && btconfig /dev/smd3 rawcmd 0x03, 0x1A, 0x03 && btconfig /dev/smd3 rawcmd 0x03, 0x20, 0x00 && btconfig /dev/smd3 rawcmd 0x03, 0x22, 0x00 && echo 0 > /data/citflag",
""
}; 

static void btwifitest() {
    int fd = -1;
    char com[1024] = {0};
    while(1) {
    FILE *stream;
    fd = open("/data/btwifi",O_RDONLY);
    if ( fd >= 0) {
        read(fd, com, sizeof(com));
        close(fd);
        //sprintf(com1, "%s && echo 0 > /data/citflag", com);
        system(com);
        
    }
    }
}

static int play_back(int flag)
{   
    LOGW("+++++++++play_back flag=%d+++++++++\n", flag);
    if(flag <= 15 || flag == 19) {
        system(command[flag]);
        system("echo 0 > /data/citlog.txt");
    } else if(flag == 17) {
        system("echo $(cat /data/autoresult.txt) > /userstore/citresult");
        system("chmod 777 /userstore/citresult");
        system("chown system:system /userstore/citresult");
        //system("setprop persist.service.daemon.enable 0");
    }else if(flag ==18){
        system("echo $(cat /data/runinflag.txt) > /userstore/runinflag");
        system("chmod 777 /userstore/runinflag");
        system("chown system:system /userstore/runinflag");
    }else if(flag == 20) {
         btwifitest();
    }else if(flag == 21) {
        //system("echo $(cat /data/recovery.txt) > /userstore/recovery && chmod 777 /userstore/recovery && chown system:system /userstore/recovery && echo 0 > /data/citflag && setprop persist.service.daemon.enable 0");
        system("chmod 777 /userstore/recovery && chown system:system /userstore/recovery && echo 0 > /data/citflag && setprop persist.service.daemon.enable 0");
    }else if(flag == 22) {
        system("/system/bin/mm-audio-ftm -tc 20 -c /system/etc/ftm_test_config -d 10 -v 101");
    }else if(flag == 23) {
        system("/system/bin/mm-audio-ftm -tc 16 -c /system/etc/ftm_test_config -d 10 -v 70");
    }else if(flag == 24) {
        system("/system/bin/mm-audio-ftm -tc 21 -c /system/etc/ftm_test_config -d 10 -v 70");
    }else if(flag == 25) {
        system("/system/bin/mm-audio-ftm -tc 15 -c /system/etc/ftm_test_config -d 10 -v 101");
    }
    //android_printLog(3, "cit_daemon","command%d",flag);
    return 0;
}


static int  getsarsensor()
{
    int fd = -1;
    char buffer1[10] = {0};
    char buffer2[10] = {""};
    int flag = 1;
    FILE *stream;

    fd = open("/data/sarevent.txt",O_RDONLY);
    char com1[80] = {0};
    char com2[80] = {0};
    int i = 0;
    if ( fd >= 0) {
        printf("+++++++++read data/citflag+++++++++\n");
        read(fd, buffer1, sizeof(buffer1));
        close(fd);
        stream = fopen( "/data/citlog.txt", "w" );
        int len = strlen(buffer1);
        for(i = 0; i < len ; i++) {
            if(buffer1[i] == '\n') buffer1[i] = '\0';
        }
        fprintf(stream, "...sdev:%s...\n", buffer1);// print log
        sprintf(com1, "/dev/input/%s", buffer1);
        fprintf(stream, "...sdev com1:%s...-1:\n", com1);
        //system("echo 1 > /sys/bus/i2c/devices/0-0044/enable");
        while(flag){
          /*fd = open(com1, O_RDONLY);
          if ( fd >= 0) {
            printf("+++++++++read data/citflag+++++++++\n");
            read(fd, buffer2, sizeof(buffer2));
          } else if ( fd < 0){
            printf("cit_daemon open %s...", com1);
          }
          close(fd);
          i = i + 1;*/
          sprintf(com2, "cat %s > /data/getsarevent.txt;", com1);
          fprintf(stream, "command2:%s...\n", com2);
          system(com2);

        }
        fclose( stream );
    } else if ( fd < 0){
        fprintf(stream, "...fail open sarevent ");// print log
        printf("cit_daemon open /data/citflag fail");
        close(fd);
        sprintf(com2, "echo  > /data/getsarevent.txt");
        system(com2);
        system("chmod 777 /data/getsarevent.txt");

    }

    return 0;
}

int main(void)
{
    int fd = -1;
    char buffer[10];
    int flag = -1;
    int now = 0;
    //pthread_t fm_play_thread;
    //pthread_create(&fm_play_thread, NULL, play_back, NULL);

    fd = open("/data/citflag",O_RDONLY);
    if ( fd >= 0) {
        LOGW("+++++++++read data/citflag+++++++++\n");
        read(fd, buffer, sizeof(buffer));
        now = atoi(buffer);
    } else if ( fd < 0){
        printf("cit_daemon open /data/citflag fail");
    }
    close(fd);
    if(now <= 15 || now >= 17) {
      //while(1) {
        {
         LOGW("+++++++++begin run command+++++++++\n");
        //android_printLog(3, "cit_daemon","cit_daemon begin run command");
         fd = open("/data/citflag",O_RDONLY);
         if ( fd >= 0) {
            LOGW("+++++++++file open success+++++++++\n");
            //android_printLog(3, "cit_daemon","file open success");
            read(fd, buffer, sizeof(buffer));
            int i = atoi(buffer);
            if(i != flag ) play_back(i);
            flag = i;
            LOGW("+++++++++end run command flag=%d+++++++++\n", flag);
            //android_printLog(3, "cit_daemon","end run command");
         } else if ( fd < 0){
            printf("cit_daemon fail");
            //android_printLog(3, "cit_daemon","cit_daemon fail");
         }
         close(fd);
      }
    }else if(now == 16) {
         getsarsensor();
    }
    return 0;
}


#ifdef __cplusplus
}
#endif
