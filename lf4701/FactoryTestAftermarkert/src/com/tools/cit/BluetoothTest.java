
package com.tools.customercit;

import android.app.Activity;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Button;
import android.view.WindowManager;
public class BluetoothTest extends TestModule /* STEP 1 */{
    public static final String MODULE_NAME = "Bluetooth";
    TextView txtLog;
    BluetoothAdapter btAdapter;
    private static int bt_number = 0;
    private boolean btEnabledOrig;
    int log_red, log_green, log_yellow, log_normal_color;
    boolean test_complete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.bluetooth, R.drawable.bluetooth);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        test_complete=false;
        txtLog = (TextView) findViewById(R.id.bt_txt_log);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // get color
        log_red = getResources().getColor(R.color.log_red);
        log_green = getResources().getColor(R.color.log_green);
        log_yellow = getResources().getColor(R.color.log_yellow);
        log_normal_color = Color.WHITE;

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        testThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        bt_number = 0;
    }

    // private String keyMessage="MESSAGE", keyColor="COLOR";
    private Thread testThread = new Thread(new Runnable() {
        @Override
        public void run() {

            do {
                // Step 1. Check whether bluetooth hardware exists
                if (btAdapter == null) {
                    FTLog.e(this, "BluetoothAdapter is null");
                    appendMsg(R.string.bt_absent, log_red);
                    break;
                }
                FTLog.i(this, "BluetoothAdapter is not null");
                // appendMsg(R.string.bt_detected, log_green);
                appendMsg(R.string.bt_checking_status, log_normal_color);
                btEnabledOrig = btAdapter.isEnabled();
                appendMsg(
                        getResources().getString(R.string.bt_status)
                                + " "
                                + getResources().getString(
                                        btEnabledOrig ? R.string.bt_enabled : R.string.bt_disabled),
                        log_green);

                // Step 2. Turn on/off
                if (btEnabledOrig) {
                    appendMsg(R.string.bt_disabling, log_normal_color);
                    btAdapter.disable();
                }
                else {
                    appendMsg(R.string.bt_enabling, log_normal_color);
                    btAdapter.enable();
                }
                try {
                    for (int i = 0; i < 20; i++) {
                        Thread.sleep(500);
                        // FTLog.i(this, "BT state: "+
                        // btAdapter.getState()+", isEnabled: " +
                        // btAdapter.isEnabled());
                        if (btAdapter.getState() != BluetoothAdapter.STATE_TURNING_OFF &&
                                btAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON &&
                                btAdapter.isEnabled() == !btEnabledOrig)
                            break;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (btAdapter.isEnabled() == !btEnabledOrig)
                    appendMsg(R.string.bt_success, log_green);
                else
                    break;
                FTLog.i(this, "Bluetooth status: " + btAdapter.isEnabled());
                if (btAdapter.isEnabled() == true) {
                    // If we're already discovering, stop it
                    if (btAdapter.isDiscovering()) {
                        btAdapter.cancelDiscovery();
                    }
                    // Request discover from BluetoothAdapter
                    appendMsg(R.string.scanning, log_normal_color);
                    if (!btAdapter.startDiscovery())
                    {
                        appendMsg(R.string.scan_fail, Color.RED);
                        break;
                    }
                }
                else {
                    // Step 3. Turn off/on
                    if (btEnabledOrig) {
                        appendMsg(R.string.bt_enabling, log_normal_color);
                        btAdapter.enable();
                        FTLog.i(this, "ACTION_REQUEST_ENABLE finished.");
                    } else {
                        appendMsg(R.string.bt_disabling, log_normal_color);
                        btAdapter.disable();
                    }
                    try {
                        for (int i = 0; i < 20; i++) {
                            Thread.sleep(500);
                            if (btAdapter.getState() != BluetoothAdapter.STATE_TURNING_OFF &&
                                    btAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON &&
                                    btAdapter.isEnabled() == btEnabledOrig)
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (btAdapter.isEnabled() == btEnabledOrig)
                        appendMsg(R.string.bt_success, log_green);
                    else
                        break;
                    FTLog.i(this, "Bluetooth status: " + btAdapter.isEnabled());

                    if (btAdapter.isEnabled() == true) {
                        // If we're already discovering, stop it
                        if (btAdapter.isDiscovering()) {
                            btAdapter.cancelDiscovery();
                        }
                        // Request discover from BluetoothAdapter
                        appendMsg(R.string.scanning, log_normal_color);
                        if (!btAdapter.startDiscovery())
                        {
                            appendMsg(R.string.scan_fail, Color.RED);
                            break;
                        }
                    }
                }
                return;
            } while (false);
            // FAIL
            runOnUiThread(new UIThread(getResources().getString(R.string.bt_fail), Color.RED));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTestResult(FAIL);
                }
            });
            test_complete=true;
        }
    });

    class UIThread implements Runnable {
        String string;
        int color;

        public UIThread(String string, int color) {
            super();
            this.string = string;
            this.color = color;
        }

        @Override
        public void run() {
            appendMsg(string, color);

        }

    }

    // keys for store data in Bundle
    private final String TEXT_STRING_KEY = "TEXT_STRING";
    private final String TEXT_RES_ID_KEY = "TEXT_RES_ID";
    private final String COLOR_KEY = "COLOR";

    // use this event to change UI.
    private final int EVENT_CHANGE_UI = 0;
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FTLog.d(this, "handle Message");
            if (msg.what == EVENT_CHANGE_UI) {
                FTLog.d(this, "handle EVENT_CHANGE_UI");
                Bundle data = msg.getData();
                int color = data.getInt(COLOR_KEY);
                String text = data.getString(TEXT_STRING_KEY);
                if (text != null) {
                    txtLog.append(FTUtil.coloredString(text, color));
                    txtLog.append("\n");
                } else {
                    int resID = data.getInt(TEXT_RES_ID_KEY);
                    FTLog.d(this, "Res ID is: " + resID + "");
                    txtLog.append(FTUtil.coloredString(BluetoothTest.this, resID, color));
                    txtLog.append("\n");
                }
            } else
                super.handleMessage(msg);
        }

    };

    /**
     * Send log message to UI thread.
     *
     * @param text
     * @param color
     */
    private void appendMsg(String text, int color) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_STRING_KEY, text);
        bundle.putInt(COLOR_KEY, color);
        Message message = uiHandler.obtainMessage();
        message.what = EVENT_CHANGE_UI;
        message.setData(bundle);
        message.sendToTarget();
    }

    /**
     * Send log message to UI thread.
     *
     * @param resId
     * @param color
     */
    private void appendMsg(int resId, int color) {
        Bundle bundle = new Bundle();
        bundle.putInt(TEXT_RES_ID_KEY, resId);
        bundle.putInt(COLOR_KEY, color);
        Message message = uiHandler.obtainMessage();
        message.what = EVENT_CHANGE_UI;
        message.setData(bundle);
        message.sendToTarget();
        FTLog.d(this, "send Message");
    }

    // we want the test thread has finished before exit.
    // So we override the following 2 methods.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !test_complete) {
            appendMsg(R.string.bt_in_test, log_normal_color);
            FTLog.i(this, "Oops! Test thread is still alive.");
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private boolean BTdisable()
    {
        if (btEnabledOrig == false)
        {
            appendMsg(R.string.bt_disabling, log_normal_color);
            btAdapter.disable();
            try {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(500);
                    if (btAdapter.getState() != BluetoothAdapter.STATE_TURNING_OFF &&
                            btAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON &&
                            btAdapter.isEnabled() == btEnabledOrig)
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (btAdapter.isEnabled() == false)
                appendMsg(R.string.bt_success, log_green);
            else
            {
                runOnUiThread(new UIThread(getResources().getString(R.string.bt_fail), Color.RED));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTestResult(FAIL);
                    }
                });
                return false;
            }
        }
        return true;
    }

    // The BroadcastReceiver that listens for discovered devices
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DisableBTTask disableTask = new DisableBTTask();
            disableTask.execute(intent);
        }
    };

    public class DisableBTTask extends AsyncTask<Intent, Integer, String> {

        @Override
        protected String doInBackground(Intent... params) {
            String action=params[0].getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (bt_number < 1)
                {
                    BluetoothDevice device = params[0]
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    appendMsg(device.getName() + "-" + device.getAddress(), log_normal_color);
                    if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                        short rssi =  params[0].getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                        appendMsg("RSSI:"+rssi, log_normal_color);
                    }
                    appendMsg(R.string.bt_success, log_green);
                    bt_number++;
                    if(BTdisable())
                    {
                        // SUCCESS
                        //setTestResult(PASS);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                findViewById(R.id.btn_pass).setEnabled(true);
                            }
                        });
                        // FINISH
                        appendMsg(R.string.bt_finished, log_green);
                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (bt_number == 0) {
                    appendMsg(R.string.none_found, log_red);
                    if(BTdisable())
                        appendMsg(R.string.set_result, log_red);
                }
            }
            test_complete=true;
            return null;
        }

    }
}
