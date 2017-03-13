
package com.tools.cit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.RectF;
import android.graphics.Rect;
import android.widget.ImageView;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.Button;

public class AccelerometerTest extends TestModule implements SensorEventListener {
    private static String MODULE_NAME = "Accelerometer";
    private SensorManager mSensorManager;
    private Sensor mAcceler;
    private GSensitiveView mGraphView;
    private LinearLayout mLinearLayout;

    private TextView txtAccelerX;
    private TextView txtAccelerY;
    private TextView txtAccelerZ;

    private Button btnPass;
    //add by qianyan LF4701Q_P000060 auto pass by Standard
    private float pre_x = 0;
    private float pre_y = 0;
    private float pre_z = 0;
    private boolean firstCome = true;
    private boolean autoPass[] = {false, false, false};
    //end add by qianyan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.accelerometer, R.drawable.accelerometer);
        //super.onCreate(savedInstanceState);
        
        txtAccelerX = (TextView) findViewById(R.id.txt_acceler_x);
        txtAccelerY = (TextView) findViewById(R.id.txt_acceler_y);
        txtAccelerZ = (TextView) findViewById(R.id.txt_acceler_z);
        mLinearLayout = (LinearLayout) findViewById(R.id.accview);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceler = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(false);        

        if (mAcceler == null) {
            txtAccelerX.setText(R.string.na);
            txtAccelerY.setText(R.string.na);
            txtAccelerZ.setText(R.string.na);
            setTestResult(FAIL);// sensor wasn't detected
        }
        else{
            //mGraphView = new GraphView(this);
            mGraphView = new GSensitiveView(this);
            //setContentView(mGraphView);
            mGraphView.setVisibility(View.VISIBLE);
            mLinearLayout.addView(mGraphView);
            
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAcceler != null){
            mSensorManager.registerListener(this, mAcceler, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mAcceler != null){
            mSensorManager.unregisterListener(this);
            //mSensorManager.unregisterListener(mGraphView);
        }
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        
        txtAccelerX.setText(event.values[0] + " m/s^2");
        txtAccelerY.setText(event.values[1] + " m/s^2");
        txtAccelerZ.setText(event.values[2] + " m/s^2");
        
        // FTLog.i(this,"accuracy: "+event.accuracy+", value: "+event.values[0]);
        float[] values = event.values;
        float ax = values[0];
        float ay = values[1];

        double g = Math.sqrt(ax * ax + ay * ay);
        double cos = ay / g;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rad = Math.acos(cos);
        if (ax < 0) {
            rad = 2 * Math.PI - rad;
        }

        int uiRot = getWindowManager().getDefaultDisplay().getRotation();
        double uiRad = Math.PI / 2 * uiRot;
        rad -= uiRad;

        mGraphView.setRotation(rad);

        int degrees = (int) (180 * rad / Math.PI);

        //add by qianyan LF4701Q_P000060 auto pass by Standard
        if(autoPass[0] && autoPass[1] && autoPass[2]){
            setTestResult(PASS);
            btnPass.setEnabled(true);
        }

        if(firstCome) {
            pre_x = event.values[0];
            pre_y = event.values[1];
            pre_z = event.values[2];
            firstCome = false;
        }
        else {
            if(pre_x - event.values[0] >=3 || pre_x - event.values[0] <= -3) {
                autoPass[0] = true;
            }
            if(pre_y - event.values[1] >=3 || pre_y - event.values[1] <= -3) {
                autoPass[1] = true;
            }
            if(pre_z - event.values[2] >=3 || pre_z - event.values[2] <= -3) {
                autoPass[2] = true;
            }
                
        }
        // end add by qianyan
        
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    private static class GSensitiveView extends View {

        private Bitmap image;
        private double rotation;
        private Paint paint;

        public GSensitiveView(Context context) {
            super(context);
            BitmapDrawable drawble = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_arrow_up1);
            image = drawble.getBitmap();

            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            double w = image.getWidth();
            double h = image.getHeight();

            Rect rect = new Rect();
            getDrawingRect(rect);

            int degrees = (int) (180 * rotation / Math.PI);
            canvas.rotate(degrees + 180, rect.width() / 2, rect.height() / 2);
            canvas.drawBitmap(image, //
                    (float) ((rect.width() - w) / 2),//  
                    (float) ((rect.height() - h) / 2),//  
                    paint);
        }

        public void setRotation(double rad) {
            rotation = rad;
            invalidate();
        }

    }

    private class GraphView extends View implements SensorEventListener
    {
        private Bitmap  mBitmap;
        private Paint   mPaint = new Paint();
        private Canvas  mCanvas = new Canvas();
        private Path    mPath = new Path();
        private RectF   mRect = new RectF();
        private float   mLastValues[] = new float[3*2];
        private float   mOrientationValues[] = new float[3];
        private int     mColors[] = new int[3*2];
        private float   mLastX;
        private float   mScale[] = new float[2];
        private float   mYOffset;
        private float   mMaxX;
        private float   mSpeed = 1.0f;
        private float   mWidth;
        private float   mHeight;

        public GraphView(Context context) {
            super(context);
            mColors[0] = Color.argb(192, 255, 64, 64);
            mColors[1] = Color.argb(192, 64, 128, 64);
            mColors[2] = Color.argb(192, 64, 64, 255);
            mColors[3] = Color.argb(192, 64, 255, 255);
            mColors[4] = Color.argb(192, 128, 64, 128);
            mColors[5] = Color.argb(192, 255, 255, 64);

            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mRect.set(-0.5f, -0.5f, 0.5f, 0.5f);
            mPath.arcTo(mRect, 0, 180);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(0xFFFFFFFF);
            mYOffset = h * 0.5f;
            mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
            mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            mWidth = w;
            mHeight = h;
            if (mWidth < mHeight) {
                mMaxX = w;
            } else {
                mMaxX = w-50;
            }
            mLastX = mMaxX;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            synchronized (this) {
                if (mBitmap != null) {
                    final Paint paint = mPaint;
                    final Path path = mPath;
                    final int outer = 0xFFC0C0C0;
                    final int inner = 0xFFff7010;

                    if (mLastX >= mMaxX) {
                        mLastX = 0;
                        final Canvas cavas = mCanvas;
                        final float yoffset = mYOffset;
                        final float maxx = mMaxX;
                        final float oneG = SensorManager.STANDARD_GRAVITY * mScale[0];
                        paint.setColor(0xFFAAAAAA);
                        cavas.drawColor(0xFFFFFFFF);
                        cavas.drawLine(0, yoffset,      maxx, yoffset,      paint);
                        cavas.drawLine(0, yoffset+oneG, maxx, yoffset+oneG, paint);
                        cavas.drawLine(0, yoffset-oneG, maxx, yoffset-oneG, paint);
                    }
                    canvas.drawBitmap(mBitmap, 0, 0, null);

                    float[] values = mOrientationValues;
                    if (mWidth < mHeight) {
                        float w0 = mWidth * 0.333333f;
                        float w  = w0 - 32;
                        float x = w0*0.5f;
                        for (int i=0 ; i<3 ; i++) {
                            canvas.save(Canvas.MATRIX_SAVE_FLAG);
                            canvas.translate(x, w*0.5f + 4.0f);
                            canvas.save(Canvas.MATRIX_SAVE_FLAG);
                            paint.setColor(outer);
                            canvas.scale(w, w);
                            canvas.drawOval(mRect, paint);
                            canvas.restore();
                            canvas.scale(w-5, w-5);
                            paint.setColor(inner);
                            canvas.rotate(-values[i]);
                            canvas.drawPath(path, paint);
                            canvas.restore();
                            x += w0;
                        }
                    } else {
                        float h0 = mHeight * 0.333333f;
                        float h  = h0 - 32;
                        float y = h0*0.5f;
                        for (int i=0 ; i<3 ; i++) {
                            canvas.save(Canvas.MATRIX_SAVE_FLAG);
                            canvas.translate(mWidth - (h*0.5f + 4.0f), y);
                            canvas.save(Canvas.MATRIX_SAVE_FLAG);
                            paint.setColor(outer);
                            canvas.scale(h, h);
                            canvas.drawOval(mRect, paint);
                            canvas.restore();
                            canvas.scale(h-5, h-5);
                            paint.setColor(inner);
                            canvas.rotate(-values[i]);
                            canvas.drawPath(path, paint);
                            canvas.restore();
                            y += h0;
                        }
                    }

                }
            }
        }

        public void onSensorChanged(SensorEvent event) {
            //Log.d(TAG, "sensor: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
            synchronized (this) {
                if (mBitmap != null) {
                    final Canvas canvas = mCanvas;
                    final Paint paint = mPaint;
                    if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                        for (int i=0 ; i<3 ; i++) {
                            mOrientationValues[i] = event.values[i];
                        }
                    } else {
                        float deltaX = mSpeed;
                        float newX = mLastX + deltaX;

                        int j = (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) ? 1 : 0;
                        for (int i=0 ; i<3 ; i++) {
                            int k = i+j*3;
                            final float v = mYOffset + event.values[i] * mScale[j];
                            paint.setColor(mColors[k]);
                            canvas.drawLine(mLastX, mLastValues[k], newX, v, paint);
                            mLastValues[k] = v;
                        }
                        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                            mLastX += mSpeed;
                    }
                    invalidate();
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

}
