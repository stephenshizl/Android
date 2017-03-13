LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := RunIn

LOCAL_CERTIFICATE := platform

ifneq (, $(findstring "$(TARGET_BUILD_VARIANT)", "eng" "userdebug"))
LOCAL_MANIFEST_FILE := eng/AndroidManifest.xml
else
LOCAL_MANIFEST_FILE := user/AndroidManifest.xml
endif

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
