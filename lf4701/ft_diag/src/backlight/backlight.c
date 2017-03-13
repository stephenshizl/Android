#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

/********************************************
* Description: 
* Set playback volume
* 
* Input parameters:
* in param: 
*       level: the backlight level
*              from 0 to 255
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_backlight_setLevel(int level) 
{
    int ret = -1;
    char bl_level[256];

    FT_LOG("[%s:%d][DISPLAY]: ft_baclight_setLevel start...\n",__func__,__LINE__);
    FT_LOG("[%s:%d][DISPLAY]: input level=%d\n",__func__,__LINE__,level);
    if ( (level < 0) || (level > 255) )
    {
        FT_LOG("[%s:%d][DISPLAY]: Invaluable level.\b",__func__,__LINE__);
        return ret;
    }
    
    ret = access(SYS_BACKLIGHT_PATH, F_OK);
    if (ret == -1)
    {
        FT_LOG("[%s:%d][DISPLAY]: There's no backlight-level sys! \n",__func__,__LINE__);
        return ret;
    }

    memset(bl_level, 0, sizeof(bl_level));
    sprintf(bl_level, "echo %d > %s", level, SYS_BACKLIGHT_PATH);
    FT_LOG("[%s:%d][DISPLAY]: Set backlight level cmd is %s\n",__func__,__LINE__, bl_level);
    ret = system(bl_level);
    if (ret == -1)
    {
        FT_LOG("[%s:%d][DISPLAY]: Error to set backlight level.\n",__func__,__LINE__);
        return ret;
    }

    FT_LOG("[%s:%d][DISPLAY]: ret=%d\n",__func__,__LINE__,ret);
    FT_LOG("[%s:%d][DISPLAY]: ft_baclight_setLevel end...\n",__func__,__LINE__);
    return ret;
}