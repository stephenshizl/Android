
LOCAL_PATH:= $(call my-dir)
############### tcpdumplog
include $(CLEAR_VARS)
LOCAL_MODULE := tcpdumplog.sh
LOCAL_MODULE_TAGS := debug
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := tcpdumplog.sh
include $(BUILD_PREBUILT)
###################
