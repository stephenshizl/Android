#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <cutils/properties.h>
#include "include/ft.h"
#include "include/ft_sys.h"
/********************************************
 * Description:
 * Get sw ap internal version
 *
 * Input parameters:
 * out param:
 *      response_data: pointer to return AP internal version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_swVer_get__InternalVer(char *version)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
   /* if (access(SW_VERSION_SRC_FILE, F_OK) ==  - 1)
    {
        perror("[SW_VERSION]: There's no SW version file");
        FT_LOG("[%s:%d]There's no %s file\n", __func__, __LINE__,SW_VERSION_SRC_FILE);
        return ret;
    }*/
    ret = property_get("ro.build.lenovo.version.in", version, "NULL");//IN_APVER_PATH
    FT_LOG("[%s,%d] Internal Version number is:%s", __func__, __LINE__, version);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Get sw AP  external version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return AP external version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_swVer_get__ExternalVer(char *version)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    /*if (access(SW_VERSION_SRC_FILE, F_OK) ==  - 1)
    {
        perror("[SW_VERSION]: There's no SW version file");
        FT_LOG("[%s:%d]There's no %s file\n", __func__, __LINE__,SW_VERSION_SRC_FILE);
        return ret;
    }*/
    ret = property_get("ro.build.lenovo.version.ex", version, "NULL");//EX_APVER_PATH
    FT_LOG("[%s,%d] External Version number is:%s", __func__, __LINE__, version);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Get hardware version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return hardware version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_hwVer_get__Ver(char *version)
{
    int ret =  - 1;
    int fd;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    /*
    fd = open(HW_VERSION_PATH,O_RDONLY);
    if(fd == -1){
    FT_LOG("[%s,%d] Open the %s failed\n",__func__,__LINE__,HW_VERSION_PATH);
    return -1;
    }
    ret = read(fd,hwVer,100);
    if(ret < 0){
    FT_LOG("[%s,%d] Read Hardware Version failed\n",__func__,__LINE__);
    return -1;
    }
    close(fd);
    if(hwVer != NULL)
    sprintf(version,"%s",hwVer);
    FT_LOG("[%s,%d] The Hardware Version is:%s\n",__func__,__LINE__,version);
    */
    ret = property_get("ro.product.hwid", version, "NULL");
    FT_LOG("[%s,%d] Hardware version is:%s\n", __func__, __LINE__, version);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Get hardware version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return hardware version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_hwVer_set__Ver(char *version)
{
    int ret =  - 1;
    return ret;
}
/********************************************
 * Description:
 * Get type designator
 *
 * Input parameters:
 * out param:
 *      designator: pointer to return type designator
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_type_designator_get(char *designator)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = property_get("ro.product.name", designator, "NULL");
    FT_LOG("[%s,%d] type designator is:%s\n", __func__, __LINE__, designator);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Get variant code
 *
 * Input parameters:
 * out param:
 *      code: pointer to return variant code
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_variant_code_get(char *code)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = property_get("ro.product.variant.code", code, "NULL");
    FT_LOG("[%s,%d] variant code is:%s\n", __func__, __LINE__, code);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Get variant version
 *
 * Input parameters:
 * out param:
 *      version: pointer to return variant version
 *                     the array length > 1000
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_variant_ver_get(char *version)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = property_get("ro.product.variant.version", version, "NULL");
    FT_LOG("[%s,%d] variant version is:%s\n", __func__, __LINE__, version);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
