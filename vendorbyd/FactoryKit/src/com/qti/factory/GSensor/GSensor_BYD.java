/*
 * Copyright (c) 2011-2012 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

package com.qti.factory.GSensor;

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
import android.widget.ImageView;

import com.qti.factory.R;
import com.qti.factory.Utils;

public class GSensor_BYD extends Activity {

    private SensorManager mSensorManager = null;
    private Sensor mGSensor = null;
    private GSensorListener mGSensorListener;
    TextView mTextView, tvNote;
    Button btntrue,btnfail;
    private final static String INIT_VALUE = "";
    private static String value = INIT_VALUE;
    private static String pre_value = INIT_VALUE;
    private final int MIN_COUNT = 15;
    String TAG = "GSensor";
    private final static int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;
    private final static int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private float x,y,z;
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    private static final int FACE = 4;
    private static final int BACK = 5;
    private boolean xAxis = false, yAxis = false, zAxis = false;
    private final int[] numResId = {R.drawable.ic_arrow_up,R.drawable.ic_arrow_right,
        R.drawable.ic_arrow_down,R.drawable.ic_arrow_left, R.drawable.ic_arrow_face, R.drawable.ic_arrow_back};

    @Override
    public void finish() {

        try {

            mSensorManager.unregisterListener(mGSensorListener, mGSensor);
        } catch (Exception e) {
        }
        super.finish();
    }

    void bindView() {
        mTextView = (TextView) findViewById(R.id.sensor);
        tvNote = (TextView)findViewById(R.id.note);
        tvNote.setText(R.string.gsensor_notification);

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

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager == null) {
            fail(getString(R.string.service_get_fail));
        }

        mGSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE);
        if (mGSensor == null) {
            fail(getString(R.string.sensor_get_fail));
        }

        mGSensorListener = new GSensorListener(this);
        if (!mSensorManager.registerListener(mGSensorListener, mGSensor, SENSOR_DELAY)) {
            fail(getString(R.string.sensor_register_fail));
        }
    }

    void updateView(Object s) {

        mTextView.setText(""+s);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gsensor_byd);

        bindView();
        getService();

        updateView(value);

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

        if (mSensorManager == null || mGSensorListener == null || mGSensor == null)
            return;
        mSensorManager.unregisterListener(mGSensorListener, mGSensor);
    }

    public class GSensorListener implements SensorEventListener {

        private int count = 0;

        public GSensorListener(Context context) {

            super();
        }

        public void onSensorChanged(SensorEvent event) {

            synchronized (this) {
                if (event.sensor.getType() == SENSOR_TYPE) {
/*
                    logd(event.values.length + ":" + event.values[0] + " " + event.values[1] + " " + event.values[2]
                            + " ");
                    String value = "(" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")";
                    //updateView(value);
                    if (value != pre_value)
                        count++;
                    if (count >= MIN_COUNT)
                        //pass();
                    pre_value = value;
*/
                    x = event.values[SensorManager.DATA_X];
                    y = event.values[SensorManager.DATA_Y];
                    z = event.values[SensorManager.DATA_Z];
                    value ="X: " + x + "\nY: "+ y + "\nZ: " + z;
                    updateView(value);
                    if ((10.0 - y) < 1.0) {
                        refreshDisplay(DOWN);
                    } else if ((10.0 - y) > 19.0) {
                        refreshDisplay(UP);
                    }

                    if ((10.0 - x) < 1.0) {
                        refreshDisplay(LEFT);
                    } else if ((10.0 - x) > 19.0){
                        refreshDisplay(RIGHT);
                    }

                    if ((10.0 - z) < 1.0) {
                        refreshDisplay(FACE);
                    } else if ((10.0 - z) > 19.0){
                        refreshDisplay(BACK);
                    }

                   if (x > 8.5) {
                        xAxis = true;
                    }
                    if (y > 8.5) {
                        yAxis = true;
                    }
                    if (z > 8.5) {
                        zAxis = true;
                    }
                    if (xAxis && yAxis && zAxis) {
                        btntrue.setEnabled(true);
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {

        }
    }

    public void refreshDisplay(int i) {
        ImageView imageArrow = (ImageView)findViewById(R.id.arrow);
        imageArrow.setImageResource(numResId[i]);
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
