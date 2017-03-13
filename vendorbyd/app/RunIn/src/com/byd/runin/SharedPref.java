
package com.byd.runin;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref
{
    private static final String PREF_NAME_STATUS = "test_status";
    private static final String PREF_NAME_REBOOT = "test_reboot";
    private static final String PREF_NAME_MODE = "test_mode";

    public static void saveTestStatus(Context context, String key, int status)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_STATUS,
            Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, status);
        editor.apply();
    }

    public static int getTestStatus(Context context, String key)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_STATUS,
            Context.MODE_PRIVATE);
        return pref.getInt(key, TestActivity.TestStatus.NONE);
    }

    public static void clearTestStatus(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_STATUS,
            Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }

    public static void clearRebootSettings(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_REBOOT,
            Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }

    public static void saveRebootTimes(Context context, long times)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_REBOOT,
            Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("reboot_times", times);
        editor.apply();
    }

    public static long getRebootTimes(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_REBOOT,
            Context.MODE_PRIVATE);
        return pref.getLong("reboot_times", 0);
    }

    public static void saveRebootDoneTimes(Context context, long times)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_REBOOT,
            Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("reboot_done_times", times);
        editor.apply();
    }

    public static long getRebootDoneTimes(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_REBOOT,
            Context.MODE_PRIVATE);
        return pref.getLong("reboot_done_times", 0);
    }

    //save testMode
    public static void saveTestMode(Context context, String testMode)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_MODE,
            Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("testMode", testMode);
        editor.apply();
    }

    public static String getTestMode(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME_MODE,
            Context.MODE_PRIVATE);
        return pref.getString("testMode", RuninTestMainActivity.SS);
    }
}
