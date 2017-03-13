
package com.tools.cit;

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
/*
    public void autoSendFinishMessage(){
           Intent intent= new Intent(LCDTest.LCD_AUTO_SEND_RESULT_ACTION);
            sendBroadcast(intent);
    }
*/
}
