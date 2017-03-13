
package com.byd.runin.lcd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;
import com.byd.runin.receiver.ReceiverTest;
import com.byd.runin.vibrator.VibratorTest;
import com.byd.runin.R;

import java.io.File;

public class LcdActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_lcd_test";
    public static final String TITLE = "RVL Test";
    private static final String TAG = "LcdActivity";

    public static final String KEY_SHARED_PREF_TEST_TIME = "key_lcd_test_time";

    private Handler mHandler;
    private View mContent;

    private ReceiverTest rt = null;
    private VibratorTest vibrator = null;

    private int[]colors =
    {
        0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF000000, 0xFFFFFFFF
    };
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);

        mContent = new View(this);
        setContentView(mContent);
        start();
    }

    private void start()
    {

        File filepath = null;

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
        {
            File file = Environment.getExternalStorageDirectory();

            File temp = new File(file.getParent());
            filepath = new File(temp.getParent() + File.separator + "sdcard0" + File.separator + "Song_for_Run_in_Test.wav");

            Log.d(TAG, "start filePath = " + filepath.getPath());
        }

        if (!filepath.exists())
        {
            Log.d(TAG, "REC_TEST_FAIL_INFO : Audio file does not exist");
            saveSharedPrefError();
            AlertDialog.Builder buidler = new AlertDialog.Builder(this);
            buidler.setTitle("Confirm to exit").setMessage("Audio file does not exist!")
                        .setPositiveButton("Confirm", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface arg0, int arg1)
                {
                    dealwithError(mStatusText, "Audio file does not exist");
                }
            }).create()
                .show();
        }
        else
        {
            Log.d(TAG, "start ...........");
            mHandler = new Handler()
            {

                @Override
                public void handleMessage(Message msg)
                {
                    index++;
                    index %= colors.length;
                    mContent.setBackgroundColor(colors[index]);
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            };
            mHandler.sendEmptyMessageDelayed(0, 1000);
            mContent.setBackgroundColor(colors[index]);

            //speak test
            rt = new ReceiverTest(this, filepath.getPath());
            rt.start();

            //Vibrator
            vibrator = new VibratorTest(this);
            vibrator.start();

            if (mMode)
            {
                long delayMillis = (11 * mTime) / 24;
                vibratorHandler.sendEmptyMessageDelayed(0, delayMillis);
            }

        }

    }

    private Handler vibratorHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            vibrator.destroy();
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (rt != null)
            rt.destroy();
        if (vibrator != null)
            vibrator.destroy();

    }

}
