/*
 *        Date              Author             CR/PR                         Reference
 *    ==========          ==========         =================         =====================================
 *
 */
package com.app.LogTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import android.view.WindowManager;
import android.os.Environment;

public class LogToolActivity extends PreferenceActivity {
    static final String TAG = "LogToolActivity";
    private Preference mSaveAllLogsPref;
    LogSaver mLogSaver;
    //dialog id
    private static final int DIALOG_SAVE_ALL = 0;
    private static final int EVENT_SAVE_ALL_COMPLETED = 10;
    private static final int EVENT_AUTO_SAVELOG_START = 11;
    private static final String ACTION_PANSIC_SALL_LOG ="Intent.action.panic_savelog";
    private String mSaveAllMsg;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_SAVE_ALL_COMPLETED:
                    removeDialog(DIALOG_SAVE_ALL);
                    getPreferenceScreen().setEnabled(true);
                    finish();
                    break;

                case EVENT_AUTO_SAVELOG_START:
                    onPreferenceTreeClick(null , mSaveAllLogsPref);
                    break;
            }

            return;
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLogSaver = new LogSaver(this);
        setPreferenceScreen(createPreferenceHierarchy());
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(TAG, "AutoLogtools start.");

        if (action != null) {
            if (action.equals(ACTION_PANSIC_SALL_LOG) ) {
                Log.d(TAG, "Recive intent save alllog.");
                Message msg = new Message();
                msg.what = EVENT_AUTO_SAVELOG_START;
                mHandler.sendMessage(msg);
            }
        }
    }

    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // SaveAllLog
        PreferenceCategory saveAllLogsPrefCat = new PreferenceCategory(this);
        saveAllLogsPrefCat.setTitle(R.string.saveAllLogs_prefs);
        root.addPreference(saveAllLogsPrefCat);
        mSaveAllLogsPref = getPreferenceManager().createPreferenceScreen(this);
        mSaveAllLogsPref.setKey("saveAllLogs_preference");
        mSaveAllLogsPref.setTitle(R.string.save_all);
        mSaveAllLogsPref.setSummary(R.string.summary_save_all_logs);
        saveAllLogsPrefCat.addPreference(mSaveAllLogsPref);

        return root;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference == mSaveAllLogsPref) {
            boolean mediaMounted = false;
            mediaMounted = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

            if(mediaMounted) {
                if (mLogSaver.externalHasEnoughMemory()) {
                    String saveAllStr = mSaveAllLogsPref.getTitle().toString();
                    saveAll();
                    showDialog(DIALOG_SAVE_ALL);
                } else {
                    Toast.makeText(this, R.string.no_space, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_LONG).show();
            }
        }

        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if((id == DIALOG_SAVE_ALL)) {
            ProgressDialog dialog = new ProgressDialog(this);
            mSaveAllMsg = getResources().getString(R.string.saving_all_log);

            switch (id) {
                case DIALOG_SAVE_ALL:
                default:
                    dialog.setMessage(mSaveAllMsg);
                    dialog.setCancelable(false);
                    dialog.setIndeterminate(true);
                    break;
            }

            return dialog;
        }

        return null;
    }

    private void saveAll() {
        Thread mTasks = new Thread() {
            public void run() {
                boolean result = mLogSaver.saveall();
                Log.d(TAG, "saving result is" + result);
                Message msg = new Message();
                msg.what = EVENT_SAVE_ALL_COMPLETED;
                mHandler.sendMessage(msg);

            };
        };
        mTasks.start();
    }
}
