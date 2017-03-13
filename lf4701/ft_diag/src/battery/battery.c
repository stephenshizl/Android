#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>

int ft_pv_test(char *value)
{
    FILE *fp;
    char str[9];
    if ((fp = fopen("/sys/class/power_supply/battery/capacity", "r")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    fgets(str,9,fp);
    strcpy(value, str);
    return 1;
}
