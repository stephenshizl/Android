
package com.byd.runin.mic;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.byd.runin.TestLog.Log;
import com.byd.runin.camera.CameraActivity;

import java.io.File;
import java.io.IOException;

public class MicTest
{
    Context mContext;
    TextView mStatusText;

    CameraActivity ca = null;
    public static final String TAG = "MicTest";
    private static final long MIC_RECORD_TIME = 60 * 1000;
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_mic_test_time";
    private static final String FILE_PATH = "/data/data/com.byd.runin/";
    private static final String MIC_TEST_FAIL_INFO = "mic test fail: ";
    private static final String RECORD_FILE_NAME = "micRec";
    private String mic_rec_file_path = null;
    private MediaRecorder recorder = null;
    private static final int MIC_REC_STOP_MSG = 1;

    public MicTest(Context context)
    {
        mContext = context;
        ca = (CameraActivity)mContext;
        mStatusText = ca.mStatusText;
    }

    public void release()
    {
        mHandler.removeMessages(MIC_REC_STOP_MSG);
        stopRecord();
        recorder = null;
        deleteRecordFile();
    }

    public void startRecord()
    {
        File recordRecAudioDir = new File(FILE_PATH);
        if (!recordRecAudioDir.exists())
        {
            if (!recordRecAudioDir.mkdir())
            {
                Log.d(TAG, "create file fail!");
                ca.dealwithError(mStatusText, MIC_TEST_FAIL_INFO + "mic test fail......");
                return ;
            }
        }

        File myRecAudioFile = null;
        try
        {
            myRecAudioFile = File.createTempFile(RECORD_FILE_NAME, ".amr",
                recordRecAudioDir);
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
            }
            else
            {
                Log.d(TAG, "create mic rec file path fail!");
                ca.dealwithError(mStatusText, MIC_TEST_FAIL_INFO + "create mic rec file path fail!");
                return ;
            }
        }
        catch (IOException e)
        {
            Log.d(TAG, e.getMessage());
            recorder.reset();
            recorder.release();
            recorder = null;
            ca.dealwithError(mStatusText, MIC_TEST_FAIL_INFO + e.getMessage());
            return ;
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.d(TAG, e.getMessage());
            recorder.reset();
            recorder.release();
            recorder = null;
            ca.dealwithError(mStatusText, MIC_TEST_FAIL_INFO + e.getMessage());
            return ;
        }

        mHandler.sendEmptyMessageDelayed(MIC_REC_STOP_MSG, MIC_RECORD_TIME);

    }

    public Handler mHandler = new Handler()
    {

        @Override public void handleMessage(Message msg)
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
                        ca.dealwithError(mStatusText, MIC_TEST_FAIL_INFO + "delete mic record file fail!");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };


    private void stopRecord()
    {
        if (recorder != null)
        {
            recorder.stop();
            recorder.reset();
            recorder.release();
        }
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
}
