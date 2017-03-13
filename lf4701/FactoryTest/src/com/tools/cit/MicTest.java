
package com.tools.cit;

import java.io.File;
import java.io.IOException;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MicTest extends TestModule implements OnClickListener, OnCompletionListener {

    public static final String Mic = "Mic";
    public static final int DURATION = 3000; // time in milliseconds
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private Visualizer mVisualizer;
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
    private static double db = 0.0;

    long startTime;

    TextView txtTestInfo;
    Button btnStart,btnPass;
    ProgressBar mProgressBar;
    VUMeter mVUMeter;

    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;

    HeadsetReceiver headsetReceiver = new HeadsetReceiver();

    private static final String SAMPLE_RECORDING_FILENAME = "sample_recording.3gpp";
    private String sampleRecordingPath;

    File sampleRecordingFile = null;
    private Handler mHandler = new Handler();

    private Runnable mStopRecording = new Runnable() {
        public void run() {
            stopRecording();
            mHandler.postDelayed(mStartPlayback, 300);
        }
    };

    private Runnable mStartPlayback = new Runnable() {
        public void run() {
            startPlayback();
        }
    };

    private Runnable mUpdateUI = new Runnable() {
        public void run() {
            updateUI();
            /*
            if(mRecorder != null)
             db = Math.log10(mRecorder.getMaxAmplitude()) * 10;
             */
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.mic, R.drawable.mic);
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        mLinearLayout = (LinearLayout) findViewById(R.id.wavelay);
        btnStart = (Button) findViewById(R.id.start);
        btnStart.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setIndeterminate(false);
        mVUMeter = (VUMeter) findViewById(R.id.vumeter);
        // Create a VisualizerView (defined below), which will render the simplified audio wave form to a Canvas.
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        sampleRecordingPath = getFilesDir() + File.separator + SAMPLE_RECORDING_FILENAME;
        FTLog.d(this, "sample recording file: " + sampleRecordingPath);
        btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(false);
    }

    public void onStart() {
        super.onStart();
        FTLog.d(this, "onStart()");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            startRecording();
        }

    }

    private void startRecording() {
        FTLog.d(this, "Start recording!");
        // wl.acquire();

        // btnStart.setEnabled(false);
        btnStart.setClickable(false);
        btnStart.setText(R.string.mic_recording);

        mRecorder = new MediaRecorder();
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(sampleRecordingPath);
            mRecorder.prepare();
            mRecorder.start();

            // UI for recording
            startTime = System.currentTimeMillis();
            updateUI();

            mVUMeter.setRecorder(mRecorder);
            mVUMeter.invalidate();

            mHandler.postDelayed(mStopRecording, DURATION);// Stop recording after 3 seconds and start playback.
        } catch (Exception e) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            onError();
            Toast.makeText(this, "Error when recording!", Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecording() {
        FTLog.d(this, "Stop recording!");
        mProgressBar.setProgress(100);
        mProgressBar.invalidate();

        mVUMeter.setRecorder(null);

        if (mRecorder == null)
            return;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        // set the file permission to a+r!!
        new File(sampleRecordingPath).setReadable(true, false);

    }

    private void startPlayback() {
        btnStart.setText(R.string.mic_playing);

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(this);
            mPlayer.setDataSource(sampleRecordingPath);
            mPlayer.prepare();
            setupVisualizerFxAndUI();

            // Make sure the visualizer is enabled only when you actually want to receive data, and when it makes sense to receive data.
            mVisualizer.setEnabled(true);

            mPlayer.start();
            // set start time
            startTime = System.currentTimeMillis();
            FTLog.d(this, "Start playing time: " + startTime);
            updateUI();

            /*
            Intent intent = new Intent();
            intent.setClass(this, MyTTS.class);
            startActivityForResult(intent,100);
            */
        } catch (Exception e) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            mVisualizer.setEnabled(false);
            onError();
            Toast.makeText(this, "Error when playing back!", Toast.LENGTH_LONG).show();
        }
    }

    private void stopPlayback() {
        FTLog.d(this, "Stop playback");
        mProgressBar.setProgress(0);

        if (mPlayer == null)
            return;
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    private void onError() {
        // we encountered an Error. Reset environment.
        FTLog.e(this, "Error when recording/playback.");
        btnStart.setClickable(true);
        btnStart.setText(R.string.mic_start);
        mVisualizerView.setVisibility(8);
        mVUMeter.setVisibility(0);
        mProgressBar.setProgress(0);
        mHandler.removeCallbacks(mStopRecording);// no need to playback

    }

    // delete recording file before exit
    private void deleteRecording() {
        File f = new File(sampleRecordingPath);
        f.delete();
    }

    private void updateUI() {
        if (mPlayer != null || mRecorder != null) {
            FTLog.d(this, "UI(progress bar) update!");
            mProgressBar.setProgress((int) (100. * (System.currentTimeMillis() - startTime) / DURATION));
            mHandler.postDelayed(mUpdateUI, 1000);
        } else {

        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(headsetReceiver);
        stopRecording();
        stopPlayback();
        deleteRecording();
        // remove all messages from message queue
        mHandler.removeCallbacks(mStopRecording);
        mHandler.removeCallbacks(mStartPlayback);
        mHandler.removeCallbacks(mUpdateUI);

        // if(wl.isHeld()) wl.release();

        super.onDestroy();

    }

    // playback finished.
    @Override
    public void onCompletion(MediaPlayer mp) {
        // wl.release();
        FTLog.d(this, "Stop playing time: " + System.currentTimeMillis());
        mPlayer.release();
        mPlayer = null;
        mVisualizer.setEnabled(false);
        mVisualizerView.setVisibility(8);
        mVUMeter.setVisibility(0);
        mProgressBar.setProgress(100);
        // enable UIs
        btnStart.setClickable(true);
        btnStart.setText(R.string.mic_start);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
        //Intent intent = new Intent();
        //intent.setClass(this, MyTTS.class);
        //startActivityForResult(intent,100);        
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FTLog.d(this, "onConfigurationChanged");
    }

    private class HeadsetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // FTLog.i(this, "On Receive");
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                FTLog.i(this, "ACTION_HEADSET_PLUG " +
                        (intent.getIntExtra("state", -1) == 1 ? "plugged" : "unplugged") +
                        ", name: " + intent.getStringExtra("name") +
                        ", " + (intent.getIntExtra("microphone", -1) == 1 ? "with" : "without")
                        + " microphone"
                        );
                if (intent.getIntExtra("state", 0) == 1) {
                    if (intent.getIntExtra("microphone", -1) == 1)
                        ((TextView) findViewById(R.id.txt_headset_info))
                                .setText(R.string.mic_hs_with_mic);
                    else
                        ((TextView) findViewById(R.id.txt_headset_info))
                                .setText(R.string.mic_hs_without_mic);
                    ;
                } else
                    ((TextView) findViewById(R.id.txt_headset_info)).setText(R.string.mic_no_hs);
            }

        }

    }

    @Override
    public String getModuleName() {
        return Mic;
    }

    /**
     * View for displaying recording volume
     *
     * @author android
     */
    public static class VUMeter extends View {
        static final float PIVOT_RADIUS = 3.5f;
        static final float PIVOT_Y_OFFSET = 10f;
        static final float SHADOW_OFFSET = 2.0f;
        static final float DROPOFF_STEP = 0.18f;
        static final float SURGE_STEP = 0.35f;
        static final long ANIMATION_INTERVAL = 70;

        Paint mPaint, mShadow;
        float mCurrentAngle;

        MediaRecorder mRecorder;

        public VUMeter(Context context) {
            super(context);
            init(context);
        }

        public VUMeter(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        void init(Context context) {
            Drawable background = context.getResources().getDrawable(R.drawable.vumeter);
            setBackgroundDrawable(background);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.WHITE);
            mShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShadow.setColor(Color.argb(60, 0, 0, 0));

            mRecorder = null;

            mCurrentAngle = 0;
        }

        public void setRecorder(MediaRecorder recorder) {
            mRecorder = recorder;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            final float minAngle = (float) Math.PI / 8;
            final float maxAngle = (float) Math.PI * 7 / 8;

            float angle = minAngle;
            if (mRecorder != null){
                int amplitude = mRecorder.getMaxAmplitude();
                //angle += (float) (maxAngle - minAngle) * mRecorder.getMaxAmplitude() / 32768;
                angle += (float) (maxAngle - minAngle) * amplitude / 32768;
                db = Math.log10(amplitude) * 10;
                Log.i("liugang", "db " + db);
            }
            if (angle > mCurrentAngle)
                mCurrentAngle = angle;
            else
                mCurrentAngle = Math.max(angle, mCurrentAngle - DROPOFF_STEP);

            mCurrentAngle = Math.min(maxAngle, mCurrentAngle);

            // when mRecorder == null, we should put the pointer to minAngle
            if (mRecorder == null)
                mCurrentAngle = minAngle;

            float w = getWidth();
            float h = getHeight();
            float pivotX = w / 2;
            float pivotY = h - PIVOT_RADIUS - PIVOT_Y_OFFSET;
            float l = h * 4 / 5;
            float sin = (float) Math.sin(mCurrentAngle);
            float cos = (float) Math.cos(mCurrentAngle);
            float x0 = pivotX - l * cos;
            float y0 = pivotY - l * sin;
            canvas.drawLine(x0 + SHADOW_OFFSET, y0 + SHADOW_OFFSET, pivotX + SHADOW_OFFSET, pivotY
                    + SHADOW_OFFSET, mShadow);
            canvas.drawCircle(pivotX + SHADOW_OFFSET, pivotY + SHADOW_OFFSET, PIVOT_RADIUS, mShadow);
            canvas.drawLine(x0, y0, pivotX, pivotY, mPaint);
            canvas.drawCircle(pivotX, pivotY, PIVOT_RADIUS, mPaint);

            if (mRecorder != null)
                postInvalidateDelayed(ANIMATION_INTERVAL);
        }
    }

    private void setupVisualizerFxAndUI() {
        mVUMeter.setVisibility(8);
        mVisualizerView.setVisibility(0);
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                    int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    /**
     * A simple class that draws waveform data received from a
     * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
     */
    public static class VisualizerView extends View {
        private byte[] mBytes;
        private float[] mPoints;
        private Rect mRect = new Rect();

        private Paint mForePaint = new Paint();

        public VisualizerView(Context context) {
            super(context);
            init();
        }

        private void init() {
            mBytes = null;

            mForePaint.setStrokeWidth(1f);
            mForePaint.setAntiAlias(true);
            mForePaint.setColor(Color.rgb(0, 128, 255));
        }

        public void updateVisualizer(byte[] bytes) {
            mBytes = bytes;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (mBytes == null) {
                return;
            }

            if (mPoints == null || mPoints.length < mBytes.length * 4) {
                mPoints = new float[mBytes.length * 4];
            }

            mRect.set(0, 0, getWidth(), getHeight());

            for (int i = 0; i < mBytes.length - 1; i++) {
                mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
                mPoints[i * 4 + 1] = mRect.height() / 2
                        + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
                mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
                mPoints[i * 4 + 3] = mRect.height() / 2
                        + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
            }

            canvas.drawLines(mPoints, mForePaint);
        }
    }

    class TestThread extends Thread implements Runnable{
        String command;

        public TestThread(String command){
            super();
            this.command=command;
        }

        @Override
        public void run() {
            File f=new File("/system/etc/tcmd/setprop");
            if(!f.exists()){
                FTLog.e(this, "/system/etc/tcmd/setprop doesn't exist");
                return;
            }
            command="setprop "+command;
            String receive=FTUtil.TCP_Send(command);
            if(("" != receive)&&(-1!=receive.indexOf("ret: 0", 0)))
                FTLog.i(this, "Execute "+command+" success.");
            else
                FTLog.e(this, "Execute " + command + " error.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            int result = data.getIntExtra("result",-1);
            int number = Integer.valueOf(data.getStringExtra("speak"));
            if(0 <= result && result < 10 && result == number && db > 20){
                btnPass.setEnabled(true);
                setTestResult(PASS);
            }
            else {
                Toast.makeText(this,R.string.tts_result, Toast.LENGTH_SHORT).show();
            }
                
        }
    }    
}
