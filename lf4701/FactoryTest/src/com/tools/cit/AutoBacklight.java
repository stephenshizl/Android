/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20101124  Sang Mingxin   Add to test the red and green led.
20101116  Sang Mingxin   Initial to auto test backlight.

===========================================================================*/
package com.tools.cit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import android.os.SystemProperties;
public class AutoBacklight extends TestModule {

    private Button falBtn, sucBtn;
    private int mOldBrightness;
    // private CITTest mLCDLight;
    private boolean mExit;
    private static final int CLOSE_BACK = 1;
    private static final int OPEN_KEYLIG = 2;
    private static final int MAXIMUM_BACKLIGHT = 255;
    private static final int MINIMUM_BACKLIGHT = 0;
    private static final String TAG = "AutoBacklight";
    private static boolean flag = true;
    private PowerManager pm;
    private int brightness = 150;
    private static final String BLIGHT_PATH = "/sys/class/leds/lcd-backlight/brightness";
    private static final String BRIGHTNESS_0 = "10";
    private static final String BRIGHTNESS_250 = "11";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState,R.layout.auto_backlight,R.drawable.light);
        falBtn = (Button) findViewById(R.id.btn_fail);
        sucBtn = (Button) findViewById(R.id.btn_pass);
      //  falBtn.setOnClickListener(this);
      //  sucBtn.setOnClickListener(this);
      //  sucBtn.setEnabled(false);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Message msg = new Message();
        msg.what = CLOSE_BACK;
        myHandler.sendMessageDelayed(msg, 2000);
        runCommand("0");

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
                    ProcessBuilder pb1 = new ProcessBuilder(cmds);
                    pb1.start(); 
                }
                catch (IOException e ) { 
                    e.printStackTrace ( ) ; 
                }
            }
        }, 1);    
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (KeyEvent.KEYCODE_MENU == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mExit)
                return;
            switch (msg.what) {
            case CLOSE_BACK:
                runCommand(BRIGHTNESS_0);
                mOldBrightness = brightness;
                Message ope = new Message();
                ope.what = OPEN_KEYLIG;
                myHandler.sendMessageDelayed(ope, 3000);
                break;
            case OPEN_KEYLIG:
                sucBtn.setEnabled(true);
                runCommand(BRIGHTNESS_250);
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
        mExit = false;
        SystemProperties.set("persist.service.daemon.enable", "1");
    }

    @Override
    protected void onPause() {
        mExit = true;
        myHandler.removeMessages(CLOSE_BACK);
        myHandler.removeMessages(OPEN_KEYLIG);
        pm.setBacklightBrightness(mOldBrightness);

        SystemProperties.set("persist.service.daemon.enable", "0");
        super.onPause();
    }

     @Override
    public String getModuleName() {
        return "Backlight";
    }
}
