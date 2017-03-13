package com.byd.runin;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.TextView;

import com.byd.runin.TestActivity.TestStatus;
import com.byd.runin.TestLog.Log;
import com.byd.runin.audio.AudioTest;
import com.byd.runin.battery.BatteryTest;
import com.byd.runin.camera.CameraActivity;
import com.byd.runin.camera.SwitchCameraActivity;
import com.byd.runin.front_camera.FrontCameraActivity;
import com.byd.runin.lcd.LcdActivity;
import com.byd.runin.reboot.RebootActivity;
import com.byd.runin.receiver.ReceiverActivity;
import com.byd.runin.s3.S3Activity;
import com.byd.runin.s3.S3Receiver;
import com.byd.runin.speaker.SpeakerActivity;
import com.byd.runin.video.VideoActivity;

import java.util.ArrayList;


public class AutoTest
{
    private static final String TAG = "AutoTest";

    private static AutoTest sInstance = null;

    public static final int MSG_START_DELAY = 1;
    public static final int MSG_FINISH_ENTRY = 2;
    public static final int MSG_DISMISS_DIALOG = 3;

    private static final int MSG_START_WAKEUP_DELAY = 4;


    public static final int TIME_DISMISS_DIALOG = 2000;

    private static final int TIME_START_DELAY = 3000;

    private Context mContext;
    private TextView mStateText;

    private ArrayList < TestEntry > mTestSequence;
    private ArrayList < TestEntry > mActiveEntries; //only one instance

    ArrayList < TestListItem > mTestListItem = null;

    private PowerManager.WakeLock mScreenOnWakeLock = null;

    private BatteryTest mBatteryTest = null;
    private AudioTest mAudioTest = null;

    public static int camCount = 0;

    public static long count = 0;
    public TestEntry testEntry;
    public static String test_choice_hours = "";
    private KeyguardManager.KeyguardLock keyguardLock = null;

    private AutoTest()
    {
        mActiveEntries = new ArrayList < TestEntry > ();
    }

    public static AutoTest getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new AutoTest();
        }

        return sInstance;
    }

    public void bindTestActivity(TestActivity activity)
    {
        for (TestEntry entry: mActiveEntries)
        {
            Log.d(TAG, "bindTestActivity entry.cls = " + entry.cls);
            Log.d(TAG, "bindTestActivity activity.getClass() = " +
                activity.getClass());
            if (entry.cls == activity.getClass())
            {
                entry.instance = activity;

                return ;
            }
        }
    }

    public void unbindTestActivity(TestActivity activity)
    {
        //back to this interface to play
        if (mAudioTest != null)
        {
            mAudioTest.start();
        }

        for (TestEntry entry: mActiveEntries)
        {
            if (entry.instance == activity)
            {
                if (entry.cls != S3Activity.class)
                {
                    Log.d(TAG, "finished ------> " + entry.title);
                    entry.instance = null;
                    mActiveEntries.remove(entry);
                    mHandler.removeMessages(MSG_FINISH_ENTRY, entry);

                    if (entry.cls == LcdActivity.class || entry.cls ==
                        VideoActivity.class)
                    {
                        if (SharedPref.getTestStatus(activity,
                            activity.mSharedPrefKey) == TestStatus.ERROR)
                        {
                            showSucessDialog(entry.title, "Fail");
                        }
                        else
                        {
                            showSucessDialog(entry.title, "Sucess");
                        }
                    }
                    else
                    {
                        showSucessDialog(entry.title, "Sucess");
                    }
                }
                else
                {
                    acquireWakeLock();
                    showSucessDialog(entry.title, "Sucess");
                }
                //updateStateText();
                return ;
            }
        }

        Log.e(TAG, "can not find activity in mActiveEntries ?");
    }

    public void wakeUpAndUnlock()
    {
        KeyguardManager km = (KeyguardManager)mContext.getSystemService
            (Context.KEYGUARD_SERVICE);
        keyguardLock = km.newKeyguardLock("unLock");

        PowerManager pm = (PowerManager)mContext.getSystemService
            (Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock
            (PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        wl.acquire();
        wl.release();

        keyguardLock.disableKeyguard();

        acquireWakeLock();
    }

    public void unbindS3Activity(TestActivity activity)
    {
        Log.d(TAG, "unbind test activity = " + activity);
        for (TestEntry entry: mActiveEntries)
        {
            if (entry.instance == activity)
            {
                if (entry.cls == S3Activity.class)
                {
                    entry.done_times++;
                    Log.d(TAG, entry.title + " times = " + entry.time +
                        " done = " + entry.done_times);
                    if (entry.done_times >= entry.time)
                    {
                        count = entry.time;
                    }
                }
                return ;
            }
        }

        Log.e(TAG, "can not find activity in mActiveEntries ?");
    }

    private void processNext()
    {

        if (mTestSequence.size() > 0)
        {

            final TestEntry entry = mTestSequence.remove(0);
            testEntry = entry;
            if (entry.time <= 0)
            {
                Log.d(TAG, "ignore this entry " + entry.title);
                processNext();
                return ;
            }

            if (entry.cls != S3Activity.class && entry.cls !=
                SwitchCameraActivity.class)
            {
                Log.d(TAG, "entry.cls = " + entry.cls);
                Message msg = mHandler.obtainMessage(MSG_FINISH_ENTRY);
                msg.obj = entry;
                mHandler.sendMessageDelayed(msg, entry.time);
            }
            else if (entry.cls == S3Activity.class || entry.cls ==
                SwitchCameraActivity.class)
            {
                //entry.done_times = 0;
            } /*else if (entry.cls == CameraActivity.class){
            //entry.done_times = 0;
            if(!checkCameraAgain()){
            processNext();
            return;
            }
                         } */
            else
            {
                Log.e(TAG, "unreachable code here with " + entry.title);
                processNext();
                return ;
            }

            mActiveEntries.add(entry);
            // close wake lock
            if (entry.cls == S3Activity.class)
            {
                releaseWakeLock();
            }
            //video s3 reboot need pause music
            if (entry.cls == S3Activity.class || entry.cls == RebootActivity.class
                || entry.cls == VideoActivity.class ||entry.cls == LcdActivity.class
                || entry.cls == CameraActivity.class || entry.cls == SwitchCameraActivity.class)
            {
                mAudioTest.pause();
            }

            // stop battery test
            if (entry.cls == RebootActivity.class)
            {

                if (mBatteryTest != null)
                {
                    mBatteryTest.stop();
                }

                if (mAudioTest != null)
                {
                    mAudioTest.stop();
                }

                SharedPref.clearRebootSettings(mContext);
                SharedPref.saveRebootTimes(mContext, entry.time);
                SharedPref.saveRebootDoneTimes(mContext, 1);
            }

            if (entry.cls == S3Activity.class)
            {
                count = entry.time;
            }

            Intent intent = new Intent(mContext, entry.cls);
            intent.putExtra("autotest", true);
            if (entry.cls == LcdActivity.class)
            {
                intent.putExtra("mode", entry.ssMode);
                intent.putExtra("time", entry.time);
            }
            if ( entry.cls == VideoActivity.class ){
                intent.putExtra("mode", entry.strMode);
            }
            if (entry.cls == SwitchCameraActivity.class)
            {
                intent.putExtra("time", entry.time);
            }
            mContext.startActivity(intent);

            Log.d(TAG, "start ------> " + entry.title);

            return ;

        }

        //updateStateText();
        Log.d(TAG, "process next------finished");
        //sInstance = null;
    }

    public void showSucessDialog(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.setTitle(title).setMessage(message)
            .setPositiveButton("OK", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                mHandler.sendEmptyMessageDelayed(MSG_START_DELAY,
                    TIME_START_DELAY); mHandler.removeMessages
                    (MSG_DISMISS_DIALOG);
            }
        }
        ).setOnCancelListener(new OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface arg0)
            {
                mHandler.sendEmptyMessageDelayed(MSG_START_DELAY,
                    TIME_START_DELAY); mHandler.removeMessages
                    (MSG_DISMISS_DIALOG);
            }
        }
        ).create();
        dialog.show();

        Message msg = mHandler.obtainMessage(MSG_DISMISS_DIALOG);
        msg.obj = dialog;
        mHandler.sendMessageDelayed(msg, TIME_DISMISS_DIALOG);
    }

    public void readyToTest(Context context, ArrayList < TestListItem > tli,
        TextView state)
    {
        mContext = context;
        mStateText = state;
        mTestListItem = tli;
        mTestSequence = AutoTestSequence.getTestSequence(tli);

        mHandler.sendEmptyMessageDelayed(MSG_START_DELAY, TIME_START_DELAY);

        acquireWakeLock();

        mBatteryTest = new BatteryTest(mContext, null);
        mBatteryTest.start();

        mAudioTest = new AudioTest(context, null);
        mAudioTest.startMusic();
    }

    public void release()
    {
        if (mHandler != null)
        {
            mHandler.removeMessages(MSG_START_DELAY);
            mHandler.removeMessages(MSG_FINISH_ENTRY);
            mHandler.removeMessages(MSG_DISMISS_DIALOG);
        }

        if (mBatteryTest != null)
        {
            mBatteryTest.stop();
        }

        if (mAudioTest != null)
        {
            mAudioTest.stop();
        }

        releaseWakeLock();
        mContext = null;
        sInstance = null;

    }

    public Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_START_DELAY:
                    processNext();
                    break;
                case MSG_FINISH_ENTRY:
                    finishEntry((TestEntry)msg.obj);
                    break;
                case MSG_DISMISS_DIALOG:
                    AlertDialog dialog = (AlertDialog)msg.obj;
                    dialog.dismiss();
                    mHandler.sendEmptyMessageDelayed(MSG_START_DELAY,
                        TIME_START_DELAY);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private void finishEntry(TestEntry entry)
    {
        Log.d(TAG, "finish ------> " + entry.title);
        if (entry.instance != null)
        {
            entry.instance.finishTest();
        }
        else
        {
            Log.e(TAG, "why entry instance null?");
        }
    }

    public void acquireWakeLock()
    {
        if (mScreenOnWakeLock == null)
        {
            PowerManager pm = (PowerManager)mContext.getSystemService
                (Context.POWER_SERVICE);
            mScreenOnWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "TestActivity");
            mScreenOnWakeLock.setReferenceCounted(false);
        }

        if (!mScreenOnWakeLock.isHeld())
        {
            mScreenOnWakeLock.acquire();
        }
    }

    public void releaseWakeLock()
    {
        if (mScreenOnWakeLock != null && mScreenOnWakeLock.isHeld())
        {
            mScreenOnWakeLock.release();
        }

        if (keyguardLock != null)
        {
            keyguardLock.reenableKeyguard();
            keyguardLock = null;
        }
    }


}
