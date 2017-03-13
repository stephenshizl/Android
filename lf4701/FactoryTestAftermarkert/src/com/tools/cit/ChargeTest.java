
package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;
import java.io.FileReader;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.text.format.DateUtils;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.view.WindowManager;
public class ChargeTest extends TestModule /* STEP 1 */{

    public static final String CHARGE = "Charge";

    private static final int DIALOG_MSG_PLUGGED = 1;
    private static final int DURATION = 10000;
    private static final int EVENT_TICK = 2;
    private Timer timer = null;
    private TimerTask task = null;
    BatteryBroadcastReceiver batteryReceiver;
    TextView txtBatteryStatus;
    TextView txtBatteryLevel;
    TextView txtBatteryCapacity;
    TextView txtBatteryHealth;
    TextView txtBatteryTechnology;
    TextView txtPowerPlugged;
    TextView txtTemperature;
    TextView txtVoltage;
    TextView txtBootTime;
    TextView txtCurrent;
    
    boolean plugged;
    private final String CHARGING_CURRENT_PATH = "/sys/class/power_supply/battery/current_now";
    private final String CHARGING_HEALTH_PATH = "/sys/class/power_supply/battery/health";
    private final String CHARGING_VOLTAGE_PATH = "/sys/class/power_supply/battery/voltage_ocv";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.charge, R.drawable.charge);
        plugged = true;
        batteryReceiver = new BatteryBroadcastReceiver();
        txtBatteryStatus = (TextView) findViewById(R.id.txt_battery_status);
        txtBatteryLevel = (TextView) findViewById(R.id.txt_battery_level);
        txtBatteryCapacity=(TextView)findViewById(R.id.txt_battery_capacity);
        txtBatteryTechnology = (TextView) findViewById(R.id.txt_battery_technology);
        txtPowerPlugged = (TextView) findViewById(R.id.txt_power_plugged);
        txtTemperature=(TextView)findViewById(R.id.txt_temperature);
        txtVoltage=(TextView)findViewById(R.id.txt_voltage);
        txtBootTime=(TextView)findViewById(R.id.txt_boot_time);
        txtCurrent=(TextView)findViewById(R.id.txt_current);
        txtBatteryHealth=(TextView)findViewById(R.id.battery_health);
        //((Button) findViewById(R.id.btn_pass)).setEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }
    @Override  
    public void onResume() {
        super.onResume();
        SystemProperties.set("persist.sys.citcharge", "true");
        myHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }
    @Override
    protected void onPause() {
        SystemProperties.set("persist.sys.citcharge", "false");
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        cancelTimer();
        TestThread exitThread=new TestThread("charge-exit-testmode");
        exitThread.start();
        super.onDestroy();
    }
      Handler myHandler = new Handler(){    
        public void handleMessage(Message msg){      
            switch(msg.what){      
                case EVENT_TICK:        
                    updateBatteryStats();        
                    sendEmptyMessageDelayed(EVENT_TICK, 1000);                          
                    break;                              
                default:break;
                }      
            super.handleMessage(msg);
            }
        };

    // STEP 3
    @Override
    public String getModuleName() {
        return CHARGE;
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_MSG_PLUGGED:
                return createPluggedDialog();

            default:
                return null;
        }
    }

    private AlertDialog createPluggedDialog()
    {
        LayoutInflater factory = LayoutInflater.from(this);
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setView(factory.inflate(R.layout.charge_plugged, null));
        return mAlertDialog.create();
    }

    private void setTimer()
    {
        if (null == timer) {
            if (null == task) {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setTestResult(FAIL);
                        timer=null;
                        task=null;
                    }
                };
            }
            timer = new Timer(true);
            timer.schedule(task, DURATION);
        }
    }
    private final String tenthsToFixedString(int x) {
        int tens = x / 10;
        return Integer.toString(tens) + "." + (x - 10 * tens);
    }
    private void cancelTimer()
    {
        if (null != timer) {
            if (null != task) {
                while (!task.cancel())
                    ;
                timer.cancel();
                timer=null;
                task=null;
            }
        }
    }

    class TestThread extends Thread implements Runnable{
        String command;

        public TestThread(String command){
            super();
            this.command=command;
        }

        @Override
        public void run() {
            File f=new File("/system/etc/tcmd/"+command);
            if(!f.exists()){
                FTLog.e(this, "/system/etc/tcmd/"+command+" doesn't exist");
                return;
            }
            String receive=FTUtil.TCP_Send(command);
            if(("" != receive)&&(-1!=receive.indexOf("ret: 0", 0)))
                FTLog.i(this, "Execute "+command+" success.");
            else
                FTLog.e(this, "Execute " + command + " error.");
        }
    }

    private String getBetteryParameters(String path) {
        FileReader BetteryReader;
        String BetteryString = "";
        char betteryBuffer[] = new char[30];        
        try {            
            BetteryReader = new FileReader(path);            
            BetteryReader.read(betteryBuffer, 0, 30);            
            for (int i = 0; i < betteryBuffer.length; i++) {
                BetteryString += betteryBuffer[i]; 
                }            
            BetteryString=BetteryString.trim();          
            BetteryReader.close();       
        } catch (Exception e) {
            Log.e(CHARGE, "without access to the health");
            e.printStackTrace();
        }        
        return BetteryString;
    }
    private void updateBatteryStats() {
        long uptime = SystemClock.elapsedRealtime();
        txtBootTime.setText(DateUtils.formatElapsedTime(uptime / 1000));

    }
    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

                float voltage_now,current_now;
                int maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                double currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / (double) maxLevel;
                String currentPercent = (int) Math.floor(currentLevel * 100) + "";
                //txtBatteryCapacity.setText(""+maxLevel);
                txtBatteryLevel.setText(currentPercent);
                txtBatteryCapacity.setText(""+currentPercent+"%");
                txtBatteryHealth.setText(getBetteryParameters(CHARGING_HEALTH_PATH));
                int plugged_status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                voltage_now = (float)Integer.parseInt(getBetteryParameters(CHARGING_VOLTAGE_PATH))/1000000.0f;
                current_now = (float)Integer.parseInt(getBetteryParameters(CHARGING_CURRENT_PATH))/1000.0f;
                    
                FTLog.i(this, "Plugged status: " + plugged_status);
                switch (plugged_status) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        removeDialog(DIALOG_MSG_PLUGGED);
                        plugged = true;
                        setTimer();
                        txtPowerPlugged.setText(R.string.power_plugged_ac);
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        removeDialog(DIALOG_MSG_PLUGGED);
                        plugged = true;
                        setTimer();
                        txtPowerPlugged.setText(R.string.power_plugged_usb);
                        break;
                    default:
                        plugged = true;
                        txtPowerPlugged.setText(R.string.battery_status_unknown);
                        break;
                }
                txtBatteryTechnology.setText(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
                txtTemperature.setText("" + tenthsToFixedString(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0))
                    +getString(R.string.battery_info_temperature_units)+"\n"+getString(R.string.temp_spec));
                txtVoltage.setText(" "+voltage_now+getString(R.string.battery_info_voltage_units)+"\n"+getString(R.string.voltage_spec));
                txtCurrent.setText(" "+current_now+getString(R.string.battery_info_current_units)+"\n"+getString(R.string.current_spec));

                //if (plugged) {
                    TestThread enterThread=new TestThread("charge-enter-testmode");
                    enterThread.start();
                    int battery_status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                            BatteryManager.BATTERY_STATUS_UNKNOWN);
                    FTLog.i(this, "Battery status: " + battery_status);
                    switch (battery_status) {
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            cancelTimer();
                            txtBatteryStatus.setText(R.string.battery_status_charging);
                            // test pass
                            //setTestResult(PASS);
                            ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                            txtBatteryStatus.setText(R.string.battery_status_discharging);
                            //setTestResult(PASS);            
                            // setTestResult(FAIL);
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            cancelTimer();
                            txtBatteryStatus.setText(R.string.battery_status_full);
                            ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                            //setTestResult(PASS);
                            break;
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            txtBatteryStatus.setText(R.string.battery_status_not_charging);
                            //setTestResult(PASS);            
                            // setTestResult(FAIL);
                            break;
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        default:
                            txtBatteryStatus.setText(R.string.battery_status_discharging);
                            //setTestResult(PASS);
                            // setTestResult(FAIL);
                            break;
                    }
                //} else
                //{
                 //   txtBatteryStatus.setText("");
                //    showDialog(DIALOG_MSG_PLUGGED);
                //}
            }
        }
    }
}
