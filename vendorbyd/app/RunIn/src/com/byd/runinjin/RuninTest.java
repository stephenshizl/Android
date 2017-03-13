

package com.byd.runinjin;

import android.util.Log;

public class RuninTest
{

    private static final String TAG = "RuninTest";
    private static final int SUCCESS = 1;

    private static final int FAILED = 0;

    private static final String SONAME = "Runin_jni";

    static
    {

        System.loadLibrary(SONAME);
        Log.e(TAG, "Runin test already loadlibrary\n");
    }

    public RuninTest(){}

    public int runinTestFlag(int type, int value)
    {
        int ret = SUCCESS;
        ret = native_runinTestflag(type, value);
        return ret;
    }

    private static native int native_runinTestflag(int type, int value);


}
