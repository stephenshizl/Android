package com.tools.customercit;

import java.io.FileOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.SystemProperties;

public class ThreeColorTest extends TestModule  implements android.view.View.OnClickListener{

    private static final String TAG = "LED";
    TextView mLedTest;
    private int mLedStatus = 0;
    private final int mLedColorRed = 0xFFFF0000;
    private final int mLedColorGreen = 0xFF00FF00;
    private final int mLedColorBlue = 0xFF0000FF;
    private final int mLedColorWhite = 0xFFFFFFFF;
    private final String mLedColorRedOn = "12";
    private final String mLedColorRedOff = "13";
    private final String mLedColorGreenOn = "14";
    private final String mLedColorGreenOff = "15";
    private final String mLedColorBlueOn = "16";
    private final String mLedColorBlueOff = "17";
    private boolean mExit;
    final byte[] LIGHT_ON = { '2', '5', '5' };
    final byte[] LIGHT_OFF = { '0' };
    private final int RED_ON = 0;
    private final int RED_OFF = 1;
    private final int GREEN_ON = 2;
    private final int GREEN_OFF = 3;
    private final int BLUE_ON = 4;
    private final int BLUE_OFF = 5;

    String RED_LED_DEV = "/sys/class/leds/red/brightness";
    String GREEN_LED_DEV = "/sys/class/leds/green/brightness";
    String BLUE_LED_DEV = "/sys/class/leds/blue/brightness";



    Button greenBtn=null;
    final int ID_LED=19871103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState, R.layout.three_color, R.drawable.lcd);



           mLedTest = ((TextView)findViewById(R.id.ledcolor));
           mLedTest.setOnClickListener(this);
           ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        runCommand("0");

    }

     Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mExit)
                return;
            switch (msg.what) {
            case RED_ON:
                runCommand(mLedColorRedOn);
                mLedTest.setTextColor(mLedColorRed);
                mLedTest.setText(getString(R.string.red));
                break;
            case RED_OFF:
                runCommand(mLedColorRedOff);
                Message green_on = new Message();
                green_on.what =GREEN_ON ;
                myHandler.sendMessageDelayed(green_on, 1000);
                break;
            case GREEN_ON:
                runCommand(mLedColorGreenOn);
                mLedTest.setTextColor(mLedColorGreen);
                mLedTest.setText(getString(R.string.green));
                break;
            case GREEN_OFF:
                runCommand(mLedColorGreenOff);
                Message blue_on = new Message();
                blue_on.what =BLUE_ON ;
                myHandler.sendMessageDelayed(blue_on, 1000);
                break;
            case BLUE_ON:
                runCommand(mLedColorBlueOn);
                mLedTest.setTextColor(mLedColorBlue);
                mLedTest.setText(getString(R.string.blue));
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        SystemProperties.set("persist.service.daemon.enable", "1");
    }
    @Override
    protected void onPause() {
        super.onPause();
        SystemProperties.set("persist.service.daemon.enable", "0");
        mExit = true;
        myHandler.removeMessages(RED_ON);
        myHandler.removeMessages(RED_OFF);
        myHandler.removeMessages(GREEN_ON);
        myHandler.removeMessages(GREEN_OFF);
        myHandler.removeMessages(BLUE_ON);
        myHandler.removeMessages(BLUE_OFF);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ledcolor:
            if(mLedStatus == 0) {
               Message msg = new Message();
               msg.what = RED_ON;
               myHandler.sendMessageDelayed(msg, 500);
            }else if(mLedStatus == 1) {
                Message red_off = new Message();
                red_off.what = RED_OFF;
                myHandler.sendMessageDelayed(red_off, 1000);
            }else if(mLedStatus == 2) {
                Message green_off = new Message();
                green_off.what =GREEN_OFF ;
                myHandler.sendMessageDelayed(green_off, 1000);
            }else if(mLedStatus == 3) {
                runCommand(mLedColorBlueOff);
                mLedTest.setTextColor(0xFF000000);
                mLedTest.setText(getString(R.string.multicolor_notification));
                ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
            }
                mLedStatus ++;
                break;
         }
    }

      @Override
        public void finish() {
            super.finish();
        }
    private void runCommand(String command){

        final List<String> cmds = new ArrayList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add("echo " + command + " > /data/citflag");
        Log.i("autoback test","command lcd:" + command);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                try{
                 // Process process2 = Runtime.getRuntime().exec("echo ariston.ini > /sys/bus/i2c/devices/0-0038/ftsscaptest");
                    ProcessBuilder pb1 = new ProcessBuilder(cmds);
                    pb1.start();
                }
                catch (IOException e ) {
                    e.printStackTrace ( ) ;
                }
            }
        }, 1);


    }

        void logd(Object d) {
            Log.d(TAG, "" + d);
        }

        void loge(Object e) {
            Log.e(TAG, "" + e);
        }
     @Override
    public String getModuleName() {
        return "ThreeColor";
    }

}
