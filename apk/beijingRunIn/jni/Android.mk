LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

#LOCAL_MODULE_PATH := $(TARGET_OUT_OPL)/lib

# put your source files here
#ifeq ($(TARGET_PRODUCT),generic)
LOCAL_SRC_FILES:= \
    com_runinjni.cpp \
    runin_onload.cpp

# put the libraries you depend on here.
LOCAL_SHARED_LIBRARIES := \
    libandroid_runtime \
    libhardware \
    libnativehelper \
    libcutils \
    libutils \
    libui \
    #libsystem_server \
#    libNcit

ifeq ($TARGET_OS)-$(TARGET_SIMULATOR),linux-true)
LOCAL_LDLIBS += -ldl
endif

ifneq ($(TARGET_SIMULATOR),true)
LOCAL_SHARED_LIBRARIES += libdl
endif

LOCAL_C_INCLUDES += \
    external/tremor/Tremor \
    frameworks/base/core/jni \
    $(JNI_H_INCLUDE) \
    $(call include-path-for, corecg graphics)
#endif

LOCAL_CFLAGS += -I external/libNcit

LOCAL_PRELINK_MODULE := false
# put your module name here
LOCAL_MODULE:= libRunin_jni

include $(BUILD_SHARED_LIBRARY)

