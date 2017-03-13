#ifndef PMEM_H
#define PMEM_H
#undef    USE_ION
#define  USE_ION

#ifdef    USE_ION
#include <linux/ion.h>

#define PMEM_NAME_STR "/dev/ion"
#define PMEM_NAME_STR_LEN sizeof(PMEM_NAME_STR)
#define ION_IOC_ALLOCATE _IOWR('I', 0, struct ion_allocation_data)

#else
#define PMEM_NAME_STR "/dev/pmem_adsp"
#define PMEM_NAME_STR_LEN sizeof(PMEM_NAME_STR)
#define PMEM_ALLOCATE _IOW('p', 5, unsigned int)

#endif
// PMEM
struct pmemDev {
    int pmem_fd;
    char pmem_name[PMEM_NAME_STR_LEN];
    int pmem_page_size;
    int pmem_size;
    void *pmem_buf;
#ifdef    USE_ION

       int ion_device_fd;

       struct ion_fd_data  ion_alloc_fd ;

    struct ion_allocation_data  ion_alloc_display_data ;

#endif
};

extern struct pmemDev *PMEM;
int initPmem(void);

#endif
