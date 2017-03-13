
package com.tools.customercit;

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
import android.os.SystemProperties;

public class TopspeakerTest extends TestModule {
    public static final String SPEAKER = "Speaker";
    private  TextView mtextlog;
    private String command_speaker = "7";                                            
    private String commandtxt_path = "kill_audiolooper_command";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.speaker, R.drawable.speaker);
        mtextlog=(TextView) findViewById(R.id.headset_test_log);
        runCommand();
        
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
      final List<String> cmds = new ArrayList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add("echo " + command_speaker + " >  /data/citflag");
        Log.i("command speaker", "command speaker:" + command_speaker);
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
        ((Button)findViewById(R.id.btn_pass)).setEnabled(true);
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
    @Override
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
        closeAlllooper();
        super.onDestroy();

    }

}
