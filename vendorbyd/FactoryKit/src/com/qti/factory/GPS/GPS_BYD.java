/*
 * Copyright (c) 2011-2013 Qualcomm Technologies, Inc.  All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.GPS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.os.Handler;
import android.os.Message;
import java.util.Calendar;
import java.util.Iterator;
import android.provider.Settings;
import com.qti.factory.R;
import com.qti.factory.Utils;

public class GPS_BYD extends Activity {
    private Button bttntrue, bttnfail;
    private TextView mtext, lngAndlatTextView;
    private boolean mGPSTestStatus = false;
    private LocationManager mLocationGps;
    private String mGPSprovider = LocationManager.GPS_PROVIDER;
    private GpsStatus gpsStatus;
    private GPSInfo currentGPSinfo = new GPSInfo();
    private Calendar c;
    private static final int GPS_START_SEARCH = 0;
    private static final int MAX_COUNT = 15; //Limiting the search results of GPS

    public class GPSInfo {
        double mLongitude;
        double mLatitude;
        double mAltitude;
        float mSpeed;
        String mBearing;
        String mTime;
        String mAccuracy;
        String mSatelliteInfo;
        String mPrn;
        String mSnr;
    }

    private static final String TAG = "AutoGPS";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_byd);
        // setTitle(R.string.auto_gps);
        mtext = (TextView) findViewById(R.id.text);
        lngAndlatTextView = (TextView) findViewById(R.id.currentgpsinfo);
        mLocationGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bttntrue = (Button) findViewById(R.id.bttntrue);
        bttntrue.setOnClickListener(BttnTrueListener);
        bttnfail = (Button) findViewById(R.id.bttnfail);
        bttnfail.setOnClickListener(BttnFailListener);
    }

    private OnClickListener BttnTrueListener = new OnClickListener() {
        public void onClick(View v) {
            pass();
        }
    };
    private OnClickListener BttnFailListener = new OnClickListener() {
        public void onClick(View v) {
            fail(null);
        }
    };
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // gpsStatus.getTimeToFirstFix();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                gpsStatus = mLocationGps.getGpsStatus(null);
                Iterable<GpsSatellite> allSatellites;
                allSatellites = gpsStatus.getSatellites();
                Iterator<GpsSatellite> it = allSatellites.iterator();
                int iPos = 0;
                String sSatellite = "";
                while (it.hasNext()) {
                    GpsSatellite gpsS = (GpsSatellite) it.next();
                    iPos++;
                    if(iPos > MAX_COUNT){
                        break;
                    }
                    sSatellite += getResources().getString(R.string.gps_satellite) + String.valueOf(iPos) + ": ";
                    sSatellite += getResources().getString(R.string.gps_prn) + "=" + String.valueOf(gpsS.getPrn()) + ", ";
                    sSatellite += getResources().getString(R.string.gps_snr) + "=" + String.valueOf(gpsS.getSnr()) + "\n";
                    bttntrue.setEnabled(true);
                }
                if (sSatellite != null) {
                    mtext.setText(sSatellite);
                }
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                // Event sent when the GPS system has started.
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                // Event sent when the GPS system has stopped.
                break;
            default:
                break;
            }
        }
    };
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateLatAndLng(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void updateLatAndLng(Location currentLocation) {
        String latLongString;
        if (currentLocation != null) {
            currentGPSinfo.mLongitude = currentLocation.getLongitude();
            currentGPSinfo.mLatitude = currentLocation.getLatitude();
            currentGPSinfo.mAltitude = currentLocation.getAltitude();
            currentGPSinfo.mSpeed = currentLocation.getSpeed();
            latLongString = getResources().getString(R.string.gps_longitude) + currentGPSinfo.mLongitude + "\n" + getResources().getString(R.string.gps_latitude) + currentGPSinfo.mLatitude + "\n" + getResources().getString(R.string.gps_altitude) + currentGPSinfo.mAltitude;
            Calendar cnow = Calendar.getInstance();
            long timewhile = cnow.getTimeInMillis() - c.getTimeInMillis();
            if (timewhile > 0) {
                latLongString += "\n" + getResources().getString(R.string.gps_time) + timewhile / 1000;
            }
            // Toast.makeText(Gpslocation.this, latLongString,
            // Toast.LENGTH_SHORT).show();
            if (latLongString != null) {
                lngAndlatTextView.setText(latLongString);
                bttntrue.setEnabled(true);
            } else {
                // lngAndlatTextView.setText("searching...");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bttntrue.setEnabled(false);
        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
        /*
         * if (mLocationGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         * mtext.setText(R.string.gps_search); lngAndlatTextView.setText("");
         * mLocationGps.requestLocationUpdates(mGPSprovider, 2000, 2,
         * locationListener); mLocationGps.addGpsStatusListener(statusListener);
         * mGPSTestStatus = true; c = Calendar.getInstance(); } else {
         * mtext.setText(R.string.ncit_error); }
         */
        Log.i(TAG, "onResume,send a delay message to start search gps");
        Message msg = new Message();
        msg.what = GPS_START_SEARCH;
        myHandler.sendMessageDelayed(msg, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mtext.setText("");
        lngAndlatTextView.setText("");
        if (mGPSTestStatus) {
            mLocationGps.removeUpdates(locationListener);
            mLocationGps.removeGpsStatusListener(statusListener);
            mGPSTestStatus = false;
        }
        /*
         * //Begin shenzhiyong 20120218 add for close gps
         * Settings.Secure.setLocationProviderEnabled(getContentResolver(),
         * LocationManager.GPS_PROVIDER, false); //End 20120218
         */
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GPS_START_SEARCH:
                Log.e(TAG, "in handlemessage,case:" + msg.what);
                GpsSearch();
                break;
            }
        }
    };

    public void GpsSearch() {
        Log.e(TAG, "go to GpsSearch to begin to search");
        if (mLocationGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mtext.setText(R.string.gps_search);
            lngAndlatTextView.setText("");
            mLocationGps.requestLocationUpdates(mGPSprovider, 2000, 2, locationListener);
            mLocationGps.addGpsStatusListener(statusListener);
            mGPSTestStatus = true;
            c = Calendar.getInstance();
        } else {
            mtext.setText(R.string.gps_error);
        }
    }

    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        Utils.writeCurMessage(this, TAG, "Failed");
        finish();
    }

    void pass() {
        setResult(RESULT_OK);
        Utils.writeCurMessage(this, TAG, "Pass");
        finish();
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
