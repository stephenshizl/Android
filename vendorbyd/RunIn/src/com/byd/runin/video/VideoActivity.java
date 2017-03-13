
package com.byd.runin.video;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;

import com.byd.runin.R;
import com.byd.runin.RuninTestMainActivity;
import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;
import com.byd.runin.receiver.ReceiverTest;
import com.byd.runin.speaker.SpeakerTest;
import com.byd.runin.vibrator.VibratorTest;

import java.io.File;

public class VideoActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_video_test";
    public static final String TITLE = "SV Test";
    private static final String TAG = "VideoActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_video_test_time";

    private VideoView videoView;
    private int mPositionWhenPaused =  - 1;
    private SpeakerTest st = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getRequestedOrientation() !=
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        videoView = new VideoView(this);
        start();
    }

    private void setViewConfigure(){
        setContentView(videoView);
        videoView.setVideoPath("android.resource://com.byd.runin/"+R.raw.test);
        videoView.setOnPreparedListener(new OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
            }
        });

        videoView.start();
    }

    private void start(){
        if ( strMode != null && strMode.equals(RuninTestMainActivity.FINAL) ){
            setViewConfigure();

        }else{
            File filepath = null;
            String status = Environment.getExternalStorageState();
            if ( status.equals(Environment.MEDIA_MOUNTED) ){
                File file = Environment.getExternalStorageDirectory();
                File temp = new File(file.getParent());
                filepath = new File(temp.getParent()+ File.separator +
                        "sdcard0" + File.separator + "Song_for_Run_in_Test.wav");

                Log.d(TAG, "start filePath = "+ filepath.getPath());
            }

            if ( !filepath.exists() ){
                Log.d(TAG, "SPEAKER_TEST_FAIL_INFO : Audio file does not exist");
                saveSharedPrefError();
                AlertDialog.Builder buidler = new AlertDialog.Builder(this);
                buidler.setTitle("Confirm to exit")
                    .setMessage("Audio file does not exist!")
                    .setPositiveButton("Confirm", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            dealwithError(mStatusText, "Audio file does not exist");
                        }
                    })
                    .create()
                    .show();
            }else{
                setViewConfigure();

                st = new SpeakerTest(this, filepath.getPath());
                st.start();

            }
        }


    }


    public void onResume()
    {
        if (mPositionWhenPaused >= 0)
        {
            videoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused =  - 1;
        }
        videoView.resume();

        super.onResume();
    }

    public void onPause()
    {
        mPositionWhenPaused = videoView.getCurrentPosition();
        videoView.pause();

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (videoView != null)
            videoView.stopPlayback();
        if (st != null)
            st.destroy();
    }

}
