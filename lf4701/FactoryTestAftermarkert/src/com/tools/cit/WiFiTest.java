
package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.view.WindowManager;
import java.util.List;

public class WiFiTest extends TestModule /* STEP 1 */{
    public static final String WiFi = "Wi-Fi";
    WifiManager wm;
    private WiFiReceiver receiverWifi;
    TextView txtLog;
    private StringBuffer mStringBuffer = new StringBuffer();
    private List<ScanResult> listResult;
    private ScanResult mScanResult;
    boolean wifiEnabledOrig;
    boolean test_complete;
    boolean two_hot[] = new boolean[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.wifi, R.drawable.wifi);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        test_complete=false;
        txtLog = (TextView) findViewById(R.id.wifi_txt_log);
        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WiFiReceiver();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return WiFi;
    }

    // private String keyMessage="MESSAGE", keyColor="COLOR";
    private Thread testThread = new Thread(new Runnable() {
        @Override
        public void run() {
            do {
                wifiEnabledOrig = wm.isWifiEnabled();
                // check wifi status
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_checking_status),
                        Color.WHITE));
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_status)
                        +
                        getResources().getString(
                                wifiEnabledOrig ? R.string.wifi_enabled : R.string.wifi_disabled),
                        Color.GREEN)
                );
                // Step 2. change to !status
                runOnUiThread(new UIThread(getResources().getString(
                        wifiEnabledOrig ? R.string.wifi_disabling : R.string.wifi_enabling),
                        Color.WHITE));
                wm.setWifiEnabled(!wifiEnabledOrig);
                for (int i = 0; i < 12; i++) {
                    try {
                        Thread.sleep(500);
                        if (wm.getWifiState() != WifiManager.WIFI_STATE_DISABLING &&
                                wm.getWifiState() != WifiManager.WIFI_STATE_ENABLING &&
                                wm.getWifiState() != WifiManager.WIFI_STATE_UNKNOWN &&
                                wm.isWifiEnabled() == !wifiEnabledOrig)
                            break;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (wm.isWifiEnabled() == wifiEnabledOrig)
                    break;
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_success),
                        Color.GREEN));

                if (wm.isWifiEnabled() == true)
                {
                    runOnUiThread(new UIThread(getResources().getString(R.string.wifi_scan),
                            Color.WHITE));
                    runOnUiThread(new UIThread(getResources().getString(R.string.wifi_scan_comment),
                            Color.WHITE));                   
                    if (!wm.startScan())
                    {
                        runOnUiThread(new UIThread(getResources().getString(
                                R.string.wifi_scan_failed),
                                Color.RED));
                        break;
                    }
                    else
                        registerReceiver(receiverWifi, new IntentFilter(
                                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                }
                else {
                    // change to status
                    runOnUiThread(new UIThread(getResources().getString(
                            wifiEnabledOrig ? R.string.wifi_enabling : R.string.wifi_disabling),
                            Color.WHITE));
                    wm.setWifiEnabled(wifiEnabledOrig);
                    for (int i = 0; i < 12; i++) {
                        try {
                            Thread.sleep(500);
                            if (wm.getWifiState() != WifiManager.WIFI_STATE_DISABLING &&
                                    wm.getWifiState() != WifiManager.WIFI_STATE_ENABLING &&
                                    wm.getWifiState() != WifiManager.WIFI_STATE_UNKNOWN &&
                                    wm.isWifiEnabled() == wifiEnabledOrig)
                                break;
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (wm.isWifiEnabled() != wifiEnabledOrig)
                        break;
                    runOnUiThread(new UIThread(getResources().getString(R.string.wifi_success),
                            Color.GREEN));
                    if (wm.isWifiEnabled() == true)
                    {
                        runOnUiThread(new UIThread(getResources().getString(R.string.wifi_scan),
                                Color.WHITE));
                        if (!wm.startScan())
                        {
                            runOnUiThread(new UIThread(getResources().getString(
                                    R.string.wifi_scan_failed),
                                    Color.RED));
                            break;
                        }
                        else
                            registerReceiver(receiverWifi, new IntentFilter(
                                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    }
                }
                return;
            } while (false);
            // FAIL
            runOnUiThread(new UIThread(getResources().getString(R.string.wifi_fail), Color.RED));
            setTestResult(FAIL);
            test_complete=true;
        }
    });

  class ennablePass implements Runnable {
        @Override
        public void run() {
            findViewById(R.id.btn_pass).setEnabled(true);
        }
    }
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

    private void appendMsg(String text, int color) {
        txtLog.append(FTUtil.coloredString(text, color));
        txtLog.append("\n");
    }

    private void appendMsg(int resId, int color) {
        txtLog.append(FTUtil.coloredString(WiFiTest.this, resId, color));
        txtLog.append("\n");
    }

    private boolean WiFidisable()
    {
        if (wifiEnabledOrig == false)
        {
            runOnUiThread(new UIThread(getResources().getString(R.string.wifi_disabling),
                    Color.WHITE));
            wm.setWifiEnabled(wifiEnabledOrig);
            for (int i = 0; i < 12; i++) {
                try {
                    Thread.sleep(500);
                    if (wm.getWifiState() != WifiManager.WIFI_STATE_DISABLING &&
                            wm.getWifiState() != WifiManager.WIFI_STATE_ENABLING &&
                            wm.getWifiState() != WifiManager.WIFI_STATE_UNKNOWN &&
                            wm.isWifiEnabled() == wifiEnabledOrig)
                        break;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (wm.isWifiEnabled() != false)
            {
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_fail), Color.RED));
                setTestResult(FAIL);
                return false;
            }
            else
            {
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_success),
                        Color.GREEN));
                return true;
            }
        }
        return true;
    }

    private class WiFiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            AsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    handleIntent(context, intent);
                }
            });
        }
    }

    private void handleIntent(Context context, Intent intent)
    {
        if (intent.getAction().equals(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        {
            if (mStringBuffer != null) {
                mStringBuffer = new StringBuffer();
            }
            listResult = wm.getScanResults();
            if (listResult != null) {
                FTLog.d(this, "APList: ");
                //int num = (listResult.size() < 3) ? listResult.size() : 3;
                int num = listResult.size();
                FTLog.d(this, "num: " + num);

                if (num > 0)
                {
                     String wifiType="";
                     int ChannelNum=0;
                    for (int i = 0; i < num; i++) {
                        mScanResult = listResult.get(i);
                        mStringBuffer.setLength(0);
                        int j=mScanResult.frequency;
                        if(mScanResult.is24GHz()) {
                            wifiType = "2.4G";
                            two_hot[0] = true;
                        } else if(mScanResult.is5GHz()) {
                            wifiType = "5G";
                            two_hot[1] = true;
                        } else {
                            wifiType = "other";
                        }
                        if(j>=2412 && j<=2484){
                            if(j==2484){
                                ChannelNum = 14;
                            }else{
                                ChannelNum=(j-2412)/5 + 1;
                            }
                        } else if(j>=5180 &&j<=5320){
                            ChannelNum=(j-5180)/5+36;
                        } else if(j>=5745 && j<=5825){
                           ChannelNum=(j-5745)/5+149;                           
                        }
                        /*if(j>=2412 && j<=2484){
                            wifiType = "2.4G";
                            two_hot[0] = true;
                            if(j==2484){
                                ChannelNum = 14;
                            }else{
                                ChannelNum=(j-2412)/5 + 1;
                            }
                        }else if(j>=5180 &&j<=5320){
                            wifiType = "5G";
                            ChannelNum=(j-5180)/5+36;
                            two_hot[1] = true;
                        }else if(j>=5745 && j<=5825){
                           wifiType = "5G";
                           ChannelNum=(j-5745)/5+149;
                           two_hot[1] = true;
                        }*/
                        mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
                                .append(" :").append(ChannelNum).append("\t\t\t\t")
                                .append(wifiType).append("\t\t\t\t")
                                .append(mScanResult.level).append("\t\t\t\t")
                                .append(mScanResult.SSID).append("\n");
                        FTLog.d(this, mStringBuffer.toString());
                       /* if("BYD24".equals(mScanResult.SSID))
                            two_hot[0] = true;
                        if("BYD50".equals(mScanResult.SSID))
                            two_hot[1] = true;
                        */
                        runOnUiThread(new UIThread(mStringBuffer.toString(), Color.WHITE));
                    }
    //                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_success),
    //                        Color.GREEN));
                    unregisterReceiver(receiverWifi);
                    if (WiFidisable())
                    {
                        runOnUiThread(new UIThread(getResources().getString(R.string.wifi_finished),
                                Color.GREEN));

                        if(two_hot[0] == true || two_hot[1] == true)
                           runOnUiThread(new Runnable() {
                            public void run() {
                                findViewById(R.id.btn_pass).setEnabled(true);
                            }
                        });//setTestResult(PASS);

                    }
                    test_complete=true;
                    return;
                }
            }
            //mStringBuffer = mStringBuffer.append("No nearby APs were found");
            mStringBuffer = mStringBuffer.append(getResources().getString(R.string.wifi_scan_none));
            FTLog.d(this, mStringBuffer.toString());
            runOnUiThread(new UIThread(mStringBuffer.toString(), Color.RED));
            unregisterReceiver(receiverWifi);
            if (WiFidisable())
                runOnUiThread(new UIThread(getResources().getString(R.string.set_result),
                        Color.RED));
            test_complete=true;
            return;
        }
    }

    // we want the test thread has finished before exit.
    // So we override the following 2 methods.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !test_complete) {
            appendMsg(R.string.wifi_in_test, Color.WHITE);
            FTLog.i(this, "Oops! Test thread is still alive.");
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }
}

/**
 * Helper class for managing the background thread used to perform io operations
 * and handle async broadcasts.
 */
final class AsyncHandler {

    private static final HandlerThread sHandlerThread =
            new HandlerThread("AsyncHandler");
    private static final Handler sHandler;

    static {
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper());
    }

    public static void post(Runnable r) {
        sHandler.post(r);
    }

    private AsyncHandler() {
    }
}
