
package com.tools.cit;

import com.tools.util.FTLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FactoryTestStarter extends BroadcastReceiver {
    private final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SECRET_CODE_ACTION)) {
            FTLog.d(this, "FactoryTestStarter receive secret code 7388");
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClass(context, PhoneInfo.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
