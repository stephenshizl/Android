
package com.app.APLogSetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.util.Log;
import android.os.SystemProperties;

public class UserLogReceiver extends BroadcastReceiver{
    private final boolean isDebug = false;

    private static String settingcode = "9990";
    private static String savecode = "9991";
    private static String TAG = "UserLogReceiverReceiver";
    private static String PACKAGE_NAME = "com.app.APLogSetting";
    private static String CLASS_NAME = "com.app.APLogSetting.UserLogSetting";
    private String action;
    private static int STRING_FLAG = 22;

    public void onReceive(Context context,Intent intent){
      if (isDebug)
            Log.i(TAG, "onReceive()");

        action = intent.getAction();
        Uri data = intent.getData();

        int length = data.toString().length();

        if (isDebug)
            Log.i(TAG, "onReceive()::action =" + action + "\ndata =" + data + "length=" + length);
        if (action.equals(action) && (length > STRING_FLAG)
                && data.toString().startsWith("android_secret_code://")
                && settingcode.equals(data.toString().substring(STRING_FLAG))) {

            if (isDebug)
                Log.i(TAG, "onReceive()::Start UserLogSetting");
            Intent newintent = new Intent();
            newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newintent.setClassName(PACKAGE_NAME, CLASS_NAME);
            context.startActivity(newintent);
        }else if (action.equals(action) && (length > STRING_FLAG)
                && data.toString().startsWith("android_secret_code://")
                && savecode.equals(data.toString().substring(STRING_FLAG))){
            Intent newintent = new Intent();
            newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newintent.setClassName("com.app.LogTool", "com.app.LogTool.LogToolActivity");
            context.startActivity(newintent);

        }

 
    }
};
