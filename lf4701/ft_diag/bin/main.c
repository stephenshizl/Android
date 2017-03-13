/****************************************************************************
 * main.c:
 *     Diag test function entrance.
 ***************************************************************************/

/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who          what, where, why
--------  -----------  ----------------------------------------------------
20140519  yuhongxia add sim status diag  simapp_test_tbl
20140520  yuhongxia add audioadb  diag  audioadb_test_tbl
===========================================================================*/

#ifdef FEATURE_WINMOB
#include <windows.h>
#endif

//#include "factory_test.h"
#include "factory_tes.h"
#include "cutils/properties.h"

/* Main Function. This initializes Diag_LSM, calls the tested APIs and exits. */
int main(void)
{
  boolean bInit_Success = FALSE;
  int i=0;
  char prop_val[1];
 // system("logcat -r 20000 -f /data/MTBF/current_logcat.log &");
 // system("ft_diag");

/*if ( property_get( "persist.service.ftdiag.enable", prop_val, "" ) > 0 )
{
 printf(" persist.service.ftdiag.enable\n");
 return -1;
}*/
  printf(" calling LSM init \n");
  bInit_Success = Diag_LSM_Init(NULL);
  if(!bInit_Success)
  {
    printf("TestApp_MultiThread: Diag_LSM_Init() failed.\n");
    return -1;
  }
  
  //DIAGPKT_DISPATCH_TABLE_REGISTER_V2_DELAY(DIAG_SUBSYS_CMD_VER_2_F, BT_SUBSYS_ID, bt_test_tbl);
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, LAUNCH_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, VERSION_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, FRONT_CAMERA_USBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, CAMERA_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, DISPLAY_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, VIBRATOR_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, BACKLIGHT_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, HEADSET_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, SDCARD_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, KEYPAD_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, LSENSOR_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, PSENSOR_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, GSENSOR_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, WIFI_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, ECOMPASS_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, GYROSCOPE_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, THREE_COLOR_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, SIGNAL_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, FLASH_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, TP_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, CHARGER_SUBSYS_ID, item_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, BT_SUBSYS_ID, item_test_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, GPS_SUBSYS_ID, item_test_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, RTC_SUBSYS_ID, item_rtc_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, ADB_SUBSYS_ID, adb_test_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, AUDIO_SUBSYS_ID, item_audio_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, FTM_VERSION_SUBSYS_ID, version_test_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, RESET_FACTORY_SUBSYS_ID, item_reset_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, BATTERY_SUBSYS_ID, item_battery_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, BLUETOOTH_SUBSYS_ID, item_bluetooth_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, WLAN_SUBSYS_ID, item_wlan_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, TOP_PSENSOR_SUBSYS_ID, psensor_test_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, TOP_CAMERA_SUBSYS_ID, camera_top_test_tbl);

  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, CIT_FLAG_SUBSYS_ID, cit_flag_test_tbl);
  
  DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, EFUSE_SUBSYS_ID, efuse_test_tbl);
  printf("DIAGPKT_DISPATCH_TABLE_REGISTER_V2  All end\n");
  do
  {
    char event_payload[40] = "TestApp_MultiThread Event with Payload1";
    event_report_payload (EVENT_DIAG_STRESS_TEST_WITH_PAYLOAD,sizeof(event_payload), event_payload);
    char event_payload2[1] = "T";
    event_report_payload (EVENT_DIAG_STRESS_TEST_WITH_PAYLOAD,sizeof(event_payload2), event_payload2);
    sleep(0xfff);
  } while (1);

  /* Now find the DeInit function and call it. */
  Diag_LSM_DeInit();

  return 0;
}
//=============================================================================



