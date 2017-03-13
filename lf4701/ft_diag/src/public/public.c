//#include "include/ft.h"
#include "include/ft_sys.h"
#include "include/public.h"
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
/*get input number by name*/
int find_inputNum(char *name)
{
    int i = 0;
    int j = 0;
    char cmd[256] = {0};
    char env_name[128] = {0};
    int fd = -1;
    int name_len = 0;
    for(i ; i<20 ; i++)
    {
        sprintf(cmd,SENSOR_PATH"%d/name",i);
        fd = open(cmd,O_RDONLY);
        if(fd<0)
        {
            printf("Open %s Failed\n",cmd);
            continue;
        }
        name_len = strlen(name);
        read(fd,env_name,name_len);
        if(memcmp(env_name, name, name_len) != 0)
        {
            printf("the %s is :%s\n",cmd,env_name);
            memset(env_name,0,128);
            continue;
        }
        close(fd);
        memset(cmd, 0, 256);
        fd = -1;
        break;
    }
    j = (i == 20)? -1 : i;
    return j;
}