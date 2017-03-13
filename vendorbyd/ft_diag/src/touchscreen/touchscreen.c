#include "include/ft.h"
#include "include/ft_sys.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
/********************************************
 * Description:
 * Proximity Sensor test
 *
 * Onput parameters:
 * out param:
 *      reference:pointer to return TouchScreen reference value
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_touchscreen_get_ref(char *reference)
{
    int fd =  - 1;
    int ret =  - 1;
    char tmp[4096] = {0};
    char *data = NULL;
    fd = open(TP_RAW_DATA_PATH, O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s,%d] Error,open %s failed\n", __func__, __LINE__,TP_RAW_DATA_PATH);
        return  - 1;
    }
    ret = read(fd, tmp, 4096);
    if (ret < 0)
    {
        FT_LOG("[%s,%d] Errot read data from %s failed\n", __func__, __LINE__,TP_RAW_DATA_PATH);
        close(fd);
        return  - 1;
    }
    data = strstr(tmp, "Reference data:");
    data += strlen("Reference data:\n");
    FT_LOG("[%s,%d] After arrange, data is :%s\n", __func__, __LINE__, data);
    strncpy(reference, data, ret);
    close(fd);
    return 0;
}
int ft_tp_get_diagapptp()
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = system("am start -n com.qti.factory/com.qti.factory.TouchPanelEdge.TouchPanelEdge_BYD");
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}
//yuhongxia add tp diag 20140519
/********************************************
 * Description:
 * Set TouchPanel function enable/disable
 *
 * Input parameters:
 * in param:
 *      mode: 0 set TP disable, 1 set TP enable
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_touchscreen_func_control(int mode)
{
    int fd =  - 1;
    int ret =  - 1;
    char mod[10] = {0};
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] mode:%d\n", __func__, __LINE__, mode);
    fd = open(TP_FUNC_CONTROL_PATH, O_WRONLY);
    if (fd < 0)
    {
        FT_LOG("[%s,%d] Error,open %s failed\n", __func__, __LINE__, TP_RAW_DATA_PATH);
        return  - 1;
    }
    sprintf(mod, "%d", mode);
    ret = write(fd, mod, 1);
    close(fd);
    if (ret != 1)
    {
        FT_LOG("[%s,%d] Write mode failed\n", __func__, __LINE__);
        return  - 1;
    }
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
