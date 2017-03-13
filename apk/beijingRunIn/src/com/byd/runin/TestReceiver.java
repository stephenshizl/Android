
package com.byd.runin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

public class TestReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            if ("true" .equals(SystemProperties.get("persist.sys.runin.test")))
            {
                Intent i = new Intent(context, BootCompleteActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }


}
