/*
 * Copyright (c) 2011-2012, Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 * Developed by QRD Engineering team.
 */

package com.qti.factory.LightSensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qti.factory.R;
import com.qti.factory.Utils;

public class LightSensor_BYD extends Activity {

    private static final String TAG = "LightSensor";
    private SensorManager LightSensorManager = null;
    private Sensor mLightSensor = null;
    private LightSensorListener mLightSensorListener;
    TextView mTextView;
    Button btntrue, btnfail;
    private final static String INIT_VALUE = "";
    private static String value = INIT_VALUE;
    private static String pre_value = INIT_VALUE;
    private final int MIN_COUNT = 4;

    private final static int SENSOR_TYPE = Sensor.TYPE_LIGHT;
    private final static int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

    @Override
    public void finish() {
        try {
            LightSensorManager.unregisterListener(mLightSensorListener,
                    mLightSensor);
        } catch (Exception e) {
        }
        super.finish();
    }

    void bindView() {
        mTextView = (TextView) findViewById(R.id.dac);
        btntrue = (Button) findViewById(R.id.btntrue);
        btntrue.setEnabled(false);
        btntrue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pass();
            }
        });
        btnfail = (Button) findViewById(R.id.btnfail);
        btnfail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fail(null);
            }
        });
    }

    void getService() {
        LightSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (LightSensorManager == null) {
            fail(getString(R.string.service_get_fail));
        }

        mLightSensor = LightSensorManager.getDefaultSensor(SENSOR_TYPE);
        if (mLightSensor == null) {
            fail(getString(R.string.sensor_get_fail));
        }

        mLightSensorListener = new LightSensorListener(this);
        if (!LightSensorManager.registerListener(mLightSensorListener,
                mLightSensor, SENSOR_DELAY)) {
            fail(getString(R.string.sensor_register_fail));
        }
    }

    void updateView(Object s, String alsData) {
        mTextView.setText("" + s);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lightsensor_byd);

        bindView();
        getService();
        updateView(value, "");

    }

    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        Utils.writeCurMessage(this, TAG, "Failed");
        finish();
    }

    void pass() {
        // toast(getString(R.string.test_pass));
        setResult(RESULT_OK);
        Utils.writeCurMessage(this, TAG, "Pass");
        finish();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (LightSensorManager == null || mLightSensorListener == null
                || mLightSensor == null)
            return;
        LightSensorManager.unregisterListener(mLightSensorListener,
                mLightSensor);
    }

    public class LightSensorListener implements SensorEventListener {
        private int count = 0;

        public LightSensorListener(Context context) {
            super();
        }

        public void onSensorChanged(SensorEvent event) {
            // LightSensor event.value has 3 equal value.
            synchronized (this) {
                if (event.sensor.getType() == SENSOR_TYPE) {
                    logd(event.values.length + ":" + event.values[0] + " "
                            + event.values[0] + " " + event.values[0] + " ");

                    String value = "" + event.values[SensorManager.DATA_X];
                    updateView(value, "");
                    if ( (int)event.values[SensorManager.DATA_X] < 5){
                        btntrue.setEnabled(true);
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {

        }
    }

    public void toast(Object s) {
        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    private void loge(Object e) {
        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    private void logd(Object s) {
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

}
