/*===========================================================================
                         EDIT HISTORY FOR MODULE
This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.
when      who            what, where, why
--------  ------         ------------------------------------------------------
20101012  Sang Mingxin   Initial to test the camera.
20120628  li.xujie@byd.com modify it for EG808T Camera diag cmd test .
20120713  li.xujie@byd.com  modify it for EG808T Camera diag cmd test --front camera ov7692 preview Orientation
20120726  li.xujie@byd.com add it for camera diag test ( screen flicker  )
===========================================================================*/
#undef LOG_TAG
#define LOG_TAG "FT_CAMERA_TEST"
#include <utils/Log.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <utils/String16.h>
#include <utils/Errors.h>
#include <binder/MemoryBase.h>
#include <binder/MemoryHeapBase.h>
#include <camera/ICameraService.h>
#include <media/mediaplayer.h>
#include <media/AudioSystem.h>
#include <cutils/atomic.h>
#include <camera/ICameraService.h>
//yhx #include <CameraHardwareInterface.h>
//yhx #include <hardware/hardware.h>
//yhx #include <hardware/camera.h>
#include <camera/Camera.h>
#include <camera/CameraParameters.h>
#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <linux/fb.h>
#include "pmem.h"
#include <errno.h>
using namespace android;
#define BYD_FTCAMERA_TRACE  ALOGE
#define FT_MAX_CAMERAS  2
static int bQuit = 0;
static int bOne = 0;
static int bTwo = 0;
static struct fb_var_screeninfo vi;
static int fd;
//yhx sp<CameraHardwareInterface> mCamHardware;
//yhx camera_module_t * FT_mModule = NULL ;
static int cameraId = 0;
/*  The size of image for display. */
typedef struct image_rect_struct
{
    uint32_t width; /* Image width */
    uint32_t height; /* Image height */
} image_rect_type;
int mNumberOfCameras;
char gv_picture_name[255] = "data/ftcamera.jpg";
char mtf_picture_name[255] = "data/MTF.jpg";
char blemish_picture_name[255] = "data/Blemish.jpg";
volatile int focusFlag = 0;
extern int ftscreen_write(unsigned char *yuv_buffer);
extern int ftscreen_init();
extern int ftscreen_destroy(void);
static int debug_frame_cnt = 0;
int ft_init(void)
{
    BYD_FTCAMERA_TRACE("\n ft_init() \n");
    fd = open("/dev/graphics/fb0", O_RDWR);
    if (fd < 0)
    {
        BYD_FTCAMERA_TRACE(" %s() : L( %d ) ,cannot open /dev/graphics/fb0, retrying with /dev/fb0 !!! \n", __func__, __LINE__);
        if ((fd = open("/dev/fb0", O_RDWR)) < 0)
        {
            BYD_FTCAMERA_TRACE(" %s() : L( %d ) ,cannot open /dev/fb0 !!! \n",__func__, __LINE__);
            return  - 1;
        }
    }
    if (ioctl(fd, FBIOGET_VSCREENINFO, &vi) < 0)
    {
        BYD_FTCAMERA_TRACE(" %s() : L( %d ) , failed to get fb0 info !!! \n",__func__, __LINE__);
        return  - 1;
    }
    return 0;
}

void dump_to_file(const char *file_name, void *write_data, int write_len)
{
    FILE *fp;
    BYD_FTCAMERA_TRACE("\n dump_to_file() \n");
    fp = fopen(file_name, "wb");
    if (fp == NULL)
    {
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] dump_to_file(): Open [%s] file Error\n", file_name);
    }
    else
    {
        fwrite(write_data, 1, write_len, fp);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] dump_to_file(): Save to file_name=%s write_len=%d\n", file_name, write_len);
        fclose(fp);
    }
}

void handleShutter(image_rect_type *size)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] handleShutter Enter\n");
}

void handlePreviewData(const sp < IMemory >  &mem)
{
    ssize_t offset;
    size_t size;
    sp < IMemoryHeap > heap = mem->getMemory(&offset, &size);
    BYD_FTCAMERA_TRACE("\n handlePreviewData() (%d, %d)\n", (int)offset, (int)size);
    ftscreen_write((uint8_t*)heap->base() + offset);
}

// picture callback - postview image ready
void handlePostview(const sp < IMemory >  &mem)
{
    #if 0
        ssize_t offset;
        size_t size;
        sp < IMemoryHeap > heap = mem->getMemory(&offset, &size);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] handlePostview(%d, %d)\n", (int)offset, (int)size);
        /* Add handle in here. */
        mCamHardware->disableMsgType(CAMERA_MSG_POSTVIEW_FRAME);
    #endif
}

void handleRawPicture(const android::sp < android::IMemory >  &mem)
{
    #if 0
        ssize_t offset;
        size_t size;
        sp < IMemoryHeap > heap = mem->getMemory(&offset, &size);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] handleRawPicture(%d, %d)\n", offset, size);
        /* Add handle in here. */
        mCamHardware->disableMsgType(CAMERA_MSG_RAW_IMAGE);
    #endif
}

void handleCompressedPicture(const sp < IMemory >  &mem)
{
    #if 0
        ssize_t offset;
        size_t size;
        sp < IMemoryHeap > heap = mem->getMemory(&offset, &size);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] handleCompressedPicture(%d, %d)\n", offset, size);
        dump_to_file(gv_picture_name, (uint8_t*)heap->base() + offset, size);
        /* Add handle in here. */
        mCamHardware->disableMsgType(CAMERA_MSG_COMPRESSED_IMAGE);
    #endif
}

void notifyCallback(int32_t msgType, int32_t ext1, int32_t ext2, void *user)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] notifyCallback(%d)\n", msgType);
    switch (msgType)
    {
        case CAMERA_MSG_FOCUS:
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] notifyCallback android::CAMERA_MSG_FOCUS\n");
            focusFlag = 1;
            break;
        case CAMERA_MSG_SHUTTER:
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] notifyCallback android::CAMERA_MSG_SHUTTER\n");
            break;
        default:
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] notifyCallback default\n");
            break;
    }
}

void dataCallback(int32_t msgType, const sp < IMemory >  &dataPtr, camera_frame_metadata_t *metadata, void *user)
{
    BYD_FTCAMERA_TRACE("\n dataCallback() ,msgType=%d \n", msgType);
    switch (msgType)
    {
        case CAMERA_MSG_PREVIEW_FRAME:
            handlePreviewData(dataPtr);
            break;
        case CAMERA_MSG_POSTVIEW_FRAME:
            handlePostview(dataPtr);
            break;
        case CAMERA_MSG_RAW_IMAGE:
            handleRawPicture(dataPtr);
            break;
        case CAMERA_MSG_COMPRESSED_IMAGE:
            handleCompressedPicture(dataPtr);
            break;
        default:
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s]\n", __FUNCTION__);
            break;
    }
}

void dataCallbackTimestamp(nsecs_t timestamp, int32_t msgType, const sp <IMemory >  &dataPtr, void *user)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] dataCallbackTimestamp(%d)\n", msgType);
}

extern "C" int FTCamera_SetPictureName(char *picture_name, int name_len)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] %s\n", __FUNCTION__);
    #if 0
        if (255 < name_len)
        {
            return  - 1;
        }
        memset(gv_picture_name, 0x00, sizeof(gv_picture_name));
        memcpy(gv_picture_name, picture_name, name_len);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] %s gv_picture_name=%s\n",__FUNCTION__, gv_picture_name);
    #endif
    return 0;
}

extern "C" int FTCamera_TakePicture(void)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] take picture!!!\n", __FUNCTION__);
    #if 0
        mCamHardware->enableMsgType(CAMERA_MSG_SHUTTER |
                                    CAMERA_MSG_POSTVIEW_FRAME |
                                    CAMERA_MSG_RAW_IMAGE |
                                    CAMERA_MSG_COMPRESSED_IMAGE);
        mCamHardware->takePicture();
        sleep(4);
        mCamHardware->enableMsgType(CAMERA_MSG_PREVIEW_FRAME);
        mCamHardware->startPreview();
    #endif
    return 0;
}
extern "C" int FTCamera_StartAutoFocus(void)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] take picture!!!\n", __FUNCTION__);
    #if 0
        mCamHardware->enableMsgType(CAMERA_MSG_SHUTTER |
                                    CAMERA_MSG_POSTVIEW_FRAME |
                                    CAMERA_MSG_RAW_IMAGE |
                                    CAMERA_MSG_COMPRESSED_IMAGE);
        if (0 == cameraId)
        {
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s]camera id:%d \n",__FUNCTION__, cameraId);
            CameraParameters mParameters = mCamHardware->getParameters();
            //mCamHardware->cancelAutoFocus();
            mParameters.set(CameraParameters::KEY_FOCUS_MODE, CameraParameters::FOCUS_MODE_AUTO);
            mParameters.set(CameraParameters::KEY_FOCUS_AREAS, "(0,0,0,0,0)");
            mParameters.set(CameraParameters::KEY_METERING_AREAS, "(0,0,0,0,0)");
            mCamHardware->autoFocus();
        }
    #endif
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] startPreview !!!\n", __FUNCTION__);
    return 0;
}

extern "C" int FTCamera_TakePictureWithAutoFocus(void)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] take picture!!!\n", __FUNCTION__);
    #if 0
        /*
        focusFlag = 0;
        mCamHardware->autoFocus();
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] enter loop !!!\n", __FUNCTION__);
        while (focusFlag == 0)
        {
        sleep(1);
        }
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] exit loop !!!\n", __FUNCTION__);
         */
        focusFlag = 0;
        mCamHardware->enableMsgType(CAMERA_MSG_SHUTTER |
                                    CAMERA_MSG_POSTVIEW_FRAME |
                                    CAMERA_MSG_RAW_IMAGE |
                                    CAMERA_MSG_COMPRESSED_IMAGE);
        mCamHardware->takePicture();
        sleep(1);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] startPreview !!!\n", __FUNCTION__);
        mCamHardware->enableMsgType(CAMERA_MSG_PREVIEW_FRAME);
        mCamHardware->startPreview();
    #endif
    return 0;
}

extern "C" int FTCamera_TakePictureWithAutoFocus_spec(int params)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] take picture!!!\n", __FUNCTION__);
    #if 0
        if (mCamHardware == NULL)
        {
            ALOGE("exec HAL_openCameraHardware(0,5) failed\n");
            return  - 1;
        }
        switch (params)
        {
            case 1:
                //	      	if (bOne)  return 0 ;
                //		bOne =1;
                /*yhx       mCamHardware->sendCommand(CAMERA_CMD_START_MTF,0,0);
                FTCamera_SetPictureName(mtf_picture_name,sizeof(mtf_picture_name));*/
                break;
            case 2:
                //	      	if (bTwo)  return 0 ;
                //		bTwo = 1;
                /* yhx    mCamHardware->sendCommand(CAMERA_CMD_START_BLEMISH,0,0);
                FTCamera_SetPictureName(blemish_picture_name,sizeof(blemish_picture_name));*/
                break;
            default:
                break;
        }
        FTCamera_TakePictureWithAutoFocus();
        switch (params)
        {
            case 1:
                //yhx    mCamHardware->sendCommand(CAMERA_CMD_STOP_MTF,0,0);
                break;
            case 2:
                //yhx    mCamHardware->sendCommand(CAMERA_CMD_STOP_BLEMISH,0,0);
                break;
            default:
                break;
        }
    #endif
    return 0;
}

#define FT_min(a,b) ((a)<(b)?(a):(b))
static int Camera_StartPreview(int FTcameraId)
{
    int ret = 0;
    #if 0
        char FT_camera_device_name[10];
        cameraId = FTcameraId;
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] sensor_id=%d \n", __FUNCTION__,cameraId);
        if (bQuit)
        {
            return 0;
        }
        bQuit = 1;
        ft_init();
        //szy    ioctl(fd, FBDIAGSTOPFLUSH, &vi);
        if ( - 1 == ftscreen_init())
        {
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] ftscreen_init error!!!\n", __FUNCTION__);
            return  - 1;
        }
        //szy    ioctl(fd, FBLOCKFB, &vi);
        //loading  Camer module
        if (hw_get_module(CAMERA_HARDWARE_MODULE_ID, (const hw_module_t **)&FT_mModule) < 0)
        {
            ALOGE("Could not load camera HAL module");
            mNumberOfCameras = 0;
        }
        else
        {
            mNumberOfCameras = FT_mModule->get_number_of_cameras();
            if (mNumberOfCameras > FT_MAX_CAMERAS)
            {
                ALOGE("Number of cameras(%d) > MAX_CAMERAS(%d).",mNumberOfCameras, FT_MAX_CAMERAS);
                mNumberOfCameras = FT_MAX_CAMERAS;
            }
        }
        snprintf(FT_camera_device_name, sizeof(FT_camera_device_name), "%d", cameraId);
        mCamHardware = new CameraHardwareInterface(FT_camera_device_name);
        mCamHardware->initialize(&FT_mModule->common);
        mCamHardware->setCallbacks(notifyCallback, dataCallback, dataCallbackTimestamp, (void*)cameraId);
        CameraParameters mParameters = mCamHardware->getParameters();
        BYD_FTCAMERA_TRACE(" after getcameraInfo  succeed\n");
        if (0 == cameraId)
        {
            mParameters.setPreviewSize(640, 480);
            mParameters.setPictureSize(2592, 1944);
            //mParameters.set(CameraParameters::KEY_FOCUS_MODE,  CameraParameters::FOCUS_MODE_CONTINUOUS_PICTURE);
            mParameters.set(CameraParameters::FOCUS_MODE_AUTO, 2);
            mParameters.set(CameraParameters::KEY_ROTATION, 90);
        }
        else
        {
            mParameters.setPreviewSize(640, 480);
            mParameters.setPictureSize(1600, 1200);
            mParameters.set(CameraParameters::KEY_ROTATION, 270);
        }
        //yhx  mParameters.set("no-display-mode",FTCAMERA_NO_DISPLAY_MODE);
        //mParameters.setOrientation(1);  lijun
        //mParameters.set(CameraParameters::KEY_ROTATION, 90);
        mCamHardware->setParameters(mParameters);
        mCamHardware->enableMsgType(CAMERA_MSG_ERROR | CAMERA_MSG_ZOOM | CAMERA_MSG_FOCUS);
        mCamHardware->enableMsgType(CAMERA_MSG_PREVIEW_FRAME);
        //szy    mCamHardware->sendCommand(CAMERA_CMD_FT_CAMERA_TEST_START,0,0);
        ret = mCamHardware->startPreview();
        if (ret != NO_ERROR)
        {
            BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] startPreview error!!!\n", __FUNCTION__);
            return  - 1;
        }
    #endif
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] End\n", __FUNCTION__);
    return 0;
}

extern "C" int FTCamera_StartPreview()
{
    #if 0
        int ret = 0;
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] %s : start  \n", __FUNCTION__);
        ret = Camera_StartPreview(0);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] %s : end (ret = %d )  \n", __FUNCTION__, ret);
        return ret;
    #endif
    return 0;
}

extern "C" int FTCamera_StartPreview1()
{
    #if 0
        int ret = 0;
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] %s : start  \n", __FUNCTION__);
        ret = Camera_StartPreview(1);
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] %s : end (ret = %d )  \n", __FUNCTION__, ret);
        return ret;
    #endif
    return 0;
}

extern "C" int FTCamera_StopPreview(void)
{
    BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s]\n", __FUNCTION__);
    #if 0
        bQuit = 0;
        bOne = 0;
        bTwo = 0;
        mCamHardware->stopPreview();
        mCamHardware->disableMsgType(CAMERA_MSG_SHUTTER |
                                     CAMERA_MSG_POSTVIEW_FRAME |
                                     CAMERA_MSG_RAW_IMAGE |
                                     CAMERA_MSG_COMPRESSED_IMAGE);
        mCamHardware->cancelPicture();
        mCamHardware->disableMsgType(CAMERA_MSG_ALL_MSGS);
        //szy    mCamHardware->sendCommand(CAMERA_CMD_FT_CAMERA_TEST_STOP,0,0);
        mCamHardware->release();
        mCamHardware.clear();
        //szy    ioctl(fd, FBDIAGSTARTFLUSH, &vi);
        //szy    ioctl(fd, FBUNLOCKFB, &vi);
        ftscreen_destroy();
    #endif
    return 0;
}

extern "C" int FTCamera_read_picture(unsigned char *data, int data_len, char*file_stat)
{
    #if 0
        FILE *fp;
        //char ftcamera_buf[100];
        static int camera_file_cursor = 0;
        int i = 0;
        BYD_FTCAMERA_TRACE("\n FTCamera_read_picture() \n");
        if ((fp = fopen(gv_picture_name, "r+")) == NULL)
        {
            BYD_FTCAMERA_TRACE(" %s() : L( %d ) ,open file(%s) error:%s ", __func__, __LINE__, gv_picture_name, strerror(errno));
            return  - 1;
        }
        BYD_FTCAMERA_TRACE("[Ftcamera.cpp] [%s] \n", __FUNCTION__);
        fseek(fp, camera_file_cursor, SEEK_SET);
        len = fread(data, sizeof(unsigned char), data_len, fp);
        /* The file be read completed and renew curr_cursor position */
        if (feof(fp))
        {
            *file_stat = 1;
            camera_file_cursor = 0;
        }
        /* Move the cursor to next position for reading */
        camera_file_cursor += len;
        return len;
    #endif
    return 0;
}
