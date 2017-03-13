
package com.lenovo.freeanswer.wakeup.debug;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.lenovo.freeanswer.wakeup.R;
import com.lenovo.freeanswer.wakeup.WakeUpManager;
import com.lenovo.freeanswer.wakeup.WakeUpManager.WakeUpCallBack;

public class DebugActivity extends Activity {
    private static final String TAG = "DebugActivity";
    public static final String WAKE_UP_START = "com.lenovo.freedial.action.VOICE_SPEECH_FREE_DIAL_START";
    public static final String WAKE_UP_STOP = "com.lenovo.freedial.action.VOICE_SPEECH_FREE_DIAL_STOP";

    private Button mStartBtn;
    private Button mStopBtn;
    private Button mFreeAnswerBtn;
    private Button mFreeDialHandsFreeBtn;
    private Button mFreeAnswerHandsFreeBtn;
    private Button mTtsRportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartBtn = (Button) findViewById(R.id.start);
        mStopBtn = (Button) findViewById(R.id.stop);
        mFreeAnswerBtn = (Button) findViewById(R.id.freeanswer);
        mFreeDialHandsFreeBtn = (Button) findViewById(R.id.freedial_handsfree);
        mFreeAnswerHandsFreeBtn = (Button) findViewById(R.id.freeanswer_handsfree);
        mTtsRportBtn = (Button) findViewById(R.id.tts_report);

        mStartBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 需要标示是来电挂断、接听唤醒 ，还是呼出时候挂断唤醒
                WakeUpManager.getInstance(DebugActivity.this).startListen(
                        new WakeUpCallBack() {
                            @Override
                            public void onResult(final String result) {
                                // TODO Auto-generated method stub
                                Log.d(TAG, "onResult " + result);

                                new Thread() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        Toast.makeText(DebugActivity.this, result,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                        Looper.loop();
                                    }
                                }.start();
                            }

                            @Override
                            public void onError(int error) {
                                // TODO Auto-generated method stub
                                Log.d(TAG, "onError " + error);
                            }
                        }, WakeUpManager.WAKE_UP_KEY_HUNGUP_ANSWER);
            }
        });

        mStopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                WakeUpManager.getInstance(DebugActivity.this).stopListen();
            }
        });

        mFreeAnswerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isFreeCallOn = WakeUpManager.getInstance(DebugActivity.this)
                        .isFreeAnswerOn();
                Toast.makeText(getApplicationContext(), "免触接听开关 " + isFreeCallOn,
                        Toast.LENGTH_SHORT).show();
            }
        });
        mFreeDialHandsFreeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isFreeCallOn = WakeUpManager.getInstance(DebugActivity.this)
                        .isFreeDialHandsFreeOn();
                Toast.makeText(getApplicationContext(), "免触拨号自动免提 " + isFreeCallOn,
                        Toast.LENGTH_SHORT).show();
            }
        });
        mFreeAnswerHandsFreeBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isFreeCallOn = WakeUpManager.getInstance(DebugActivity.this)
                        .isFreeAnswerHandsFreeOn();
                Toast.makeText(getApplicationContext(), "免触接听自动免提 " + isFreeCallOn,
                        Toast.LENGTH_SHORT).show();
            }
        });
        mTtsRportBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isFreeCallOn = WakeUpManager.getInstance(DebugActivity.this)
                        .isTtsReportOn();
                Toast.makeText(getApplicationContext(), "来电语音播报开关" + isFreeCallOn,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        WakeUpManager.getInstance(DebugActivity.this).stopListen();
    }
}
