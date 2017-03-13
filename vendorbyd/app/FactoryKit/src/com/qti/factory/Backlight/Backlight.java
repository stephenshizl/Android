package com.qti.factory.Backlight;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ContentResolver;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Message;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.os.RemoteException;

import com.qti.factory.R;
import com.qti.factory.Utils;

public class Backlight extends Activity {

    private static final String TAG = "Backlight";
    private String resultString = Utils.RESULT_FAIL;
    private TextView mTextView;
    private Button btntrue,btnfail;
    private boolean mExit;

    private IPowerManager mPowerManager;
    private static final int BLIGHT_ON = 0;
    private static final int BLIGHT_OFF = 1;
    private static final int BRIGHTNESS_MIN = 0;
    private static final int BRIGHTNESS_MAX = 255;
    private int oldBrightness = 0;


    Handler myHandler = new Handler(){
        public void handleMessage(Message msg){
        if(mExit) return;
            switch(msg.what){
                case BLIGHT_ON:
                    setScreenBrightness(BRIGHTNESS_MAX);
                    btntrue.setEnabled(true);
                    break;
                case BLIGHT_OFF:
                    setScreenBrightness(BRIGHTNESS_MIN);
                    Message close = new Message();
                    close.what = BLIGHT_ON;
                    myHandler.sendMessageDelayed(close, 4000);
                    break;
                default :
                    break;
            }
        super.handleMessage(msg);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backlight);

        bindView();
        getservice();

    }

    private void bindView() {

        mTextView = (TextView)findViewById(R.id.backlight_test_hint);
        mTextView.setText(R.string.backlight_test_hint);

        btntrue = (Button)findViewById(R.id.btntrue);
        btntrue.setOnClickListener(BtnTrueListener);

        btnfail = (Button)findViewById(R.id.btnfail);
        btnfail.setOnClickListener(BtnFailListener);

        btntrue.setEnabled(false);
    }

    private void getservice() {
        if(mPowerManager == null){
            mPowerManager= IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        }
    }

    private int getOldBrightness() {
        int oldBrightness = 0;
        try {
             oldBrightness = Settings.System.getInt(getBaseContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
        }
        return oldBrightness;
    }

    private void setScreenBrightness(int brightness) {
        if(mPowerManager != null) {
            try {
               // mPowerManager.setBacklightBrightness(brightness);
                mPowerManager.setTemporaryScreenBrightnessSettingOverride(brightness);

            } catch(RemoteException darn) {
                loge("setScreenBrightness set backlight brightness failed");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Message open = new Message();
        open.what = BLIGHT_OFF;
        myHandler.sendMessageDelayed(open, 1000);

        oldBrightness = getOldBrightness();
        logd("oldBrightness is " + oldBrightness);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

     @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPowerManager != null) {
            try {
                //mPowerManager.setBacklightBrightness(oldBrightness);
                mPowerManager.setTemporaryScreenBrightnessSettingOverride(oldBrightness);
            } catch(RemoteException darn) {
                loge("onDestroy set backlight brightness failed");
            }
        }
        mPowerManager = null;
    }

    private OnClickListener BtnTrueListener = new OnClickListener() {
        public void onClick(View v) {
            pass();
        }
    };

    private OnClickListener BtnFailListener = new OnClickListener() {
        public void onClick(View v) {
            fail(null);
        }
    };

    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        resultString=Utils.RESULT_FAIL;
        finish();
    }

    void pass() {
        setResult(RESULT_OK);
        resultString=Utils.RESULT_PASS;
        finish();
    }

    public void toast(Object s) {
        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    private void loge(Object e) {
        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

/*
    public  synchronized  void runthread() {
        Thread lightThread = new Thread(){
            public void run(){
                for(int i=0;i<6;i++){
                    //mLED_tool.citTestLight(BACKLIGHT,20*i);
                    //sleep(800);
                }
            }
        };
    }
*/
}



