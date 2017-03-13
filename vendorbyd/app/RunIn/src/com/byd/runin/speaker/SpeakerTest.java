
package com.byd.runin.speaker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.widget.TextView;

import com.byd.runin.TestLog.Log;
import com.byd.runin.video.VideoActivity;

import java.io.File;
import java.io.IOException;

public class SpeakerTest
{
    Context mContext;
    private static final String TAG = "SpeakerActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_speaker_test_time";
    private static final String SPEAKER_TEST_FAIL_INFO = "speaker test fail: ";

    private MediaPlayer mMediaPlayer;
    private AudioManager audioManager = null;
    private VideoActivity va = null;
    private TextView mStatusText;
    int current;
    String mPath = null;

    public SpeakerTest(Context context, String path)
    {
        mContext = context;
        mPath = path;
        va = (VideoActivity)mContext;
        mStatusText = va.mStatusText;
        mMediaPlayer = new MediaPlayer();

        audioManager = (AudioManager)mContext.getSystemService
            (Context.AUDIO_SERVICE);
        //save volume
        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }

    public void start()
    {
        play();
        loop();
    }

    private void play()
    {
        try
        {
            mMediaPlayer.setDataSource(mPath);
            //mMediaPlayer.stop();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalArgumentException : " + e.getMessage());
            va.dealwithError(mStatusText, SPEAKER_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            va.dealwithError(mStatusText, SPEAKER_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            va.dealwithError(mStatusText, SPEAKER_TEST_FAIL_INFO + e.getMessage());
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
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
    }

    public void destroy()
    {
        stop();

    }
}
