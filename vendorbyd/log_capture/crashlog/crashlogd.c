/**
 * Copyright (C) 2010-2020 BYD Corporation.  All rights reserved.
 *
 * BYD Corporation and its licensors retain all intellectual property
 * and proprietary rights in and to this software, related documentation
 * and any modifications thereto.  Any use, reproduction, disclosure or
 * distribution of this software and related documentation without an express
 * license agreement from BYD Corporation is strictly prohibited.
 *
 *
 *
 *
 * General Description: This file implements all the bp panic block log functions.
 *
 *
 *     Date                 Author            CR/PR                    Reference
 *    =======             ==========         ========              =================== 
 *
 *
 */

#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <ctype.h>
#include <errno.h>
#include <time.h>
#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <private/android_filesystem_config.h>
#include <linux/ioctl.h>
#include <linux/rtc.h>
#define LOG_TAG "CRASHALOG"
#include <linux/android_alarm.h>
#include "cutils/log.h"
#include <sys/inotify.h>
#include <cutils/properties.h>
#include <sys/wait.h>
#include <sys/sendfile.h>
#include <sys/sha1.h>
#include <backtrace.h>
#define LOG_NDDEBUG 0
#define CRASHEVENT "CRASH"
#define STATSEVENT "STATS"
#define STATEEVENT "STATE"
#define APALOGEVENT "APALOG"
#define STATSTRIGGER "STTRIG"
#define HPROF "HPROF"
#define APALOGTRIGGER "APALOGTRIG"
#define BZEVENT "BZ"
#define ERROREVENT "ERROR"
#define BZTRIGGER "bz_trigger"
#define BZMANUAL "MANUAL"
#define KERNEL_CRASH "IPANIC"
#define SYSSERVER_WDT "UIWDT"
#define KERNEL_FORCE_CRASH "IPANIC_FORCED"
#define FABRIC_FAKE_CRASH "FABRIC_FAKE"
#define KERNEL_FAKE_CRASH "IPANIC_FAKE"
#define ANR_CRASH "ANR"
#define JAVA_CRASH "JAVACRASH"
#define LOW_MEM "LOWMEM"
#define WTF_CRASH "WTF"
#define TOMB_CRASH "TOMBSTONE"
#define LOST "LOST_DROPBOX"
#define AP_COREDUMP "APCOREDUMP"
#define MODEM_CRASH "MPANIC"
#define MODEM_SHUTDOWN "MSHUTDOWN"
#define CURRENT_UPTIME "CURRENTUPTIME"
#define PER_UPTIME "UPTIME"
#define SYS_REBOOT "REBOOT"
#define AP_INI_M_RST "APIMR"
#define M_RST_WN_COREDUMP "MRESET"
#define FABRIC_ERROR "FABRICERR"
// Add Recovery error crash type
#define RECOVERY_ERROR "RECOVERY_ERROR"
#define CRASHALOG_ERROR_DEAD "CRASHALOG_DEAD"
#define CRASHALOG_ERROR_PATH "CRASHALOG_PATH"
#define USBBOGUS "USBBOGUS"

#define FILESIZE_MAX  (10*1024*1024)
#define PATHMAX 512
#define UPTIME_FREQUENCY (5 * 60)
#define TIMEOUT_VALUE (20*1000)
#define UPTIME_HOUR_FREQUENCY 12
#define SIZE_FOOTPRINT_MAX (PROPERTY_VALUE_MAX + 1) * 11
#define BUILD_FIELD "ro.build.version.incremental"
//#define BOARD_FIELD "ro.product.model"
#define BOARD_FIELD "ro.product.name"
#define FINGERPRINT_FIELD "ro.build.fingerprint"
#define KERNEL_FIELD "sys.kernel.version"
#define USER_FIELD "ro.build.user"
#define HOST_FIELD "ro.build.host"
#define IFWI_FIELD "sys.ifwi.version"
#define SCUFW_VERSION "sys.scu.version"
#define PUNIT_VERSION "sys.punit.version"
#define IAFW_VERSION "sys.ia32.version"
#define VALHOOKS_VERSION "sys.valhooks.version"
#define MODEM_FIELD "gsm.version.baseband"
#define IMEI_FIELD "persist.radio.device.imei"
#define PROP_CRASH "persist.service.crashlog.enable"
#define PROP_PROFILE "persist.service.profile.enable"
#define PROP_COREDUMP "persist.sys.coredump.enabled"
#define PROP_CRASH_MODE "persist.sys.crashlogd.mode"
#define PROP_ANR_USERSTACK "persist.anr.userstack.disabled"
#define PROP_APALOG_DEPTH "persist.crashreport.aplogdepth"
#define PROP_APALOG_NB_PACKET "persist.crashreport.packet"
#define PROP_APALOG_DEPTH_DEF "5"
#define PROP_APALOG_NB_PACKET_DEF "1"
#define PROP_ALOGSYSTEMSTATE "init.svc.logsystemstate"
#define BOOT_STATUS "sys.boot_completed"
#define SYS_PROP "/system/build.prop"
#define SAVEDLINES  1
#define MAX_RECORDS 5000
//#define MAX_DIR 1000
#define MAX_DIR 10
#define PERM_USER "system"
#define PERM_GROUP "log"
#define HISTORY_FILE_DIR  "/data/logs"
#define HISTORY_CORE_DIR  "/data/logs/core"
#define ALOGS_MODEM_DIR  "data/logs/modemcrash"
#define APALOG_FILE_BOOT   "/data/logs/aplog_boot"
#define APALOG_FILE_0        "/data/logs/aplog"
#define APALOG_FILE_1    "/data/logs/aplog.1"
#define BPALOG_FILE_0    "/data/logs/bplog"
#define BPALOG_FILE_1    "/data/logs/bplog.1"
#define APALOG_TYPE       0
#define BPALOG_TYPE       1
#define APALOG_STATS_TYPE 2
#define SDCARD_ALOGS_DIR "/mnt/sdcard/data/logs"
#define SDCARD_CRASH_DIR "/mnt/sdcard/data/logs/crashlog"
#define EMMC_CRASH_DIR "/data/logs/crashlog"
#define SDCARD_STATS_DIR "/mnt/sdcard/data/logs/stats"
#define EMMC_STATS_DIR "/data/logs/stats"
#define SDCARD_APALOGS_DIR "/mnt/sdcard/data/logs/aplogs"
#define EMMC_APALOGS_DIR "/data/logs/aplogs"
#define SDCARD_BZ_DIR "/mnt/sdcard/data/logs/bz"
#define EMMC_BZ_DIR "/data/logs/bz"
#define ALOGINFO_DIR "/data/logs/info/"
#define ALOGRESERVED "/data/logs/reserved"
#define BZ_CURRENT_ALOG "/data/logs/currentbzlog"
#define CRASH_CURRENT_ALOG "/data/logs/currentcrashlog"
#define STATS_CURRENT_ALOG "/data/logs/currentstatslog"
#define APALOGS_CURRENT_ALOG "/data/logs/currentaplogslog"
#define HISTORY_FILE  "/data/logs/history_event"
#define HISTORY_UPTIME "/data/logs/uptime"
#define ALOG_UUID "/data/logs/uuid.txt"
#define ALOG_BUILDID "/data/logs/buildid.txt"
#define KERNEL_CMDLINE "/proc/cmdline"
#define STARTUP_STR "androidboot.pwrsrc="
//#define PANIC_CONSOLE_NAME "/proc/emmc_ipanic_console"
#define PANIC_CONSOLE_NAME "/proc/apanic_console"
#define PROC_FABRIC_ERROR_NAME "/proc/ipanic_fabric_err"
#define PROC_UUID  "/proc/emmc0_id_entry"
#define LAST_KMSG "/proc/last_kmsg"
#define LAST_KMSG_FILE "last_kmsg"

//#define SAVED_CONSOLE_NAME "/data/dontpanic/emmc_ipanic_console"
#define SAVED_CONSOLE_NAME "/data/dontpanic/apanic_console"
#define SAVED_THREAD_NAME "/data/dontpanic/emmc_ipanic_threads"
#define SAVED_ALOGCAT_NAME "/data/dontpanic/emmc_ipanic_logcat"
#define SAVED_FABRIC_ERROR_NAME "/data/dontpanic/ipanic_fabric_err"
#define CONSOLE_NAME "emmc_ipanic_console"
#define THREAD_NAME "emmc_ipanic_threads"
#define ALOGCAT_NAME "emmc_ipanic_logcat"
#define FABRIC_ERROR_NAME "ipanic_fabric_err"
#define CRASHFILE_NAME "crashfile"

#define MODEM_SHUTDOWN_TRIGGER "data/logs/modemcrash/mshutdown.txt"

// Add recovery error trigger
#define RECOVERY_ERROR_TRIGGER "/cache/recovery/recoveryfail"
// Add recovery error log path
#define RECOVERY_ERROR_ALOG "/cache/recovery/last_log"

//2014-03-29-ji.wei2@byd.com-define startup reason
#define PWR_SMPL 2
#define ADB_REBOOT 17
#define PWR_BUT_P 128
#define PWR_USB_BUT 145

#define TIME_FORMAT_1 "%Y%m%d%H%M%S"
#define TIME_FORMAT_2 "%Y-%m-%d/%H:%M:%S  "

#define PRINT_TIME(var_tmp, format_time, local_time) { \
    strftime(var_tmp, 32, format_time, local_time);    \
    var_tmp[31]=0;                                     \
}

char *CRASH_DIR = NULL;
char *STATS_DIR = NULL;
char *APALOGS_DIR = NULL;
char *BZ_DIR = NULL;

char buildVersion[PROPERTY_VALUE_MAX];
char boardVersion[PROPERTY_VALUE_MAX];
char uuid[256];
int loop_uptime_event = 1;
int test_flag = 0;
int index_cons = 0;
int WDCOUNT_START = 0;
// global variable to abort a clean procedure
int abort_clean_sd = 0;

static int do_mv(char *src, char *des)
{
    struct stat st;

    /* check if destination exists */
    if (stat(des, &st)) {
        /* an error, unless the destination was missing */
        if (errno != ENOENT) {
            ALOGE("failed on %s - %s\n", des, strerror(errno));
            return -1;
        }
    }

    /* attempt to move it */
    if (rename(src, des)) {
        ALOGE("failed on '%s' - %s\n", src, strerror(errno));
        return -1;
    }

    return 0;
}

static unsigned int android_name_to_id(const char *name)
{
    const struct android_id_info *info = android_ids;
    unsigned int n;

    for (n = 0; n < android_id_count; n++) {
        if (!strcmp(info[n].name, name))
            return info[n].aid;
    }

    return -1U;
}

static unsigned int decode_uid(const char *s)
{
    unsigned int v;

    if (!s || *s == '\0')
        return -1U;
    if (isalpha(s[0]))
        return android_name_to_id(s);

    errno = 0;
    v = (unsigned int)strtoul(s, 0, 0);
    if (errno)
        return -1U;
    return v;
}

static mode_t get_mode(const char *s)
{
    mode_t mode = 0;
    while (*s) {
        if (*s >= '0' && *s <= '7') {
            mode = (mode << 3) | (*s - '0');
        } else {
            return -1;
        }
        s++;
    }
    return mode;
}

static int do_chmod(char *file, char *mod)
{
    mode_t mode = get_mode(mod);
    if (chmod(file, mode) < 0) {
        return -errno;
    }
    return 0;
}

static int do_chown(char *file, char *uid, char *gid)
{
    if (strstr(file, SDCARD_CRASH_DIR))
        return 0;

    if (chown(file, decode_uid(uid), decode_uid(gid)))
        return -errno;

    return 0;
}

static int do_copy(char *src, char *des, int limit)
{
    int buflen = 4*1024;
    char buffer[4*1024];
    int rc = 0;
    int fd1 = -1, fd2 = -1;
    struct stat info;
    int brtw, brtr;
    char *p;
    int filelen,tmp;

    if (stat(src, &info) < 0)
        return -1;

    if ((fd1 = open(src, O_RDONLY)) < 0){
        ALOGE("can not open file: %s\n", src);
        goto out_err;
    }

    if ((fd2 = open(des, O_WRONLY | O_CREAT | O_TRUNC, 0664)) < 0){
        ALOGE("can not open file: %s\n", des);
        goto out_err;
    }

    if ( (limit == 0) || (limit >= info.st_size) )
        filelen = info.st_size;
    else{
        filelen = limit;
        lseek(fd1, info.st_size-limit, SEEK_SET);
    }

    while(filelen){
        p = buffer;
        tmp = ((filelen>buflen) ? buflen : filelen);
        brtr = tmp;
        while (brtr) {
            rc = read(fd1, p, brtr);
            if (rc < 0)
                goto out_err;
            if (rc == 0)
                break;
            p += rc;
            brtr -= rc;
        }

        p = buffer;
        brtw = tmp;
        while (brtw) {
            rc = write(fd2, p, brtw);
            if (rc < 0)
                goto out_err;
            if (rc == 0)
                break;
            p += rc;
            brtw -= rc;
        }

        filelen = filelen - tmp;
    }

    rc = 0;
    goto out;
out_err:
    rc = -1;
out:
    if (fd1 >= 0)
        close(fd1);
    if (fd2 >= 0)
        close(fd2);

    do_chown(des, PERM_USER, PERM_GROUP);
    return rc;
}

static void flush_aplog_atboot(char *mode, int dir, char* ts)
{
    char cmd[512] = { '\0', };
    char log_boot_name[512] = { '\0', };

    snprintf(log_boot_name, sizeof(log_boot_name)-1, "%s%d/%s_%s_%s", CRASH_DIR, dir, strrchr(APALOG_FILE_BOOT,'/')+1,mode,ts);
    snprintf(cmd, sizeof(cmd)-1, "/system/bin/logcat -b system -b main -b radio -b events -v threadtime -d -f %s", log_boot_name);
    int status = system(cmd);
    if (status != 0)
        ALOGE("flush ap log from boot returns status: %d.\n", status);
    do_chown(log_boot_name, PERM_USER, PERM_GROUP);
    return ;
}

static void do_last_kmsg_copy(int dir)
{
    char destion[PATHMAX];
    struct stat info;

    if(stat(LAST_KMSG, &info) == 0) {
        snprintf(destion, sizeof(destion), "%s%d/%s", CRASH_DIR, dir, LAST_KMSG_FILE);
        do_copy(LAST_KMSG, destion, FILESIZE_MAX);
    }

}

static void do_log_copy(char *mode, int dir, char* ts, int type)
{
    char destion[PATHMAX];
    struct stat info;

    if(type == APALOG_TYPE){
        if(stat(APALOG_FILE_0, &info) == 0){
            snprintf(destion,sizeof(destion), "%s%d/%s_%s_%s", CRASH_DIR, dir,strrchr(APALOG_FILE_0,'/')+1,mode,ts);
            do_copy(APALOG_FILE_0,destion, FILESIZE_MAX);
            if(info.st_size < 1*1024*1024){
                if(stat(APALOG_FILE_1, &info) == 0){
                    snprintf(destion,sizeof(destion), "%s%d/%s_%s_%s", CRASH_DIR, dir,strrchr(APALOG_FILE_1,'/')+1,mode,ts);
                    do_copy(APALOG_FILE_1,destion, FILESIZE_MAX);
                }
            }
        }
    }
    if(type == APALOG_STATS_TYPE){
        if(stat(APALOG_FILE_0, &info) == 0){
            snprintf(destion,sizeof(destion), "%s%d/%s_%s_%s", STATS_DIR, dir,strrchr(APALOG_FILE_0,'/')+1,mode,ts);
            do_copy(APALOG_FILE_0,destion, FILESIZE_MAX);
            if(info.st_size < 1*1024*1024){
                if(stat(APALOG_FILE_1, &info) == 0){
                    snprintf(destion,sizeof(destion), "%s%d/%s_%s_%s", STATS_DIR, dir,strrchr(APALOG_FILE_1,'/')+1,mode,ts);
                    do_copy(APALOG_FILE_1,destion, FILESIZE_MAX);
                }
            }
        }
    }
    if(type == BPALOG_TYPE){
        if(stat(BPALOG_FILE_0, &info) == 0){
            snprintf(destion,sizeof(destion), "%s%d/%s_%s_%s%s", CRASH_DIR, dir,strrchr(BPALOG_FILE_0,'/')+1,mode,ts,".istp");
            do_copy(BPALOG_FILE_0,destion, FILESIZE_MAX);
            if(info.st_size < 1*1024*1024){
                if(stat(BPALOG_FILE_1, &info) == 0){
                    snprintf(destion,sizeof(destion), "%s%d/%s_%s_%s%s", CRASH_DIR, dir,strrchr(BPALOG_FILE_1,'/')+1,mode,ts,".istp");
                    do_copy(BPALOG_FILE_1,destion, FILESIZE_MAX);
                }
            }
        }
    }

    return ;
}

static int remove_folder(char* path)
{
    char spath[PATHMAX];
    DIR *d;
    struct dirent* de;

    d = opendir(path);
    if (d == 0) {
         return -1;
    }
    else {
        while ((de = readdir(d)) != 0) {
           if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
               continue;
           snprintf(spath, sizeof(spath)-1,  "%s/%s", path, de->d_name);
           if (remove(spath) < 0)
               ALOGE("Unable to remove %s", spath);
        }
        closedir(d);
    }
    int status = rmdir(path);
    return status;
}

static int write_file(const char *path, const char *value)
{
    int fd, ret, len;

    fd = open(path, O_WRONLY | O_CREAT, 0622);

    if (fd < 0){
        ALOGE("can not open file: %s\n", path);
        return -errno;
    }
    len = strlen(value);

    do {
        ret = write(fd, value, len);
    } while (ret < 0 && errno == EINTR);

    close(fd);
    if (ret < 0) {
        return -errno;
    } else {
        return 0;
    }
}

static int get_version_info(char *fn, char *field, char *buf)
{

    char *data;
    int sz;
    int fd;
    int i = 0;
    char *info = NULL;
    int len = -1;
    int p;

    data = 0;
    fd = open(fn, O_RDONLY);
    if (fd < 0){
        ALOGE("can not open file: %s\n", fn);
        return 0;
    }

    sz = lseek(fd, 0, SEEK_END);
    if (sz < 0)
        goto oops;

    if (lseek(fd, 0, SEEK_SET) != 0)
        goto oops;

    data = (char *)malloc(sz + 2);
    if (data == 0)
        goto oops;

    if (read(fd, data, sz) != sz)
        goto oops;

    data[sz] = '\n';
    data[sz + 1] = 0;

    while (i < sz) {
        if (data[i] == '=')
            if (i - strlen(field) > 0)
                if (!memcmp(&data[i - strlen(field)], field, strlen(field))) {
                    p = ++i;
                    while ((data[i] != '\n') && (data[i] != 0))
                        i++;
                    len = i - p;
                    if (len > 0) {
                        memcpy(buf, &data[p], len);
                        buf[len] = 0;
                        break;
                    }
                }
        i++;
    }

oops:
    close(fd);
    if (data != 0)
        free(data);
    return len;

}

static int get_uptime(long long *time_ns)
{
    struct timespec ts;
    int fd, result;

    fd = open("/dev/alarm", O_RDONLY);
    if (fd < 0){
        ALOGE("can not open file: %s\n", "/dev/alarm");
        return -1;
    }
    result =
        ioctl(fd,
                ANDROID_ALARM_GET_TIME(ANDROID_ALARM_ELAPSED_REALTIME), &ts);
    close(fd);
    *time_ns = (((long long) ts.tv_sec) * 1000000000LL) + ((long long) ts.tv_nsec);
    return 0;
}

static void compute_key(char* key, char *event, char *type)
{
    static SHA1_CTX *sha = NULL;
    char buf[256] = { '\0', };
    long long time_ns=0;
    char *tmp_key = key;
    unsigned char results[SHA1_DIGEST_LENGTH];
    int i;

    get_uptime(&time_ns);
    snprintf(buf, 256, "%s%s%s%s%lld", buildVersion, uuid, event, type, time_ns);

    if (sha == NULL) {
        sha = (SHA1_CTX*)malloc(sizeof(SHA1_CTX));
        if (sha == NULL) {
            ALOGE("%s - Cannot create SHA1_CTX memory... fails to compute the key!\n", __FUNCTION__);
            return -1;
        }
        SHA1Init(sha);
    }
    SHA1Update(sha, (unsigned char*) buf, strlen(buf));
    SHA1Final(results, sha);
    for (i = 0; i < SHA1_DIGEST_LENGTH/2; i++)
    {
        sprintf(tmp_key, "%02x", results[i]);
        tmp_key+=2;
    }
    *tmp_key=0;
}

static void find_file_in_dir(char *name_file_found, char *dir_to_search, char *name_file_to_match)
{
    DIR *d;
    struct dirent* de;
    d = opendir(dir_to_search);
    while ((de = readdir(d))) {
        const char *name = de->d_name;
        if (strstr(name, name_file_to_match)){
            sprintf(name_file_found, "%s", name);
            break;
        }
    }
    closedir(d);
}


static void backup_apcoredump(unsigned int dir, char* name, char* path)
{
    char src[512] = { '\0', };
    char des[512] = { '\0', };
    snprintf(src, sizeof(src), "%s", path);
    snprintf(des, sizeof(des), "%s%d/%s", CRASH_DIR, dir, name);
    int status = do_copy(src, des, 0);
    if (status != 0)
        ALOGE("backup ap core dump status: %d.\n",status);
    else
        remove(path);
}

static void build_footprint(char *id)
{
    char prop[PROPERTY_VALUE_MAX];

    /* footprint contains:
     * buildId
     * fingerPrint
     * kernelVersion
     * buildUserHostname
     * modemVersion
     * ifwiVersion
     * iafwVersion
     * scufwVersion
     * punitVersion
     * valhooksVersion */

    snprintf(id, SIZE_FOOTPRINT_MAX, "%s,", buildVersion);

    property_get(FINGERPRINT_FIELD, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(KERNEL_FIELD, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(USER_FIELD, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, "@", SIZE_FOOTPRINT_MAX);

    property_get(HOST_FIELD, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(MODEM_FIELD, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(IFWI_FIELD, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(IAFW_VERSION, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(SCUFW_VERSION, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(PUNIT_VERSION, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
    strncat(id, ",", SIZE_FOOTPRINT_MAX);

    property_get(VALHOOKS_VERSION, prop, "");
    strncat(id, prop, SIZE_FOOTPRINT_MAX);
}

static void create_first_crashfile(char* type, char* path, char* key, char* uptime, char* footprint, char* boardVersion, char* date, char* imei)
{
    FILE *fp;
    char fullpath[PATHMAX];
    char mpanicpath[PATHMAX];
    snprintf(fullpath, sizeof(fullpath)-1, "%s/%s", path, CRASHFILE_NAME);
    fp = fopen(fullpath,"w");
    fclose(fp);
    do_chown(fullpath, PERM_USER, PERM_GROUP);
    fp = fopen(fullpath,"w");
    if (!strncmp(type,BZEVENT,sizeof(BZEVENT))) {
        fprintf(fp,"EVENT=BZ\n");
    }
    else {
        fprintf(fp,"EVENT=CRASH\n");
    }
    fprintf(fp,"ID=%s\n", key);
    fprintf(fp,"SN=%s\n", uuid);
    fprintf(fp,"DATE=%s\n", date);
    fprintf(fp,"UPTIME=%s\n", uptime);
    fprintf(fp,"BUILD=%s\n", footprint);
    fprintf(fp,"BOARD=%s\n", boardVersion);
    fprintf(fp,"IMEI=%s\n", imei);
    if (!strncmp(type,BZEVENT,sizeof(BZEVENT))) {
        fprintf(fp,"TYPE=MANUAL\n");
    }
    else {
        fprintf(fp,"TYPE=%s\n", type);
    }
    if (!strcmp(MODEM_CRASH,type)){
        ALOGI("Modem panic detected : generating DATA0\n");
        FILE *fd_panic;
        DIR *d;
        struct dirent* de;
        d = opendir(path);
        while ((de = readdir(d))) {
            const char *name = de->d_name;
            if (strstr(name, "mpanic")){
                snprintf(mpanicpath, sizeof(mpanicpath)-1, "%s/%s", path, name);
                fd_panic = fopen(mpanicpath, "r");
                if (fd_panic == NULL){
                    ALOGE("can not open file: %s\n", mpanicpath);
                    break;
                }
                char value[PATHMAX] = "";
                fscanf(fd_panic, "%s", value);
                fclose(fd_panic);
                fprintf(fp,"DATA0=%s\n", value);
                break;
            }
        }
        closedir(d);
    }
    fprintf(fp,"_END\n");
    fclose(fp);
}


static void analyze_crash(char* type, char* path, char* key, char* uptime, char* date)
{
    char cmd[512] = { '\0', };
    char footprint[SIZE_FOOTPRINT_MAX] = { '\0', };
    char imei[PROPERTY_VALUE_MAX];

    property_get(IMEI_FIELD, imei, "");

    build_footprint(footprint);

    snprintf(cmd, sizeof(cmd)-1, "/system/bin/analyze_crash %s %s %s %s %s %s %s %s", type, path, key, uptime, footprint, boardVersion, date, imei);
    int status = system(cmd);
    if (status != 0){
        ALOGE("analyze crash status: %d.\n", status);
        create_first_crashfile(type, path, key, uptime, footprint, boardVersion, date, imei);
     }
}

static void notify_crash_to_upload(char* event_id)
{
    char cmd[512];
    char boot_state[PROPERTY_VALUE_MAX];

    property_get(BOOT_STATUS, boot_state, "-1");
    if (strcmp(boot_state, "1"))
        return;


}

static void notify_crashreport()
{
    char boot_state[PROPERTY_VALUE_MAX];
    property_get(BOOT_STATUS, boot_state, "-1");
    if (strcmp(boot_state, "1"))
        return;
}

static void history_file_write(char *event, char *type, char *subtype, char *log, char* lastuptime, char* key, char* date_tmp_2)
{
    char uptime[32];
    struct stat info;
    long long tm=0;
    time_t t;
    int hours, seconds, minutes;
    FILE *to;
    char tmp[PATHMAX];
    char * p;
    char * p1;

    // compute subtype
    if (!subtype)
        subtype = type;

    // compute uptime
    get_uptime(&tm);
    hours = (int) (tm / 1000000000LL);
    seconds = hours % 60;
    hours /= 60;
    minutes = hours % 60;
    hours /= 60;
    snprintf(uptime,sizeof(uptime),"%04d:%02d:%02d",hours, minutes,seconds);

    if (stat(HISTORY_FILE, &info) != 0) {
        to = fopen(HISTORY_FILE, "w");
        if (to == NULL){
            ALOGE("can not open file: %s\n", HISTORY_FILE);
            return;
        }
        do_chmod(HISTORY_FILE, "644");
        do_chown(HISTORY_FILE, PERM_USER, PERM_GROUP);
        fprintf(to, "#V1.0 %-16s%-24s\n", CURRENT_UPTIME, uptime);
        fprintf(to, "#EVENT  ID                    DATE                 TYPE\n");
        fclose(to);
    }

    if (log != NULL) {
        snprintf(tmp, sizeof(tmp), "%s", log);
        if((p = strrchr(tmp,'/'))){
            p[0] = '\0';
        }
        to = fopen(HISTORY_FILE, "a");
        if (to == NULL){
            ALOGE("can not open file: %s\n", HISTORY_FILE);
            return;
        }
        fprintf(to, "%-8s%-22s%-20s%s %s\n", event, key, date_tmp_2, type, tmp);
        fclose(to);
        if (!strncmp(event, CRASHEVENT, sizeof(CRASHEVENT)))
            analyze_crash(subtype, tmp, key, uptime, date_tmp_2);
        else if(!strncmp(event, BZEVENT, sizeof(BZEVENT)))
            analyze_crash(event, tmp, key, uptime, date_tmp_2);
    } else if (type != NULL) {

        to = fopen(HISTORY_FILE, "a");
        if (to == NULL){
            ALOGE("can not open file: %s\n", HISTORY_FILE);
            return;
        }
        if (lastuptime != NULL)
            fprintf(to, "%-8s%-22s%-20s%-16s %s\n", event, key, date_tmp_2, type, lastuptime);
        else
            fprintf(to, "%-8s%-22s%-20s%-16s\n", event, key, date_tmp_2, type);
        fclose(to);
        ALOGE("%-8s%-22s%-20s%s\n", event, key, date_tmp_2, type);
    } else {

        to = fopen(HISTORY_FILE, "a");
        if (to == NULL){
            ALOGE("can not open file: %s\n", HISTORY_FILE);
            return;
        }
        fprintf(to, "%-8s%-22s%-20s%s\n", event, key, date_tmp_2, lastuptime);
        fclose(to);
        ALOGE("%-8s%-22s%-20s%s\n", event, key, date_tmp_2, lastuptime);
    }
    return;
}

static int del_file_more_lines(char *fn)
{
    char *data;
    int sz;
    int fd, i;
    int count = 0;
    int tmp = 0;
    int dest = 0;
    data = 0;
    fd = open(fn, O_RDWR);
    if (fd < 0){
        ALOGE("can not open file: %s\n", fn);
        return 0;
    }

    sz = lseek(fd, 0, SEEK_END);
    if (sz < 0) {
        close(fd);
        return 0;
    }

    if (lseek(fd, 0, SEEK_SET) != 0) {
        close(fd);
        return 0;
    }

    data = (char *)malloc(sz + 2);
    if (data == 0) {
        close(fd);
        return 0;
    }

    if (read(fd, data, sz) != sz) {
        close(fd);
        if (data != 0)
            free(data);
        return 0;
    }

    close(fd);

    data[sz] = '\n';
    data[sz + 1] = 0;

    for (i = 0; i < sz; i++)
        if (data[i] == '\n')
            count++;

    if (count >= MAX_RECORDS + SAVEDLINES) {

        count = count - (MAX_RECORDS >> 1);
        for (i = 0; i < sz; i++) {
            if (data[i] == '\n') {
                tmp++;
                if (tmp == SAVEDLINES)
                    dest = i;
                if (tmp >= count)
                    break;
            }
        }
        memcpy(&data[dest + 1], &data[i + 1], sz - i - 1);
        fd = open(fn, O_RDWR | O_TRUNC);
        if (fd < 0) {
            free(data);
            ALOGE("can not open file: %s\n", fn);
            return 0;
        }

        if (write(fd, &data[0], sz - i - 1 + dest + 1) !=
                (sz - i - 1 + dest + 1)) {
            close(fd);
            free(data);
            return 0;
        }
        close(fd);
    }

    if (data != 0)
        free(data);
    return 0;
}

static void restart_profile1_srv(void)
{
    char value[PROPERTY_VALUE_MAX];

    property_get(PROP_PROFILE, value, "");
    if (!strncmp(value, "1", 1)){
        property_set("ctl.start", "profile1_rest");
    }
}

static void restart_profile2_srv(void)
{
    char value[PROPERTY_VALUE_MAX];

    property_get(PROP_PROFILE, value, "");
    if (!strncmp(value, "2", 1)){
        property_set("ctl.start", "profile2_rest");
    }
}

static void init_profile_srv(void)
{
    char value[PROPERTY_VALUE_MAX];

    property_get(PROP_PROFILE, value, "");
    if (!strncmp(value, "1", 1)){
        property_set("ctl.start", "profile1_init");
    }
    if (!strncmp(value, "2", 1)){
        property_set("ctl.start", "profile2_init");
    }
}

static void start_dumpstate_srv(char* crash_dir, int crashseq) {
    char dumpstate_dir[PATHMAX];
    if (crash_dir == NULL) return;
    snprintf(dumpstate_dir, sizeof(dumpstate_dir), "%s%d/", crash_dir, crashseq);
    property_set("crashlogd.storage.path", dumpstate_dir);
    property_set("ctl.start", "logsystemstate");
}

static int mv_modem_crash(char *spath, char *dpath)
{

    char src[512] = { '\0', };
    char des[512] = { '\0', };
    struct stat st;
    DIR *d;
    struct dirent *de;

    if (stat(spath, &st))
        return -1;
    if (stat(dpath, &st))
        return -1;

    d = opendir(spath);
    if (d == 0) {
        ALOGE("opendir failed, %s\n", strerror(errno));
        return -1;
    }
    while ((de = readdir(d)) != 0) {
        if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
            continue;
        if (strstr(de->d_name, "cd") && strstr(de->d_name, ".tar.gz")){
            snprintf(src, sizeof(src), "%s/%s", spath, de->d_name);
            snprintf(des, sizeof(des), "%s/%s", dpath, de->d_name);
            do_copy(src, des, 0);
            remove(src);
        }
    }
    if (closedir(d) < 0){
        ALOGE("closedir failed, %s\n", strerror(errno));
        return -1;
    }
    return 0;

}

struct wd_name {
    int wd;
    int mask;
    char *eventname;
    char *filename;
    char *cmp;
};

struct wd_name wd_array[] = {
    /* -------------Warning: if table is updated, don't forget to update also WDCOUNT and WDCOUNT_START in main function--- */
    {0, IN_MOVED_TO|IN_DELETE_SELF|IN_MOVE_SELF, SYSSERVER_WDT, "/data/system/dropbox", "system_server_watchdog"},
    {0, IN_MOVED_TO|IN_DELETE_SELF|IN_MOVE_SELF, ANR_CRASH, "/data/system/dropbox", "anr"},
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, TOMB_CRASH, "/data/tombstones", "tombstone"},
    {0, IN_MOVED_TO|IN_DELETE_SELF|IN_MOVE_SELF, JAVA_CRASH, "/data/system/dropbox", "crash"},
    {0, IN_MOVED_TO|IN_DELETE_SELF|IN_MOVE_SELF, LOW_MEM, "/data/system/dropbox", "lowmem"},
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, AP_COREDUMP ,"/data/logs/core", ".core"},
    {0, IN_MOVED_TO|IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, LOST ,"/data/system/dropbox", ".lost"}, /* for full dropbox */
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, HPROF, "/data/logs/core", ".hprof"},
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, STATSTRIGGER, "/data/logs/stats", "_trigger"},
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, APALOGTRIGGER, "/data/logs/aplogs", "_trigger"},
    /* -----------------------------above is dir, below is file------------------------------------------------------------ */
    {0, IN_CLOSE_WRITE, CURRENT_UPTIME, "/data/logs/uptime", ""},
    /* -------------------------above is AP, below is modem---------------------------------------------------------------- */
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, MODEM_CRASH ,"/data/logs/modemcrash", "mpanic.txt"},/*for modem crash */
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, AP_INI_M_RST ,"/data/logs/modemcrash", "apimr.txt"},
    {0, IN_CLOSE_WRITE|IN_DELETE_SELF|IN_MOVE_SELF, M_RST_WN_COREDUMP ,"/data/logs/modemcrash", "mreset.txt"},
};

int WDCOUNT = ((int)(sizeof(wd_array)/sizeof(struct wd_name)));

void process_anr_or_uiwdt(char *destion, int dir, int remove_path)
{
    char cmd[PATHMAX];
    int src, dest;
    char dest_path[PATHMAX];
    char dest_path_symb[PATHMAX];
    struct stat stat_buf;
    char *tracefile;
    FILE *fp;
    int i;

    if (!strcmp(".gz", &destion[strlen(destion) - 3])) {
        /* extract gzip file */
        snprintf(cmd, sizeof(cmd), "/system/bin/gzip -d %s", destion);
        system(cmd);
        destion[strlen(destion) - 3] = 0;
        do_chown(destion, PERM_USER, PERM_GROUP);
    }
    fp = fopen(destion, "r");
    if (fp == NULL) {
        ALOGE("Failed to open file %s:%s\n", destion, strerror(errno));
        return;
    }
    /* looking for "Trace file:" from the first 100 lines */
    for (i = 0; i < 100; i++) {
        if (fgets(cmd, sizeof(cmd), fp)) {
            if (!strncmp("Trace file:", cmd, 11)) {
                tracefile = cmd + 11;
                tracefile[strlen(tracefile) - 1] = 0; /* eliminate trailing \n */
                // copy
                snprintf(dest_path,sizeof(dest_path),"%s%d/trace_all_stack.txt", CRASH_DIR, dir);
                src = open(tracefile, O_RDONLY);
                fstat(src, &stat_buf);
                if (src < 0) {
                    ALOGE("Failed to open file %s:%s\n", tracefile, strerror(errno));
                    break;
                }
                dest = open(dest_path, O_WRONLY|O_CREAT, 0664);
                close(dest);
                do_chmod(dest_path, "600");
                do_chown(dest_path, PERM_USER, PERM_GROUP);
                dest = open(dest_path, O_WRONLY, stat_buf.st_mode);
                if (dest < 0) {
                    ALOGE("Failed to open file %s:%s\n", dest_path, strerror(errno));
                    close(src);
                    break;
                }
                sendfile(dest, src, NULL, stat_buf.st_size);
                close(src);
                close(dest);
                // remove src file
                if (unlink(tracefile) != 0) {
                    ALOGE("Failed to remove file %s:%s\n", tracefile, strerror(errno));
                }
                // parse
                backtrace_parse_tombstone_file(dest_path);
                if ((remove_path > 0) && unlink(dest_path) != 0) {
                    ALOGE("Failed to remove file %s:%s\n", dest_path, strerror(errno));
                }
                break;
            }
        }
    }
    fclose(fp);
    do_chown(dest_path, PERM_USER, PERM_GROUP);
    snprintf(dest_path_symb, sizeof(dest_path_symb), "%s_symbol", dest_path);
    do_chown(dest_path_symb, PERM_USER, PERM_GROUP);
}

static int get_str_in_file(char *file, char *keyword, char *result)
{
    char buffer[PATHMAX];
    int rc = 0;
    FILE *fd1;
    struct stat info;
    int filelen,buflen;

    if (stat(file, &info) < 0)
        return -1;

    if (keyword == NULL)
        return -1;

    fd1 = fopen(file,"r");
    if(fd1 == NULL){
        ALOGE("can not open file: %s\n", file);
        return -1;
    }

    while(!feof(fd1)){
        if (fgets(buffer, sizeof(buffer), fd1) != NULL){
            if (keyword && strstr(buffer,keyword)){
                int buflen = strlen(buffer);
                strcpy(result,buffer+strlen(keyword));
                result[strlen(result)-1]= '\0';
                rc = 0;
                break;

            }
        }
    }

    if (fd1 != NULL)
        fclose(fd1);

    return rc;
}


void backtrace_anr_uiwdt(char *dest, int dir)
{
    char value[PROPERTY_VALUE_MAX];
    property_get(PROP_ANR_USERSTACK, value, "0");
    if (strncmp(value, "1", 1)) {
        process_anr_or_uiwdt(dest, dir, 0);
    }
}

void monitor_crashenv(void)
{
    int status = system("/system/bin/monitor_crashenv");
    if (status != 0)
        ALOGE("monitor_crashenv status: %d.\n", status);
    return ;
}

static int crashlog_raise_infoerror(char *type, char *subtype)
{
    char date_tmp_2[32];
    time_t t;
    struct tm *time_tmp;
    char key[SHA1_DIGEST_LENGTH+1];

    time(&t);
    time_tmp = localtime((const time_t *)&t);
    PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
    // compute crash id
    compute_key(key, type, subtype);

    ALOGE("%-8s%-22s%-20s%s\n", type, key, date_tmp_2, subtype);
    unlink(ALOGRESERVED);
    history_file_write(type, subtype, NULL, ALOGINFO_DIR, NULL, key, date_tmp_2);
    del_file_more_lines(HISTORY_FILE);
    monitor_crashenv();
    notify_crashreport();
    return 0;
}

void check_running_power_service()
{
    char powerservice[PROPERTY_VALUE_MAX];
    char powerenable[PROPERTY_VALUE_MAX];

    property_get("init.svc.profile_power", powerservice, "");
    property_get("persist.service.power.enable", powerenable, "");
    if (strcmp(powerservice, "running") && !strcmp(powerenable, "1")) {
        ALOGE("power service stopped whereas property is set .. restarting\n");
        property_set("ctl.start", "profile_power");
    }
}

void check_crashlog_dead()
{
    char token[PROPERTY_VALUE_MAX];
    property_get("crashlogd.token", token, "");
    if ((strlen(token) < 4)) {
         strcat(token, "1");
         property_set("crashlogd.token", token);
         if (!strncmp(token, "11", 2))
             crashlog_raise_infoerror(ERROREVENT, CRASHALOG_ERROR_DEAD);
    }
}

#define CRASH_MODE 0
#define CRASH_MODE_NOSD 1
#define STATS_MODE 2
#define APALOGS_MODE 3
#define BZ_MODE 4
static void sdcard_available(int mode)
{
    CRASH_DIR = EMMC_CRASH_DIR;
    STATS_DIR = EMMC_STATS_DIR;
    APALOGS_DIR = EMMC_APALOGS_DIR;
    BZ_DIR = EMMC_BZ_DIR;
    /*
    struct stat info;
    char value[PROPERTY_VALUE_MAX];

    CRASH_DIR = EMMC_CRASH_DIR;
    STATS_DIR = EMMC_STATS_DIR;
    APALOGS_DIR = EMMC_APALOGS_DIR;
    BZ_DIR = EMMC_BZ_DIR;

    property_get(PROP_CRASH_MODE, value, "");
    if ((!strncmp(value, "lowmemory", 9)) || (mode == CRASH_MODE_NOSD))
        return;

    if ((stat(SDCARD_ALOGS_DIR, &info) == 0) && (opendir(SDCARD_ALOGS_DIR))){
        CRASH_DIR = SDCARD_CRASH_DIR;
        STATS_DIR = SDCARD_STATS_DIR;
        APALOGS_DIR = SDCARD_APALOGS_DIR;
        BZ_DIR = SDCARD_BZ_DIR;
    } else {
        mkdir(SDCARD_ALOGS_DIR, 0777);
        if ((stat(SDCARD_ALOGS_DIR, &info) == 0) && (opendir(SDCARD_ALOGS_DIR))){
            CRASH_DIR = SDCARD_CRASH_DIR;
            STATS_DIR = SDCARD_STATS_DIR;
            APALOGS_DIR = SDCARD_APALOGS_DIR;
            BZ_DIR = SDCARD_BZ_DIR;
        }
    }*/
    return;
}

static unsigned int find_dir(unsigned int max, int mode)
{
    struct stat sb;
    char path[PATHMAX];
    unsigned int i, oldest = 0;
    FILE *fd;
    DIR *d;
    struct dirent *de;
    struct stat st;
    struct stat std;
    char *dir;

    sdcard_available(mode);

    switch(mode){
    case CRASH_MODE:
    case CRASH_MODE_NOSD:
        snprintf(path, sizeof(path), CRASH_CURRENT_ALOG);
        break;
    case APALOGS_MODE:
        snprintf(path, sizeof(path), APALOGS_CURRENT_ALOG);
        break;
    case BZ_MODE:
        snprintf(path, sizeof(path), BZ_CURRENT_ALOG);
        break;
    default:
        snprintf(path, sizeof(path), STATS_CURRENT_ALOG);
        break;
    }

    if ((!stat(path, &sb))) {
        fd = fopen(path, "r");
        if (fd == NULL){
            ALOGE("can not open file: %s\n", path);
            goto out_err;
        }
        if (fscanf(fd, "%4d", &i)==EOF) {
            i = 0;
        }
        fclose(fd);
        i = i % MAX_DIR;
        oldest = i++;
        fd = fopen(path, "w");
        if (fd == NULL){
            ALOGE("can not open file: %s\n", path);
             goto out_err;
        }
        fprintf(fd, "%4d", (i % max));
        fclose(fd);
    } else {
        if (errno == ENOENT){
            ALOGE("File %s does not exist, returning to crashlog folder 0.\n",path);
        } else {
            ALOGE("Other error : %d.\n", errno);
            //need to return -1 to avoid overwrite old log folder
            goto out_err;
        }

        fd = fopen(path, "w");
        if (fd == NULL){
            ALOGE("can not open file: %s\n", path);
            goto out_err;
        }
        oldest = 0;
        fprintf(fd, "%4d", 1);
        fclose(fd);
    }

    /* we didn't find an available file, so we clobber the oldest one */
    switch(mode){
    case CRASH_MODE_NOSD:
    case CRASH_MODE:
        dir = CRASH_DIR;
        break;
    case APALOGS_MODE:
        dir = APALOGS_DIR;
        break;
    case BZ_MODE:
        dir = BZ_DIR;
        break;
    default:
        dir = STATS_DIR;
        break;
    }
    snprintf(path, sizeof(path),  "%s%d", dir, oldest);
    if (stat(path, &st) < 0)
        mkdir(path, 0777);
    else{
        d = opendir(path);
        if (d == 0) {
            ALOGE("opendir failed, %s\n", strerror(errno));
            goto out_err;
       }
        while ((de = readdir(d)) != 0) {
            if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
                continue;
            snprintf(path, sizeof(path),  "%s%d/%s", dir, oldest,
                    de->d_name);
            remove(path);
        }
        if (closedir(d) < 0){
            ALOGE("closedir failed, %s\n", strerror(errno));
            goto out_err;
        }
        rmdir(path);
        snprintf(path, sizeof(path),  "%s%d", dir, oldest);
        mkdir(path, 0777);
    }
    //if (opendir(path) == 0) {
    if (stat(path, &std) < 0) {
       ALOGE("Can not create dir %s", path);
       ALOGE("[byd] mkdir fail");
       goto out_err;
    }
    if (!strstr(path, "sdcard")) {
         char cmd[512] = { '\0', };

         snprintf(cmd, sizeof(cmd)-1, "/system/bin/chown %s %s", PERM_USER, path);
         int status = system(cmd);
         if (status != 0)
            ALOGE("status chown %s: %d\n", cmd, status);
   }
   goto out;

out_err:
    oldest = -1;
    crashlog_raise_infoerror(ERROREVENT, CRASHALOG_ERROR_PATH);
out:
    return oldest;
}

#define SCREENSHOT_PATTERN "SCREENSHOT="

static int do_screenshot_copy(char* bz_description, char* bzdir){
    char buffer[PATHMAX];
    char screenshot[PATHMAX];
    char destion[PATHMAX];
    FILE *fd1;
    struct stat info;
    int bz_num = 0;
    unsigned int screenshot_len;

    if (stat(bz_description, &info) < 0)
        return -1;

    fd1 = fopen(bz_description,"r");
    if(fd1 == NULL){
        ALOGE("can not open file: %s\n", bz_description);
        return -1;
    }

    while(!feof(fd1)){
        if (fgets(buffer, sizeof(buffer), fd1) != NULL){
            if (strstr(buffer,SCREENSHOT_PATTERN)){
                //Compute length of screenshot path
                screenshot_len = strlen(buffer) - strlen(SCREENSHOT_PATTERN);
                if ((screenshot_len > 0) && (screenshot_len < sizeof(screenshot))) {
                   //Copy file path
                   strncpy(screenshot, buffer+strlen(SCREENSHOT_PATTERN), screenshot_len);
                   //If last character is '\n' replace it by '\0'
                   if ((screenshot[screenshot_len-1]) == '\n')
                     screenshot_len--;
                   screenshot[screenshot_len]= '\0';

                   if(bz_num == 0)
                      snprintf(destion,sizeof(destion),"%s/bz_screenshot.png", bzdir);
                   else
                      snprintf(destion,sizeof(destion),"%s/bz_screenshot%d.png", bzdir, bz_num);
                   bz_num++;
                   do_copy(screenshot,destion,0);
                }
            }
        }
    }

    if (fd1 != NULL)
        fclose(fd1);

    return 0;
}

static int find_str_in_file(char *file, char *keyword, char *tail)
{
    char buffer[4*1024];
    int rc = 0;
    FILE *fd1;
    struct stat info;
    int brtw, brtr;
    char *p;
    int filelen,tmp,stringlen,buflen;

    if (stat(file, &info) < 0)
        return -1;

    if (keyword == NULL)
        return -1;

    fd1 = fopen(file,"r");
    if(fd1 == NULL){
        ALOGE("can not open file: %s\n", file);
        goto out_err;
    }
    while(!feof(fd1)){
        if (fgets(buffer, sizeof(buffer), fd1) != NULL){
            if (keyword && strstr(buffer,keyword)){
                if (!tail){
                    rc = 0;
                    goto out;
                } else{
                    int buflen = strlen(buffer);
                    int str2len = strlen(tail);
                    if ((buflen > str2len) && (!strncmp(&(buffer[buflen-str2len-1]), tail, strlen(tail)))){
                        rc = 0;
                        goto out;
                    }
                }
            }
        }
    }

out_err:
    rc = -1;
out:
    if (fd1 != NULL)
        fclose(fd1);

    return rc;
}

/*
* Name          : clean_crashlog_in_sd
* Description   : clean legacy crash log folder
* Parameters    :
*   char *dir_to_search       -> name of the folder
*   int max                   -> max number of folder to remove*/
static void clean_crashlog_in_sd(char *dir_to_search, int max)
{
    char path[PATHMAX];
    DIR *d;
    struct dirent* de;
    int i = 0;

    d = opendir(dir_to_search);
    if (d) {
        while ((de = readdir(d))) {
            const char *name = de->d_name;
            snprintf(path, sizeof(path)-1, "%s/%s", dir_to_search, name);
            if ((strstr(path, SDCARD_CRASH_DIR) ||
               (strstr(path, SDCARD_STATS_DIR)) ||
               (strstr(path, SDCARD_APALOGS_DIR)) ||
               (strstr(path, SDCARD_BZ_DIR))))
                if (find_str_in_file(HISTORY_FILE, path, NULL)) {
                    if  (remove_folder(path) < 0)
                        ALOGE("failed to remove folder %s", path);
                    i++;
                    if (i >= max)
                       break;
                }
            }
            closedir(d);
    }
    // abort clean of legacy log folder when there is no more folders expect the ones in history_event
    abort_clean_sd = (i < max);
}

static int do_crashlogd(unsigned int files)
{
    int fd, fd1;
    int wd;
    int index_prod = 0;
    char buffer[PATHMAX];
    char *offset = NULL;
    struct inotify_event *event;
    int len, tmp_len;
    int i = 0;
    unsigned int j = 0;
    char path[PATHMAX];
    char destion[PATHMAX];
    char date_tmp[32];
    char date_tmp_2[32];
    struct stat info;
    int dir;
    long long tm;
    int hours, seconds, minutes;
    time_t t;
    char cmd[512] = { '\0', };
    char key[SHA1_DIGEST_LENGTH+1];
    char current_key[2][SHA1_DIGEST_LENGTH+1];
    struct tm *time_tmp;
    monitor_crashenv();
    check_crashlog_dead();
    fd = inotify_init();
    if (fd < 0) {
        ALOGE("inotify_init failed, %s\n", strerror(errno));
        return 1;
    }
    ALOGW("The value of fd = %d",fd);
    for (i = WDCOUNT_START; i < WDCOUNT; i++) {
        wd = inotify_add_watch(fd, wd_array[i].filename,
             wd_array[i].mask);
        if (wd < 0) {
            ALOGE("Can't add watch for %s. and the value of wd = %d.\n", wd_array[i].filename,wd);
            ALOGE("%d",errno);
            return -1;
        }
        wd_array[i].wd = wd;
        ALOGW("%s has been snooped\n", wd_array[i].filename);
    }

    while ((len = read(fd, buffer, sizeof(buffer)))) {
        /* clean children to avoid zombie processes */
        while(waitpid(-1, NULL, WNOHANG) > 0){};
        offset = buffer;
        event = (struct inotify_event *)buffer;
        while (((char *)event - buffer) < len) {
            /* for dir to be delete */
            if((event->mask & IN_DELETE_SELF) ||(event->mask & IN_MOVE_SELF)){
                for (i = WDCOUNT_START; i < WDCOUNT; i++) {
                    if (event->wd != wd_array[i].wd)
                        continue;
                    mkdir(wd_array[i].filename, 0777);
                    wd = inotify_add_watch(fd, wd_array[i].filename, wd_array[i].mask);
                    if (wd < 0) {
                        ALOGE("Can't add watch for %s.\n", wd_array[i].filename);
                        return -1;
                    }
                    wd_array[i].wd = wd;
                    ALOGW("%s has been deleted or moved, we watch it again.\n", wd_array[i].filename);
                }
            }
            if (!(event->mask & IN_ISDIR)) {
                for (i = WDCOUNT_START; i < WDCOUNT; i++) {
                    if (event->wd != wd_array[i].wd)
                        continue;
                    if(!event->len){
                        /* for file */
                        if (!memcmp(wd_array[i].filename,HISTORY_UPTIME,strlen(HISTORY_UPTIME))) {
                            if (!get_uptime(&tm)) {
                                hours = (int) (tm / 1000000000LL);
                                seconds = hours % 60; hours /= 60;
                                minutes = hours % 60; hours /= 60;
                                snprintf(date_tmp,sizeof(date_tmp),"%04d:%02d:%02d",hours, minutes,seconds);
                                snprintf(destion,sizeof(destion),"#V1.0 %-16s%-24s",wd_array[i].eventname,date_tmp);
                                fd1 = open(HISTORY_FILE,O_RDWR);
                                if (fd1 > 0) {
                                    write(fd1,destion,strlen(destion));
                                    close(fd1);
                                }
                                if (!abort_clean_sd)
                                    clean_crashlog_in_sd(SDCARD_ALOGS_DIR, 10);
                                /*Update event every 12 hours*/
                                if ((hours / UPTIME_HOUR_FREQUENCY) >= loop_uptime_event) {
                                    time(&t);
                                    time_tmp = localtime((const time_t *)&t);
                                    PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                                    compute_key(key, PER_UPTIME, NULL);
                                    history_file_write(PER_UPTIME, NULL, NULL, NULL, date_tmp, key, date_tmp_2);
                                    del_file_more_lines(HISTORY_FILE);
                                    loop_uptime_event = (hours / UPTIME_HOUR_FREQUENCY) + 1;
                                    notify_crashreport();
                                    restart_profile2_srv();
                                    check_running_power_service();
                                }
                            }
                        }
                        break;
                    }
                    else{
                        /* for modem reset */
                        if(strstr(event->name, wd_array[i].cmp) && (strstr(event->name, "apimr.txt" ) ||strstr(event->name, "mreset.txt" ) )){
                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            compute_key(key, CRASHEVENT, wd_array[i].eventname);

                            dir = find_dir(files,CRASH_MODE);
                            if (dir == -1) {
                                ALOGE("find dir %d for modem reset failed\n", files);
                                ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname);
                                history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, NULL, NULL, key, date_tmp_2);
                                del_file_more_lines(HISTORY_FILE);
                                notify_crashreport();
                                break;
                            }

                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            if((stat(path, &info) == 0) && (info.st_size != 0)){
                                snprintf(destion,sizeof(destion),"%s%d/%s", CRASH_DIR,dir,event->name);
                                do_copy(path, destion, FILESIZE_MAX);
                            }
                            snprintf(destion,sizeof(destion),"%s%d/", CRASH_DIR,dir);
                            ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname, destion);
                            usleep(TIMEOUT_VALUE);
                            do_log_copy(wd_array[i].eventname,dir,date_tmp,APALOG_TYPE);
                            do_log_copy(wd_array[i].eventname,dir,date_tmp,BPALOG_TYPE);
                            history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, destion, NULL, key, date_tmp_2);
                            del_file_more_lines(HISTORY_FILE);
                            notify_crashreport();
                            break;
                        }
                        /* for modem crash */
                        else if(strstr(event->name, wd_array[i].cmp) && strstr(event->name, "mpanic.txt" )){
                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            compute_key(key, CRASHEVENT, wd_array[i].eventname);
                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            dir = find_dir(files,CRASH_MODE);
                            if (dir == -1) {
                                ALOGE("find dir %d for modem crash failed\n", files);
                                ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname);
                                history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, NULL, NULL, key, date_tmp_2);
                                del_file_more_lines(HISTORY_FILE);
                                remove(path);
                                notify_crashreport();
                                break;
                            }

                            snprintf(destion,sizeof(destion),"%s%d", CRASH_DIR,dir);
                            int status = mv_modem_crash(wd_array[i].filename, destion);
                            if (status != 0)
                                ALOGE("backup modem core dump status: %d.\n", status);
                            snprintf(destion,sizeof(destion),"%s%d/%s", CRASH_DIR,dir,event->name);
                            do_copy(path, destion, 0);
                            snprintf(destion,sizeof(destion),"%s%d/", CRASH_DIR,dir);

                            ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname, destion);
                            usleep(TIMEOUT_VALUE);
                            do_log_copy(wd_array[i].eventname,dir,date_tmp,APALOG_TYPE);
                            do_log_copy(wd_array[i].eventname,dir,date_tmp,BPALOG_TYPE);
                            history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, destion, NULL, key, date_tmp_2);
                            del_file_more_lines(HISTORY_FILE);
                            remove(path);
                            notify_crashreport();
                            break;
                        }
                        /* for full dropbox */
                        else if(strstr(event->name, wd_array[i].cmp) && (strstr(event->name, ".lost" ))){
                            char lostevent[32] = { '\0', };
                            char lostevent_subtype[32] = { '\0', };
                            if (strstr(event->name, "anr"))
                                strcpy(lostevent, ANR_CRASH);
                            else if (strstr(event->name, "crash"))
                                strcpy(lostevent, JAVA_CRASH);
                            else if (strstr(event->name, "watchdog"))
                                strcpy(lostevent, SYSSERVER_WDT);
                            else
                                break;
                            snprintf(lostevent_subtype, sizeof(lostevent_subtype), "%s_%s", LOST, lostevent);

                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            compute_key(key, CRASHEVENT, lostevent);

                            dir = find_dir(files,CRASH_MODE_NOSD);
                            if (dir == -1) {
                                ALOGE("find dir %d for lost dropbox failed\n", files);
                                ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, lostevent);
                                history_file_write(CRASHEVENT, lostevent, lostevent_subtype, NULL, NULL, key, date_tmp_2);
                                del_file_more_lines(HISTORY_FILE);
                                notify_crashreport();
                                break;
                            }

                            snprintf(destion,sizeof(destion),"%s%d/",CRASH_DIR,dir);
                            ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, lostevent, destion);
                            usleep(TIMEOUT_VALUE);
                            do_log_copy(lostevent,dir,date_tmp,APALOG_TYPE);
                            history_file_write(CRASHEVENT, lostevent, lostevent_subtype, destion, NULL, key, date_tmp_2);
                            del_file_more_lines(HISTORY_FILE);
                            notify_crashreport();
                            break;
                        }
                        /* for hprof */
                        else if(strstr(event->name, wd_array[i].cmp) && (strstr(event->name, ".hprof" ))){
                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            compute_key(key, CRASHEVENT, HPROF);

                            dir = find_dir(files,CRASH_MODE);
                            if (dir == -1) {
                                ALOGE("find dir %d for hprof failed\n", files);
                                break;
                            }
                            /*copy hprof file*/
                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            snprintf(destion,sizeof(destion),"%s%d/%s", CRASH_DIR,dir,event->name);
                            do_copy(path, destion, 0);
                            remove(path);
                            snprintf(destion,sizeof(destion),"%s%d/",CRASH_DIR,dir);
                            ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, HPROF, destion);
                            history_file_write(CRASHEVENT, HPROF, NULL, destion, NULL, key, date_tmp_2);
                            del_file_more_lines(HISTORY_FILE);
                            notify_crashreport();
                            break;
                        }
                        // for aplog and bz trigger
                        else if((strcmp(wd_array[i].eventname, APALOGTRIGGER)==0) && (strstr(event->name, "_trigger" ))){
                            char *p;
                            char tmp[PATHMAX] = {'\0',};
                            int nbPacket,aplogDepth;
                            int aplogIsPresent;
                            char value[PROPERTY_VALUE_MAX];
                            property_get(PROP_APALOG_DEPTH, value, PROP_APALOG_DEPTH_DEF);
                            aplogDepth = atoi(value);
                            if (aplogDepth < 0)
                                aplogDepth = 0;
                            property_get(PROP_APALOG_NB_PACKET, value, PROP_APALOG_NB_PACKET_DEF);
                            nbPacket = atoi(value);
                            if (nbPacket < 0)
                                nbPacket = 0;

                            int j,k;
                            aplogIsPresent = 0;
                            dir = -1;
                            /*copy data file*/
                            for( j=0; j < nbPacket ; j++){
                                if(strstr(event->name, "aplog_trigger" ))
                                    dir=-1;
                                for(k=0;k < aplogDepth ; k++) {
                                    aplogIsPresent = 1;
                                    if ((j == 0) && (k == 0))
                                        snprintf(path, sizeof(path),"%s",APALOG_FILE_0);
                                    else
                                        snprintf(path, sizeof(path),"%s.%d",APALOG_FILE_0,(j*aplogDepth)+k);

                                    if(stat(path, &info) == 0){
                                        if(k == 0 && strstr(event->name, "aplog_trigger" )){
                                            dir = find_dir(files,APALOGS_MODE);
                                            if (dir == -1) {
                                                ALOGE("find dir %d for aplog trigger failed\n", files);
                                                //No need to write in the history event in this case
                                                break;
                                             }
                                        }
                                        if ((j == 0) && (k == 0)) {
                                            if(!strncmp(event->name, BZTRIGGER, sizeof(BZTRIGGER))) {
                                                dir = find_dir(files,BZ_MODE);
                                                if (dir == -1) {
                                                    ALOGE("find dir %d for BZ trigger failed\n", files);
                                                    //No need to write in the history event in this case
                                                    break;
                                                }
                                                snprintf(destion,sizeof(destion),"%s%d/aplog", BZ_DIR,dir);
                                            }
                                            else if(strstr(event->name, "aplog_trigger" ))
                                                snprintf(destion,sizeof(destion),"%s%d/aplog", APALOGS_DIR,dir);
                                        }
                                        else{
                                            if(!strncmp(event->name, BZTRIGGER, sizeof(BZTRIGGER)))
                                                snprintf(destion,sizeof(destion),"%s%d/aplog.%d", BZ_DIR,dir,(j*aplogDepth)+k);
                                            else if(strstr(event->name, "aplog_trigger" ))
                                                snprintf(destion,sizeof(destion),"%s%d/aplog.%d", APALOGS_DIR,dir,(j*aplogDepth)+k);
                                        }
                                        do_copy(path, destion, 0);
                                    }
                                    else {
                                        aplogIsPresent = 0;
                                        break;
                                    }
                                }

                                if((k != 0) && (dir != -1) && strstr(event->name, "aplog_trigger" )) {
                                    time(&t);
                                    time_tmp = localtime((const time_t *)&t);
                                    PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                                    compute_key(key, APALOGEVENT, APALOGTRIGGER);
                                    snprintf(destion,sizeof(destion),"%s%d/", APALOGS_DIR,dir);
                                    ALOGE("%-8s%-22s%-20s%s %s\n", APALOGEVENT, key, date_tmp_2, event->name, destion);
                                    history_file_write(APALOGEVENT, APALOGTRIGGER, NULL, destion, NULL, key, date_tmp_2);
                                    del_file_more_lines(HISTORY_FILE);
                                    notify_crashreport();
                                    restart_profile2_srv();
                                }
                                if(aplogIsPresent == 0)
                                    break;
                            }

                            if(-1!=dir && !strncmp(event->name, BZTRIGGER, sizeof(BZTRIGGER))) {
                                    snprintf(destion,sizeof(destion),"%s%d/bz_description", BZ_DIR,dir);
                                    snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                                    do_copy(path,destion,0);
                                    time(&t);
                                    time_tmp = localtime((const time_t *)&t);
                                    PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                                    compute_key(key, BZEVENT, BZMANUAL);
                                    snprintf(destion,sizeof(destion),"%s%d", BZ_DIR,dir);
                                    do_screenshot_copy(path,destion);
                                    snprintf(destion,sizeof(destion),"%s%d/", BZ_DIR,dir);
                                    ALOGE("%-8s%-22s%-20s%s %s\n", BZEVENT, key, date_tmp_2, BZMANUAL, destion);
                                    history_file_write(BZEVENT, BZMANUAL, NULL, destion, NULL, key, date_tmp_2);
                                    del_file_more_lines(HISTORY_FILE);
                                    notify_crashreport();
                                    restart_profile2_srv();
                            }
                            /*delete trigger file*/
                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            remove(path);
                            break;
                        }
                        // for STATS trigger
                        else if((strcmp(wd_array[i].eventname,STATSTRIGGER)==0) && (strstr(event->name, "trigger" ))){
                            char *p;
                            char tmp[32];
                            char type[20] = { '\0', };
                            char tmp_data_name[PATHMAX];

                            snprintf(tmp,sizeof(tmp),"%s",event->name);
                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            dir = find_dir(files,STATS_MODE);
                            if (dir == -1) {
                                ALOGE("find dir %d for stat trigger failed\n", files);
                                p = strstr(tmp,"trigger");
                                if ( p ){
                                    strcpy(p,"data");
                                }
                                compute_key(key, STATSEVENT, tmp);
                                ALOGE("%-8s%-22s%-20s%s\n", STATSEVENT, key, date_tmp_2, tmp);
                                history_file_write(STATSEVENT, tmp, NULL, NULL, NULL, key, date_tmp_2);
                                del_file_more_lines(HISTORY_FILE);
                                notify_crashreport();
                                break;
                            }
                            /*copy data file*/
                            p = strstr(tmp,"trigger");
                            if ( p ){
                                strcpy(p,"data");
                                find_file_in_dir(tmp_data_name,wd_array[i].filename,tmp);
                                snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,tmp_data_name);
                                snprintf(destion,sizeof(destion),"%s%d/%s", STATS_DIR,dir,tmp_data_name);
                                do_copy(path, destion, 0);
                                remove(path);
                            }
                            /*copy trigger file*/
                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            snprintf(destion,sizeof(destion),"%s%d/%s", STATS_DIR,dir,event->name);
                            do_copy(path, destion, 0);
                            remove(path);
                            snprintf(destion,sizeof(destion),"%s%d/", STATS_DIR,dir);
                            compute_key(key, STATSEVENT, tmp);
                            /*create type */
                            snprintf(tmp,sizeof(tmp),"%s",event->name);
                            p = strstr(tmp,"_trigger");
                            if (p) {
                                for (j=0;j<sizeof(type)-1;j++) {
                                    if (p == (tmp+j))
                                        break;
                                    type[j]=toupper(tmp[j]);
                                }
                            } else
                                snprintf(type,sizeof(type),"%s",event->name);
                            ALOGE("%-8s%-22s%-20s%s %s\n", STATSEVENT, key, date_tmp_2, type, destion);
                            if (!strncmp(type, USBBOGUS, sizeof(USBBOGUS))) {
                                usleep(TIMEOUT_VALUE);
                                do_log_copy(type,dir,date_tmp,APALOG_STATS_TYPE);
                            }
                            history_file_write(STATSEVENT, type, NULL, destion, NULL, key, date_tmp_2);
                            del_file_more_lines(HISTORY_FILE);
                            notify_crashreport();
                            break;
                        }
                        /* for anr and UIwatchdog */
                        else if (strstr(event->name, wd_array[i].cmp) && ( strstr(event->name, "anr") || strstr(event->name, "system_server_watchdog"))) {
                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            compute_key(key, CRASHEVENT, wd_array[i].eventname);

                            dir = find_dir(files,CRASH_MODE);
                            if (dir == -1) {
                                ALOGE("find dir %dfor and and UIwatchdog failed\n", files);
                                ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname);
                                del_file_more_lines(HISTORY_FILE);
                                history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, NULL, NULL, key, date_tmp_2);
                                notify_crashreport();
                                restart_profile1_srv();
                                break;
                            }

                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            if (stat(path, &info) == 0) {
                                snprintf(destion,sizeof(destion),"%s%d/%s",CRASH_DIR,dir,event->name);
                                do_copy(path, destion, FILESIZE_MAX);
                                ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname, destion);
                                usleep(TIMEOUT_VALUE);
                                history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, destion, NULL, key, date_tmp_2);
                                do_log_copy(wd_array[i].eventname,dir,date_tmp,APALOG_TYPE);
                                del_file_more_lines(HISTORY_FILE);
                                backtrace_anr_uiwdt(destion, dir);
                                if (strstr(event->name, "anr")) {
                                    snprintf(destion,sizeof(destion),"%s%d",CRASH_DIR,dir);
                                    char value[PROPERTY_VALUE_MAX];
                                    property_get(PROP_ALOGSYSTEMSTATE, value, "stopped");
                                    if(strcmp(value,"running") == 0){
                                        ALOGE("Can't launch dumpstate for %s.\n", destion);
                                    }else{
                                        wd = inotify_add_watch(fd, destion, IN_CLOSE_WRITE);
                                        if (wd < 0) {
                                            ALOGE("Can't add watch for %s.\n", destion);
                                            notify_crashreport();
                                            break;
                                        }
                                        strcpy(current_key[index_prod],key);
                                        index_prod = (index_prod + 1) % 2;
                                        start_dumpstate_srv(CRASH_DIR, dir);
                                    }


                                }
                                notify_crashreport();
                                restart_profile1_srv();
                            }
                            break;
                        }
                        /* for other case */
                        else if (strstr(event->name, wd_array[i].cmp)) {
                            time(&t);
                            time_tmp = localtime((const time_t *)&t);
                            PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
                            PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
                            compute_key(key, CRASHEVENT, wd_array[i].eventname);

                            dir = find_dir(files,CRASH_MODE);
                            if (dir == -1) {
                                ALOGE("find dir %d for other crashes failed\n", files);
                                ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname);
                                history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, NULL, NULL, key, date_tmp_2);
                                del_file_more_lines(HISTORY_FILE);
                                notify_crashreport();
                                break;
                            }

                            snprintf(path, sizeof(path),"%s/%s",wd_array[i].filename,event->name);
                            if (stat(path, &info) == 0) {
                                if (strstr(event->name, ".core" ))
                                    backup_apcoredump(dir, event->name, path);
                                else
                                {
                                    snprintf(destion,sizeof(destion),"%s%d/%s",CRASH_DIR,dir,event->name);
                                    do_copy(path, destion, FILESIZE_MAX);
                                    /* parse anr file */
                                    if (strstr(event->name, "anr") || strstr(event->name, "system_server_watchdog")){
                                        backtrace_anr_uiwdt(destion, dir);
                                        restart_profile1_srv();
                                        if(strstr(event->name, "anr")){
                                            snprintf(destion,sizeof(destion),"%s%d",CRASH_DIR,dir);
                                            char value[PROPERTY_VALUE_MAX];
                                            property_get(PROP_ALOGSYSTEMSTATE, value, "stopped");
                                            if(strcmp(value,"running") == 0){
                                                ALOGE("Can't launch dumpstate for %s.\n", destion);
                                            }else{
                                                wd = inotify_add_watch(fd, destion, IN_CLOSE_WRITE);
                                                if (wd < 0) {
                                                    ALOGE("Can't add watch for %s.\n", destion);
                                                    notify_crashreport();
                                                    break;
                                                }
                                                strcpy(current_key[index_prod],key);
                                                index_prod = (index_prod + 1) % 2;
                                                start_dumpstate_srv(CRASH_DIR, dir);
                                            }
                                        }
                                    }
                                }
                                snprintf(destion,sizeof(destion),"%s%d/",CRASH_DIR,dir);
                                ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, wd_array[i].eventname, destion);
                                usleep(TIMEOUT_VALUE);
                                do_log_copy(wd_array[i].eventname,dir,date_tmp,APALOG_TYPE);
                                history_file_write(CRASHEVENT, wd_array[i].eventname, NULL, destion, NULL, key, date_tmp_2);
                                del_file_more_lines(HISTORY_FILE);
                                if (strstr(event->name, "crash") || strstr(event->name, "tombstone")) {
                                    snprintf(destion,sizeof(destion),"%s%d",CRASH_DIR,dir);
                                    char value[PROPERTY_VALUE_MAX];
                                    property_get(PROP_ALOGSYSTEMSTATE, value, "stopped");
                                    if(strcmp(value,"running") == 0){
                                        ALOGE("Can't launch dumpstate for %s.\n", destion);
                                    }else{
                                        wd = inotify_add_watch(fd, destion, IN_CLOSE_WRITE);
                                        if (wd < 0) {
                                            ALOGE("Can't add watch for %s.\n", destion);
                                            notify_crashreport();
                                            break;
                                        }
                                        strcpy(current_key[index_prod],key);
                                        index_prod = (index_prod + 1) % 2;
                                        start_dumpstate_srv(CRASH_DIR,  dir);
                                    }
                                }
                                notify_crashreport();
                            }
                            break;
                        }
                    }
                }
                if(event->len && !strncmp(event->name, "dropbox-", 8) && (i==WDCOUNT)){
                    inotify_rm_watch(fd, event->wd);
                   // notify_crash_to_upload(current_key[index_cons]);
                   // index_cons = (index_cons + 1) % 2;
                }
            }
            tmp_len = sizeof(struct inotify_event) + event->len;
            event = (struct inotify_event *)(offset + tmp_len);
            offset += tmp_len;
        }

    }
    return 0;
}

void check_running_logs_service()
{
    char logservice[PROPERTY_VALUE_MAX];
    char logenable[PROPERTY_VALUE_MAX];

    property_get("init.svc.apk_logfs", logservice, "");
    property_get("persist.service.apklogfs.enable", logenable, "");
    if (strcmp(logservice, "running") && !strcmp(logenable, "1")) {
        ALOGE("log service stopped whereas property is set .. restarting\n");
        property_set("ctl.start", "apk_logfs");
    }
}

void do_timeup()
{
    int fd;

    while (1) {
        check_running_logs_service();
        sleep(UPTIME_FREQUENCY);
        fd = open(HISTORY_UPTIME, O_RDWR | O_CREAT, 0666);
        if (fd < 0)
            ALOGE("can not open file: %s\n", HISTORY_UPTIME);
        else
            close(fd);
    }
}

struct fabric_type {
    char *keyword;
    char *tail;
    char *name;
};

struct fabric_type ft_array[] = {
    {"DW0:", "f501", "MEMERR"},
    {"DW0:", "f502", "INSTERR"},
    {"DW0:", "f504", "SRAMECCERR"},
    {"DW0:", "00dd", "HWWDTALOGERR"},
};
struct fabric_type fake[] = {
    {"DW2:", "02608002", "FABRIC_FAKE"},
    {"DW3:", "40102ff4", "FABRIC_FAKE"},
};

static int crashlog_check_fabric(char *reason, unsigned int files)
{
    char date_tmp[32];
    char date_tmp_2[32];
    struct stat info;
    time_t t;
    char destion[PATHMAX];
    char crashtype[32] = {'\0'};
    int dir;
    unsigned int i = 0;
    char key[SHA1_DIGEST_LENGTH+1];
    struct tm *time_tmp;

    if ((stat(PROC_FABRIC_ERROR_NAME, &info) == 0)  || (test_flag == 1)) {

        strcpy(crashtype, FABRIC_ERROR);
        for (i = 0; i < sizeof(ft_array)/sizeof(struct fabric_type); i++)
            if (!find_str_in_file(SAVED_FABRIC_ERROR_NAME, ft_array[i].keyword, ft_array[i].tail))
                   strncpy(crashtype, ft_array[i].name, sizeof(crashtype)-1);
        if ((!find_str_in_file(SAVED_FABRIC_ERROR_NAME, fake[0].keyword, fake[0].tail)) &&
          (!find_str_in_file(SAVED_FABRIC_ERROR_NAME, fake[1].keyword, fake[1].tail))) {
                   strncpy(crashtype, fake[0].name, sizeof(crashtype)-1);
                   if (!strncmp(reason, "HWWDT_RESET", 11))
                       strcat(reason,"_FAKE");
        }
        time(&t);
        time_tmp = localtime((const time_t *)&t);
        PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
        PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
        dir = find_dir(files,CRASH_MODE);

        if (dir == -1) {
            ALOGE("find dir %d for check fabric failed\n", files);
            compute_key(key, CRASHEVENT, crashtype);
            ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, crashtype);
            history_file_write(CRASHEVENT, crashtype, NULL, NULL, NULL, key, date_tmp_2);
            del_file_more_lines(HISTORY_FILE);
            //Need to return 0 to avoid closing crashlogd
            return 0;
        }

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/%s_%s.txt", CRASH_DIR, dir,
                FABRIC_ERROR_NAME, date_tmp);
        do_copy(SAVED_FABRIC_ERROR_NAME, destion, FILESIZE_MAX);
        do_last_kmsg_copy(dir);

        destion[0] = '\0';
        snprintf(destion,sizeof(destion),"%s%d/",CRASH_DIR,dir);
        compute_key(key, CRASHEVENT, crashtype);
        ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, crashtype, destion);
        history_file_write(CRASHEVENT, crashtype, NULL, destion, NULL, key, date_tmp_2);
        del_file_more_lines(HISTORY_FILE);
    }
    return 0;
}

static int crashlog_check_panic(char *reason, unsigned int files)
{
    char date_tmp[32];
    char date_tmp_2[32];
    struct stat info;
    time_t t;
    char destion[PATHMAX];
    char crashtype[32] = {'\0'};
    int dir;
    char key[SHA1_DIGEST_LENGTH+1];
    struct tm *time_tmp;

    if ((stat(PANIC_CONSOLE_NAME, &info) == 0) || (test_flag == 1)) {

        if (!find_str_in_file(SAVED_CONSOLE_NAME, "Kernel panic - not syncing: Kernel Watchdog", NULL)) {
            strcpy(crashtype, KERNEL_FORCE_CRASH);
        } else if (!find_str_in_file(SAVED_CONSOLE_NAME, "EIP is at panic_dbg_set", NULL) || !find_str_in_file(SAVED_CONSOLE_NAME, "EIP is at kwd_trigger_open", NULL)) {
            if (!strncmp(reason, "SWWDT_RESET", 11))
                  strcat(reason,"_FAKE");
            strcpy(crashtype, KERNEL_FAKE_CRASH);
        } else
            strcpy(crashtype, KERNEL_CRASH);

        time(&t);
        time_tmp = localtime((const time_t *)&t);
        PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
        PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
        dir = find_dir(files,CRASH_MODE);

        if (dir == -1) {
            ALOGE("find dir %d for check panic failed\n", files);
            compute_key(key, CRASHEVENT, crashtype);
            ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, crashtype);
            history_file_write(CRASHEVENT, crashtype, NULL, NULL, NULL, key, date_tmp_2);
            del_file_more_lines(HISTORY_FILE);
            //Need to return 0 to avoid closing crashlogd
            return 0;
        }

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/%s_%s.txt", CRASH_DIR, dir,
                THREAD_NAME, date_tmp);
        do_copy(SAVED_THREAD_NAME, destion, FILESIZE_MAX);
        snprintf(destion,sizeof(destion),"%s%d/",CRASH_DIR,dir);

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/%s_%s.txt", CRASH_DIR, dir,
                CONSOLE_NAME, date_tmp);
        do_copy(SAVED_CONSOLE_NAME, destion, FILESIZE_MAX);

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/%s_%s.txt", CRASH_DIR, dir,
                ALOGCAT_NAME, date_tmp);
        do_copy(SAVED_ALOGCAT_NAME, destion, FILESIZE_MAX);
        do_last_kmsg_copy(dir);

        write_file(PANIC_CONSOLE_NAME, "1");

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/", CRASH_DIR, dir);
        compute_key(key, CRASHEVENT, crashtype);
        ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, crashtype, destion);
        history_file_write(CRASHEVENT, crashtype, NULL, destion, NULL, key, date_tmp_2);
        del_file_more_lines(HISTORY_FILE);
    }
    return 0;
}

static int crashlog_check_modem_shutdown(char *reason, unsigned int files)
{
    char date_tmp[32];
    char date_tmp_2[32];
    struct stat info;
    time_t t;
    char destion[PATHMAX];
    int dir;
    struct tm *time_tmp;
    char key[SHA1_DIGEST_LENGTH+1];

    if (stat(MODEM_SHUTDOWN_TRIGGER, &info) == 0) {

        time(&t);
        time_tmp = localtime((const time_t *)&t);
        PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
        PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
        compute_key(key, CRASHEVENT, MODEM_SHUTDOWN);

        dir = find_dir(files,CRASH_MODE);
        if (dir == -1) {
            ALOGE("find dir %d for check modem shutdown failed\n", files);
            ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, MODEM_SHUTDOWN);
            history_file_write(CRASHEVENT, MODEM_SHUTDOWN, NULL, NULL, NULL, key, date_tmp_2);
            del_file_more_lines(HISTORY_FILE);
            remove(MODEM_SHUTDOWN_TRIGGER);
            //Need to return 0 to avoid closing crashlogd
            return 0;
        }

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/", CRASH_DIR, dir);

        ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, MODEM_SHUTDOWN, destion);
        usleep(TIMEOUT_VALUE);
        do_log_copy(MODEM_SHUTDOWN, dir, date_tmp, APALOG_TYPE);
        do_last_kmsg_copy(dir);
        history_file_write(CRASHEVENT, MODEM_SHUTDOWN, NULL, destion, NULL, key, date_tmp_2);
        del_file_more_lines(HISTORY_FILE);
        remove(MODEM_SHUTDOWN_TRIGGER);
    }
    return 0;
}

static int crashlog_check_recovery(unsigned int files)
{
    char date_tmp[32];
    char date_tmp_2[32];
    struct stat info;
    time_t t;
    char destion[PATHMAX];
    char destion2[PATHMAX];
    int dir;
    struct tm *time_tmp;
    char key[SHA1_DIGEST_LENGTH+1];

    //Check if trigger file exists
    if (stat(RECOVERY_ERROR_TRIGGER, &info) == 0) {
        // compute dates
        time(&t);
        time_tmp = localtime((const time_t *)&t);
        PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
        PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
        // compute crash id
        compute_key(key, CRASHEVENT, RECOVERY_ERROR);

        // get output crash dir
        dir = find_dir(files,CRASH_MODE);

        if (dir == -1) {
            ALOGE("find dir %d for check recovery failed\n", files);
            ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, RECOVERY_ERROR);
            history_file_write(CRASHEVENT, RECOVERY_ERROR, NULL, NULL, NULL, key, date_tmp_2);
            del_file_more_lines(HISTORY_FILE);
            // remove trigger file
            remove(RECOVERY_ERROR_TRIGGER);
            //Need to return 0 to avoid closing crashlogd
            return 0;
        }

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/", CRASH_DIR, dir);

        ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, RECOVERY_ERROR, destion);
        //copy log
        destion2[0] = '\0';
        snprintf(destion2, sizeof(destion2), "%s%s", destion, "recovery_last_log");
        do_copy(RECOVERY_ERROR_ALOG, destion2, FILESIZE_MAX);
        do_last_kmsg_copy(dir);
        //Write event in history_event
        history_file_write(CRASHEVENT, RECOVERY_ERROR, NULL, destion, NULL, key, date_tmp_2);
        del_file_more_lines(HISTORY_FILE);
        // remove trigger file
        remove(RECOVERY_ERROR_TRIGGER);
    }
    return 0;
}

static int crashlog_check_startupreason(char *reason, unsigned int files)
{
    char date_tmp[32];
    char date_tmp_2[32];
    time_t t;
    char destion[PATHMAX];
    int dir;
    char key[SHA1_DIGEST_LENGTH+1];
    struct tm *time_tmp;

    if ((strstr(reason, "WDT_RESET")) && (!strstr(reason, "FAKE"))) {

        time(&t);
        time_tmp = localtime((const time_t *)&t);
        PRINT_TIME(date_tmp, TIME_FORMAT_1, time_tmp);
        PRINT_TIME(date_tmp_2, TIME_FORMAT_2, time_tmp);
        compute_key(key, CRASHEVENT, "WDT");

        dir = find_dir(files,CRASH_MODE);
        if (dir == -1) {
            ALOGE("find dir %d for check startup reason failed\n", files);
            ALOGE("%-8s%-22s%-20s%s\n", CRASHEVENT, key, date_tmp_2, "WDT");
            history_file_write(CRASHEVENT, "WDT", reason, NULL, NULL, key, date_tmp_2);
            del_file_more_lines(HISTORY_FILE);
            //Need to return 0 to avoid closing crashlogd
            return 0;
        }

        destion[0] = '\0';
        snprintf(destion, sizeof(destion), "%s%d/", CRASH_DIR, dir);
        ALOGE("%-8s%-22s%-20s%s %s\n", CRASHEVENT, key, date_tmp_2, "WDT", destion);
        flush_aplog_atboot("WDT", dir, date_tmp);
        usleep(TIMEOUT_VALUE);
        do_log_copy("WDT", dir, date_tmp, APALOG_TYPE);
        do_last_kmsg_copy(dir);
        history_file_write(CRASHEVENT, "WDT", reason, destion, NULL, key, date_tmp_2);
        del_file_more_lines(HISTORY_FILE);
    }
    return 0;
}

static int file_read_value(const char *path, char *value, const char *default_value)
{
    struct stat info;
    FILE *fd;
    int ret = -1;

    if ( stat(path, &info) == 0 ) {
        fd = fopen(path, "r");
        if (fd == NULL){
            ALOGE("can not open file: %s\n", ALOG_BUILDID);
            return -1;
        }
        ret = fscanf(fd, "%s", value);
        fclose(fd);
        if (ret == 1)
            return 0;
    }
    if (default_value) {
        strcpy(value, default_value);
        return ret;
    } else {
        return ret;
    }
}

static void write_uuid(char *uuid_value)
{
    FILE *fd;

    fd = fopen(ALOG_UUID, "w");
    if (fd == NULL){
        ALOGE("can not open file: %s\n", ALOG_BUILDID);
        return;
    }
    fprintf(fd, "%s", uuid_value);
    fclose(fd);
    do_chown(ALOG_UUID, PERM_USER, PERM_GROUP);
}

static void read_uuid(void)
{
    char temp_uuid[256];
    struct stat info;
    FILE *fd;
    if (file_read_value(PROC_UUID, uuid, "MSM8x25") != 0) {
        write_uuid(uuid);
        ALOGE("PROC_UUID error\n");
        return;
    }
    file_read_value(ALOG_UUID, temp_uuid, "");
    if (strcmp(uuid, temp_uuid) != 0)
        write_uuid(uuid);
}

static int swupdated(char *buildname)
{
    struct stat info;
    FILE *fd;
    char currentbuild[PROPERTY_VALUE_MAX];

    if (stat(ALOG_BUILDID, &info) == 0) {

        fd = fopen(ALOG_BUILDID, "r");
        if (fd == NULL){
            ALOGE("can not open file: %s\n", ALOG_BUILDID);
            return 0;
        }
        fscanf(fd, "%s", currentbuild);
        fclose(fd);

        if (strcmp(currentbuild, buildname)) {
            fd = fopen(ALOG_BUILDID, "w");
            if (fd == NULL){
                ALOGE("can not open file: %s\n", ALOG_BUILDID);
                return 0;
            }
            do_chown(ALOG_BUILDID, "root", "log");
            fprintf(fd, "%s", buildname);
            fclose(fd);
            ALOGI("Reset history after build update -> %s\n", buildname);
            return 1;
        }
    } else {
        fd = fopen(ALOG_BUILDID, "w");
        if (fd == NULL){
            ALOGE("can not open file: %s\n", ALOG_BUILDID);
            return 0;
        }
        do_chown(ALOG_BUILDID, "root", "log");
        fprintf(fd, "%s", buildname);
        fclose(fd);
        ALOGI("Reset history after blank device update -> %s\n", buildname);
        return 1;

    }
    return 0;
}


static void reset_history(void)
{
    FILE *to;
    int fd;

    to = fopen(HISTORY_FILE, "w");
    if (to == NULL){
        ALOGE("can not open file: %s\n", HISTORY_FILE);
        return;
    }
    do_chown(HISTORY_FILE, PERM_USER, PERM_GROUP);
    fprintf(to, "#V1.0 %-16s%-24s\n", CURRENT_UPTIME, "0000:00:00");
    fprintf(to, "#EVENT  ID                    DATE                 TYPE\n");
    fclose(to);

    fd = open(HISTORY_UPTIME, O_RDWR | O_CREAT, 0666);
    if (fd < 0){
        ALOGE("open HISTORY_UPTIME error\n");
        return;
    }
    close(fd);
}

static void reset_crashlog(void)
{
    char path[PATHMAX];
    FILE *fd;

    snprintf(path, sizeof(path), CRASH_CURRENT_ALOG);
    fd = fopen(path, "w");
    if (fd == NULL){
        ALOGE("can not open file: %s\n", path);
        return;
    }
    fprintf(fd, "%4d", 0);
    fclose(fd);
}

static void reset_logs(char *path)
{
    char file[PATHMAX];
    DIR *d;
    struct dirent* de;
    d = opendir(path);

    if (d == 0)
        return;

    while ((de = readdir(d)) != 0) {
        if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
            continue;
        else {
            snprintf(file, sizeof(file), "%s/%s", path, de->d_name);
            remove(file);
        }
    }
    closedir(d);
}

static void reset_statslog(void)
{
    char path[PATHMAX];
    FILE *fd;
    snprintf(path, sizeof(path), STATS_CURRENT_ALOG);
    fd = fopen(path, "w");
    if (fd == NULL){
        ALOGE("can not open file: %s\n", path);
        return;
    }
    fprintf(fd, "%4d", 0);
    fclose(fd);
}

static void reset_aplogslog(void)
{
    char path[PATHMAX];
    FILE *fd;
    snprintf(path, sizeof(path), APALOGS_CURRENT_ALOG);
    fd = fopen(path, "w");
    if (fd == NULL){
        ALOGE("can not open file: %s\n", path);
        return;
    }
    fprintf(fd, "%4d", 0);
    fclose(fd);
}

static void reset_bzlog(void)
{
    char path[PATHMAX];
    FILE *fd;
    snprintf(path, sizeof(path), BZ_CURRENT_ALOG);
    fd = fopen(path, "w");
    if (fd == NULL){
        ALOGE("can not open file: %s\n", path);
        return;
    }
    fprintf(fd, "%4d", 0);
    fclose(fd);
}
static void reset_swupdate(void)
{
    reset_logs(HISTORY_CORE_DIR);
    reset_logs(ALOGS_MODEM_DIR);
    reset_crashlog();
    reset_statslog();
    reset_aplogslog();
    reset_bzlog();
    reset_history();
}

static void uptime_history(char *lastuptime)
{
    FILE *to;
    int fd;
    struct stat info;

    char name[32];
    char date_tmp[32];
    struct tm *time_tmp;
    time_t t;

    to = fopen(HISTORY_FILE, "r");
    if (to == NULL){
        ALOGE("can not open file: %s\n", HISTORY_FILE);
        return;
    }
    fscanf(to, "#V1.0 %16s%24s\n", name, lastuptime);
    fclose(to);
    if (!memcmp(name, CURRENT_UPTIME, sizeof(CURRENT_UPTIME))) {

        to = fopen(HISTORY_FILE, "r+");
        if (to == NULL){
            ALOGE("can not open file: %s\n", HISTORY_FILE);
            return;
        }
        fprintf(to, "#V1.0 %-16s%-24s\n", CURRENT_UPTIME, "0000:00:00");
        strcpy(name, PER_UPTIME);
        fseek(to, 0, SEEK_END);
        time(&t);
        time_tmp = localtime((const time_t *)&t);
        PRINT_TIME(date_tmp, TIME_FORMAT_2, time_tmp);
        fprintf(to, "%-8s00000000000000000000  %-20s%s\n", name, date_tmp, lastuptime);
        fclose(to);
    }
}

static void read_startupreason(char *startupreason)
{
    char cmdline[512] = { '\0', };
    char *p, *endptr;
    unsigned long reason;
    static const char *bootmode_reason[] = {
        "BATT_INSERT",
        "PWR_BUTTON_PRESS",
        "RTC_TIMER",
        "USB_CHRG_INSERT",
        "Reserved",
        "COLD_RESET",
        "COLD_BOOT",
        "UNKNOWN",
        "SWWDT_RESET",
        "HWWDT_RESET",
        "WATCHDOG_COUNTER_EXCEEDED",
        "POWER_SUPPLY_DETECTED",
        "FASTBOOT_BUTTONS_COMBO",
        "PWR_SMPL",
        "NO_MATCHING_OSIP_ENTRY",
        "CRITICAL_BATTERY",
        "INVALID_CHECKSUM"};

    struct stat info;
    FILE *fd;

    strcpy(startupreason, bootmode_reason[7]);

    if (stat(KERNEL_CMDLINE, &info) == 0) {
        fd = fopen(KERNEL_CMDLINE, "r");
        if (fd == NULL) {
            ALOGE("can not open file: %s\n", KERNEL_CMDLINE);
            return;
        }
        fread(cmdline, 1, sizeof(cmdline)-1, fd);
        fclose(fd);
        p = strstr(cmdline, STARTUP_STR);
        if(p) {
            p += strlen(STARTUP_STR);
            if (!isspace(*p)) {
                errno = 0;
                reason=strtoul(p, &endptr, 16);
                if((errno != ERANGE) && (endptr != p) && \
                   (reason >= 0) ) {
                    switch(reason) {
                        case PWR_SMPL :
                            strcpy(startupreason, bootmode_reason[13]);
                            break;
                        case ADB_REBOOT :
                            strcpy(startupreason, bootmode_reason[12]);
                            break;
                        case  PWR_BUT_P:
                            strcpy(startupreason, bootmode_reason[1]);
                            break;
                        case PWR_USB_BUT :
                            strcpy(startupreason, bootmode_reason[3]);
                            break;
                        default:
                            strcpy(startupreason, "UNKNOWN");
                            break;
                    }
                } else {
                    strcpy(startupreason, "UNKNOWN");
                }
            }
        }
    }
}
static void update_logs_permission(void)
{
    char value[PROPERTY_VALUE_MAX] = "0";

    if (property_get(PROP_COREDUMP, value, "") <= 0) {
        ALOGE("Property %s not readable - core dump capture is disabled\n", PROP_COREDUMP);
    }

    if (!strncmp(value, "1", 1)) {
        ALOGI("Folders /logs and /logs/core set to 0777\n");
        chmod(HISTORY_FILE_DIR,0777);
        chmod(HISTORY_CORE_DIR,0777);
    }
}

int main(int argc, char **argv)
{

    int i;
    int ret = 0;
    unsigned int files = 0xFFFFFFFF;
    char date_tmp[32];
    struct stat info;
    time_t t;
    char destion[PATHMAX];
    char *vinfo;
    pid_t pid;
    unsigned int dir;
    char key[SHA1_DIGEST_LENGTH+1];
    struct tm *time_tmp;

    pthread_t thread;
    char value[PROPERTY_VALUE_MAX];
    if (argc > 2) {
        ALOGE("USAGE: %s [number] \n", argv[0]);
        return -1;
    }
    if (argc == 2) {
        if(!memcmp(argv[1], "-modem", 6)){
            WDCOUNT_START = 8;
            WDCOUNT = WDCOUNT_START + 4;
            ALOGD(" crashlogd only snoop modem \n");
        }
        else if(!memcmp(argv[1], "-test", 5)){
            test_flag = 1;
        }
        else if(!memcmp(argv[1], "-nomodem", 8)){
            WDCOUNT = (WDCOUNT - 3);
            ALOGD(" crashlogd only snoop AP \n");
        }
        else{
            errno = 0;
            files = (unsigned int)strtoul(argv[1], 0, 0);
            if (errno) {
                ALOGE(" saved files number must be digital \n");
                return -1;
            }
        }
    }
    property_get(PROP_CRASH, value, "");
    if (strncmp(value, "1", 1)){
        if (stat(PANIC_CONSOLE_NAME, &info) == 0){
            write_file(PANIC_CONSOLE_NAME, "1");
        }
        return -1;
    }

    if (property_get(BUILD_FIELD, buildVersion, "") <=0){
        get_version_info(SYS_PROP, BUILD_FIELD, buildVersion);
    }
    if (property_get(BOARD_FIELD, boardVersion, "") <=0){
        get_version_info(SYS_PROP, BOARD_FIELD, boardVersion);
    }
    umask(022);
    read_uuid();
    sdcard_available(CRASH_MODE);
    // check startup reason and sw update
    char startupreason[32] = { '\0', };
    char encryptstate[16] = { '\0', };
    struct stat st;
    char lastuptime[32];
    char crypt_state[PROPERTY_VALUE_MAX];
    char encrypt_progress[PROPERTY_VALUE_MAX];
    char decrypt[PROPERTY_VALUE_MAX];
    char token[PROPERTY_VALUE_MAX];

    strcpy(encryptstate,"DECRYPTED");

    property_get("ro.crypto.state", crypt_state, "unencrypted");
    property_get("vold.encrypt_progress",encrypt_progress,"");
    property_get("vold.decrypt", decrypt, "");
    property_get("crashlogd.token", token, "");

    if ((!strcmp(crypt_state, "unencrypted")) && ( !encrypt_progress[0])){
        if (strcmp(token, "")){
            goto next2;}
        ALOGI("phone enter state: normal start.\n");
        if (swupdated(buildVersion)) {
            strcpy(lastuptime, "0000:00:00");
            strcpy(startupreason,"SWUPDATE");
            reset_swupdate();
        }
        else {
            read_startupreason(startupreason);
            uptime_history(lastuptime);
        }
        goto next;
    }
    if (encrypt_progress[0]){
        ALOGI("phone enter state: encrypting.\n");
        strcpy(encryptstate,"DECRYPTED");
        goto next2;
    }
   if ((!strcmp(crypt_state, "encrypted")) && !strcmp(decrypt, "trigger_restart_framework")){
        if (strcmp(token, "")){
            goto next2;}
        ALOGI("phone enter state: phone encrypted.\n");
        strcpy(encryptstate,"ENCRYPTED");
        if (swupdated(buildVersion)) {
            strcpy(lastuptime, "0000:00:00");
            strcpy(startupreason,"SWUPDATE");
            reset_swupdate();
        }
        else {
            read_startupreason(startupreason);
            uptime_history(lastuptime);
        }
        goto next;
    }
next:
    crashlog_check_fabric(startupreason, files);
    crashlog_check_panic(startupreason, files);
    crashlog_check_modem_shutdown(startupreason, files);
    crashlog_check_startupreason(startupreason, files);
    crashlog_check_recovery(files);
    time(&t);
    time_tmp = localtime((const time_t *)&t);
    PRINT_TIME(date_tmp, TIME_FORMAT_2, time_tmp);
    compute_key(key, SYS_REBOOT, startupreason);
    history_file_write(SYS_REBOOT, startupreason, NULL, NULL, lastuptime, key, date_tmp);
    compute_key(key, STATEEVENT, encryptstate);
    history_file_write(STATEEVENT, encryptstate, NULL, NULL, NULL, key, date_tmp);

    del_file_more_lines(HISTORY_FILE);
    notify_crashreport();
next2:
    update_logs_permission();
    ret = pthread_create(&thread, NULL, (void *)do_timeup, NULL);
    if (ret < 0) {
        ALOGE("pthread_create error");
        return -1;
    }
    do_crashlogd(files);

    return 0;

}
