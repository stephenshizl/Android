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

    //ft function declare
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
       } diagtestapp_subsys_hdr_v2_type;

        typedef struct
        {
            diagtestapp_subsys_hdr_v2_type hdr;
            char my_rsp[512];
        } ft_diag_res_pkt;

        //PACK(void *) (PACK(void *) req_pkt, unsigned short pkt_len);

        PACK(void*)PTAPI_SW_VER_get_internal(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI__SW_VER_get_external(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_HW_VER_get(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_HW_VER_set(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_TYPE_DESIGNATOR_get(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_VARIANT_CODE_get(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_VARIANT_VER_get(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type version_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_SW_VER_get_internal},
            {0x01, 0x01, PTAPI__SW_VER_get_external},
            {0x02, 0x02, PTAPI_HW_VER_get},
            {0x03, 0x03, PTAPI_HW_VER_set},
            {0x04, 0x04, PTAPI_TYPE_DESIGNATOR_get},
            {0x05, 0x05, PTAPI_VARIANT_CODE_get},
            {0x06, 0x06, PTAPI_VARIANT_VER_get}
        };

        PACK(void*)PTAPI_ADB_enable(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_ADB_disable(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type adb_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_ADB_enable},
            {0x01, 0x01, PTAPI_ADB_disable}
        };

        PACK(void*)PTAPI_CAMERA_start_preview(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_capture_default(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_capture_mtf(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_capture_blemish(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_delete_photo(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_stop_preview(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_lte_start_preview(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_lte_stop_preview(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_lte_capture(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_lte_deletepic(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_lte_wake(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CAMERA_lte_unlock(PACK(void*)req_pkt, unsigned short pkt_len);

        static const diagpkt_user_table_entry_type camera_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_CAMERA_start_preview},
            {0x01, 0x01, PTAPI_CAMERA_capture_default},
            {0x02, 0x02, PTAPI_CAMERA_capture_mtf},
            {0x03, 0x03, PTAPI_CAMERA_capture_blemish},
            {0x04, 0x04, PTAPI_CAMERA_delete_photo},
            {0x05, 0x05, PTAPI_CAMERA_stop_preview},
            {0x06, 0x06, PTAPI_CAMERA_lte_start_preview},
            {0x07, 0x07, PTAPI_CAMERA_lte_stop_preview},
            {0x08, 0x08, PTAPI_CAMERA_lte_capture},
            {0x09, 0x09, PTAPI_CAMERA_lte_deletepic},
            {0x0a, 0x0a, PTAPI_CAMERA_lte_wake},
            {0x0b, 0x0b, PTAPI_CAMERA_lte_unlock}
        };

        PACK(void*)PTAPI_DISPLAY_color_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_DISPLAY_color_test_lte(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type display_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_DISPLAY_color_test},
            {0x01, 0x01, PTAPI_DISPLAY_color_test_lte},
        };

        PACK(void*)PTAPI_VIBRATOR_test_start(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_VIBRATOR_test_stop(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type vibrator_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_VIBRATOR_test_start},
            {0x01, 0x01, PTAPI_VIBRATOR_test_stop}
        };

        PACK(void*)PTAPI_BACKLIGHT_level_test(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type backlight_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_BACKLIGHT_level_test}
        };

        PACK(void*)PTAPI_LED_level_test(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type led_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_LED_level_test}
        };

        PACK(void*)PTAPI_HEADSET_status_test(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type headset_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_HEADSET_status_test}
        };

        PACK(void*)PTAPI_SDCARD_get_info(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_SDCARD_io_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_INTERNAL_SDCARD_get_info(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type sdcard_test_tbl[] =
        {
            { 0x00, 0x00, PTAPI_SDCARD_get_info},
            { 0x01, 0x01, PTAPI_SDCARD_io_test},
            { 0x02, 0x02, PTAPI_INTERNAL_SDCARD_get_info}
        };

        PACK(void*)PTAPI_KEYPAD_power_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_KEYPAD_volume_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_KEYPAD_hook_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_KEYPAD_back_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_KEYPAD_back_test_getvalue(PACK(void*)req_pkt, unsigned short pkt_len);

        static const diagpkt_user_table_entry_type keypad_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_KEYPAD_power_test},
            {0x01, 0x01, PTAPI_KEYPAD_volume_test},
            {0x02, 0x02, PTAPI_KEYPAD_hook_test},
            {0x03, 0x03, PTAPI_KEYPAD_back_test}
        };

        PACK(void*)PTAPI_LSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_LSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_LSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_LSENSOR_adb_test(PACK(void*)req_pkt, unsigned short pkt_len);

        static const diagpkt_user_table_entry_type lsensor_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_LSENSOR_open_test},
            {0x01, 0x01, PTAPI_LSENSOR_get_value},
            {0x02, 0x02, PTAPI_LSENSOR_close_test},
            {0x03, 0x03, PTAPI_LSENSOR_adb_test}
        };

        PACK(void*)PTAPI_PSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PSENSOR_adb_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration_auto(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration_2cm(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration_4cm(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration_delete(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration_read2cm(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_PLSENSOR_calibration_read4cm(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type psensor_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_PSENSOR_open_test},
            {0x01, 0x01, PTAPI_PSENSOR_get_value},
            {0x02, 0x02, PTAPI_PSENSOR_close_test},
            {0x03, 0x03, PTAPI_PLSENSOR_calibration},
            {0x04, 0x04, PTAPI_PSENSOR_adb_test},
            {0x05, 0x05, PTAPI_PLSENSOR_calibration_auto},
            {0x06, 0x06, PTAPI_PLSENSOR_calibration_2cm},
            {0x07, 0x07, PTAPI_PLSENSOR_calibration_4cm},
            {0x08, 0x08, PTAPI_PLSENSOR_calibration_delete},
            {0x0a, 0x0a, PTAPI_PLSENSOR_calibration_read2cm},
            {0x0b, 0x0b, PTAPI_PLSENSOR_calibration_read4cm}
        };

        PACK(void*)PTAPI_GSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_GSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_GSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_GSENSOR_adb_test(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type gsensor_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_GSENSOR_open_test},
            {0x01, 0x01, PTAPI_GSENSOR_get_value},
            {0x02, 0x02, PTAPI_GSENSOR_close_test},
            {0x03, 0x03, PTAPI_GSENSOR_adb_test}
        };

        PACK(void*)PTAPI_WIFI_open_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_close_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_scan_ssid(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_tx_set(PACK(void*)req_pkt, unsigned short pkt_len) ;
        PACK(void*)PTAPI_WIFI_tx_start(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_tx_close(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_rx_set(PACK(void*)req_pkt, unsigned short pkt_len) ;
        PACK(void*)PTAPI_WIFI_rx_report(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_check_address(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_calibration_update(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_bdata_backup(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_WIFI_bdata_restore(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_WIFI_test_init(PACK(void *) req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type wifi_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_WIFI_open_test},
            {0x01, 0x01, PTAPI_WIFI_close_test},
            {0x02, 0x02, PTAPI_WIFI_scan_ssid},
            {0x03, 0x03, PTAPI_WIFI_tx_set},
            {0x04, 0x04, PTAPI_WIFI_tx_start},
            {0x05, 0x05, PTAPI_WIFI_tx_close},
            {0x06, 0x06, PTAPI_WIFI_rx_set},
            {0x07, 0x07, PTAPI_WIFI_rx_report},
            {0x08, 0x08, PTAPI_WIFI_check_address},
            {0x09, 0x09, PTAPI_WIFI_calibration_update},
            {0x0a, 0x0a, PTAPI_WIFI_bdata_backup},
            {0x0b, 0x0b, PTAPI_WIFI_bdata_restore},
            {0x0c, 0x0c, PTAPI_WIFI_test_init}
        };

        PACK(void*)PTAPI_DATA_restore_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_DATA_lastlog_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_DATA_dataflag_test(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type data_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_DATA_restore_test},
            {0x01, 0x01, PTAPI_DATA_lastlog_test},
            {0x02, 0x02, PTAPI_DATA_dataflag_test}
        };

        PACK(void*)PTAPI_SENSOR_ID_get(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type sensorid_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_SENSOR_ID_get}
        };

        PACK(void*)PTAPI_AWAKE_set_enable(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_AWAKE_set_disable(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_AWAKE_set_sleep(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type awake_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_AWAKE_set_enable},
            {0x01, 0x01, PTAPI_AWAKE_set_disable},
            {0x02, 0x02, PTAPI_AWAKE_set_sleep}
        };

        PACK(void*)PTAPI_FM_set_enable(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_FM_stereo_enable(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_FM_stereo_disable(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_FM_set_volume(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_FM_set_channel(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTPAI_FM_set_disable(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type fm_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_FM_set_enable},
            {0x01, 0x01, PTAPI_FM_stereo_enable},
            {0x02, 0x02, PTAPI_FM_stereo_disable},
            {0x03, 0x03, PTAPI_FM_set_volume},
            {0x04, 0x04, PTAPI_FM_set_channel},
            {0x05, 0x05, PTPAI_FM_set_disable}
        };

        PACK(void*)PTAPI_BT_get_addr(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_BT_test_init(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type bt_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_BT_get_addr},
            {0x01, 0x01, PTAPI_BT_test_init}
        };

        PACK(void*)PTAPI_FLASH_FLASH(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_FLASH_TORCH(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_FLASH_OFF(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type flash_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_FLASH_FLASH},
            {0x01, 0x01, PTAPI_FLASH_TORCH},
            {0x02, 0x02, PTAPI_FLASH_OFF}
        };

        PACK(void*)PTAPI_TP_get_ref(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_TP_get_diagapptp(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_TP_func_ctrl(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type tp_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_TP_get_ref},
            {0x01, 0x01, PTAPI_TP_get_diagapptp},
            {0x02, 0x02, PTAPI_TP_func_ctrl}
        };

        PACK(void*)PTAPI_CHARGER_get_cap(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CHARGER_get_vol(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CHARGER_get_current(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CHARGER_func_ctrl(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_CHARGER_get_temp(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type charger_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_CHARGER_get_cap},
            {0x01, 0x01, PTAPI_CHARGER_get_vol},
            {0x02, 0x02, PTAPI_CHARGER_get_current},
            {0x03, 0x03, PTAPI_CHARGER_func_ctrl},
            {0x04, 0x04, PTAPI_CHARGER_get_temp}
        };

        PACK(void*)PTAPI_SIMAPP_get_status(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type simapp_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_SIMAPP_get_status}
        };

        PACK(void *) PTAPI_AUDIO_speaker_test(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_headsetMic_to_headsetRecv(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_receiver_test(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_phoneMic_to_headsetRecv(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_mainMic_to_speaker(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_secondMic_to_speaker(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_clean_status(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_breakoff_audio_access(PACK(void *) req_pkt, unsigned short pkt_len);

        PACK(void *) PTAPI_AUDIO_Mic_to_Speaker(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_Mic_to_Receiver(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_mic1_to_headset(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_AUDIO_mic2_to_headset(PACK(void *) req_pkt, unsigned short pkt_len);

        static const diagpkt_user_table_entry_type audio_test_tbl[] =
        {
            {0x00,0x00,PTAPI_AUDIO_speaker_test},
            {0x01,0x01,PTAPI_AUDIO_headsetMic_to_headsetRecv},
            {0x02,0x02,PTAPI_AUDIO_receiver_test},
            {0x03,0x03,PTAPI_AUDIO_phoneMic_to_headsetRecv},
            {0x04,0x04,PTAPI_AUDIO_mainMic_to_speaker},
            {0x05,0x05,PTAPI_AUDIO_secondMic_to_speaker},
            {0x06,0x06,PTAPI_AUDIO_clean_status},
            {0x07,0x07,PTAPI_AUDIO_breakoff_audio_access},
            {0x08,0x08,PTAPI_AUDIO_Mic_to_Speaker},
            {0x09,0x09,PTAPI_AUDIO_Mic_to_Receiver},
            {0x10,0x10,PTAPI_AUDIO_mic1_to_headset},
            {0x11,0x11,PTAPI_AUDIO_mic2_to_headset}
        };
        PACK(void*)PTAPI_EFUSE_get_status(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type efuse_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_EFUSE_get_status}
        };

        PACK(void*)PTAPI_POWER_off(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type power_test_tbl[] =
        {
            {0x00, 0x00, PTAPI_POWER_off}
        };

        PACK(void*)PTAPI_Hall_get_state(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type hall_test_tb1[] =
        {
            {0x00, 0x00, PTAPI_Hall_get_state}
        };

        PACK(void*)PTAPI_RUNIN_get_data(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_RUNIN_get_failed_item(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_RUNIN_get_result(PACK(void *) req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type runin_test_tb1[] =
        {
            {0x00, 0x00, PTAPI_RUNIN_get_data},
            {0x01, 0x01, PTAPI_RUNIN_get_failed_item},
            {0x02, 0x02, PTAPI_RUNIN_get_result}
        };

        PACK(void*)PTAPI_MSENSOR_get_data(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_MSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_MSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_MSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type msensor_test_tb1[] =
        {
            {0x00, 0x00, PTAPI_MSENSOR_get_data},
            {0x01, 0x01, PTAPI_MSENSOR_open_test},
            {0x02, 0x02, PTAPI_MSENSOR_get_value},
            {0x03, 0x03, PTAPI_MSENSOR_close_test}
        };
        //add for set enforce 0/1
        PACK(void*)PTAPI_setenforce_off(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_setenforce_on(PACK(void *) req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type setenforce_test_tb1[] =
        {
            {0x00, 0x00, PTAPI_setenforce_off},
            {0x01, 0x01, PTAPI_setenforce_on},
        };
        //end
        //add for set emmc upgrade
        PACK(void*)PTAPI_emmc_upgrade(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type emmcupgrade_test_tb1[] =
        {
            {0x00, 0x00, PTAPI_emmc_upgrade},
        };
        //end
        PACK(void*)PTAPI_HWINFO_get_meminfo(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_HWINFO_get_cpuinfo(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type hwinfo_test_tb1[] =
        {
            {0x00, 0x00, PTAPI_HWINFO_get_meminfo},
            {0x01, 0x01, PTAPI_HWINFO_get_cpuinfo}
        };
        PACK(void *) PTAPI_KEYBOX_write_data(PACK(void *) req_pkt, unsigned short pkt_len);
        PACK(void *) PTAPI_KEYBOX_check_data(PACK(void *) req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type keybox_test_tb1[] =
        {
            {0x00,0x00,PTAPI_KEYBOX_write_data},
            {0x01,0x01,PTAPI_KEYBOX_check_data}
        };
        PACK(void*)PTAPI_read_cit_flag(PACK(void*)req_pkt, unsigned short pkt_len);
        PACK(void*)PTAPI_clear_cit_flag(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type cit_result_test_tbl[] =
        {
            {0x00,0x00,PTAPI_read_cit_flag},
            {0x01,0x01,PTAPI_clear_cit_flag},
        };
        PACK(void *) PTAPI_RTC_get(PACK(void *) req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type item_rtc_tbl[] =
        {
            {0x00,0x00,PTAPI_RTC_get}
        };
        PACK(void*)PTAPI_spk_calibration(PACK(void*)req_pkt, unsigned short pkt_len);
        static const diagpkt_user_table_entry_type spk_calibration_test_tbl[] =
        {
            {0x00,0x00,PTAPI_spk_calibration},
        };
    #ifdef _cplusplus
        }
    #endif


    #define VERSION_SUBSYS_ID 0x01

    #define ADB_USBSYS_ID 0x02

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

    #define AWAKE_SUBSYS_ID 0x0e

    #define BACKLIGHT_SUBSYS_ID 0x0f

    #define WIFI_SUBSYS_ID 0x10

    #define DATA_SUBSYS_ID 0x11

    #define SENSORID_SUBSYS_ID 0x12

    #define FM_SUBSYS_ID 0x13

    #define BT_SUBSYS_ID 0x14

    #define FLASH_SUBSYS_ID 0x15

    #define CHARGER_SUBSYS_ID 0x16

    #define SIMAPP_SUBSYS_ID 0x17

    #define AUDIOADB_SUBSYS_ID 0x18

    #define EFUSE_SUBSYS_ID 0x19

    #define RUNIN_SUBSYS_ID 0x1a

    #define KEYBOX_SUBSYS_ID 0x1b

    #define MSENSOR_SUBSYS_ID 0x1c

    #define HALL_STATE_ID 0x1d

    #define HWINFO_SUBSYS_ID 0x1e

    #define POWER_OFF_ID 0x1f

    #define LED_SUBSYS_ID 0x20
    #define CIT_SUBSYS_ID 0x21
    #define SPKCALIBRATION_SUBSYS_ID 0x22
    #define RTC_SUBSYS_ID 0x23
    #define ENFORCE_SUBSYS_ID 0x24
    #define EMMCUPGRADE_SUBSYS_ID 0x25
#endif
