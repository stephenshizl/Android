
package com.byd.runin;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity;
import com.byd.runin.TestActivity.TestStatus;
import com.byd.runin.TestLog.Log;
import com.byd.runin.R;
import java.io.IOException;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.View;
public class SpkReceiverPlay extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_speaker_test";
    public static final String TITLE = "Speaker Receiver Test";
    private static final String TAG = "SpeakerActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_speaker_test_time";
    private static final String SPEAKER_TEST_FAIL_INFO = "speaker test fail: ";

    private MediaPlayer mMediaPlayer;
    private static String mTestString = "Audio is playing ....";
    private String path = null;
    private AudioManager audioManager = null;
    private static final int SET_SPKREV = 0;
    private Button bttnplay,bttnstop;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.spkrev);
        bttnplay = (Button)findViewById(R.id.bttnplay);
        bttnplay.setOnClickListener(BttnPlayListener);

        bttnstop = (Button)findViewById(R.id.bttnstop);
        bttnstop.setOnClickListener(BttnStopListener);
           try {
                Runtime.getRuntime().exec("system/bin/mm-audio-ftm -tc 52 -c /system/etc/ftm_test_config -d -1 -v 100 -fl 2000 -fh 2000");
                } catch(IOException e) {
            Log.d(TAG, "SpkReceiverPlay IOException  " );
                    }
        mMediaPlayer = MediaPlayer.create(this, R.drawable.iec2685);
        audioManager = (AudioManager)this.getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }
    private void play(String path)
    {
        try
        {
            //mMediaPlayer.setDataSource(path);
            mMediaPlayer.stop();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mStatusText.setText(mTestString);
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalArgumentException : " + e.getMessage());
            dealwithError(mStatusText, SPEAKER_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            dealwithError(mStatusText, SPEAKER_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            dealwithError(mStatusText, SPEAKER_TEST_FAIL_INFO + e.getMessage());
        }
    }

    private void loop()
    {
        mMediaPlayer.setLooping(true);
    }

    private void stop()
    {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.stop();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
    }
    private OnClickListener BttnPlayListener = new OnClickListener() {
        public void onClick(View v) {
            play(path);
            loop();
       }
    };

    private OnClickListener BttnStopListener = new OnClickListener() {
        public void onClick(View v) {
            stop();
            finish();
        }
    };
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
