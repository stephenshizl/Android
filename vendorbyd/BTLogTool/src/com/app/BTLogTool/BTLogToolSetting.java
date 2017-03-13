package com.app.BTLogTool;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class BTLogToolSetting extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    static final boolean DBG = true;
    static final String TAG = "BTLogSetting";

    private CheckBoxPreference mAPLogBoxPreference;
    private boolean mBTLogEnable;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.main);
        mBTLogEnable = ((SystemProperties.get("persist.service.btlogs.enable", "0").equals("1"))?true:false);
        mAPLogBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("BTLogBox");
        mAPLogBoxPreference.setChecked(mBTLogEnable);
        mAPLogBoxPreference.setOnPreferenceChangeListener(this);
        if(DBG)
            Log.d(TAG, "getInitValue, BT," + mBTLogEnable);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if ((preference.getKey()).equals("BTLogBox")) {
        	mAPLogBoxPreference.setChecked(!mAPLogBoxPreference.isChecked());
        	mBTLogEnable = mAPLogBoxPreference.isChecked();
            SystemProperties.set("persist.service.btlogs.enable", mBTLogEnable ? "1" : "0");
            if (mBTLogEnable) {
                Log.d(TAG, "persist.service.btlogs.enable = " + SystemProperties.get("persist.service.btlogs.enable", "0"));
                Toast.makeText(this, "System have 	BTLog now!", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "persist.service.btlogs.enable = " + SystemProperties.get("persist.service.btlogs.enable", "0"));
                Toast.makeText(this, "System have no BTLog now!", Toast.LENGTH_LONG).show();
            }
        } else {
            if(DBG)
                Log.d(TAG, "onSharedPreferenceChanged, do nothing");
        }
        return true;
    }
}


