/****************************************************************************
 * factory_test.h
 ***************************************************************************/

/*===========================================================================

  EDIT HISTORY FOR MODULE

  This section contains comments describing changes made to the module.
  Notice that changes are listed in reverse chronological order.

  when      who          what, where, why
  --------  ------       ------------------------------------------------------
  20140519  yuhongxia   add tp disable enable diag   PTAPI_TP_func_ctrl
  20140519  yuhongxia   add sim status diag  SIMAPP_SUBSYS_ID
  ===========================================================================*/


#ifndef _FACTORY_TEST_H_
#define _FACTORY_TEST_H_
#ifdef FEATURE_WINMOB
#include <windows.h>
#endif
#define _REDEF_VAR_XGB
#include <unistd.h>
#include <pthread.h>
#include <event.h>
#include <log.h>
#include "stdio.h"
#include "diag_lsm.h"
#include "diagpkt.h"
#include "diagcmd.h"
#include "diagdiag.h"
#include "diag.h"
#include "cutils/properties.h"
#include <utils/Log.h>

#define F_LOG(...) (void)android_printLog( 4,"FACTORY",__VA_ARGS__)

#define POWER_KEY 0
#define VOLUME_KEY 1
#define HOOK_KEY 2

#ifdef _cplusplus
extern "C"{
#endif
typedef struct
{
    uint8 command_code;
    uint8 subsys_id;
    uint16 subsys_cmd_code;
    uint32 status;
    uint16 delayed_rsp_id;
    uint16 rsp_cnt; /* 0, means one response and 1, means two responses */
}
diagtestapp_subsys_hdr_v2_type;

typedef struct
{
    diagtestapp_subsys_hdr_v2_type hdr;
    char my_rsp[512]; //wsh

}ft_diag_res_pkt;


PACK(void *) PTAPI_SW_VER_get_internal(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI__SW_VER_get_external(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_HW_VER_get(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_HW_VER_set(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type version_test_tbl[] =
{
    {0x00,0x00,PTAPI_SW_VER_get_internal},
    {0x01,0x01,PTAPI__SW_VER_get_external},
    {0x02,0x02,PTAPI_HW_VER_get},
    {0x03,0x03,PTAPI_HW_VER_set}
};

PACK(void *) PTAPI_ADB_enable(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_ADB_disable(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_unlock_screen(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_sceen_wake(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_AWAKE_set_enable(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type adb_test_tbl[] =
{
    {0x00,0x00,PTAPI_ADB_enable},
    {0x01,0x01,PTAPI_ADB_disable},
    {0x02,0x02,PTAPI_unlock_screen},
    {0x03,0x03,PTAPI_AWAKE_set_enable},
    {0x04,0x04,PTAPI_sceen_wake}
};

PACK(void *) PTAPI_RTC_get(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_rtc_tbl[] =
{
    {0x00,0x00,PTAPI_RTC_get}
};

PACK(void *) PTAPI_pv_back(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_battery_tbl[] =
{
    {0x01,0x01,PTAPI_pv_back}
};

PACK(void *) PTAPI_reset_phone(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_rm_cit_flag(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_reset_tbl[] =
{
    {0x00,0x00,PTAPI_reset_phone},
    {0x01,0x01,PTAPI_rm_cit_flag}
};

PACK(void *) PTAPI_WIFI_open_test(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_close_test(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_scan_ssid(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_tx_set(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_tx_start(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_tx_close(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_rx_set(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_rx_report(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_check_address(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_calibration_update(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_bdata_backup(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_bdata_restore(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_WIFI_init_test(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_wlan_tbl[] =
{
    {0x00,0x00,PTAPI_WIFI_open_test},
    {0x01,0x01,PTAPI_WIFI_close_test},
    {0x02,0x02,PTAPI_WIFI_scan_ssid},
    {0x03,0x03,PTAPI_WIFI_tx_set},
    {0x04,0x04,PTAPI_WIFI_tx_start},
    {0x05,0x05,PTAPI_WIFI_tx_close},
    {0x06,0x06,PTAPI_WIFI_rx_set},
    {0x07,0x07,PTAPI_WIFI_rx_report},
    {0x08,0x08,PTAPI_WIFI_check_address},
    {0x09,0x09,PTAPI_WIFI_calibration_update},
    {0x0a,0x0a,PTAPI_WIFI_bdata_backup},
    {0X0b,0x0b,PTAPI_WIFI_bdata_restore},
    {0x0c,0x0c,PTAPI_WIFI_init_test}
};

PACK(void *) PTAPI_BT_get_addr(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_BT_test_init(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_bluetooth_tbl[] =
{
    {0x00,0x00,PTAPI_BT_get_addr},
    {0x01,0x01,PTAPI_BT_test_init}
};

PACK(void *) PTAPI_AUDIO_MIC(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_AUDIO_PINK(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_audio_tbl[] =
{
    {0x00,0x00,PTAPI_AUDIO_MIC},
    {0x01,0x01,PTAPI_AUDIO_PINK}
};


PACK(void *) PTAPI_AUDIOADB_PHONESPEAKER(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_AUDIOADB_HEADSETMIC_HEADSETSPK(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_AUDIOADB_PHONERECEIVER(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_AUDIOADB_PHONEMIC_HEADSETRECEIVER(PACK(void *) req_pkt, unsigned short pkt_len);

static const diagpkt_user_table_entry_type audioadb_test_tbl[] =
{
    {0x00,0x00,PTAPI_AUDIOADB_PHONESPEAKER},
    {0x01,0x01,PTAPI_AUDIOADB_HEADSETMIC_HEADSETSPK},
    {0x02,0x02,PTAPI_AUDIOADB_PHONERECEIVER},
    {0x03,0x03,PTAPI_AUDIOADB_PHONEMIC_HEADSETRECEIVER}
};

PACK(void *) PTAPI_PSENSOR_open_test(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_PLSENSOR_calibration_2cm(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_PLSENSOR_calibration_4cm(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_PSENSOR_close_test(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_read_2cm_test(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_read_4cm_test(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type psensor_test_tbl[] =
{
    {0x00,0x00,PTAPI_PSENSOR_open_test},
    {0x01,0x01,PTAPI_PLSENSOR_calibration_2cm},
    {0x02,0x02,PTAPI_PLSENSOR_calibration_4cm},
    {0x03,0x03,PTAPI_PSENSOR_close_test},
    {0x04,0x04,PTAPI_read_2cm_test},
    {0x05,0x05,PTAPI_read_4cm_test}
};
/*
PACK(void *) PTAPI_CAMERA_start_preview(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_capture_default(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_capture_mtf(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_capture_blemish(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_delete_photo(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_stop_preview(PACK(void *) req_pkt, unsigned short pkt_len);
*/
PACK(void *) PTAPI_CAMERA_lte_start_preview(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_lte_stop_preview(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_lte_capture(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_lte_deletepic(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_lte_wake(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_CAMERA_lte_unlock(PACK(void *) req_pkt, unsigned short pkt_len);

static const diagpkt_user_table_entry_type camera_top_test_tbl[] =
{
    {0x01,0x01,PTAPI_CAMERA_lte_start_preview},
    {0x02,0x02,PTAPI_CAMERA_lte_stop_preview},
    {0x03,0x03,PTAPI_CAMERA_lte_capture},
    {0x04,0x04,PTAPI_CAMERA_lte_deletepic},
    {0x05,0x05,PTAPI_CAMERA_lte_wake},
    {0x06,0x06,PTAPI_CAMERA_lte_unlock}

};

PACK(void *) PTAPI_read_cit_flag(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type cit_flag_test_tbl[] =
{
    {0x00,0x00,PTAPI_read_cit_flag}
};

PACK(void *) PTAPI_EFUSE_get_status(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type efuse_test_tbl[] =
{
    {0x00,0x00,PTAPI_EFUSE_get_status}
};
PACK(void *) PTAPI_start_activity(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_get_result(PACK(void *) req_pkt, unsigned short pkt_len);
PACK(void *) PTAPI_send_broadcast(PACK(void *) req_pkt, unsigned short pkt_len);
static const diagpkt_user_table_entry_type item_test_tbl[] =
{
    {0x00,0x00,PTAPI_start_activity},
    {0x01,0x01,PTAPI_get_result},
    {0x02,0x02,PTAPI_send_broadcast}
};
#ifdef _cplusplus
}
#endif

#define LAUNCH_SUBSYS_ID 0x00

#define VERSION_SUBSYS_ID 0x01

#define FRONT_CAMERA_USBSYS_ID 0x02

#define CAMERA_SUBSYS_ID 0x03

#define SIMCARD_SUBSYS_ID  0x04

#define GSENSOR_SUBSYS_ID 0x05

#define PSENSOR_SUBSYS_ID 0x06

#define LSENSOR_SUBSYS_ID 0x07

#define TP_SUBSYS_ID 0x08

#define KEYPAD_SUBSYS_ID 0x09

#define DISPLAY_SUBSYS_ID 0x0a

#define VIBRATOR_SUBSYS_ID 0x0b

#define HEADSET_SUBSYS_ID 0x0c

#define SDCARD_SUBSYS_ID 0x0d

#define THREE_COLOR_SUBSYS_ID 0x0e

#define BACKLIGHT_SUBSYS_ID 0x0f

#define WIFI_SUBSYS_ID 0x10

#define ECOMPASS_SUBSYS_ID 0x11

#define GYROSCOPE_SUBSYS_ID 0x12

#define SIGNAL_SUBSYS_ID 0x13

#define BT_SUBSYS_ID 0x14

#define FLASH_SUBSYS_ID 0x15

#define CHARGER_SUBSYS_ID 0x16

#define GPS_SUBSYS_ID 0x17

#define AUDIO_SUBSYS_ID 0x18

#define RTC_SUBSYS_ID 0x19

#define ADB_SUBSYS_ID 0x2a

#define FTM_VERSION_SUBSYS_ID 0x2b

#define RESET_FACTORY_SUBSYS_ID 0x2c

#define BATTERY_SUBSYS_ID 0x2d

#define BLUETOOTH_SUBSYS_ID 0x2e

#define WLAN_SUBSYS_ID 0x2f

#define TOP_PSENSOR_SUBSYS_ID 0x30

#define TOP_CAMERA_SUBSYS_ID 0x31
#define CIT_FLAG_SUBSYS_ID 0x32
#define EFUSE_SUBSYS_ID 0x33
#endif


