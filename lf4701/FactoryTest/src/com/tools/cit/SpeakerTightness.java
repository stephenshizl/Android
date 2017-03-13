
package com.tools.cit;

import java.io.IOException;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.media.SoundPool;
import java.util.TimerTask;
import java.util.Timer;
import android.os.Message;
import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
//add FO FINIT 
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import android.os.SystemProperties;
public class SpeakerTightness extends TestModule {
    public static final String SPEAKER = "SpeakerTightness";
    private  TextView mtextlog;
    private String TAG = "Speaker";
    private String pink_com = "/system/bin/mm-audio-ftm -tc 2 -c /system/etc/ftm_test_config_mtp -d 8 -v 21 -file /system/audio_cit/PinkNoise.wav";
    private String commandtxt_path = "kill_audiolooper_command";
    private boolean FLAG[] = {false , false};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.speakertightness, R.drawable.speaker);
       ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        mtextlog=(TextView) findViewById(R.id.headset_test_log);
        TextView tv = (TextView)findViewById(R.id.fofint1);
        tv.setText(getString(R.string.speaker_title1));
        TextView tv2 = (TextView)findViewById(R.id.fofint2);
        tv2.setText(getString(R.string.speaker_title2));
        runCommand();
        new Handler().postDelayed(new Runnable(){  
            public void run() { 
                try{
                   readFofinit("/data/audio_test2", true);
                   readFofinit("/data/audio_test1", false);
                } catch (Exception e ) { 
                    e.printStackTrace ( ) ; 
                }
            }
        }, 16000);
    }
    private void readFofinit(String path, boolean flagvar) {
        final boolean flag = flagvar;
        FileReader fofinitReader;
        FileWriter fWriter = null;
        String fofinitString = "";
        char acBuffer[] = new char[30];
        try {
            fofinitReader = new FileReader(path);
            fofinitReader.read(acBuffer, 0, 30);
            for (int i = 0; i < acBuffer.length; i++) {
                fofinitString += acBuffer[i];
            }
            fofinitString = fofinitString.trim();
            fofinitReader.close();
        } catch (Exception e) {
            Log.e("top speakertest", "without access to the " + path);
            e.printStackTrace();
        }
        Log.i("top speakertest", "speakertest fofinit:" + fofinitString);
        if(!fofinitString.equals("")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(flag) {
                        TextView tv = (TextView)findViewById(R.id.fofint1);
                        tv.setText(getString(R.string.speaker_title1) + getString(R.string.speaker_fofintpass));
                        tv.setTextColor(Color.GREEN);
                        FLAG[0] = true;
                    } else {
                        TextView tv = (TextView)findViewById(R.id.fofint2);
                        tv.setText(getString(R.string.speaker_title2) + getString(R.string.speaker_fofintpass));
                        tv.setTextColor(Color.GREEN);
                        FLAG[1] = true;
                    }
                    if(FLAG[0] && FLAG[1]) {                    
                        ((Button)findViewById(R.id.btn_pass)).setEnabled(true);
                    }
                }
            });
         } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(flag){
                        TextView tv1 = (TextView)findViewById(R.id.fofint1);
                        tv1.setTextColor(Color.RED);
                        tv1.setText(getString(R.string.speaker_title1) + getString(R.string.speaker_fofintfail));
                        
                    } else {
                        TextView tv = (TextView)findViewById(R.id.fofint2);
                        tv.setText(getString(R.string.speaker_title2) + getString(R.string.speaker_fofintfail));
                        tv.setTextColor(Color.RED);
                    }
                    
                    ((Button)findViewById(R.id.btn_pass)).setEnabled(false);
                }
            });
         }
    }
    //close all audio loop
    private void closeAlllooper(){
     new Thread(new Runnable(){
        public  void run(){
            try{
                //Log.i("audiolooper", "closeall 1 looper command:" + close_command);
             // Process process2 = Runtime.getRuntime().exec("echo ariston.ini > /sys/bus/i2c/devices/0-0038/ftsscaptest");
                List<String> cmds = new ArrayList<String>();
                cmds.add("sh");
                cmds.add("-c");
                cmds.add(commandtxt_path);
                ProcessBuilder pb = new ProcessBuilder(cmds);
                pb.start();
                //Process process1 = Runtime.getRuntime().exec(close_command);
                //Process process2 = Runtime.getRuntime().exec("ls > /data/test/test.txt");
               
            }
            catch (IOException e ) { 
                e.printStackTrace ( ) ; 
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
     }).start(); 
    }
    private void runCommand(){              
        
        final List<String> pink_cmds = new ArrayList<String>();
        pink_cmds.add("sh");
        pink_cmds.add("-c");
        pink_cmds.add("echo 9 > /data/citflag");
        Log.i("command speaker", "command speaker:" + pink_cmds);
        new Handler().postDelayed(new Runnable(){  
            public void run() { 
                try{
                // Process process2 = Runtime.getRuntime().exec("echo ariston.ini > /sys/bus/i2c/devices/0-0038/ftsscaptest");
                    ProcessBuilder pb1 = new ProcessBuilder(pink_cmds);
                    pb1.start(); 
                } catch (IOException e ) { 
                    e.printStackTrace ( ) ; 
                }
            }
        }, 1); 

    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    // STEP 3
    @Override
    public String getModuleName() {
        return SPEAKER;
    }
    protected void onResume() {
        super.onResume();
        SystemProperties.set("persist.service.daemon.enable", "1");

    }
   @Override
    protected void onPause() {
        super.onPause();
        SystemProperties.set("persist.service.daemon.enable", "0");
    }
    @Override
    protected void onDestroy() {
        try {
            Process process1 = Runtime.getRuntime().exec("rm /data/audio_test1");
            Process process2 = Runtime.getRuntime().exec("rm /data/audio_test2");
        } catch (Exception e ) { 
            e.printStackTrace () ; 
        } 
        closeAlllooper();
        super.onDestroy();

    }

}
