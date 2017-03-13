
package com.byd.runin.battery;

import android.os.Bundle;
import android.widget.TextView;
import com.byd.runin.CITTest;
import com.byd.runin.TestActivity;

public class BatteryActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_battery_test";
    public static final String TITLE = "Battery Test";

    private TextView mStatusText;

    private BatteryTest mBatteryTest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;
        super.onCreate(savedInstanceState);

        mStatusText = new TextView(this);
        mStatusText.setTextSize(30);
        setContentView(mStatusText);
        mBatteryTest = new BatteryTest(this, mStatusText);
        mBatteryTest.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mBatteryTest.stop();
        /*CITTest citTest1 = new CITTest();
        if(!citTest1.getChargingStatus().equals("charging")){
        citTest1.setCharging();
        }*/
    }

    @Override
    public void saveSharedPrefDoing()
    {
        //disable this function, add in BatteryTest
    }

    @Override
    public void saveSharedPrefDone()
    {
        //disable this function, add in BatteryTest
    }
}
