package com.lenovo.freeanswer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StatusManager {
    private static final String TAG = "StatusMessager";

        private static final int MAX_TIP_COUNT = 3;
    private static StatusManager mInstance = new StatusManager();

    public static StatusManager getInstance() {
        return mInstance;
    }

    
    public void displayStart(Context context, int mFlagHungup) {
        Log.d(TAG, "displayStart");
        Intent intent = new Intent(SpeechConstant.FREE_DIAL_MESSAGE_START);
        context.sendBroadcast(intent);

        if (mFlagHungup == WakeUpManager.WAKE_UP_KEY_HUNGUP) {
            int count = SystemSettings.getInt(context,
                    SystemSettings.NOTIFICATION_HUNGUP_TIP_COUNT, 0);
            if (count < MAX_TIP_COUNT) {
                //NotificationController.getInstance(context).showHungupNotification();
                count++;
                SystemSettings.saveInt(context, SystemSettings.NOTIFICATION_HUNGUP_TIP_COUNT,
                        count);
            }
        } else {
            int count = SystemSettings.getInt(context,
                    SystemSettings.NOTIFICATION_HUNGUP_ANSWER_TIP_COUNT, 0);
            if (count < MAX_TIP_COUNT) {
                //NotificationController.getInstance(context).showAnswerHungupNotification();
                count++;
                SystemSettings.saveInt(context,
                        SystemSettings.NOTIFICATION_HUNGUP_ANSWER_TIP_COUNT,
                        count);
            }
        }

    }

    public void displayStop(Context context) {
        Log.d(TAG, "displayStop");
        Intent intent = new Intent(SpeechConstant.FREE_DIAL_MESSAGE_STOP);
        context.sendBroadcast(intent);

        //NotificationController.getInstance(context).clearNotification();
    }
}