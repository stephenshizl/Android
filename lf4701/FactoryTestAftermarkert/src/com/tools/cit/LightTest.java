
package com.tools.customercit;

import com.tools.util.FTLog;
import android.util.Log;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.WindowManager;
public class LightTest extends TestModule implements SensorEventListener {
    private static String LIGHT = "Light";
    private SensorManager mSensorManager;
    private Sensor mLightSensor;

    private TextView light;
    private TextView action;
    private int LightChangeCount=0;
    private float lightnumber=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.light, R.drawable.light);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        light = (TextView) findViewById(R.id.light_level);
        action=(TextView)findViewById(R.id.light_action);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLightSensor == null) {
            light.setText(R.string.na);
            action.setText(R.string.na);
            FTLog.w(this, "Light sensor does NOT exist!");// mProximity.
            setTestResult(FAIL);// sensor wasn't detected            
        }
        else
            FTLog.i(this, "Found light sensor: " + mLightSensor.getName());
       
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLightSensor != null)
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mLightSensor != null)
            mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        light.setText(event.values[0] + " lux");
        FTLog.i(this, "accuracy: " + event.accuracy + ", value: " + event.values[0]);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
         if(event.values[0]>=300)
        {
            action.setTextColor(Color.GREEN);
            action.setText(R.string.light_strong);           
        }
        else {
            action.setTextColor(Color.RED);
            action.setText(R.string.light_weak);                  
        }
    }

    @Override
    public String getModuleName() {
        return LIGHT;
    }

}
