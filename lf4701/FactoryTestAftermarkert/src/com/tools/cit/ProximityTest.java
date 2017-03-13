
package com.tools.customercit;

import com.tools.util.FTLog;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProximityTest extends TestModule implements SensorEventListener {
    private static String PROXIMITY = "Proximity";
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private int count = 0;
    private TextView distance;
    private static final String ADC_PATH = "sys/class/capella_sensors/proximity/ps_adc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.proximity, R.drawable.proximity);
        distance = (TextView) findViewById(R.id.distance);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (mProximity == null) {
            distance.setText(R.string.na);
            FTLog.w(this, "Proximity sensor does NOT exist!");// mProximity.
            setTestResult(FAIL);// sensor wasn't detected
        }
        else {
            FTLog.i(this, "Found proximity sensor: " + mProximity.getName());
            distance.setText("7");
        }
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
        if (mProximity != null)
            mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mProximity != null)
            mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String adcTxt = readADC();
        int adcInt = Integer.valueOf(adcTxt);
        String helpExtra = (adcInt >= 50)?getString(R.string.proximity_cover):"";
        String helpTip = adcTxt + helpExtra;
        distance.setText(helpTip);
        count ++;
        /*if (count >= 5){
            setTestResult(PASS);
            findViewById(R.id.btn_pass).setEnabled(true);
            count = 0;
        }      */ 
        findViewById(R.id.btn_pass).setEnabled(true);
        FTLog.i(this, "accuracy: " + event.accuracy + ", value: " + event.values[0]);
    }

    @Override
    public String getModuleName() {
        return PROXIMITY;
    }

    private String readADC() {
        File file = new File(ADC_PATH);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str = null;
            String adc = "";
            while ((str = br.readLine()) != null) {
                if (str != null) {
                    adc += str;
                }
            }  
            br.close();
            int start = adc.indexOf("[");
            int end = adc.indexOf("]");
            adc = adc.substring(start + 1, end);
            return adc;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        
    }

}
