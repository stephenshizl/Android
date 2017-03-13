/****************************************************************************
 * wifi.c
 ***************************************************************************/

/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who          what, where, why
--------  ------       ------------------------------------------------------

===========================================================================*/

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <pthread.h>
#include <errno.h>
#include <string.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <sys/socket.h>

#include "include/ft.h"
#include "include/ft_sys.h"


#define MAX_LEN 512
#define DIAG_WIFI_DEBUG   0

typedef enum
{
    WIFI_DIAG_802_11_A_MODE = 1,
    WIFI_DIAG_802_11_B_MODE,
    WIFI_DIAG_802_11_G_MODE,
    WIFI_DIAG_802_11_N_MODE,
    WIFI_DIAG_802_11_Z_MODE,

    WIFI_DIAG_802_11_MAX_MODE
}WIFI_DIAG_MODE;


#define WIFI_CAIBITRATE_FILE  "/persist/bdata.bin"
#define CAMERA_PHOTO_FILE      "/data/ftcamera.jpg"
#define    PULL_WIFI_CALIBRATE_FILE_ID    0x01
#define    PULL_CAMERA_PHOTO_FILE_ID      0x02
int         s_wifi_mode;
int         s_wifi_channel;
float        s_wifi_rate;
int         s_wifi_power_level;
char        s_wifi_sine_wave[6] = {0};
volatile char s_data_num_old = 0;

struct wifi_diag_ops {
    int        (*init)(void);
    int        (*open)(void);
    int        (*close)(void);
    int        (*scan_ssid)(char* response_ssid_list, int list_length);
    int        (*tx_set)(int  mode, int channel,  int rate, int power_level, int sine_wave);
    int        (*tx_start) (void);
    int        (*tx_stop) (void);
    int        (*rx_set) (int mode, int channel, int rate);
    int        (*rx_report) (char* response_packet, int pkt_length);
    int        (*get_version) (char* response_version, int ver_length);
    int        (*set_mac_address) (char* mac_addr_buf);
    int        (*pull_file) (char* phone_file_path, char* pc_file_path, unsigned  char* data, int data_len, int file_id, char* file_stat);
    int        (*push_file) (char* pc_file_path, char* phone_file_path, unsigned  char* data, int data_num, int data_len, char del_flag);
};

struct rateStr {
    char      rate_str[30];
} ;


int atheros_wifi_init(void);
int atheros_wifi_open(void);
int atheros_wifi_close(void);
int atheros_wifi_scan_ssid(char* response_ssid_list, int list_length);
int atheros_wifi_tx_set(int  mode, int channel,  int rate, int power_level, int sine_wave);
int atheros_wifi_tx_start(void);
int atheros_wifi_tx_stop(void);
int atheros_wifi_rx_set(int mode, int channel, int rate);
int atheros_wifi_rx_report(char* response_packet, int pkt_length);
int atheros_wifi_get_version(char* response_version, int ver_length);
int atheros_wifi_set_mac_address(char* mac_addr_buf);
int atheros_wifi_pull_file(char* phone_file_path, char* pc_file_path, unsigned  char* data, int data_len, int  file_id, char* file_stat);
int atheros_wifi_push_file(char* pc_file_path, char* phone_file_path, unsigned  char* data, int data_num, int data_len, char del_flag);


int broadcomm_wifi_init(void);
int broadcomm_wifi_open(void);
int broadcomm_wifi_close(void);
int broadcomm_wifi_scan_ssid(char* response_ssid_list, int list_length);
int broadcomm_wifi_tx_set(int  mode, int channel,  int rate, int power_level, int sine_wave);
int broadcomm_wifi_tx_start(void);
int broadcomm_wifi_tx_stop(void);
int broadcomm_wifi_rx_set(int mode, int channel, int rate);
int broadcomm_wifi_rx_report(char* response_packet, int pkt_length);
int broadcomm_wifi_get_version(char* response_version, int ver_length);
int broadcomm_wifi_set_mac_address(char* mac_addr_buf);
int broadcomm_wifi_pull_file(char* phone_file_path, char* pc_file_path, unsigned  char* data, int data_len, int  file_id, char* file_stat);
int broadcomm_wifi_push_file(char* pc_file_path, char* phone_file_path, unsigned  char* data, int data_num, int data_len, char del_flag);

const struct wifi_diag_ops wifi_driver_handler = {
#ifndef WIFI_DIAG_BROADCOMM
    .init = atheros_wifi_init,
    .open = atheros_wifi_open,
    .close = atheros_wifi_close,
    .scan_ssid = atheros_wifi_scan_ssid,
    .tx_set = atheros_wifi_tx_set,
    .tx_start = atheros_wifi_tx_start,
    .tx_stop = atheros_wifi_tx_stop,
    .rx_set = atheros_wifi_rx_set,
    .rx_report = atheros_wifi_rx_report,
    .get_version = atheros_wifi_get_version,
    .set_mac_address = atheros_wifi_set_mac_address,
    .pull_file = atheros_wifi_pull_file,
    .push_file = atheros_wifi_push_file
#else
    .init = broadcomm_wifi_init,
    .open = broadcomm_wifi_open,
    .close = broadcomm_wifi_close,
    .scan_ssid = broadcomm_wifi_scan_ssid,
    .tx_set = broadcomm_wifi_tx_set,
    .tx_start = broadcomm_wifi_tx_start,
    .tx_stop = broadcomm_wifi_tx_stop,
    .rx_set = broadcomm_wifi_rx_set,
    .rx_report = broadcomm_wifi_rx_report,
    .get_version = broadcomm_wifi_get_version,
    .set_mac_address = broadcomm_wifi_set_mac_address,
    .pull_file = broadcomm_wifi_pull_file,
    .push_file = broadcomm_wifi_push_file
#endif
};

#define B_TABLE_LEN 7
const struct rateStr rateName_B_tbl[B_TABLE_LEN] =
{
   {"11B_LONG_1_MBPS"},
   {"11B_LONG_2_MBPS"},
   {"11B_LONG_5_5_MBPS"},
   {"11B_LONG_11_MBPS"},
   {"11B_SHORT_2_MBPS"},
   {"11B_SHORT_5_5_MBPS"},
   {"11B_SHORT_11_MBPS"}
 };

#define G_TABLE_LEN 8
//Spica_Virgo 11A 20MHz Rates
const struct  rateStr rateName_G_tbl[G_TABLE_LEN] =
{
   {"11A_6_MBPS"},
   {"11A_9_MBPS"},
   {"11A_12_MBPS"},
   {"11A_18_MBPS"},
   {"11A_24_MBPS"},
   {"11A_36_MBPS"},
   {"11A_48_MBPS"},
   {"11A_54_MBPS"}
};

#define N_TABLE_LEN 32

const struct rateStr rateName_N_tbl[N_TABLE_LEN] =
{
//MCS Index #0-15 (20MHz)
   {"MCS_6_5_MBPS"},
   {"MCS_13_MBPS"},
   {"MCS_19_5_MBPS"},
   {"MCS_26_MBPS"},
   {"MCS_39_MBPS"},
   {"MCS_52_MBPS"},
   {"MCS_58_5_MBPS"},
   {"MCS_65_MBPS"},
   {"MCS_SG_7_2_MBPS"},
   {"MCS_SG_14_4_MBPS"},
   {"MCS_SG_21_7_MBPS"},
   {"MCS_SG_28_9_MBPS"},
   {"MCS_SG_43_3_MBPS"},
   {"MCS_SG_57_8_MBPS"},
   {"MCS_SG_65_MBPS"},
   {"MCS_SG_72_2_MBPS"},

//MCS Index #8-15 (40MHz)

   {"MCS_CB_13_5_MBPS" },
   {"MCS_CB_27_MBPS" },
   {"MCS_CB_40_5_MBPS" },
   {"MCS_CB_54_MBPS"},
   {"MCS_CB_81_MBPS"},
   {"MCS_CB_108_MBPS"},
   {"MCS_CB_121_5_MBPS"},
   {"MCS_CB_135_MBPS"},
   {"MCS_SG_CB_15_MBPS"},
   {"MCS_SG_CB_30_MBPS"},
   {"MCS_SG_CB_45_MBPS"},
   {"MCS_SG_CB_60_MBPS"},
   {"MCS_SG_CB_90_MBPS"},
   {"MCS_SG_CB_120_MBPS"},
   {"MCS_SG_CB_135_MBPS"},
   {"MCS_SG_CB_150_MBPS"}

};
int tx_set_status = 0;
static int fp_read_file(char* dest_data,  FILE *fp, char * cmp_key)
{
        char *line = NULL;
        char line_buf[128];

    /* Reads n-1 most character from FP until a newline is found when go
    throug the whole file pointed by FP */
        while (fgets(line_buf, 128, fp) != NULL) {
           if (strstr(line_buf, cmp_key) != NULL) {
            sprintf(dest_data, "%s", line_buf);
            dest_data += strlen(dest_data);
            continue;
           }
        }
        return 0;
}

int atheros_wifi_init(void)
{
    int ret = -1;
    FT_LOG("[%s,%d]DIAG ATHEROS WIF\n",__func__,__LINE__);

    //system("setprop persist.secret.root 1");

   //for QC WIFI INIT BY ZKY.
    ret = system("ptt_socket_app -v -f");
    if(ret != 0)
    {
        FT_LOG("[%s,%d] Error,start ptt_socket_app server fail\n",__func__,__LINE__);
        return -1;
    }
    return 0;
}

int atheros_wifi_open(void)
{
        FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    system("rmmod wlan");
    system("insmod /system/lib/modules/pronto/pronto_wlan.ko con_mode=5");// ftm mode

    return 0;
}


int atheros_wifi_close(void)
{
    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    system("ifconfig wlan0 down");
    system("rmmod wlan");

        return 0;
}


int atheros_wifi_scan_ssid(char* response_ssid_list, int list_length)
{
    FILE *fp;
    char *line = NULL;

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    return 0;
}


int atheros_wifi_tx_set(int mode, int channel,  int rate, int power_level, int sine_wave)
{
    char cmd[100] = {0};
    float wifi_rate;
    int     wifi_channel;

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);
        FT_LOG("Begin: mode: %d; power_level:%d;channel: %d,rate :%d\n",mode,power_level,channel,rate);
        system("iwpriv wlan0 ftm 1"); // start ftm
    system("iwpriv wlan0 tx 0"); //tx stop
    system("iwpriv wlan0 set_cb 0"); //set channel bonding mode
    sprintf(cmd,"iwpriv wlan0 set_channel %d",channel);
        FT_LOG("[%s,%d]DIAG ATHEROS WIFI cmd is %s \n",__func__,__LINE__,cmd);
    system(cmd); //set to channel
    system("iwpriv wlan0 ena_chain 2"); //enable tx chain
    system("iwpriv wlan0 pwr_cntl_mode 2"); //CLPC mode
    memset(cmd,0x00,100);
    sprintf(cmd,"iwpriv wlan0 set_txpower %d",power_level);
        FT_LOG("[%s,%d]DIAG ATHEROS WIFI cmd is %s \n",__func__,__LINE__,cmd);
    system(cmd); // set tx power to 10 db
    /* Convert user rate to WIFI rate */
        memset(cmd,0x00,100);
    if(WIFI_DIAG_802_11_A_MODE == mode
            || WIFI_DIAG_802_11_G_MODE == mode)  {
            if(rate >0 && rate <= G_TABLE_LEN) {
                sprintf(cmd,"iwpriv wlan0 set_txrate %s",&rateName_G_tbl[rate-1]);
            } else {
                FT_LOG("[%s,%d]WIFI_DIAG_802_11_G_MODE WIFI\n",__func__,__LINE__);
                return -1;
            }
    } else if(WIFI_DIAG_802_11_B_MODE == mode)  {
            if(rate >0 && rate <= B_TABLE_LEN) {
                sprintf(cmd,"iwpriv wlan0 set_txrate %s",&rateName_B_tbl[rate-1]);
            } else {
                FT_LOG("[%s,%d]WIFI_DIAG_802_11_B_MODE WIFI\n",__func__,__LINE__);
                return -1;
            }
        } else if(WIFI_DIAG_802_11_N_MODE == mode){
            if (rate >0 && rate <= N_TABLE_LEN) {
                sprintf(cmd,"iwpriv wlan0 set_txrate %s", &rateName_N_tbl[rate-1]);
            } else {
                FT_LOG("[%s,%d]WIFI_DIAG_802_11_N_MODE WIFI\n",__func__,__LINE__);
                return -1;
            }
    } else {
            FT_LOG("[%s,%d]non WIFI\n",__func__,__LINE__);
                return -1;
        }

        FT_LOG("[%s,%d]DIAG ATHEROS WIFI cmd is %s \n",__func__,__LINE__,cmd);

        system(cmd); //set tx rate

    return 0;
}


int atheros_wifi_tx_start(void)
{

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    system("iwpriv wlan0 tx 1"); //tx start

    return 0;
}


int atheros_wifi_tx_stop(void)
{

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n", __func__,__LINE__);

    system("iwpriv wlan0 tx 0"); //tx stop

    return 0;
}

int atheros_wifi_rx_set(int mode, int channel,  int rate)
{
    char cmd_buf[100];

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

        memset(cmd_buf,0x00,100);
        sprintf(cmd_buf,"iwpriv wlan0 set_channel %d",channel);
        FT_LOG("[%s,%d]DIAG ATHEROS WIFI cmd is %s \n",__func__,__LINE__,cmd_buf);
    system(cmd_buf); //set to channel 6
    system("iwpriv wlan0 ena_chain 1");//enable rx chain
    system("iwpriv wlan0 clr_rxpktcnt 1"); //clear count
    system("iwpriv wlan0 rx 1"); //rx start

    return 0;
}

int atheros_wifi_rx_report(char* response_packet, int pkt_length)
{
    FILE *fp;

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    system("iwpriv wlan0 get_rxpktcnt > /data/misc/wifi/rxpktcnt"); //get rx packet count
    if ((fp = fopen("/data/misc/wifi/rxpktcnt", "r+")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }

    fp_read_file(response_packet,fp,"get_rxpktcnt:");
    fclose(fp);
    system("rm /data/misc/wifi/rxpktcnt");

    return 0;
}

int atheros_wifi_get_version(char* response_version, int ver_length)
{
    FILE *fp;

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    return 0;
}

int atheros_wifi_set_mac_address(char* mac_addr_buf)
{
    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

    return 0;
}
int atheros_wifi_pull_file(char* phone_file_path, char* pc_file_path, unsigned char* data, int data_len, int  file_id, char* file_stat)
{
        FILE *fp =NULL;
        char athtestcmd_buf[100];
     int len;
     static int curr_cursor = 0;

        FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);

        switch (file_id)
        {
        case PULL_WIFI_CALIBRATE_FILE_ID:
            if ((fp = fopen(WIFI_CAIBITRATE_FILE, "r+")) == NULL)     {
                FT_LOG("[%s,%d]open file(%s) error: %s\n", __func__,__LINE__,WIFI_CAIBITRATE_FILE, strerror(errno));
                return -1;
            }
            break;
        case PULL_CAMERA_PHOTO_FILE_ID:
            if ((fp = fopen(CAMERA_PHOTO_FILE, "r+")) == NULL)     {
                FT_LOG("[%s,%d]open file(%s) error:%s\n", __func__,__LINE__,CAMERA_PHOTO_FILE, strerror(errno));
                return -1;
            }
            break;
        default:
            break;
        };

       fseek(fp, curr_cursor, SEEK_SET);
    len = fread(data, sizeof(unsigned char), data_len, fp);

    /* The file be read completed and renew curr_cursor position */
    if (feof(fp)) {
        *file_stat = 1;
        curr_cursor = 0;
    } else {
        *file_stat = 0;
    }
        fclose(fp);

    /* Move the cursor to next position for reading */
    curr_cursor += len;

#if DIAG_WIFI_DEBUG
    {
    unsigned char len_high = 0;
    unsigned char len_low = 0;

    int j = 0;
    int i =0;

       len_high = (len >> 8) & 0x00ff;
       len_low = (len & 0x00ff);

        while (i++ < data_len) {
        printf("%2.2x ", *(data++));
        if (j++ > 16) {
            printf("\n");
            j = 0;
        }
        }
    printf("\n");
    printf("len=%d(high=%x, low=%x), file state(%d)\n", len, len_high, len_low, *file_stat);
    }
#endif /* DIAG_WIFI_DEBUG */

    return len;
}

int atheros_wifi_push_file(char* pc_file_path, char* phone_file_path,  unsigned char* data, int data_num, int data_len, char del_flag)
{
    FILE *fp = NULL;
    char athtestcmd_buf[100];
        int len = 0;

    FT_LOG("[%s,%d]DIAG ATHEROS WIFI\n",__func__,__LINE__);
    memset(athtestcmd_buf, 0, sizeof(athtestcmd_buf));

    if (del_flag == 1) {
            system("chmod 644 /persist/bdata.bin");
                system("mv /persist/bdata.bin  /persist/bdata.old.bin");
                    FT_LOG("[%s,%d]backup file succss\n",__func__,__LINE__);
            return 0;
       } else if (del_flag == 2) {
                system("mv /persist/bdata.old.bin  /persist/bdata.bin");
                    FT_LOG("[%s,%d]restore file succss\n",__func__,__LINE__);
        return 0;
    }

    FT_LOG("[%s,%d]before old data num(%d), data num(%d) \n", __func__,__LINE__,s_data_num_old, data_num);

    /* In order to avoid same packet sending repeatly */
    if (s_data_num_old == data_num) {
             FT_LOG("[%s,%d]duplicate data \n",__func__,__LINE__);
        return 0;
    }

    s_data_num_old = data_num;
    FT_LOG("[%s,%d]after old data num(%d), data num(%d)\n", __func__,__LINE__,s_data_num_old, data_num);

    if ((fp = fopen("/persist/bdata.bin", "ab+")) == NULL)
    {
        FT_LOG("[%s,%d]open file error: %s\n", __func__,__LINE__,strerror(errno));
        return -1;
    }

        len = fwrite(data, sizeof(char), data_len, fp);
        fclose(fp);

       FT_LOG("[%s,%d]data length=%d, ret=%d:\n", __func__,__LINE__,data_len, len);
#if DIAG_WIFI_DEBUG
    {
        int i = 0;
        while (i++ < data_len) {
        printf("%2.2x ", *(data++));
        }
        printf("\n");
    }
#endif /* DIAG_WIFI_DEBUG */

    return len;
}

int broadcomm_wifi_init(void)
{
    FT_LOG("[%s,%d]DIAG BROADCOMM WIFI\n",__func__,__LINE__);

    return 0;
}

int broadcomm_wifi_open(void)
{
    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    system("echo 1 > /sys/devices/platform/msm_sdcc.2/polling");
    system("insmod /system/wifi/dhd.ko \"firmware_path=/system/wifi/sdio-g-cdc-full11n-reclaim-roml-wme-aoe-pktfilter-wapi.bin nvram_path=/system/wifi/nvram.txt\"");
    system("ifconfig eth0 up");
    return 0;

}

int broadcomm_wifi_close(void)
{
    printf("DIAG BROADCOMM WIFI: %s\n",__func__);


    system("ifconfig eth0 down");
    system("rmmod dhd");
    system("echo 0 > /sys/devices/platform/msm_sdcc.2/polling");
    return 0;
}

int broadcomm_wifi_scan_ssid(char * response_ssid_list, int list_length)
{
    FILE *fp;
    char *line = NULL;
    char buf[MAX_LEN];

    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    system("/system/wifi/wlarm_android scan");
    system("sleep 6");
    system("/system/wifi/wlarm_android scanresults > /system/wifi/brcm_ssid_temp.txt");

    if ((fp = fopen("/system/wifi/brcm_ssid_temp.txt", "r+")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }

    /* Reverse character '\' that is begin with SSID: " string */
    fp_read_file(response_ssid_list,  fp, "SSID: \"");

    fclose(fp);
    system("rm /system/wifi/brcm_ssid_temp.txt");

    return 0;
}

int broadcomm_wifi_tx_set(int mode, int channel, int rate, int power_level, int sine_wave)
{
    char wlarm_android_buf[100];
    int wifi_channel;
    float wifi_rate;

    printf("DIAG BROADCOMM WIFI: %s\n",__func__);


    /*
    mode: 802.11 a/b/g/n
    chanel:
    802.11 b/g          1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14
     802.11 n              36, 40, 44, 48, 52, 56, 60, 64,100, 104, 108, 112, 116,120, 124, 128, 132, 136,
                       140,149, 153, 157, 161,184, 188, 192, 196, 200, 204, 208, 212, 216
    rate:
       802.11 b             1, 2, 5.5, 11
       802.11 g/n          1, 2, 5.5, 6, 9, 11, 12, 18, 24, 36, 48, 54
       802.11 a              6, 9, 12, 18, 24, 36, 48, 54
    */

    if((mode != WIFI_DIAG_802_11_A_MODE) && (mode != WIFI_DIAG_802_11_A_MODE) &&
    (mode != WIFI_DIAG_802_11_B_MODE) && (mode != WIFI_DIAG_802_11_N_MODE))
    {
        return -1;
    }

    if (WIFI_DIAG_802_11_A_MODE == mode) {
        switch(channel) {
            case 0:        wifi_channel = 36;        break;
            case 1:        wifi_channel = 36;        break;
            case 2:        wifi_channel = 40;        break;
            case 3:        wifi_channel = 44;        break;
            case 4:        wifi_channel = 48;        break;
            case 5:        wifi_channel = 52;        break;
            case 6:        wifi_channel = 56;        break;
            case 7:        wifi_channel = 60;        break;
            case 8:        wifi_channel = 64;        break;
            case 9:        wifi_channel = 100;    break;
            case 10:        wifi_channel = 104;    break;
            case 11:        wifi_channel = 108;    break;
            case 12:        wifi_channel = 112;    break;
            case 13:        wifi_channel = 116;    break;
            case 14:        wifi_channel = 120;    break;
            case 15:        wifi_channel = 124;    break;
            case 16:        wifi_channel = 128;    break;
            case 17:        wifi_channel = 132;    break;
            case 18:        wifi_channel = 136;    break;
            case 19:        wifi_channel = 140;    break;
            case 20:        wifi_channel = 149;    break;
            case 21:        wifi_channel = 153;    break;
            case 22:        wifi_channel = 157;    break;
            case 23:        wifi_channel = 161;    break;
            case 24:        wifi_channel = 184;    break;
            case 25:        wifi_channel = 188;    break;
            case 26:        wifi_channel = 192;    break;
            case 27:        wifi_channel = 196;    break;
            case 28:        wifi_channel = 200;    break;
            case 29:        wifi_channel = 204;    break;
            case 30:        wifi_channel = 208;    break;
            case 31:        wifi_channel = 212;    break;
            case 32:        wifi_channel = 216;    break;
            default:        wifi_channel = 36;        break;
        }
    } else {
        switch(channel) {
            case 0:        wifi_channel = 1;        break;
            case 1:        wifi_channel = 1;           break;
            case 2:        wifi_channel = 2;           break;
            case 3:        wifi_channel = 3;        break;
            case 4:        wifi_channel = 4;        break;
            case 5:        wifi_channel = 5;        break;
            case 6:        wifi_channel = 6;        break;
            case 7:        wifi_channel = 7;        break;
            case 8:        wifi_channel = 8;        break;
            case 9:        wifi_channel = 9;        break;
            case 10:        wifi_channel = 10;        break;
            case 11:        wifi_channel = 11;        break;
            case 12:        wifi_channel = 12;        break;
            case 13:        wifi_channel = 13;        break;
            case 14:        wifi_channel = 14;        break;
            default:        wifi_channel = 1;        break;
        }
    }

    sprintf(wlarm_android_buf, "/system/wifi/wlarm_android channel %d", wifi_channel);

    if(WIFI_DIAG_802_11_A_MODE == mode)  {
        switch(rate) {
            case 0:          wifi_rate = 54;      break;
            case 1:          wifi_rate = 6;         break;
            case 2:        wifi_rate = 9;        break;
            case 3:          wifi_rate = 12;       break;
            case 4:          wifi_rate = 18;       break;
            case 5:          wifi_rate = 24;       break;
            case 6:        wifi_rate = 36;    break;
            case 7:        wifi_rate = 48;    break;
            case 8:        wifi_rate = 54;    break;
            default:        wifi_rate = 54;    break;
        }
    } else if(WIFI_DIAG_802_11_A_MODE == mode)  {
        switch(rate) {
            case 0:        wifi_rate = 11;    break;
            case 1:        wifi_rate = 1;        break;
            case 2:        wifi_rate = 2;        break;
            case 3:        wifi_rate = 5.5;    break;
            case 4:        wifi_rate = 11;    break;
            default:        wifi_rate = 11;    break;
         }
    } else {
        switch(rate) {
            case 0:        wifi_rate = 54;    break;
            case 1:        wifi_rate = 1;        break;
            case 2:        wifi_rate = 2;        break;
            case 3:        wifi_rate = 5.5;    break;
            case 4:        wifi_rate = 6;        break;
            case 5:        wifi_rate = 9;        break;
            case 6:        wifi_rate = 11;    break;
            case 7:        wifi_rate = 12;    break;
            case 8:        wifi_rate = 18;    break;
            case 9:        wifi_rate = 24;    break;
            case 10:        wifi_rate = 36;    break;
            case 11:        wifi_rate = 48;    break;
            case 12:        wifi_rate = 54;    break;
            default:        wifi_rate = 54;    break;
        }
    }

    s_wifi_mode = mode;
    s_wifi_channel = wifi_channel;
    s_wifi_rate = wifi_rate;
    s_wifi_power_level = power_level;

    system("/system/wifi/wlarm_android down");
    system("/system/wifi/wlarm_android mpc 0");
    system("/system/wifi/wlarm_android country ALL");
    system("/system/wifi/wlarm_android frameburst 1");
    system("/system/wifi/wlarm_android scansuppress 1");
    system("/system/wifi/wlarm_android up");
    system(wlarm_android_buf);
    memset(wlarm_android_buf, 0, sizeof(wlarm_android_buf));

    if(WIFI_DIAG_802_11_N_MODE == mode) {
          sprintf(wlarm_android_buf, "system/wifi/wlarm_android nrate -m %f", wifi_rate);
          system(wlarm_android_buf);
    } else {
          sprintf(wlarm_android_buf, "/system/wifi/wlarm_android rate  %f", wifi_rate);
          system(wlarm_android_buf);
    }

    tx_set_status = 1;
    return 0;
}

int broadcomm_wifi_tx_start(void)
{
    char wlarm_android_buf[100];

    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    if(tx_set_status != 1) {
        return -1;
    }

    if(s_wifi_power_level == 0) {
        s_wifi_power_level = 20;
    }

    sprintf(wlarm_android_buf, "/system/wifi/wlarm_android txpwr1 -o -d %d", s_wifi_power_level);
    system(wlarm_android_buf);
    system("/system/wifi/wlarm_android pkteng_start 00:90:4C:C5:00:D8 tx 100 1024 0");

    tx_set_status = 0;
    return 0;
}

int broadcomm_wifi_tx_stop(void)
{
    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    system("/system/wifi/wlarm_android pkteng_stop tx");
    return 0;
}

int broadcomm_wifi_rx_set(int mode, int channel, int rate)
{
    printf("DIAG BROADCOMM WIFI: %s\n",__func__);
    mode = 0;
    channel = 0;
    rate = 0;

        return 0;
}

int broadcomm_wifi_rx_report(char* response_packet, int pkt_length)
{
    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    return 0;
}

int broadcomm_wifi_get_version(char * response_version, int ver_length)
{
    FILE *fp;

    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    system("/system/wifi/wlarm_android ver > /system/wifi/brcm_ver_temp.txt");

    if ((fp = fopen("/system/wifi/brcm_ver_temp.txt", "r+")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }

    fp_read_file(response_version,  fp, "version");

    fclose(fp);
    system("rm /system/wifi/brcm_ver_temp.txt");

    return 0;
}

int broadcomm_wifi_set_mac_address(char* mac_addr_buf)
{
    printf("DIAG BROADCOMM WIFI: %s\n",__func__);

    return 0;
}

int broadcomm_wifi_pull_file(char* phone_file_path, char* pc_file_path, unsigned  char* data, int data_len, int file_id, char* file_stat)
{
    char athtestcmd_buf[100];

    printf("DIAG ATHEROS WIFI: %s\n",__func__);
    memset(athtestcmd_buf, 0,  sizeof(athtestcmd_buf));

    system("adb shell");
    sprintf(athtestcmd_buf, "adb pull %s %s", phone_file_path, pc_file_path);
    system(athtestcmd_buf);

    return 0;
}

int broadcomm_wifi_push_file(char* pc_file_path, char* phone_file_path, unsigned  char* data, int data_num, int data_len, char del_flag)
{
    char athtestcmd_buf[100];

    printf("DIAG ATHEROS WIFI: %s\n",__func__);
    memset(athtestcmd_buf, 0,  sizeof(athtestcmd_buf));

    system("adb shell");
    system("set property:persist.secret.root");
    sprintf(athtestcmd_buf, "adb push %s %s", pc_file_path, phone_file_path);
    system(athtestcmd_buf);

    return 0;
}


int wifi_init(void)
{
    wifi_driver_handler.init();

    return 0;
}

int ft_wifi_open(void)
{
    wifi_driver_handler.open();

    return 0;
}

int ft_wifi_close(void)
{
    wifi_driver_handler.close();

    return 0;
}

int ft_wifi_scan_ssid(char* response_ssid_list, int list_length)
{
    wifi_driver_handler.scan_ssid(response_ssid_list, list_length);

    return 0;
}

int ft_wifi_tx_set(int mode, int channel, int rate, int power_level, int sine_wave)
{
    wifi_driver_handler.tx_set(mode, channel, rate, power_level, sine_wave);

    return 0;
}

int ft_wifi_tx_start(void)
{
    wifi_driver_handler.tx_start();

    return 0;
}

int ft_wifi_tx_stop(void)
{
    wifi_driver_handler.tx_stop();

    return 0;
}

int ft_wifi_rx_set(int mode, int channel, int rate)
{
    wifi_driver_handler.rx_set(mode, channel, rate);

    return 0;
}

int ft_wifi_rx_report(char* response_packet, int pkt_length)
{
    wifi_driver_handler.rx_report(response_packet, pkt_length);

    return 0;
}

int wifi_get_version(char * response_version, int ver_length)
{
    wifi_driver_handler.get_version(response_version, ver_length);

    return 0;
}

int wifi_set_mac_address(char* mac_addr_buf)
{
    wifi_driver_handler.set_mac_address(mac_addr_buf);

    return 0;
}

int wifi_pull_file(char* phone_file_path, char* pc_file_path, unsigned  char* data, int data_len, int  file_id, char* file_stat)
{
    return wifi_driver_handler.pull_file(phone_file_path, pc_file_path, data, data_len, file_id, file_stat);

}

int wifi_push_file(char* pc_file_path, char* phone_file_path, unsigned  char* data, int data_num, int data_len, char del_flag)
{

    return wifi_driver_handler.push_file(pc_file_path, phone_file_path, data, data_num, data_len, del_flag);
}

int wifi_check_address(char *addr)
{
    int fd = 0;
    int len = 0;

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    system("insmod /system/lib/modules/pronto/pronto_wlan.ko");
    FT_LOG("[%s,%d] /system/lib/modules/pronto/pronto_wlan.ko\n",__func__,__LINE__);

    fd = open(NIC_MAC_ADDRESS, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the %s file\n",__func__,__LINE__,NIC_MAC_ADDRESS);
        return -1;
    }
    else
    {
        read(fd, addr, 20);
        close(fd);
        addr[17] = '\0';
        FT_LOG("[%s:%d] Wlan address is %s \n",__func__,__LINE__, addr);
    }
    system("rmmod wlan");
    FT_LOG("[%s,%d] Rmmod wlan and cfg80211\n",__func__,__LINE__);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return 0;
}

int wifi_cal_update()
{
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    return 0;
}

int wifi_bdata_backup()
{
    return 0;
}

int wifi_bdata_restore()
{
    return 0;
}
