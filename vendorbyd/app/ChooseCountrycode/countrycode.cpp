#include <sys/mount.h>
#include <assert.h>
#include <ctype.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <signal.h>
#include <time.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <cutils/log.h>
#include <log/log.h>
#include <log/log_read.h>
#include <log/logger.h>
#include <log/logd.h>
#include <log/logprint.h>

#define COUNTRYCODE_LOG_TAG "COUNTRYCODE"


namespace android {
static void show_help(const char *cmd)
{
    fprintf(stderr,"Usage: %s [options] [filterspecs]\n", cmd);

    fprintf(stderr,"options include:\n"
                    "  -Y             mount countrycode partition.\n"
                    "  -N             umount countrycode partition.\n"
                    );
}
}

int main(int argc, char **argv)
{

    if (argc == 2 && 0 == strcmp(argv[1], "--help")) {
        android::show_help(argv[0]);
        exit(0);
    }
    umask(022);
    int ret;
    int res;
    ret = getopt(argc, argv, "YN");
    if (ret < 0) {
        return 0;
    }
        switch(ret) {
            case 'Y':
                res = mount("/dev/block/bootdevice/by-name/countrycode", "/countrycode", "ext4",  MS_NODEV | MS_NOSUID, 0);
                if (res < 0) {
                    ALOGD("cannot mount ext4 filesystem\n");
                    goto error;
               }
            break;
            case 'N':
                umount("/countrycode");
            break;
            default:
                ALOGD("Unrecognized Option !\n");
                android::show_help(argv[0]);
                exit(-1);
            break;
        }

error:
    return 0;
}
