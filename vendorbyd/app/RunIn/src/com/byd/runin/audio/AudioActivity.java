
package com.byd.runin.audio;

import java.io.FileDescriptor;
import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import com.byd.runin.TestActivity;

public class AudioActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_audio_test";
    public static final String TITLE = "Audio Test";

    private MediaPlayer mMediaPlayer;
    private TextView mErrorTextView;
    private static String mErrorText = "AudioTest Faild";
    private static String mTestString = "Audio is playing ....";
    private FileDescriptor mFileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);
        mErrorTextView = new TextView(this);
        setContentView(mErrorTextView);
        mErrorTextView.setText("");
        mMediaPlayer = new MediaPlayer();
        //mMediaPlayer.setAudioStreamType(1);
        try
        {
            mFileDescriptor = getAssets().openFd("audio.mp3").getFileDescriptor
                ();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            mErrorTextView.setText(mErrorText);
            e.printStackTrace();
        }
        play(mFileDescriptor);
        loop();
    }
    private void play(FileDescriptor fileDescriptor)
    {
        try
        {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(fileDescriptor);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mErrorTextView.setText(mTestString);
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            mErrorTextView.setText(mErrorText);
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            mErrorTextView.setText(mErrorText);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            mErrorTextView.setText(mErrorText);
            e.printStackTrace();
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
