/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20101012  Sang Mingxin   Initial to show the camera preview.

===========================================================================*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <getopt.h> 

#include <fcntl.h> 
#include <unistd.h>
#include <errno.h>
#include <malloc.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>

#include <asm/types.h> 

#include <linux/videodev2.h>

#include <stdint.h>
#include <asm/page.h>

#include <linux/fb.h>

#include <memory.h>
#include <time.h>


int ftscreen_write (unsigned char* yuv_buffer);
int ftscreen_init(void);
int ftscreen_destroy(void);


