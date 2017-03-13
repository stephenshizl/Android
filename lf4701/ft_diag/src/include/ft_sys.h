#include <utils/Log.h>
#include "public.h"

#define FT_LOG(...) (void)android_printLog( 4,"FT_TST",__VA_ARGS__)


#define HW_VERSION_PATH "/sys/board_properties/hw_version"
#define SW_VERSION_SRC_FILE "/system/etc/system.prop"
#define IN_APVER_PATH "ro.build.version.incremental"
#define EX_APVER_PATH "ro.build.display.id"

#define SENSOR_PATH "/sys/class/input/input"
#define SENSOR_VALUE "value"
#define G_SENSOR_NAME "bma2x2"
#define P_SENSOR_NAME "ltr559_ps"
#define L_SENSOR_NAME "ltr559_als"
#define SENSOR_ENABLE "enable"
#define L_SENSOR_DATA "data"
#define SENSOR_ON "1"
#define SENSOR_OFF "0"

#define L_SENSOR_ENABLE "/sys/bus/i2c/devices/i2c-1/1-0023/als_enable"
#define L_SENSOR_VALUE "/sys/bus/i2c/devices/i2c-1/1-0023/als_adc"

#define P_SENSOR_ENABLE "/sys/bus/i2c/devices/i2c-1/1-0023/ps_enable"
#define P_SENSOR_VALUE "/sys/bus/i2c/devices/i2c-1/1-0023/ps_adc"

#define PLSENSOR_MANUID "/sys/bus/i2c/devices/i2c-1/1-0023/manuid"

//#define SYS_HEADSET_STATUS "/sys/class/switch/h2w/state"
#define SYS_HEADSET_STATUS "/sys/android_headset/state"
#define SYS_BACKLIGHT_PATH "/sys/class/leds/lcd-backlight/brightness"
//#define POWER_KEY_EVENT "7k_handset"
#define POWER_KEY_EVENT "qpnp_pon"
#define HEADSET_KEY_EVENT "msm8226-tapan-snd-card Headset Jack"
#define HEADSETBUTTON_KEY_EVENT "msm8226-tapan-snd-card Button Jack"
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

#define SYS_USB_CONFIG "persist.sys.usb.config"

#define FLASH_LIGHT_PATH "/sys/class/leds/torch-led/brightness"
#define FLASH_LIGHT_OFF "0"
#define FLASH_LIGHT_FLASH "1"
#define FLASH_LIGHT_TORCH "2"

#define BACK_KEY_EVENT "ft5x06_ts"//"ft6306"
#define BACK_KEY_ABS_X_CODE (0x35)
#define BACK_KEY_ABS_X_VALUE (0x12c)
#define BACK_KEY_ABS_Y_CODE (0x36)
#define BACK_KEY_ABS_Y_VALUE (0x368)

#define TP_RAW_DATA_PATH "/sys/android_touch/rawdata"
//yuhongxia add tp path
#define TP_FUNC_CONTROL_PATH "/sys/android_touch/enable"
#define BATTERY_CAP_PATH "sys/class/power_supply/battery/capacity"

#define LAST_LOG_PATH "/cache/recovery"
#define LAST_LOG_NAME "last_log"
#define DATA_FLAG_PATH "/data/data_flag"

//efuse
#define EFUSE_SUC_REG "00058034"
#define NOT_EFUSE_FLAG "00000000"
#define EFUSE_SUC_FLAG "007ffcfe"
#define EFUSE_BYD_REG "000580ac"
#define EFUSE_BYD_FLAG "8e82170b"
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