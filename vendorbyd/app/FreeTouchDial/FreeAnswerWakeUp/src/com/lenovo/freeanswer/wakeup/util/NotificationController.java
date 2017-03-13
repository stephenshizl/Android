
package com.lenovo.freeanswer.wakeup.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.lenovo.freeanswer.wakeup.R;

public class NotificationController {

    private static final int NOTIFICATION_ID_HUNGUP = 0x10000000;
    private static final int NOTIFICATION_ID_HUNGUP_ANSWER = 0x20000000;

    private static NotificationController sInstance;
    private final Context mContext;
    private final NotificationManager mNotificationManager;

    /** Constructor */
    NotificationController(Context context) {
        mContext = context.getApplicationContext();
        mNotificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    /** Singleton access */
    public static synchronized NotificationController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NotificationController(context);
        }
        return sInstance;
    }

    private Notification createNotification(int iconId, String ticker,
            CharSequence title, String contentText, Intent intent, Bitmap largeIcon,
            boolean ongoing) {
        // Pending Intent
        PendingIntent pending = null;
        if (intent != null) {
            pending = PendingIntent.getActivity(
                    mContext, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        // NOTE: the ticker is not shown for notifications in the Holo UX
        Notification.Builder builder = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pending)
                .setSmallIcon(iconId)
                .setTicker(ticker)
                .setOngoing(ongoing)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        Notification notification = builder.getNotification();
        return notification;
    }

    public void showHungupNotification() {
        String ticker = mContext.getString(R.string.hungup);
        Notification notification = createNotification(R.drawable.notification_service_enable,
                ticker, ticker, ticker,
                new Intent(), null, false);
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID_HUNGUP, notification);
        }
    }

    public void showAnswerHungupNotification() {
        String ticker = mContext.getString(R.string.hungup_answer);
        Notification notification = createNotification(R.drawable.notification_service_enable,
                ticker, ticker, ticker,
                new Intent(), null, false);
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID_HUNGUP_ANSWER, notification);
        }
    }

    public void clearNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }
}
