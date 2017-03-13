#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/input.h>
/*-----------------------------------------------------------------
 * Keypad test
-----------------------------------------------------------------*/
/********************************************
 * Description:
 * Check if the power key is pressed
 *
 * Input parameters:
 * out param:
 *      key: the value of power key
 *
 * Return value:
 * 0: one key is pressed
 * -1: failed
 ********************************************/
int ft_key_test_Power(int *key)
{
    int ret =  - 1;
    struct input_event ev_key;
    int fd, len = 0, count = 0, i = 0, event_num = 0;
    FILE *fp = NULL;
    char cmd[256] = {0};
    char event[2048] = {0};
    FT_LOG("[%s:%d] [POWER_KAY]: start...\n", __func__, __LINE__);
    event_num = find_inputNum(POWER_KEY_EVENT);
    FT_LOG("event_num:%d\n", event_num);
    memset(cmd, 0, sizeof(cmd));
    sprintf(cmd, "%s%d", INPUT_EVENT, event_num);
    fd = open(cmd, O_RDONLY);
    if ( - 1 == fd)
    {
        FT_LOG("[%s:%d] Faild to open '/dev/input/event%d'\n", __func__,__LINE__, event_num);
        return ret;
    }
    while (count < 20)
    {
        len = read(fd, &ev_key, sizeof(struct input_event));
        if (EV_KEY == ev_key.type)
        {
            //            if((1 == ev_key.value)&&(KEY_POWER == ev_key.code))
            //            if(ev_key.value == 1)
            if (ev_key.value == 0)
            //when keyup the value is 0
            {
                FT_LOG("[%s:%d] type:%d code:%d value:%d\n", __func__, __LINE__,\
                    ev_key.type, ev_key.code, ev_key.value);
                ret = 0;
                //                strcpy(key, "POWER KEY");
                *key = ev_key.code;
                break;
            }
        }
        count++;
    }
    close(fd);
    FT_LOG("[%s:%d] key=%d ret=%d\n", __func__, __LINE__,  *key, ret);
    FT_LOG("[%s:%d] end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Check if the volume key is pressed
 *
 * Input parameters:
 * out param:
 *      key: the volume key value,it can be volume up key or volume down key
 *
 * Return value:
 * 0: Volume up is pressed
 * -1: failed or other key is pressed
 ********************************************/
 int ft_key_test_Volume(int *key)
{
    int ret =  - 1;
    struct input_event ev_key;
    int fd, len = 0, count = 0, i = 0, event_num = 0;
    FILE *fp = NULL;
    char cmd[256] = {0};
    char event[2048] = {0};
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    event_num = find_inputNum(GPIO_KEY_EVENT);
    FT_LOG("event_num:%d\n", event_num);
    memset(cmd, 0, sizeof(cmd));
    sprintf(cmd, "%s%d", INPUT_EVENT, event_num);
    fd = open(cmd, O_RDONLY);
    if ( - 1 == fd)
    {
        FT_LOG("[%s:%d] Faild to open '/dev/input/event%d'\n", __func__,__LINE__, cmd);
        return ret;
    }
    while (count < 20)
    {
        len = read(fd, &ev_key, sizeof(struct input_event));
        if (EV_KEY == ev_key.type)
        {
            //            if((1 == ev_key.value)&&(KEY_VOLUMEUP == ev_key.code))
            //            if(ev_key.value == 1)
            if (ev_key.value == 0)
            {
                FT_LOG("[%s:%d] type:%d code:%d value:%d\n", __func__, __LINE__,\
                    ev_key.type, ev_key.code, ev_key.value);
                ret = 0;
                //                strcpy(key, "VOLUMEUP KEY");
                *key = ev_key.code;
                break;
            }
        }
        count++;
    }
    close(fd);
    FT_LOG("[%s:%d] key=%d ret=%d\n", __func__, __LINE__,  *key, ret);
    FT_LOG("[%s:%d] end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Check if the hook key is pressed
 *
 * Output parameters:
 * out param:
 *      key:the hook key value
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_key_test_Hook(int *key)
{
    int ret =  - 1;
    struct input_event ev_key;
    int fd, len = 0, count = 0, event_num = 0, i;
    FILE *fp = NULL;
    char cmd[256] = {0};
    char event[2048] = {0};
    FT_LOG("[%s:%d] Start...\n", __func__, __LINE__);
    event_num = find_inputNum(HEADSETBUTTON_KEY_EVENT);
    FT_LOG("event_num:%d", event_num);
    printf("event_num:%d", event_num);
    sprintf(cmd, "%s%d", INPUT_EVENT, event_num);
    fd = open(cmd, O_RDONLY);
    if ( - 1 == fd)
    {
        FT_LOG("[%s:%d] [HOOK_KEY]: Faild to open %s\n", __func__, __LINE__, cmd);
        return ret;
    }
    while (count < 10)
    {
        len = read(fd, &ev_key, sizeof(struct input_event));
        if (EV_KEY == ev_key.type)
        {
            //            if((1 == ev_key.value)&&(HOOK_KEY == ev_key.code))
            //            if(ev_key.value == 1)
            if (ev_key.value == 0)
            {
                FT_LOG("[%s:%d] [HOOK_KEY]: Hook key pressed: type:%d code:%d value:%d\n",  \
                    __func__, __LINE__, ev_key.type, ev_key.code, ev_key.value);
                ret = 0;
                *key = ev_key.code;
                break;
            }
        }
        count++;
    }
    close(fd);
    FT_LOG("[%s:%d] key=%d\n", __func__, __LINE__,  *key);
    FT_LOG("[%s:%d] end...\n", __func__, __LINE__);
    return ret;
}
/********************************************
 * Description:
 * Check if the back key is pressed
 *
 * Input parameters:
 *
 * Return value:
 *  0: the back key was pressed.
 * -1: the other area was presseds.
 ********************************************/
int ft_key_test_Back()
{
    int ret =  - 1;
    struct input_event ev_key;
    int fd, len = 0, count = 0, i = 0, event_num = 0;
    FILE *fp = NULL;
    char cmd[256];
    int KEY_ABS_X = 0, KEY_ABS_Y = 0;
    FT_LOG("[%s:%d] Start...\n", __func__, __LINE__);
    event_num = find_inputNum(BACK_KEY_EVENT);
    FT_LOG("[%s,%d] event_num:%d\n", __func__, __LINE__, event_num);
    memset(cmd, 0, sizeof(cmd));
    sprintf(cmd, "%s%d", INPUT_EVENT, event_num);
    fd = open(cmd, O_RDONLY);
    if ( - 1 == fd)
    {
        FT_LOG("[%s:%d] Faild to open '/dev/input/event%d'\n", __func__,__LINE__, event_num);
        return ret;
    }
    while (count < 30)
     /*20*/
    {
        len = read(fd, &ev_key, sizeof(struct input_event));
        if (EV_ABS == ev_key.type)
        {
            FT_LOG("[%s:%d] type:%d code:%d value:%d\n", __func__, __LINE__,  \
                ev_key.type, ev_key.code, ev_key.value);
            if (ev_key.code == BACK_KEY_ABS_X_CODE && ev_key.value == BACK_KEY_ABS_X_VALUE)
            {
                KEY_ABS_X = 1;
            }
            else if (ev_key.code == BACK_KEY_ABS_Y_CODE && ev_key.value == BACK_KEY_ABS_Y_VALUE)
            {
                KEY_ABS_Y = 1;
            }
            else
            {}
        }
         FT_LOG("[%s:%d] yytype:%d yycode:%d yyvalue:%d\n", __func__, __LINE__,  \
                          ev_key.type, ev_key.code, ev_key.value);
        if (EV_KEY == ev_key.type && KEY_ABS_X && KEY_ABS_X)
        {
            FT_LOG("[%s:%d] type:%d code:%d value:%d\n", __func__, __LINE__,  \
                             ev_key.type, ev_key.code, ev_key.value);
            if (ev_key.value == 1)
            {
                FT_LOG("[%s,%d] ev_key.value is %d\n", __func__, __LINE__, ev_key.value);
                ret = 0;
                break;
            }
        }
        count++;
    }
    close(fd);
    KEY_ABS_X = 0;
    KEY_ABS_Y = 0;
    FT_LOG("[%s:%d] ret=%d\n", __func__, __LINE__, ret);
    FT_LOG("[%s:%d] end...\n", __func__, __LINE__);
    return ret;
}
