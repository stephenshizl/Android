LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) 

LOCAL_JAVA_LIBRARIES := telephony-common 
#qcrilhook 
LOCAL_STATIC_JAVA_LIBRARIES := qcrilhookjar

LOCAL_PACKAGE_NAME := FactoryTestAftermarket
LOCAL_CERTIFICATE := platform

LOCAL_REQUIRED_MODULES := \
                       sdtest

ifneq (, $(findstring "$(TARGET_BUILD_VARIANT)", "eng" "userdebug"))
LOCAL_MANIFEST_FILE := eng/AndroidManifest.xml
else
LOCAL_MANIFEST_FILE := user/AndroidManifest.xml
endif

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := qcrilhookjar:classes.jar 
include $(BUILD_MULTI_PREBUILT)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))