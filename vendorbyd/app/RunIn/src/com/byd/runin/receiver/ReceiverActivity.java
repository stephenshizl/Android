
package com.byd.runin.receiver;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity;
import com.byd.runin.TestActivity.TestStatus;
import com.byd.runin.TestLog.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import com.byd.runin.R;

public class ReceiverActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_receiver_test";
    public static final String TITLE = "Receiver Test";
    private static final String TAG = "ReceiverActivity";
     public static final String KEY_SHARED_PREF_TEST_TIME = "key_receiver_test_time";
    private static final String REC_TEST_FAIL_INFO = "receiver test fail: ";

    private MediaPlayer mMediaPlayer;
    private static String mTestString = "Audio is playing ....";
    private String path = null;
    private AudioManager audioManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);
        mMediaPlayer = MediaPlayer.create(this, R.raw.receiver);
        //mMediaPlayer.setAudioStreamType(1);
        audioManager = (AudioManager)this.getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 15,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        play(path);
        loop();
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
            dealwithError(mStatusText, REC_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            dealwithError(mStatusText, REC_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            dealwithError(mStatusText, REC_TEST_FAIL_INFO + e.getMessage());
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stop();

    }
}
