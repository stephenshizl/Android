
package com.qti.factory.Compass;

import android.content.Context;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.os.Message;
import android.os.Handler;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.qti.factory.R;
import com.qti.factory.Utils;
import com.qti.factory.Values;
import com.qti.factory.Framework.MainApp;
import com.qti.factory.Compass.CompassView;

public class Compass extends Activity implements OnClickListener, SensorEventListener {

    private static final String TAG = "Compass";
    private Button falBtn, sucBtn;
    private TextView sensorValue;
    private SensorManager mSensorManager;
    Sensor oriSensor;
    private CompassView mView;

    public boolean mbTestResult =  false;
    private boolean bWaitingClose = false;
    private static final int EVENT_VERIFY_RESULT = 1001;
    private static final int EVENT_DELAY_TIME    = 10000;
    private String resultString;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.compass, null);
        setContentView(view);

        mView = (CompassView) view.findViewById(R.id.MSensorPattern);

        sucBtn = (Button) view.findViewById(R.id.sucBtn);
        sucBtn.setOnClickListener(this);
        sucBtn.setEnabled(false);
        falBtn = (Button) view.findViewById(R.id.failBtn);
        falBtn.setOnClickListener(this);

        sensorValue = (TextView)findViewById(R.id.sensorValue);
        //sucBtn.setEnabled(false);
        //falBtn.setEnabled(false);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        oriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch(id){
            case R.id.sucBtn:
                pass();
                break;

            case R.id.failBtn:
                fail(null);
                break;

            default:
                Log.e(TAG,"Error!");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (oriSensor != null)
            mSensorManager.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {

        if (oriSensor != null)
            mSensorManager.unregisterListener(this, oriSensor);
        super.onStop();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            //disable the key
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
    //    @Override
    public void onSensorChanged(SensorEvent event) {
    // TODO Auto-generated method stub
        Log.e(TAG,"smx the sensor is:"+event.sensor.getType()+"["+event.values[0]+"]["+event.values[1]+"]["+event.values[2]+"]");
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            if (mView != null) {
                sensorValue.setText("Value: " + event.values[0]);
                mView.setValue(event.values[0]);
                mView.invalidate();
                if((event.values[0] >= 350) || (event.values[0] <= 10)) {
                    sucBtn.setEnabled(true);
                }
            }
        }
    }

    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        resultString = Utils.RESULT_FAIL;
        finish();
    }

    void pass() {
        // toast(getString(R.string.test_pass));
        setResult(RESULT_OK);
        resultString = Utils.RESULT_PASS;
        finish();

    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
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

    public void toast(Object s) {

        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

}
