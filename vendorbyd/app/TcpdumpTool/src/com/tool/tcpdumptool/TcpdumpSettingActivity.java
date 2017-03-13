package com.tool.tcpdumptool;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;

public class TcpdumpSettingActivity extends PreferenceActivity {
    private static final String TAG = "TcpdumpSettingActivity";
    private CheckBoxPreference mTcpDumpPref = null;
    private static final String TCP_LOG_PROP_NAME = "persist.service.tcpdump.enable";
    private static final boolean isUserVersion = (SystemProperties.getInt("ro.debuggable", 0) != 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.tcpdump);
        mTcpDumpPref = (CheckBoxPreference)findPreference("tcpdump_enable_checkbox");
        refreshCheckboxState();
        Log.d(TAG, "mTcpDumpPref=" + mTcpDumpPref);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // TODO Auto-generated method stub
        if(preference == mTcpDumpPref){
            //check if media is mounted
            /*
            boolean mediaMounted = false;
            mediaMounted = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if(!mediaMounted) {
                Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_LONG).show();
                mTcpDumpPref.setChecked(false);
                return true;
            } 
            */
            /*
            if(isUserVersion){
                Toast.makeText(this, R.string.user_version_text, Toast.LENGTH_LONG).show();
                mTcpDumpPref.setChecked(false);
                return true;
            }
            */
            //setCheckbox status
            boolean enable = mTcpDumpPref.isChecked();
            Log.d(TAG, "mPppDumpPref enable=" + enable);
            if(enable){
                SystemProperties.set(TCP_LOG_PROP_NAME, "1");
            }else{
                SystemProperties.set(TCP_LOG_PROP_NAME, "0");
            }
        }
        return true;
    }
    private void refreshCheckboxState(){
        Log.d(TAG, "refreshCheckboxState()");
        String LogState = null;
        LogState = SystemProperties.get(TCP_LOG_PROP_NAME);
        Log.d(TAG, TCP_LOG_PROP_NAME + "=" + LogState);
        if(null == LogState){
            Log.d(TAG, "null ="+TCP_LOG_PROP_NAME);
            return;
        }
        if(LogState.equals("1")){
            mTcpDumpPref.setChecked(true);
        }else{
            mTcpDumpPref.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onResume()");
        refreshCheckboxState();
        super.onResume();
    }
}
