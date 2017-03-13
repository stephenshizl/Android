
package com.byd.runin.s3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byd.runin.AutoTest;
import com.byd.runin.TestLog.Log;

public class S3Receiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {

        String action = intent.getAction();

        if (action.equals(S3Activity.ACTION_WAKE_UP))
        {
            Log.d("S3Receiver", "onReceive");
            if (S3Activity.sInstance != null)
            {
                S3Activity.sInstance.sendWakeupMessage();
            }
        }
    }
}
