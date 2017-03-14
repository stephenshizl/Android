package com.example.onehand;

import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OneHand extends Activity implements OnGestureListener {
    private String TAG = "onehandlauncher";
    private GestureDetector detector;
    private static int curX = 200;
    private static int curY = 200;
    private static Boolean flagShut = false;
    private static int afterLongpress = 0;
    double nLenStart = 0;
    double nLenEnd = 0;
    double ratio = 0.2;
    private static int windowLayoutParams_width = 1000;
    private static int windowLayoutParams_height = 1000;
    private int xChangebig = -1;
    private int yChangebig = -1;
    private Vibrator vibrator;
    LinearLayout mainLayout = null;
    private static int min_windowLayoutParams_width = 100;
    private static int min_windowLayoutParams_height = 100;
    private static int max_windowLayoutParams_width = 1000;
    private static int max_windowLayoutParams_height = 1000;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_hand);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mainLayout = (LinearLayout) findViewById(R.id.mainlayout);
        max_windowLayoutParams_width = getWindowManager().getDefaultDisplay()
                .getWidth(); // ÆÁÄ»¿í£¨ÏñËØ£¬Èç£º480px£©
        max_windowLayoutParams_height = getWindowManager().getDefaultDisplay()
                .getHeight();
        windowLayoutParams_width = max_windowLayoutParams_width; // ÆÁÄ»¿í£¨ÏñËØ£¬Èç£º480px£©
        windowLayoutParams_height = max_windowLayoutParams_height;
        // detector = new GestureDetector(this);
        // mainLayout.setPadding(curX, curY, 0, 0);
        Log.i(TAG, TAG + windowLayoutParams_width + " "
                + windowLayoutParams_height);
        android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                windowLayoutParams_width, windowLayoutParams_height - 50);
        layoutParams.leftMargin = curX;
        layoutParams.topMargin = curY;
        mainLayout.setLayoutParams(layoutParams);
        detector = new GestureDetector(this, this);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Toast.makeText(getApplicationContext(), "asdfasf",
                // 1000).show();
                // finish();
                Intent intent = new Intent();
                intent.setClassName("com.android.dialer", "com.android.dialer.DialtactsActivity");
                // intent.setClass(OneHand.this, this.getClass());
                startActivity(intent);
            }
        });
        // findViewById(R.id.background).layout(0, 0, 0, 0);
    }

    void setPosition(float x, float y) {
        Log.i(TAG, TAG + "maxwindown " + max_windowLayoutParams_width + " "
                + max_windowLayoutParams_height
                + " setposition windowLayoutParams_width"
                + windowLayoutParams_width + " " + windowLayoutParams_height
                + "leftMargin:" + x + " " + y);
        android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                windowLayoutParams_width, windowLayoutParams_height);

        if ((x + windowLayoutParams_width) < max_windowLayoutParams_width) {

            layoutParams.leftMargin = (int) x;
            Log.i(TAG, "qianyan setposition x" + x);
            // layoutParams.topMargin = (int) y;
        } else {
            layoutParams.leftMargin = (int) (max_windowLayoutParams_width - windowLayoutParams_width);
        }
        if ((y + windowLayoutParams_height) < max_windowLayoutParams_height) {
            layoutParams.topMargin = (int) y;
            Log.i(TAG, "qianyan setposition y" + y);
        } else {
            layoutParams.topMargin = (int) (max_windowLayoutParams_height - windowLayoutParams_height);
        }
        Log.i(TAG, "qianyan setposition margin" + layoutParams.leftMargin + " "
                + layoutParams.topMargin);
        mainLayout.setLayoutParams(layoutParams);
    }

    void setSize(int xChangebig, int yChangebig) {

        if (xChangebig == 1) {
            // Log.i(TAG, TAG + " current " + display.getWidth() +
            // " "+display.getHeight());
            windowLayoutParams_width = (int) (windowLayoutParams_width * (1 + ratio));
            if (windowLayoutParams_width > max_windowLayoutParams_width) {
                windowLayoutParams_width = max_windowLayoutParams_width;
            }
        } else if (xChangebig == 0) {
            windowLayoutParams_width = (int) (windowLayoutParams_width * (1 - ratio));
            if (windowLayoutParams_width < min_windowLayoutParams_width) {
                windowLayoutParams_width = min_windowLayoutParams_width;
            }
        }

        if (yChangebig == 1) {
            // Log.i(TAG, TAG + " current " + display.getWidth() +
            // " "+display.getHeight());
            windowLayoutParams_height = (int) (windowLayoutParams_height * (1 + ratio));
            if (windowLayoutParams_height > max_windowLayoutParams_height) {
                windowLayoutParams_height = max_windowLayoutParams_height;
            }
        } else if (yChangebig == 0) {
            windowLayoutParams_height = (int) (windowLayoutParams_height * (1 - ratio));
            if (windowLayoutParams_height < min_windowLayoutParams_height) {
                windowLayoutParams_height = min_windowLayoutParams_height;
            }
        }
        Log.i(TAG, TAG + " setsize " + windowLayoutParams_width
                + windowLayoutParams_height);
        android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                windowLayoutParams_width, windowLayoutParams_height);
        layoutParams.leftMargin = mainLayout.getLeft();
        layoutParams.topMargin = mainLayout.getTop();
        mainLayout.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_hand, menu);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float arg2,
            float arg3) {
        float x = event2.getX() - event1.getX();
        float y = event2.getY() - event1.getY();
        Log.i(TAG, TAG + " onScroll " + flagShut + x + " " + y);
        if (!flagShut) {
            Log.i(TAG, TAG + " onScroll enter" + flagShut + x + " " + y);
            if (x < -200) {// right -> left
                xChangebig = 1;
                setSize(xChangebig, -1);
            } else if (x > 200) { // left -> right
                xChangebig = 0;
                setSize(xChangebig, -1);
            } else if (y < -200) {// bottom -> top
                yChangebig = 1;
                setSize(-1, yChangebig);
            } else if (y > 200) {
                yChangebig = 0;
                setSize(-1, yChangebig);

            }

        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onLongPress(MotionEvent arg0) {
        afterLongpress++;
        Log.i(TAG, TAG + " onLongPress " + afterLongpress);
        if (afterLongpress > 1) {
            vibrator.vibrate(500);// vibrate for 1000ms
            mainLayout.setBackgroundColor(Color.WHITE);
            flagShut = false;
            afterLongpress = 0;
        } else {
            flagShut = true;
            vibrator.vibrate(500);// vibrate for 1000ms
            mainLayout.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float arg2,
            float arg3) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.i(TAG, "flagshut" + flagShut);
            if (flagShut) {
                Log.i(TAG, "flagshut");
                setPosition(event.getRawX(), event.getRawY());
            }
        }
        return detector.onTouchEvent(event);
    }
}
