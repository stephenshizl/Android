
package com.byd.runin.vibrator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.byd.runin.TestLog.Log;
import com.byd.runin.lcd.LcdActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VibratorTest
{
    private static final String TAG = "VibratorActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_receiver_test_time";

    //private static final String path = "/sys/class/pci_bus/0000:00/device/0000:00:17.0/vibrator";
    //private static final String path = "/sys/class/timed_output/vibrator/enable/time_out";
    private static final String path = "/sys/class/timed_output/vibrator/enable";
    private static final String off = "0";
    private static final String on = "9000";

    public static final int MSG_FINISH_ENTRY = 1;
    private static final int VIBRATOR_TIME_CYCEL = 1000;
    private static final int VIBRATOR_START_MSG = 2;

    private static final int VIBRATOR_STOP_MSG = 3;

    private TextView mStatusText;
    Context mContext;
    private LcdActivity lcd = null;

    public VibratorTest(Context context)
    {
        mContext = context;
        lcd = (LcdActivity)mContext;
        mStatusText = lcd.mStatusText;


    }

    public void start()
    {
        startVibator();
    }

    private void startVibator()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                control(path, on);
                lcd.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mHandler != null)
                        {
                            mHandler.sendEmptyMessageDelayed(VIBRATOR_STOP_MSG,
                                VIBRATOR_TIME_CYCEL);
                        }
                    }
                }
                );
            }
        }, "startVibator").start();
    }

    private void stopVibator()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                control(path, off);

                lcd.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mHandler != null)
                        {
                            mHandler.sendEmptyMessageDelayed(VIBRATOR_START_MSG, VIBRATOR_TIME_CYCEL);
                        }
                    }
                }
                );
            }
        }, "stopVibator").start();
    }

    public Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case VIBRATOR_STOP_MSG:
                    stopVibator();
                    break;
                case VIBRATOR_START_MSG:
                    startVibator();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    public void destroy()
    {
        if (mHandler != null)
        {
            mHandler.removeMessages(VIBRATOR_START_MSG);
            mHandler.removeMessages(VIBRATOR_STOP_MSG);
        }

        control(path, off);
        mHandler = null;

    }

    private void control(String path, String val)
    {
        File file = new File(path);
        try
        {
            FileWriter writer = new FileWriter(file);
            writer.write(val);
            writer.close();
        }
        catch (IOException e)
        {
            Log.d("Vibrator", "control................");
            e.printStackTrace();
        }
    }
}
