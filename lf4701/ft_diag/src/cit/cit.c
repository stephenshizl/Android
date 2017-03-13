#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>

int ft_read_cit_flag(char *value)
{
    FILE *fp;
    char str[90];
    if ((fp = fopen("/data/manualresult.txt", "r")) == NULL)
    {
        printf("open file error\n");
        return -1;
    }
    fgets(str,90,fp);
    strcpy(value, str);
    return 1;
}
