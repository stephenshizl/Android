
package com.byd.runin.reboot;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import com.byd.runin.TestActivity;

public class RebootActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_reboot_test";
    public static final String TITLE = "Reboot Test";

    public static final String KEY_SHARED_PREF_TEST_TIME = "key_reboot_test_time";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }
}
