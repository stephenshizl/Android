
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
    libcamera_client 


LOCAL_C_INCLUDES += external/jpeg \
    hardware/libhardware/include \
    hardware/intel/libcamera \
    frameworks/native/include/utils \
    frameworks/native/include/binder \
    external/skia/include/core \
    external/skia/include/images \
    $(TARGET_OUT_HEADERS)/libmfldadvci \
    $(TARGET_OUT_HEADERS)/libsharedbuffer \
    $(LOCAL_PATH)/include \
    vendor/byd/ft_diag/src/camera \
    $(TARGET_OUT_INTERMEDIATES)/KERNEL_OBJ/usr/include
#    frameworks/av/include/camera 
#    frameworks/av/services/camera/libcameraservice 
#    frameworks/av/services/camera/libcameraservice/device1/ 
    
LOCAL_ADDITIONAL_DEPENDENCIES := $(TARGET_OUT_INTERMEDIATES)/KERNEL_OBJ/usr

LOCAL_SRC_FILES  := \
    adb/adb.c \
    rtc/rtc.c \
    version/version.c \
    reset/reset.c \
    bt/bt.c \
    wifi/wifi.c \
    camera/camera.c \
    speaker/speaker.c \
    psensor/psensor.c \
    cit/cit.c \
    efuse/efuse.c \
    battery/battery.c
#    sensor/psensor.c 

#    camera/ftcamera.cpp 
#    camera/ftscreen.cpp 
#    camera/pmem.cpp 

LOCAL_MODULE      := libft_diag
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_TAGS := optional
include $(BUILD_SHARED_LIBRARY)


