

package com.byd.runin.s3;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;

import com.byd.runin.AutoTest;
import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class S3Activity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_s3_test";
    public static final String TITLE = "StandBy Test";

    public static final String TAG = "S3Activity";

    public static final String ACTION_WAKE_UP = "com.byd.runin.s3.WAKE_UP";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_s3_test_time";
    private static final String S3_TEST_FAIL_INFO = "S3 test fail: ";

    private static final long TIME_WAKE_UP = 3000;
    private static final int S3_WAKEUP_TIME_DELAY = 10000;

    private static final int MSG_SLEEP = 1;
    private static final int MSG_WAKE_UP = 2;


    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPM;

    private Looper mLooper;
    private ThreadHandler mHandler;

    private ArrayList < String > mStatus;
    static S3Activity sInstance = null;
    int count = 0;

    private KeyguardManager.KeyguardLock keyguardLock = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);

        mPM = (PowerManager)getSystemService(Context.POWER_SERVICE);

        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mLooper = thread.getLooper();

        mHandler = new ThreadHandler(mLooper);

        mHandler.sendEmptyMessage(MSG_WAKE_UP);
        sInstance = this;
        mStatus = new ArrayList < String > ();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mLooper != null)
        {
            mLooper.quit();
            mLooper = null;
        }

        if (keyguardLock != null)
            keyguardLock.reenableKeyguard();

        if (mWakeLock != null && mWakeLock.isHeld())
        {
            mWakeLock.release();
            mWakeLock = null;
        }

        cancelWakeupAlarm();
        mHandler.removeMessages(MSG_SLEEP);
        mHandler.removeMessages(MSG_WAKE_UP);
        sInstance = null;
        mHandler = null;

        AutoTest.getInstance().mHandler.removeMessages
            (AutoTest.MSG_FINISH_ENTRY);
    }
    public void isFinish()
    {

        this.finish();
    }
    private final class ThreadHandler extends Handler
    {
        public ThreadHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_WAKE_UP:
                    handleWakeupMsg();
                    break;
                case MSG_SLEEP:
                    handleSleepMsg();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    void sendWakeupMessage()
    {
        mHandler.sendEmptyMessage(MSG_WAKE_UP);
    }

    private void updateStatusText(String text)
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
        String date = format.format(new Date());

        if (text.equals("Wake up"))
        {
            mStatus.clear();
        }
        mStatus.add(0, date + " " + text);

        if (mStatus.isEmpty())
            return ;
        String finalText = "";
        for (String item: mStatus)
        {
            finalText += item;
            finalText += "\n";
        }

        final String finalString = finalText;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mStatusText.setText(finalString);
            }
        }
        );
    }

    private void handleSleepMsg()
    {
        try
        {
            //finishStartingCpu();
            setWakeupAlarm();
            if (mWakeLock != null && mWakeLock.isHeld())
            {
                mWakeLock.release();
                mWakeLock = null;
            }
            updateStatusText("Sleep");
            goToSleep();

        }
        catch (Exception e)
        {
            Log.e(TAG, "sleep is fail,key have problem");
            dealwithError(mStatusText, S3_TEST_FAIL_INFO + e.getMessage());
        }
    }

    private void handleWakeupMsg()
    {
        try
        {
            Log.d(TAG, "S3Activity test time = " + AutoTest.count + "  done times = " + count);
            if (count == AutoTest.count)
            {
                Log.i(TAG, Integer.toString(count));
                AutoTest.count = 0;
                wakeupAndUnlock();
                this.isFinish();
                return ;
            }
            //SharedPref.saveTestStatus(this, S3Activity.KEY_SHARED_PREF, TestStatus.ING);
            //beginStartingCpu(this);
            wakeupAndUnlock();
            cancelWakeupAlarm();
            mHandler.sendEmptyMessageDelayed(MSG_SLEEP, TIME_WAKE_UP);
            ++count;
            updateStatusText("Wake up");

        }
        catch (Exception e)
        {
            Log.e(TAG, "wakeup fail,key have problem");
            dealwithError(mStatusText, S3_TEST_FAIL_INFO + e.getMessage());
        }
    }

    private void wakeupAndUnlock()
    {
        KeyguardManager km = (KeyguardManager)getSystemService
            (Context.KEYGUARD_SERVICE);
        keyguardLock = km.newKeyguardLock("unLock");

        mWakeLock = mPM.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        mWakeLock.acquire();

        keyguardLock.disableKeyguard();
    }

    private void goToSleep()
    {
        mPM.goToSleep(SystemClock.uptimeMillis());
        Log.d(TAG, "goToSleep");
    }

    private void setWakeupAlarm()
    {
        Intent intent = new Intent(ACTION_WAKE_UP, null, getApplicationContext()
            , S3Receiver.class);
        PendingIntent operation = PendingIntent.getBroadcast
            (getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + S3_WAKEUP_TIME_DELAY, operation);

    }

    private void cancelWakeupAlarm()
    {
        Intent intent = new Intent(ACTION_WAKE_UP, null, getApplicationContext()
            , S3Receiver.class);

        PendingIntent operation = PendingIntent.getBroadcast
            (getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.cancel(operation);

    }

    @Override
    public void acquireWakeLock()
    {
        // disable parent wake lock function
    }

    @Override
    public void releaseWakeLock()
    {
        // disable parent wake lock function
    }

}
