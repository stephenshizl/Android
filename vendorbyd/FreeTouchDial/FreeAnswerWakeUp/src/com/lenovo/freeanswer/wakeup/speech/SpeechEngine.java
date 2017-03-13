
package com.lenovo.freeanswer.wakeup.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.lenovo.freeanswer.wakeup.recognition.RecognitionManager;
import com.lenovo.freeanswer.wakeup.recognition.RecognizerListener;
import com.lenovo.freeanswer.wakeup.record.Recorder;
import com.lenovo.freeanswer.wakeup.record.RecorderListener;
import com.lenovo.freeanswer.wakeup.result.RecognitionResult;
import com.lenovo.freeanswer.wakeup.util.AppLog;

public class SpeechEngine {

    private static final String TAG = "SpeechEngine";

    private static SpeechEngine mInstance = new SpeechEngine();

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

    protected SpeechStatus mStatus = SpeechStatus.IDLE;
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

    protected boolean mRecorderStopped = false;
    protected boolean mWakeupStopped = false;

    // protected boolean mRecogStopped = false;

    protected void setStatus(SpeechStatus s) {
        synchronized (mStatusMutex) {
            mStatus = s;
        }
    }

    public boolean isIdle() {
        return mStatus == SpeechStatus.IDLE;
    }

    public boolean start(Context context, Intent intent) {
        boolean ret = false;
        try {
            AppLog.d(TAG, "start " + mStatus);
            // mHandler.removeCallbacks(mStopRunnable);
            mLastStatusStart = true;
            mRequestContext = context;
            mRequestIntent = intent;
            synchronized (mStatusMutex) {
                if (mStatus != SpeechStatus.IDLE) {
                    return false;
                }
                mStatus = SpeechStatus.STARTING;
                try {
                    if (mHandlerThread == null) {
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
                // mRecogManager.setRecognizerListener(mRecognizerListener);
                mRecogManager.start(intent);

                mRecorderStopped = false;
                mWakeupStopped = false;
                // mRecogStopped = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.ex(e);
            AppLog.d(TAG, "start Exception " + e.getMessage());
            ret = false;
            stop(false);
        }

        return ret;
    }

    public boolean stop(boolean delay) {
        boolean ret = false;
        try {
            AppLog.d(TAG, "stop " + mStatus);
            // mHandler.removeCallbacks(mStopRunnable);
            mLastStatusStart = false;
            synchronized (mStatusMutex) {
                if (mStatus == SpeechStatus.IDLE || mStatus == SpeechStatus.STOPPING) {
                    return false;
                }
                mStatus = SpeechStatus.STOPPING;
                /*
                 * if (delay) { mHandler.postDelayed(mStopRunnable,
                 * SpeechConstant.ENGINE_STOP_DELAY); } else
                 */{
                    mRecorder.stop();
                    mRecogManager.stop();
                }
            }
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public void setSpeechResultListener(SpeechResultListener listener) {
        mSpeechResultListener = listener;
    }

    protected void checkStopped() {
        synchronized (mStatusMutex) {
            AppLog.d(TAG, "checkStopped " + mRecorderStopped +" "+ mWakeupStopped);
            if (mRecorderStopped && mWakeupStopped) {
                mStatus = SpeechStatus.IDLE;

                try {
                    if (mHandlerThread != null) {
                        mHandlerThread.quit();
                        mHandlerThread = null;
                    }

                    // 最后请求是一个start, 但是被stop阻止
                    if (mLastStatusStart) {
                        if (mRequestContext != null && mRequestIntent != null) {
                            AppLog.d(TAG, "start for the last request.");
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

    private RecorderListener mRecordListener = new RecorderListener() {

        @Override
        public void onStart(int code) {
            // TODO Auto-generated method stub
            AppLog.d(TAG, "mRecordListener onStart " + code);
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
            AppLog.d(TAG, "mRecordListener onReadyForSpeech ");
            sendEmptyMessage(MESSAGE_ENGINE_READY_FOR_SPEECH);
            mRecogManager.onRecorderInit();
        }

        @Override
        public void onBufferReceived(byte[] data, int offset, int size) {
            // TODO Auto-generated method stub
//            AppLog.d(TAG, "mRecordListener onBufferReceived ");
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
            AppLog.d(TAG, "mRecordListener onError ");
            mRecorderStopped = true;
            sendMessage(MESSAGE_RECORDER_ERROR, error);
        }

        @Override
        public void onStop() {
            // TODO Auto-generated method stub
            AppLog.d(TAG, "mRecordListener onStop ");
            mRecorderStopped = true;
            sendEmptyMessage(MESSAGE_RECORDER_STOP);
        }

        @Override
        public void onAbandon() {
            // TODO Auto-generated method stub
            mRecorderStopped = true;
            sendEmptyMessage(MESSAGE_RECORDER_ABANDON);
        }

    };

    private RecognizerListener mWakeupListener = new RecognizerListener() {

        @Override
        public void onStart(int code) {
            AppLog.d(TAG, "mWakeupListener onStart");
            // TODO Auto-generated method stub
            if (code != SpeechConstant.SPEECH_ERROR_OK) {
                mWakeupStopped = true;
                sendMessage(MESSAGE_ENGINE_START, code);
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
            AppLog.d(TAG, "mWakeupListener onBeginningOfSpeech");

        }

        @Override
        public void onEndOfSpeech() {
            AppLog.d(TAG, "mWakeupListener onEndOfSpeech");
            // TODO Auto-generated method stub

        }

        @Override
        public void onResult(RecognitionResult result) {
            // TODO Auto-generated method stub
            AppLog.d(TAG, "mWakeupListener onResult " + result.mErrorCode);
            sendResultMessage(result.mResult[0]);
        }

        @Override
        public void onError(int error) {
            AppLog.d(TAG, "mWakeupListener onError ");
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
            AppLog.d(TAG, "mWakeupListener onStop ");
            // TODO Auto-generated method stub
            mWakeupStopped = true;
            sendEmptyMessage(MESSAGE_ENGINE_STOP);
        }

    };
	private boolean mIsTtsSpeaking = false;

    protected static final int MESSAGE_RECORDER_START = 201;
    protected static final int MESSAGE_RECORDER_STOP = 202;
    protected static final int MESSAGE_RECORDER_ABANDON = 203;
    protected static final int MESSAGE_RECORDER_ERROR = 204;

    protected static final int MESSAGE_ENGINE_START = 101;
    protected static final int MESSAGE_ENGINE_READY_FOR_SPEECH = 102;
    protected static final int MESSAGE_ENGINE_BEGIN_OF_SPEECH = 103;
    protected static final int MESSAGE_ENGINE_BUFFER_RECEIVED = 104;
    protected static final int MESSAGE_ENGINE_RMS_CHANGED = 105;
    protected static final int MESSAGE_ENGINE_END_OF_SPEECH = 106;
    protected static final int MESSAGE_ENGINE_RESULT = 107;
    protected static final int MESSAGE_ENGINE_PARTIAL_RESULT = 108;
    protected static final int MESSAGE_ENGINE_ERROR = 109;
    protected static final int MESSAGE_ENGINE_STOP = 110;

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

    protected class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            // TODO Auto-generated constructor stub
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            AppLog.d(TAG, "Handler Message " + msg.what);
            if (mSpeechResultListener == null) {
                return;
            }
            switch (msg.what) {
                case MESSAGE_RECORDER_START:
                    if (mSpeechResultListener != null) {
                        int code = (Integer) msg.obj;
                        mSpeechResultListener.onStart(code);
                        mRecorderStopped = true;
                    }
                    break;
                case MESSAGE_RECORDER_STOP:
                    if (mSpeechResultListener != null) {
                        mSpeechResultListener.onStop();
                    }
                    AppLog.d(TAG, "MESSAGE_RECORDER_STOP " + mStatus);
                    // setStatus(SpeechStatus.IDLE); //要在回调处理完成再设置状态
                    checkStopped();
                    break;
                case MESSAGE_RECORDER_ABANDON:
                    if (mSpeechResultListener != null) {
                        mSpeechResultListener.onAbandon();
                    }
                    AppLog.d(TAG, "MESSAGE_RECORDER_ABANDON " + mStatus);
                    // setStatus(SpeechStatus.IDLE); //要在回调处理完成再设置状态
                    checkStopped();
                    break;
                case MESSAGE_RECORDER_ERROR:
                    if (mSpeechResultListener != null) {
                        int error = (Integer) msg.obj;
                        mSpeechResultListener.onError(error);
                    }
                    AppLog.d(TAG, "MESSAGE_RECORDER_ERROR " + mStatus);
                    // setStatus(SpeechStatus.IDLE); //要在回调处理完成再设置状态
                    checkStopped();
                    break;
                case MESSAGE_ENGINE_START:
                    if (mSpeechResultListener != null) {
                        int code = (Integer) msg.obj;
                        mSpeechResultListener.onStart(code);
                    }
                    break;
                case MESSAGE_ENGINE_READY_FOR_SPEECH:
                    setStatus(SpeechStatus.RUNNING);
                    if (mSpeechResultListener != null) {
                        mSpeechResultListener.onReadyForSpeech(null);
                    }
                    break;
                case MESSAGE_ENGINE_BEGIN_OF_SPEECH:
                    if (mSpeechResultListener != null) {
                        mSpeechResultListener.onBeginningOfSpeech();
                    }
                    break;
                case MESSAGE_ENGINE_BUFFER_RECEIVED:
                    if (mSpeechResultListener != null) {
                        byte[] data = (byte[]) msg.obj;
                        mSpeechResultListener.onBufferReceived(data);
                    }
                    break;
                case MESSAGE_ENGINE_END_OF_SPEECH:
                    if (mSpeechResultListener != null) {
                        mSpeechResultListener.onEndOfSpeech();
                    }
                    break;
                case MESSAGE_ENGINE_RESULT:
                    if (mSpeechResultListener != null) {
                        Bundle results = (Bundle) msg.obj;
                        mSpeechResultListener.onResults(results);
                    }
                    break;
                case MESSAGE_ENGINE_PARTIAL_RESULT:
                    if (mSpeechResultListener != null) {
                        Bundle partialResults = (Bundle) msg.obj;
                        mSpeechResultListener.onPartialResults(partialResults);
                    }
                    break;
                case MESSAGE_ENGINE_ERROR:
                    if (mSpeechResultListener != null) {
                        int error = (Integer) msg.obj;
                        mSpeechResultListener.onError(error);
                    }
                    break;
                case MESSAGE_ENGINE_STOP:
                    checkStopped();
                    // if (mSpeechResultListener != null) {
                    // mSpeechResultListener.onStop();
                    // }
                    break;
            }
        }
    };
}
