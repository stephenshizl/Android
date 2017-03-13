package com.lenovo.freeanswer;

import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;

import android.util.Log;

public class SpeechEngine {
    private static final String TAG = "SpeechEngine";
    private static SpeechEngine mInstance = new SpeechEngine();

    protected Object mStatusMutex = new Object();
    protected boolean mLastStatusStart = false;
    protected Context mRequestContext = null;
    protected Intent mRequestIntent = null;

    protected Recorder mRecorder = new Recorder(SpeechConstant.DEFAULT_SAMPLE_RATE,
            SpeechConstant.DEFAULT_AUDIO_FORMAT);
    protected RecognitionManager mRecogManager = null;
    protected SpeechResultListener mSpeechResultListener = null;

    protected HandlerThread mHandlerThread = new HandlerThread(TAG);
    protected MyHandler mHandler = null;

    protected SpeechStatus mStatus = SpeechStatus.IDLE;
    
    protected static final int MESSAGE_ENGINE_START = 101;
    protected static final int MESSAGE_ENGINE_READY_FOR_SPEECH = 102;
    protected static final int MESSAGE_ENGINE_RESULT = 107;
    protected static final int MESSAGE_ENGINE_STOP = 110;
    
    protected static final int MESSAGE_RECORDER_START = 201;
    protected static final int MESSAGE_RECORDER_STOP = 202;
    protected static final int MESSAGE_RECORDER_ABANDON = 203;
    protected static final int MESSAGE_RECORDER_ERROR = 204;
    protected static final int MESSAGE_ENGINE_SHOULD_START_RECORD = 205; //add by wanghongyan for SALLY-3753 20150603

    protected boolean mRecorderStopped = false;
    protected boolean mWakeupStopped = false;

    private static boolean shouldStartRecorder = false; //add by wanghongyan for SALLY-3753 20150603
    
    public static synchronized SpeechEngine getInstance() {
        return mInstance;
    }

    protected SpeechEngine() {
    }

    public enum SpeechStatus {
        IDLE,
        STARTING,
        RUNNING,
        STOPPING
    };

    protected void setStatus(SpeechStatus s) {
        synchronized (mStatusMutex) {
            mStatus = s;
        }
    }

    public boolean start(Context context, Intent intent) {
        boolean ret = false;
        try {
            Log.d(TAG, "start : " + mStatus);
            mLastStatusStart = true;
            mRequestContext = context;
            mRequestIntent = intent;
            synchronized (mStatusMutex) {
                if(mStatus != SpeechStatus.IDLE) {
                    return false;
                }

                mStatus = SpeechStatus.STARTING;
                try {
                    if(mHandlerThread == null) {
                        mHandlerThread = new HandlerThread(TAG);
                    }
                    mHandlerThread.start();
                    mHandler = new MyHandler(mHandlerThread.getLooper());
                } catch (Exception e) {
                }

                mRecogManager = new RecognitionManager(context);
                mRecorder.setRecordListener(mRecordListener);
                mRecorder.start();
                mRecogManager.setWakeupListener(mWakeupListener);
                mRecogManager.start(intent);

                mRecorderStopped = false;
                mWakeupStopped = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "start Exception : " + e.getMessage());
            ret = false;
            stop(false);
        }
        return ret;
    }

    public boolean stop(boolean delay) {
        boolean ret = false;
        try {
            Log.d(TAG, "stop " + mStatus);
            mLastStatusStart = false;
             synchronized (mStatusMutex) {
                if (mStatus == SpeechStatus.IDLE || mStatus == SpeechStatus.STOPPING) {
                    return false;
                }

                mStatus = SpeechStatus.STOPPING;
                mRecorder.stop();
                mRecogManager.stop();
             }
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    protected void sendEmptyMessage(int msg) {
        mHandler.sendEmptyMessage(msg);
    }
    
    protected void sendMessage(int what, Object obj) {
        Message msg = mHandler.obtainMessage(what, obj);
        mHandler.sendMessage(msg);
    }

    public void sendResultMessage(String wakeKey) {
        Bundle results = new Bundle();
        results.putString(SpeechConstant.ENGINE_WAKEUP_WORD_KEY, wakeKey);
        Message msg = mHandler.obtainMessage(MESSAGE_ENGINE_RESULT, results);
        mHandler.sendMessage(msg);
    }

    public void setSpeechResultListener(SpeechResultListener listener) {
        mSpeechResultListener = listener;
    }

    protected void checkStopped() {
        synchronized(mStatusMutex) {
            Log.d(TAG, "checkStopped " + mRecorderStopped + " " + mWakeupStopped);
            if(mRecorderStopped && mWakeupStopped) {
                mStatus = SpeechStatus.IDLE;

                try {
                    if(mHandlerThread != null) {
                        mHandlerThread.quit();
                        mHandlerThread = null;
                    }

                    if(mLastStatusStart) {
                        if(mRequestContext != null && mRequestIntent != null) {
                            Log.d(TAG, "start for the last request");
                            start(mRequestContext, mRequestIntent);
                            mRequestContext = null;
                            mRequestIntent = null;
                        }
                        mLastStatusStart = false;
                    }
                } catch (Exception e) {
    
                }
            }
        }
    }

    //add by wanghongyan for SALLY-3753 20150603 start
    public boolean getStartRecorder() {
        return mSpeechResultListener.getStartRecorder();
    }
    //add by wanghongyan for SALLY-3753 20150603 end

    private RecorderListener mRecordListener = new RecorderListener() {
        @Override
        public void onStart(int code) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mRecordListener onStart " + code);
            if (code != SpeechConstant.SPEECH_ERROR_OK) {
                sendMessage(MESSAGE_RECORDER_START, code);
            }
            if (code == SpeechConstant.SPEECH_RECORDER_INIT_OK) {
                mRecogManager.onRecorderInit();
            }
        }

        @Override
        public void onReadyForSpeech() {
            // TODO Auto-generated method stub
            Log.d(TAG, "mRecordListener onReadyForSpeech ");
            sendEmptyMessage(MESSAGE_ENGINE_READY_FOR_SPEECH);
            mRecogManager.onRecorderInit();
        }

        @Override
        public void onBufferReceived(byte[] data, int offset, int size) {
            // TODO Auto-generated method stub
            // sendMessage(MESSAGE_ENGINE_BUFFER_RECEIVED, data);
            if (mRecogManager != null) {
                mRecogManager.addData(data, offset, size);
            }
        }

        @Override
        public void onRmsChanged(float rms) {
            // TODO Auto-generated method stub
            // sendEmptyMessage(MESSAGE_ENGINE_RMS_CHANGED);
        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mRecordListener onError ");
            mRecorderStopped = true;
            sendMessage(MESSAGE_RECORDER_ERROR, error);
        }

        @Override
        public void onStop() {
            // TODO Auto-generated method stub
            Log.d(TAG, "mRecordListener onStop ");
            mRecorderStopped = true;
            sendEmptyMessage(MESSAGE_RECORDER_STOP);
        }

        @Override
        public void onAbandon() {
            // TODO Auto-generated method stub
            mRecorderStopped = true;
            sendEmptyMessage(MESSAGE_RECORDER_ABANDON);
        }

        //add by wanghongyan for SALLY-3753 20150603 start
        @Override
        public boolean getStartRecorder() {
            shouldStartRecorder = mSpeechResultListener.getStartRecorder();
            return shouldStartRecorder;
        }
        //add by wanghongyan for SALLY-3753 20150603 end
    };

    private RecognizerListener mWakeupListener = new RecognizerListener() {
        @Override
        public void onStart(int code) {
            Log.d(TAG, "mWakeupListener onStart");
            // TODO Auto-generated method stub
            if (code != SpeechConstant.SPEECH_ERROR_OK) {
                mWakeupStopped = true;
                sendMessage(MESSAGE_ENGINE_START, code);
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
            Log.d(TAG, "mWakeupListener onBeginningOfSpeech");

        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "mWakeupListener onEndOfSpeech");
            // TODO Auto-generated method stub

        }

        @Override
        public void onResult(RecognitionResult result) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mWakeupListener onResult " + result.mErrorCode);
            sendResultMessage(result.mResult[0]);
        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "mWakeupListener onError ");
            // TODO Auto-generated method stub
            if (error == SpeechConstant.SPEECH_ENGINE_INIT_FAILED ||
                    error == SpeechConstant.SPEECH_ENGINE_OPEN_FAILED ||
                    error == SpeechConstant.SPEECH_ENGINE_ADD_INTENT_FAILED) {
                sendMessage(MESSAGE_ENGINE_START, error);
                return;
            }
        }

        
        @Override
                public void onStop() {
                    Log.d(TAG, "mWakeupListener onStop ");
                    // TODO Auto-generated method stub
                    mWakeupStopped = true;
                    sendEmptyMessage(MESSAGE_ENGINE_STOP);
                }
    };

    protected class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Handler Message " + msg.what);
            Log.d(TAG, "HandlerMessage() mSpeechResultListener = " + mSpeechResultListener);
            if (mSpeechResultListener == null) {
                return;
            }

            switch (msg.what) {
                case MESSAGE_RECORDER_START:
                    if(mSpeechResultListener != null) {
                        int code = (Integer) msg.obj;
                        mSpeechResultListener.onStart(code);
                        mRecorderStopped = true;
                    }
                    break;
                case MESSAGE_RECORDER_STOP:
                    if(mSpeechResultListener != null) {
                        mSpeechResultListener.onStop();
                    }
                    Log.d(TAG, "MESSAGE_RECORDER_STOP : " + mStatus);
                    checkStopped();
                    break;
                case MESSAGE_RECORDER_ABANDON:
                    if(mSpeechResultListener != null) {
                        mSpeechResultListener.onAbandon();
                    }
                    Log.d(TAG, "MESSAGE_RECORDER_ABANDON : " + mStatus);
                    checkStopped();
                    break;
                case MESSAGE_RECORDER_ERROR:
                    if(mSpeechResultListener != null) {
                        int error = (Integer) msg.obj;
                        mSpeechResultListener.onError(error);
                    }
                    Log.d(TAG, "MESSAGE_RECORDER_ERROR : " + mStatus);
                    checkStopped();
                    break;
                case MESSAGE_ENGINE_START:
                    if(mSpeechResultListener != null) {
                        int code = (Integer) msg.obj;
                        mSpeechResultListener.onStart(code);
                    }
                    break;
                case MESSAGE_ENGINE_READY_FOR_SPEECH:
                    setStatus(SpeechStatus.RUNNING);
                    if(mSpeechResultListener != null) {
                        mSpeechResultListener.onReadyForSpeech(null);
                    }
                    break;
                case MESSAGE_ENGINE_RESULT:
                    if(mSpeechResultListener != null) {
                        Bundle results = (Bundle) msg.obj;
                        mSpeechResultListener.onResults(results);
                    }
                    break;
                case MESSAGE_ENGINE_STOP:
                    checkStopped();
                    break;
                default:
                    break;
            }
        }
    };
}
