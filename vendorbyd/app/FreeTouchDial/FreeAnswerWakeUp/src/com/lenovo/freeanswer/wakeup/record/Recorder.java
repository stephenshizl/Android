
package com.lenovo.freeanswer.wakeup.record;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.lenovo.freeanswer.wakeup.WakeUpManager;
import com.lenovo.freeanswer.wakeup.speech.SpeechConstant;
import com.lenovo.freeanswer.wakeup.speech.SpeechException;
import com.lenovo.freeanswer.wakeup.util.AppLog;

public class Recorder implements IRecorder {

    private static final String TAG = "Recorder";

    private RecorderListener mRecordListener = null;
    private static final Object mRecorderMutex = new Object();
    private volatile boolean mWorking = false;

    private enum RecordStatus {
        IDLE,
        STARTING,
        WORKING
    };

    private RecordStatus mStatus = RecordStatus.IDLE;
    private Object mStatusMutex = new Object();

    private int mSample = 16000;
	private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private boolean mSwitchMode = false;
    private boolean mEc = true; // 是否回声消除

    public Recorder(int sample, int format) {
        mSample = sample;
        mAudioFormat = format;
    }

    protected void setStatus(RecordStatus s) {
        synchronized (mStatusMutex) {
            mStatus = s;
        }
    }

    public RecordStatus getStatus() {
        synchronized (mStatusMutex) {
            return mStatus;
        }
    }

    public void switchMode(boolean ec) {
        mSwitchMode = true;
        mEc = ec;
    }

    private volatile Runnable mWorkRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
        	int tryTimes;
        	boolean isErrorExisted;
        	
        	tryTimes = 0;
        	while(true) {
	            synchronized (mRecorderMutex) {
	            	setStatus(RecordStatus.STARTING);
	            	
	            	isErrorExisted = false;
	            	tryTimes++;
	            	
	                AppLog.d(TAG, "mWorkThread Started.");
	                long start = System.currentTimeMillis();
	                AudioRecord record = null;
	                AudioManager am = null;
	                try {
	                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
	
	                    am = (AudioManager) WakeUpManager.getContext().getSystemService(
	                            Context.AUDIO_SERVICE);
	                    if (am != null) {
	                        // 确保正确的录音模式
	                        if (am.getMode() == AudioManager.MODE_RINGTONE) {
	                            AppLog.e(TAG, "mWorkThread Invalid Audio Mode " + am.getMode());
	                            am.setMode(AudioManager.MODE_NORMAL);
	                            AppLog.ex(new SpeechException("Invalid Audio Mode in Recorder"));
	                        }
	                        // 使用优化的音频系统，三种模式：50NS、20EC、CAR；以及一种系统默认的MIC
	                        if (mEc) {
	                            am.setParameters("smartlevoice=20EC");
	                        } else {
	                            am.setParameters("smartlevoice=50NS");
	                        }
	
	                    }
	
	                    // Create Recorder and Start Recording...
	                    int bufferSize = AudioRecord.getMinBufferSize(
	                            mSample, AudioFormat.CHANNEL_IN_MONO, mAudioFormat);
	                    AppLog.d(TAG, "AudioRecord.getMinBufferSize " + bufferSize);
	
	                    Thread.sleep(10);
	                    int src = MediaRecorder.AudioSource.VOICE_RECOGNITION;
	                    
	                    record = new AudioRecord(
	                            src,
	                            mSample,
	                            AudioFormat.CHANNEL_IN_MONO,
	                            mAudioFormat,
	                            SpeechConstant.DEFAULT_RECORDER_BUFFER_SIZE);
	
	                    if (mRecordListener != null) {
	                        mRecordListener.onStart(SpeechConstant.SPEECH_RECORDER_INIT_OK);
	                    }
	
	                    Thread.sleep(10);
	                    record.startRecording();
	                    AppLog.d(TAG, "startRecording() " + record.getAudioSessionId() + " cost "
	                            + (System.currentTimeMillis() - start));
	
	                    setStatus(RecordStatus.WORKING);
	
	                    boolean first = true;
	
	                    // 读取和识别使用同样大小的SLOT
	                    final byte[] buffer = new byte[SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE];
	                    while (mWorking) {
	                        int readSize = record.read(buffer, 0, buffer.length);
	
	                        if (readSize > 0) {
	                            if (first) {
	                                first = false;
	                                AppLog.d(TAG, "onReadyForSpeech "
	                                        + (System.currentTimeMillis() - start)
	                                        + " mRecordListener " + mRecordListener);
	                                if (mRecordListener != null) {
	                                    mRecordListener.onReadyForSpeech();
	                                }
	                            }
	                            if (mRecordListener != null) {
	                                mRecordListener.onBufferReceived(buffer, 0, readSize);
	                            }
	                        } else {
	                            AppLog.d(TAG, "record.read return " + readSize);
	                            if (mRecordListener != null) {
	                                mRecordListener
	                                        .onError(SpeechConstant.SPEECH_RECORDER_RUNNING_FAILED);
	                            }
	                        }
	
	                        // 切换Mode，使用50NS或20EC
	                        if (mSwitchMode) {
	                            AppLog.d(TAG, "SwitchMode " + mEc);
	                            long begin = System.currentTimeMillis();
	                            record.stop();
	                            if (mEc) {
	                                am.setParameters("smartlevoice=20EC");
	                            }
	                            else {
	                                am.setParameters("smartlevoice=50NS");
	                            }
	                            record.startRecording();
	                            mSwitchMode = false;
	                            AppLog.d(TAG, "SwitchMode Cost " + (System.currentTimeMillis() - begin)
	                                    + ", " + am.getParameters("smartlevoice"));
	                        }
	                    }
	                } catch (Exception e) {
	                    if (mRecordListener != null) {
	                        mRecordListener.onStart(SpeechConstant.SPEECH_RECORDER_INIT_FAILED);
	                    }
	                    AppLog.d(TAG, e.getMessage());
	                    if(getStatus() != RecordStatus.WORKING) {
	                    	isErrorExisted = true;
	                    }
	                } finally {
	                    AppLog.d(TAG, "WorkThread finished.");
	                    setStatus(RecordStatus.IDLE);
	                    if (mRecordListener != null) {
	                        mRecordListener.onStop();
	                    }
	                    if (record != null) {
	                        try {
	                            record.stop();
	                            record.release();
	                            record = null;
	                        }
	                        catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                    if (am != null) {
	                        am.setParameters("smartlevoice=off");
	                    }
	                }
	            }
	            if(isErrorExisted) {
	            	if(tryTimes >= 2) break;
	            	try {
	            		Thread.sleep(1000);
	            	} catch(Exception ex) {
	            	}
	            } else {
	            	break;
	            }
	            AppLog.d(TAG, "Error existed, retry once more.");
        	} // End while
        }
    };

    public void setRecordListener(RecorderListener listener) {
        mRecordListener = listener;
    }

    @Override
    public void start() throws SpeechException {
        // TODO Auto-generated method stub
        if (getStatus() != RecordStatus.IDLE) {
            return;
        }
        AppLog.d(TAG, "start");
        mWorking = true;
        setStatus(RecordStatus.STARTING);

        Thread workThread = new Thread(mWorkRunnable);
        workThread.start();
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        if (getStatus() == RecordStatus.IDLE) {
            return;
        }
        AppLog.d(TAG, "stop");
        mWorking = false;
    }

}
