#include "include/ft.h"
#include "include/ft_sys.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>



int ft_audioadb_phonespk(char* duration,char* volume,char* frequence)
{
    int ret = 1;
    char cmd_buf[3000] = {0};

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%d,%d,%d] ft_audioadb_phonespk...\n",(int)duration,(int)volume,(int)frequence);
  //  ret=system("cd system/bin;mm-audio-ftm -tc 8 -c ftm_test_config -d 3 -v 70 -fl 2000 -fh 2000");

    volume=(int)volume*10;
    if((int)frequence == 8)
    {
	frequence=(int)frequence*100;
	sprintf(cmd_buf, "cd system/bin;mm-audio-ftm -tc 8 -c ftm_test_config_mtp -d %d -v %2d -fl %3d -fh %3d",
		duration,volume,frequence,frequence);
    }
    else
    {
	frequence=(int)frequence*1000;
	sprintf(cmd_buf, "cd system/bin;mm-audio-ftm -tc 8 -c ftm_test_config_mtp -d %d -v %2d -fl %4d -fh %4d",
		duration,volume,frequence,frequence);
    }
    ret = system(cmd_buf);

    FT_LOG("[%s] cmd_buf...\n",cmd_buf);
    FT_LOG("[%d] ret...\n",ret);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}
int ft_audioadb_headsetmic_headsetspk(char* duration,char* volume)
{
    int ret = 1;
    char cmd_buf[3000] = {0};

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%d,%d,%d] ft_audioadb_headsetmic_headsetspk\n",(int)duration,(int)volume);
 //   ret = system("cd system/bin;mm-audio-ftm -tc 15 -c ftm_test_config -d 50 -v 70");

    volume=(int)volume*10;
    sprintf(cmd_buf, "cd system/bin;mm-audio-ftm -tc 15 -c ftm_test_config_mtp -d %d -v %2d", duration,volume);
    //ret = system(cmd_buf);

    FT_LOG("[%s] cmd_buf...\n",cmd_buf);
    FT_LOG("[%d] ret...\n",ret);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}


int ft_audioadb_phonereceiver(char* duration,char* volume,char* frequence)
{
    int ret = 1;
    char cmd_buf[3000] = {0};

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%d,%d,%d] ft_audioadb_phonereceiver1...\n",(int)duration,(int)volume,(int)frequence);
  //  ret=system("cd system/bin;mm-audio-ftm -tc 2 -c ftm_test_config -d 2 -v 70 -fl 2000 -fh 2000");

    volume=(int)volume*10;
    if((int)frequence == 8)
    {
	frequence=(int)frequence*100;
	sprintf(cmd_buf, "cd system/bin;mm-audio-ftm -tc 2 -c ftm_test_config_mtp -d %d -v %2d -fl %3d -fh %3d",
		duration,volume,frequence,frequence);
    }
    else
    {
	frequence=(int)frequence*1000;
	sprintf(cmd_buf, "cd system/bin;mm-audio-ftm -tc 2 -c ftm_test_config_mtp -d %d -v %2d -fl %4d -fh %4d",
		duration,volume,frequence,frequence);
    }
    ret = system(cmd_buf);

    FT_LOG("[%s] cmd_buf...\n",cmd_buf);
    FT_LOG("[%d] ret...\n",ret);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}

int ft_audioadb_phonemic_headsetspk(char* duration,char* volume)
{
    int ret = 1;
    char cmd_buf[3000] = {0};

    FT_LOG("[%s,%d] Start...\n",__func__,__LINE__);
    FT_LOG("[%d,%d,%d] ft_audioadb_phonereceiver1...\n",(int)duration,(int)volume);
  //  ret = system("cd system/bin;mm-audio-ftm -tc 16 -c ftm_test_config -d 10 -v 70");

    volume=(int)volume*10;
    sprintf(cmd_buf, "cd system/bin;mm-audio-ftm -tc 16 -c ftm_test_config_mtp -d %d -v %2d", duration,volume);
    //ret = system(cmd_buf);

    FT_LOG("[%s] cmd_buf...\n",cmd_buf);
    FT_LOG("[%d] ret...\n",ret);
    FT_LOG("[%s,%d] End...\n",__func__,__LINE__);
    return ret;
}
