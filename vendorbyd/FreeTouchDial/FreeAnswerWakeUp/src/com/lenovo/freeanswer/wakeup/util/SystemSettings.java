
package com.lenovo.freeanswer.wakeup.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import com.lenovo.freeanswer.wakeup.WakeUpManager;
import com.lenovo.freeanswer.wakeup.speech.SpeechConstant;

public class SystemSettings {

    private static SharedPreferences mPrefer = null;

    public static final String LENOVO_K7 = "Lenovo K920";
    public static final String LENOVO_K7_ALIAS = "Lenovo K7";

    private static final String PREFERENCES_NAME = "com.lenovo.freeanswer.preferences";
    public static final String NOTIFICATION_HUNGUP_TIP_COUNT = "NOTIFICATION_HUNGUP_TIP_COUNT";
    public static final String NOTIFICATION_HUNGUP_ANSWER_TIP_COUNT = "NOTIFICATION_HUNGUP_ANSWER_TIP_COUNT";

    public static boolean isPreInstalledPhone() {
        return (Build.MODEL.contains(LENOVO_K7) || Build.MODEL.contains(LENOVO_K7_ALIAS));
    }

    public static int getIntFromSystem(ContentResolver contentResolver, String key, int defaultValue) {
        try {
            return Settings.System.getInt(contentResolver,
                    key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static void putIntValueToSystem(ContentResolver contentResolver, String key,
            int isChecked) {
        try {
            Settings.System.putInt(contentResolver, key, isChecked);
        } catch (Exception e) {
        }
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
