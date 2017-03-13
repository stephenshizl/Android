
package com.tools.cit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;
//add run command
import java.util.List;
import android.os.Handler;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
public class KeyTest extends TestModule {
    public static final String name = "Key";
    boolean flag = true;
    public  int POWER_KEY=1;
    public int VOLUME_DOWN_ACTIONDOWN=0;
    public int VOLUME_DOWN_ACTIONUP=0;
    public int VOLUME_UP_ACTIONDOWN =0;
    public int VOLUME_UP_ACTIONUP=0;
    public int M_BUTTON_ACTIONDOWN =0;
    public int M_BUTTON_ACTIONUP=0;
    //add by qianyan headsethook,vlume button,scrollpush
    public int KEY_HEADSETHOOK=0;
    public int KEY_HEADSETHOOK_AC1=0;
    public int KEY_HEADSETHOOK_AC2=0;
    public int KEY_VOLUMEDOWN_AC1 = 0;
    public int KEY_VOLUMEUP_AC1 = 0;
    public int KEY_VOLUMEDOWN_AC2 = 0;
    public int KEY_VOLUMEUP_AC2 = 0;
    public int KEYCODE_SCROLL_PUSH = 0;
    public int KEYCODE_VOLUME_DOWN = 0;
    public int KEYCODE_VOLUME_UP = 0;
    public int KEYCODE_M_BUTTON = 0;
    public boolean count[] = {false, false, false, false, false, false, false, false, false, false}; 
    private Button passButton;
    private boolean isAll = true;
    //end by qianyan
    
    private final String AC1_PATH = "/sys/class/arizona_extcon_r/headset_key1";
    private final String AC2_PATH = "/sys/bus/platform/devices/arizona-extcon/headset_key2";
    private final String SCROLL_PATH = "/sys/class/scroll_wheel/scroll_occurs";
    private final String AC1_COMMAND = "keyac_command";
    private String TAG = "keytest";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.key, R.drawable.key);
        passButton = (Button) findViewById(R.id.btn_pass);       
    }

    @Override
    protected void onResume(){
        super.onResume();
        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        ((TextView)findViewById(R.id.key_power)).setVisibility(View.GONE);
        //setACFlag(AC1_PATH);
        //setACFlag(AC2_PATH);
        //setACFlag(SCROLL_PATH);
        SystemProperties.set("persist.sys.citkey", "true");
    }

    @Override
    protected void onPause() {
        SystemProperties.set("persist.sys.citkey", "false");
        super.onPause();
    }

    @Override
    public String getModuleName() {
        return name;
    }
    private int getACFlag(String path, boolean ac1) {
        String acPath = path;
        /*if(ac1) {
            new Thread(new Runnable(){
                public  void run(){
                    try{
                        Log.i(TAG, "start getacflag");
                        Process process = Runtime.getRuntime().exec(AC1_COMMAND);
                        InputStreamReader ir = new InputStreamReader(process.getInputStream());  
                        LineNumberReader input = new LineNumberReader(ir);  
                        String line;  
                        List<String> strList = new ArrayList(); 
                        try {
                            int result = process.waitFor(); 
                            Log.i(TAG, "result:" + result);
                        } catch(InterruptedException e){
                            System.err.println("processes was interrupted");
                        }
                        while ((line = input.readLine()) != null){  
                            strList.add(line);  
                        }  
                        Log.i(TAG, "exit getACFlag" +strList);
                    } catch (IOException e ) { 
                    Log.e(TAG, "exit getACFlag" + e);
                    e.printStackTrace ( ) ; 
                    }

                }
            }).start();
           acPath = "/data/cit_keyac1.txt"; 
        }

        new Thread(new Runnable(){   
            public void run(){   
                try {   
                        Thread.sleep(3000);
                    } catch (InterruptedException e) { 
                        Log.i(TAG, "getACFlag thread sleep InterruptedException");
                    }

            }   
        }).start();*/

        FileReader ACReader;
        FileWriter fWriter = null;
        String ACString = "";
        int AcFlag=0;
        char acBuffer[] = new char[30];
        try {
            ACReader = new FileReader(acPath);
            ACReader.read(acBuffer, 0, 30);
            for (int i = 0; i < acBuffer.length; i++) {
                ACString += acBuffer[i];
            }
            ACString=ACString.trim();
            ACReader.close();
        } catch (Exception e) {
            Log.e("KeyTest", "without access to the health" + acPath);
            e.printStackTrace();
        }
        if(!ACString.equals("")){
            AcFlag = Integer.parseInt(ACString);
        }

        return AcFlag;
    }
    private void setACFlag(String path) {
        Log.i("keytest", "setacflag start");
        String acPath = path;
        final List<String> cmds = new ArrayList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add("echo 0 > " + path);
        new Handler().postDelayed(new Runnable(){  
        public void run() { 
            try{
             // Process process2 = Runtime.getRuntime().exec("echo ariston.ini > /sys/bus/i2c/devices/0-0038/ftsscaptest");
                ProcessBuilder pb1 = new ProcessBuilder(cmds);
                pb1.start(); 
            }
            catch (Exception e ) { 
                Log.e("KeyTest", "seACFlag handler run error" + cmds);
                e.printStackTrace ( ) ; 
            }
        }
        }, 1);    

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        TextView v = null;
        TextView v1 = null;
        Log.i("keytest dispatchKeyEvent keyevent", "keytest onkeydown1 keyevent" + event.getKeyCode());
        //int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                 //earphone volume down button by qianyan
                 /*KEYCODE_VOLUME_DOWN++;
                 if(KEYCODE_VOLUME_DOWN == 2){
                    v = (TextView)findViewById(R.id.key_vol_down);
                 }*/
                Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent KEYCODE_VOLUME_DOWN");
                if(getACFlag(AC2_PATH, false)== 55 ){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent ac2 volumdown" + getACFlag(AC2_PATH, false) + KEY_VOLUMEDOWN_AC2);
                    KEY_VOLUMEDOWN_AC2++;
                   // setACFlag(AC2_PATH);
                } else if(getACFlag(AC1_PATH, true)== 55){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent ac1 volumdown" + getACFlag(AC1_PATH, true) + KEY_VOLUMEDOWN_AC1);
                    KEY_VOLUMEDOWN_AC1++;
                    //setACFlag(AC1_PATH);
                } else if(getACFlag(SCROLL_PATH, true)== 55){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent volumdown");
                    KEYCODE_VOLUME_DOWN++; 
                    //setACFlag(SCROLL_PATH);
                }
                if(KEY_VOLUMEDOWN_AC2 == 1){
                   v = (TextView)findViewById(R.id.ac2_volume_down);
                   KEY_VOLUMEDOWN_AC2 = 0;
                   count[0] = true;
                } else if(KEY_VOLUMEDOWN_AC1 == 1){
                   v = (TextView)findViewById(R.id.ac1_volume_down);  
                   KEY_VOLUMEDOWN_AC1 = 0;
                   count[1] = true;
                } else if(KEYCODE_VOLUME_DOWN == 1){
                   v = (TextView)findViewById(R.id.key_vol_down); 
                   KEYCODE_VOLUME_DOWN = 0;
                   //count[2] = true;
                   count[2] = true;
                }
                 //end by qianyan 
            break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                //earphone volume down button by qianyan
                /*KEYCODE_VOLUME_UP++;
                if(KEYCODE_VOLUME_UP == 2){
                    v = (TextView)findViewById(R.id.key_vol_up);
                }*/
                Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent KEYCODE_VOLUME_up");
                if(getACFlag(AC2_PATH, false)== 55 ){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent ac1 volumup" + getACFlag(AC2_PATH, false));
                    KEY_VOLUMEUP_AC2++;
                   // setACFlag(AC2_PATH);
                } else if(getACFlag(AC1_PATH, true)== 55){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent ac1 volumup" + getACFlag(AC1_PATH, true));
                    KEY_VOLUMEUP_AC1++;
                    //setACFlag(AC1_PATH);
                } else if(getACFlag(SCROLL_PATH, true) == 55){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent volumup");
                    KEYCODE_VOLUME_UP++; 
                    //setACFlag(SCROLL_PATH);
                }
                if(KEY_VOLUMEUP_AC2 == 1){
                   v = (TextView)findViewById(R.id.ac2_volume_up);
                   KEY_VOLUMEUP_AC2 = 0;
                   count[3] = true;
                } else if(KEY_VOLUMEUP_AC1 == 1){
                   v = (TextView)findViewById(R.id.ac1_volume_up); 
                   KEY_VOLUMEUP_AC1 = 0;
                   count[4] = true;
                } else if(KEYCODE_VOLUME_UP == 1){
                   v = (TextView)findViewById(R.id.key_vol_up); 
                   KEYCODE_VOLUME_UP = 0;
                   count[5] = true;
                }
                 //end by qianyan 
            break;
            case KeyEvent.KEYCODE_M_BUTTON:
                Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent KEYCODE_M_BUTTON");
                KEYCODE_M_BUTTON++;
                if(KEYCODE_M_BUTTON == 1){
                    v = (TextView)findViewById(R.id.key_m_button);
                    KEYCODE_M_BUTTON = 0;
                    count[6] = true;
                }
            break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if(getACFlag(AC2_PATH, false)== 55){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent ac2 hook" + getACFlag(AC2_PATH, false));
                    KEY_HEADSETHOOK_AC2++;
                    //setACFlag(AC2_PATH);
                }
                if(getACFlag(AC1_PATH ,true)== 55){
                    Log.i("keytest dispatchKeyEvent","key dispatchKeyEvent ac1 hook" + getACFlag(AC1_PATH, true));
                    KEY_HEADSETHOOK_AC1++;
                    //setACFlag(AC1_PATH);
                }
                if(KEY_HEADSETHOOK_AC2 == 1){
                   v = (TextView)findViewById(R.id.key_headset_hook);
                   KEY_HEADSETHOOK_AC2 = 0;
                    count[7] = true;
                } else if(KEY_HEADSETHOOK_AC1 == 1){
                    v = (TextView)findViewById(R.id.key_headset_hook1);   
                    KEY_HEADSETHOOK_AC1 = 0;
                    count[8] = true;
                }
             
            break;            
            case KeyEvent.KEYCODE_SCROLL_PUSH:
                KEYCODE_SCROLL_PUSH++;
                if(KEYCODE_SCROLL_PUSH == 1){
                    v = (TextView)findViewById(R.id.key_vol_button);
                    KEYCODE_SCROLL_PUSH = 0;
                    count[9] = true;

             }
             break;
        }
        Log.i("keytest", "key invisble" + v1 + " " + v);
         isAll = true;
        for(int i = 0; i < count.length - 1; i++) {
            Log.i("key", "isAll" + i + count[i]);
            if(!count[i]) {
                isAll = false;
                break;
             }
        }
        // passButton.setEnabled(true);
        /*if(isAll){
            Log.i("key", "pass set true");
            Log.i("keytest", "key invisble v is all" + v1 + " " + v);
            passButton.setEnabled(true);
            
             
        }*/
        /*
        if(v1 !=null){
            v1.setVisibility(View.INVISIBLE);
             Log.i("keytest", "key invisble v1" + v1 + " " + v);
        }*/
        if (v !=  null) {
            v.setVisibility(View.INVISIBLE);
             Log.i("keytest", "key invisble v " + v1 + " " + v);
        }
        if(isAll){
            Log.i("key", "pass set true");
            Log.i("keytest", "key invisble v is all" + v1 + " " + v);
            passButton.setEnabled(true);    
             
        }
        return true;
    }
    
}
