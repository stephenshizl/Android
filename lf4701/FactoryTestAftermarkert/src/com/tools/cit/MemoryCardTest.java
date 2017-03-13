
package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.widget.Button;
import java.io.File;
import android.util.Log;
import android.os.SystemProperties;
import android.os.StatFs;
import java.text.DecimalFormat;

public class MemoryCardTest extends TestModule /* STEP 1 */{
    public static final String MEMORY_CARD = "Memory Card";
    public static final String SD_CARD_PATH="/mnt/external_sdcard";
    public static final int button_enable=1;
    public static final int button_disabe=0;
       
    private  StorageManager mStorageManager;

    MemoryCardReceiver memoryCardReceiver;
    IntentFilter intentFilter;
    TextView msgMemoryCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.memorycard, R.drawable.memory_card);

        msgMemoryCard = (TextView) findViewById(R.id.msg_memory_card);
     mStorageManager = StorageManager.from(this);
     ((Button) findViewById(R.id.btn_pass)).setEnabled(false);  
/*
        memoryCardReceiver = new MemoryCardReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
*/
        //startTest();

        testThread.start();
    }

    public void onDestroy(){
        super.onDestroy();
        SystemProperties.set("persist.sys.sdtest", "0");

    }

    @Override
    protected void onStart() {
        super.onStart();
     //  registerReceiver(memoryCardReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
       // unregisterReceiver(memoryCardReceiver);
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return MEMORY_CARD;
    }

    private void appendMsg(String text, int color) {
        msgMemoryCard.append(FTUtil.coloredString(text, color));
        msgMemoryCard.append("\n");
    }

    private void appendMsg(int resId, int color) {
        msgMemoryCard.append(FTUtil.coloredString(MemoryCardTest.this, resId, color));
        msgMemoryCard.append("\n");
    }

        private void appendMsg(int id) {
      if(id==button_enable){
         ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
        // setTestResult(PASS);
       }

    }
    private Thread testThread = new Thread(new Runnable() {
        @Override
        public void run() {
            startTest();
        }
    });

    private boolean startTest() {
        //runOnUiThread(new UIThread(getResources().getString(R.string.sd_test_start), Color.WHITE));
        FTUtil.waitforcheck();
        String status = Environment.getExternalStorageState();
       // String status = mStorageManager.getVolumeState(SD_CARD_PATH);
        DecimalFormat df = new DecimalFormat("###.00");
          FTLog.i(this, "wang0808 status" + status);
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            SystemProperties.set("persist.sys.sdtest", "1");
            
            runOnUiThread(new UIThread(getResources().getString(R.string.internal_sd)+
                getResources().getString(R.string.memory_size)+df.format(getTotalInternalMemorySize()/1024.0)+
                getResources().getString(R.string.unit), Color.GREEN));
            runOnUiThread(new UIThread(getResources().getString(R.string.internal_sd)+
                getResources().getString(R.string.memory_available)+df.format(getAvailableInternalMemorySize()/1024.0)+
                getResources().getString(R.string.unit), Color.GREEN));
/*
            runOnUiThread(new UIThread(getResources().getString(R.string.external_sd)+
                getResources().getString(R.string.memory_size)+getSDAllSize()+
                getResources().getString(R.string.unit), Color.GREEN));

            runOnUiThread(new UIThread(getResources().getString(R.string.external_sd)+
                getResources().getString(R.string.memory_available)+getSDFreeSize()+
                getResources().getString(R.string.unit), Color.GREEN));      
  */          
            if(getSDAllSize() > 0){
                
                runOnUiThread(new UIThread(getResources().getString(R.string.external_sd)+
                    df.format(getSDAllSize()/1024.0)+getResources().getString(R.string.unit), Color.GREEN));
                runOnUiThread(new UIThread(getResources().getString(R.string.external_sd)+
                    (df.format(getSDFreeSize()/1024.0)).toString()+
                     getResources().getString(R.string.available), Color.GREEN));
                //runOnUiThread(new UIThread(getResources().getString(R.string.sd_present_rw), Color.GREEN));
                //setTestResult(PASS);
                runOnUiThread(new UIThread2(button_enable));
            }else{
                runOnUiThread(new UIThread(getResources().getString(R.string.sd_unavaliable), Color.RED));
                //setTestResult(FAIL);
            }
            //runOnUiThread(new UIThread(getResources().getString(R.string.sd_test_complete), Color.WHITE));
            // auto-save
   //         setTestResult(PASS);
            return true;

        } else if (status.equals(Environment.MEDIA_SHARED)) {
            runOnUiThread(new UIThread(getResources().getString(R.string.sd_present_shared), Color.GREEN));
            runOnUiThread(new UIThread(getResources().getString(R.string.sd_test_complete), Color.WHITE));
         runOnUiThread(new UIThread2(button_enable));
            // auto-save
  //          setTestResult(PASS);
            return true;
        } else if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            runOnUiThread(new UIThread(getResources().getString(R.string.sd_read_only), Color.GREEN));
        } else {
            runOnUiThread(new UIThread(getResources().getString(R.string.sd_unavaliable), Color.RED));
            runOnUiThread(new UIThread("Memory card status is: " + status, Color.WHITE));
            runOnUiThread(new UIThread(getResources().getString(R.string.insert_sd_card), Color.WHITE));
        }
   //     setTestResult(FAIL);
        return false;
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
      public long getSDFreeSize(){  
       StatFs sf = new StatFs("/storage/sdcard1");  
          
             long blockSize = sf.getBlockSize();   
          
            long freeBlocks = sf.getAvailableBlocks();  
    
  
            return (freeBlocks * blockSize)/1024 /1024;  
      } 
    public static long getSDAllSize(){  
            StatFs sf = new StatFs("/storage/sdcard1");  
            long blockSize = sf.getBlockSize();   
            long allBlocks = sf.getBlockCount();  
            return (allBlocks * blockSize)/1024/1024; //MB 
     }   
    
    public static long getTotalInternalMemorySize() {       
        File path = Environment.getDataDirectory();        
        StatFs stat = new StatFs(path.getPath());        
        long blockSize = stat.getBlockSize();        
        long totalBlocks = stat.getBlockCount();       
        return totalBlocks * blockSize/1024/1024;   
        }
    
    /**     * @return     */   
    public static long getAvailableInternalMemorySize() {        
        
        File path = Environment.getDataDirectory();        
        StatFs stat = new StatFs(path.getPath());        
        long blockSize = stat.getBlockSize();       
        long availableBlocks = stat.getAvailableBlocks();        
        return availableBlocks * blockSize/1204/1204;   
    }
    private class MemoryCardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
                FTLog.i(this, "ACTION_MEDIA_CHECKING " + intent.getDataString());
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTABLE)) {

                FTLog.i(this, "ACTION_MEDIA_UNMOUNTABLE " + intent.getDataString());
         //       appendMsg(R.string.sd_unmountable, Color.RED);
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                FTLog.i(this, "ACTION_MEDIA_MOUNTED " + intent.getDataString());
                appendMsg(R.string.sd_mounted, Color.GREEN);
                startTest();

            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                FTLog.i(this, "ACTION_MEDIA_UNMOUNTED " + intent.getDataString());
      //          appendMsg(R.string.sd_unmounted, Color.WHITE);
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_NOFS)) {
                FTLog.i(this, "ACTION_MEDIA_NOFS " + intent.getDataString());
       //         appendMsg(R.string.sd_no_fs, Color.RED);
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
                FTLog.i(this, "ACTION_MEDIA_EJECT " + intent.getDataString());
       //         appendMsg(R.string.sd_eject, Color.YELLOW);
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
                FTLog.i(this, "ACTION_MEDIA_REMOVED " + intent.getDataString());
        //        appendMsg(R.string.sd_removed, Color.WHITE);
            }

        }

    }
}
