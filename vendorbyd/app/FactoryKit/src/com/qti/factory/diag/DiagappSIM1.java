/*
 * Copyright (c) 2011-2012 Qualcomm Technologies, Inc.  All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.diag;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import com.qualcomm.factory.Utilities;
import android.os.SystemProperties;
//yhx import com.codeaurora.telephony.msim.SubscriptionManager;
import android.telephony.SubscriptionManager;

import com.qti.factory.Utils;
import com.qti.factory.Values;
public class DiagappSIM1 extends Activity {

    String TAG = "DiagappSIM1";
    String resultString = "Failed";
    String toastString = "";
    int simState = TelephonyManager.SIM_STATE_UNKNOWN;
    boolean result = false;
    int SUB_ID = 0;

    private static final String SIMSTATUS_RESULT_PATH = "/data/simappstatus.txt";
    public static final int SIM_STATE_UNKNOWN = 0;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_CARD_IO_ERROR = 6;
    @Override
    public void finish() {
        logd(resultString);
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logd("");

        super.onCreate(savedInstanceState);

        String IMSI = null;
        if ("dsds".equals(Utils.getSystemProperties(Values.PROP_MULTISIM, null))) {
            // if (TelephonyManager.isMultiSimEnabled()) {
         //yhx   MSimTelephonyManager mMSimTelephonyManager = (MSimTelephonyManager) getSystemService(Service.MSIM_TELEPHONY_SERVICE);
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            //yhx SubscriptionManager mSubscriptionManager = SubscriptionManager.getInstance();
            //yhx if (mMSimTelephonyManager == null)
            if (mTelephonyManager == null)
                finish();
            else {
              //yhx  logd("SIM state=" + mMSimTelephonyManager.getSimState(SUB_ID));
              //yhx  IMSI = mMSimTelephonyManager.getSubscriberId(SUB_ID);
                    logd("SIM state=" + mTelephonyManager.getSimState(SUB_ID));
                    IMSI = mTelephonyManager.getSubscriberId(SubscriptionManager.getSubId(SUB_ID)[0]);
                if (IMSI != null && !IMSI.equals("")) {
                    result = true;
                    toastString = "IMSI: " + IMSI;
               //yhx  } else if (mMSimTelephonyManager.getSimState(SUB_ID) == TelephonyManager.SIM_STATE_READY) {
                } else if (mTelephonyManager.getSimState(SUB_ID) == TelephonyManager.SIM_STATE_READY) {
                    result = true;
                    toastString = "State: Ready";
               //yhx } else if (mSubscriptionManager != null&& mSubscriptionManager.isSubActive(SUB_ID)) {
                } else if (SubscriptionManager.getSubState(SubscriptionManager.getSubId(SUB_ID)[0]) == 1) {
                    result = true;
                    toastString = "SIM: Enabled";
                }
            }

        } else {
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            IMSI = mTelephonyManager.getSubscriberId();
            if (IMSI != null && !IMSI.equals("")) {
                result = true;
                toastString = IMSI;
            } else if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                result = true;
                toastString = "State: Ready";
            }

        }

        if (result) {
            setResult(RESULT_OK);
            saveResultToDATA(1);
           // resultString = Utilities.RESULT_PASS;
            toast(toastString);

        } else {

            setResult(RESULT_CANCELED);
            saveResultToDATA(0);
           // resultString = Utilities.RESULT_FAIL;
        }
        finish();
    }
    public void saveResultToDATA(int result){
        FileWriter fWriter = null;
        try {
            File simstatusResult = new File(SIMSTATUS_RESULT_PATH);
	    if(!simstatusResult.exists()){
		simstatusResult.createNewFile();

	    }
                fWriter = new FileWriter(SIMSTATUS_RESULT_PATH);
                fWriter.write(String.valueOf(1));
            if(result == 0){
                fWriter = new FileWriter(SIMSTATUS_RESULT_PATH);
                fWriter.write(String.valueOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( fWriter != null){
                try{
                    fWriter.close();
                } catch (Exception e){
                }
            }
        }
    }
    public void toast(Object s) {

        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }
}
