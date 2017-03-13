/**TEST  LED*/
package com.tools.customercit;

import android.util.Log;
import  android.widget.Button;
import  android.hardware.Camera;
import  android.hardware.Camera.Parameters;
import  android.widget.TextView;
import android.view.KeyEvent;
import  android.widget.ToggleButton;
import android.view.View.OnClickListener;
import android.view.View;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.FeatureInfo;
import android.view.WindowManager;
public class AutoLED extends TestModule implements OnClickListener{
    
private Button sucBtn,falBtn;
private Button btn1,btn2;
private Camera mCamera;
private TextView tv1;

private static final String TAG = "AutoLED";

    public  void  initView()
    {
         //sucBtn = (Button)findViewById(R.id.led_success);
        //  falBtn = (Button)findViewById(R.id.led_fail);
          btn1 = (Button)this.findViewById(R.id.Button1);
         btn2 = (Button)this.findViewById(R.id.Button2);
          //sucBtn.setOnClickListener(this);
         // falBtn.setOnClickListener(this);
         btn1.setOnClickListener(this);
         btn2.setOnClickListener(this);
         tv1 = (TextView)findViewById(R.id.led);
         btn2.setEnabled(false);
         btn1.setEnabled(true);
         //sucBtn.setEnabled(false);
    }

@Override
    protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
     super.onCreate(savedInstanceState);
     super.onCreate(savedInstanceState, R.layout.auto_led, R.drawable.light);
    // setContentView(R.layout.auto_led);
     initView();
    }

@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (KeyEvent.KEYCODE_MENU == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
} 

  @Override
  public void onResume() {
    super.onResume();
  }
  
  @Override
  protected void onPause() {
    if ( mCamera != null )  
       { 
      mCamera.release();  
      mCamera = null;  
    }
    super.onPause();
  }
  
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id)
    {      
        case R.id.Button1:
                   if ( null == mCamera )  
                    {  
                     mCamera = Camera.open();      
                    }  
                    PackageManager pm= this.getPackageManager();
                    FeatureInfo[]  features=pm.getSystemAvailableFeatures();
                    
                    for(FeatureInfo f : features)
                    {
                          if(PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))
                         {
                                Camera.Parameters parameters = mCamera.getParameters();               
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);    
                                mCamera.setParameters( parameters );              
                                mCamera.startPreview();
                          }
                    }
                    btn2.setEnabled(true);
                    btn1.setEnabled(false);
                    break;

        case R.id.Button2:
                    if ( mCamera != null )  
                       {  
                             Camera.Parameters parameters = mCamera.getParameters();               
                                parameters.setFlashMode(Parameters.FLASH_MODE_OFF); 
                                mCamera.setParameters( parameters );
                        mCamera.stopPreview();  
                         mCamera.release();  
                         mCamera = null;  
                    }    
                    btn2.setEnabled(false);
                    btn1.setEnabled(true);
                    //sucBtn.setEnabled(true);
                    findViewById(R.id.btn_pass).setEnabled(true);
                    break;
        
    //case R.id.led_success:
//saveTestResult(PASS); 
//backToTest(1) ;//openActivity(TEST_LED+1);
      //finish();
      //break;
    //case R.id.led_fail:
//saveTestResult(FAIL);
//backToTest(0) ; //openFailActivity(TEST_LED);
      //finish();
      //break;
    //default:
     // Log.e(TAG,"Error!");
     // break;
    }
  }

   @Override
    public String getModuleName() {
        return "LED";
    }
}

