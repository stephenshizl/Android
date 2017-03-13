LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := Tools

ifeq ($(BYD_TO_INTEL),true)
include $(BUILD_BIN_PACKAGE)
else
include $(BUILD_PACKAGE)
endif

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
