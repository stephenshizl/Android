package com.lenovo.freeanswer;

import android.content.Context;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;

import android.util.Log;

public class Recorder implements IRecorder {
    private static final String TAG = "Recorder";

    private RecorderListener mRecordListener = null;
    private static final Object mRecorderMutex = new Object();

    private volatile boolean mWorking = false;
    private RecordStatus mStatus = RecordStatus.IDLE;
    private Object mStatusMutex = new Object();

    private int mSample = 16000;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    
    private boolean mSwitchMode = false;
    private boolean mEc = true;

    private enum RecordStatus {
        IDLE,
        STARTING,
        WORKING
    };

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

    private volatile Runnable mWorkRunnable = new Runnable() {
        @Override
        public void run() {
            int tryTimes = 0;
            boolean isErrorExisted;

            while(true) {
                synchronized (mRecorderMutex) {
                    setStatus(RecordStatus.STARTING);
                    isErrorExisted = false;
                    tryTimes ++;

                    Log.d(TAG, "mWorkThread Started.");
                    long start = System.currentTimeMillis();
                    AudioRecord record = null;
                    AudioManager am = null;
                    try {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                        am = (AudioManager) WakeUpManager.getContext().getSystemService(Context.AUDIO_SERVICE);
                        if(am != null) {
                            if (am.getMode() == AudioManager.MODE_RINGTONE) {
	                            Log.e(TAG, "mWorkThread Invalid Audio Mode " + am.getMode());
	                            am.setMode(AudioManager.MODE_NORMAL);
                            }

                            if (mEc) {
                                am.setParameters("smartlevoice=20EC");
                            } else {
                                am.setParameters("smartlevoice=50NS");
                            }
                        }

                        // Create Recorder and Start Recording...
                        int bufferSize = AudioRecord.getMinBufferSize(
                            mSample, AudioFormat.CHANNEL_IN_MONO, mAudioFormat);
                        Log.d(TAG, "AudioRecord.getMinBufferSize " + bufferSize);
	
                        Thread.sleep(10);
                        int src = MediaRecorder.AudioSource.MIC;

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
                        Log.d(TAG, "startRecording() " + record.getAudioSessionId() + " cost "
                            + (System.currentTimeMillis() - start));
	
                        setStatus(RecordStatus.WORKING);
	
                        boolean first = true;

                        final byte[] buffer = new byte[SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE];
                        while (mWorking) {
                            int readSize = record.read(buffer, 0, buffer.length);

                            if (readSize > 0) {
                                if(first) {
                                    first = false;
                                    Log.d(TAG, "onReadyForSpeech "
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
                                Log.d(TAG, "record.read return " + readSize);
                                if (mRecordListener != null) {
                                    mRecordListener.onError(SpeechConstant.SPEECH_RECORDER_RUNNING_FAILED);
                                }
                            }

                            if (mSwitchMode) {
                                Log.d(TAG, "SwitchMode " + mEc);
                                long begin = System.currentTimeMillis();
                                record.stop();
                                if (mEc) {
                                    am.setParameters("smartlevoice=20EC");
                                } else {
                                    am.setParameters("smartlevoice=50NS");
                                }
                                record.startRecording();
                                mSwitchMode = false;
                                Log.d(TAG, "SwitchMode Cost " + (System.currentTimeMillis() - begin)
                                    + ", " + am.getParameters("smartlevoice"));
                            }
                        }
                    } catch (Exception e) {
                        if (mRecordListener != null) {
                            mRecordListener.onStart(SpeechConstant.SPEECH_RECORDER_INIT_FAILED);
                        }
                        Log.d(TAG, e.getMessage());
                        if(getStatus() != RecordStatus.WORKING) {
                            isErrorExisted = true;
                        }
                    } finally {
                        Log.d(TAG, "WorkThread finished.");
                        setStatus(RecordStatus.IDLE);
                        if (mRecordListener != null) {
                            mRecordListener.onStop();
                        }

                        if (record != null) {
                            try {
                                record.stop();
                                record.release();
                                record = null;
                            } catch (Exception e) {
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
                    } catch(Exception ex) {}
                } else {
                    break;
                }
                Log.d(TAG, "Error existed, retry once more");
            }
        }
    };

    public void setRecordListener(RecorderListener listener) {
        mRecordListener = listener;
    }

    public void start() throws SpeechException {
        // TODO Auto-generated method stub
        //add by wanghongyan for SALLY-3753 20150603 start
        boolean shouldStartRecorder = mRecordListener.getStartRecorder();
        Log.d(TAG, "start() shouldStartRecorder = " + shouldStartRecorder);
        //add by wanghongyan for SALLY-3753 20150603 end
        if (getStatus() != RecordStatus.IDLE || !shouldStartRecorder) {
            return;
        }
        Log.d(TAG, "start");
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
        Log.d(TAG, "stop");
        mWorking = false;
    }
}

