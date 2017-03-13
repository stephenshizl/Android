
#include <stdio.h>
#include <assert.h>
#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#include "utils/Errors.h"
#include "utils/Log.h"
#include <dlfcn.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/types.h>
#include "runin_def.h"
#define CHECKFUNP(x)    {if((*x)==0) return FALSE; else return TRUE;}
#define pSunPath "/dev/socket/cit"
#define FILENAME "com_runinjni"
#define SUCCESS 1
#define FAILED 0
#define FUNCTIONPOINTERNULL 0
namespace android
{
    #define RUNIN_TEST_FLAG 6
    static void *ac = NULL;
    struct sockaddr_un
    {
        unsigned short sun_family;
        char sun_path[108];
    };
    typedef struct
    {
        unsigned short TestType;
        unsigned short Item;
        unsigned short Value;
    } RUNTestTypeStr;
    /********************************************************************
     * Function Name: Error
     * Purpose: will get error id when call dl operation
     * Input: none
     * Output: none
     * Return: return dlerror
     ********************************************************************/
    static const char *Error()
    {
        const char *error = dlerror();
        if (error == NULL)
        {
            error = "unknown reason!";
        }
        return error;
    }
    static jint com_runinTestflag(JNIEnv *env, jobject thiz, int item, int value)
    {
        RUNIN_LOG("Runin test to com_runinTestflag !!!\n", FILENAME);
        int result =  - 1;
        int path =  - 1;
        int clienthandle =  - 1;
        struct sockaddr_un clientStr;
        RUNTestTypeStr almsg;
        //begin create socket
        clienthandle = socket(AF_LOCAL, SOCK_STREAM, 0);
        if (clienthandle < 0)
        {
            RUNIN_LOG("[%s--com_runinTestflag] create socked failed!\n", FILENAME);
            return NULL;
        }
        //connect
        memset((void*) &clientStr, 0, sizeof(clientStr));
        clientStr.sun_family = AF_LOCAL;
        memcpy(clientStr.sun_path, pSunPath, strlen(pSunPath));
        result = connect(clienthandle, (struct sockaddr*) &clientStr, sizeof(clientStr));
        if (result < 0)
        {
            RUNIN_LOG("[%s--com_runinTestflag] connect failed!\n", FILENAME);
            close(clienthandle);
            return NULL;
        }
        //send socket
        memset((void*) &almsg, 0, sizeof(almsg));
        almsg.TestType = RUNIN_TEST_FLAG;
        almsg.Item = item;
        almsg.Value = value;
        result = send(clienthandle, &almsg, sizeof(almsg), 0);
        if (result < 0)
        {
            perror("failed connect");
            RUNIN_LOG("[%s--com_runinTestflag] send failed!\n", FILENAME);
            close(clienthandle);
            return NULL;
        }
        int btok =  - 1;
        result = recv(clienthandle, (void*) &btok, sizeof(int), 0);
        if (result < 0)
        {
            perror("failed connect");
            RUNIN_LOG("[%s--com_runinTestflag] recv failed!\n", FILENAME);
            close(clienthandle);
            return NULL;
        }
        RUNIN_LOG("[%d--com_runinTestflag] recv result!\n", btok);
        close(clienthandle);
        return btok;
    }
    static JNINativeMethod RuninTest_Methods[] =
    {
        {
            "native_runinTestflag", "(II)I", (void*)com_runinTestflag
        },
    };
    static int registerNativeMethods(JNIEnv *env, const char *className,
                                     JNINativeMethod *gMethods, int numMethods)
    {
        jclass clazz;
        clazz = env->FindClass(className);
        if (clazz == NULL)
        {
            RUNIN_LOG("Native registration unable to find class '%s'", className);
            return JNI_FALSE;
        }
        if (env->RegisterNatives(clazz, gMethods, numMethods) < 0)
        {
            RUNIN_LOG("RegisterNatives failed for '%s'", className);
            return JNI_FALSE;
        }
        return JNI_TRUE;
    }
    int register_com_runinjni(JNIEnv *env)
    {
        return registerNativeMethods(env, "com/byd/runinjin/RuninTest",
                                     RuninTest_Methods, NELEM(RuninTest_Methods));
    }
}
;
