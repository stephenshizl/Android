
package com.tools.cit;

import java.util.Timer;
import java.util.TimerTask;

import com.tools.util.FTLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.IWindowManager;
import android.view.WindowManager;
public class GPSTest extends TestModule {
    private static String MODULE_NAME = "GPS";
    // private SensorManager mSensorManager;
    // private Sensor mProximity;
    private long mlCount = 0;
    private long mlTimerUnit = 500;
    private Timer timer = null;
    private TimerTask task = null;
    private Handler handler = null;
    private Message msg = null;

    private TextView txtSatelliteN;
    private TextView txtLongitude;
    private TextView txtLatitude;
    private TextView txtAltitude;
    private TextView txtAccuracy;
    private TextView txtLog;
    Intent serverIntent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.gps, R.drawable.gps);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        txtSatelliteN = (TextView) findViewById(R.id.txt_gps_satellite_no);
        txtLongitude = (TextView) findViewById(R.id.txt_gps_longitude);
        txtLatitude = (TextView) findViewById(R.id.txt_gps_latitude);
        txtAltitude = (TextView) findViewById(R.id.txt_gps_altitude);
        txtAccuracy = (TextView) findViewById(R.id.txt_gps_accuracy);
        txtLog = (TextView) findViewById(R.id.gps_test_log);
        // tvTime = (TextView) findViewById(R.id.tvTime);
        serverIntent.setClassName(GPSTest.this, "com.tools.cit.GpsServer");
        fillContent();
        startTimer();
        // Handle timer message
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                case 1:
                   mlCount++;
                   int totalSec = 0;
                   int yushu = 0;
                   totalSec = (int) (mlCount / 10);
                   yushu = (int) (mlCount % 10);
                   // Set time display
                   int min = (totalSec / 60);
                   int sec = (totalSec % 60);
                   Log.i("gpstest", "total sec:"+ totalSec + "total min:" + min);
                   fillContent();
                   if (1 <= min){
                        Log.i("gpstest", "enter stop gps:" + min);
                        stopTimer();
                        txtLog.setText(R.string.gps_timeout);
                   }                   
                    break;
                default:
                    break;
                }

                super.handleMessage(msg);
            }

        };
    }

    private void fillContent() {
            if(GpsServer.workString.equals(getString(R.string.gps_timeout))) {
                startService(serverIntent);
                txtLog.setText(getString(R.string.gps_working));
            } else {
             String longitudefromgServer = GpsServer.txtLongitude;
                String txtLatitudefromgServer = GpsServer.txtLatitude;
                String txtAltitudefromgServer = GpsServer.txtAltitude;
                String txtAccuracyfromgServer = GpsServer.txtAccuracy;
                if (!longitudefromgServer.equals("") && !txtLatitudefromgServer.equals("") && !txtAltitudefromgServer.equals("")) { 
                //setTestResult(PASS);
                     findViewById(R.id.btn_pass).setEnabled(true);
                }
                txtLongitude.setText(longitudefromgServer);
                txtLatitude.setText(txtLatitudefromgServer);
                txtAltitude.setText(txtAltitudefromgServer);
                txtAccuracy.setText(txtAccuracyfromgServer);
                txtLog.setText(GpsServer.workString);
                txtSatelliteN.setText(GpsServer.txtSatelliteN);
          }
    }

    @Override
    protected void onStart() {
        FTLog.d(this, "in onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        FTLog.d(this, "in onResume");
        super.onResume();       
    }

    @Override
    protected void onPause() {
        FTLog.d(this, "in onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        FTLog.d(this, "in onDestroy");
        stopTimer();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        FTLog.d(this, "in onStop");
        super.onStop();
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    private void startTimer()
    {
        FTLog.d(this, "start timer");
        if (null == timer) {
            if (null == task) {
                task = new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (null == msg) {
                            msg = new Message();
                        } else {
                            msg = Message.obtain();
                        }
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }

                };
            }

            timer = new Timer(true);
            timer.schedule(task, mlTimerUnit, mlTimerUnit);
        }
    }

    private void stopTimer()
    {
        FTLog.d(this, "stop timer");

        if (null != timer) {
            task.cancel();
            task = null;
            timer.cancel(); // Cancel timer
            timer.purge();
            timer = null;
            if(msg != null){
                handler.removeMessages(msg.what);
            }
        }
    }

    protected void buttomBarOnClick(View v) {
        if (v.getId() == R.id.btn_pass || v.getId() == R.id.btn_fail) {
            FTLog.d(this, "set result and stop timer.");
            stopTimer();
        }
        super.buttomBarOnClick(v);
    }
}
