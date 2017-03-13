#include "include/ft.h"
#include "include/ft_sys.h"
#include <errno.h>
#if 1
    /********************************************
     * Description:
     * Start camera preview
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_camera_start_preview(int type)
    {
        int ret =  - 1;
        FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
        if (type == 0)
        {
            ret = FTCamera_StartPreview();
        }
        else if (type == 1)
        {
            ret = FTCamera_StartPreview1();
        }
        else
        {
            FT_LOG("[%s,%d] type: %d", __func__, __LINE__, type);
        }
        FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
        return ret;
    }
    /********************************************
     * Description:
     * Take photo by default parameter
     *    Save path: data/default.jpg
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_camera_default_capture()
    {
        int ret =  - 1;
        ret = FTCamera_TakePictureWithAutoFocus();
        return ret;
    }
    /********************************************
     * Description:
     * Take photo by MTF parameter
     *    Save path: data/MTP.jpg
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_camera_MTF_capture()
    {
        int ret =  - 1;
        ret = FTCamera_TakePictureWithAutoFocus_spec(1);
        return ret;
    }
    /********************************************
     * Description:
     * Take photo by Blemish parameter
     *    Save path: data/Blemish.jpg
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_camera_Blemish_capture()
    {
        int ret =  - 1;
        ret = FTCamera_TakePictureWithAutoFocus_spec(2);
        return ret;
    }
    /********************************************
     * Description:
     * Delete the photo
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_camera_del_photo()
    {
        int ret =  - 1;
        FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
        ret = system("rm data/frontcamera.jpg;rm data/backcamera.jpg");
        if (ret != 0)
        {
            FT_LOG("[%s,%d] Error,delete picture fail\n", __func__, __LINE__);
            return  - 1;
        }
        FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
        return 0;
    }
    /********************************************
     * Description:
     * Stop camera preview
     *
     * Input parameters:
     *
     * Return value:
     * 1: success
     * 0: failed
     ********************************************/
    int ft_camera_stop_preview()
    {
        int ret =  - 1;
        ret = FTCamera_StopPreview();
        return ret;
    }
#endif
int ft_camera_start_preview_lte(int type)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    if (type == 0)
    {
        // ret = FTCamera_StartPreview();
        //ret = system("am start -n com.qti.factory/com.qti.factory.CameraBack.CameraBack");
        ret = system("am start -n com.qti.factory/com.qti.factory.diag.DiagCameraBack");
    }
    else if (type == 1)
    {
        // ret = FTCamera_StartPreview1();
        //ret = system("am start -n com.qti.factory/com.qti.factory.CameraFront.CameraFront");
        ret = system("am start -n com.qti.factory/com.qti.factory.diag.DiagCameraFront");
    }
    else
    {
        FT_LOG("[%s,%d]Error camera id ,test failed type: %d", __func__, __LINE__, type);
        return -1;
    }
    if(ret != 0)
    {
        FT_LOG("[%s,%d] Error, Open Camera test application failed, ret: %d, errno: %d\n", __func__, __LINE__, ret, errno);
        return -1;
    }

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}

int ft_camera_default_capture_lte()
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    //ret = system("input tap  1764 1032");
    ret = system("input keyevent 7");
    if(ret != 0)
    {
        FT_LOG("[%s,%d] Error, TakePicture failed, ret: %d, errno: %d\n", __func__, __LINE__, ret, errno);
        return -1;
    }
        FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}
int ft_camera_del_photo_lte()
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
   // ret = system("rm data/ftcamera.jpg;rm data/ftcamerafront.jpg;");
    ret = system("rm data/frontcamera.jpg;rm data/backcamera.jpg");
    if (ret != 0)
    {
        FT_LOG("[%s,%d] Error,delete picture fail\n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}

int ft_camera_stop_preview_lte()
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    ret = system("input tap  1756 929");
    ret = system("am force-stop com.qti.factory");
    if (ret != 0)
    {
        FT_LOG("[%s,%d] Stop camera preview failed\n", __func__, __LINE__);
        return -1;
    }

    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}
int ft_camera_lte_wake()
{
    int ret =  - 1;
    ret = system("input keyevent 26");
    return ret;
}
int ft_camera_lte_unlock()
{
    int ret =  - 1;
    ret = system("input swipe 400 600 100 600");
    return ret;
}
