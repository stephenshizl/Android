package com.app.LogTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoSavelogReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutoSavelogReceiver", "received intent for save");

        String action = intent.getAction();
        Intent autosaveintent = new Intent(action);
        autosaveintent.setClass(context, LogToolActivity.class);
        autosaveintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);/*for start activity*/
        context.startActivity(autosaveintent);

    }
}
