LOCAL_PATH := $(call my-dir)
$(shell mkdir -p $(PRODUCT_OUT))
$(shell cp -r $(LOCAL_PATH)/* $(PRODUCT_OUT)/)

