
package com.qti.factory.OTG;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import android.os.SystemProperties;

import com.qti.factory.R;
import com.qti.factory.Utils;

public class OTG extends Activity {

    final String TAG = "OTG";
    String resultString = Utils.RESULT_FAIL;
    private TextView umsLog;
    private Button bttntrue ,bttnfail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otg);

        umsLog = (TextView) findViewById(R.id.otg_log);

        ((Button) findViewById(R.id.otg_retry)).setOnClickListener(BttnOTGRetryListener);

        bttntrue = (Button)findViewById(R.id.bttntrue);
        bttntrue.setOnClickListener(BttnTrueListener);

        bttnfail = (Button)findViewById(R.id.bttnfail);
        bttnfail.setOnClickListener(BttnFailListener);
        bttntrue.setEnabled(false);
    }

    private void startTest(){
        String path="/sys/bus/usb/devices/1-1/idProduct";
        File mfile=new File(path);
        if(mfile.exists()){
            appendLog(R.string.otg_in_test2);
            bttntrue.setEnabled(true);
        }else{
            appendLog(R.string.otg_in_test1);
            bttntrue.setEnabled(false);
        }
    }
    private void appendLog(int res) {
        umsLog.append("\n");
        umsLog.append(getResources().getString(res));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("wxb", "on resume");
        startTest();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private OnClickListener BttnTrueListener = new OnClickListener() {
        public void onClick(View v){
            pass();
        }
    };

    private OnClickListener BttnFailListener = new OnClickListener() {
        public void onClick(View v) {
            fail(null);
        }
    };
    private OnClickListener BttnOTGRetryListener = new OnClickListener() {
        public void onClick(View v){
            startTest();
        }
    };
    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        resultString = Utils.RESULT_FAIL;
        finish();
    }

    void pass() {
        setResult(RESULT_OK);
        resultString=Utils.RESULT_PASS;
        Utils.enableBluetooth(false);
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
}


