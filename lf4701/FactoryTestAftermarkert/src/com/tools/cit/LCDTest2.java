
package com.tools.customercit;

import com.tools.util.FTLog;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.util.Log;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.content.Intent;
import android.view.WindowManager;
import android.view.Window;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;

public class LCDTest2 extends Activity implements OnTouchListener {
    public static final String VIBRATOR = "Vibrator";
    LinearLayout bgView;
    Drawable[] bgs = null;
    int i = 0;
    int black=0;
    int white=1;
    int red=2;
    int blue=3;
    int green=4;
    private static String ACTION_RGB_WHITE= "com.tools.AUTOCIT.RGB_WHITE";
   // private static String ACTION_RGB_BLACK = "com.tools.AUTOCIT.RGB_BLACK";
    private static String ACTION_RGB_RED = "com.tools.AUTOCIT.RGB_RED";  
    private static String ACTION_RGB_BLUE = "com.tools.AUTOCIT.RGB_BLUE";
    private static String ACTION_RGB_GREEN = "com.tools.AUTOCIT.RGB_GREEN";
    private static String ACTION_RGB_END = "com.tools.AUTOCIT.RGB_END";

    private final BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                 String action = intent.getAction();
          
                 if(action.equals(ACTION_RGB_WHITE)) {
                    bgView.setBackground(bgs[white]);
                    bgView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                  }
                 if(action.equals(ACTION_RGB_RED)) {
                    bgView.setBackground(bgs[red]);
                    bgView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                  }   
                 if(action.equals(ACTION_RGB_BLUE)) {
                    bgView.setBackground(bgs[blue]);
                    bgView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                  }
                 if(action.equals(ACTION_RGB_GREEN)) {
                    bgView.setBackground(bgs[green]);
                    bgView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                  }
                 if(action.equals(ACTION_RGB_END)) {
                    autoSendFinishMessage();
                    finish();
                  }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.lcd2);

        bgs = new Drawable[] {
               // getResources().getDrawable(R.drawable.back),
                new ColorDrawable(Color.BLACK),
                new ColorDrawable(Color.WHITE),
                new ColorDrawable(Color.RED),
                new ColorDrawable(Color.BLUE),
                new ColorDrawable(Color.GREEN)//,
                //getResources().getDrawable(R.drawable.vcom)
        };

        bgView = (LinearLayout) findViewById(R.id.lcd_test2);
        bgView.setOnTouchListener(this);
        changeBackground();

        IntentFilter mIntentFilter = new IntentFilter();  
       // mIntentFilter.addAction(ACTION_RGB_BLACK);
        mIntentFilter.addAction(ACTION_RGB_WHITE);
        mIntentFilter.addAction(ACTION_RGB_RED);
        mIntentFilter.addAction(ACTION_RGB_GREEN);
        mIntentFilter.addAction(ACTION_RGB_BLUE);
        mIntentFilter.addAction(ACTION_RGB_END);
        registerReceiver(mReceiver2, mIntentFilter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(i==bgs.length){
              sendFinishMessage();
                    finish();
                }else
                    changeBackground();
                break;
            default:
                ;
        }
        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver2);
    }
    private void changeBackground() {
        FTLog.i(this, "Change color to " + String.format("%s", bgs[i].getClass().getName()));
        bgView.setBackground(bgs[i++]);
        //bgView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        /*if (i == colors.length)
            //i = 0;
            finish();*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        FTLog.d(this, "SFTT configuration changed in LCDTest2");
    }
    public void sendFinishMessage(){
           Intent intent= new Intent(LCDTest.LCD_SEND_RESULT_ACTION);
            sendBroadcast(intent);
    }
    public void autoSendFinishMessage(){
           Intent intent= new Intent(LCDTest.LCD_AUTO_SEND_RESULT_ACTION);
            sendBroadcast(intent);
    }
}
