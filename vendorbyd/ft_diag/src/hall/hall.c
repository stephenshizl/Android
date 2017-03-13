#include "include/ft.h"
#include "include/ft_sys.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
/********************************************
 * Description:
 * Get the hall state
 *
 * Input parameters:
 * out param:
 *     hall: return hall state
 *
 * Return value:
 * 0: success
 * -1: failed
 ********************************************/
int ft_hall_state_read(char *state)
{
    int res =  - 1, cmp =  - 1;
    int flen = 0;
    FILE *fp = NULL;
    FT_LOG("[%s:%d] start...\n", __func__, __LINE__);
    if (access(HALL_STATE, F_OK) == 0)
    {
        fp = fopen(HALL_STATE, "r");
        if (NULL == fp)
        {
            FT_LOG("[%s:%d] open failed.\n", __func__, __LINE__);
            return  - 1;
        }
    }
    else
    {
        FT_LOG("[%s:%d] hall node dones't exits.\n", __func__, __LINE__);
        return  - 1;
    }
    memset(state, 0, sizeof(state));
    fseek(fp, 0, SEEK_END);
    flen = ftell(fp);
    fseek(fp, 0, SEEK_SET);
    fread(state, flen, 1, fp);
    cmp = strcmp(state, HALL_ACTIVE);
    FT_LOG("[%s:%d] state = %s, res = %d\n", __func__, __LINE__, state, cmp);
    if (cmp == 0)
    {
        sprintf(state, "%s", "active");
    }
    else
    {
        sprintf(state, "%s", "inactive");
    }
    fclose(fp);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 1;
}
