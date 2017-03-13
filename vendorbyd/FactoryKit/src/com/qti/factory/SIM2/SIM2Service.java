/*
 * Copyright (c) 2013-2015, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.SIM2;

import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;

//yhx import com.codeaurora.telephony.msim.SubscriptionManager;
import android.telephony.SubscriptionManager;

import com.qti.factory.Utils;
import com.qti.factory.Values;
import com.qti.factory.Framework.MainApp;

public class SIM2Service extends Service {

    String TAG = "SIM2Service";
    int simState = TelephonyManager.SIM_STATE_UNKNOWN;
    boolean result = false;
    final int SUB_ID = 1;
    int index = -1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return -1;
        index = intent.getIntExtra(Values.KEY_SERVICE_INDEX, -1);
        if (index < 0)
            return -1;

        init();
        startTest();
        finishTest();

        return super.onStartCommand(intent, flags, startId);
    }

    public static final int SIM_STATE_UNKNOWN = 0;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_CARD_IO_ERROR = 6;

    private void init() {
        simState = TelephonyManager.SIM_STATE_UNKNOWN;
        result = false;
    }

    private void finishTest() {
        Map<String, String> item = (Map<String, String>) MainApp.getInstance().mItemList.get(index);
        if (result) {
            item.put("result", Utils.RESULT_PASS);
            Utils.saveStringValue(getApplicationContext(), item.get("title"),Utils.RESULT_PASS);
            Utils.writeCurMessage(TAG, Utils.RESULT_PASS);
        } else {
            item.put("result", Utils.RESULT_FAIL);
            Utils.saveStringValue(getApplicationContext(), item.get("title"),Utils.RESULT_FAIL);
            Utils.writeCurMessage(TAG, Utils.RESULT_FAIL);
        }

        sendBroadcast(new Intent(Values.BROADCAST_UPDATE_MAINVIEW));
    }

    private void startTest() {
        logd("");
        String IMSI = null;
        if ("dsds".equals(Utils.getSystemProperties(Values.PROP_MULTISIM, null))) {
           //yhx  MSimTelephonyManager mMSimTelephonyManager = (MSimTelephonyManager) getSystemService(Service.MSIM_TELEPHONY_SERVICE);
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            //yhx SubscriptionManager mSubscriptionManager = SubscriptionManager.getInstance();
            //yhx if (mMSimTelephonyManager == null)
            if (mTelephonyManager == null)
                return;
            else {
                //yhx IMSI = mMSimTelephonyManager.getSubscriberId(SUB_ID);
                IMSI = mTelephonyManager.getSubscriberId(SubscriptionManager.getSubId(SUB_ID)[0]);
                if (IMSI != null && !IMSI.equals("")) {
                result = true;
            //yhx    } else if (mMSimTelephonyManager.getSimState(SUB_ID) == TelephonyManager.SIM_STATE_READY) {
                } else if (mTelephonyManager.getSimState(SUB_ID) == TelephonyManager.SIM_STATE_READY) {
                    result = true;
             //yhx    } else if (mSubscriptionManager != null && mSubscriptionManager.isSubActive(SUB_ID)) {
                } else if (SubscriptionManager.getSubState(SubscriptionManager.getSubId(SUB_ID)[0]) == 1) {
                    result = true;
                }
            }
        } else {
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            logd("SIM state=" + mTelephonyManager.getSimState());
            IMSI = mTelephonyManager.getSubscriberId();
            if (IMSI != null && !IMSI.equals("")) {
                result = true;
            } /*
                    * else if (mTelephonyManager.getSimState() !=
                    * TelephonyManager.SIM_STATE_ABSENT) { result = true; }
                    */
        }

    }

    private void logd(Object s) {

        if (Values.SERVICE_LOG) {
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
