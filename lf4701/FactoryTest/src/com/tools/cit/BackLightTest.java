package com.tools.cit;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.PowerManager;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.util.Log;
import android.widget.LinearLayout;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.Button;
import android.widget.SeekBar;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

public class BackLightTest extends TestModule implements 
    OnClickListener, OnTouchListener, SeekBar.OnSeekBarChangeListener{
    public static final String BACKLIGHT = "BackLightTest";
    private PowerManager pm;
    private int i = 0;
    //private Context context;
    LinearLayout bgView;
    private int mOldBrightness;
    private Button btnPass;
    private int oldMode;
    private SeekBar mSeekBar;
    private static final int SEEK_BAR_RANGE = 10000;
    private int mScreenBrightnessMinimum;
    private int mScreenBrightnessMaximum; 
    private static String ACTION_BRIGHTNESS = "com.tools.AUTOCIT.BRIGHTNESS";
    private static String ACTION_BRIGHTNESS_20 = "com.tools.AUTOCIT.BRIGHTNESS_20";
    private static String ACTION_BRIGHTNESS_50 = "com.tools.AUTOCIT.BRIGHTNESS_50";
    private static String ACTION_BRIGHTNESS_80 = "com.tools.AUTOCIT.BRIGHTNESS_80";
    
    private final BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                 String action = intent.getAction();
                 String []actions=action.split("_");
                 int brightness=Integer.parseInt(actions[1]);
                 if(actions[0].equals(ACTION_BRIGHTNESS)) {
                          setPercentBrightness(brightness);
                          if(brightness>=80)
                            setTestResult(PASS);            
                  }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.backlight, R.drawable.example);
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        Log.i("BackLightTest","onCreate");
        /*
        bgView = (LinearLayout)findViewById(R.id.backlight);
        bgView.setOnTouchListener(this);
        */
        mSeekBar = (SeekBar)findViewById(R.id.seekbar);
        mSeekBar.setMax(SEEK_BAR_RANGE);
        mSeekBar.setProgress(0);  
        
    
        btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(false);
        
        
        try {
          oldMode = Settings.System.getInt(getBaseContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE);
          mOldBrightness = Settings.System.getInt(getBaseContext().getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);          
        } catch (SettingNotFoundException snfe) {
          Log.i("BackLightTest","get Brightness failed");
        } 

        mScreenBrightnessMinimum = pm.getMinimumScreenBrightnessSetting();
        mScreenBrightnessMaximum = pm.getMaximumScreenBrightnessSetting();
        
        mSeekBar.setOnSeekBarChangeListener(this);
        
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_BRIGHTNESS_20);
        mIntentFilter.addAction(ACTION_BRIGHTNESS_50);
        mIntentFilter.addAction(ACTION_BRIGHTNESS_80);
        registerReceiver(mReceiver2, mIntentFilter);
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return BACKLIGHT;
    }    

    public void onClick(View arg0) {
        
    }

    public void onResume(){
        super.onResume();
        pm.setBacklightBrightness(mScreenBrightnessMinimum);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver2);
    }
    private void changeBackLight() {
        switch(i){
            case 0:
                pm.setBacklightBrightness(pm.getMaximumScreenBrightnessSetting() / 5);
                break;
            case 1:
                pm.setBacklightBrightness(pm.getMaximumScreenBrightnessSetting() / 2);
                break;
            case 2:
                pm.setBacklightBrightness(pm.getMaximumScreenBrightnessSetting());
                //btnPass.setEnabled(true);
                break;
        }
        i++;
    }    

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("BackLightTest","onTouch");    
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(i<3)
                    changeBackLight();
                break;
            default:
                ;
        }
        return true;
    }    

  @Override
  protected void onPause() {
    pm.setBacklightBrightness(mOldBrightness);
    Settings.System.putInt(getContentResolver(),
            Settings.System.SCREEN_BRIGHTNESS_MODE, oldMode);     
    super.onPause();
  }

    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
            /*
        int range = (mScreenBrightnessMaximum - mScreenBrightnessMinimum);
        brightness = (brightness * range)/SEEK_BAR_RANGE + mScreenBrightnessMinimum;            
        pm.setBacklightBrightness(progress);
        */
        if(progress <= SEEK_BAR_RANGE/5 &&progress > SEEK_BAR_RANGE/10){
            mSeekBar.setProgress(SEEK_BAR_RANGE/5);
            setBrightness(SEEK_BAR_RANGE/5);
            
        } else if(progress > SEEK_BAR_RANGE/5 && progress <= SEEK_BAR_RANGE/2){
            mSeekBar.setProgress(SEEK_BAR_RANGE/2);
            setBrightness(SEEK_BAR_RANGE/2);        
        } else if(progress > SEEK_BAR_RANGE/2 && progress <= SEEK_BAR_RANGE*8/10){
            mSeekBar.setProgress(SEEK_BAR_RANGE * 8 / 10);
            setBrightness(SEEK_BAR_RANGE * 8 / 10);
            //btnPass.setEnabled(true);
           // setTestResult(PASS);
        }else if(progress > SEEK_BAR_RANGE *8/10){
            mSeekBar.setProgress(SEEK_BAR_RANGE);
            setBrightness(SEEK_BAR_RANGE);
            btnPass.setEnabled(true);
        }else{
            mSeekBar.setProgress(0);
            setBrightness(0);
        }
        
            
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // NA
    }  
    private void setPercentBrightness(int percentBrightness){
            int brightness=percentBrightness*SEEK_BAR_RANGE/100;
            mSeekBar.setProgress(brightness);
            setBrightness(brightness);
        
    }
    private void setBrightness(int brightness ) { 
        int range = (mScreenBrightnessMaximum - mScreenBrightnessMinimum);
        brightness = (brightness * range)/SEEK_BAR_RANGE + mScreenBrightnessMinimum;            
        pm.setBacklightBrightness(brightness);        
    }
}