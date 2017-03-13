
package com.byd.runin.cpu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity.TestStatus;
import com.byd.runin.TestLog.Log;

public class CpuTest
{
    private static final String TAG = "CpuTest";

    private Context mContext;
    //private STATUS mStatus = STATUS.NONE;
    private TextView mStatusText;
    private static final int MSG_CAL_CPU = 1;
    private static final int TIME_CAL_CPU = 1000;
    private static final int MSG_USE_CPU = 2;
    private static final int TIME_USE_CPU = 500;
    private static final int USE_CPU_THRESHOLD = 500000000;
    private static final String ACTION_CPU_CHANGED = "com.byd.runin.cpu.CPU_CHANGED";
    private boolean mExit = false;

    public CpuTest(Context context, TextView status)
    {
        mContext = context;
        mStatusText = status;
    }

    public void start()
    {
        mExit = true;
        SharedPref.saveTestStatus(mContext, CpuActivity.KEY_SHARED_PREF,
            TestStatus.ING);
        mContext.startService(new Intent(mContext, CpuInfoService.class));
        CPUTestThread cpuTestThread = new CPUTestThread();
        for (int i = 0; i < 6; i++)
        {
            Thread cpuTest = new Thread(cpuTestThread);
            cpuTest.start();
        }
        calculateCpuUsage();
        useCpu();
    }

    public void stop()
    {
        mExit = false;
        SharedPref.saveTestStatus(mContext, CpuActivity.KEY_SHARED_PREF,
            TestStatus.DONE);
        Intent intent = new Intent(mContext, CpuInfoService.class);
        intent.putExtra("finish", true);
        mContext.startService(intent);
        //mContext = null;
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
                if (mHandler != null)
                {
                    mHandler.sendEmptyMessageDelayed(MSG_USE_CPU, TIME_USE_CPU);
                }
            }
        }, "UseCpu")
            .start();
    }

    class CPUTestThread implements Runnable
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
                Log.d(TAG, System.currentTimeMillis() + "," + startTime + "," +
                    (System.currentTimeMillis() - startTime));
                // busy loop
                while ((System.currentTimeMillis() - startTime) <= busyTime)
                    ;
                // idle loop
                try
                {
                    Thread.sleep(idleTime);
                }
                catch (InterruptedException e)
                {
                    System.out.println(e);
                }
            }
        }
    }

    private void calculateCpuUsage()
    {

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                float usage = readUsage();
                String text = "Cpu Usage : " + usage + "%";
                final String level = text; updateCpuInfoText(level);
                if (mHandler != null)
                {
                    mHandler.sendEmptyMessageDelayed(MSG_CAL_CPU, TIME_CAL_CPU);
                }
            }
        }, "CalCpu").start();
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
            Log.i("yushengqiang", "wait time = " + idle1);

            //cpu total time
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                Long.parseLong(toks[4]) + Long.parseLong(toks[6]) +
                Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            Log.i("yushengqiang", "user:" + Long.parseLong(toks[2]) + "\nnice:"
                + Long.parseLong(toks[3]) + "\nsystem:" + Long.parseLong
                (toks[4]) + "\niowait:" + Long.parseLong(toks[6]) + "\nirq:" +
                Long.parseLong(toks[7]) + "\nsoftirq:" + Long.parseLong(toks[8]));
            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                Long.parseLong(toks[4]) + Long.parseLong(toks[6]) +
                Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            //cpu usage
            return (int)100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateCpuInfoText(String level)
    {

        Intent intent = new Intent(mContext, CpuInfoService.class);
        intent.putExtra("update", true);
        intent.putExtra("level", level);
        mContext.startService(intent);
    }


}
