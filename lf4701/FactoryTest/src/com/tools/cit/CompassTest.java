/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tools.cit;

import com.tools.util.FTLog;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.os.SystemProperties;
import android.widget.Button;
import android.view.WindowManager;
public class CompassTest extends TestModule implements SensorEventListener{

    private static final String MODULE_NAME = "Compass";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private CompassView mView;
    private float[] mValues;
    private String Direction[] = new String[8];
    private String config;
    private boolean CHANGE = true;
    private int rangex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.compass, R.drawable.compass);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
       config=SystemProperties.get("ro.build.lcd_size");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        FTLog.i(this, "Orientation sensor is: " + (mSensor == null ? "NULL" : mSensor.getName()));
        if (mSensor == null){
            //setTestResult(FAIL);
            ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        }
        initDirection(this);
        mView = new CompassView(this);
        ((LinearLayout) findViewById((R.id.compass_layout))).addView(mView);
    }

    @Override
    protected void onResume()
    {
        FTLog.d(this, "onResume");
        super.onResume();
        if (mSensor != null)
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop()
    {
        FTLog.d(this, "onStop");
        if (mSensor != null)
            mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }
    public void initDirection(Context mcontext){
        Direction[0]=mcontext.getString(R.string.direction1);
        Direction[1]=mcontext.getString(R.string.direction2);
        Direction[2]=mcontext.getString(R.string.direction3);
        Direction[3]=mcontext.getString(R.string.direction4);
        Direction[4]=mcontext.getString(R.string.direction5);
        Direction[5]=mcontext.getString(R.string.direction6);
        Direction[6]=mcontext.getString(R.string.direction7);
        Direction[7]=mcontext.getString(R.string.direction8);
    }
    public class CompassView extends View {
        private Paint mPaint = new Paint();
        private Paint paint2=new Paint();
        private Path mPath = new Path();
        private Path mPath2 = new Path();
        private boolean mAnimate;
        private final int MIN_WIDTH = 40;
        private final int MIN_HEIGHT = 120;
     

        public CompassView(Context context) {
            super(context);
            compassViewInit();
        }

        public CompassView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            compassViewInit();
        }

        public CompassView(Context context, AttributeSet attrs) {
            super(context, attrs);
            compassViewInit();
        }

        private void compassViewInit() {
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();
          
            setMinimumWidth(MIN_WIDTH);
            setMinimumHeight(MIN_HEIGHT);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            FTLog.i(this, "onDraw");
            Paint paint = mPaint;
        
            canvas.drawColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);

            int w = getWidth();
            int h = getHeight();
            FTLog.d(this, "Canvas width:" + w + ", height: " + h);
            FTLog.d(this, "View width:" + getWidth() + ", height: " + getHeight());
            int cx = w / 2;
            int cy = h / 2;

            paint2.setTextSize(28f);
            paint2.setAntiAlias(true);
            paint2.setColor(Color.RED);
            paint2.setStyle(Paint.Style.STROKE);
            mPath2.moveTo(cx, 0);
            mPath2.lineTo(cx-40,20 );
            mPath2.lineTo(cx+40, 20);
            mPath2.lineTo(cx, 0);
            mPath2.close();
            canvas.drawPath(mPath2, mPaint);         
            float mvalue1=0;
            if (mValues != null) {
                mvalue1=mValues[0];
                if(mvalue1<0){
                    mvalue1=mvalue1+360f;
                }
                float mvalue=mvalue1;        
                if(mvalue>=360f){
                    mvalue=mvalue-360f;
                }
                int index=(int)(mvalue/45f);         
                canvas.drawText(Direction[index]+" "+(int)mvalue1 , cx-40, 50, paint2);
            }

            canvas.translate(cx, cy);
        
            if (mValues != null) {
                canvas.rotate(-(int)mvalue1);
            }
            canvas.drawPath(mPath, mPaint);    
            drawCompass(canvas, cx/2, paint2);

        }
    public void drawCompass(Canvas canvas,int radius,Paint paint){

         int mradius=radius*2;
         int distance = 50;
         canvas.drawCircle(0, 0, mradius, paint); 
         canvas.drawText(Direction[0], (float)(0-getTextWidth(paint,Direction[0])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[1], (float)(0-getTextWidth(paint,Direction[1])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[2], (float)(0-getTextWidth(paint,Direction[2])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[3], (float)(0-getTextWidth(paint,Direction[3])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[4], (float)(0-getTextWidth(paint,Direction[4])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[5], (float)(0-getTextWidth(paint,Direction[5])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[6], (float)(0-getTextWidth(paint,Direction[6])/2), -radius - distance, paint);
         canvas.rotate(45f);
         canvas.drawText(Direction[7], (float)(0-getTextWidth(paint,Direction[7])/2), -radius - distance, paint);
         canvas.rotate(45f);

            
    }
    public float getTextWidth(Paint paint,String mstring){
        return paint.measureText(mstring);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        mAnimate = true;
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAnimate = false;
        super.onDetachedFromWindow();
    }
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        FTLog.d(this, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
        ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
        if(CHANGE) {
            rangex = (int)event.values[0];
            CHANGE = false;
        } else {
            if(rangex - (int)event.values[0] >= 1 || rangex - (int)event.values[0] <= -1) {                
                mValues = event.values;
                if (mView != null) {
                    try { 
                        Thread.sleep(20); 
                    } catch (InterruptedException e) { 
                        e.printStackTrace(); 
                    }
                    ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                    mView.invalidate();
                }
                CHANGE = true;
            }
        }
       
        /*if(((mvalue2<20f)&&(mvalue2>=0f))||((mvalue2>340f)&&(mvalue2<=360f))){
             setTestResult(PASS);
            ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
            
        }*/
   
    }
}
