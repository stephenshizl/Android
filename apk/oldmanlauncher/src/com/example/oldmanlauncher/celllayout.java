package com.example.oldmanlauncher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class celllayout extends ViewGroup {

    private final static String TAG = "MyViewGroup";
    private final static int VIEW_MARGIN_WIDTH = 35;
    private final static int VIEW_MARGIN_HEIGHT = 25;
    public celllayout(Context context) {
        super(context);
       
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec" + heightMeasureSpec);
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            // measure
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean arg0, int left, int top, int right, int botom) {
        Log.d(TAG, "changed = " + arg0 + " left = " + left + " top = " + top
                + " right = " + right + " botom = " + botom);
        final int count = getChildCount();
        int row = 0;// which row lay you view relative to parent
        int lengthX = left; // right position of child relative to parent
        int lengthY = top; // bottom position of child relative to parent
        
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            lengthX += width + VIEW_MARGIN_WIDTH;
            lengthY = row * (height + VIEW_MARGIN_HEIGHT) + VIEW_MARGIN_HEIGHT + height + top;
            // if it can't drawing on a same line , skip to next line
            if (lengthX > right) {
                lengthX = width + VIEW_MARGIN_WIDTH + left;
                row++;
                lengthY = row * (height + VIEW_MARGIN_HEIGHT) + VIEW_MARGIN_HEIGHT + height + top;
            }
            child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
        }

    }

    
    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
    }
}
