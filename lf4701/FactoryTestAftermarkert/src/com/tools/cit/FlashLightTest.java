
package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import java.io.File;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.content.pm.PackageManager;
import android.content.pm.FeatureInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.util.Log;
import android.view.View;

public class FlashLightTest extends TestModule /* STEP 1 */implements
        android.view.View.OnClickListener {

    public static final String FlashLight = "FlashLight";
    private Button btn,btn1;
    private TextView tv_notify;
    boolean flag = true;
    private Camera mCamera;

    private static String FLASH_OPEN_ACTION = "com.tools.AUTOCIT.FLASH_OPEN";
    private static String FLASH_CLOSE_ACTION = "com.tools.AUTOCIT.FLASH_CLOSE";
    private final BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                 String action = intent.getAction();
                 if(action.equals(FLASH_OPEN_ACTION)) {
                      openLedFlash();
                  }
                 if(action.equals(FLASH_CLOSE_ACTION)){
                    closeLedFlash();
                    setTestResult(PASS);
                 }
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FLASH_OPEN_ACTION);
        mIntentFilter.addAction(FLASH_CLOSE_ACTION);
        registerReceiver(mReceiver2, mIntentFilter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        FTLog.d(this, "onPause()");
/*
        if (!flag)
        {
            // 关闭
            TestThread closeThread=new TestThread("stop-flashlight");
            closeThread.start();
            btn.setText(R.string.flashlight_open);
            flag = !flag;
        }*/
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mCamera !=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mReceiver2 != null) {
             unregisterReceiver(mReceiver2);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.flashlight, R.drawable.flashlight);

        btn = (Button) findViewById(R.id.flashlight_open);
        btn1 = (Button)findViewById(R.id.flashlight_close);
        tv_notify = (TextView)findViewById(R.id.flashlight_notify);
        btn1.setOnClickListener(this);
        btn.setOnClickListener(this);
        btn1.setEnabled(false);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);

    }

    // STEP 3
    @Override
    public String getModuleName() {
        return FlashLight;
    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.flashlight_open) {
            FTLog.i(this, "Open button clicked");
                openLedFlash();
                btn.setEnabled(false);
                btn1.setEnabled(true);
                

        }else if(v.getId()==R.id.flashlight_close){
            closeLedFlash();
            btn1.setEnabled(false);
            btn.setEnabled(true);
            //setTestResult(PASS);
            tv_notify.setText(getString(R.string.flash_led_notification));
            ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
        }
        
    }
    public void closeLedFlash(){
        if ( mCamera != null )  
        {  
            Camera.Parameters parameters = mCamera.getParameters();               
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF); 
            mCamera.setParameters( parameters );
            mCamera.stopPreview();  
            mCamera.release();  
            mCamera = null;  
         }  
    }
    public void openLedFlash(){
        if ( null == mCamera ){  
            try{ 
                mCamera = Camera.open();
            }catch(Exception e){
                e.printStackTrace(); 
            }
        } 
       if (mCamera != null) {
            PackageManager pm= this.getPackageManager();
            FeatureInfo[]  features=pm.getSystemAvailableFeatures();
            for(FeatureInfo f : features){
                if(PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)){
                    Camera.Parameters parameters = mCamera.getParameters();               
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);    
                    mCamera.setParameters( parameters );              
                    mCamera.startPreview();
                }
            }
        } else {
            setTestResult(FAIL);
        }

    }
    /*
        class TestThread extends Thread implements Runnable{
        String command;

        public TestThread(String command){
            super();
            this.command=command;
        }

        @Override
        public void run() {
            File f=new File("/system/etc/tcmd/"+command);
            if(!f.exists()){
                FTLog.e(this, "/system/etc/tcmd/"+command+" doesn't exist");
                setTestResult(FAIL);
                return;
            }
            String receive=FTUtil.TCP_Send(command);
            if(("" != receive)&&(-1!=receive.indexOf("ret: 0", 0)))
                FTLog.i(this, "Execute "+command+" success.");
            else{
                FTLog.e(this, "Execute " + command + " error.");
                setTestResult(FAIL);
            }
        }
    }

    */
}
