#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdio.h>
#include <stdlib.h>

#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

/********************************************
* Description:
*  Write the keybox data to device
*
* Output parameters:
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_keybox_write_data()
{
    int ret = 0;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);

    ret = system("StoreKeybox /storage/sdcard0/keybox_HP_Slate_6.bin");
    if(ret != 0)
    {
        FT_LOG("[%s,%d] Error, Write keybox failed, ret: %d\n", __func__, __LINE__, ret);
        return -1;
    }

    FT_LOG("[%s,%d] Write keybox finish\n", __func__, __LINE__);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}

/********************************************
* Description:
*  Check keybox write success or not.
*  After write,a folder will be create in persist folder.
*  So, we check if there have the "data" folder.
*  If data exist,we think write keybox success.
*
* Output parameters:
*
* Return value:
* 0: success
* -1: failed
********************************************/
int ft_keybox_check_data()
{
    int ret = -1;

    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);

    ret = access(PERSIST_DATA_PATH, F_OK);
    if(ret != 0)
    {
        FT_LOG("[%s,%d] %s folder not exist!\n", __func__, __LINE__, PERSIST_DATA_PATH);
        return -1;
    }

    FT_LOG("[%s,%d] data folder exist, write Keybox success!\n", __func__, __LINE__);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}