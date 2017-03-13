package com.tools.customercit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receive test results from test module and write to database.
 * @author wangx
 *
 */
public class TestResultReceiver extends BroadcastReceiver {

    public static final String DELIVER_RESULT_ACTION = "com.tools.action.SFTT_DELIVER_RESULT";
    
    @Override
    public void onReceive(Context arg0, Intent arg1) {

        Intent mIntent=new Intent();
        mIntent.setAction(DELIVER_RESULT_ACTION);
        mIntent.putExtras(arg1.getExtras());//deliver the Extras to StabilityTestActivity
        mIntent.setClassName("com.tools.stabilitytest", "com.tools.stabilitytest.StabilityTestActivity");
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        arg0.startActivity(mIntent);
    }

}
