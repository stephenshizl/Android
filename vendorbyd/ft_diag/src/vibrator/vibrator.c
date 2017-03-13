#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <hardware_legacy/vibrator.h>
//#include <hardware/vibrator.h>
static struct vibrator_module *gVibraModule;
/*-----------------------------------------------------------------
 * Vibrator test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * start vibrate
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_vibrator_start()
{
    int ret =  - 1;
    /*
    int fd, n;
    char enable[]="";
    char enable_cmd[128];
    int TIME = 10000;
    FT_LOG("[%s:%d] start...\n",__func__,__LINE__);
    if(!gVibraModule){
    FT_LOG("[%s,%d] Prepare load %s module\n",__func__,__LINE__,VIBRATOR_HARDWARE_MODULE_ID);
    ret = hw_get_module(VIBRATOR_HARDWARE_MODULE_ID, (hw_module_t const**)&gVibraModule);
    if(ret){
    FT_LOG("[%s,%d] Load %s error,module:%s\n",__func__,__LINE__,VIBRATOR_HARDWARE_MODULE_ID,strerror(-ret));
    return -1;
    }
    }
    ret = gVibraModule->vibrator_on(TIME);
    FT_LOG("[%s:%d] ret=%d\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d] end...\n",__func__,__LINE__);
    */
    int TIME = 100000;
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    ret = vibrator_on(TIME);
    FT_LOG("[%s:%d] ret=%d\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d] end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * stop vibrate
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_vibrator_stop()
{
    int ret =  - 1;
    /*
    int fd, n;
    char enable[]="";
    char enable_cmd[128];
    FT_LOG("[%s:%d] start...\n",__func__,__LINE__);
    if(!gVibraModule){
    FT_LOG("[%s,%d] Prepare load %s module\n",__func__,__LINE__,VIBRATOR_HARDWARE_MODULE_ID);
    ret = hw_get_module(VIBRATOR_HARDWARE_MODULE_ID, (hw_module_t const**)&gVibraModule);
    if(ret){
    FT_LOG("[%s,%d] Load %s error,module:%s\n",__func__,__LINE__,VIBRATOR_HARDWARE_MODULE_ID,strerror(-ret));
    return -1;
    }
    }
    ret = gVibraModule->vibrator_off();
    FT_LOG("[%s:%d] ret=%d\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d] end...\n",__func__,__LINE__);
     */
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    ret = vibrator_off();
    FT_LOG("[%s:%d] ret=%d\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d] end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Proximity Sensor test
 *
 * Input parameters:
 * in param:
 *      level:vibrate level
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_vibrator_set_level(int level)
{
    int ret =  - 1;
    int fd =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    return ret;
}
