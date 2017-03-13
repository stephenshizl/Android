#include "include/ft.h"
#include "include/ft_sys.h"

int ft_reset_phone()
{
    int ret = -1;
    ret = property_set("service.adb.root","0");//close adb root
    if(ret < 0)
    {
        FT_LOG("[%s,%d] Error,fail to set adb root\n",__func__,__LINE__);
        return -1;
    }
    system("am broadcast -a android.intent.action.MASTER_CLEAR --es android.intent.extra.REASON MasterClearConfirm");
    return 1; 
}
int ft_rm_cit_flag()
{
    system("rm -rf /data/manualresult.txt");
    return 1; 
}
