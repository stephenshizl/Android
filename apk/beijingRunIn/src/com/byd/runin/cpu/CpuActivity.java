
package com.byd.runin.cpu;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.widget.TextView;

import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity;
import com.byd.runin.TestActivity.TestStatus;
import com.byd.runin.TestLog.Log;
import com.byd.runin.cpu.CpuTest.CPUTestThread;

public class CpuActivity extends TestActivity
{
    private static final String TAG = "CpuActivity";
    public static final String KEY_SHARED_PREF = "key_cpu_test";
    public static final String TITLE = "CPU Test";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_cpu_test_time";
    private static final String CPU_TEST_FAIL_INFO = "cpu test fail: ";
    private static final int MSG_CAL_CPU = 1;
    private static final int TIME_CAL_CPU = 500;
    private static final int MSG_USE_CPU = 2;
    private static final int TIME_USE_CPU = 500;
    private static final int USE_CPU_THRESHOLD = 500000000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;
        super.onCreate(savedInstanceState);

        mStatusText.setText("cpu test......");

        start();
    }

    private void start()
    {
        Log.d(TAG, "CpuActivity start");
        startServiceAsUser(new Intent(this, CpuInfoService.class),
            UserHandle.OWNER);
        for (int i = 0; i < 15; i++)
        {
            Thread cpuTest = new Thread(mCpuTestRunnable);
            cpuTest.start();
        }
        calculateCpuUsage();
        useCpu();
    }

    private void stop()
    {
        Intent intent = new Intent(this, CpuInfoService.class);
        intent.putExtra("finish", true);
        startServiceAsUser(intent, UserHandle.OWNER);
    }
    @Override
    protected void onDestroy()
    {
        stop();
        super.onDestroy();

        mHandler.removeMessages(MSG_CAL_CPU);
        mHandler.removeMessages(MSG_USE_CPU);
        mHandler = null;
    }

    private void useCpu()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int a = USE_CPU_THRESHOLD;
                while (true)
                {
                    if (a == 1)break; a--;
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mHandler != null)
                        {
                            mHandler.sendEmptyMessageDelayed(MSG_USE_CPU,
                                TIME_USE_CPU);
                        }
                    }
                }
                );
            }
        }
        , "UseCpu")
            .start();
    }

    private Runnable mCpuTestRunnable = new Runnable()
    {

        @Override
        public void run()
        {
            int busyTime = 10;
            int idleTime = busyTime;
            long startTime = 0;
            while (true)
            {
                startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) <= busyTime)
                {
                    ;
                }
                try
                {
                    Thread.sleep(idleTime);
                }
                catch (InterruptedException e)
                {
                    Log.d(TAG, e.getMessage());
                    dealwithError(mStatusText, CPU_TEST_FAIL_INFO +
                        e.getMessage());
                }
            }
        }
    };

    private void calculateCpuUsage()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                float usage = readUsage(); String text = "Cpu Usage : " + usage
                    + "%"; final String finalText = text; runOnUiThread(new
                    Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateCpuInfoText(finalText); if (mHandler != null)
                        {
                            mHandler.sendEmptyMessageDelayed(MSG_CAL_CPU,
                                TIME_CAL_CPU);
                        }
                    }
                }
                );
            }
        }, "CalCpu")
            .start();
    }

    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CAL_CPU:
                    calculateCpuUsage();
                    break;

                case MSG_USE_CPU:
                    useCpu();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };

    /**
     * cpu usage compute
     * @return
     */
    private float readUsage()
    {
        try
        {
            //read cpu info
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[]toks = load.split(" ");
            //cpu free time
            long idle1 = Long.parseLong(toks[5]);
            //cpu total time
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                Long.parseLong(toks[4]) + Long.parseLong(toks[6]) +
                Long.parseLong(toks[5]) + Long.parseLong(toks[7]) +
                Long.parseLong(toks[8]);

            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                Log.d(TAG, e.getMessage());
                dealwithError(mStatusText, CPU_TEST_FAIL_INFO + e.getMessage());
            }

            reader.seek(0);

            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                Long.parseLong(toks[4]) + Long.parseLong(toks[6]) +
                Long.parseLong(toks[5]) + Long.parseLong(toks[7]) +
                Long.parseLong(toks[8]);

            //cpu usage
            return 100 * ((cpu2 - cpu1) - (idle2 - idle1)) / (cpu2 - cpu1);
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
            dealwithError(mStatusText, CPU_TEST_FAIL_INFO + e.getMessage());
        }

        return 0;
    }

    private void updateCpuInfoText(String level)
    {
        Intent intent = new Intent(this, CpuInfoService.class);
        intent.putExtra("update", true);
        intent.putExtra("level", level);
        startServiceAsUser(intent, UserHandle.OWNER);
    }
}
