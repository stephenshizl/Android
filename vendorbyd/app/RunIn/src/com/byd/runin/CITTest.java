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

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import android.os.Handler;
import android.os.Message;
import java.io.BufferedReader;

public class CITTest {
  private static final String TAG= "CITTest";
//  private static final String CURRENT_PATH = "/sys/class/power_supply/bq24192_charger/device/set_charging";
  private static final String CURRENT_PATH = "/sys/class/power_supply/bq27441_battery/status";
   private static final String NO_CHANGE_PATH= "/sys/devices/platform/pxa910-i2c.2/i2c-2/2-006b/batt_cur_control";
  File chargingFile ;
  private static final int CHARGING= 1;
  private static final int DISCHARGING= 2;
    private static final int AGAIN_CHARGING= 3;
 private static final int NO_CHANGE= 4;
 private static final int NO_CHANGE_1= 5;
 private static final int NO_CHANGE_2= 6;
  //private static final int AGAIN_CHARGING_BEGIN= 4;
  public CITTest()
  {
 
  }

  /*public String getChargingStatus() {

			FileReader voltageReader;
			String volString = "";
			char voltageBuffer[] = new char[30];
			try {
				voltageReader = new FileReader(CURRENT_PATH);
				voltageReader.read(voltageBuffer, 0, 30);
				for (int i = 0; i < voltageBuffer.length; i++) {
					volString += voltageBuffer[i];
				}
				Log.d(TAG, "status = " + volString);
				voltageReader.close();
			} catch (Exception e) {
				Log.e(TAG, "without access to the status");
				e.printStackTrace();
			}
			return volString;

 	 }*/
 	 public int getCurrent() {
	        File file = new File("/sys/class/power_supply/battery/current_now");
	        if (!file.exists())
	        {
	            Log.i(TAG,  " doesn't exist");
	            return 0;
	        }
	        BufferedReader reader = null;
	        String tempString = "";
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            tempString = reader.readLine();
	            Log.i(TAG, ": " + tempString);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	            }
	        }
	        return Integer.parseInt(tempString);
    	}

 	 public String getChargingStatus() {
	        File file = new File(CURRENT_PATH);
	        if (!file.exists())
	        {
	            Log.i(TAG,  " doesn't exist");
	            return null;
	        }
	        BufferedReader reader = null;
	        String tempString = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            tempString = reader.readLine();
	            Log.i(TAG, ": " + tempString);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	            }
	        }
	        return tempString.toLowerCase();
    	}
 	 public void setCharging() {
  		try {
                File file = new File(CURRENT_PATH);
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write("charging".getBytes());
                outStream.close();
			} catch (Exception e) {
			    e.printStackTrace();
			}
 	 }
 	 public void setDischarging() {
  		    try {
                File file = new File(CURRENT_PATH);
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write("discharging".getBytes());
                outStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
  }


}

