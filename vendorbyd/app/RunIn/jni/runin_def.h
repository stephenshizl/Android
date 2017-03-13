#ifndef _RUNIN_DEF_H_
    #define _RUNIN_DEF_H_
    #include <utils/Log.h>
    #define RUNIN_DEBUG 1
    #define RUNIN_TAG "RUNIN_APP"
    #if (RUNIN_DEBUG == 1)
        #define RUNIN_LOG(...) (void)android_printLog(4,RUNIN_TAG,__VA_ARGS__)
    #else
        #define RUNIN_LOG(...) do{}while(0)
    #endif
#endif //_RUNIN_DEF_H_
