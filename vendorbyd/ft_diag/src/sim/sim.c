#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <cutils/properties.h>
/********************************************
 * Description:
 * Sim card detect test
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_sim_detect_test()
{
    int ret =  - 1;
    return ret;
}
/********************************************
 * Description:
 * Sim card read/write test
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_sim_IO_test()
{
    int ret =  - 1;
    return ret;
}

//yuhongxia add sim status diag 20140519
/*int ft_simapp_read_status()
{
    FILE *fp;
    int ret =  - 1;
    system("am start -n com.qti.factory/com.qti.factory.diag.SIM1");
    if ((fp = fopen("/data/simappstatus.txt", "r+")) == NULL)
    {
        printf("open file error\n");
        return  - 1;
    }
    if (fgetc(fp) == '1')
    {
        ret = 1;
    }
    else if (fgetc(fp) == '0')
    {
        ret = 0;
    }
    else
    {
        return  - 1;
    }
    fclose(fp);
    return ret;
}*/


int ft_simapp_read_status(char *simstatus)
{
    int ret =  - 1;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ret = property_get("gsm.sim.state", simstatus,"NULL");
    FT_LOG("[%s,%d] sim1 sim2 status is:%s", __func__, __LINE__, simstatus);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return ret;
}