#include <utils/Log.h>
#include "public.h"
#define FT_LOG(...)  (void)android_printLog( 4,"FT_TST",__VA_ARGS__)
//hall node
//#define HALL_STATE "/sys/devices/hall_sensor.76/input/input1/state"
#define HALL_STATE "/sys/class/input/input5/state"
#define HALL_ACTIVE "hall_state: active\n"
//runin flag
#define RUNIN_LOG_PATH "/data/runin/data"
//hardware info
#define MEMORY_INFO "/proc/meminfo"
#define CUP_INFO "/proc/cpuinfo"
#define INTERNAL_SDCARD_PATH "/storage/sdcard0"
#define HW_VERSION_PATH "/sys/board_properties/hw_version"
#define SW_VERSION_SRC_FILE "/system/etc/system.prop"
#define EX_APVER_PATH "apps.setting.product.outswver"
#define IN_APVER_PATH "apps.setting.product.swversion"
#define SENSOR_PATH "/sys/class/input/input"
#define SENSOR_VALUE "value"
#define G_SENSOR_NAME "bma2x2-accel"
#define P_SENSOR_NAME "ltr559_ps"
#define L_SENSOR_NAME "ltr559_als"
#define M_SENSOR_NAME "MMC3524x.c"
#define SENSOR_ENABLE "enable"
#define L_SENSOR_DATA "data"
#define SENSOR_ON "1"
#define SENSOR_OFF "0"
//#define L_SENSOR_ENABLE "/sys/bus/i2c/devices/i2c-1/1-0023/als_enable"
//#define L_SENSOR_VALUE "/sys/bus/i2c/devices/i2c-1/1-0023/als_adc"
#define L_SENSOR_ENABLE "/sys/class/sensors/cm36686-light/enable"
#define L_SENSOR_VALUE "/sys/class/capella_sensors/lightsensor/ls_adc"
#define P_SENSOR_ENABLE "/sys/class/sensors/cm36686-proximity/enable"
#define P_SENSOR_ENABLE1 "/sys/class/sensors/proximity/enable"
#define P_SENSOR_VALUE "/sys/class/capella_sensors/proximity/ps_adc"
#define P_SENSOR_VALUE1 "/sys/class/i2c-adapter/i2c-0/0-001e/pxvalue"
#define G_SENSOR_ENABLE "/sys/class/input/input1/enable"
#define G_SENSOR_VALUE "/sys/class/input/input1/value"
// value * g/1024
#define M_SENSOR_ENABLE "/sys/class/sensors/mmc3524x-mag/enable"
#define M_SENSOR_VALUE "/sys/bus/i2c/devices/0-0030/value"

//#define P_SENSOR_ENABLE "/sys/bus/i2c/devices/i2c-1/1-0023/ps_enable"
//#define P_SENSOR_VALUE "/sys/bus/i2c/devices/i2c-1/1-0023/ps_adc"     range: 0-2047
#define PLSENSOR_MANUID "/sys/bus/i2c/devices/i2c-1/1-0023/manuid"
//#define SYS_HEADSET_STATUS "/sys/class/switch/h2w/state"
#define SYS_HEADSET_STATUS "/sys/android_headset/state"
#define SYS_BACKLIGHT_PATH "/sys/class/leds/lcd-backlight/brightness"
#define SYS_RED_PATH "/sys/class/leds/chg_red/brightness"
#define SYS_GREEN_PATH "/sys/class/leds/chg_green/brightness"
#define SYS_BLUE_PATH "/sys/class/leds/chg_blue/brightness"
#define NUMBER_OF_DEVICE (20)
//#define POWER_KEY_EVENT "7k_handset"
#define POWER_KEY_EVENT "qpnp_pon"
#define HEADSET_KEY_EVENT "msm8939-snd-card-skuk Headset Jack"
#define HEADSETBUTTON_KEY_EVENT "msm8939-snd-card-skuk Button Jack"
#define INPUT_EVENT "/dev/input/event"
//#define GPIO_KEY_EVENT "7x27a_kp"
#define GPIO_KEY_EVENT "gpio-keys"
#define MSIC_AUDIO_EVENT "msic"
#define SDCARD_PATH "/dev/block/mmcblk1p1"
#define SDCARD_EXT "/storage/sdcard1"
#define FILESYSTEM_TYPE "vfat"
#define SYS_VIBRATOR_LEVEL "dev/proc/msic"
#define NIC_MAC_ADDRESS "/sys/class/net/wlan0/address"
#define INTERNAL_FLASH_PATH "/storage/sdcard0"
#define PATH_LEN (255)
#define FLASH_SAVE_DIR_MOVIES "Movies"
#define FLASH_SAVE_DIR_MUSIC "Music"
#define FLASH_SAVE_DIR_PICTURES "Pictures"
#define SYS_USB_CONFIG "sys.usb.config"
//#define FLASH_LIGHT_PATH "/sys/class/leds/torch-led/brightness"
#define FLASH_LIGHT_PATH "/sys/class/leds/torch-light/brightness"
#define FLASH_LIGHT_FLASH_PATH "/sys/class/leds/led:flash_0/brightness"
#define FLASH_LIGHT_TORCH_PATH "/sys/class/leds/led:flash_torch/brightness"
#define FLASH_LIGHT_OFF "0"
//#define FLASH_LIGHT_FLASH "1"
//#define FLASH_LIGHT_TORCH "2"
#define FLASH_LIGHT_FLASH "2"
#define FLASH_LIGHT_TORCH "1"
#define BACK_KEY_EVENT "goodix-ts"//"ft6306"
#define BACK_KEY_ABS_X_CODE (0x35)
#define BACK_KEY_ABS_X_VALUE (0x12c)
#define BACK_KEY_ABS_Y_CODE (0x36)
#define BACK_KEY_ABS_Y_VALUE (0x368)
#define TP_RAW_DATA_PATH "/sys/android_touch/rawdata"
//yuhongxia add tp path
#define TP_FUNC_CONTROL_PATH "/sys/android_touch/enable"
#define BATTERY_CAP_PATH "sys/class/power_supply/battery/capacity"
#define BATTERY_VOL_PATH "sys/class/power_supply/battery/voltage_now"
/*add by yu.hongxia@byd.com for battery test and temperature at 20150111*/
#define BATTERY_CURRENT_PATH "/sys/class/power_supply/bms/current_now"
#define BATTERY_TEMP_PATH "sys/class/power_supply/battery/temp"
#define BATTERY_CHARGING_ENABLE_PATH "sys/class/power_supply/battery/charging_enabled"
/*end wuxiaobo*/
#define LAST_LOG_PATH "/cache/recovery"
#define LAST_LOG_NAME "last_log"
#define DATA_FLAG_PATH "/data/data_flag"
#define READ_PLSENSOR_DATA "sensors_test 5 l"
#define READ_GSENSOR_DATA "cat /sys/class/input/input1/value"
// "sensors_test 5 a"
#define READ_MSENSOR_DATA "cat /sys/bus/i2c/devices/0-0030/value"
//"sensors_test 1 c"
#define READ_EFUSE_CHECK "cat /proc/cmdline"

#define PERSIST_DATA_PATH "/persist/data"
//efuse
#define EFUSE_SUC_REG "00058098"
#define NOT_EFUSE_FLAG "00000000"
#define EFUSE_SUC_FLAG "00303030"
#define EFUSE_BYD_REG "00058038"
#define EFUSE_BYD_FLAG "bf010017"
#define EFUSE_FLAG_NON "0"
#define EFUSE_FLAG_SUC "1"
#define EFUSE_FLAG_FAIL "9"
#define S_INIT 0
#define S_EXIT 1
#define S_RED 2
#define S_GREEN 3
#define S_BLUE 4
#define S_WHITE 5
#define S_BLACK 6
#define S_ZEBRA_H 7
#define S_ZEBRA_V 8
#define S_BLACK_M 9
#define S_WHITE_M 10
#define S_FLICKER 11
#define S_GRAYSCALE 12
#define S_RGB_ROW 13
#define S_WHITE_BLACK 14
#define S_GRID 15
#define S_UNLOCK 16
#define S_HAVEDEL 17
#define S_PASS 18
#define S_FAIL 19