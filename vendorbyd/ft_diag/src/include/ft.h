#include "ft_sys.h"
#include "fm.h"
/*-----------------------------------------------------------------
 * Version test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get sw ap internal version
 *
 * Input parameters:
 * out param:
 *      response_data: pointer to return AP internal version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_swVer_get__InternalVer(char *version);
/********************************************
 * Description:
 * Get sw AP  external version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return AP external version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_swVer_get__ExternalVer(char *version);
/********************************************
 * Description:
 * Get hardware version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return hardware version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_hwVer_get__Ver(char *version);
/********************************************
 * Description:
 * Set hardware version
 *
 * Input parameters:
 * in param:
 *      version: Version number of hardware
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_hwVer_set__Ver(char *version);
/********************************************
 * Description:
 * Get type designator
 *
 * Input parameters:
 * out param:
 *      designator: pointer to return type designator
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_type_designator_get(char *designator);
/********************************************
 * Description:
 * Get variant code
 *
 * Input parameters:
 * out param:
 *      code: pointer to return variant code
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_variant_code_get(char *code);
/********************************************
 * Description:
 * Get variant version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return variant version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_variant_ver_get(char *version);
/********************************************
 * Description:
 * Get LCD Serial Number
 *
 * Input parameters:
 * out param:
 *      num:pointer to return LCD serial number
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
//int ft_LCD_get__Num(char *num);
/********************************************
 * Description:
 * Get LCD Serial Number
 *
 * Input parameters:
 * out param:
 *      id:pointer to return gsensor,lsensor Etc. id
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_PLsensor_get__id(char *id);
/********************************************
 * Description:
 * Get LCD Serial Number
 *
 * Input parameters:
 * out param:
 *      addr:pointer to return BT mac address
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int bt_get_addr_hci(char *addr);
/*-----------------------------------------------------------------
 * ADB test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get LCD Serial Number
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_adb_set_enable();
/********************************************
 * Description:
 * Get LCD Serial Number
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_adb_set_disable();
/*-----------------------------------------------------------------
 * Camera test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Start camera preview
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_camera_start_preview(int type);
/********************************************
 * Description:
 * Take photo by default parameter
 *    Save path: data/default.jpg
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_camera_default_capture();
/********************************************
 * Description:
 * Take photo by MTF parameter
 *    Save path: data/MTP.jpg
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_camera_MTF_capture();
/********************************************
 * Description:
 * Take photo by Blemish parameter
 *    Save path: data/Blemish.jpg
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_camera_Blemish_capture();
/********************************************
 * Description:
 * Delete the photo
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_camera_del_photo();
/********************************************
 * Description:
 * Stop camera preview
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_camera_stop_preview();
/*-----------------------------------------------------------------
 * SIM test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Sim card detect test
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_sim_detect_test();
/********************************************
 * Description:
 * Sim card read/write test
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_sim_IO_test();
/*-----------------------------------------------------------------
 * Proximity Sensor test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Open Psensor
 *
 * Onput parameters:
 *
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_psensor_open();
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
int ft_psensor_get_value(int *value);
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
    int ft_psensor_test_cal(char *sensor, int type);
#endif
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
int ft_psensor_close();
/*-----------------------------------------------------------------
 * TouchScreen test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Proximity Sensor test
 *
 * Onput parameters:
 * out param:
 *      reference:pointer to return TouchScreen reference value
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_touchscreen_get_ref(char *reference);
// yuhongxia add tp disable enable diag 20140519 begin
/********************************************
 * Description:
 * Set TouchPanel function enable/disable
 *
 * Input parameters:
 * in param:
 *      mode: 0 set TP disable, 1 set TP enable
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_touchscreen_func_control(int mode);
// yuhongxia add tp disable enable diag 20140519 end
/*-----------------------------------------------------------------
 * Gravity Sensor Test
-----------------------------------------------------------------*/
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
int ft_gsensor_open();
/********************************************
 * Description:
 * Gsensor test
 *
 * Onput parameters:
 * out param:
 *      x: x value
 *      y: y value
 *      z: z value
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_gsensor_get_value(float *x, float *y, float *z);
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
int ft_gsensor_close();
/*-----------------------------------------------------------------
 * Key Press Test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Check if the power key is pressed
 *
 * Input parameters:
 * out param:
 *      key: the power key value
 *
 * Return value:
 * 0: Power key is pressed
 * -1: failed or other key is pressed
 ********************************************/
int ft_key_test_Power(int *key);
/********************************************
 * Description:
 * Check if the volume key is pressed
 *
 * Input parameters:
 * out param:
 *      key: the volume key value,it can be volume up key or volume down key
 *
 * Return value:
 * 0: Volume up is pressed
 * -1: failed or other key is pressed
 ********************************************/
int ft_key_test_Volume(int *key);
/********************************************
 * Description:
 * Check if the hook key is pressed
 *
 * Output parameters:
 * out param:
 *      key:the hook key value
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_key_test_Hook(int *key);
/********************************************
 * Description:
 * Check if the back key is pressed
 *
 * Output parameters:
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_key_test_Back();
/*-----------------------------------------------------------------
 * Light Sensor Test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Open Lsensor
 *
 * Onput parameters:
 *
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_lsensor_open();
/********************************************
 * Description:
 * Light Sensor test
 *
 * Onput parameters:
 * out param:
 *      value:pointer to return light sensor value
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_lsensor_get_value(int *value);
/********************************************
 * Description:
 * Close Lsensor
 *
 * Onput parameters:
 *
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_lsensor_close();
/*-----------------------------------------------------------------
 * Lcd Test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Proximity Sensor test
 *
 * Input parameters:
 * in param:
 *      color:type of color value
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_display_test(int color);
/*-----------------------------------------------------------------
 * Vibrator Test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Proximity Sensor test
 *
 * Input parameters:
 * in param:
 *      level:vibrate level
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_vibrator_set_level(int level);
int ft_vibrator_start();
int ft_vibrator_stop();
/*-----------------------------------------------------------------
 * Headset Test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Check if the headset is plugged
 *
 * Output parameters:
 * out param:
 *      status: 1: plugged
 *                 0: not plugged
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_headset_status(int *status);
/********************************************
 * Description:
 * Start listen headset status from /dev/input/eventX
 *
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_headset_start_test();
/*-----------------------------------------------------------------
 * SD card test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get SD card information
 *
 * Input parameters:
 * out param:
 *      isInsert: if the SD card is insert, 1: insert, 0: not insert
 *      totalSpace: total space(MB) of SD card, 0 if no SD card
 *      freeSpace: free space(MB) of SD card, 0 if no SD card
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_sdcard_get_info(int *isInsert, int *totalSpace, int *freeSpace);
/********************************************
 * Description:
 * Test SD Card IO function
 *
 * Input parameters:
 * out param:
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_sdcard_IO_test();
/********************************************
 * Description:
 * Always set the device screen and not lock screen
 *
 * Input parameters:
 * out param:
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_awake_test_enable();
/********************************************
 * Description:
 * Set the device normal
 *
 * Input parameters:
 * out param:
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_awake_test_disable();
/********************************************
 * Description:
 * Set playback volume
 *
 * Input parameters:
 * in param:
 *       level: the backlight level
 *              from 0 to 255
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_backlight_setLevel(int level);
int ft_wifi_open();
int ft_wifi_close();
int ft_wifi_scan_ssid(char *response_ssid_list, int list_length);
int ft_wifi_tx_set(int mode, int channel, int rate, int power_level, int sine_wave);
int ft_wifi_tx_start();
int ft_wifi_tx_stop();
int ft_wifi_rx_set(int mode, int channel, int rate);
int ft_wifi_rx_report(char *response_packet, int pkt_length);
int wifi_check_address(char *addr);
int wifi_cal_update();
//int ft_clear_flash();
int ft_clear_flash(char *dir);
int ft_clear_facRestore();
int ft_bt_test_init();
int wifi_bdata_backup();
int wifi_bdata_restore();
int ft_flash_test_flash();
int ft_flash_test_torch();
int ft_flash_test_off();
int ft_battery_read_cap(int *cap);
/********************************************
 * Description:
 * Delete /cache/recovery/last_log
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_lastlog_delete();
/********************************************
 * Description:
 * Check if there is last_log files after factory reset
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_lastlog_detect();
/********************************************
 * Description:
 * Create a file in data partition for data flag
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_dataflag_creat();
/********************************************
 * Description:
 * Check if the data partition flag exist
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_dataflag_detect();
/********************************************
 * Description:
 * Test power off
 *
 * Input parameters:
 * out param:
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_power_off_test();
/********************************************
 * Description:
 * Get the battery's voltage
 *
 * Input parameters:
 * out param:
 *     vol: return the battery's voltage
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_voltage(float *vol);
/*add by wu.xiaobo@byd.com for battery current and temperature at 20141125*/
/********************************************
 * Description:
 * Get the battery's current
 *
 * Input parameters:
 * out param:
 *     current: return the battery's current
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_current(long *current);
/********************************************
 * Description:
 * control the charger function
 *
 * Input parameters:
 *     val: 0: discharging, 1: charging
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_charger_control(int val);
/********************************************
 * Description:
 * Get the battery's temperature.
 *
 * Input parameters:
 * out param:
 *     temp: return the battery's temperature
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_battery_read_temp(float *temp);
/*end wuxiaobo*/
/*-----------------------------------------------------------------
 * hall test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Get the hall state
 *
 * Input parameters:
 * out param:
 *     hall: return hall state
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_hall_state_read(char *state);
/*-----------------------------------------------------------------
 * runin test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * get runin test data
 *
 * Input parameters:
 * out param:
 *     data : return runin test info
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_runin_get_data(int item, char *dret);
/********************************************
 * Description:
 * get runin test data
 *
 * Input parameters:
 * out param:
 *     data : return runin failed item
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_runin_get_failed_item(int item, char *finfo);
//M-sensor test
/********************************************
 * Description:
 * Get msensor data
 *
 * Input parameters:
 * out param:
 *      data:pointer to return gsensor,lsensor Etc. id
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_get_msensor_data(char *addr);
//audio test
/********************************************
 * Description:
 * After test finish init the audio
 *
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_audio_finish();
//internal sdcard test
/********************************************
 * Description:
 * Get internal SD card size
 *
 * Input parameters:
 * out param:
 *      totalSpace: total space(GB) of Internal SD card
 *
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_internal_sdcard_get_info(double *totalSpace);
//hartware info test
/********************************************
 * Description:
 * Get internal SD card information
 *
 * Input parameters:
 * out param:
 *      response_data: total space(MB) of internal SD card
 *
 * Return value:
 * 1: success
 * -1: failed
 ********************************************/
int hw_get_meminfo(char *response_data);
/********************************************
 * Description:
 * Get CPU model name
 *
 * Input parameters:
 * out param:
 *      response_data: CPU model name
 *
 * Return value:
 * 1: success
 * -1: failed
 ********************************************/
int hw_get_cpuinfo(char *response_data);
/********************************************
* Description:
*  Write the keybox data to device
*
* Output parameters:
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_keybox_write_data();

/********************************************
* Description:
*  Check keybox write success or not.
*  After write,a folder will be create in persist folder.
*  So, we check if there have the "data" folder.
*  If data exist,we think write keybox success.
*
* Output parameters:
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_keybox_check_data();
int ft_rtc_get(char *value);
int ft_runin_get_result(char *value);
