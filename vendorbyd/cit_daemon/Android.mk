
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := \
    libcutils\
    libutils

LOCAL_SRC_FILES := \
    cit_daemon.c 

LOCAL_LDLIBS += -pthread
LOCAL_MODULE:= cit_daemon
LOCAL_MODULE_TAGS := optional
LOCAL_C_INCLUDES += system/core/include/log

include $(BUILD_EXECUTABLE)

