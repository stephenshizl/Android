/****************************************************************************
 * fm.c
 ***************************************************************************/
 
/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who          what, where, why
--------  ------       ------------------------------------------------------
20100817  Guo Zhangku  Initial framework.
20130814  Li Tao        add fm interface for auto CIT
===========================================================================*/

#ifndef _FM_H_
#define _FM_H_

#define FM_SUCCESS 0
#define FM_FAILURE -1
enum FM_ERROR{
    FM_ERR_OPEN_FAILED = 1,    //FM open failed
    FM_ERR_OPENED,            //FM have opened
    FM_ERR_NOT_OPEN,        //FM have close
};

#ifdef __cplusplus
extern "c"
{
#endif //__cplusplus
/*
int fm_open();
int fm_close();
int fm_audio_device();
int fm_set_frequent(int freq);
int fm_get_frequent();
int fm_set_control(int id, int value);
int fm_get_control(int id);
int fm_adjust_volume(int volume);
*/
int FM_Enable();
int Enable_FM_Stereo();
int Disable_FM_Stereo();
int FM_Set_Channel(int freq);
int FM_Set_Volume(int volume);
int FM_Disable();

#ifdef __cplusplus
}
#endif //__cplusplus


#endif //_FM_H_

