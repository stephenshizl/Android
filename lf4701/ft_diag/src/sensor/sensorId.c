#include "include/ft.h"
#include<pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


int ft_PLsensor_get__id(char *id)
{
    int ret = -1;
    int fd = -1;
    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    fd = open(PLSENSOR_MANUID,O_RDONLY);
    if (fd < 0)
    {
        FT_LOG("[%s:%d]Error opening the file \n",__func__,__LINE__);
        return -1;
    }
    ret = read(fd,id,10);
    if (ret < 0)
    {
        FT_LOG("[%s:%d]failed to read the file \n",__func__,__LINE__);
        return -1;
    }
    close(fd);
    id[strlen(id)-1] = '\0';
    FT_LOG("[%s:%d]end...\n",__func__,__LINE__);
    return 0;
}