/*========================================================================

set_psensor_data.c

DESCRIPTION

EXTERNALIZED FUNCTIONS

INITIALIZATION AND SEQUENCING REQUIREMENTS

Copyright (c) 2010 BYD, Incorporated. All Rights Reserved.

========================================================================*/
/*===========================================================================

EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when        who              what, where, why
--------  ---------    --------------------------------------------------------
===========================================================================*/

#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <string.h>
#include <stdint.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <utils/Log.h>
#include <cutils/properties.h>
#include <cutils/sockets.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/statfs.h>
#include <sys/inotify.h>
#include <poll.h>

#define SET_PSENSOR_LOG_ERROR 1
//#define SET_PSENSOR_LOG_INFO 0


#ifdef SET_PSENSOR_LOG_ERROR
#define SET_PSENSOR_PRINT_E(fmt, args...) \
    { ALOGE(fmt , ## args); }
#else
#define SET_PSENSOR_PRINT_E(fmt, args...) \
    do { } while (0)
#endif /* SET_PSENSOR_LOG_ERROR */

#ifdef SET_PSENSOR_LOG_INFO
#define SET_PSENSOR_PRINT_I(fmt, args...) \
    { ALOGE(fmt , ## args); }
#else
#define SET_PSENSOR_PRINT_I(fmt, args...) \
    do { } while (0)
#endif /* SET_PSENSOR_LOG_INFO */


#define MAX_RW_BUF	1024

const char *ps_cal_2cm_src_file_name = "/userstore/plsensor_calibration_2cm";
const char *ps_cal_2cm_des_file_name = "/sys/class/capella_sensors/proximity/ps_cal_2cm";

const char *ps_cal_4cm_src_file_name = "/userstore/plsensor_calibration_4cm";
const char *ps_cal_4cm_des_file_name = "/sys/class/capella_sensors/proximity/ps_cal_4cm";

int main()
{
	
	char buf[MAX_RW_BUF];
	
	FILE * ps_cal_2cm_file_r;
	FILE * ps_cal_2cm_file_w;
	
	FILE * ps_cal_4cm_file_r;
	FILE * ps_cal_4cm_file_w;
	int ret;
	int size;
	
	ps_cal_2cm_file_r = fopen(ps_cal_2cm_src_file_name, "rb");
	if (!ps_cal_2cm_file_r) {
		SET_PSENSOR_PRINT_E("open '%s' failure.\n", ps_cal_2cm_src_file_name);
		printf("open '%s' failure.\n", ps_cal_2cm_src_file_name);
		return -1;
	}
	
	ps_cal_2cm_file_w = fopen(ps_cal_2cm_des_file_name, "w");
	if (!ps_cal_2cm_file_r) {
		SET_PSENSOR_PRINT_E("open '%s' failure.\n", ps_cal_2cm_des_file_name);
		printf("open '%s' failure.\n", ps_cal_2cm_des_file_name);
		return -1;
	}
	
	ps_cal_4cm_file_r = fopen(ps_cal_4cm_src_file_name, "rb");
	if (!ps_cal_4cm_file_r) {
		SET_PSENSOR_PRINT_E("open '%s' failure.\n", ps_cal_4cm_src_file_name);
		printf("open '%s' failure.\n", ps_cal_4cm_src_file_name);
		return -1;
	}
	
	ps_cal_4cm_file_w = fopen(ps_cal_4cm_des_file_name, "w");
	if (!ps_cal_4cm_file_w) {
		SET_PSENSOR_PRINT_E("open '%s' failure.\n", ps_cal_4cm_des_file_name);
		printf("open '%s' failure.\n", ps_cal_4cm_des_file_name);
		return -1;
	}
	
	ret = size = fread(buf, 1, MAX_RW_BUF, ps_cal_2cm_file_r);	
	if (ret <= 0) {
		SET_PSENSOR_PRINT_E("Error fread.ret =%d\n", ret);
		printf("Error fread.ret =%d\n", ret);
		return -1;
	}
	
	ret = fwrite(buf, 1, size, ps_cal_2cm_file_w);	
	if (ret != size) {
		SET_PSENSOR_PRINT_E("Error fwrite.ret(%d) != size(%d)\n", ret, size);
		printf("Error fwrite.ret(%d) != size(%d)\n", ret, size);
		return -1;
	}	
	
	ret = size = fread(buf, 1, MAX_RW_BUF, ps_cal_4cm_file_r);	
	if (ret <= 0) {
		SET_PSENSOR_PRINT_E("Error fread.ret =%d\n", ret);
		printf("Error fread.ret =%d\n", ret);
		return -1;
	}
	
	ret = fwrite(buf, 1, size, ps_cal_4cm_file_w);	
	if (ret != size) {
		SET_PSENSOR_PRINT_E("Error fwrite.ret(%d) != size(%d)\n", ret, size);
		printf("Error fwrite.ret(%d) != size(%d)\n", ret, size);
		return -1;
	}
	
	fclose(ps_cal_2cm_file_r);
	fclose(ps_cal_2cm_file_w);
	fclose(ps_cal_4cm_file_r);
	fclose(ps_cal_4cm_file_w);

	return 0;	
}

