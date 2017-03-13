#include "include/ft.h"
#include "include/ft_sys.h"


int ft_camera_start_preview_lte(int type)
{
	int ret = -1;

	FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
	if(type == 0)
	{
		//    ret = FTCamera_StartPreview();
		ret = system("am start -n com.app.Diagapp/com.app.Diagapp.DiagappCameraBack");
	}
	else if(type == 1)
	{
		// ret = FTCamera_StartPreview1();
		ret = system(" am start -n com.app.Diagapp/com.app.Diagapp.DiagappCameraFront");

	}
	else
	{
		FT_LOG("[%s,%d] type: %d",__func__,__LINE__,type);
	}
	FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
	return ret;
}


int ft_camera_default_capture_lte()
{
	int ret = -1;
	ret = system("input tap  1100 650");
	ret = system("input tap  1140 560");
	printf("return value:%d\n",ret);
	return ret;
}

int ft_camera_del_photo_lte()
{
	int ret = 0;

	FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
	system("rm /data/Blemish_back.jpg;rm /data/Blemish_front.jpg;");
	if(ret != 0)
	{
		FT_LOG("[%s,%d] Error,delete picture fail\n",__func__,__LINE__);
		return -1;
	}

	FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
	return 0;
}

int ft_camera_stop_preview_lte()
{
	int ret = -1;

	ret = system("input tap  1260 550");
	return ret;
}


int ft_camera_lte_wake()
{
	int ret = -1;
	ret = system("input keyevent 26");
	return ret;
}
int ft_camera_lte_unlock()
{
	int ret = -1;

	ret = system("input swipe 375 870 600 870");
	return ret;
}

