
package com.tools.util;

import android.util.Log;

public class FTLog {
    private static final boolean DEBUG = true;
    public static final String LOG_TAG = "FactoryTest";

    public static void i(Object caller, String msg) {
        Log.i(LOG_TAG, caller.getClass().getName() + ": " + msg);
    }

    public static void d(Object caller, String msg) {
        if (!DEBUG)
            return;
        Log.d(LOG_TAG, caller.getClass().getName() + ": " + msg);
    }

    public static void w(Object caller, String msg) {
        Log.w(LOG_TAG, caller.getClass().getName() + ": " + msg);
    }

    public static void e(Object caller, String msg) {
        Log.e(LOG_TAG, caller.getClass().getName() + ": " + msg);
    }
}
