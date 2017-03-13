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
#include "factory_test.h"
//#include "factory_tes.h"
#include "cutils/properties.h"
/* Main Function. This initializes Diag_LSM, calls the tested APIs and exits. */
int main(void)
{
    boolean bInit_Success = FALSE;
    int i = 0;
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
    if (!bInit_Success)
    {
        printf("TestApp_MultiThread: Diag_LSM_Init() failed.\n");
        return  - 1;
    }
    //DIAGPKT_DISPATCH_TABLE_REGISTER_V2_DELAY(DIAG_SUBSYS_CMD_VER_2_F, BT_SUBSYS_ID, bt_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, VERSION_SUBSYS_ID, version_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, ADB_USBSYS_ID, adb_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, CAMERA_SUBSYS_ID, camera_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, DISPLAY_SUBSYS_ID, display_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, VIBRATOR_SUBSYS_ID, vibrator_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, BACKLIGHT_SUBSYS_ID, backlight_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, HEADSET_SUBSYS_ID, headset_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, SDCARD_SUBSYS_ID, sdcard_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, KEYPAD_SUBSYS_ID, keypad_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, LSENSOR_SUBSYS_ID, lsensor_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, PSENSOR_SUBSYS_ID, psensor_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, GSENSOR_SUBSYS_ID, gsensor_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, WIFI_SUBSYS_ID, wifi_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, DATA_SUBSYS_ID, data_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, SENSORID_SUBSYS_ID, sensorid_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, AWAKE_SUBSYS_ID, awake_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, FM_SUBSYS_ID, fm_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, BT_SUBSYS_ID, bt_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, FLASH_SUBSYS_ID, flash_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, TP_SUBSYS_ID, tp_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, CHARGER_SUBSYS_ID, charger_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, SIMAPP_SUBSYS_ID, simapp_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, AUDIOADB_SUBSYS_ID, audio_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, EFUSE_SUBSYS_ID, efuse_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, POWER_OFF_ID, power_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, HALL_STATE_ID, hall_test_tb1);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, RUNIN_SUBSYS_ID, runin_test_tb1);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, MSENSOR_SUBSYS_ID, msensor_test_tb1);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, HWINFO_SUBSYS_ID, hwinfo_test_tb1);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, LED_SUBSYS_ID, led_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, KEYBOX_SUBSYS_ID, keybox_test_tb1);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, CIT_SUBSYS_ID, cit_result_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, SPKCALIBRATION_SUBSYS_ID, spk_calibration_test_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, RTC_SUBSYS_ID, item_rtc_tbl);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, ENFORCE_SUBSYS_ID, setenforce_test_tb1);
    DIAGPKT_DISPATCH_TABLE_REGISTER_V2(DIAG_SUBSYS_CMD_VER_2_F, EMMCUPGRADE_SUBSYS_ID, emmcupgrade_test_tb1);

    printf("DIAGPKT_DISPATCH_TABLE_REGISTER_V2  All end\n");
    //ft_plsensor_calibration();
    //system("/system/xbin/set_spkcalibration");
    do
    {
        char event_payload[40] = "TestApp_MultiThread Event with Payload1";
        event_report_payload(EVENT_DIAG_STRESS_TEST_WITH_PAYLOAD, sizeof(event_payload), event_payload);
        char event_payload2[1] = "T";
        event_report_payload(EVENT_DIAG_STRESS_TEST_WITH_PAYLOAD, sizeof(event_payload2), event_payload2);
        sleep(0xfff);
    }

    while (1);
    /* Now find the DeInit function and call it. */
    Diag_LSM_DeInit();
    return 0;
}

//=============================================================================
