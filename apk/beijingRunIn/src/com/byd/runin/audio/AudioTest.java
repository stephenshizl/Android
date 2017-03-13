
package com.byd.runin.audio;

import java.io.FileDescriptor;
import java.io.IOException;
import android.media.AudioManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class AudioTest
{
    Context mContext;
    MediaPlayer mCurrentMediaPlayer;
    MediaPlayer mNextMediaPlayer;
    AudioManager mAudioManager;
    private AssetFileDescriptor mAssertFileDescriptor = null;

    public AudioTest(Context context, MediaPlayer mediaPlayer)
    {
        mContext = context;
        if (mCurrentMediaPlayer == null)
            mCurrentMediaPlayer = new MediaPlayer();

        mAudioManager = new AudioManager(context);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15,
            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public void startMusic()
    {
        try
        {
            if (mAssertFileDescriptor != null)
            {
                mAssertFileDescriptor.close();
                mAssertFileDescriptor = null;
            }
            mAssertFileDescriptor = mContext.getAssets().openFd("audio.mp3");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        play(mAssertFileDescriptor.getFileDescriptor());
    }

    public void play(FileDescriptor fileDescriptor)
    {
        try
        {
            mCurrentMediaPlayer.reset();
            mCurrentMediaPlayer.setDataSource(fileDescriptor);
            mCurrentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCurrentMediaPlayer.prepare();
            mCurrentMediaPlayer.start();
            mCurrentMediaPlayer.setOnCompletionListener(listener);

        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    MediaPlayer.OnCompletionListener listener = new
        MediaPlayer.OnCompletionListener()
    {

        @Override
        public void onCompletion(MediaPlayer mp)
        {
            // TODO Auto-generated method stub
            restartPlay();

        }
    };

    private void restartPlay()
    {
        try
        {
            if (mCurrentMediaPlayer == null)
            {
                mCurrentMediaPlayer = new MediaPlayer();
            }
            else
            {
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = null;
                mCurrentMediaPlayer = new MediaPlayer();
            }

            mCurrentMediaPlayer.reset();
            if (mAssertFileDescriptor != null)
            {
                mAssertFileDescriptor.close();
                mAssertFileDescriptor = null;
            }
            mAssertFileDescriptor = mContext.getAssets().openFd("audio.mp3");
            mCurrentMediaPlayer.setDataSource
                (mAssertFileDescriptor.getFileDescriptor());
            mCurrentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCurrentMediaPlayer.prepare();
            mCurrentMediaPlayer.start();
            mCurrentMediaPlayer.setOnCompletionListener(listener);

        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void start()
    {
        mCurrentMediaPlayer.start();
    }

    public void pause()
    {
        mCurrentMediaPlayer.pause();
    }

    public void stop()
    {
        if (mCurrentMediaPlayer != null)
        {
            mCurrentMediaPlayer.stop();
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mCurrentMediaPlayer.release();
            mCurrentMediaPlayer = null;
        }

        try
        {
            if (mAssertFileDescriptor != null)
            {
                mAssertFileDescriptor.close();
                mAssertFileDescriptor = null;
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
