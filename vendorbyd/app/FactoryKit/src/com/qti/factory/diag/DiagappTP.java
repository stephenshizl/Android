
/*===========================================================================

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110919	shenzhiyong    add TP test in CIT

===========================================================================*/

package com.qti.factory.diag;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Window;

import com.qti.factory.Utils;

public class DiagappTP extends Activity{

    String TAG = "DiagappTP";
    String resultString = "Failed";

    public static int lineNum = 0;
    public static boolean testFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//delete title
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//delete title bar

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);

        setContentView(new PaintView(this,myHandler));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        init();
    }

    public void init() {
        lineNum = 0;
        testFinish = false;
    }

    public void onPause(){
        super.onPause();
    }

    @Override
    public void finish() {
       // Utilities.writeCurMessage(this, TAG, resultString);
        super.finish();
    }

    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    disLineView();
                    break;
                default:
                    break;
            }
        }
    };

    public void disLineView() {
        if(testFinish) {
            Log.e(TAG,"in disLineView,lineNum="+lineNum);
            setContentView(new LineView(this, myHandler,lineNum));
            lineNum++;
        }
    }


    private Paint       mPaint;
    public class PaintView extends View {

        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint mGreenPaint;
        private Paint mWhitePaint;
        private Paint mBluePaint;
        private final int SQUARE_WIDTH = 100;

        private Handler mHandler;

        private static final int TOUCH_AREA_NONE = 0;
        private static final int TOUCH_AREA_LEFT_TOP = 1;
        private static final int TOUCH_AREA_LEFT = 2;
        private static final int TOUCH_AREA_LEFT_BOT = 3;
        private static final int TOUCH_AREA_BOTTOM = 4;
        private static final int TOUCH_AREA_RIGHT_BOT = 5;
        private static final int TOUCH_AREA_RIGHT = 6;
        private static final int TOUCH_AREA_RIGHT_TOP = 7;
        private static final int TOUCH_AREA_TOP = 8;

        private int mCurrTouchArea = TOUCH_AREA_NONE;
        private float mSlope=0.0f;

        public PaintView(Context c,Handler handler) {
            super(c);
            mWhitePaint = new Paint();
            mWhitePaint.setColor(Color.WHITE);
            mWhitePaint.setStyle(Paint.Style.STROKE);

            mGreenPaint = new Paint();
            mGreenPaint.setColor(Color.GREEN);
            mGreenPaint.setStyle(Paint.Style.STROKE);

            mBluePaint = new Paint();
            mBluePaint.setColor(Color.BLUE);
            mBluePaint.setStyle(Paint.Style.STROKE);

            mPath = new Path();
            mHandler = handler;
            testFinish = false;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            switch(mCurrTouchArea){
            case TOUCH_AREA_NONE:
                mSlope = (float)(getHeight()-SQUARE_WIDTH)/(float)(getWidth()-SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                break;
            case TOUCH_AREA_LEFT_TOP:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                if(mY > SQUARE_WIDTH) {
                    mCurrTouchArea = TOUCH_AREA_LEFT;
                }else if(mX >SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }
/*
                if(inBacklashRect(mX, mY)){
                    mCurrTouchArea = TOUCH_AREA_LEFT;
                }else if(mX>SQUARE_WIDTH || mY>SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }
*/
                break;
            case TOUCH_AREA_LEFT:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                if(mY>(getHeight()-SQUARE_WIDTH)){
                    mCurrTouchArea=TOUCH_AREA_LEFT_BOT;
                }else if(mX>SQUARE_WIDTH) {
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }
                break;
            case TOUCH_AREA_LEFT_BOT:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mWhitePaint, SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-SQUARE_WIDTH,getHeight()-1);

                if(mY < (getHeight()-SQUARE_WIDTH)){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }else if(mX > SQUARE_WIDTH){
                    mCurrTouchArea=TOUCH_AREA_BOTTOM;
                }
                break;
            case TOUCH_AREA_BOTTOM:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mWhitePaint, getWidth()-SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-1,getHeight()-1);
                if(mY<(getHeight()-SQUARE_WIDTH)){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }else if(mX>=(getWidth()-SQUARE_WIDTH)){
                    mCurrTouchArea = TOUCH_AREA_RIGHT_BOT;
                }
                break;
            case TOUCH_AREA_RIGHT_BOT:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-1,getHeight()-1);
                drawRect(canvas, mWhitePaint, getWidth()-SQUARE_WIDTH,
                        SQUARE_WIDTH, getWidth()-1,getHeight()-SQUARE_WIDTH);
                if(mY <= getHeight()-SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_RIGHT;
                }else if (mX < getWidth()-SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }
                break;
            case TOUCH_AREA_RIGHT:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-1,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        SQUARE_WIDTH, getWidth()-1,getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint, getWidth()-SQUARE_WIDTH,
                        0, getWidth()-1,SQUARE_WIDTH);

                if(mY<=SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_RIGHT_TOP;
                }else if(mX < getWidth()-SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }
                break;
            case TOUCH_AREA_RIGHT_TOP:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-1,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        SQUARE_WIDTH, getWidth()-1,getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        0, getWidth()-1,SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint, SQUARE_WIDTH,
                        0, getWidth()-SQUARE_WIDTH,SQUARE_WIDTH);

                if(mY>SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }else if(mX<=(getWidth()-SQUARE_WIDTH)){
                    mCurrTouchArea = TOUCH_AREA_TOP;
                }
                break;
            case TOUCH_AREA_TOP:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        0, SQUARE_WIDTH, SQUARE_WIDTH, getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, 0,
                        getHeight()-SQUARE_WIDTH, SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-SQUARE_WIDTH,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        getHeight()-SQUARE_WIDTH, getWidth()-1,getHeight()-1);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        SQUARE_WIDTH, getWidth()-1,getHeight()-SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, getWidth()-SQUARE_WIDTH,
                        0, getWidth()-1,SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint, SQUARE_WIDTH,
                        0, getWidth()-SQUARE_WIDTH,SQUARE_WIDTH);
                if(mX<=SQUARE_WIDTH){
/*
                    TouchPanelEdge.this.setResult(RESULT_OK);
                    resultString = Utilities.RESULT_PASS;
                    finish();
*/
                    testFinish = true;
                    mHandler.sendEmptyMessage(0);

                }else if(mY>SQUARE_WIDTH){
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }
                break;
            default:
                break;
            }
            canvas.drawPath(mPath, mPaint);
        }

        private boolean inBacklashRect(float x, float y){
            float minY = mSlope*(x-SQUARE_WIDTH);
            float maxY = mSlope*x+SQUARE_WIDTH;
            if(y>=minY&&y<=maxY)
                return true;
            else
                return false;
        }

        private boolean inSolidus(float x, float y){
            float minY = mSlope*(getWidth()-SQUARE_WIDTH-x);
            float maxY = getHeight()-mSlope*(x-SQUARE_WIDTH);
            if(y>=minY&&y<=maxY)
                return true;
            else
                return false;
        }

        private void drawLine(Canvas canvas, float x, float y){
            canvas.drawLine(mX, 0, mX, getHeight(), mBluePaint);
            canvas.drawLine(0, mY, getWidth(), mY, mBluePaint);
        }

        private void drawRect(Canvas canvas, Paint paint,
                int left, int top, int right, int bottom){
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, paint);
        }

        private void drawBacklashRect(Canvas canvas, Paint paint){
            canvas.drawLine(SQUARE_WIDTH, 0, 0, SQUARE_WIDTH, paint);
            canvas.drawLine(0, SQUARE_WIDTH, getWidth()-SQUARE_WIDTH, getHeight(), paint);
            canvas.drawLine(getWidth()-SQUARE_WIDTH, getHeight(),
                    getWidth(), getHeight()-SQUARE_WIDTH, paint);
            canvas.drawLine(getWidth(), getHeight()-SQUARE_WIDTH, SQUARE_WIDTH, 0, paint);
        }

        private void drawSolidusRect(Canvas canvas, Paint paint){
            canvas.drawLine(0, getHeight()-SQUARE_WIDTH, SQUARE_WIDTH, getHeight(), paint);
            canvas.drawLine(SQUARE_WIDTH, getHeight(), getWidth(), SQUARE_WIDTH, paint);
            canvas.drawLine(getWidth(), SQUARE_WIDTH, getWidth()-SQUARE_WIDTH, 0, paint);
            canvas.drawLine(getWidth()-SQUARE_WIDTH, 0, 0, getHeight()-SQUARE_WIDTH, paint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            if(x<SQUARE_WIDTH&&y<SQUARE_WIDTH){
                if(mCurrTouchArea == TOUCH_AREA_NONE){
                    mCurrTouchArea = TOUCH_AREA_LEFT_TOP;
                }
            }
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mCurrTouchArea=TOUCH_AREA_NONE;
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
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
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    public class LineView extends View {

        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint mGreenPaint;
        private Paint mWhitePaint;
        private Paint mBluePaint;
        private final int SQUARE_WIDTH = 50;

        private Handler mHandler;

        private static final int TOUCH_AREA_NONE = 0;
        private static final int TOUCH_AREA_LEFT = 1;
        private static final int TOUCH_AREA_CENTER = 2;
        private static final int TOUCH_AREA_RIGHT = 3;

        private int mCurrTouchArea = TOUCH_AREA_NONE;
        private float mSlope=0.0f;

        private int mlineNum = 0;
        private int height = 0;
        private int screenScale[] = {1, 3, 5, 7};

        public LineView(Context c,Handler handler ,int line) {
            super(c);
            mWhitePaint = new Paint();
            mWhitePaint.setColor(Color.WHITE);
            mWhitePaint.setStyle(Paint.Style.STROKE);

            mGreenPaint = new Paint();
            mGreenPaint.setColor(Color.GREEN);
            mGreenPaint.setStyle(Paint.Style.STROKE);

            mBluePaint = new Paint();
            mBluePaint.setColor(Color.BLUE);
            mBluePaint.setStyle(Paint.Style.STROKE);

            mPath = new Path();

            mHandler = handler;
            mlineNum = line;
            testFinish = false;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            height = ((getHeight()/8)*screenScale[mlineNum])-SQUARE_WIDTH/2;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            switch(mCurrTouchArea){
            case TOUCH_AREA_NONE:
                drawRect(canvas, mWhitePaint,
                        0, height, SQUARE_WIDTH, height+SQUARE_WIDTH);
                break;
            case TOUCH_AREA_LEFT:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, height, SQUARE_WIDTH, height+SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint,
                        SQUARE_WIDTH, height, getWidth()-SQUARE_WIDTH, height+SQUARE_WIDTH);
                if((mY < height) || (mY > height+SQUARE_WIDTH)) {
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }else if(mX>SQUARE_WIDTH) {
                    mCurrTouchArea = TOUCH_AREA_CENTER;
                }
                break;
            case TOUCH_AREA_CENTER:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, height, SQUARE_WIDTH, height+SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        SQUARE_WIDTH, height, getWidth()-SQUARE_WIDTH, height+SQUARE_WIDTH);
                drawRect(canvas, mWhitePaint,
                        getWidth()-SQUARE_WIDTH, height, getWidth()-1, height+SQUARE_WIDTH);
                if((mY < height) || (mY > height+SQUARE_WIDTH)) {
                    mCurrTouchArea = TOUCH_AREA_NONE;
                }else if(mX>getWidth()-SQUARE_WIDTH) {
                    mCurrTouchArea = TOUCH_AREA_RIGHT;
                }
                break;
            case TOUCH_AREA_RIGHT:
                drawLine(canvas, mX, mY);
                drawRect(canvas, mGreenPaint,
                        0, height, SQUARE_WIDTH, height+SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        SQUARE_WIDTH, height, getWidth()-SQUARE_WIDTH, height+SQUARE_WIDTH);
                drawRect(canvas, mGreenPaint,
                        getWidth()-SQUARE_WIDTH, height, getWidth()-1, height+SQUARE_WIDTH);
                if(lineNum < 4)
                {
                    testFinish = true;
                    mHandler.sendEmptyMessage(0);
                }else {
                    DiagappTP.this.setResult(RESULT_OK);
               //     resultString = Utilities.RESULT_PASS;
                    finish();
                }
                break;
            default:
                break;
            }
            canvas.drawPath(mPath, mPaint);
        }

        private void drawLine(Canvas canvas, float x, float y){
            canvas.drawLine(mX, 0, mX, getHeight(), mBluePaint);
            canvas.drawLine(0, mY, getWidth(), mY, mBluePaint);
        }

        private void drawRect(Canvas canvas, Paint paint,
                int left, int top, int right, int bottom){
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, paint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            if(x<SQUARE_WIDTH&&y>height&&y<height+SQUARE_WIDTH){
                if(mCurrTouchArea == TOUCH_AREA_NONE){
                    mCurrTouchArea = TOUCH_AREA_LEFT;
                }
            }
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mCurrTouchArea=TOUCH_AREA_NONE;
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
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
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}


