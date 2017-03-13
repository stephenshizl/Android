LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) 

LOCAL_PACKAGE_NAME := FactoryTest

LOCAL_CERTIFICATE := platform

LOCAL_REQUIRED_MODULES := \
                       sdtest

ifneq (, $(findstring "$(TARGET_BUILD_VARIANT)", "eng" "userdebug"))
LOCAL_MANIFEST_FILE := eng/AndroidManifest.xml
else
LOCAL_MANIFEST_FILE := user/AndroidManifest.xml
endif

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))