package com.tools.customercit;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class GpsServer extends Service {

    private int mlCount = 0;
    private long mlTimerUnit = 5000;
    private Timer timer = null;
    private TimerTask task = null;
    private Message msg = null;
    private Handler handler = null;
    private boolean bIsRunningFlg = false;

    LocationManager lm;
    LocationListener locationListener = new MyLocationListener();
    private static final int TYPE_LATITUDE = 0, TYPE_LONGITUDE = 1;
    public static String workString = "";
    public static String txtSatelliteN = "";
    public static String txtLongitude = "";
    public static String txtLatitude = "";
    public static String txtAltitude = "";
    public static String txtAccuracy = "";
        
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
        
    @Override
    @SuppressLint("HandlerLeak")
    public int onStartCommand(Intent intent, int flags, int startId) {
        txtLongitude = "";
        txtLatitude = "";
        txtAltitude = "";
        txtAccuracy = "";
        txtSatelliteN = "";
        Log.i("gps service start", "Gpsservicestart");
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // lm.setTestProviderEnabled("gps", true);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled...
            workString = getString(R.string.gps_not_working);
        } else {
            workString = getString(R.string.gps_working);
            startTimer();
        }
        lm.addGpsStatusListener(gpsStatusListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // Handle timer message
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                case 1:
                    mlCount += 5;
                    int min = (mlCount / 60);
                    if (min >= 10) {
                        stopTimer();
                        workString = getString(R.string.gps_timeout);
                    }
                    break;

                default:
                    break;
                }

                super.handleMessage(msg);
            }
        };
        return super.onStartCommand(intent, flags, startId);
    }
            
    private void stopTimer() {
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

        mlCount = 0;
        bIsRunningFlg = false;
    }

    private void startTimer() {
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
            timer.schedule(task, mlTimerUnit, mlTimerUnit); // set timer
                                                            // duration
        }
        if (!bIsRunningFlg) {
            bIsRunningFlg = true;
            // btnStartPause.setImageResource(R.drawable.pause);
        }
    } // end startTimer() 

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            txtLongitude = convertCoordinate(location.getLongitude(),
                    TYPE_LONGITUDE);
            txtLatitude = convertCoordinate(location.getLatitude(),
                    TYPE_LATITUDE);
            txtAltitude = new DecimalFormat("0.00").format(location
                    .getAltitude()) + " m";
            txtAccuracy = new DecimalFormat("0.00").format(location
                    .getAccuracy()) + " m";
            workString = getString(R.string.gps_ok);
            stopTimer();
        }

        private String convertCoordinate(double coordinate, int type) {
            switch (type) {
            case TYPE_LATITUDE:
                return convertToDegree(Math.abs(coordinate))
                        + (coordinate > 0 ? "N" : "S");
            case TYPE_LONGITUDE:
                return convertToDegree(Math.abs(coordinate))
                        + (coordinate > 0 ? "E" : "W");
            default:
                return "";
            }
        }

        private String convertToDegree(double coordinate) {
            String ret = "";
            if (coordinate < 0)
                return ret;
            int intPart = (int) Math.floor(coordinate);
            ret = ret + intPart + "\u00B0"; //

            coordinate -= intPart;
            coordinate *= 60;
            intPart = (int) Math.floor(coordinate);
            ret = ret + intPart + "\u2019"; //

            coordinate -= intPart;
            coordinate *= 60;
            intPart = (int) Math.round(coordinate);
            ret = ret + intPart + "\u201D"; //
            return ret;
        }

        @Override
        public void onProviderDisabled(String provider) {
            // providerDisabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            txtLongitude = "";
            txtLatitude = "";
            txtAltitude = "";
            txtAccuracy = "";
            txtSatelliteN = "";

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {

            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                txtSatelliteN = "";
                GpsStatus gpsStatus = lm.getGpsStatus(null);
                Iterable<GpsSatellite> i = gpsStatus.getSatellites();
                int n = 0;
                Iterator<GpsSatellite> ii = i.iterator();
                int maxSatellites = gpsStatus.getMaxSatellites();
                ArrayList<GpsSatellite> satelliteList = new ArrayList<GpsSatellite>();
                while (ii.hasNext() && n <= maxSatellites) {
                    GpsSatellite satellite = ii.next();
                    satelliteList.add(satellite);
                    n++;
                }
                txtSatelliteN += n + "";
                txtSatelliteN += "\n";
                for (int j = 0; j < satelliteList.size(); j++) {
                    txtSatelliteN += "GpsSatellite" + j + "  " + "SNR:"
                            + satelliteList.get(j).getSnr();
                    txtSatelliteN += "\n";
                }
                
            }

        }
    };

}
