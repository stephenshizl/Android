#
# Copyright (C) BYD 2010
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= crashlogd.c

LOCAL_C_INCLUDES += \
  vendor/byd/app/log_capture/backtrace

# sys/sha1.h has been moved out of default bionic includes
LOCAL_C_INCLUDES += \
    bionic/libc/upstream-netbsd/android/include

# Options


#LOCAL_MODULE_TAGS := eng debug
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE:= crashlogd
LOCAL_MULTILIB := 32
LOCAL_UNSTRIPPED_PATH := $(TARGET_ROOT_OUT_UNSTRIPPED)

LOCAL_SHARED_LIBRARIES:= libparse_stack libc libcutils

include $(BUILD_EXECUTABLE)

#PRODUCT_COPY_FILES += \
        $(LOCAL_PATH)/analyze_crash:system/bin/analyze_crash \
        $(LOCAL_PATH)/del_hist.sh:system/bin/del_hist.sh \
        $(LOCAL_PATH)/del_log.sh:system/bin/del_log.sh \
#        $(LOCAL_PATH)/monitor_crashenv:system/bin/monitor_crashenv \
#        $(LOCAL_PATH)/dumpstate_dropbox.sh:system/bin/dumpstate_dropbox.sh

#analyze_crash
include $(CLEAR_VARS)
LOCAL_MODULE := analyze_crash
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := analyze_crash
include $(BUILD_PREBUILT)

#del_hist.sh
include $(CLEAR_VARS)
LOCAL_MODULE := del_hist.sh
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := del_hist.sh
include $(BUILD_PREBUILT)

#del_log.sh
include $(CLEAR_VARS)
LOCAL_MODULE := del_log.sh
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := del_log.sh
include $(BUILD_PREBUILT)

#monitor_crashenv
include $(CLEAR_VARS)
LOCAL_MODULE := monitor_crashenv
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := monitor_crashenv
include $(BUILD_PREBUILT)

#dumpstate_dropbox.sh
include $(CLEAR_VARS)
LOCAL_MODULE := dumpstate_dropbox.sh
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_EXECUTABLES)
LOCAL_SRC_FILES := dumpstate_dropbox.sh
include $(BUILD_PREBUILT)
