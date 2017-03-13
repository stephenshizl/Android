LOCAL_PATH:= $(call my-dir)
############# TcpdumpTool
include $(CLEAR_VARS)
LOCAL_STATIC_JAVA_LIBRARIES += android-common
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_MODULE_TAGS := debug
LOCAL_PACKAGE_NAME := TcpdumpTool
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
###################
include $(call all-makefiles-under,$(LOCAL_PATH))
