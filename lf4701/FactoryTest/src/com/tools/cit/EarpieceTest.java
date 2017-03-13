
package com.tools.cit;

import java.io.IOException;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;

public class EarpieceTest extends TestModule /* STEP 1 */{
    public static final String EARPIECE = "Earpiece";
    HeadsetReceiver headsetReceiver = new HeadsetReceiver();
    //private SeekBar seekBar;
    private static final int LEFT_HEADSET = 1;
    private static final int RIGHT_HEADSET = 1;
    private static final int LEFT_HEADPHONE = 0;
    private static final int RIGHT_HEADPHONE = 0;
    private boolean leftEarphone = false;
    private boolean rightEarphone = false;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.earpiece, R.drawable.earpiece);
        IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_RIGHT_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);

    }


    // STEP 3
    @Override
    public String getModuleName() {
        return EARPIECE;
    }

 
    @Override
    protected void onDestroy() {
        unregisterReceiver(headsetReceiver);
        super.onDestroy();
    }

    private class HeadsetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {         
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) { //right earphone
            String FLAG_LEFTRIGHT ="";
            FLAG_LEFTRIGHT =  intent.getStringExtra("position");
            if(FLAG_LEFTRIGHT.equals("right")) { //right earphone
                FTLog.i(this, "ACTION_HEADSET_PLUG " +
                (intent.getIntExtra("state", -1) == 1 ? "plugged" : "unplugged") +
                ", name: " + intent.getStringExtra("name") +
                ", " + (intent.getIntExtra("microphone", -1) == 1 ? "with" : "without")
                 + " microphone");
                if (intent.getIntExtra("state", 0) == 1) {
                    rightEarphone = true;
                    switch(intent.getIntExtra("microphone", -1)) {
                        case RIGHT_HEADSET:
                             ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_right_mic));
                                        break;
                                    case RIGHT_HEADPHONE:
                                        ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_right_nomic));
                                        break;
                               } //end switch
                               if(leftEarphone && rightEarphone) {
                                       findViewById(R.id.btn_pass).setEnabled(true);
                                       findViewById(R.id.btn_pass).setFocusable(false);
                                       findViewById(R.id.btn_fail).setFocusable(false);
                               }
                        } else { 
                                ((TextView) findViewById(R.id.txt_headset_info))
                                    .setText(getString(R.string.ear_hs_unplugged)); 
                }
            } else if (FLAG_LEFTRIGHT.equals("left")){ // left earphone
                if (intent.getIntExtra("state", 0) == 1) {
                                leftEarphone = true;
                               switch(intent.getIntExtra("microphone", -1)) {
                                    case LEFT_HEADSET:
                                    ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_left_mic));
                                    break;
                                    case LEFT_HEADPHONE:
                                    ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_left_nomic));
                                    break;
                                } //end switch
                                if(leftEarphone && rightEarphone) {
                                    findViewById(R.id.btn_pass).setEnabled(true);
                                    findViewById(R.id.btn_pass).setFocusable(false);
                                    findViewById(R.id.btn_fail).setFocusable(false);
                               }
                   
                        } else {  
                    ((TextView) findViewById(R.id.txt_headset_info))
                                    .setText(getString(R.string.ear_hs_unplugged)); 
                }
                
                    }

            }

            }

        }
  
}
