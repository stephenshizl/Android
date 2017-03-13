LOCAL_PATH:= $(call my-dir)
$(shell mkdir -p $(TARGET_OUT)/xbin)
$(shell cp -f $(LOCAL_PATH)/* $(TARGET_OUT)/xbin)
include $(call all-subdir-makefiles)