
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import android.view.WindowManager;
import android.os.SystemProperties;
public class EveryMicTest extends TestModule implements OnClickListener, OnCompletionListener {
    public static final String Mic = "Mic";
    public static final int RESULT_CODE = 0X11;
    public static final int DURATION = 3000; // time in milliseconds
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private Visualizer mVisualizer;
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
    private static double db = 0.0;
    long startTime;
    private static final int LEFT_HEADSET = 1;
    private static final int RIGHT_HEADSET = 1;
    private static final int LEFT_HEADPHONE = 0;
    private static final int RIGHT_HEADPHONE = 0;
     private boolean leftEarphone = false;
    private boolean rightEarphone = false;
    
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

    public static String type = "0";
    public static String command = "";
    public static int which;
    private String commandtxt_path = "/system/xbin/kill_audiolooper_command";
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
        super.onCreate(savedInstanceState, R.layout.everymic, R.drawable.mic);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_RIGHT_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);
        
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
        
        Intent intent = getIntent();
        command = intent.getStringExtra("command");
        which = intent.getIntExtra("which", 0x00);
        runCommand();
        initHelp();
        OnClickListener bottomBarOnClickListener=new OnClickListener() {
        @Override
            public void onClick(View v) {    
                buttomBarOnClickeverymic(v);
            }
        };
        ((Button) findViewById(R.id.btn_pass)).setOnClickListener(bottomBarOnClickListener);
        ((Button) findViewById(R.id.btn_fail)).setOnClickListener(bottomBarOnClickListener);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(true);

    }

   private void buttomBarOnClickeverymic(View v) {
        if(v.getId() == R.id.btn_pass){
          //  FTLog.i(this, "Test Passed: "+this.getModuleName());
            //setTestResult(PASS);
            Intent intent = new Intent();
            intent.putExtra("result", "1");
            setResult(RESULT_CODE, intent);
            saveResultToDATA(1);
            closeAlllooper();
            super.finish();
        }else if(v.getId() == R.id.btn_fail){
            //FTLog.i(this, "Test Failed: "+this.getModuleName());
            //setTestResult(FAIL);
            //statusBar.setText(R.string.fail_saved);
            Intent intent = new Intent();
            intent.putExtra("result", "0");
            saveResultToDATA(0);
            setResult(RESULT_CODE, intent);
            closeAlllooper();
            super.finish();
        }  
}
     //close all audio loop
    private void closeAlllooper(){
     new Thread(new Runnable(){
        public  void run(){
            try{
                Log.i("audiolooper", "closeall 1 looper command:");
             // Process process2 = Runtime.getRuntime().exec("echo ariston.ini > /sys/bus/i2c/devices/0-0038/ftsscaptest");
                List<String> cmds = new ArrayList<String>();
                cmds.add("sh");
                cmds.add("-c");
                cmds.add(commandtxt_path);
                ProcessBuilder pb = new ProcessBuilder(cmds);
                pb.start();
                //Process process1 = Runtime.getRuntime().exec(close_command);
                //Process process2 = Runtime.getRuntime().exec("ls > /data/test/test.txt");
               
            }
            catch (IOException e ) { 
                e.printStackTrace ( ) ; 
            }
        }
     }).start();
   
 } 

private static final String SIMSTATUS_RESULT_PATH = "/data/simappstatus.txt";
public void saveResultToDATA(int result){
        FileWriter fWriter = null;
        try {
            File simstatusResult = new File(SIMSTATUS_RESULT_PATH);
        if(!simstatusResult.exists()){
                   simstatusResult.createNewFile();
            }
            fWriter = new FileWriter(SIMSTATUS_RESULT_PATH);
            fWriter.write(String.valueOf(result));
            //if(result == 0){
            //    fWriter = new FileWriter(simstatusResult);
            //    fWriter.write(String.valueOf(result));
            //    fWriter.close();
            //}
                    fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( fWriter != null){
                try{
                    fWriter.close();
                } catch (Exception e){
                }
            }
        }
    }
    void initHelp() {

        String help = "";
        TextView textView = (TextView) findViewById(R.id.help);
        switch (which){
            case 0x11:      
            case 0x13:
            case 0x15:
                type = "1"; 
                help = getString(R.string.txttipac1);
                textView.setText(help);
                btnStart.setEnabled(false);
                break;
            case 0x12:
            case 0x14:    
            case 0x16:
                type = "2";
                help = getString(R.string.txttipac2);
                textView.setText(help);
                btnStart.setEnabled(false);
                break;
            case 0x17:    
            case 0x18:
                type = "0";
                help = getString(R.string.txttip);
                textView.setText(help);
                btnStart.setEnabled(true);
                break;    
        }
    }
    
    private void runCommand(){
        final List<String> cmds = new ArrayList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add("echo " + command + " > /data/citflag");
        Log.i("mic test","command speaker:" + command);
        new Handler().postDelayed(new Runnable(){  
        public void run() { 
            try{
             // Process process2 = Runtime.getRuntime().exec("echo ariston.ini > /sys/bus/i2c/devices/0-0038/ftsscaptest");
                ProcessBuilder pb1 = new ProcessBuilder(cmds);
                pb1.start(); 
            }
            catch (IOException e ) { 
                e.printStackTrace ( ) ; 
            }
        }
        }, 1); 
        
    }
    public void onStart() {
        super.onStart();
        FTLog.d(this, "onStart()");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start) {
            startRecording();
        } else if(id == R.id.btn_pass) {
            Intent intent = new Intent();
            intent.putExtra("result", "1");
            setResult((int)which, intent);
        } else if (id == R.id.btn_fail) {
            Intent intent = new Intent();
            intent.putExtra("result", "0");
            setResult((int)which, intent);
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
    protected void onResume() {
        super.onResume();
        SystemProperties.set("persist.service.daemon.enable", "1");

    }
   @Override
    protected void onPause() {
        super.onPause();
        SystemProperties.set("persist.service.daemon.enable", "0");
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
        try {
            Process process1 = Runtime.getRuntime().exec("echo 0 > /data/citflag");
        } catch (Exception e ) { 
            e.printStackTrace () ; 
        } 
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
                String FLAG_LEFTRIGHT ="";
                FLAG_LEFTRIGHT =  intent.getStringExtra("position");
                if(FLAG_LEFTRIGHT.equals("right")) { //right earphone
  
                    FTLog.i(this, "ACTION_HEADSET_PLUG " +
                        (intent.getIntExtra("state", -1) == 1 ? "plugged" : "unplugged") +
                        ", name: " + intent.getStringExtra("name") +
                        ", " + (intent.getIntExtra("microphone", -1) == 1 ? "with" : "without")
                        + " microphone");
                    if (intent.getIntExtra("state", 0) == 1) {
                        rightEarphone = true;
                        switch(intent.getIntExtra("microphone", -1)) {
                            case RIGHT_HEADSET:
                                ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_right_mic));
                                break;
                            case RIGHT_HEADPHONE:
                                ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_right_nomic));
                                break;
                        } //end switch
                        if(type.equals("2")) {
                            btnStart.setEnabled(true);
                            ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                        } else {
                            btnStart.setEnabled(false);
                            ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
                        }
                      
                      } else {
                            rightEarphone = false;
                            if(!leftEarphone && !rightEarphone) {
                                ((TextView) findViewById(R.id.txt_headset_info))
                                 .setText(getString(R.string.mic_no_hs)); 
                                if(type.equals("0")) {
                                    btnStart.setEnabled(true);
                                    ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                                } else {
                                        btnStart.setEnabled(false);
                                        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
                                }
                            }
                    }
               
                } else if (FLAG_LEFTRIGHT.equals("left")){ // left earphone
                    if (intent.getIntExtra("state", 0) == 1) {
                        leftEarphone = true;
                        switch(intent.getIntExtra("microphone", -1)) {
                            case LEFT_HEADSET:
                                ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_left_mic));
                                break;
                            case LEFT_HEADPHONE:
                                ((TextView) findViewById(R.id.txt_headset_info)).setText(getString(R.string.ear_hs_plugged_left_nomic));
                                break;
                        } //end switch
                        if(type.equals("1")) {
                            btnStart.setEnabled(true);
                            ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                        } else {
                            btnStart.setEnabled(false);
                            ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
                        }
                   
                     } else {  
                         leftEarphone = false;
                         if(!leftEarphone && !rightEarphone) {
                              ((TextView) findViewById(R.id.txt_headset_info))
                              .setText(getString(R.string.mic_no_hs)); 
                              if(type.equals("0")) {
                                  btnStart.setEnabled(true);
                                  ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                              } else {
                                  btnStart.setEnabled(false);
                                  ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
                              }
                         }
                    }
                
        }
        }

        }

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
    public String getModuleName() {
        return Mic;
    }
}
