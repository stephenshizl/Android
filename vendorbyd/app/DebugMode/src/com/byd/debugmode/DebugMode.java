package com.byd.debugmode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.UserManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

public class DebugMode extends PreferenceActivity {
    static final String TAG = "DebugMode";
    private Preference mExample;
    private Preference mUms;
    private static final String KEY_EXAMPLE = "primary_example";
    private static final String KEY_UMS = "primary_ums";
    private UsbManager mUsbManager;
    private StorageManager mStorageManager = null;

    private static final String EXTERNAL_STORAGE_PATH = "/storage/sdcard1";
    private String defaultfunction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen root = getPreferenceScreen();
        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        defaultfunction  = mUsbManager.getDefaultFunction();
        mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        addPreferencesFromResource(R.xml.preference);
        root = getPreferenceScreen();
        mExample = (Preference)root.findPreference(KEY_EXAMPLE);
        mUms = (Preference)root.findPreference(KEY_UMS);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("debugmode","defaultfunction is" + defaultfunction);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStorageManager != null) {
            mUsbManager.setCurrentFunction(defaultfunction, true);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
       if(preference==mExample){

        } else if(preference == mUms && externalSdcardExist()) {

            Log.i("debugmode","For ####33284# externalSdcard is exist");
//        	Intent intent = new Intent();
//              intent.setComponent(new ComponentName("com.android.settings","com.android.settings.UsbSettings"));
//              startActivity(intent);
            String function = UsbManager.USB_FUNCTION_MASS_STORAGE;
            mUsbManager.setCurrentFunction(function, true);
            mStorageManager.enableUsbMassStorage();
            Toast.makeText(this, R.string.ums_turn_on,
                Toast.LENGTH_LONG).show();
        } else {
            Log.i("debugmode","For ####33284# externalSdcard is not exist");
            Toast.makeText(this, R.string.ums_inster_SDCard,
                Toast.LENGTH_LONG).show();
                }

        return true;
    }

   private boolean externalSdcardExist() {
       StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
       if (storageVolumes != null) {
           int length = storageVolumes.length;
           for (int i = 0; i < length; i++) {
               StorageVolume storageVolume = storageVolumes[i];
               if (EXTERNAL_STORAGE_PATH.equals(storageVolumes[i].getPath())) {
                   String storageState = storageVolumes[i].getState();
                   if(!((Environment.MEDIA_REMOVED).equals(storageState)
                           ||(Environment.MEDIA_BAD_REMOVAL).equals(storageState)
                           ||(Environment.MEDIA_UNKNOWN).equals(storageState))) {
                       return true;
                   }
               }
           }
       }
       return false;
   }
}