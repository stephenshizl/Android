
package com.byd.runin.cpu;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.byd.runin.TestLog.Log;

public class CpuInfoService extends Service
{
    private static final String TAG = "CpuInfoService";

    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;

    private CpuInfo mCpuInfo;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mCpuInfo = new CpuInfo(this);

        Log.d(TAG, "onCreate");
    }

    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            Intent intent = (Intent)msg.obj;
            boolean finish = intent.getBooleanExtra("finish", false);
            if (finish)
            {
                stopSelf();
                mCpuInfo.hide();
                return ;
            }

            boolean update = intent.getBooleanExtra("update", false);
            if (update)
            {
                //String state = intent.getStringExtra("state");
                String level = intent.getStringExtra("level");
                mCpuInfo.setText(level);
                return ;
            }

            mCpuInfo.show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Message msg = mServiceHandler.obtainMessage();
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mServiceLooper.quit();

        Log.d(TAG, "onDestroy");
    }
}
