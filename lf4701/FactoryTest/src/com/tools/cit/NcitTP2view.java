

/*===========================================================================

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110919    shenzhiyong    add TP test in CIT

===========================================================================*/

package com.tools.cit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class NcitTP2view extends View  {

    private final ViewConfiguration mVC;
    private Paint mWhitePaint;
    private Paint mLeftTopPaint;
    private Paint mLeftBottomPaint;
    private Paint mRightTopPaint;
    private Paint mRightBottomPaint;
    private Paint mBottomPaint;
    private Path path1;
    private Path path2;
    private final Paint mPaint;
    private final Paint mTargetPaint;
    private final Paint mPathPaint;
    private boolean mCurDown;
    private int mCurNumPointers;
    private int mMaxNumPointers;
    private Activity mActivity;
    private int iFlag = 0;
    final int INCREACE_WIDTH = 80; //modify by liugang for gg401 20120905
    public final ArrayList<PointerState> mPointers
             = new ArrayList<PointerState>();
    
    public NcitTP2view(Activity activity) {
        super(activity);
        setFocusable(true);
        mActivity = activity;
        mVC = ViewConfiguration.get(activity);
        
        mBottomPaint = new Paint();
        mBottomPaint.setColor(Color.WHITE);
        mBottomPaint.setStyle(Paint.Style.STROKE);
        mLeftTopPaint = new Paint();
        mLeftTopPaint.setStyle(Paint.Style.STROKE);
        mLeftTopPaint.setAntiAlias(true);
        mLeftTopPaint.setColor(Color.WHITE);
        mLeftBottomPaint = new Paint();
        mLeftBottomPaint.setColor(Color.WHITE);
        mLeftBottomPaint.setStyle(Paint.Style.STROKE);
        mRightTopPaint = new Paint();
        mRightTopPaint.setColor(Color.WHITE);
        mRightTopPaint.setStyle(Paint.Style.STROKE);
        mRightBottomPaint = new Paint();
        mRightBottomPaint.setColor(Color.WHITE);
        mRightBottomPaint.setStyle(Paint.Style.STROKE);
       
        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setStyle(Paint.Style.STROKE);
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        
        mTargetPaint = new Paint();
        mTargetPaint.setAntiAlias(false);
        mTargetPaint.setARGB(255, 0, 0, 192);
        
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(false);
        mPathPaint.setARGB(255, 0, 96, 255);
        mPathPaint.setStrokeWidth(2);
        
        PointerState ps = new PointerState();
        ps.mVelocity = VelocityTracker.obtain();
        mPointers.add(ps);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
         this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        synchronized (mPointers) {
            int action = event.getAction();
            int NP = mPointers.size();
            if (action == MotionEvent.ACTION_DOWN) {
                for (int p=0; p<NP; p++) {
                    final PointerState ps = mPointers.get(p);
                    ps.mXs.clear();
                    ps.mYs.clear();
                    ps.mVelocity = VelocityTracker.obtain();
                    ps.mCurDown = false;
                }
                mPointers.get(0).mCurDown = true;
                mMaxNumPointers = 0;
            }
            
            if ((action&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                final int index = (action&MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int id = event.getPointerId(index);
                while (NP <= id) {
                    PointerState ps = new PointerState();
                    ps.mVelocity = VelocityTracker.obtain();
                    mPointers.add(ps);
                    NP++;
                }
                final PointerState ps = mPointers.get(id);
                ps.mVelocity = VelocityTracker.obtain();
                ps.mCurDown = true;
            }
            
            final int NI = event.getPointerCount();
            mCurDown = action != MotionEvent.ACTION_UP
                    && action != MotionEvent.ACTION_CANCEL;
            mCurNumPointers = mCurDown ? NI : 0;
            if (mMaxNumPointers < mCurNumPointers) {
                mMaxNumPointers = mCurNumPointers;
            }
            if(NI == 1){
                iFlag = 1;
            }
            
            for (int i=0; i<NI; i++) {
                final int id = event.getPointerId(i);
                final PointerState ps = mPointers.get(id);
                ps.mVelocity.addMovement(event);
                ps.mVelocity.computeCurrentVelocity(1);
                final int N = event.getHistorySize();
                for (int j=0; j<N; j++) {
                  
                    ps.mXs.add(event.getHistoricalX(i, j));
                    ps.mYs.add(event.getHistoricalY(i, j));
                }
                ps.mXs.add(event.getX(i));
                ps.mYs.add(event.getY(i));
                ps.mCurX = (int)event.getX(i);
                ps.mCurY = (int)event.getY(i);
                
                ps.mCurPressure = event.getPressure(i);
                ps.mCurSize = event.getSize(i);
                ps.mCurWidth = (int)(ps.mCurSize*(getWidth()/3));
            }
            
            if ((action&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                final int index = (action&MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int id = event.getPointerId(index);
                final PointerState ps = mPointers.get(id);
                ps.mXs.add(Float.NaN);
                ps.mYs.add(Float.NaN);
                ps.mCurDown = false;
            }
            
            if (action == MotionEvent.ACTION_UP) {
                for (int i=0; i<NI; i++) {
                    final int id = event.getPointerId(i);
                    final PointerState ps = mPointers.get(id);
                    if (ps.mCurDown) {
                        ps.mCurDown = false;
                    }
                }
            }
            
            postInvalidate();
        }

        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
          this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        synchronized (mPointers) {
            final int NP = mPointers.size();
            
            path1 = new Path();
            path2 = new Path();
            canvas.drawRect(0, 0, INCREACE_WIDTH, INCREACE_WIDTH, mLeftTopPaint);
            if (NP > 0) {
                path1.moveTo(0, getHeight()-INCREACE_WIDTH);
                path1.lineTo(getWidth()-INCREACE_WIDTH, 0);
                path1.lineTo(getWidth(), INCREACE_WIDTH);
                path1.lineTo(INCREACE_WIDTH, getHeight());
                path1.close();
                
                path2.moveTo(getWidth()-INCREACE_WIDTH, getHeight());
                path2.lineTo(0, INCREACE_WIDTH);
                path2.lineTo(INCREACE_WIDTH, 0);
                path2.lineTo(getWidth(), getHeight()-INCREACE_WIDTH);
                path2.close();
               
            }
            for (int p=0; p<NP; p++) {
                final PointerState ps = mPointers.get(p);
                
                if (mCurDown && ps.mCurDown) {
                    canvas.drawLine(0, (int)ps.mCurY, getWidth(), (int)ps.mCurY, mTargetPaint);
                    canvas.drawLine((int)ps.mCurX, 0, (int)ps.mCurX, getHeight(), mTargetPaint);
                    int pressureLevel = (int)(ps.mCurPressure*255);
                    mPaint.setARGB(255, pressureLevel, 128, 255-pressureLevel);
                    canvas.drawPoint(ps.mCurX, ps.mCurY, mPaint);
                    canvas.drawCircle(ps.mCurX, ps.mCurY, ps.mCurWidth, mPaint);
                }
            }
            
            for (int p=0; p<NP; p++) {
                final PointerState ps = mPointers.get(p);
                int flag=0;
                final int N = ps.mXs.size();
                float lastX=0, lastY=0;
                boolean haveLast = false;
                boolean drawn = false;
                mPaint.setARGB(255, 128, 255, 255);
                for (int i=0; i<N; i++) {
                    float x = ps.mXs.get(i);
                    float y = ps.mYs.get(i);
                    if (Float.isNaN(x)) {
                        haveLast = false;
                        iFlag = 0;
                        continue;
                    }
                    if (haveLast && iFlag == 1) {
                        
                       if(x<INCREACE_WIDTH&&y<INCREACE_WIDTH){
                           mLeftTopPaint.setColor(Color.GREEN);
                            canvas.drawPath(path2, mWhitePaint);
                            flag = 1;
                        }
                        if(isContaintLeftLines(x, y)&&flag==1){
                            mWhitePaint.setColor(Color.GREEN);
                            canvas.drawRect(getWidth()-INCREACE_WIDTH, getHeight()-INCREACE_WIDTH, getWidth(), getHeight(), mRightBottomPaint);
                            flag = 2;
                            }
                        if((!isContaintLeftLines(x, y))&&flag==2){
                            
                            flag = 3;
                        }
                        if(x>(getWidth()-INCREACE_WIDTH)&&y>(getHeight()-INCREACE_WIDTH)){
                            if(flag==3){
                                mRightBottomPaint.setColor(Color.WHITE);
                                flag = 4;
                            }
                            if(flag==2){
                            mRightBottomPaint.setColor(Color.GREEN);
                            canvas.drawRect(0, getHeight()-INCREACE_WIDTH, getWidth(), getHeight(), mBottomPaint);
                            flag = 5;
                            }
                        }
                        
                        if(x>0&&x<getWidth()&&y>(getHeight()-INCREACE_WIDTH)&&y<getHeight()&&flag==5){
                            mBottomPaint.setColor(Color.GREEN);
                            canvas.drawRect(0, getHeight()-INCREACE_WIDTH, INCREACE_WIDTH, getHeight(), mLeftBottomPaint);
                            flag = 6;
                        }
                        if(!(x>0&&x<getWidth()&&y>(getHeight()-INCREACE_WIDTH)&&y<getHeight())&&flag==6){
                            flag = 7;
                        }
                        if(x>0&&x<INCREACE_WIDTH&&y>(getHeight()-INCREACE_WIDTH)&&y<getHeight()){
                            if(flag==7){
                                mLeftBottomPaint.setColor(Color.WHITE);
                                flag =8;
                            }
                            if(flag==6){
                                mLeftBottomPaint.setColor(Color.GREEN);
                                canvas.drawPath(path1, mWhitePaint);
                                flag = 9;
                            }
                        }
                        
                        if(isContaintRightLines(x, y)&&flag==9){
                            canvas.drawRect(getWidth()-INCREACE_WIDTH, 0, getWidth(), INCREACE_WIDTH, mRightTopPaint);
                            flag = 10;
                        }
                        if((!isContaintRightLines(x, y))&&flag==10){
                            flag = 11;
                        }
                        if(x>(getWidth()-INCREACE_WIDTH)&&x<getWidth()&&y>0&&y<INCREACE_WIDTH){
                            if(flag==11){
                                mRightTopPaint.setColor(Color.WHITE);
                                flag = 12;
                            }
                            if(flag==10){
                                mRightTopPaint.setColor(Color.GREEN);
                                flag = 18;
                            }
                        }
                     
                        if(flag==18){
                            iFlag = 0;
                            mActivity.finish();
                        }
                        canvas.drawLine(lastX, lastY, x, y, mPathPaint);
                        //canvas.drawPoint(lastX, lastY, mPaint);
                        drawn = true;
                    }
                    lastX = x;
                    lastY = y;
                    haveLast = true;
                }
                
                if (drawn) {
                    if (ps.mVelocity != null) {
                        mPaint.setARGB(255, 255, 64, 128);
                        float xVel = ps.mVelocity.getXVelocity() * (1000/60);
                        float yVel = ps.mVelocity.getYVelocity() * (1000/60);
                        canvas.drawLine(lastX, lastY, lastX+xVel, lastY+yVel, mPaint);
                    } else {
                        canvas.drawPoint(lastX, lastY, mPaint);
                    }
                }
            }
        }
    }

    public static class PointerState {
        private final ArrayList<Float> mXs = new ArrayList<Float>();
        private final ArrayList<Float> mYs = new ArrayList<Float>();
        private boolean mCurDown;
        private int mCurX;
        private int mCurY;
        private float mCurPressure;
        private float mCurSize;
        private int mCurWidth;
        private VelocityTracker mVelocity;
    }

    public boolean isContaintLeftLines(float x,float y){
       boolean flag = false;
       final float toLeft;
       final float toRight;
       final float topX1 = 0.0f;
       final float topX2 = INCREACE_WIDTH;
       final float topY1 = INCREACE_WIDTH;
       final float topY2 = 0.0f;
       final float bottomX1 = getWidth()-INCREACE_WIDTH;
       final float bottomX2 = getWidth();
       final float bottomY1 = getHeight();
       final float bottomY2 = getHeight()-INCREACE_WIDTH;
       final float dis = (float) Math.hypot(INCREACE_WIDTH,INCREACE_WIDTH);
       
       float lineLeft = (float) Math.hypot((bottomX1-topX1), (bottomY1-topY1));
       float toLeftTop = (float) Math.hypot((x-topX1), (y-topY1));
       float toLeftBottom = (float) Math.hypot((x-bottomX1), (y-bottomY1));
       float P1 = (lineLeft+toLeftTop+toLeftBottom)/2;
       float s1 = (float) Math.sqrt( P1*(P1-lineLeft)*(P1-toLeftTop)*(P1-toLeftBottom));
       toLeft = 2*s1/lineLeft;
       
       float lineRight = lineLeft;
       float toRightTop = (float) Math.hypot((x-topX2), (y-topY2));
       float toRightBottom = (float) Math.hypot((x-bottomX2), (y-bottomY2));
       float P2 = (lineRight+toRightTop+toRightBottom)/2;
       float s2 = (float) Math.sqrt(P2*(P2-lineRight)*(P2-toRightTop)*(P2-toRightBottom));
       toRight = 2*s2/lineRight;
       
       if((toLeft+toRight)>dis) flag = false;
       else flag =true;

       return flag;
    }

   private boolean isContaintRightLines(float x,float y){
       boolean flag = false;
       final float toLeft;
       final float toRight;
       
       final float topX1 = getWidth()-INCREACE_WIDTH;
       final float topX2 = getWidth();
       final float topY1 = 0.0f;
       final float topY2 = INCREACE_WIDTH;
       final float bottomX1 = 0.0f;
       final float bottomX2 = INCREACE_WIDTH;
       final float bottomY1 = getHeight()-INCREACE_WIDTH;
       final float bottomY2 = getHeight();
       final float dis = (float) Math.hypot(INCREACE_WIDTH,INCREACE_WIDTH);
       
       float lineLeft = (float) Math.hypot((bottomX1-topX1), (bottomY1-topY1));
       float toLeftTop = (float) Math.hypot((x-topX1), (y-topY1));
       float toLeftBottom = (float) Math.hypot((x-bottomX1), (y-bottomY1));
       float P1 = (lineLeft+toLeftTop+toLeftBottom)/2;
       float s1 = (float) Math.sqrt( P1*(P1-lineLeft)*(P1-toLeftTop)*(P1-toLeftBottom));
       toLeft = 2*s1/lineLeft;
       
       float lineRight = lineLeft;
       float toRightTop = (float) Math.hypot((x-topX2), (y-topY2));
       float toRightBottom = (float) Math.hypot((x-bottomX2), (y-bottomY2));
       float P2 = (lineRight+toRightTop+toRightBottom)/2;
       float s2 = (float) Math.sqrt(P2*(P2-lineRight)*(P2-toRightTop)*(P2-toRightBottom));
       toRight = 2*s2/lineRight;
       
       if((toLeft+toRight)>dis) flag = false;
       else flag =true;
       
       return flag;
   }

}

