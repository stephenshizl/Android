
LOCAL_PATH := $(my-dir)
include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := \
    libcutils \
    libutils \
    libcrypto \
    libbinder \
    libhardware_legacy \
    libhardware \
    libui \
    libmedia \
    libcamera_client \
    libpixelflinger


LOCAL_C_INCLUDES += external/jpeg \
    hardware/libhardware/include \
    hardware/intel/libcamera \
    frameworks/native/include/utils \
    frameworks/native/include/binder \
    frameworks/av/include/camera \
    frameworks/av/services/camera/libcameraservice \
    external/skia/include/core \
    external/skia/include/images \
    $(TARGET_OUT_HEADERS)/libmfldadvci \
    $(TARGET_OUT_HEADERS)/libsharedbuffer \
    $(LOCAL_PATH)/include \
    $(TARGET_OUT_INTERMEDIATES)/KERNEL_OBJ/usr/include

LOCAL_ADDITIONAL_DEPENDENCIES := $(TARGET_OUT_INTERMEDIATES)/KERNEL_OBJ/usr

LOCAL_SRC_FILES  := \
    public/public.c \
    adb/adb.c \
    bt/bt.c \
    wifi/wifi.c \
    data/data.c \
    version/version.c \
    keybox/keybox.c \
    camera/camera.c \
    camera/ftscreen.cpp \
    camera/ftcamera.cpp \
    camera/pmem.cpp \
    sim/sim.c \
    fm/fm.c \
    flash/flash.c \
    sensor/psensor.c \
    sensor/gsensor.c \
    sensor/lsensor.c \
    sensor/sensorId.c \
    sensor/msensor.c \
    keypad/keypad.c \
    display/display.c \
    vibrator/vibrator.c \
    headset/headset.c \
    sdcard/sdcard.c \
    awake/awake.c \
    backlight/backlight.c \
    touchscreen/touchscreen.c \
    charger/charger.c \
    audio/audio.c \
    efuse/efuse.c \
    power/power.c \
    hall/hall.c \
    runin/runin.c \
    rtc/rtc.c \
    hwinfo/hwinfo.c \

LOCAL_MODULE      := libft_diag
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_TAGS := optional
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := busybox
LOCAL_MODULE_TAGS := optional eng debug
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := busybox/busybox
include $(BUILD_PREBUILT)
