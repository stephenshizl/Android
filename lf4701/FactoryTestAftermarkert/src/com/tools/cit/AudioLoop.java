package com.tools.customercit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.content.IntentFilter;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.SystemProperties;
import android.os.Handler;
public class AudioLoop extends TestModule {
    private String TAG = "Audio loop";
    private static float SOUND_EFFECTS_ENABLED = 0;
    private Button ac1_earphone;
    private Button ac2_earphone;
    private Button topmic_ac1;
    private Button topmic_ac2;
    private Button bottommic_ac1;
    private Button bottommic_ac2;
    private Button topmic_bottomspeaker;
    private Button bottommic_topspeaker;

    private TextView txt_ac1_earphone;
    private TextView txt_ac2_earphone;
    private TextView txt_topmic_ac1;
    private TextView txt_topmic_ac2;
    private TextView txt_bottommic_ac1;
    private TextView txt_bottommic_ac2;
    private TextView txt_topmic_bottomspeaker;
    private TextView txt_bottommic_topspeaker;
    
    private String command_ac1_earphone = "1";
    private String command_ac2_earphone = "2";
    private String command_topmic_ac1 = "3";
    private String command_topmic_ac2 = "4";
    private String command_bottommic_ac1 = "5";
    private String command_bottommic_ac2 = "6";
    private String command_topmic_bottomspeaker = "";
    private String command_bottommic_topspeaker = "";
    private String commandtxt_path = "kill_audiolooper_command";
    
    
    private boolean flag[] = {false, false, false, false, false, false};
    private boolean passFlag = true;
    HeadsetReceiver headsetReceiver = new HeadsetReceiver();
    private static final int LEFT_HEADSET = 1;
    private static final int RIGHT_HEADSET = 1;
    private static final int LEFT_HEADPHONE = 0;
    private static final int RIGHT_HEADPHONE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.audio_loop, R.drawable.mic);
        initData();
        ac1_earphone.setOnClickListener(clickEvent);
        ac2_earphone.setOnClickListener(clickEvent);
        topmic_ac1.setOnClickListener(clickEvent);
        topmic_ac2.setOnClickListener(clickEvent);
        bottommic_ac1.setOnClickListener(clickEvent);
        bottommic_ac2.setOnClickListener(clickEvent);
        topmic_bottomspeaker.setOnClickListener(clickEvent);
        bottommic_topspeaker.setOnClickListener(clickEvent);
        IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_RIGHT_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
 
    private void initData() {
      
        ac1_earphone = (Button) findViewById(R.id.ac1);
        ac2_earphone = (Button) findViewById(R.id.ac2);
        topmic_ac1 = (Button) findViewById(R.id.topmicac1);
        topmic_ac2 = (Button) findViewById(R.id.topmicac2);
        bottommic_ac1 = (Button) findViewById(R.id.bottommicac1);
        bottommic_ac2 = (Button) findViewById(R.id.bottommicac2);
        topmic_bottomspeaker = (Button) findViewById(R.id.topmicbottomspeaker);
        bottommic_topspeaker = (Button) findViewById(R.id.bottommictopspeaker);

        txt_ac1_earphone = (TextView) findViewById(R.id.txt_ac1);
        txt_ac2_earphone = (TextView) findViewById(R.id.txt_ac2);
        txt_topmic_ac1 = (TextView) findViewById(R.id.txt_topmicac1);
        txt_topmic_ac2 = (TextView) findViewById(R.id.txt_topmicac2);
        txt_bottommic_ac1 = (TextView) findViewById(R.id.txt_bottommicac1);
        txt_bottommic_ac2 = (TextView) findViewById(R.id.txt_bottommicac2);
        txt_topmic_bottomspeaker = (TextView) findViewById(R.id.txt_topmicbottomspeaker);
        txt_bottommic_topspeaker = (TextView) findViewById(R.id.txt_bottommictopspeaker);
        unenableAllear();
    }
    
     private void unenableAllear() {
    ac1_earphone.setEnabled(false);
    topmic_ac1.setEnabled(false);
    bottommic_ac1.setEnabled(false);
    ac2_earphone.setEnabled(false);
    topmic_ac2.setEnabled(false);
    bottommic_ac2.setEnabled(false);

    }
    private void unenableLeftear() {
    ac1_earphone.setEnabled(false);
    topmic_ac1.setEnabled(false);
    bottommic_ac1.setEnabled(false);

    }
    private void unenableRightear() {
    ac2_earphone.setEnabled(false);
    topmic_ac2.setEnabled(false);
    bottommic_ac2.setEnabled(false);

    }
   private void enableLeftear() {
    ac1_earphone.setEnabled(true);
    topmic_ac1.setEnabled(true);
    bottommic_ac1.setEnabled(true);

   }
   private void enableRightear() {
    ac2_earphone.setEnabled(true);
    topmic_ac2.setEnabled(true);
    bottommic_ac2.setEnabled(true);

   }
    OnClickListener clickEvent = new OnClickListener() {        
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ac1:
                    Intent intent1 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent1.putExtra("which", 0x11);
                    intent1.putExtra("command", command_ac1_earphone);
                    startActivityForResult(intent1, 0x11);
                    break;
                case R.id.ac2:
                    Intent intent2 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent2.putExtra("which", 0x12);
                    intent2.putExtra("command", command_ac2_earphone);
                    startActivityForResult(intent2, 0x12);
                    break;
                case R.id.topmicac1:
                    Intent intent3 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent3.putExtra("which", 0x13);
                    intent3.putExtra("command", command_topmic_ac1);
                    startActivityForResult(intent3, 0x13);
                    break;
                case R.id.topmicac2:
                    Intent intent4 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent4.putExtra("which", 0x14);
                    intent4.putExtra("command", command_topmic_ac2);
                    startActivityForResult(intent4, 0x14);
                    break;
                case R.id.bottommicac1:
                    Intent intent5 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent5.putExtra("which", 0x15);
                    intent5.putExtra("command", command_bottommic_ac1);
                    startActivityForResult(intent5, 0x15);
                    break;
                case R.id.bottommicac2:
                    Intent intent6 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent6.putExtra("which", 0x16);
                    intent6.putExtra("command", command_bottommic_ac2);
                    startActivityForResult(intent6, 0x16);
                    break;
                case R.id.topmicbottomspeaker:
                    Intent intent7 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent7.putExtra("which", 0x17);
                    intent7.putExtra("command", command_topmic_bottomspeaker);
                    startActivityForResult(intent7, 0x17);
                    break;
                case R.id.bottommictopspeaker:
                    Intent intent8 = new Intent(AudioLoop.this, EveryMicTest.class);
                    intent8.putExtra("which", 0x18);
                    intent8.putExtra("command", command_bottommic_topspeaker);
                    startActivityForResult(intent8, 0x18);
                    break;
                default:
                    break;
            }
            
        }
    };
    @Override
    public String getModuleName() {
        return "Mic";
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0x11:             
                String resultString1 = data.getStringExtra("result");
                if(resultString1.equals("1")) {
                    txt_ac1_earphone.setText(getString(R.string.audiook));
                    txt_ac1_earphone.setTextColor(Color.GREEN);
                    flag[0] = true;
                    passAll();
                } else {
                    txt_ac1_earphone.setText(getString(R.string.audiono));
                    txt_ac1_earphone.setTextColor(Color.RED);
                    flag[0] = false;
                }
                break;
            case 0x12:
                String resultString2 = data.getStringExtra("result");
                if(resultString2.equals("1")) {
                    txt_ac2_earphone.setText(getString(R.string.audiook));
                    txt_ac2_earphone.setTextColor(Color.GREEN);
                    flag[1] = true;
                    passAll();
                } else {
                    txt_ac2_earphone.setText(getString(R.string.audiono));
                    txt_ac2_earphone.setTextColor(Color.RED);
                    flag[1] = false;
                }
                break;
            case 0x13:
                String resultString3 = data.getStringExtra("result");
                if(resultString3.equals("1")) {
                    txt_topmic_ac1.setText(getString(R.string.audiook));
                    txt_topmic_ac1.setTextColor(Color.GREEN);
                    flag[2] = true;
                    passAll();
                } else {
                    txt_topmic_ac1.setText(getString(R.string.audiono));
                    txt_topmic_ac1.setTextColor(Color.RED);
                    flag[2] = false;
                }
                break;
            case 0x14:
                String resultString4 = data.getStringExtra("result");
                if(resultString4.equals("1")) {
                    txt_topmic_ac2.setText(getString(R.string.audiook));
                    txt_topmic_ac2.setTextColor(Color.GREEN);
                    flag[3] = true;
                    passAll();
                } else {
                    txt_topmic_ac2.setText(getString(R.string.audiono));
                    txt_topmic_ac2.setTextColor(Color.RED);
                    flag[3] = false;
                }
                break;
            case 0x15:
                String resultString5 = data.getStringExtra("result");
                if(resultString5.equals("1")) {
                    txt_bottommic_ac1.setText(getString(R.string.audiook));
                    txt_bottommic_ac1.setTextColor(Color.GREEN);
                    flag[4] = true;
                    passAll();
                } else {
                    txt_bottommic_ac1.setText(getString(R.string.audiono));
                    txt_bottommic_ac1.setTextColor(Color.RED);
                    flag[4] = false;
                }
                break;
            case 0x16:
                String resultString6 = data.getStringExtra("result");
                if(resultString6.equals("1")) {
                    txt_bottommic_ac2.setText(getString(R.string.audiook));
                    txt_bottommic_ac2.setTextColor(Color.GREEN);
                    flag[5] = true;
                    passAll();
                } else {
                    txt_bottommic_ac2.setText(getString(R.string.audiono));
                    txt_bottommic_ac2.setTextColor(Color.RED);
                    flag[5] = false;
                }
                break;
                /*
            case 0x17:
                String resultString7 = data.getStringExtra("result");
                if(resultString7.equals("1")) {
                    txt_topmic_bottomspeaker.setText(getString(R.string.audiook));
                    txt_topmic_bottomspeaker.setTextColor(Color.GREEN);
                    flag[6] = true;
                    passAll();
                } else {
                    txt_topmic_bottomspeaker.setText(getString(R.string.audiono));
                    txt_topmic_bottomspeaker.setTextColor(Color.RED);
                    flag[6] = false;
                }
                break;
            case 0x18:
                String resultString8 = data.getStringExtra("result");
                if(resultString8.equals("1")) {
                    txt_bottommic_topspeaker.setText(getString(R.string.audiook));
                    txt_bottommic_topspeaker.setTextColor(Color.GREEN);
                    flag[7] = true;
                    passAll();
                } else {
                    txt_bottommic_topspeaker.setText(getString(R.string.audiono));
                    txt_bottommic_topspeaker.setTextColor(Color.RED);
                    flag[7] = false;
                }
                break;*/

            default:
                break;
        }
    }
    
    private void passAll() {  

        for(int i = 0; i < flag.length; i++) {
            if(!flag[i]) {
                passFlag = false;
                break;
            }
        }
        if(passFlag) {
            findViewById(R.id.btn_pass).setEnabled(true);
        }
        passFlag = true;
    }
    @Override
    protected void onDestroy() {
        closeAlllooper();
     unregisterReceiver(headsetReceiver);
        super.onDestroy();
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
    private class HeadsetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // FTLog.i(this, "On Receive");
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) { 
            String FLAG_LEFTRIGHT ="";
            FLAG_LEFTRIGHT =  intent.getStringExtra("position");
            if(FLAG_LEFTRIGHT.equals("right")) { //right earphone
                        Log.i("AudioLoop", "ACTION_HEADSET_PLUG " +(intent.getIntExtra("state", -1) == 1 ? "plugged" : "unplugged") +
                            ", name: " + intent.getStringExtra("name") +
                            ", " + (intent.getIntExtra("microphone", -1) == 1 ? "with" : "without")
                            + " microphone");
                        if (intent.getIntExtra("state", 0) == 1) {
                               switch(intent.getIntExtra("microphone", -1)) {
                                    case RIGHT_HEADSET:
                                       enableRightear();
                                        break;
                                    case RIGHT_HEADPHONE:
                                       unenableRightear();
                                        break;
                               } //end switch
                      
                        } else {
                                unenableRightear();
                        }
               
                    } else if (FLAG_LEFTRIGHT.equals("left")){ // left earphon
                        if (intent.getIntExtra("state", 0) == 1) {
                               switch(intent.getIntExtra("microphone", -1)) {
                                    case LEFT_HEADSET:
                                    enableLeftear();
                                        break;
                                    case LEFT_HEADPHONE:
                                        unenableLeftear();
                                        break;
                                } //end switch
                   
                        } else {  
                            unenableLeftear();
                        }
                
                    }
            
        }
    }

    }
}
