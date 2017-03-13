
package com.tools.cit;

import com.tools.util.FTLog;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Intent;

public class TouchTest2 extends Activity {
    private Paint mPaint;
    private boolean bExit;
    protected LockPatternView mLockPatternView;      

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Begin Delete by liugang for W101HM 20130807
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
        setContentView(new MyView(this));
        */
        /*Begin add by liugang for W101HM 20130807*/
        setContentView(R.layout.auto_touch);
        mLockPatternView = (LockPatternView) findViewById(R.id.auto_touchview);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        /*End add*/
      
    }

    /*Begin add by liugang for W101HM 20130807*/
    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {

        }

        public void onPatternCleared() {

        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
              
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

        }

        private void patternInProgress() {

        }
        public void onTouchFinished() {
          if(!bExit){
            bExit = true;
            Intent result = new Intent();
            result.putExtra("result", bExit);
            setResult(RESULT_OK,result);
            finish();
          }
        }
    };    
    /*End add*/
    public class MyView extends View {

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;

        public MyView(Context c) {
            super(c);
            // Detect the screen size
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
            FTLog.d(this, "Screen size: " + dm.widthPixels + "x" + dm.heightPixels);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            canvas.drawPath(mPath, mPaint);
        }

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
        }

        private void touch_move(float x, float y) {
            mPath.lineTo(x, y);
        }

        private void touch_up(float x, float y) {
            mCanvas.drawPath(mPath, mPaint);
            mCanvas.drawPoint(x, y, mPaint);
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up(x, y);
                    invalidate();
                    break;
            }
            return true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        FTLog.d(this, "SFTT configuration changed in TouchTest2");
    }
}
