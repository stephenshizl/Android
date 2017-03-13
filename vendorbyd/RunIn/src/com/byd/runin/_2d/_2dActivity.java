package com.byd.runin._2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.View;

import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;

public class _2dActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_2d_test";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_2d_test_time";
    public static final String TITLE = "2D Test";

    private static final String TAG = "_2dActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;
        super.onCreate(savedInstanceState);

        setContentView(new SampleView(this));
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private static class SampleView extends View
    {
        private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float mRotate;
        private Matrix mMatrix = new Matrix();
        private Shader mShader;
        private boolean mDoTiming;

        public SampleView(Context context)
        {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);

            float x = 600;
            float y = 860;
            mShader = new SweepGradient(x, y, new int[]
            {
                Color.GREEN, Color.RED, Color.BLUE, Color.GREEN
            }, null);
            mPaint.setShader(mShader);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            Paint paint = mPaint;
            float x = 550;
            float y = 860;

            canvas.drawColor(Color.WHITE);

            mMatrix.setRotate(mRotate, x, y);
            mShader.setLocalMatrix(mMatrix);
            mRotate += 3;
            if (mRotate >= 360)
            {
                mRotate = 0;
            }
            invalidate();

            if (mDoTiming)
            {
                long now = System.currentTimeMillis();
                for (int i = 0; i < 20; i++)
                {
                    canvas.drawCircle(x, y, 500, paint);
                }
                now = System.currentTimeMillis() - now;
                Log.d(TAG, "sweep ms = " + (now / 20.));
            }
            else
            {
                canvas.drawCircle(x, y, 500, paint);
            }
        }
    }
}
