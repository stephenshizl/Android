
package com.tools.customercit;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import com.tools.util.FTUtil;
import android.os.Handler;
import android.os.Message;

public class VibratorTest extends TestModule /* STEP 1 */implements OnTouchListener {
    private Vibrator vibrator;
    public static final String VIBRATOR = "Vibrator";
    private static final int VIBRATOR_COMPLETED = 1;
    TextView txtLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.vibrator, R.drawable.vibrate);

        findViewById(R.id.vibrator).setOnTouchListener(this);
        txtLog = (TextView) findViewById(R.id.vibrator);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // Check whether the hardware has a vibrator
        if (vibrator.hasVibrator())
            txtLog.append(FTUtil.coloredString(getResources().getString(R.string.touch_to_vibrate),
                    Color.LTGRAY));
        else
        {
            txtLog.append(FTUtil.coloredString(
                    getResources().getString(R.string.vibrate_not_exist), R.color.log_red));
            setTestResult(FAIL);
        }
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return VIBRATOR;
    }
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case VIBRATOR_COMPLETED:
                ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                txtLog.setText(getString(R.string.vibrate_completed));
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                vibrator.vibrate(1000);//vibrate for 1000ms
                Message msg = new Message();
                msg.what = VIBRATOR_COMPLETED;
                myHandler.sendMessageDelayed(msg, 1000);
                //((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                break;
            default:
                ;
        }
        return true;
    }
}
