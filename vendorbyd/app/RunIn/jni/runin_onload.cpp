
/*
 *
 *    Revision History:
 *    Date                Author         CR/PR ID          Headline
 *   2014-09-05          zhanglu          kobe     modify the code
 *
 *
 *
 */
#include "JNIHelp.h"
#include "jni.h"
#include "utils/Log.h"
#include "runin_def.h"
namespace android
{
    int register_com_runinjni(JNIEnv *env);
}
;
using namespace android;
jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    JNIEnv *env = NULL;
    jint result =  - 1;
    RUNIN_LOG("Enter JNI_OnLoad");
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
    {
        RUNIN_LOG("GetEnv failed!");
        goto bail;
    }
    RUNIN_LOG("Could not retrieve the env!");
    if (register_com_runinjni(env) < 0)
    {
        RUNIN_LOG("ERROR: native registration failed\n");
        goto bail;
    }
    result = JNI_VERSION_1_4;
    bail: return result;
}
