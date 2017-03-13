
package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.internal.telephony.TelephonyIntents;
import com.android.internal.telephony.IccCardConstants;

public class SIMTest extends TestModule {
    private static String SIM = "SIM";

    private TextView txtLog;
    public static final int button_enable=1;
    public static final int button_disabe=0;
    private SimCardReceiver mBroadcastReceiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.sim, R.drawable.sim);
        txtLog=(TextView) findViewById(R.id.sim_txt_log);
     ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
      filter = new IntentFilter(TelephonyIntents.ACTION_SIM_STATE_CHANGED);
      mBroadcastReceiver=new SimCardReceiver();
           testThread.start();
    }

    private Thread testThread = new Thread(new Runnable() {
        @Override
        public void run() {
            showSIMParams();
        }
    });

    private void showSIMParams() {
    
        runOnUiThread(new UIThread(getResources().getString(R.string.sim_checking_status), Color.WHITE));
        FTUtil.waitforcheck();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
     boolean hasSimcard=tm.hasIccCard();
        switch (tm.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                runOnUiThread(new UIThread(getResources().getString(R.string.sim_ready), Color.GREEN));
                String phoneID=tm.getSubscriberId();
                if (!(phoneID == null || phoneID.trim().equals("")))
                runOnUiThread(new UIThread(getResources().getString(R.string.sim_imsi)+phoneID, Color.GREEN));
                runOnUiThread(new UIThread(getResources().getString(R.string.sim_finished), Color.WHITE));
         //       setTestResult(PASS);
                runOnUiThread(new UIThread2(button_enable));
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
          if(hasSimcard){    
                    runOnUiThread(new UIThread(getResources().getString(R.string.sim_absent), Color.RED));
                    runOnUiThread(new UIThread(getResources().getString(R.string.sim_fail), Color.RED));

         }else{
             runOnUiThread(new UIThread(getResources().getString(R.string.sim_remove), Color.RED));
                      runOnUiThread(new UIThread(getResources().getString(R.string.sim_fail), Color.RED));
         }

                // set result
          //      setTestResult(FAIL, R.string.sim_absent_msg);
                break;
            default:
         if(hasSimcard){
             
                   runOnUiThread(new UIThread(getResources().getString(R.string.na), Color.RED));
                   runOnUiThread(new UIThread(getResources().getString(R.string.sim_fail), Color.RED));
         }else{
             runOnUiThread(new UIThread(getResources().getString(R.string.sim_remove), Color.RED));
                   runOnUiThread(new UIThread(getResources().getString(R.string.sim_fail), Color.RED));

         }
           //     setTestResult(FAIL, R.string.na);
                ;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
     registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
     unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public String getModuleName() {
        return SIM;
    }

    class UIThread implements Runnable {
        String string;
        int color;

        public UIThread(String string, int color) {
            super();
            this.string = string;
            this.color = color;
        }

        @Override
        public void run() {
            appendMsg(string, color);
        }

    }
      class UIThread2 implements Runnable{

        int id;

        public UIThread2(int id) {
            super();
            this.id = id;
        }

        @Override
        public void run() {
            appendMsg(id);
        }

   }
    private void appendMsg(String text, int color) {
        txtLog.append(FTUtil.coloredString(text, color));
        txtLog.append("\n");
    }

    private void appendMsg(int resId, int color) {
        txtLog.append(FTUtil.coloredString(SIMTest.this, resId, color));
        txtLog.append("\n");
    }
    private void appendMsg(int id) {
      if(id==button_enable){
         ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
          //setTestResult(PASS);
       }

    }
      private class SimCardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
              final String action = intent.getAction();
        String stateExtra = intent.getStringExtra(IccCardConstants.INTENT_KEY_ICC_STATE);
        if (IccCardConstants.INTENT_VALUE_ICC_ABSENT.equals(stateExtra)) {
          
        }
        else if (IccCardConstants.INTENT_VALUE_ICC_READY.equals(stateExtra)) {
            showSIMParams();             
         }    
        }
      }
    
}
