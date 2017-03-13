

/*===========================================================================

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110919    shenzhiyong    add TP test in CIT

===========================================================================*/

package com.tools.customercit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View;

public class NcitTPview extends View {

    private final ViewConfiguration mVC;
    private Paint mWhitePaint;
    private Paint mLeftTopPaint;
    private Paint mLeftBottomPaint;
    private Paint mRightTopPaint;
    private Paint mRightBottomPaint;
    private Paint mLeftPaint;
    private Paint mTopPaint;
    private Paint mRightPaint;
    private Paint mBottomPaint;

    //Begin add by liugang 20120911 for GG401
    /*
    private Paint mCenterPaint;
    private Paint mCenterTopPaint;
    private Paint mCenterBottomPaint;
    */
    //End add
    private final Paint mPaint;
    private final Paint mTargetPaint;
    private final Paint mPathPaint;
    private boolean mCurDown;
    private int mCurNumPointers;
    private int mMaxNumPointers;
    private Activity mActivity;
    private int iFlag = 0;
    final int INCREACE_WIDTH = 80;  //Modify by liugang 20120911 for GG401
    public final ArrayList<PointerState> mPointers
             = new ArrayList<PointerState>();
    
    public NcitTPview(Activity activity) {
        super(activity);
        setFocusable(true);
        mActivity = activity;
        mVC = ViewConfiguration.get(activity);
        
        mLeftPaint = new Paint();
        mLeftPaint.setColor(Color.WHITE);
       mLeftPaint.setStyle(Paint.Style.STROKE);
        mTopPaint = new Paint();
        mTopPaint.setColor(Color.WHITE);
       mTopPaint.setStyle(Paint.Style.STROKE);
        mRightPaint = new Paint();
        mRightPaint.setColor(Color.WHITE);
       mRightPaint.setStyle(Paint.Style.STROKE);
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

       //Begin add by liugang 20120911 for GG401
       /*
       mCenterPaint = new Paint();
       mCenterPaint.setColor(Color.WHITE);
       mCenterPaint.setStyle(Paint.Style.STROKE); 
       mCenterTopPaint = new Paint();
       mCenterTopPaint.setColor(Color.WHITE);
       mCenterTopPaint.setStyle(Paint.Style.STROKE);
       mCenterBottomPaint = new Paint();
       mCenterBottomPaint.setColor(Color.WHITE);
       mCenterBottomPaint.setStyle(Paint.Style.STROKE);       
       */
       //End add
       
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
            final int w = getWidth();
            final int h = getHeight();
            final int NP = mPointers.size();
            
            canvas.drawRect(0, 0, INCREACE_WIDTH, INCREACE_WIDTH, mLeftTopPaint);
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
                    if (haveLast && iFlag==1) {
                        
                       if(x<INCREACE_WIDTH&&y<INCREACE_WIDTH&&flag==0){
                           mLeftTopPaint.setColor(Color.GREEN);
                           canvas.drawRect(0, INCREACE_WIDTH, INCREACE_WIDTH, (getHeight()-INCREACE_WIDTH), mLeftPaint);
                 mLeftPaint.setColor(Color.WHITE);
                           flag = 1;
                        }
                        if(x>0&&x<=INCREACE_WIDTH&&y>INCREACE_WIDTH&&y<(getHeight()-INCREACE_WIDTH)&&flag==1){
                            mLeftPaint.setColor(Color.GREEN);
                            canvas.drawRect(0,getHeight()-INCREACE_WIDTH, INCREACE_WIDTH, getHeight(), mLeftBottomPaint);
                            mLeftBottomPaint.setColor(Color.WHITE);
                            flag = 2;
                            }
                        if(x>INCREACE_WIDTH&&y>0&&flag==2){
                            flag = 3;
                        }
                        if(x>=0&&x<INCREACE_WIDTH&&y>getHeight()-INCREACE_WIDTH&&y<getHeight()){
                            if(flag==3){
                            mLeftBottomPaint.setColor(Color.WHITE);
                            flag = 4;
                            }
                            if(flag==2){
                                mLeftBottomPaint.setColor(Color.GREEN);
                    /*
                                canvas.drawRect(INCREACE_WIDTH, getHeight()-INCREACE_WIDTH, getWidth()-INCREACE_WIDTH, getHeight(), mBottomPaint);
                   mBottomPaint.setColor(Color.WHITE);
                   */
                   //Begin add by liuang 20120911 for GG401
                                   canvas.drawRect(INCREACE_WIDTH, getHeight()-INCREACE_WIDTH, getWidth() - INCREACE_WIDTH, getHeight(),mBottomPaint);
                                mBottomPaint.setColor(Color.GREEN);
                                
                                flag = 5;
                  //End add
                            }
                        }
/*                        
                        if(x>INCREACE_WIDTH&&x<getWidth()-INCREACE_WIDTH&&y>getHeight()-INCREACE_WIDTH&&y<getHeight()&&flag==5){
                            mBottomPaint.setColor(Color.GREEN);
                            canvas.drawRect(getWidth()-INCREACE_WIDTH,getHeight()-INCREACE_WIDTH,getWidth(),getHeight(),mRightBottomPaint);
                mRightBottomPaint.setColor(Color.WHITE);
                            flag = 6;
                        }
*/
                 //Begin add by liuang 20120911 for GG401
                 /*
                        if(x>(w - INCREACE_WIDTH)/2 && x < (w + INCREACE_WIDTH)/2 && y < INCREACE_WIDTH && flag==5){
                            mCenterTopPaint.setColor(Color.GREEN);
                            canvas.drawRect((w - INCREACE_WIDTH)/2, INCREACE_WIDTH, (w + INCREACE_WIDTH)/2, h - INCREACE_WIDTH, mCenterPaint);
                            mCenterPaint.setColor(Color.WHITE);
                            flag = 6;
                        }

                        if((x < (w - INCREACE_WIDTH)/2 || x > (w + INCREACE_WIDTH)/2) && y < (h - INCREACE_WIDTH) && flag == 6){
                            flag = 7;
                        }

                        if( x > (w - INCREACE_WIDTH)/2 && x < (w + INCREACE_WIDTH)/2 && y < (h - INCREACE_WIDTH)&& y >INCREACE_WIDTH){
                            if(flag == 7){
                                mCenterBottomPaint.setColor(Color.WHITE);
                                flag =8;
                            }

                            if(flag ==6){
                                mCenterPaint.setColor(Color.GREEN);
                                canvas.drawRect((w - INCREACE_WIDTH)/2, h - INCREACE_WIDTH, (w + INCREACE_WIDTH)/2, h,mCenterBottomPaint);
                                mCenterBottomPaint.setColor(Color.WHITE);
                                flag = 9;
                            }    
                        }

                        if(x > (w - INCREACE_WIDTH)/2 && x < (w + INCREACE_WIDTH)/2 && y > (h - INCREACE_WIDTH)){
                            mCenterBottomPaint.setColor(Color.GREEN);
                            canvas.drawRect(w - INCREACE_WIDTH, 0, w, INCREACE_WIDTH, mRightTopPaint);
                            mRightTopPaint.setColor(Color.WHITE);
                            flag = 10;
                        }

                        */
                        if(x > INCREACE_WIDTH && x < w && y < h && y > h - INCREACE_WIDTH && flag == 5){
                            Log.i("test" , "I'm here");
                            //mRightPaint.setColor(Color.WHITE);
                            canvas.drawRect(w-INCREACE_WIDTH, h - INCREACE_WIDTH, w, h,mRightBottomPaint);
                               mRightBottomPaint.setColor(Color.GREEN);
                            flag = 6;                            
                        }

                        if(x < w - INCREACE_WIDTH && x > INCREACE_WIDTH &&y < h - INCREACE_WIDTH && flag == 6){
                            flag = 7;
                        }

                        Log.i("liugang","flag " + flag);

                        if(x > w - INCREACE_WIDTH && y > h - INCREACE_WIDTH && y < h){
                            if( flag == 7){
                                flag = 8;
                            }
                            if( flag == 6){
                                Log.i("liugang","flag == 6");
                                mRightPaint.setColor(Color.GREEN);
                                canvas.drawRect(w-INCREACE_WIDTH,INCREACE_WIDTH, w, h - INCREACE_WIDTH,mRightPaint);
                                   //mRightPaint.setColor(Color.WHITE);
                                flag = 9;
                            }
                        }

                        /*    
                        if(x > w - INCREACE_WIDTH && y > h - INCREACE_WIDTH && flag == 9){
                            mRightPaint.setColor(Color.GREEN);
                            flag = 10;
                        }
                        */
                //End add
                        if(x > w - INCREACE_WIDTH && y > INCREACE_WIDTH && y < h - INCREACE_WIDTH && flag == 9){
                            mRightTopPaint.setColor(Color.GREEN);
                            canvas.drawRect(w-INCREACE_WIDTH,0, w, INCREACE_WIDTH,mRightTopPaint);
                            flag = 10;
                        }

                        if(x < w - INCREACE_WIDTH &&  y > INCREACE_WIDTH && y < h - INCREACE_WIDTH && flag == 10){
                            flag = 11;
                        }

                        if(x < w && x > w - INCREACE_WIDTH && y > 0 && y < INCREACE_WIDTH){
                            if( flag == 11){
                                flag = 12;
                            }

                            if( flag == 10 ){
                                mTopPaint.setColor(Color.GREEN);
                                canvas.drawRect(INCREACE_WIDTH,0, w - INCREACE_WIDTH, INCREACE_WIDTH,mTopPaint);
                                flag = 13;
                            }
                        }

                        if(x < INCREACE_WIDTH && y < INCREACE_WIDTH && flag == 13){
                            flag = 14;
                        }
                        
/*                        
                        if(x>INCREACE_WIDTH&&x<(getWidth()-INCREACE_WIDTH)&&y>=INCREACE_WIDTH&&y<(getHeight()-INCREACE_WIDTH)&&flag==6){
                            flag = 7;
                        }
                        if(x>(getWidth()-INCREACE_WIDTH)&&x<getWidth()&&y>(getHeight()-INCREACE_WIDTH)&&y<getHeight()){
                            if(flag==7){
                                mRightBottomPaint.setColor(Color.WHITE);//do nothing i think 
                                flag =8;
                            }
                            if(flag==6){
                                mRightBottomPaint.setColor(Color.GREEN);
                                canvas.drawRect(getWidth()-INCREACE_WIDTH,INCREACE_WIDTH,getWidth(),getHeight()-INCREACE_WIDTH,mRightPaint);
                   mRightPaint.setColor(Color.WHITE);
                                flag = 9;
                            }
                        }

                         if(x>=(getWidth()-INCREACE_WIDTH)&&x<=getWidth()&&y>INCREACE_WIDTH&&y<(getHeight()-INCREACE_WIDTH)&&flag==9){
                            mRightPaint.setColor(Color.GREEN);
                            canvas.drawRect(getWidth()-INCREACE_WIDTH,0,getWidth(),INCREACE_WIDTH,mRightTopPaint);
                mRightTopPaint.setColor(Color.WHITE);
                            flag = 10;
                        }
                          if(x>INCREACE_WIDTH&&x<(getWidth()-INCREACE_WIDTH)&&y>INCREACE_WIDTH&&y<(getHeight()-INCREACE_WIDTH)&&flag==10){
                           flag = 11;
                        }
                        if(x>getWidth()-INCREACE_WIDTH&&x<getWidth()&&y>0&&y<INCREACE_WIDTH){
                            if(flag==11){
                                mRightTopPaint.setColor(Color.WHITE);
                                flag = 12;
                            }
                           if(flag==10){
                               mRightTopPaint.setColor(Color.GREEN);
                               canvas.drawRect(INCREACE_WIDTH,0,getHeight()-INCREACE_WIDTH,INCREACE_WIDTH,mTopPaint);
                  mTopPaint.setColor(Color.WHITE);
                               flag = 13;
                           }
                        }
                        if(x>INCREACE_WIDTH&&x<getWidth()-INCREACE_WIDTH&&y>0&&y<INCREACE_WIDTH&&flag==13){
                            mTopPaint.setColor(Color.GREEN);
                            flag = 14;
                        }
                        if(x>INCREACE_WIDTH&&x<getWidth()-INCREACE_WIDTH&&y>INCREACE_WIDTH&&y<(getHeight()-INCREACE_WIDTH)&&flag==14){
                            flag = 15;
                        }
                        if(x>0&&x<INCREACE_WIDTH&y>0&&y<INCREACE_WIDTH){
                            if(flag==15){
                                mLeftTopPaint.setColor(Color.WHITE);
                                flag = 16;
                            }
                            if(flag==14){
                                mLeftTopPaint.setColor(Color.GREEN);
                                flag = 18;
                            }
                        }
*/                   
                     
                        if(flag==14){  //Modify by liugang 20120911 for GG401
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
}




