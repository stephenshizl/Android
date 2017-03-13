
/*===========================================================================

EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20100720  Sang Mingxin   Add to provide memory test native interface.
20100512  Sang Mingxin   Initial to provide native interface.

===========================================================================*/

package com.byd.runin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

import com.byd.runin.TestLog.Log;

import java.io.BufferedReader;

public class BatteryInfo
{
    private static final String TAG = "BetteryInfo";
    private static final String ENABLE_CHARGING_PATH = "/sys/class/power_supply/battery/charging_enabled";
    private static final String PRESENT_CHANGE_PATH = "/sys/class/power_supply/battery/charging_enabled";
    private static final String STATUS_CHARGING_PATH = "sys/class/power_supply/battery/status";
    private static final String CHARGING = "charging";
    private static final String DISCHARGING = "discharging";

    private static final String STATUS_ENABLE_VALUE = "1";
    private static final String STATUS_DISENABLE_VALUE = "0";

    public BatteryInfo(){

    }

    public static String getChargingStatus()
    {
        //File file = new File(ENABLE_CHARGING_PATH);
        File file = new File(STATUS_CHARGING_PATH);
        if (!file.exists())
        {
            Log.i(TAG, " doesn't exist");
            return null;
        }
        BufferedReader reader = null;
        String tempString = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            tempString = reader.readLine();
            Log.i(TAG, "getChargingStatus: " + tempString);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        //if (tempString.equals(STATUS_ENABLE_VALUE))
        if (tempString.equals(CHARGING))
        {
            return CHARGING;
        }
        else
        {
            return DISCHARGING;
        }

    }

    //start charging
    public static void setCharging()
    {
        setPresent(STATUS_ENABLE_VALUE);
    }

    private static void setPresent(String status_value)
    {
        try
        {
            File file = new File(PRESENT_CHANGE_PATH);
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(status_value.getBytes());
            outStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //stop charging
    public static void setDischarging()
    {
        setPresent(STATUS_ENABLE_VALUE);
        setPresent(STATUS_DISENABLE_VALUE);
        int nCount = 0;
      try
        {
            while (isEnableCharging())
            {
                File file = new File(ENABLE_CHARGING_PATH);
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(STATUS_DISENABLE_VALUE.getBytes());
                outStream.close();
                ++nCount;
                if (nCount > 3)
                    break;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static boolean isEnableCharging()
    {
        boolean enable = false;
        try
        {
            File file = new File(ENABLE_CHARGING_PATH);
            FileInputStream is = new FileInputStream(file);
            int value = is.read();
            if (value - '0' == 1)
                enable = true;
            Log.d(TAG, "isEnableCharging enable = " + enable);
            is.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return enable;
    }


}
