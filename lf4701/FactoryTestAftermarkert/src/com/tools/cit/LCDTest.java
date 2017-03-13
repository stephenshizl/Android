
package com.tools.customercit;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

public class LCDTest extends TestModule /* STEP 1 */implements OnTouchListener {
    public static final String LCD = "LCD Color";
    LcdTestResultReceiver receiver = new LcdTestResultReceiver();
    public static final String LCD_SEND_RESULT_ACTION = "byd.SFTT.action.lcdtestfinish";
    public static final String LCD_AUTO_SEND_RESULT_ACTION = "byd.SFTT.action.auto.lcdtestfinish";
    private static String ACTION_RGB_BEGIN= "com.tools.AUTOCIT.RGB_BEGIN";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.lcd, R.drawable.lcd);
        findViewById(R.id.txt_tap_to_start).setOnTouchListener(this);
        
        //registerReceiver(receiver, new IntentFilter(LCD_SEND_RESULT_ACTION));
        
        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(LCD_SEND_RESULT_ACTION);
        mIntentFilter.addAction(LCD_AUTO_SEND_RESULT_ACTION);
        mIntentFilter.addAction(ACTION_RGB_BEGIN);
        /*
        mIntentFilter.addAction(ACTION_RGB_BLACK);
        mIntentFilter.addAction(ACTION_RGB_RED);
        mIntentFilter.addAction(ACTION_RGB_GREEN);
        mIntentFilter.addAction(ACTION_RGB_BLUE);*/
        registerReceiver(receiver, mIntentFilter);
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return LCD;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showLCDTest2();
            default:
                ;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onPause(); 
        unregisterReceiver(receiver);
    }
    
    private void showLCDTest2() {
        Intent intent = new Intent();
        intent.setClassName("com.tools.customercit", "com.tools.customercit.LCDTest2");
        startActivity(intent);
    }
    class LcdTestResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LCD_SEND_RESULT_ACTION)) {
                ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                 //setTestResult(PASS);
                ((TextView)findViewById(R.id.txt_tap_to_start)).setText(getString(R.string.lcd_tap_to_finish));
            }
            if (intent.getAction().equals(LCD_AUTO_SEND_RESULT_ACTION)) {
                ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                 setTestResult(PASS);
            } 
            if(intent.getAction().equals(ACTION_RGB_BEGIN)){
                showLCDTest2();
            }
        }
    }
}
