package com.lenovo.freeanswer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.ContentResolver;
import android.provider.Settings;

public class SystemSettings {
    private static SharedPreferences mPrefer = null;

    private static final String PREFERENCES_NAME = "com.lenovo.freeanswer.preferences";
    public static final String NOTIFICATION_HUNGUP_TIP_COUNT = "NOTIFICATION_HUNGUP_TIP_COUNT";
    public static final String NOTIFICATION_HUNGUP_ANSWER_TIP_COUNT = "NOTIFICATION_HUNGUP_ANSWER_TIP_COUNT";

    public static int getIntFromSystem(ContentResolver contentResolver, String key, int defaultValue) {
        try {
            return Settings.System.getInt(contentResolver, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }
    
    public static void saveInt(Context context, String name, int value) {
        if (mPrefer == null) {
            mPrefer = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = mPrefer.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static int getInt(Context context, String name, int defaultValue) {
        if (mPrefer == null) {
            mPrefer = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return mPrefer.getInt(name, defaultValue);
    }
}