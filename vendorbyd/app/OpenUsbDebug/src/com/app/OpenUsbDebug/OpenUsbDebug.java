package com.app.OpenUsbDebug;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.PreferenceManager;
import android.util.Log;
import android.os.SystemProperties;
import android.widget.Toast;
import android.os.PowerManager;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.content.BroadcastReceiver;
import android.preference.CheckBoxPreference;

public class OpenUsbDebug extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    static final String TAG = "OpenUsbDebug";
    static final boolean DBG =true;

    private static final String KEY_USBDEBUG = "usbdebug";
    private static final String FUCTION = "diag,serial_smd,serial_tty,mass_storage";
    private static final String FUCTION_ADB = "adb,diag,serial_smd,serial_tty,mass_storage";
    private CheckBoxPreference mUsbdebug;
    private UsbManager mUsbManager;
    private boolean mUsbdebugEnable;
    private String currentFunction;
    private String defaltFunction;

    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context content, Intent intent) {
            if (intent.getAction().equals(UsbManager.ACTION_USB_STATE)) {
                boolean connected = intent.getExtras().getBoolean(UsbManager.USB_CONNECTED);
                if (!connected) {
                    // It was disconnected from the plug, disable any select
                    if(DBG) Log.d(TAG, "usb disconnected!");
                    mUsbdebug.setChecked(false);
                    mUsbdebug.setEnabled(false);
                } else {
                    if(DBG) Log.d(TAG, "usb connected!");
                    mUsbdebug.setEnabled(true);
                    currentFunction = SystemProperties.get("sys.usb.config","");
                    if ( FUCTION.equalsIgnoreCase(currentFunction) || FUCTION_ADB.equalsIgnoreCase(currentFunction)){
                          mUsbdebug.setChecked(true);
                      } else {
                          mUsbdebug.setChecked(false);
                      }
                }
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mUsbManager==null)
            mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        PreferenceScreen root = getPreferenceScreen();
        if (root != null) {
            root.removeAll();
        }
        addPreferencesFromResource(R.xml.preference);
        root = getPreferenceScreen();
        if (root!=null) {
            mUsbdebug = (CheckBoxPreference)root.findPreference(KEY_USBDEBUG);
            mUsbdebug.setOnPreferenceChangeListener(this); 
        } else {
            Log.e(TAG, "root is null!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mStateReceiver,
                new IntentFilter(UsbManager.ACTION_USB_STATE));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if ((preference.getKey()).equals(KEY_USBDEBUG)) {
            mUsbdebug.setChecked(!mUsbdebug.isChecked());
            mUsbdebugEnable = mUsbdebug.isChecked();
            if (mUsbdebugEnable) {
                if(DBG) Log.d(TAG, "enable usbdebug");
                mUsbManager.setCurrentFunction(FUCTION_ADB);
                Toast.makeText(this, "enabled usbdebug", Toast.LENGTH_LONG).show();
            } else {
                if(DBG) Log.d(TAG, "disable usbdebug");
                defaltFunction = getDefaultFunctions();
                if(DBG) Log.d(TAG, "defaltFunciton is " + defaltFunction);
                mUsbManager.setCurrentFunction(defaltFunction);
                Toast.makeText(this, "disabled usbdebug", Toast.LENGTH_LONG).show();
            }
        } else {
            if(DBG)
                Log.d(TAG, "onSharedPreferenceChanged, do nothing");
        }
        return true;
    }
    private String getDefaultFunctions() {
        String func = SystemProperties.get("persist.sys.usb.config",
                UsbManager.USB_FUNCTION_NONE);
        if (UsbManager.USB_FUNCTION_NONE.equals(func)) {
            func = UsbManager.USB_FUNCTION_MTP;
        }
        return func;
    }
}

