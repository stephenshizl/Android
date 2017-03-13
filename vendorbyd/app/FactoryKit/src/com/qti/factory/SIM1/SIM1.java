/*
 * Copyright (c) 2011-2015, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.SIM1;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

//yhx import com.codeaurora.telephony.msim.SubscriptionManager;
import android.telephony.SubscriptionManager;

import com.qti.factory.Utils;
import com.qti.factory.Values;

public class SIM1 extends Activity {

    String TAG = "SIM1";
    String resultString = "Failed";
    String toastString = "";
    int simState = TelephonyManager.SIM_STATE_UNKNOWN;
    boolean result = false;
    int SUB_ID = 0;

    public static final int SIM_STATE_UNKNOWN = 0;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_CARD_IO_ERROR = 6;

    @Override
    public void finish() {

        Utils.writeCurMessage(TAG, resultString);

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
            logd("SIM state=" + mTelephonyManager.getSimState());
            IMSI = mTelephonyManager.getSubscriberId();
            if (IMSI != null && !IMSI.equals("")) {
                result = true;
                toastString = IMSI;
            } /*
                     * else if (mTelephonyManager.getSimState() !=
                     * TelephonyManager.SIM_STATE_ABSENT) { result = true; toastString =
                     * "State: Ready"; }
                     */
        }

        if (result) {
            setResult(RESULT_OK);
            resultString = Utils.RESULT_PASS;
            toast(toastString);

        } else {
            setResult(RESULT_CANCELED);
            resultString = Utils.RESULT_FAIL;
        }
        finish();
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
