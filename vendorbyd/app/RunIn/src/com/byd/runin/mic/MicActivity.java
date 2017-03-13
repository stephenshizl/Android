
package com.byd.runin.mic;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;

import java.io.File;
import java.io.IOException;

public class MicActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_mic_test";
    public static final String TITLE = "MIC Test";

    public static final String TAG = "MicActivity";
    private static final long MIC_RECORD_TIME = 60 * 1000;
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_mic_test_time";
    private static final String FILE_PATH = "/data/data/com.byd.runin/";
    private static final String MIC_TEST_FAIL_INFO = "mic test fail: ";
    private static final String RECORD_FILE_NAME = "micRec";
    private String mic_rec_file_path = null;
    private MediaRecorder recorder = null;
    private static final int MIC_REC_STOP_MSG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;
        super.onCreate(savedInstanceState);

        startRecord();
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeMessages(MIC_REC_STOP_MSG);

        stopRecord();
        recorder = null;
        deleteRecordFile();

    }

    private boolean deleteRecordFile()
    {
        File file = new File(mic_rec_file_path);
        if (file != null && file.exists())
        {
            return file.delete();
        }

        return false;

    }

    private void stopRecord()
    {
        if (recorder != null)
        {
            recorder.stop();
            recorder.reset();
            recorder.release();
        }
    }

    private void startRecord()
    {
        File recordRecAudioDir = new File(FILE_PATH);
        if (!recordRecAudioDir.exists())
        {
            if (!recordRecAudioDir.mkdir())
            {
                Log.d(TAG, "create file fail!");
                dealwithError(mStatusText, MIC_TEST_FAIL_INFO + "mic test fail......");
                return ;
            }
        }

        File myRecAudioFile = null;
        try
        {
            myRecAudioFile = File.createTempFile(RECORD_FILE_NAME, ".amr",recordRecAudioDir);
            if (myRecAudioFile != null)
            {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mic_rec_file_path = myRecAudioFile.getAbsolutePath();
                recorder.setOutputFile(mic_rec_file_path);
                recorder.prepare();
                recorder.start();

                mStatusText.setText("mic test......");
            }
            else
            {
                Log.d(TAG, "create mic rec file path fail!");
                dealwithError(mStatusText, MIC_TEST_FAIL_INFO + "create mic rec file path fail!");
                return ;
            }
        }
        catch (IOException e)
        {
            Log.d(TAG, e.getMessage());
            recorder.reset();
            recorder.release();
            recorder = null;
            dealwithError(mStatusText, MIC_TEST_FAIL_INFO + e.getMessage());
            return ;
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, e.getMessage());
            recorder.reset();
            recorder.release();
            recorder = null;
            dealwithError(mStatusText, MIC_TEST_FAIL_INFO + e.getMessage());
            return ;
        }

        mHandler.sendEmptyMessageDelayed(MIC_REC_STOP_MSG, MIC_RECORD_TIME);

    }

    public Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MIC_REC_STOP_MSG:
                    stopRecord();
                    if (deleteRecordFile())
                    {
                        Log.d(TAG, "delete file success!");
                        startRecord();
                    }
                    else
                    {
                        Log.d(TAG, "delete mic record file fail!");
                        dealwithError(mStatusText, MIC_TEST_FAIL_INFO + "delete mic record file fail!");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

}
