package com.app.APLogSetting;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import android.util.TypedValue;
public class UserLogSetting extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    static final boolean DBG = true;
    static final String TAG = "AndroidLogSetting";

    private CheckBoxPreference mAPLogBoxPreference;
    private boolean mAPLogEnable;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.main);
        mAPLogEnable = ((SystemProperties.get("persist.service.apklogfs.enable", "0").equals("1"))?true:false);
        mAPLogBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("APLogBox");
        mAPLogBoxPreference.setChecked(mAPLogEnable);
        mAPLogBoxPreference.setOnPreferenceChangeListener(this);
        if(DBG)
            Log.d(TAG, "getInitValue, AP," + mAPLogEnable);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if ((preference.getKey()).equals("APLogBox")) {
            mAPLogBoxPreference.setChecked(!mAPLogBoxPreference.isChecked());
            mAPLogEnable = mAPLogBoxPreference.isChecked();
            //SystemProperties.set("persist.service.apklogfs.enable", mAPLogEnable ? "1" : "0");
           // SystemProperties.set("persist.service.crashlog.enable", mAPLogEnable ? "1" : "0");
            if (mAPLogEnable) {
                do{
                    SystemProperties.set("persist.service.apklogfs.enable","1" );
                    SystemProperties.set("persist.service.crashlog.enable","1");
                }while(SystemProperties.get("persist.service.apklogfs.enable", "0") == "1"
                      && SystemProperties.get("persist.service.crashlog.enable", "0") == "1");
                Log.d(TAG, "persist.service.apklogfs.enable = " + SystemProperties.get("persist.service.apklogfs.enable", "0"));
                Log.d(TAG, "persist.service.crashlog.enable = " + SystemProperties.get("persist.service.crashlog.enable", "0"));
                Toast.makeText(this, "System have APLog now!", Toast.LENGTH_LONG).show();
            } else {
                do{
                    SystemProperties.set("persist.service.apklogfs.enable","0" );
                    SystemProperties.set("persist.service.crashlog.enable","0");
                 }while(SystemProperties.get("persist.service.apklogfs.enable", "0") == "0"
                      && SystemProperties.get("persist.service.crashlog.enable", "0") == "0");
                Log.d(TAG, "persist.service.apklogfs.enable = " + SystemProperties.get("persist.service.apklogfs.enable", "0"));
                Log.d(TAG, "persist.service.crashlog.enable = " + SystemProperties.get("persist.service.crashlog.enable", "0"));
                Toast.makeText(this, "System have no APLog now!", Toast.LENGTH_LONG).show();
            }
        } else {
            if(DBG)
                Log.d(TAG, "onSharedPreferenceChanged, do nothing");
        }
        return true;
    }
}


