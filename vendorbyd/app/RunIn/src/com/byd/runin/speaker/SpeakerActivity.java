
package com.byd.runin.speaker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;

import com.byd.runin.SharedPref;
import com.byd.runin.TestActivity;
import com.byd.runin.TestActivity.TestStatus;
import com.byd.runin.TestLog.Log;
import com.byd.runin.R;

import java.io.File;
import java.io.IOException;

public class SpeakerActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_speaker_test";
    public static final String TITLE = "Speaker Test";
    private static final String TAG = "SpeakerActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_speaker_test_time";
    private static final String SPEAKER_TEST_FAIL_INFO = "speaker test fail: ";

    private MediaPlayer mMediaPlayer;
    private static String mTestString = "Audio is playing ....";
    private String path = null;
    private AudioManager audioManager = null;
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);
        mMediaPlayer = new MediaPlayer();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //save volume
        current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        play(path);
        loop();
    }
    private void play(String path)
    {
        try {
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
                mMediaPlayer.setDataSource(filepath.getPath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mStatusText.setText(mTestString);
            }

        } catch (IllegalArgumentException e) {
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
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
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
