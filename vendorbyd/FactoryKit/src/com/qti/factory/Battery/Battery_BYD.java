/*
 * Copyright (c) 2011-2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.Battery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.qti.factory.R;
import com.qti.factory.Utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import java.io.FileInputStream;
import android.os.ServiceManager;
import android.os.IPowerManager;

public class Battery_BYD extends Activity {

    private static final String TAG = "Battery_BYD";
    private String resultString = Utils.RESULT_FAIL;

    private TextView mBatteryStatus = null;
    private TextView tvPower, tvVoltage, tvCurrent,tvTemp,lenovonote;
    private TextView note = null;
    private Button bttntrue,bttnfail;
    private IPowerManager mPowerManager;
    private static final int EVENT_TICK = 1;
    private static int voltage1 = 0;
    final String CAPACITY = "/sys/class/power_supply/battery/capacity";
    final String VOLTAGE_NOW = "/sys/class/power_supply/battery/voltage_now";
    final String STATUS = "/sys/class/power_supply/battery/status";
    final String TEMPRATURE = "/sys/class/power_supply/battery/temp";

@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery);
        setTitle(R.string.battery_name);
        mBatteryStatus = (TextView)findViewById(R.id.sensor);
        tvPower = (TextView) findViewById(R.id.tvPower);
        tvVoltage = (TextView) findViewById(R.id.tvVoltage);
        tvCurrent = (TextView) findViewById(R.id.tvCurrent);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        lenovonote = (TextView) findViewById(R.id.lenovonote);
        note = (TextView)findViewById(R.id.note);
        note.setText(R.string.battery_notification);

        bttntrue = (Button)findViewById(R.id.bttntrue);
        bttntrue.setOnClickListener(BttnTrueListener);

        bttnfail = (Button)findViewById(R.id.bttnfail);
        bttnfail.setOnClickListener(BttnFailListener);

        bttntrue.setEnabled(false);
    }

    private void getservice() {
        if(mPowerManager == null){
            mPowerManager= IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        myHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);
    }

    @Override
    public void onPause() {
        unregisterReceiver(mBatteryInfoReceiver);
        myHandler.removeMessages(EVENT_TICK);
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

    void fail(Object msg) {
        loge(msg);
   //     toast(msg);
        setResult(RESULT_CANCELED);
        resultString=Utils.RESULT_FAIL;
        finish();
    }

    void pass() {
        setResult(RESULT_OK);
        resultString=Utils.RESULT_PASS;
        finish();
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
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int plugType = intent.getIntExtra("plugged", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temp = intent.getIntExtra("temperature", 0);
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                String statusString = null;

                tvPower.setText(getString(R.string.Battery_power) + level * 100 / scale + "%");
                tvVoltage.setText(getString(R.string.battery_info_status_voltage) + " " + voltage + "mV");
                tvTemp.setText(getString(R.string.battery_info_status_temperature) + " " + (float)temp/10+"℃");
                voltage1=voltage;

                if ((status == BatteryManager.BATTERY_STATUS_CHARGING)
                        || (status == BatteryManager.BATTERY_STATUS_FULL)) {
                    statusString = getString(R.string.battery_info_status_charging);
                    if (plugType > 0) {
                        // plugType 1:AC, 2:USB , 4: Wireless ,
                        if ((plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                || (plugType == BatteryManager.BATTERY_PLUGGED_USB)) {
                            statusString = statusString + " "
                                    + getString((plugType == BatteryManager.BATTERY_PLUGGED_AC) ? R.string.battery_info_status_charging_ac
                                            : R.string.battery_info_status_charging_usb);
                            note.setText(statusString);
                            tvCurrent.setText(getString(R.string.battery_info_status_current) + getBatteryCurrent() + "mA");

                            bttntrue.setEnabled(true);

                        } else {
                            note.setText(R.string.battery_plug_in);

                        }
                    }

                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    note.setText(R.string.battery_notification);
                }
            }
        }
    };
    private String getBatteryCurrent() {
        String BatteryCurrent = "";
        FileInputStream is = null;
        int count = 0;
        try {
            is = new FileInputStream("/sys/class/power_supply/bms/current_now");
            byte[] buffer = new byte[20];
            count = is.read(buffer);
            BatteryCurrent = new String(buffer, 0, count);
        } catch (IOException e) {
      //Log.e("No /sys/class/power_supply/battery/current_now =" + e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    //Log.d("/sys/class/power_supply/battery/current_now =" + BatteryCurrent);
        if (count == 0) {
            return null;
        }


        if (count > 4){
        //if(Integer.parseInt(BatteryCurrent.substring(0, count - 4)) > 400)
        if(Integer.parseInt(BatteryCurrent.substring(0, count - 4)) < -400)
        {
            lenovonote.setText(getString(R.string.chargingok));
            bttntrue.setEnabled(true);
        }
        //else if ((Integer.parseInt(BatteryCurrent.substring(0, count - 4)) < 400) && voltage1 > 4100)
        else if ((Integer.parseInt(BatteryCurrent.substring(0, count - 4)) > -400) && voltage1 > 4100)
        {
            lenovonote.setText(getString(R.string.chargingcheck));
        }
        else
        {
            lenovonote.setText(getString(R.string.chargingfail));
        }
            return BatteryCurrent.substring(0, count - 4);}
        else{
        //if(Integer.parseInt(BatteryCurrent.substring(0, count - 1)) > 400)
        if(Integer.parseInt(BatteryCurrent.substring(0, count - 1)) < -400)
        {
            lenovonote.setText(getString(R.string.chargingok));
            bttntrue.setEnabled(true);
        }
        //else if ((Integer.parseInt(BatteryCurrent.substring(0, count - 1)) < 400) && voltage1 > 4100)
        else if ((Integer.parseInt(BatteryCurrent.substring(0, count - 1)) > -400) && voltage1 > 4100)
        {
            lenovonote.setText(getString(R.string.chargingcheck));
        }
        else
        {
            lenovonote.setText(getString(R.string.chargingfail));
        }
            return BatteryCurrent.substring(0, count - 1);}
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case EVENT_TICK:
                tvCurrent.setText(getString(R.string.battery_info_status_current) + getBatteryCurrent() + "mA"); /*battery_info_status_current*/
                sendEmptyMessageDelayed(EVENT_TICK, 500);
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    private String getBatteryInfo(String path) {

        File mFile;
        FileReader mFileReader;
        mFile = new File(path);

        try {
            mFileReader = new FileReader(mFile);
            char data[] = new char[128];
            int charCount;
            String status[] = null;
            try {
                charCount = mFileReader.read(data);
                status = new String(data, 0, charCount).trim().split("\n");
               // Log.d(status[0]);
                return status[0];
            } catch (IOException e) {
               // Log.e(e);
            }
        } catch (FileNotFoundException e) {
            //Log.e(e);
        }
        return null;
    }

}
