
package com.tools.cit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class WiFiTest2 extends TestModule /* STEP 1 */{
    public static final String WiFi = "Wi-Fi";
    WifiManager wm;
    TextView txtLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.wifi, R.drawable.wifi);
        txtLog = (TextView) findViewById(R.id.wifi_txt_log);
        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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
        return WiFi;
    }

    // private String keyMessage="MESSAGE", keyColor="COLOR";
    private Thread testThread = new Thread(new Runnable() {
        @Override
        public void run() {
            do {
                boolean wifiEnabledOrig = wm.isWifiEnabled();
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
                        e.printStackTrace();
                    }
                }
                if (wm.isWifiEnabled() == wifiEnabledOrig)
                    break;
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_success),
                        Color.GREEN));

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
                // get mac
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_get_mac),
                        Color.WHITE));
                WifiInfo info = wm.getConnectionInfo();
                if (null == info)
                    break;
                runOnUiThread(new UIThread(info.getMacAddress(), Color.WHITE));
                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_success),
                        Color.GREEN));
                // SUCCESS
                setTestResult(PASS);

                runOnUiThread(new UIThread(getResources().getString(R.string.wifi_finished),
                        Color.GREEN));
                return;
            } while (false);
            // FAIL
            runOnUiThread(new UIThread(getResources().getString(R.string.wifi_fail), Color.RED));
            setTestResult(FAIL);
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

    private void appendMsg(String text, int color) {
        txtLog.append(FTUtil.coloredString(text, color));
        txtLog.append("\n");
    }

    private void appendMsg(int resId, int color) {
        txtLog.append(FTUtil.coloredString(WiFiTest2.this, resId, color));
        txtLog.append("\n");
    }

    // we want the test thread has finished before exit.
    // So we override the following 2 methods.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && testThread.isAlive()) {
            appendMsg(R.string.wifi_in_test, Color.WHITE);
            FTLog.i(this, "Oops! Test thread is still alive.");
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }
}
