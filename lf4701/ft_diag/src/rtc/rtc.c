#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>

int ft_rtc_get(char *value)
{
    FILE *fp;
    char str[9];
    if ((fp = fopen("/sys/class/rtc/rtc0/time", "r")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    fgets(str,9,fp);
    printf("-------------------%s\n",str);
    strcpy(value, str);
    return 1;
}
