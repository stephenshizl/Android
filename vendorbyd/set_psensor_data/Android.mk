LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := set_psensor_data
LOCAL_SRC_FILES := set_psensor_data.c
LOCAL_SHARED_LIBRARIES:= \
    libutils \
    libcutils
    
include $(BUILD_EXECUTABLE)
