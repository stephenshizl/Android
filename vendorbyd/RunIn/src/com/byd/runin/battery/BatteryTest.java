
package com.byd.runin.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.UserHandle;
import android.widget.TextView;
import com.byd.runin.BatteryInfo;
import com.byd.runin.TestLog.Log;

public class BatteryTest
{
    private static final String TAG = "BatteryTest";

    private static final int MIN_LEVEL = 55;
    private static final int MAX_LEVEL = 65;

    private Context mContext;
    private STATUS mStatus = STATUS.NONE;

    private enum STATUS
    {
        NONE, LOAD_FAILED, CHARGING, DISCHARGE, NOT_PLUG_IN,
    }

    public BatteryTest(Context context, TextView status)
    {
        mContext = context;
    }

    public void start()
    {
        mContext.registerReceiver(mBatteryInfoReceiver, new IntentFilter
            (Intent.ACTION_BATTERY_CHANGED));

        mContext.startServiceAsUser(new Intent(mContext,
            BatteryInfoService.class), UserHandle.OWNER);
    }

    public void stop()
    {
        mContext.unregisterReceiver(mBatteryInfoReceiver);
        if (mStatus != STATUS.LOAD_FAILED)
        {
            updateStatus(0); // restore
        }

        Intent intent = new Intent(mContext, BatteryInfoService.class);
        intent.putExtra("finish", true);
        mContext.startServiceAsUser(intent, UserHandle.OWNER);
        mContext = null;
    }

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int plug = intent.getIntExtra("plugged", 0);

                Log.d(TAG, "plug = " + plug + " , level = " + level +
                    " , scale = " + scale);

                if (plug == 0)
                {
                    mStatus = STATUS.NOT_PLUG_IN;
                    updateBatteryInfoText("", String.valueOf(level * 100 /
                        scale) + "%");
                }
                else if (mStatus == STATUS.LOAD_FAILED)
                {
                    Log.e(TAG, "load failed no charging switch method");
                }
                else
                {
                    if (mStatus == STATUS.NOT_PLUG_IN)
                    {
                        Log.d(TAG, "not plug in status change to none status");
                        mStatus = STATUS.NONE;
                    }

                    updateStatus(level * 100 / scale);

                    int status = intent .getIntExtra("status",
                        BatteryManager.BATTERY_STATUS_UNKNOWN);

                    String text = getStatusText(status);
                    updateBatteryInfoText(text, String.valueOf(level * 100 /
                        scale) + "%");
                }

                Log.d(TAG, "status = " + mStatus);
            }
        }
    };

    private String getStatusText(int status)
    {
        String text = "";
        if (status == BatteryManager.BATTERY_STATUS_CHARGING)
        {
            text = "Charging...";
        }
        else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING)
        {
            text = "DisCharging";
        }
        else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
        {
            text = "Not Charging";
        }
        else if (status == BatteryManager.BATTERY_STATUS_FULL)
        {
            text = "Full";
        }
        else
        {
            text = "Unknown";
        }

        return text;
    }

    private void updateBatteryInfoText(String state, String level)
    {
        switch (mStatus)
        {
            case NONE:
                state = "None";
                break;
            case LOAD_FAILED:
                state = "Load failed";
                break;
            case CHARGING:
                break;
            case DISCHARGE:
                break;
            case NOT_PLUG_IN:
                state = "Not Plug in";
                break;
        }

        Intent intent = new Intent(mContext, BatteryInfoService.class);
        intent.putExtra("update", true);
        intent.putExtra("state", state);
        intent.putExtra("level", level);
        mContext.startServiceAsUser(intent, UserHandle.OWNER);
    }

    private void updateStatus(int level)
    {
        Log.d(TAG, "updateStatus level = " + level);
        try
        {
            if (level < MIN_LEVEL && mStatus != STATUS.CHARGING)
            {
            Log.d(TAG, "batteryCapacity < 55 && wei chongdian ze chongdian");
                choiceCharging(level);
                mStatus = STATUS.CHARGING;
            }
            else if (level > MAX_LEVEL && mStatus != STATUS.DISCHARGE)
            {
            Log.d(TAG, "batteryCapacity >65 && chongdian ze tingchong");
                Log.d(TAG, "updateStatus level 111111111    = " + level);
                choiceCharging(level);
                mStatus = STATUS.DISCHARGE;
            }
            else
            {
            Log.d(TAG, "batteryCapacity 55-65 shenme dou bu zuo ");
              //  choiceCharging(level);
            }
        }
        catch (NoSuchMethodError e)
        {
            Log.e(TAG, "NoSuchMethodError");
            mStatus = STATUS.LOAD_FAILED;
        }
    }

    private void choiceCharging(int batteryCapacity)
    {
        if (batteryCapacity < MIN_LEVEL)
        {Log.d(TAG, "batteryCapacity < 55");
        //if (!BatteryInfo.getChargingStatus().equals("charging"))
            if (BatteryInfo.getChargingStatus().equals("charging"))
            {
                Log.d(TAG, "change to charging");
                BatteryInfo.setCharging();
            }
        }
        else if (batteryCapacity > MAX_LEVEL)
        {
            Log.d(TAG, "batteryCapacity >65");
           // if (!BatteryInfo.getChargingStatus().equals("discharging"))
            if (!BatteryInfo.getChargingStatus().equals("charging"))
            {
                Log.i(TAG, "change to discharging");
                BatteryInfo.setDischarging();
            }
        }
    }
}
