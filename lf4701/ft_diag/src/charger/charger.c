#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

/********************************************
* Description: 
* Get the battery's capacity
* 
* Input parameters:
* out param:
*     cap: return the battery's capacity
* 
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_battery_read_cap(int *cap)
{
    int ret = -1;
    int cur_val = 0, vol_val = 0, len = 0;
    int fd = 0;
    char tmp[10] = {0};

    FT_LOG("[%s:%d] start...\n",__func__,__LINE__);
    fd = open(BATTERY_CAP_PATH, O_RDONLY);
    if (fd < 0) 
    {
        FT_LOG("[%s:%d]Error opening  the current_now file\n",__func__,__LINE__);
        return -1;
    }
    else 
    {
        memset(tmp, 0, 10);
        len = read(fd, tmp, 10);
        tmp[len] = '\0';
        *cap = atoi(tmp);
        FT_LOG("[%s:%d]The battery's capacity: %d \n",__func__,__LINE__, *cap);
    }
    close(fd);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}