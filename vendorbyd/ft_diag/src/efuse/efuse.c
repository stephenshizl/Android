#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <inttypes.h>
#include <stdbool.h>
#include <sys/mman.h>

#if __LP64__
#define strtoptr strtoull
#else
#define strtoptr strtoul
#endif


static int ft_get_efuse_data(char *address, char *efvalue)
{
    int ret = -1;
    int fd = -1;
    uint32_t value = 0;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);

    uintptr_t addr = strtoptr(address, 0, 16);

    uintptr_t endaddr = addr + 3;

    if (endaddr <= addr) {
        //fprintf(stderr, "end address <= start address\n");
        FT_LOG("[%s,%d] end address <= start address\n", __func__, __LINE__);
        return -1;
    }

    fd = open("/dev/mem", O_RDWR | O_SYNC);
    if(fd < 0) {
        //fprintf(stderr,"cannot open /dev/mem\n");
        FT_LOG("[%s,%d] cannot open /dev/mem\n", __func__, __LINE__);
        return -1;
    }

    off64_t mmap_start = addr & ~(PAGE_SIZE - 1);
    size_t mmap_size = endaddr - mmap_start + 1;
    mmap_size = (mmap_size + PAGE_SIZE - 1) & ~(PAGE_SIZE - 1);

    void* page = mmap64(0, mmap_size, PROT_READ | PROT_WRITE,
                        MAP_SHARED, fd, mmap_start);

    if(page == MAP_FAILED){
        //fprintf(stderr,"cannot mmap region\n");
        FT_LOG("[%s,%d] cannot mmap region\n", __func__, __LINE__);
        return -1;
    }

    uint32_t* x = (uint32_t*) (((uintptr_t) page) + (addr & 4095));

    //fprintf(stderr,"%08"PRIxPTR": %08x\n", addr, *x);
    FT_LOG("[%s,%d] Register Value is: %08x\n", __func__, __LINE__, *x);
    sprintf(efvalue, "%08x", *x);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}

/********************************************
 * Description:
 * Efuse status test
 *
 * Input parameters:
 *
 * Return value:
 * 1: success
 * 0: failed
 ********************************************/
int ft_efuse_read_status(char *addr)
{
    int ret = -1;
    char estatus[100] = {0}; // store efuse status value
    char bstatus[100] = {0}; //store efuse byd status value

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);

    ret = ft_get_efuse_data(EFUSE_SUC_REG, estatus);
    FT_LOG("[%s,%d] eFuse status is: %s\n", __func__, __LINE__, estatus);

    if (ret < 0)
    {
        FT_LOG("[%s,%d] Error, get eFuse status data failed\n", __func__, __LINE__);
        return -1;
    }

    estatus[strlen(estatus)] = '\0';

    if(strcmp(estatus, NOT_EFUSE_FLAG) == 0)
    {
        FT_LOG("[%s,%d] estatus is: %s, the device not eFuse\n", __func__, __LINE__, estatus);
        strcpy(addr, EFUSE_FLAG_NON);
        return 1;
    }

    if(strcmp(estatus, EFUSE_SUC_FLAG) != 0)
    {
        FT_LOG("[%s,%d] Error, eFuse status not 00303030, eFuse failed\n", __func__, __LINE__);
        strcpy(addr, EFUSE_FLAG_FAIL);
        return 1;
    }

    ret = ft_get_efuse_data(EFUSE_BYD_REG, bstatus);
    FT_LOG("[%s,%d] BYD status is: %s\n", __func__, __LINE__, bstatus);

    if (ret < 0)
    {
        FT_LOG("[%s,%d] Error, get eFuse BYD status data failed\n", __func__, __LINE__);
        return -1;
    }

    bstatus[strlen(bstatus)] = '\0';

    if(strcmp(bstatus, EFUSE_BYD_FLAG) != 0)
    {
        FT_LOG("[%s,%d] Error, eFuse BYD status not bf020017, eFuse failed\n", __func__, __LINE__);
        strcpy(addr, EFUSE_FLAG_FAIL);
        return 1;
    }

    FT_LOG("[%s,%d] This device eFuse successful!\n", __func__, __LINE__);
    strcpy(addr, EFUSE_FLAG_SUC);

    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 1;
}

/*//yuhongxia add efuse status diag 20140619
#define READ_EFUSE_CHECK "cat /proc/cmdline"
//#define READ_EFUSE_CHECK "cd proc;cat cmdline"
int ft_efuse_read_status(char *addr)
{
    static char result[10240];
    char *p = result;
    int count = 0;
    FT_LOG("--->>> %s()\n", __func__);
    exe_cmd(READ_EFUSE_CHECK, result, sizeof(result));
    FT_LOG("%s result: %s\n", READ_EFUSE_CHECK, result);
    char *str1 = strstr(result, "androidboot.efuse=0x0");
    FT_LOG("%s yhxde androidboot.efuse=0x0 result: %s\n", READ_EFUSE_CHECK, str1);
    strlcpy(addr, str1, 296);
    FT_LOG("<<<---yhxde %s(): %s\n", __func__, addr);
    return 1;
}*/