/****************************************************************************
 * factory_test.c:
 *     factory test item function define
 ***************************************************************************/

/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who          what, where, why
--------  -----------  ----------------------------------------------------
20140519  yuhongxia add tp disable/enable diag
20140519  yuhongxia add sim status diag
===========================================================================*/

#include <stdlib.h>
#include <stdio.h>
#include "factory_tes.h"
#include <cutils/properties.h>
#include "cutils/properties.h"
#define FT_LOG(...) (void)android_printLog( 4,"FT_TST",__VA_ARGS__)
char *activity_str[] = {
    "am start -n com.tools.cit/com.tools.cit.HomeActivity",
    "am start -n com.tools.cit/com.tools.cit.PhoneInfo",//1
    "am start -n com.tools.cit/com.tools.cit.FrontCameraTest",
    "am start -n com.tools.cit/com.tools.cit.CameraTest",
    "am start -n com.tools.cit/com.tools.cit.SIMTest",
    "am start -n com.tools.cit/com.tools.cit.AccelerometerTest",//5
    "am start -n com.tools.cit/com.tools.cit.ProximityTest",
    "am start -n com.tools.cit/com.tools.cit.LightTest",
    "am start -n com.tools.cit/com.tools.cit.TouchTest",
    "am start -n com.tools.cit/com.tools.cit.KeyTest",
    "am start -n com.tools.cit/com.tools.cit.LCDTest",//10
    "am start -n com.tools.cit/com.tools.cit.VibratorTest",
    "am start -n com.tools.cit/com.tools.cit.HeadsetTest",
    "am start -n com.tools.cit/com.tools.cit.MemoryCardTest",
    "am start -n com.tools.cit/com.tools.cit.ThreeColorTest",
    "am start -n com.tools.cit/com.tools.cit.BackLightTest",//15
    "am start -n com.tools.cit/com.tools.cit.WiFiTest",
    "am start -n com.tools.cit/com.tools.cit.CompassTest",
    "am start -n com.tools.cit/com.tools.cit.GyroscopeTest",
    "am start -n com.tools.cit/com.tools.cit.SignalTest",
    "am start -n com.tools.cit/com.tools.cit.BluetoothTest",//20
    "am start -n com.tools.cit/com.tools.cit.FlashLightTest",
    "am start -n com.tools.cit/com.tools.cit.ChargeTest",
    "am start -n com.tools.cit/com.tools.cit.GPSTest",
    "am start -n com.tools.cit/com.tools.cit.AUDIO",
    };
    
char *broadcast_front_camera[] = {
    "am broadcast -a com.tools.AUTOCIT.TAKE_PICTURE_FRONT",
    "am broadcast -a com.tools.AUTOCIT.CAMERA_BACK_END"
};
char *broadcast_back_camera[] = {
    "am broadcast -a com.tools.AUTOCIT.TAKE_PICTURE_BACK",
    "am broadcast -a com.tools.AUTOCIT.CAMERA_BACK_END"
};
char *broadcast_brightness[] = {
    "am broadcast -a com.tools.AUTOCIT.BRIGHTNESS_20",
    "am broadcast -a com.tools.AUTOCIT.BRIGHTNESS_50",
    "am broadcast -a com.tools.AUTOCIT.BRIGHTNESS_80"
};
char *broadcast_rgb[] = {
    "am broadcast -a com.tools.AUTOCIT.RGB_WHITE",
    "am broadcast -a com.tools.AUTOCIT.RGB_BLACK",
    "am broadcast -a com.tools.AUTOCIT.RGB_RED",
    "am broadcast -a com.tools.AUTOCIT.RGB_GREEN",
    "am broadcast -a com.tools.AUTOCIT.RGB_BLUE"
};
char *broadcast_light[] = {
    "am broadcast -a com.tools.AUTOCIT.LIGHT_RED",
    "am broadcast -a com.tools.AUTOCIT.LIGHT_GREEN",
    "am broadcast -a com.tools.AUTOCIT.LIGHT_BLUE"
};
char *broadcast_flash[] = {
    "am broadcast -a com.tools.AUTOCIT.FLASH_OPEN",
    "am broadcast -a com.tools.AUTOCIT.FLASH_CLOSE"
};

int ft_send_broadcast(int subsys_id, int broadcast_type)
{
    switch(subsys_id)
    {
        case 2:
            system(broadcast_front_camera[broadcast_type]);
            break;
        case 3:
            system(broadcast_back_camera[broadcast_type]);
            break;
        case 10:
            system(broadcast_rgb[broadcast_type]);
            break;
        case 15:
            system(broadcast_brightness[broadcast_type]);
            break;
        case 14:
            system(broadcast_light[broadcast_type]);
            break;
        case 21:
            system(broadcast_flash[broadcast_type]);
            break;
    }
    return 1;
}

int ft_start_test(char *cmd)
{

    int ret = -1;
    system(cmd);
    ret = 1;
    return ret;
}


int ft_get_result()
{
    FILE *fp;
    int ret = -1;
    if ((fp = fopen("/data/simappstatus.txt", "r+")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    int tmp = fgetc(fp);
    if(tmp == '1')
    {
        ret = 1;
    }
    else if(tmp == '0')
    {
        ret = 0;
    }
    else{return -1;}
    
    printf("ft_bt_get_result %d", ret);
    fclose(fp);
    return ret; 
}

PACK(void *) PTAPI_send_broadcast(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = -1;
    char tmp[10] = {0};
    char cmd_value[10] = {0};
    
    sprintf(cmd_value,"%d",((diagtestapp_subsys_hdr_v2_type *)req_pkt)->subsys_id);
    int index = atoi(cmd_value);
    
    unsigned char *temp = (unsigned char *)req_pkt +4;
    int brodcast_type = (int)(*(char *)temp);

    PACK(void *)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    result = ft_send_broadcast(index, brodcast_type);
    if(result == 0)
    {
        result = 1;
    }
    else if(result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }

    sprintf(tmp,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,tmp);
    }
    return ft_pkt;
}

PACK(void *) PTAPI_start_activity(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = -1;
    char tmp[10] = {0};
    int status = 0;
    char cmd_value[10] = {0};
    PACK(void *)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    property_set("persist.sys.autocit","1");
    sprintf(cmd_value,"%d",((diagtestapp_subsys_hdr_v2_type *)req_pkt)->subsys_id);
    int index = atoi(cmd_value);
    result = ft_start_test(activity_str[index]);
    if(result == 0)
    {
        result = 1;
    }
    else if(result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }

    sprintf(tmp,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,tmp);
    }
    return ft_pkt;
}

PACK(void *) PTAPI_get_result(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = -1;
    char tmp[10] = {0};
    int status = 0;
    PACK(void *)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    result = ft_get_result();
    if(result == 0)
    {
        result = 1;
        status = 0;
    }
    else if(result == 1)
    {
        result = 1;
        status = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(tmp,"%d%d",result, status);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,tmp);
    }
    return ft_pkt;
}


//RTC
PACK(void *) PTAPI_RTC_get(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char time_str[50]={0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }

    result = ft_rtc_get(time_str);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,time_str);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

/************Version test******************/
PACK(void *) PTAPI_SW_VER_get_internal(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[80]={0};
  char version[80]={0};
  PACK(void *)ft_pkt = NULL;
  ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt ) + 60);

  memset(ft_pkt,0,sizeof(ft_diag_res_pkt ) + 60);
  //result =  property_get( "ro.build.display.id.iversion", version, "" );
  result = ft_swVer_get__InternalVer(version);
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(log,"%d%s",result, version);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,log);
  }
    diagpkt_commit((PACK(void*))ft_pkt);
  printf("diagpkt_commit:%s\n",log);
  return ft_pkt;
}

PACK(void *) PTAPI__SW_VER_get_external(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[80]={0};
  char version[80]={0};
  PACK(void *)ft_pkt = NULL;
  ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt ) + 60);

  memset(ft_pkt,0,sizeof(ft_diag_res_pkt ) + 60);
  //result =  property_get( "ro.build.display.id.iversion", version, "" );
  result = ft_swVer_get__ExternalVer(version);
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(log,"%d%s",result, version);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,log);
  }
  printf("diagpkt_commit:%s\n",log);
    diagpkt_commit((PACK(void*))ft_pkt);
  return ft_pkt;
}

PACK(void *) PTAPI_HW_VER_get(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[80]={0};
  char version[80]={0};
  PACK(void *)ft_pkt = NULL;
  ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt ) + 60);

  memset(ft_pkt,0,sizeof(ft_diag_res_pkt ) + 60);
  //result =  property_get( "ro.build.display.id.iversion", version, "" );
  result = ft_hwVer_get__Ver(version);
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }
  if(version != NULL)
  {
    sprintf(log,"%d%s",result, version);
  }
  else
  {
    sprintf(log,"%d",result);
  }
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,log);
  }
  diagpkt_commit((PACK(void*))ft_pkt);
  printf("diagpkt_commit:%s\n",log);
  return ft_pkt;
}

PACK(void *) PTAPI_HW_VER_set(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[80]={0};
  char version[80]={0};
  PACK(void *)ft_pkt = NULL;
  ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt ) + 60);

  memset(ft_pkt,0,sizeof(ft_diag_res_pkt ) + 60);
  //result =  property_get( "ro.build.display.id.iversion", version, "" );
  *version = (char*)req_pkt +4;
  result = ft_hwVer_set__Ver(version);
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(log,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,log);
  }
  printf("diagpkt_commit:%s\n",log);
  return ft_pkt;
}

//Adb test
PACK(void *) PTAPI_ADB_enable(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  PACK(void *)ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_adb_set_enable();
  if(result>=0)
  {
    result=1;
  }
  else
  {
     result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,rsp);
  }

   return ft_pkt;


}

PACK(void *) PTAPI_ADB_disable(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  PACK(void *)ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_adb_set_disable();
  if(result>=0)
  {
    result=1;
  }
  else
  {
     result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,rsp);
  }
   return ft_pkt;
}

PACK(void *) PTAPI_unlock_screen(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
   char log[20]={0};
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt == NULL)
   {
     return NULL;
   }
   memset(ft_pkt->my_rsp,0,sizeof(ft_pkt->my_rsp));
   printf("Begin:  %s\n",__func__);
   result = ft_unlock_screen();
   if(result>=0)
   {
     result=1;
   }
   else
   {
     result=0;
   }
   sprintf(log,"%d",result);

   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("diagpkt_commit:%s\n",log);

   return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_sceen_wake(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
   char log[20]={0};
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt == NULL)
   {
     return NULL;
   }
   memset(ft_pkt->my_rsp,0,sizeof(ft_pkt->my_rsp));
   printf("Begin:  %s\n",__func__);
   result = ft_sceen_wake();
   if(result>=0)
   {
     result=1;
   }
   else
   {
     result=0;
   }
   sprintf(log,"%d",result);

   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("diagpkt_commit:%s\n",log);

   return (PACK(void *))ft_pkt;
}

//battery

PACK(void *) PTAPI_pv_back(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
    char value[50]={0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }

    result = ft_pv_test(value);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,value);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

//reset phone

PACK(void *) PTAPI_reset_phone(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  PACK(void *)ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result = ft_reset_phone();
  if(result>=0)
  {
    result=1;
  }
  else
  {
     result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,rsp);
  }
   return ft_pkt;
}

PACK(void *) PTAPI_rm_cit_flag(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  PACK(void *)ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result = ft_rm_cit_flag();
  if(result>=0)
  {
    result=1;
  }
  else
  {
     result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,rsp);
  }
   return ft_pkt;
}
/***********WIFI test************/
PACK(void *) PTAPI_WIFI_init_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  atheros_wifi_init();

  sprintf(log,"%d",1);
  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
  return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_WIFI_open_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  ft_wifi_open();

  sprintf(log,"%d",1);
  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
  return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_close_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  ft_wifi_close();

  sprintf(log,"%d",1);
  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
  return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_scan_ssid(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[512]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  ft_wifi_scan_ssid(log, sizeof(log));

  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
 // printf("ft_pkt",ft_pkt);
  return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_tx_set(PACK(void *) req_pkt, unsigned short pkt_len)
{
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt==NULL)
   {
     printf("diagpkt_alloc fail!\n");
     return NULL;
   }
   unsigned char *temp = (unsigned char *)req_pkt + 4;
   int mode=(int)*temp;
   int channel=(int)*(temp+1) ;
   int rate=(int)*(temp+2);
   int power_level=(int)*(temp+3);
   int sine_wave=(int)*(temp+4);
   char log[20]={0};

   printf("Begin: mode: %d; power_level:%d;channel: %d,rate :%d\n",mode,power_level,channel,rate);

   ft_wifi_tx_set(mode, channel, rate, power_level, sine_wave);

   sprintf(log,"%d",1);
   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("response:%s\n",ft_pkt->my_rsp);
   return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_tx_start(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  ft_wifi_tx_start();

  sprintf(log,"%d",1);
  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
  return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_tx_close(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  ft_wifi_tx_stop();

  sprintf(log,"%d",1);
  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
  return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_rx_set(PACK(void *) req_pkt, unsigned short pkt_len)
{
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt==NULL)
   {
     printf("diagpkt_alloc fail!\n");
     return NULL;
   }
   unsigned char *temp = (unsigned char *)req_pkt + 4;
   int mode=(int)*temp;
   int channel=(int)*(temp+1) ;
   int rate=(int)*(temp+2);
   char log[20]={0};

   printf("Begin: mode: %d; channel: %d; rate :%d\n",mode,channel,rate);

   ft_wifi_rx_set(mode, channel, rate);

   sprintf(log,"%d",1);
   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("response:%s\n",ft_pkt->my_rsp);
   return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_WIFI_rx_report(PACK(void *) req_pkt, unsigned short pkt_len)
{
  char log[512]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
  if(ft_pkt==NULL)
  {
    printf("diagpkt_alloc fail!\n");
    return NULL;
  }

  ft_wifi_rx_report(log, sizeof(log));

  memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
  strcpy(ft_pkt->my_rsp,log);
  printf("response:%s\n",ft_pkt->my_rsp);
  return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_WIFI_check_address(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char addr[50]={0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }

    result = wifi_check_address(addr);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,addr);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_WIFI_calibration_update(PACK(void *) req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    wifi_cal_update();
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,"1");
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_WIFI_bdata_backup(PACK(void *) req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    wifi_bdata_backup();
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,"1");
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_WIFI_bdata_restore(PACK(void *) req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    wifi_bdata_restore();
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,"1");
    return (PACK(void *))ft_pkt;
}
/********BT Test********/
PACK(void *) PTAPI_BT_get_addr(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[80]={0};
  char addr[80]={0};
  PACK(void *)ft_pkt = NULL;
  ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt ));

  result = bt_get_addr_hci(addr);
  if(result ==1)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(log,"%d%s",result, addr);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,log);
  }
  diagpkt_commit((PACK(void*))ft_pkt);
  printf("diagpkt_commit:%s\n",log);
  return ft_pkt;
}

PACK(void *) PTAPI_BT_test_init(PACK(void *) req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    ft_bt_test_init();
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,"1");
    return (PACK(void *))ft_pkt;
}

/********audioadb test********/

PACK(void *) PTAPI_AUDIOADB_PHONESPEAKER(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    int frequence=(int)*(temp+2);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

    printf("Begin:  %s\n",__func__);
 //  result =ft_audioadb_phonespk();
    result = ft_audioadb_phonespk(duration, volume, frequence);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_AUDIOADB_HEADSETMIC_HEADSETSPK(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

    printf("Begin:  %s\n",__func__);
   result =ft_audioadb_headsetmic_headsetspk(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIOADB_PHONERECEIVER(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    int frequence=(int)*(temp+2);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

    printf("Begin:  %s\n",__func__);
   result =ft_audioadb_phonereceiver(duration, volume, frequence);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIOADB_PHONEMIC_HEADSETRECEIVER(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

    printf("Begin:  %s\n",__func__);
   result =ft_audioadb_phonemic_headsetreceiver(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    return (PACK(void *))ft_pkt;
}

/*********Psensor test***********/
PACK(void *) PTAPI_PSENSOR_open_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    printf("Begin:  %s\n",__func__);
    result = ft_start_psensor_test();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_read_2cm_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
    char value[50]={0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    result = ft_read_2cm_test(value);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,value);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_read_4cm_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
    char value[50]={0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    result = ft_read_4cm_test(value);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,value);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_PSENSOR_close_test(PACK(void *) req_pkt, unsigned short pkt_len)
{

    int result = 0;
    char data[20]={0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    printf("Begin:  %s\n",__func__);
    result = ft_stop_psensor_test();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_PLSENSOR_calibration_2cm(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   result =ft_psensor_2cm_test();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_PLSENSOR_calibration_4cm(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   result =ft_psensor_4cm_test();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    if (ft_pkt != NULL)
    {
        memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp,data);
        printf("response:%s\n",ft_pkt->my_rsp);
    }
    return (PACK(void *))ft_pkt;
}
/*
//Camera test
PACK(void *) PTAPI_CAMERA_start_preview(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  unsigned char *temp = (unsigned char *)req_pkt +4;
  int cam_type = (int)(*(char *)temp);


  PACK(void *)ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_camera_start_preview(cam_type);
  if(result>=0)
  {
    result=1;
  }
  else
  {
     result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,rsp);
  }

   return ft_pkt;
}

PACK(void *) PTAPI_CAMERA_capture_default(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_camera_default_capture();
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,rsp);
    printf("response:%s\n",ft_pkt->my_rsp);
  }

  return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_CAMERA_capture_mtf(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_camera_MTF_capture();
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,rsp);
    printf("response:%s\n",ft_pkt->my_rsp);
  }

  return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_CAMERA_capture_blemish(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_camera_Blemish_capture();
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,rsp);
    printf("response:%s\n",ft_pkt->my_rsp);
  }

  return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_CAMERA_delete_photo(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
   char log[20]={0};
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt == NULL)
   {
     return NULL;
   }
   memset(ft_pkt->my_rsp,0,sizeof(ft_pkt->my_rsp));
   printf("Begin:  %s\n",__func__);
   result =ft_camera_del_photo();
   if(result>=0)
   {
     result=1;
   }
   else
   {
     result=0;
   }
   sprintf(log,"%d",result);

   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("diagpkt_commit:%s\n",log);

   return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_CAMERA_stop_preview(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  printf("Begin:  %s\n",__func__);
  result =ft_camera_stop_preview();
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(log,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    printf("response:%s\n",ft_pkt->my_rsp);
  }

  return (PACK(void *) )ft_pkt;
}*/

PACK(void *) PTAPI_CAMERA_lte_start_preview(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  unsigned char *temp = (unsigned char *)req_pkt +4;
  int cam_type = (int)(*(char *)temp);


  PACK(void *)ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_camera_start_preview_lte(cam_type);
  if(result>=0)
  {
    result=1;
  }
  else
  {
     result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,rsp);
  }

   return ft_pkt;
}
PACK(void *) PTAPI_CAMERA_lte_stop_preview(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char log[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  printf("Begin:  %s\n",__func__);
  result =ft_camera_stop_preview_lte();
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(log,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    printf("response:%s\n",ft_pkt->my_rsp);
  }

  return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_CAMERA_lte_capture(PACK(void *) req_pkt, unsigned short pkt_len)
{
  int result = 0;
  char rsp[20]={0};
  ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

  result =ft_camera_default_capture_lte();
  if(result>=0)
  {
    result=1;
  }
  else
  {
    result=0;
  }

  sprintf(rsp,"%d",result);
  if (ft_pkt != NULL)
  {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,rsp);
    printf("response:%s\n",ft_pkt->my_rsp);
  }

  return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_CAMERA_lte_deletepic(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
   char log[20]={0};
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt == NULL)
   {
     return NULL;
   }
   memset(ft_pkt->my_rsp,0,sizeof(ft_pkt->my_rsp));
   printf("Begin:  %s\n",__func__);
   result =ft_camera_del_photo_lte();
   if(result>=0)
   {
     result=1;
   }
   else
   {
     result=0;
   }
   sprintf(log,"%d",result);

   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("diagpkt_commit:%s\n",log);

   return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_CAMERA_lte_wake(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
   char log[20]={0};
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt == NULL)
   {
     return NULL;
   }
   memset(ft_pkt->my_rsp,0,sizeof(ft_pkt->my_rsp));
   printf("Begin:  %s\n",__func__);
   result =ft_camera_lte_wake();
   if(result>=0)
   {
     result=1;
   }
   else
   {
     result=0;
   }
   sprintf(log,"%d",result);

   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("diagpkt_commit:%s\n",log);

   return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_CAMERA_lte_unlock(PACK(void *) req_pkt, unsigned short pkt_len)
{
   int result = 0;
   char log[20]={0};
   ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
   if(ft_pkt == NULL)
   {
     return NULL;
   }
   memset(ft_pkt->my_rsp,0,sizeof(ft_pkt->my_rsp));
   printf("Begin:  %s\n",__func__);
   result =ft_camera_lte_unlock();
   if(result>=0)
   {
     result=1;
   }
   else
   {
     result=0;
   }
   sprintf(log,"%d",result);

   memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
   strcpy(ft_pkt->my_rsp,log);
   printf("diagpkt_commit:%s\n",log);

   return (PACK(void *))ft_pkt;
}

//cit flag
PACK(void *) PTAPI_read_cit_flag(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char cit_value[100]={0};
    char log[100] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }

    result = ft_read_cit_flag(cit_value);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,cit_value);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

/********Awake test********/
PACK(void *) PTAPI_AWAKE_set_enable(PACK(void *) req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    ft_awake_test_enable();
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,"1");
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_MIC(PACK(void *) req_pkt, unsigned short pkt_len)
{
    pthread_t audio_thread[9];

    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int type = (int)(*(char *)temp);
    
    int duration=(int)*(temp+1) ;
    int volume=(int)*(temp+2) ;
    int frequence=(int)*(temp+3);
    printf("------------------type:%d duration:%d volume:%d frequence:%d\n", type,duration,volume,frequence);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,"1");
    diagpkt_commit((PACK(void*))ft_pkt);
    ft_speaker_mic(type,duration,volume,frequence);
    return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_AUDIO_PINK(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char value[3]={0};
    char log[40] = {0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int type = (int)(*(char *)temp);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    FT_LOG("[%s,%d] PTAPI_AUDIO_PINK:%d\n",__func__,__LINE__,type);
    if(type == 1)
    {
        result = ft_speaker_f0_test1(value);
    }
    else if(type == 2)
    {
        result = ft_speaker_f0_test2(value);
    }
    
    sprintf(log,"%d%s",result,value);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_EFUSE_get_status(PACK(void *) req_pkt, unsigned short pkt_len)
{
int result = 0;
    char data[10] = {0};
    char addr[2] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_efuse_read_status(addr);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    //   sprintf(data,"%d",result);
    sprintf(data, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
