package com.qti.factory.Hall;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.io.FileInputStream;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.qti.factory.R;
import com.qti.factory.Utils;

public class Hall extends Activity {
    private static final String TAG = "Hall";
    private static final String HALL_STATUS_NEAR = "hall_state: active";
    private static final String HALL_STATUS_FAR = "hall_state: inactive";
   // private static final String HALL_STATUS_FILE = "/sys/devices/hall_sensor.76/input/input1/state";
    private static final String HALL_STATUS_FILE = "/sys/class/input/input5/state";
    private Button btnSuc, btnFail;
    private TextView tvHallNotification, tvHallState;
    private SensorManager sensorMgr = null;
    private Sensor sensor = null;
    private SensorEventListener lsn = null;
    boolean hallState = false, mHall = false;
    String resultString = Utils.RESULT_FAIL;
    private final static int SENSOR_TYPE = Sensor.TYPE_MAGNETIC_FIELD;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hall);
        btnSuc = (Button) findViewById(R.id.hall_suc);
        btnFail = (Button) findViewById(R.id.hall_fail);
        btnSuc.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pass();
            }
        });
        btnFail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                fail(null);
            }
        });
		/*sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorMgr.getDefaultSensor(SENSOR_TYPE);
		if (sensor == null) {
			logd("fghk="+"1111");
            fail(null);
        }*/
        tvHallNotification = (TextView) findViewById(R.id.hall_notification);
        tvHallState = (TextView) findViewById(R.id.hall_state);
        tvHallNotification.setText(R.string.tvHallNotification);
        //tvHallState.setText(R.string.hall_inactive);
        btnSuc.setEnabled(false);

        /*lsn = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                synchronized (this) {
                    if (event.sensor.getType() == SENSOR_TYPE) {
                        hallState = getHallStatus();
                        if (hallState) {
                            mHall = true;
                            tvHallState.setText(R.string.hall_active);
                        } else {
                            tvHallState.setText(R.string.hall_inactive);
                        }
                        if ((hallState == false) && mHall){
                            btnSuc.setEnabled(true);
                            mHall = false;
                        }
                    }
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };*/
    }

    private boolean getHallStatus() {
        boolean isHallActive = false;
        String strStatus = "";
        FileInputStream is = null;
        int count = 0;
        try {
            is = new FileInputStream(HALL_STATUS_FILE);
            byte[] buffer = new byte[30];
            count = is.read(buffer);
            if (count > 0) {
                strStatus = new String(buffer, 0, count);
            }
        } catch (IOException e) {
            Log.e(TAG, "no file :" + HALL_STATUS_FILE + e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        if (strStatus.contains(HALL_STATUS_NEAR)) {
            isHallActive = true;
        } else {
            isHallActive = false;
        }
        return isHallActive;
    }

    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        resultString = Utils.RESULT_FAIL;
        finish();
    }

    void pass() {
        setResult(RESULT_OK);
        resultString = Utils.RESULT_PASS;
        finish();
    }

    public void toast(Object s) {
        if (s == null)
            {Toast.makeText(this, "abcdefgh", Toast.LENGTH_SHORT).show();
            return;}
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

    public void onResume() {
       // sensorMgr.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
    }

    protected void onPause() {
        //sensorMgr.unregisterListener(lsn);
        if (getHallStatus()){
            btnSuc.setEnabled(true);
        }
        super.onPause();
    }
}
