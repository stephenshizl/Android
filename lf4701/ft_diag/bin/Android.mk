################################################################################
# @file pkgs/stringl/Android.mk
# @brief Makefile for building the string library on Android.
#
# -----------------------------------------------------------------------------
# Copyright (c) 2008 QUALCOMM Incorporated.  All Rights Reserved.  
# QUALCOMM Proprietary and Confidential.
# -----------------------------------------------------------------------------
#
################################################################################

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
include $(QC_PROP_ROOT)/common/build/remote_api_makefiles/remote_api_defines.mk

libdiag_includes:= \
    $(LOCAL_PATH)/../../qcom/proprietary/diag/include \
    $(LOCAL_PATH)/../../qcom/proprietary/diag/src 

#LOCAL_C_INCLUDES:= $(libdiag_includes)
#LOCAL_C_INCLUDES += $(QC_PROP_ROOT)/common/inc
#LOCAL_C_INCLUDES += $(TARGET_OUT_HEADERS)/nv/inc/
#LOCAL_C_INCLUDES += $(QC_PROP_ROOT)/oncrpc/inc/
#LOCAL_C_INCLUDES += $(QC_PROP_ROOT)/oncrpc/oncrpc/
#LOCAL_C_INCLUDES += $(QC_PROP_ROOT)/oncrpc/dsm/

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/../../../qcom/proprietary/diag/include \
    $(LOCAL_PATH)/../../../qcom/proprietary/diag/src \
    $(QC_PROP_ROOT)/common/inc \
    $(TARGET_OUT_HEADERS)/nv/inc \
    $(QC_PROP_ROOT)/oncrpc/inc \
    $(QC_PROP_ROOT)/oncrpc/oncrpc \
    $(QC_PROP_ROOT)/oncrpc/dsm/


LOCAL_SRC_FILES := \
    main.c \
    factory_tes.c
#    factory_test.c

commonSharedLibraries :=libdiag

LOCAL_LDLIBS += -pthread
LOCAL_MODULE:= ft_diag
LOCAL_MODULE_TAGS := optional
LOCAL_SHARED_LIBRARIES := $(commonSharedLibraries)
#LOCAL_SHARED_LIBRARIES += libnv
LOCAL_SHARED_LIBRARIES += libcutils
LOCAL_SHARED_LIBRARIES += libft_diag

include $(BUILD_EXECUTABLE)

