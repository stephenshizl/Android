#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>

int ft_start_psensor_test()
{
    system("am start -n com.tools.psensorcalibration/com.tools.psensorcalibration.MainActivity");
    system("echo 1 > sys/class/sensors/cm36686-proximity/enable");
    return 1;
}

int ft_psensor_2cm_test()
{
    system("am broadcast -a COM.TOOLS.PLSENSOR_2CM");
    return 1;
}

int ft_psensor_4cm_test()
{
    system("am broadcast -a COM.TOOLS.PLSENSOR_4CM");
    return 1;
}

int ft_stop_psensor_test()
{
    system("am broadcast -a COM.TOOLS.PLSENSOR.FINISH");
    system("echo 0 > sys/class/sensors/cm36686-proximity/enable");
    return 1;
}

int ft_read_2cm_test(char *value)
{
    FILE *fp;
    char str[4];
    if ((fp = fopen("/data/psensor_2cm", "r")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    fgets(str,4,fp);
    strcpy(value, str);
    return 1;
}

int ft_read_4cm_test(char *value)
{
    FILE *fp;
    char str[4];
    if ((fp = fopen("/data/psensor_4cm", "r")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    fgets(str,4,fp);
    strcpy(value, str);
    return 1;
}