
package com.byd.runin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import static com.android.internal.telephony.TelephonyIntents.SECRET_CODE_ACTION;
public class RunInReceiver extends BroadcastReceiver {

    public RunInReceiver(){

    }

    @Override public void onReceive(Context arg0, Intent arg1)
    {
        // TODO Auto-generated method stub
        if (arg1.getAction().equals(SECRET_CODE_ACTION))
        {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClass(arg0, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            arg0.startActivity(i);
        }
    }
}
