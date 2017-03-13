
package com.byd.runin.battery;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.byd.runin.R;

public class BatteryInfo
{
    private Context mContext;

    private WindowManager.LayoutParams mDecorLayoutParams;
    private WindowManager mWindowManager;
    private View mDecor;
    private boolean mIsShowing = false;

    private TextView mState;
    private TextView mLevel;

    public BatteryInfo(Context context)
    {
        mContext = context;
        initFloatingWindowLayout();
        initFloatingWindow();
    }

    private void initFloatingWindowLayout()
    {
        mDecorLayoutParams = new WindowManager.LayoutParams();
        WindowManager.LayoutParams p = mDecorLayoutParams;
        p.gravity = Gravity.TOP | Gravity.RIGHT;
        p.width = LayoutParams.WRAP_CONTENT;
        p.height = LayoutParams.WRAP_CONTENT;
        p.x = 0;
        p.y = 70;
        p.format = PixelFormat.TRANSLUCENT;
        //        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.type = WindowManager.LayoutParams.TYPE_PHONE;
        p.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        p.token = null;
    }

    private void initFloatingWindow()
    {
        mWindowManager = (WindowManager)mContext.getSystemService
            (Context.WINDOW_SERVICE);
        LayoutInflater inflate = (LayoutInflater)mContext.getSystemService
            (Context.LAYOUT_INFLATER_SERVICE);
        mDecor = inflate.inflate(R.layout.battery_info, null);

        initContentView(mDecor);
    }

    private void initContentView(View view)
    {
        mState = (TextView)view.findViewById(R.id.state);
        mLevel = (TextView)view.findViewById(R.id.level);
    }

    public void setText(String state, String level)
    {
        mState.setText(state + " " + level);
        //mLevel.setText(level);
    }

    public void show()
    {
        if (!mIsShowing)
        {
            mIsShowing = true;
            mWindowManager.addView(mDecor, mDecorLayoutParams);
        }
    }

    public void hide()
    {
        if (mIsShowing)
        {
            mIsShowing = false;
            mWindowManager.removeView(mDecor);
        }
    }
}
