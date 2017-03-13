/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20101116  Sang Mingxin   Initial to auto test earphone.

===========================================================================*/
package com.tools.cit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
//import android.media.AudioSystem;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
//import android.content.ComponentName;
//import android.servicemenu.CITTest;
//import android.view.WindowManager;
public class AutoEarphone extends TestModule  {
    private Button sucBtn, falBtn, btnRecord, btnStop, btnChannel;
    private TextView earText, tvHook, tvHookKey, tvMic, tvChannel, mTextView;
    private AudioManager mAudioManager;
    private boolean mIsHeadsetPlugged = false;
    private final int TEST_AUDIO = 0, TEST_FINISH = 1;
    Context mContext;
    String mAudiofilePath;
    boolean isRecording = false;
    int currentVolume = -1;
    private final int TEST_VOL = 7;
    MediaRecorder mMediaRecorder = new MediaRecorder();
    private static MediaPlayer mediaPlayer;
    private static final String TAG = "AutoEarphone";
    private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                mIsHeadsetPlugged = (intent.getIntExtra("state", 0) == 1);
                if (mIsHeadsetPlugged) {
                    earText.setText(R.string.auto_ear_info);
                    earText.setBackground(null);
                    btnRecord.setEnabled(true);
                    
                } else {
                    earText.setText(R.string.insert_headset);
                    earText.setBackgroundResource(R.color.red);
                    sucBtn.setEnabled(false);
                }
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,R.layout.auto_earphone,R.drawable.headset);
        //setContentView(R.layout.auto_earphone);
        mContext = this;
        isRecording = false;
        earText = (TextView) findViewById(R.id.auto_ear_text);
        sucBtn = (Button) findViewById(R.id.btn_pass);
        falBtn = (Button) findViewById(R.id.btn_fail);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        init1();
    }

    private void init1() {
        btnRecord = (Button) findViewById(R.id.ear_record);
        btnStop = (Button) findViewById(R.id.ear_stop);
        btnChannel = (Button) findViewById(R.id.btn_channel);
        tvHook = (TextView) findViewById(R.id.auto_ear_hook);
        tvMic = (TextView) findViewById(R.id.auto_ear_mic);
        tvChannel = (TextView) findViewById(R.id.auto_ear_channel);
        mTextView = (TextView) findViewById(R.id.auto_mic_info);
        tvHookKey = (TextView) findViewById(R.id.auto_hook);
        btnRecord.setEnabled(false);
        btnStop.setEnabled(false);
        btnChannel.setEnabled(false);
        btnRecord.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mIsHeadsetPlugged) {
                    mTextView.setText(getString(R.string.headset_recording));
                    btnStop.setEnabled(true);
                    btnRecord.setEnabled(false);
                    try {
                        record();
                        isRecording = true;
                    } catch (Exception e) {
                        Log.e(TAG, "error:" + e);
                    }
                } else {
                    showWarningDialog(getString(R.string.insert_headset));
                }
            }
        });
        btnStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isRecording) {
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                    // isRecording = false;
                    Log.e("wxb", "stop on click");
                    try {
                        replay();
                    } catch (Exception e) {
                        Log.e(TAG, "error:" + e);
                    }
                } else {
                    showWarningDialog(getString(R.string.insert_headset));
                }
                // btnRecord.setEnabled(true);
                btnRecord.setEnabled(false);
                btnStop.setEnabled(false);
            }
        });
        btnChannel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mIsHeadsetPlugged) {
                    btnChannel.setClickable(false);
                    testChannel();
                } else {
                    showWarningDialog(getString(R.string.insert_headset));
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_HEADSETHOOK:
            tvHookKey.setBackgroundResource(R.color.green);
            btnRecord.setEnabled(true);
            tvHook.setText(getString(R.string.auto_ear_hook_pass));
            break;
        default:
            Log.e(TAG, "Error!");
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        sucBtn.setEnabled(false);// false
        if (mAudioManager != null) {
            mIsHeadsetPlugged = mAudioManager.isWiredHeadsetOn();
            if (!mIsHeadsetPlugged) {
                earText.setText(R.string.auto_ear_input);
            }
        }
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, TEST_VOL, 0);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mHeadsetReceiver);
        myHandler.removeMessages(TEST_AUDIO);
        myHandler.removeMessages(TEST_FINISH);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        super.onPause();
    }

    @Override
    public void finish() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
       // AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
        super.finish();
    }

    public void setAudio() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
       // AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_WIRED_ACCESSORY);
        float ratio = 0.6f;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
    }

    void record() throws IllegalStateException, IOException, InterruptedException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(this.getCacheDir().getAbsolutePath() + "/test.amr");
        mAudiofilePath = this.getCacheDir().getAbsolutePath() + "/test.amr";
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    void replay() throws IllegalArgumentException, IllegalStateException, IOException {
        final TextView mTextView = (TextView) findViewById(R.id.auto_mic_info);
        mTextView.setText(getString(R.string.headset_playing));
        // Replaying sound right now by record();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        File file = new File(mAudiofilePath);
        FileInputStream mFileInputStream = new FileInputStream(file);
        final MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(mFileInputStream.getFD());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mPlayer) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                File file = new File(mAudiofilePath);
                file.delete();
                final TextView mTextView = (TextView) findViewById(R.id.auto_mic_info);
                mTextView.setText(getString(R.string.headset_replay_end));
                showConfirmDialog();
            }
        });
    }

    void showWarningDialog(String title) {
        new AlertDialog.Builder(mContext).setTitle(title)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setCancelable(false).show();
    }

    void showConfirmDialog() {
        new AlertDialog.Builder(mContext).setTitle(getString(R.string.headset_confirm))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        btnChannel.setEnabled(true);
                        tvMic.setText(getString(R.string.auto_ear_mic_pass));
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //backToTest(0);
                        setTestResult(FAIL);
                        finish();
                    }
                }).setCancelable(false).show();
    }

    public void testChannel() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("/system/media/audio/ringtones/Aurora.ogg");
            mediaPlayer.prepare();
        } catch (IOException ex) {
            Log.e(TAG, "setDataSource failed " + ex.toString());
        }
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0.0F, 1.0F);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        Message msg = new Message();
        msg.what = TEST_AUDIO;
        myHandler.sendMessageDelayed(msg, 3000);
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case TEST_AUDIO:
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource("/system/media/audio/ringtones/Aurora.ogg");
                    mediaPlayer.prepare();
                } catch (IOException ex) {
                    Log.e(TAG, "setDataSource failed " + ex.toString());
                }
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(1.0F, 0.0F);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                Message msg1 = new Message();
                msg1.what = TEST_FINISH;
                myHandler.sendMessageDelayed(msg1, 3000);
                break;
            case TEST_FINISH:
                btnChannel.setClickable(true);
                sucBtn.setEnabled(true);
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                tvChannel.setText(getString(R.string.auto_ear_channel_pass));
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public String getModuleName() {
        return "Earphone";
    }
}
