
package com.byd.runin.receiver;

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
import com.byd.runin.lcd.LcdActivity;


import java.io.File;
import java.io.IOException;

public class ReceiverTest
{
    private static final String TAG = "ReceiverActivity";
    private static final String REC_TEST_FAIL_INFO = "receiver test fail: ";

    private MediaPlayer mMediaPlayer;
    private AudioManager audioManager = null;
    private TextView mStatusText;
    Context mContext;
    int current;
    String mPath = null;

    private LcdActivity lcd = null;

    public ReceiverTest(Context context, String path)
    {
        mContext = context;
        lcd = (LcdActivity)mContext;
        mStatusText = lcd.mStatusText;
        mMediaPlayer = new MediaPlayer();
        mPath = path;
        //mMediaPlayer.setAudioStreamType(1);
        audioManager = (AudioManager)mContext.getSystemService
            (Context.AUDIO_SERVICE);

        //save volume
        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 15,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);

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
            //mMediaPlayer.reset();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalArgumentException : " + e.getMessage());
            lcd.dealwithError(mStatusText, REC_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            lcd.dealwithError(mStatusText, REC_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, "IllegalStateException : " + e.getMessage());
            lcd.dealwithError(mStatusText, REC_TEST_FAIL_INFO + e.getMessage());
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
