LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


LOCAL_SRC_FILES := $(call all-subdir-java-files)
#LOCAL_STATIC_JAVA_LIBRARIES += android-common com.marvell.cmmb.aidl
LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := UserLog
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

