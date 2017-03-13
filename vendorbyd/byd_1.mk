include vendor/3rd_App/3rd_App.mk
include vendor/Len_E2E/Len_E2E.mk
include vendor/nec/nec_App.mk

PRODUCT_PACKAGES += \
    LenOTAPRC \
    LenOTAROW
#FreeCall rm freecall,LF7002Q_C000179,zhaofei,20160625
ifeq "prc" "$(CONFIG_BUILD_REGION)"
	ifeq "lf7002q" "$(TARGET_PRODUCT)"
	else
		PRODUCT_PACKAGES += \
		  FreeAnswer
	endif	  
else
    PRODUCT_PACKAGES += \
        ChooseCountryCode
endif 
#FreeCall rm freecall,LF7002Q_C000147,halina,20160606

#add by yangjie
PRODUCT_PACKAGES += \
    LogTool \
    UserLog \
    crashlogd \
    kernel_kmsg \
    Tools \
    DebugMode \
    BTLogTool \
    OpenUsbDebug \
    ChromeCustomizations \
    SoundRecorder

PRODUCT_PACKAGES += \
    mm-audio-ftm

ifneq ($(TARGET_BUILD_VARIANT),user)
PRODUCT_PACKAGES += \
    ft_diag \
    libft_diag \
    FactoryKit
endif
ifneq (,$(filter $(TARGET_BUILD_VARIANT),eng userdebug))
    ADDITIONAL_DEFAULT_PROPERTIES += persist.service.apklogfs.enable=1 \
                                     persist.service.crashlog.enable=1
endif
#end

# Begin [add for lenovo prop config]
ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.operator=open \
                               ro.lenovo.platform=qualcomm \
                               ro.lenovo.adb=apkctl \
                               ro.lenovo.signalbars=five \
                               ro.lenovo.videocall=false \
                               ro.lenovo.vibeuistyle=false \
                               ro.lenovo.wificert=no \
                               persist.backgrounddata.enable=false \
                               persist.lenovo.ltetype=CSFB

ifeq "prc" "$(CONFIG_BUILD_REGION)"
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.region=prc \
                                 ro.lenovo.device=phone
  ADDITIONAL_DEFAULT_PROPERTIES += persist.radio.mbn_path=/system/vendor/modemconfig01/
  LF7002Q_CFLAGS += -DCONFIG_BUILD_REGION_PRC
else ifeq "nec" "$(CONFIG_BUILD_REGION)"
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.region=nec
  LF7002Q_CFLAGS += -DCONFIG_BUILD_REGION_NEC
else
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.region=row \
                                 ro.lenovo.device=tablet
  LF7002Q_CFLAGS += -DCONFIG_BUILD_REGION_ROW
endif

ifeq "wifi" "$(CONFIG_BUILD_MODE)"
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.mode=wifi
  LF7002Q_CFLAGS += -DCONFIG_BUILD_MODE_WIFI
else
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.mode=lte
  LF7002Q_CFLAGS += -DCONFIG_BUILD_MODE_LTE
endif

ifeq "lf7002q" "$(TARGET_PRODUCT)"
  ADDITIONAL_BUILD_PROPERTIES += ro.build.project=lf7002q
  LF7002Q_CFLAGS += -DTARGET_PRODUCT_LF7002Q
else
  ADDITIONAL_BUILD_PROPERTIES += ro.build.project=phoebeM
  LF7002Q_CFLAGS += -DTARGET_PRODUCT_PHOEBEM
endif

ifneq "voice" "$(CONFIG_BUILD_REGIONRIL)"
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.sim=dsds
else
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.sim=dsds
endif

ifeq "row" "$(CONFIG_BUILD_REGION)"
  ifeq "voice" "$(CONFIG_BUILD_REGIONRIL)"
      ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.tablet=3gcall
  else
      ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.tablet=3gdata
  endif
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.bqb=pass
else
  ADDITIONAL_BUILD_PROPERTIES += ro.lenovo.bqb=no
endif
# End [add for lenovo prop config]

ifeq ($(CONFIG_BUILD_REGION), prc)
PRODUCT_COPY_FILES += \
    vendor/byd/app/Lenovo_Suite/LenovoSuite_pad_prc_open_160606.iso:$(PRODUCT_OUT)/system/Lenovo_Suite/LenovoSuite_pad.iso
endif
ifeq ($(CONFIG_BUILD_REGION), row)
PRODUCT_COPY_FILES += \
    vendor/byd/app/Lenovo_Suite/LenovoSuite_pad_row_160606.iso:$(PRODUCT_OUT)/system/Lenovo_Suite/LenovoSuite_pad.iso \
    vendor/3rd_App/ROW/ESFile/ESOemConfig:$(PRODUCT_OUT)/system/etc/ESOemConfig
endif

# Begin [add for cts, remove app ops in row version]
ifeq "prc" "$(CONFIG_BUILD_REGION)"
  ADDITIONAL_BUILD_PROPERTIES += persist.sys.strict_op_enable=true
else
  ADDITIONAL_BUILD_PROPERTIES += persist.sys.strict_op_enable=false
endif
# End [add for cts, remove app ops in row version]

#add by chenying for LF7001QM_C000311
PRODUCT_COPY_FILES += \
    vendor/byd/sh_gestures/gestures:system/etc/gestures

#add by halina,20160517
ifeq "lf7002q" "$(TARGET_PRODUCT)"
    $(shell cp -r vendor/byd/keyguard_ogg/Lock2.ogg  frameworks/base/data/sounds/effects/ogg/Lock.ogg)
    $(shell cp -r vendor/byd/keyguard_ogg/Unlock2.ogg  frameworks/base/data/sounds/effects/ogg/Unlock.ogg)
else
    $(shell cp -r vendor/byd/keyguard_ogg/Lock.ogg  frameworks/base/data/sounds/effects/ogg/Lock.ogg)
    $(shell cp -r vendor/byd/keyguard_ogg/Unlock.ogg  frameworks/base/data/sounds/effects/ogg/Unlock.ogg)
endif

ifeq "lf7002q" "$(TARGET_PRODUCT)"
    PRODUCT_PACKAGES += LenovoPhab
    ifneq ($(TARGET_BUILD_VARIANT),user)
        PRODUCT_PACKAGES += FT_Terminal_Test
    endif
else
    PRODUCT_PACKAGES += WideTouch
endif
