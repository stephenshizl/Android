
package com.byd.runin;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class VibratorUtil
{

    private static Vibrator vib;

    public static void vibrate(final Activity activity, long[]pattern, boolean isRepeat)
    {
        vib = (Vibrator)activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 0 :  - 1);
    }

    public static void cancel()
    {
        if (vib != null)
        {
            vib.cancel();
        }


    }
}
