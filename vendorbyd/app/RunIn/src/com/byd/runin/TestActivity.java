
package com.byd.runin;

import com.byd.runin.TestLog.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.widget.TextView;

import com.byd.runin.SharedPref;
import com.byd.runin.s3.S3Activity;
import com.byd.runin.video.VideoActivity;
import com.byd.runin.camera.CameraActivity;
import com.byd.runin.camera.SwitchCameraActivity;
import com.byd.runin.front_camera.FrontCameraActivity;
import com.byd.runin.lcd.LcdActivity;

public class TestActivity extends Activity
{
    protected String mSharedPrefKey = "unknown";
    protected String mTitle = "";
    private static final String TAG = "TestActivity";

    private boolean mAutoTest = false;

    protected boolean mMode = false;
    protected long mTime = 0;
    protected String strMode = null;

    public TextView mStatusText;

    private PowerManager.WakeLock mScreenOnWakeLock = null;

    public static class TestStatus
    {
        public static final int NONE = 0;
        public static final int ING = 1;
        public static final int DONE = 2;
        public static final int ERROR = 3;
    }

    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
        mAutoTest = getIntent().getBooleanExtra("autotest", false);
        Log.d("TestActivity", "TestActivity onCreate  mAutoTest= " + mAutoTest);
        if (mAutoTest)
        {
            AutoTest.getInstance().bindTestActivity(this);
        }

        super.onCreate(savedInstanceState);

        mStatusText = new TextView(this);
        setContentView(mStatusText);
        mStatusText.setTextSize(30);

        saveSharedPrefDoing();

        setTitle(mTitle);
        if (mTitle.equals(LcdActivity.TITLE))
        {
            mMode = getIntent().getBooleanExtra("mode", false);
            mTime = getIntent().getLongExtra("time", 0);
        }

        if ( mTitle.equals(VideoActivity.TITLE) ){
            strMode = getIntent().getStringExtra("mode");
        }

        if ( mTitle.equals(SwitchCameraActivity.TITLE) ){
            mTime = getIntent().getLongExtra("time", 20);
            if (mTime < 20)
            {
                mTime = 20;
            }
        }

        //        if ( !mTitle.equals(S3Activity.TITLE) )
        //            acquireWakeLock();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mAutoTest)
        {
            AutoTest.getInstance().unbindTestActivity(this);
        }
        if (SharedPref.getTestStatus(this, mSharedPrefKey) != TestStatus.ERROR)
        {
            saveSharedPrefDone();
        }

    }

    public void finishTest()
    {
        finish();
    }

    @Override
    protected void onResume()
    {
        if (!AutoTest.getInstance().mHandler.hasMessages
            (AutoTest.MSG_FINISH_ENTRY))
        {
            android.util.Log.d("TestActivity",
                "because of activity suspend,lead to message dismiss,Create new message");
            Message msg = AutoTest.getInstance().mHandler.obtainMessage
                (AutoTest.MSG_FINISH_ENTRY);
            msg.obj = AutoTest.getInstance().testEntry;
            if (AutoTest.getInstance().testEntry != null)
            {
                if (AutoTest.getInstance().testEntry.title != S3Activity.TITLE)
                {
                    if (AutoTest.getInstance().testEntry.title !=
                        SwitchCameraActivity.TITLE)
                    {
                        AutoTest.getInstance().mHandler.sendMessageDelayed(msg,
                            AutoTest.getInstance().testEntry.time);
                    }
                    else
                    {
                        Log.d("TestActivity", "SwitchCameraActivity.....");
                    }
                }
                else
                {
                    //AutoTest.getInstance().mHandler.sendMessageDelayed(msg, 10000);
                }
            }
        }

        super.onResume();
    }

    public void acquireWakeLock()
    {
        if (mScreenOnWakeLock == null)
        {
            PowerManager pm = (PowerManager)getSystemService
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
    }

    public void saveSharedPrefDoing()
    {
        Log.d("TestActivity", "TestActivity saveSharedPrefDoing = 1");
        SharedPref.saveTestStatus(this, mSharedPrefKey, TestStatus.ING);
    }

    public void saveSharedPrefDone()
    {
        Log.d("TestActivity", "TestActivity saveSharedPrefDone = 2");
        SharedPref.saveTestStatus(this, mSharedPrefKey, TestStatus.DONE);
    }

    public void saveSharedPrefError()
    {
        Log.d(TAG, "TestActivity saveSharedPrefError = " + TestStatus.ERROR);
        SharedPref.saveTestStatus(this, mSharedPrefKey, TestStatus.ERROR);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder buidler = new AlertDialog.Builder(this);
        buidler.setTitle("Confirm to exit").setMessage(
            "Testing...\nDo you confirm to exit ?").setPositiveButton("Confirm",
            new OnClickListener()
        {

            @Override public void onClick(DialogInterface arg0, int arg1)
            {
                finish();
            }
        }
        ).setNegativeButton("Cancel", null).create().show();
    }

    public void dealwithError(TextView statusText, String errorMsg)
    {
        statusText.setText(errorMsg);
        saveSharedPrefError();
        finishTest();
    }
}
