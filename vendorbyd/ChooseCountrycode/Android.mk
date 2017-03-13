# Copyright 2006-2014 The Android Open Source Project

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= countrycode.cpp
LOCAL_MODULE := countrycode
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS := -Werror
LOCAL_SHARED_LIBRARIES:=libc libcutils
include $(BUILD_EXECUTABLE)

include $(call first-makefiles-under,$(LOCAL_PATH))
