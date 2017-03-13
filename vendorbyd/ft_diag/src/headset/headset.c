#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>
#include <linux/input.h>
#include <errno.h>
#include <unistd.h>
/********************************************
 * Description:
 * Check if the headset is plugged
 *
 * Output parameters:
 * out param:
 *      status: 1: plugged
 *                  0: not plugged
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_headset_status(int *status)
{
    int ret =  - 1;
    char headset_status[128], buf[4];
    FILE *fp = NULL;
    FT_LOG("[%s:%d] ft_headset_status start...\n", __func__, __LINE__);
    sprintf(headset_status, "cat %s", SYS_HEADSET_STATUS);
    fp = popen(headset_status, "r");
    if (NULL == fp)
    {
        FT_LOG("[%s:%d] [Headset]: Error to popen. \n", __func__, __LINE__);
        return ret;
    }
    fgets(buf, sizeof(buf) - 1, fp);
    //  2: have no mic. 1: have a mic.
    if (!strncmp(buf, " 2", strlen(" 2")) || !strncmp(buf, " 1", strlen(" 1"))
        || !strncmp(buf, " 3", strlen(" 3")))
    {
        FT_LOG("[%s:%d] [Headset]: Insert\n", __func__, __LINE__);
        *status = 1;
        ret = 0;
    }
    if (!strncmp(buf, " 0", strlen(" 0")))
    {
        FT_LOG("[%s:%d] [Headset]: Removed\n", __func__, __LINE__);
        *status = 0;
        ret = 0;
    }
    pclose(fp);
    FT_LOG("[%s:%d] status=%d\n", __func__, __LINE__,  *status);
    FT_LOG("[%s:%d] ft_headset_status end...\n", __func__, __LINE__);
    return ret;
}
