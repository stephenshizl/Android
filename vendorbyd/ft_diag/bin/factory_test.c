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
#include "factory_test.h"
#include "../src/include/ft.h"
//#include "nv.h"
#include "cutils/properties.h"
#include <fcntl.h>
/************Version test******************/
PACK(void*)PTAPI_SW_VER_get_internal(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    result = ft_swVer_get__InternalVer(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, version);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI__SW_VER_get_external(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    result = ft_swVer_get__ExternalVer(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, version);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    printf("diagpkt_commit:%s\n", log);
    diagpkt_commit((PACK(void*))ft_pkt);
    return ft_pkt;
}
PACK(void*)PTAPI_HW_VER_get(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    result = ft_hwVer_get__Ver(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    if (version != NULL)
    {
        sprintf(log, "%d%s", result, version);
    }
    else
    {
        sprintf(log, "%d", result);
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_HW_VER_set(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    *version = (char*)req_pkt + 4;
    result = ft_hwVer_set__Ver(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_TYPE_DESIGNATOR_get(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    result = ft_type_designator_get(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    if (version != NULL)
    {
        sprintf(log, "%d%s", result, version);
    }
    else
    {
        sprintf(log, "%d", result);
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_VARIANT_CODE_get(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    result = ft_variant_code_get(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    if (version != NULL)
    {
        sprintf(log, "%d%s", result, version);
    }
    else
    {
        sprintf(log, "%d", result);
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_VARIANT_VER_get(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char version[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
    //result =  property_get( "ro.build.display.id.iversion", version, "" );
    result = ft_variant_ver_get(version);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    if (version != NULL)
    {
        sprintf(log, "%d%s", result, version);
    }
    else
    {
        sprintf(log, "%d", result);
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
//Adb test
PACK(void*)PTAPI_ADB_enable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_adb_set_enable();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    return ft_pkt;
}
PACK(void*)PTAPI_ADB_disable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_adb_set_disable();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    return ft_pkt;
}
//Camera test
PACK(void*)PTAPI_CAMERA_start_preview(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int cam_type = (int)(*(char*)temp);
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_camera_start_preview(cam_type);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    return ft_pkt;
}
PACK(void*)PTAPI_CAMERA_capture_default(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_camera_default_capture();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, rsp);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_capture_mtf(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_camera_MTF_capture();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, rsp);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_capture_blemish(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_camera_Blemish_capture();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, rsp);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_delete_photo(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt->my_rsp, 0, sizeof(ft_pkt->my_rsp));
    printf("Begin:  %s\n", __func__);
    result = ft_camera_del_photo();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("diagpkt_commit:%s\n", log);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_stop_preview(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_camera_stop_preview();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_lte_start_preview(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int cam_type = (int)(*(char*)temp);
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_camera_start_preview_lte(cam_type);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    return ft_pkt;
}
PACK(void*)PTAPI_CAMERA_lte_stop_preview(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_camera_stop_preview_lte();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_lte_capture(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_camera_default_capture_lte();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, rsp);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_lte_deletepic(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt->my_rsp, 0, sizeof(ft_pkt->my_rsp));
    printf("Begin:  %s\n", __func__);
    result = ft_camera_del_photo_lte();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("diagpkt_commit:%s\n", log);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_lte_wake(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt->my_rsp, 0, sizeof(ft_pkt->my_rsp));
    printf("Begin:  %s\n", __func__);
    result = ft_camera_lte_wake();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("diagpkt_commit:%s\n", log);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CAMERA_lte_unlock(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt->my_rsp, 0, sizeof(ft_pkt->my_rsp));
    printf("Begin:  %s\n", __func__);
    result = ft_camera_lte_unlock();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("diagpkt_commit:%s\n", log);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_DISPLAY_color_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    unsigned char *color = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    color = (unsigned char*)req_pkt + 4;
    int type = (int) *color;
    printf("Begin:  %s\n", __func__);
    result = ft_display_test(type);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_DISPLAY_color_test_lte(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    unsigned char *color = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    color = (unsigned char*)req_pkt + 4;
    int type = (int) *color;
    printf("Begin:  %s\n", __func__);
    result = ft_display_test_lte(type);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
/********vibrator test*********/
PACK(void*)PTAPI_VIBRATOR_test_start(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_vibrator_start();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_VIBRATOR_test_stop(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_vibrator_stop();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_BACKLIGHT_level_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    unsigned char *level = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    level = (unsigned char*)req_pkt + 4;
    int bright = (int) *level;
    printf("Begin:  %s\n", __func__);
    result = ft_backlight_setLevel(bright);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}


PACK(void*)PTAPI_LED_level_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    unsigned char *ledcolor = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ledcolor = (unsigned char*)req_pkt + 4;
    int color = (int) *ledcolor;
    printf("Begin:  %s\n", __func__);
    result = ft_led_setLevel(color);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_HEADSET_status_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int status = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_headset_status(&status);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d", result, status);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
/*******sdcard test*********/
PACK(void*)PTAPI_SDCARD_get_info(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int siInsert = 0;
    int totalSpace = 0;
    int freeSpace = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_sdcard_get_info(&siInsert, &totalSpace, &freeSpace);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d,%d,%d", result, siInsert, totalSpace, freeSpace);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", data);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_SDCARD_io_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int status = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_sdcard_IO_test();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
/*internal sdcard test*/
PACK(void*)PTAPI_INTERNAL_SDCARD_get_info(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    double totalSpace = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt));
    printf("Begin:  %s\n", __func__);
    result = ft_internal_sdcard_get_info(&totalSpace);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%.2f", result, totalSpace);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", data);
    return (PACK(void*))ft_pkt;
}
/*******keypad test********/
PACK(void*)PTAPI_KEYPAD_power_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int key = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_key_test_Power(&key);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d", result, key);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_KEYPAD_volume_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int key = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_key_test_Volume(&key);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d", result, key);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_KEYPAD_hook_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int key = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_key_test_Hook(&key);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d", result, key);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_KEYPAD_back_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_key_test_Back();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
//lsensor test
PACK(void*)PTAPI_LSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_lsensor_open();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_LSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int value = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_lsensor_get_value(&value);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d", result, value);
    FT_LOG("PTAPI_LSENSOR_get_value data:%s  result:%d  value:%d\n", data, result, value);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_LSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_lsensor_close();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
/*********Psensor test***********/
PACK(void*)PTAPI_PSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_psensor_open();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    int value = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_psensor_get_value(&value);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d", result, value);
    FT_LOG("PTAPI_PSENSOR_get_value is data:%s result:%d value:%d \n", data, result, value);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_psensor_close();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PLSENSOR_calibration(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PLSENSOR_calibration_auto(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    char addr[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof (ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration_auto(addr);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PLSENSOR_calibration_read2cm(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    char addr[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration_read2cm(addr);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PLSENSOR_calibration_read4cm(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    char addr[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration_read4cm(addr);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_EFUSE_check(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[380] = {0};
    char addr[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_efuse_check(addr);
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
PACK(void*)PTAPI_LSENSOR_adb_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[380] = {0};
    char addr[380] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_get_lsensor_data(addr);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_PSENSOR_adb_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[380] = {0};
    char addr[380] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_get_psensor_data(addr);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
/************Gsensor test****************/
PACK(void*)PTAPI_GSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_gsensor_open();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_GSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    float x = 0.0;
    float y = 0.0;
    float z = 0.0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_gsensor_get_value(&x, &y, &z);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%f,%f,%f", result, x, y, z);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_GSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_gsensor_close();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_GSENSOR_adb_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[380] = {0};
    char addr[380] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_get_gsesnor_data(addr);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
/***********WIFI test************/
PACK(void*)PTAPI_WIFI_open_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    ft_wifi_open();
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_close_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    ft_wifi_close();
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_scan_ssid(PACK(void*)req_pkt, unsigned short pkt_len)
{
    char log[512] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    ft_wifi_scan_ssid(log, sizeof(log));
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    // printf("ft_pkt",ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_tx_set(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int mode = (int) *temp;
    int channel = (int)*(temp + 1);
    int rate = (int)*(temp + 2);
    int power_level = (int)*(temp + 3);
    int sine_wave = (int)*(temp + 4);
    char log[20] = {0};
    printf("Begin: mode: %d; power_level:%d;channel: %d,rate :%d\n", mode, power_level, channel, rate);
    ft_wifi_tx_set(mode, channel, rate, power_level, sine_wave);
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_tx_start(PACK(void*)req_pkt, unsigned short pkt_len)
{
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    ft_wifi_tx_start();
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_tx_close(PACK(void*)req_pkt, unsigned short pkt_len)
{
    char log[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    ft_wifi_tx_stop();
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_rx_set(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int mode = (int) *temp;
    int channel = (int)*(temp + 1);
    int rate = (int)*(temp + 2);
    char log[20] = {0};
    printf("Begin: mode: %d; channel: %d; rate :%d\n", mode, channel, rate);
    ft_wifi_rx_set(mode, channel, rate);
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_rx_report(PACK(void*)req_pkt, unsigned short pkt_len)
{
    char log[512] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    ft_wifi_rx_report(log, sizeof(log));
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_check_address(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char addr[50] = {0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    result = wifi_check_address(addr);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, addr);
    if (ft_pkt != NULL)
    {
        memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(ft_pkt->my_rsp, log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_calibration_update(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    wifi_cal_update();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_bdata_backup(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    wifi_bdata_backup();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_WIFI_bdata_restore(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    wifi_bdata_restore();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void *) PTAPI_WIFI_test_init(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int res = -1;
    char result[20] = {0};

    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));

    if (ft_pkt == NULL)
    {
        return NULL;
    }

    res = atheros_wifi_init();
    FT_LOG("[%s,%d] factory  res = %d\n",__func__,__LINE__,res);
    if (res == 0)
    {
        res = 1;
    }
    else
    {
        res = 0;
    }
    sprintf(result,"%d",res);
    FT_LOG("[%s,%d] factory  result = %s\n",__func__,__LINE__,result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt *)ft_pkt)->my_rsp,result);
    return ft_pkt;
}

/******Data test*********/
PACK(void*)PTAPI_DATA_restore_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char addr[50] = {0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    result = ft_clear_flash(INTERNAL_FLASH_PATH);
    if (result >= 0)
    {
        ft_clear_facRestore();
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_DATA_lastlog_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret =  - 1;
    char *result = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int mode = (int) *temp; // 0,delete;1,detect
    if (mode == 0)
    {
        ret = ft_lastlog_delete();
    }
    else if (mode == 1)
    {
        ret = ft_lastlog_detect();
    }
    else
    {}
    if (ret >= 0)
    {
        result = "1";
    }
    else
    {
        result = "0";
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, result);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_DATA_dataflag_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret =  - 1;
    char *result = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int mode = (int) *temp; // 0,delete;1,detect
    if (mode == 0)
    {
        ret = ft_dataflag_creat();
    }
    else if (mode == 1)
    {
        ret = ft_dataflag_detect();
    }
    else
    {}
    if (ret >= 0)
    {
        result = "1";
    }
    else
    {
        result = "0";
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, result);
    return (PACK(void*))ft_pkt;
}
/*******sensorId test*********/
PACK(void*)PTAPI_SENSOR_ID_get(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char id[10] = {0};
    char log[10] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    result = ft_PLsensor_get__id(id);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, id);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
/********Awake test********/
PACK(void*)PTAPI_AWAKE_set_enable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ft_awake_test_enable();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_AWAKE_set_disable(PACK(void*)req_pkt, unsigned short pkt_len)
{    
    int ret = -1;
    char *result = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ret = ft_awake_test_disable();
    if (ret >= 0)
    {
        result = "1";
    }
    else
    {
        result = "0";
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, result);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_AWAKE_set_sleep(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret =  - 1;
    char *result = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int mode = (int) *temp; // 0,sleep;1,wake up
    ret = ft_awake_test_sleep();
    if (ret >= 0)
    {
        result = "1";
    }
    else
    {
        result = "0";
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, result);
    return (PACK(void*))ft_pkt;
}
/********FM test********/
PACK(void*)PTAPI_FM_set_enable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    FM_Enable();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_FM_stereo_enable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    Enable_FM_Stereo();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_FM_stereo_disable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    Disable_FM_Stereo();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_FM_set_volume(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int volume = (int) *temp;
    char log[20] = {0};
    printf("Begin: volume: %d\n", volume);
    FM_Set_Volume(volume);
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_FM_set_channel(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int channle = (int) *temp;
    char log[20] = {0};
    printf("Begin: channle: %d\n", channle);
    FM_Set_Channel(channle);
    sprintf(log, "%d", 1);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, log);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTPAI_FM_set_disable(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    FM_Disable();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
/********BT Test********/
PACK(void*)PTAPI_BT_get_addr(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[80] = {0};
    char addr[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = bt_get_addr_hci(addr);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_BT_test_init(PACK(void*)req_pkt, unsigned short pkt_len)
{
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ft_bt_test_init();
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, "1");
    return (PACK(void*))ft_pkt;
}
/********Flash Test********/
PACK(void*)PTAPI_FLASH_FLASH(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret = 0;
    char result[20] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ret = ft_flash_test_flash();
    if (ret > 0)
    {
        ret = 1;
    }
    else
    {
        ret = 0;
    }
    sprintf(result, "%d", ret);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, result);
    return ft_pkt;
}
PACK(void*)PTAPI_FLASH_TORCH(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret = 0;
    char result[20] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ret = ft_flash_test_torch();
    if (ret > 0)
    {
        ret = 1;
    }
    else
    {
        ret = 0;
    }
    sprintf(result, "%d", ret);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, result);
    return ft_pkt;
}
PACK(void*)PTAPI_FLASH_OFF(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret = 0;
    char result[20] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ret = ft_flash_test_off();
    if (ret > 0)
    {
        ret = 1;
    }
    else
    {
        ret = 0;
    }
    sprintf(result, "%d", ret);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, result);
    return ft_pkt;
}
/******* TP test********/
PACK(void*)PTAPI_TP_get_ref(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char rsp[4096] = {0};
    char reference[4096] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt) + 2560);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_touchscreen_get_ref(reference);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d%s", result, reference);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    return ft_pkt;
}
PACK(void*)PTAPI_TP_get_diagapptp(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_tp_get_diagapptp();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
//yuhongxia add for tp disable/enable diag 20140519 begin
PACK(void*)PTAPI_TP_func_ctrl(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret =  - 1;
    char *result = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int mode = (int) *temp;
    FT_LOG("[%s,%d] mode:%d\n", __func__, __LINE__, mode);
    ret = ft_touchscreen_func_control(mode);
    if (ret >= 0)
    {
        result = "1";
    }
    else
    {
        result = "0";
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, result);
    return (PACK(void*))ft_pkt;
}
//yuhongxia add for tp disable/enable diag 20140519 end
/********Charger test********/
PACK(void*)PTAPI_CHARGER_get_cap(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char tmp[10] = {0};
    int cap = 0;
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_battery_read_cap(&cap);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(tmp, "%d%d", result, cap);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, tmp);
    return ft_pkt;
}
PACK(void*)PTAPI_CHARGER_get_vol(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char tmp[10] = {0};
    float vol = 0.0;
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    result = ft_battery_read_voltage(&vol);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(tmp, "%d%.3f", result, vol);
    if (ft_pkt != NULL)
    {
        memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
        strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, tmp);
    }
    return ft_pkt;
}
/*add by yu.hongxia@byd.com for get battery current and temperature at 20150111*/
PACK(void*)PTAPI_CHARGER_get_current(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char tmp[10] = {0};
    long current = 0;
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_battery_read_current(&current);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(tmp, "%d%d", result, current);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, tmp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return ft_pkt;
}
PACK(void*)PTAPI_CHARGER_func_ctrl(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int ret =  - 1;
    char *result = NULL;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int val = (int) *temp;
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    ret = ft_charger_control(val);
    if (ret >= 0)
    {
        result = "1";
    }
    else
    {
        result = "0";
    }
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, result);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_CHARGER_get_temp(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char tmp[10] = {0};
    float temp = 0.0;
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_battery_read_temp(&temp);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(tmp, "%d%.1f", result, temp);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, tmp);
    return ft_pkt;
}
/*end by wuxiaobo*/
//yuhongxia add for simcard status diag through call diagappsim1 begin
/********simapp test********/
PACK(void*)PTAPI_SIMAPP_get_status(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result =  - 1;
    char tmp[80] = {0};
    char simstatus[80] = {0};
   // int status = 0;
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt)+60);
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt) + 60);
   // result = ft_simapp_read_status(&status);
   result = ft_simapp_read_status(simstatus);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(tmp, "%d%s", result, simstatus);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, tmp);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", tmp);
    return ft_pkt;
}

/********audio test********/
PACK(void *) PTAPI_AUDIO_speaker_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    int frequence=(int)*(temp+2);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result = ft_audio_speaker_test(duration, volume, frequence);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_AUDIO_headsetMic_to_headsetRecv(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_headsetMic_to_headsetRecv(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_receiver_test(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    int frequence=(int)*(temp+2);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
   result =ft_audio_receiver_test(duration, volume, frequence);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_phoneMic_to_headsetRecv(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    int frequent=(int)*(temp+2) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_phoneMic_headsetRecv(duration, volume, frequent);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_mainMic_to_speaker(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_mainMic_speaker(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_secondMic_to_speaker(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_secondMic_speaker(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_clean_status(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_clean_status();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_AUDIO_breakoff_audio_access(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_breakoff_audio_access();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void*)PTAPI_PLSENSOR_calibration_2cm(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int first = (int) *temp;
    int second = (int)*(temp + 1);
    int third = (int)*(temp + 2);
    int forth = (int)*(temp + 3);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration_2cm(first, second, third, forth);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PLSENSOR_calibration_4cm(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int first = (int) *temp;
    int second = (int)*(temp + 1);
    int third = (int)*(temp + 2);
    int forth = (int)*(temp + 3);
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration_4cm(first, second, third, forth);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_PLSENSOR_calibration_delete(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_plsensor_calibration_delete();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    return (PACK(void*))ft_pkt;
}
//yuhongxia add for efuse status diag
PACK(void*)PTAPI_EFUSE_get_status(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[380] = {0};
    char addr[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    printf("Begin:  %s\n", __func__);
    int  fd;
    static char  cmdline[1024];

    fd = open("/proc/cmdline", O_RDONLY);
    if (fd >= 0) {
        result = read(fd, cmdline, sizeof(cmdline)-1 );
        FT_LOG("[%s,%d] result=%d...\n", __func__, __LINE__, result);
        if (result < 0) {
            result = 0;
            cmdline[result] = 0;
        }else {
            result = 1;
        }        
        close(fd);
    } else {
        cmdline[0] = 0;
    }
    memset(data, 0, sizeof(data));
    if( strstr(cmdline, "is_secure_fuse=1") != NULL){
        sprintf(data, "%d", 1);
    } else {
        sprintf(data, "%d", 0);
    }
    FT_LOG("[%s,%d] PTAPI_EFUSE_get_status data=%s...\n", __func__, __LINE__, data);
    //   sprintf(data,"%d",result);
    //sprintf(data, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
   
   }
//yuhongxia add for efuse status diag
//wuxiaobo add for
PACK(void*)PTAPI_POWER_off(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[20] = {0};
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_power_off_test();
    if (result == 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    return ft_pkt;
}
//end
/********hall test********/
PACK(void*)PTAPI_Hall_get_state(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[80] = {0};
    char state[80] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_hall_state_read(state);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%s", result, state);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, data);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", data);
    return ft_pkt;
}
/********runin test********/
PACK(void*)PTAPI_RUNIN_get_data(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[128] = {0};
    char data[128] = {0};
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int cam_type = (int)(*(char*)temp);
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    FT_LOG("[%s:%d] start... \n", __func__, __LINE__);
    result = ft_runin_get_data(cam_type, data);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    memset(rsp, 0, sizeof(rsp));
    sprintf(rsp, "%d%s", result, data);
    FT_LOG("[%s:%d] rsp = %s\n", __func__, __LINE__, rsp);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return ft_pkt;
}
PACK(void*)PTAPI_RUNIN_get_failed_item(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char rsp[128] = {0};
    char data[128] = {0};
    unsigned char *temp = (unsigned char*)req_pkt + 4;
    int cam_type = (int)(*(char*)temp);
    PACK(void*)ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    result = ft_runin_get_failed_item(cam_type, data);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(rsp, "%d%s", result, data);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return ft_pkt;
}
/*M-sensor test*/
PACK(void*)PTAPI_MSENSOR_get_data(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[256] = {0};
    char addr[256] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt));
    result = ft_get_msensor_data(addr);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, addr);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}

PACK(void*)PTAPI_MSENSOR_open_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_msensor_open();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_MSENSOR_get_value(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[200] = {0};
    int x = 0;
    int y = 0;
    int z = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_msensor_get_value(&x, &y, &z);
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d%d,%d,%d", result, x, y, z);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void*)PTAPI_MSENSOR_close_test(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    printf("Begin:  %s\n", __func__);
    result = ft_msensor_close();
    if (result >= 0)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
/*hwinfo test*/
PACK(void*)PTAPI_HWINFO_get_meminfo(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[256] = {0};
    char memsize[256] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt));
    result = hw_get_meminfo(memsize);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, memsize);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}
PACK(void*)PTAPI_HWINFO_get_cpuinfo(PACK(void*)req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char log[256] = {0};
    char cpuinfo[256] = {0};
    PACK(void*)ft_pkt = NULL;
    ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    memset(ft_pkt, 0, sizeof(ft_diag_res_pkt));
    result = hw_get_cpuinfo(cpuinfo);
    if (result == 1)
    {
        result = 1;
    }
    else
    {
        result = 0;
    }
    sprintf(log, "%d%s", result, cpuinfo);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(((ft_diag_res_pkt*)ft_pkt)->my_rsp, log);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("diagpkt_commit:%s\n", log);
    return ft_pkt;
}

/******** Keybox Test ********/
PACK(void *) PTAPI_KEYBOX_write_data(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    printf("Begin:  %s\n",__func__);
    if (ft_pkt == NULL)
    {
        return (PACK(void *)) 0;
    }
    result = ft_keybox_write_data();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}


PACK(void *) PTAPI_KEYBOX_check_data(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    printf("Begin:  %s\n",__func__);
    if (ft_pkt == NULL)
    {
        return (PACK(void *)) 0;
    }
    result = ft_keybox_check_data();
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

//add for cit 
PACK(void *) PTAPI_read_cit_flag(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char cit_value[100]={0};
    char response[100] = {0};
    int fd = -1;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }

    fd = open("/data/autoresult.txt", O_RDONLY);
    if ( fd >= 0) {
       FT_LOG("+++++++++read citresult+++++++++\n");
       read(fd, cit_value, sizeof(cit_value));
       close(fd);
    } else if ( fd < 0){
       FT_LOG("read citresult fail");
       close(fd);
    }
    result = fd;
    if(result>=0){
        result=1;
    }
    else {
        result=0;
    }   
    int i = 0;
    for(i = 0; i < strlen(cit_value); i++) { 
      if(cit_value[i] == '\n') cit_value[i] = '0'; 
    }
    FT_LOG("[%s,%d] read reult...%s...\n", __func__, __LINE__, cit_value);
    sprintf(response,"%d%s",result,cit_value);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,response);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_clear_cit_flag(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = -1;
    char response[20] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    result = system("echo 0 > /data/autoresult.txt");
    if(result>=0){
        result=1;
    }
    else {
        result=0;
    }
    sprintf(response,"%d",result);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,response);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}
//end add cit
pthread_t spkcalibration;
void *calibration()
{
    FT_LOG("[%s: %d] *set_spkcalibration cmd:...\n", __func__, __LINE__);
    int i = system("/system/xbin/get_spkcalibration");
    FT_LOG("[%s: %d] *set_spkcalibration after result=...\n", __func__, __LINE__, i);
    return (void *)0;
}
pthread_t spkcalibrationbroadcast;
void *sendbroadcast()
{
    FT_LOG("[%s: %d] *set_spkcalibration broadcast:...\n", __func__, __LINE__);
    int i = system("am broadcast -a COM.FTDIAG_TOOLS.SPKCALIBRATION");
    FT_LOG("[%s: %d] *set_spkcalibration broadcast after result=...\n", __func__, __LINE__, i);
    return (void *)0;
}

PACK(void *) PTAPI_spk_calibration(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    int ret = -1;
    char cmd[50];
    char response[20] = {0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int temperature=(int)*temp;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }
    FT_LOG("[%s,%d] PTAPI_spk_calibration before...\n", __func__, __LINE__);
    //sprintf(cmd, "/system/xbin/get_spkcalibration");
    sprintf(cmd,"echo %d > /userstore/spkcalibration_temp", temperature);
    FT_LOG("[%s,%d] PTAPI_spk_calibration temperature cmd:%s...\n", __func__, __LINE__, cmd);
    result = system(cmd);
    pthread_create(&spkcalibrationbroadcast, NULL, sendbroadcast, NULL);
    ret = pthread_create(&spkcalibration, NULL, calibration, NULL);
    FT_LOG("[%s,%d] PTAPI_spk_calibration after...\n", __func__, __LINE__);
    if(ret>=0){
      result = 1;
    } else {
      result = 0;
    }
    FT_LOG("[%s,%d] set_spkcalibration after...ret=%d\n", __func__, __LINE__, result);
    sprintf(response,"%d",result);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,response);
    }
    FT_LOG("[%s,%d] ret response after:%s...\n", __func__, __LINE__, response);
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
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
//runin result
PACK(void *) PTAPI_RUNIN_get_result(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char runin_result[5]={0};
    char log[50] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt==NULL)
    {
        printf("diagpkt_alloc fail!\n");
        return NULL;
    }

    result = ft_runin_get_result(runin_result);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(log,"%d%s",result,runin_result);
    FT_LOG("liutao [%s]:runin result2\n",log);
    if(ft_pkt !=NULL)
    {
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,log);
    }
    diagpkt_commit((PACK(void*))ft_pkt);
    printf("response:%s\n",ft_pkt->my_rsp);
    return (PACK(void *))ft_pkt;
}
//add by qianyan
PACK(void *) PTAPI_AUDIO_Mic_to_Speaker(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_headsetMic_to_Speaker(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_AUDIO_Mic_to_Receiver(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_headsetMic_to_Receiver(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_AUDIO_mic1_to_headset(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_mic1_to_headset(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}
PACK(void *) PTAPI_AUDIO_mic2_to_headset(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[20]={0};
    unsigned char *temp = (unsigned char *)req_pkt + 4;
    int duration=(int)*temp;
    int volume=(int)*(temp+1) ;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc ((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if(ft_pkt == NULL)
    {
        return NULL;
    }

    printf("Begin:  %s\n",__func__);
    result =ft_audio_mic2_to_headset(duration, volume);
    if(result>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    sprintf(data,"%d",result);
    memcpy ((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp,data);
    printf("response:%s\n",ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void *))ft_pkt;
}

PACK(void *) PTAPI_setenforce_off(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    
    printf("Begin:  %s\n", __func__);
    int  fd;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    fd = system("setenforce 0");
    if(fd>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    memset(data, 0, sizeof(data));
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
PACK(void *) PTAPI_setenforce_on(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[380] = {0};
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    
    printf("Begin:  %s\n", __func__);
    int  fd;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    fd = system("setenforce 1");
    if(fd>=0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    memset(data, 0, sizeof(data));
    sprintf(data, "%d", result);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}
//add for emmc upgrade
PACK(void *) PTAPI_emmc_upgrade(PACK(void *) req_pkt, unsigned short pkt_len)
{
    int result = 0;
    char data[380] = {0};
    int f = -1;
    char buffer[10];
    int upgrade = 0;
    ft_diag_res_pkt *ft_pkt = diagpkt_alloc((int)(*(char*)req_pkt), sizeof(ft_diag_res_pkt));
    if (ft_pkt == NULL)
    {
        return NULL;
    }
    
    printf("Begin:  %s\n", __func__);
    int  fd;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    fd = system("/system/bin/emmcupgrade");
    if(fd>0)
    {
        result=1;
    }
    else
    {
        result=0;
    }
    f = open("/data/upgrade",O_RDONLY);
    while(f==-1) {
        FT_LOG("[%s,%d] while...\n", __func__, __LINE__);
        f = open("/data/upgrade",O_RDONLY);
    }
    if ( f >= 0) {
            read(f, buffer, sizeof(buffer));
            upgrade = atoi(buffer);
            FT_LOG("[%s,%d] while..%d...\n", __func__, __LINE__, upgrade);
    }
    FT_LOG("[%s,%d] end...fd=%d..\n", __func__, __LINE__,fd);
    memset(data, 0, sizeof(data));
    sprintf(data, "%d%d", result,upgrade);
    FT_LOG("[%s,%d] end...result=%d..upgrade=%d..\n", __func__, __LINE__,result,upgrade);
    memcpy((void*)ft_pkt, (void*)req_pkt, pkt_len);
    strcpy(ft_pkt->my_rsp, data);
    printf("response:%s\n", ft_pkt->my_rsp);
    diagpkt_commit((PACK(void*))ft_pkt);
    return (PACK(void*))ft_pkt;
}