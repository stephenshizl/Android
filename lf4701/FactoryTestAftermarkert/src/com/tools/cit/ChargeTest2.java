
package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import java.io.File;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.os.Message;
import android.os.Handler;

public class ChargeTest2 extends TestModule /* STEP 1 */{

    public static final String CHARGE = "Charge";

    private final String CHARGE_STATUS_CHARGING = "charging";
    private final String CHARGE_STATUS_FULL = "full";
    private final String check_charge_status = "check-charge-status";
    private static final int DIALOG_MSG_PLUGGED = 1;
    BatteryBroadcastReceiver batteryReceiver;
    TextView txtBatteryStatus;
    TextView txtBatteryLevel;
    TextView txtBatteryTechnology;
    TextView txtPowerPlugged;
    TextView txtCurrent;
    int plugged;
    CheckChargeTask checkTask;
    FileReader currentfile;

    Button btnPass;

    private static final String CURRENT = "/sys/class/power_supply/battery/current_now";
    private char buffer[] = new char[10];
    private int len = 0;

    private static final int START_READ = 0;
    private static final int CUR_READ = 1;
    private int current;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.charge, R.drawable.charge);
        plugged = 0;
        batteryReceiver = new BatteryBroadcastReceiver();
        txtBatteryStatus = (TextView) findViewById(R.id.txt_battery_status);
        txtBatteryLevel = (TextView) findViewById(R.id.txt_battery_level);
        txtBatteryTechnology = (TextView) findViewById(R.id.txt_battery_technology);
        txtPowerPlugged = (TextView) findViewById(R.id.txt_power_plugged);
        txtCurrent = (TextView)findViewById(R.id.txt_current);
        btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(false);

        Message msg = mHandler.obtainMessage(START_READ);
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case START_READ:
                    try{
                        FileReader currentfile = new FileReader(CURRENT);
                        len = currentfile.read(buffer, 0, 10);
                        currentfile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    int current0 = Integer.valueOf((new String(buffer, 0, len)).trim());
                    current = current0;
                    txtCurrent.setText(String.valueOf(current0));
                    Message msg0 = mHandler.obtainMessage(CUR_READ);
                    mHandler.sendMessageDelayed(msg0,5000);                    
                    break;
                case  CUR_READ:
                    try{
                        FileReader currentfile = new FileReader(CURRENT);
                        len = currentfile.read(buffer, 0, 10);
                        currentfile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    int current1 = Integer.valueOf((new String(buffer, 0, len)).trim());
                    current = current1;
                    txtCurrent.setText(String.valueOf(current1));
                    Message msg1 = mHandler.obtainMessage(START_READ);
                    mHandler.sendMessageDelayed(msg1,5000);                      
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onStart() {
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeMessages(START_READ);
        mHandler.removeMessages(CUR_READ);
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return CHARGE;
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_MSG_PLUGGED:
                return createPluggedDialog();

            default:
                return null;
        }
    }

    private AlertDialog createPluggedDialog()
    {
        LayoutInflater factory = LayoutInflater.from(this);
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setView(factory.inflate(R.layout.charge_plugged, null));
        return mAlertDialog.create();
    }

    class CheckChargeTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return FTUtil.TCP_Send(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            FTLog.i(this, "CheckCharge result:" + result);
            /*if (("" != result) && (-1 != result.indexOf("ret: 0", 0)))
            {
                int num = result.indexOf("ret: 0", 0);
                result = result.substring(0, num - 1);
                txtBatteryStatus.setText(result);
                if (result.trim().equalsIgnoreCase(CHARGE_STATUS_CHARGING)
                        || result.trim().equalsIgnoreCase(CHARGE_STATUS_FULL))
                {
                    setTestResult(PASS);
                    return;
                }
            }*/
            if(batterylevel >= 40 && batterylevel <= 60){
                       setTestResult(PASS);
              return;
            }
            setTestResult(FAIL);
            return;
        }

    }
    private int batterylevel = 0;
    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

                int maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                double currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                        / (double) maxLevel;
                String currentPercent = (int) Math.floor(currentLevel * 100) + "%";
                txtBatteryLevel.setText(currentPercent);
                int plugged_status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
          batterylevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                FTLog.i(this, "Plugged status: " + plugged_status);
                switch (plugged_status) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        //removeDialog(DIALOG_MSG_PLUGGED);
                        plugged++;
                        txtPowerPlugged.setText("AC");
                        if(currentLevel * 100 >= 40 && currentLevel * 100 <= 60){
                            btnPass.setEnabled(true); 
                        }
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        //removeDialog(DIALOG_MSG_PLUGGED);
                        plugged++;
                        txtPowerPlugged.setText("USB");
                        if(currentLevel * 100 >= 40 && currentLevel * 100 <= 60){
                            btnPass.setEnabled(true); 
                        } 
                        break;
                    default:
                        plugged = 0;
                        txtPowerPlugged.setText(R.string.na);
               if(currentLevel * 100 >= 40 && currentLevel * 100 <= 60){
                            btnPass.setEnabled(true); 
                        } 
                        break;
                }
                txtBatteryTechnology
                        .setText(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));

           if (null == checkTask || AsyncTask.Status.FINISHED == checkTask.getStatus()) {
                        checkTask = new CheckChargeTask();
                        checkTask.execute(check_charge_status);
                    }
                if (1 == plugged) {
                    /*
                    if (null == checkTask || AsyncTask.Status.FINISHED == checkTask.getStatus()) {
                        checkTask = new CheckChargeTask();
                        checkTask.execute(check_charge_status);
                    }
                    else
                        FTLog.i(this, "checkTask is still running");
                        */
                } else if (0 == plugged)
                {
                    //txtBatteryStatus.setText("");
                    //showDialog(DIALOG_MSG_PLUGGED);
                }
            }
        }
    }
}
