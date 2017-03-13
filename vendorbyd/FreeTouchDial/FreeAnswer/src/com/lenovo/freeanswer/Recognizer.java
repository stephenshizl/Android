package com.lenovo.freeanswer;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public abstract class Recognizer implements IRecognition {
    protected String TAG = "Recognizer";

    public enum RecognitionStatus {
        IDLE,
        STARTING,
        PREPARING,
        RECOGNIZING
    };

    private RecognitionStatus mStatus = RecognitionStatus.IDLE;
    private Object mStatusMutex = new Object();
    protected Context mContext = null;
    protected Intent mRecognizationIntent = null;
    protected RecognizerListener mRecognizerListener = null;

    private PipedOutputStream mDataOutBuffer = null;
    private PipedInputStream mDataInBuffer = null;

    protected ExecutorService mWorkerThread = Executors.newFixedThreadPool(1);

    private volatile boolean mWorking = false;
    private volatile boolean mRecognizitionEnd = false;

    protected RecognitionResult mResult = new RecognitionResult();
    private boolean mIntentUpdated = false;

    public Recognizer(Context context) {
        mContext = context;
    }

    public final void setRecognizerListener(RecognizerListener listener) {
        mRecognizerListener = listener;
    }

    protected void setStatus(RecognitionStatus s) {
        synchronized (mStatusMutex) {
            mStatus = s;
        }
    }

    public RecognitionStatus getStatus() {
        synchronized (mStatusMutex) {
            return mStatus;
        }
    }

    public boolean isIdle() {
        return getStatus() == RecognitionStatus.IDLE;
    }

    public boolean isStarting() {
        return getStatus() == RecognitionStatus.STARTING;
    }

    protected boolean isWorking() {
        return mWorking;
    }

    protected void beginOfSpeech() {
        Log.d(TAG, "beginOfSpeech");
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_BEGIN_OF_SPEECH;
        if (mRecognizerListener != null) {
            mRecognizerListener.onBeginningOfSpeech();
        }
    }

    protected void endOfSpeech() {
        Log.d(TAG, "endOfSpeech");
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_END_OF_SPEECH;
        if (mRecognizerListener != null) {
            mRecognizerListener.onEndOfSpeech();
        }
    }

    protected void onSuccess() {
        Log.d(TAG, "onSuccess");
        mRecognizitionEnd = true;
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_OK;
        if (mRecognizerListener != null) {
            mRecognizerListener.onResult(mResult);
        }
        Log.d(TAG, "onSuccess " + mResult.mResult[0]);
    }

    protected void onError(int error) {
        Log.d(TAG, "onError " + error);
        mRecognizitionEnd = true;
        mResult.mErrorCode = error;
        if (mRecognizerListener != null) {
            mRecognizerListener.onError(error);
        }
        if (error == SpeechConstant.SPEECH_ENGINE_INIT_FAILED) {
            try {
                stop();
            } catch (SpeechException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void create(Intent intent) throws SpeechException {
        if (!isIdle()) {
            throw new SpeechException("Already Started!");
        }
        Log.d(TAG, "create " + this.getClass().getSimpleName());
        setStatus(RecognitionStatus.STARTING);
        mRecognizationIntent = intent;
        try {
            mDataOutBuffer = new PipedOutputStream();
            mDataInBuffer = new PipedInputStream(SpeechConstant.DEFAULT_RECORDER_BUFFER_SIZE);
            mDataInBuffer.connect(mDataOutBuffer);
            Log.d(TAG, "DataBuffer Ready " + this.getClass().getSimpleName());
            // mOverlayBuffer.init(SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE *
            // SpeechConstant.DEFAULT_OVERLAY_SLOT);
            mWorkerThread.execute(mWorkRunnable);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            release();
        }
        Log.d(TAG, "create END " + this.getClass().getSimpleName());
    }

    @Override
    public final void release() throws SpeechException {
        if(isIdle()) {
            throw new SpeechException("Already Stopped!");
        }

        Log.d(TAG, "release " + this.getClass().getSimpleName());
        mWorking = false;
        try {
            if(mDataOutBuffer != null) {
                mDataOutBuffer.close();
                mDataOutBuffer = null;
            }

            if(mDataInBuffer != null) {
                mDataInBuffer.close();
                mDataInBuffer = null;
            }
        } catch (Exception e) {

        } finally {
            if(mDataOutBuffer != null) {
                try {
                    mDataOutBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDataOutBuffer = null;
            }

            if (mDataInBuffer != null) {
                try {
                    mDataInBuffer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mDataInBuffer = null;
            }
        }
    }

    @Override
    public final boolean start(Intent intent) throws SpeechException {
        Log.d(TAG, "start " + this.getClass().getSimpleName());
        mWorking = true;
        return true;
    }

    @Override
    public final void stop() throws SpeechException {
        Log.d(TAG, "stop " + this.getClass().getSimpleName());
        mWorking = false;
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_STOPED;
    }

    public void onRecorderInit() {
        Log.d(TAG, "onRecorderInit");
    }

    protected volatile Runnable mWorkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(!isStarting()) {
                    return;
                }

                int code = createImp();
                if(mRecognizerListener != null) {
                    mRecognizerListener.onStart(code);
                }

                if(!addIntent(mRecognizationIntent)) {
                    if(mRecognizerListener != null) {
                        mRecognizerListener.onStart(SpeechConstant.SPEECH_ENGINE_ADD_INTENT_FAILED);
                    }
                }

                Log.e(TAG, "Recognizing thread");
                setStatus(RecognitionStatus.RECOGNIZING);

                if (!startImp(false)) {
                    if (mRecognizerListener != null) {
                        mRecognizerListener.onStart(SpeechConstant.SPEECH_ENGINE_INIT_FAILED);
                    }
                } else {
                    mRecognizitionEnd = false;
                    mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_STARTING;

                    byte[] buf = new byte[SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE];
                    int len = readData(buf, 0, SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE);

                    while(len != -1 && isWorking()) {
                        if (len != SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE) {
                            Log.d(TAG, "readData **** " + len);
                        }

                        if (len > 0) {
                            recognizeImp(buf, len);
                        } else {
                            Thread.sleep(10);
                        }

                        if (mRecognizitionEnd) {
                            mRecognizitionEnd = false;
                            stopImp();

                            Log.d(TAG, "Restart recognizing ...... ");
                            Thread.sleep(10);

                            if (startImp(mIntentUpdated)) {
                                mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_STARTING;
                            } else {
                                Log.d(TAG, "Restart Error!");
                                break;
                            }
                            mIntentUpdated = false;
                        }
                        Thread.sleep(1);
                        len = readData(buf, 0, SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE);
                    }
                    stopImp();
                }
                releaseImp();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.e(TAG, "WorkThread finished");
            }
            setStatus(RecognitionStatus.IDLE);
            if(mRecognizerListener != null) {
                mRecognizerListener.onStop();
            }
        }
    };

    @Override
    public final void addData(byte[] data, int offset, int size) {
        try {
            // AppLog.d(TAG, "addData BEGIN " + mDataInBuffer.available() + ","
            // + this.getClass().getSimpleName());
            if (mDataOutBuffer != null) {
                if (mDataInBuffer.available() > SpeechConstant.DEFAULT_RECORDER_BUFFER_SIZE - size) {
                    clearBuffer();
                }
                mDataOutBuffer.write(data, offset, size);
            }
            else {
                Log.d(TAG, "DataOutBuffer is not ready.");
            }
            // AppLog.d(TAG, "addData END " + this.getClass().getSimpleName());
        } catch (Exception e) {
            try {
                Log.d(TAG,
                        "addData Exception " + mDataInBuffer.available() + "," + e.getMessage());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    protected final int readData(byte[] data, int offset, int size) {
        try {
            if(mDataInBuffer != null) {
                for(int i = 0; i < 60 && mDataInBuffer.available() < size; i ++) {
                    Thread.sleep(30);
                }
                int len = mDataInBuffer.read(data, offset, size);
                if(len != size) {
                    Log.e(TAG, "readData partially " + len + "," + size + "," + this.getClass().getSimpleName());
                }

                if (len % SpeechConstant.MIN_RECORDER_SLOT_SIZE != 0 && len < size) {
                    Thread.sleep(40);
                    int newLen = mDataInBuffer.read(data, offset + len, size - len);
                    Log.e(TAG, "readData AGAIN " + len + "," + newLen + "," + size);
                    return len + newLen;
                }
                return len;
            } else {
                Log.d(TAG, "DataInBuffer is not ready.");
            }
        } catch (Exception e) {
            Log.d(TAG, this.getClass().getSimpleName() + " readData Exception " + e.getMessage());
        }
        return 0;
    }

     public void clearBuffer() {
        try {
            Log.d(TAG, "clearBuffer " + mDataInBuffer.available() + ","
                    + getClass().getSimpleName());
            int size = mDataInBuffer.available();
            int remain = size % 4;
            if (remain != 0) {
                size -= remain;
            }
            // if (size > SpeechConstant.DEFAULT_RECORDER_BUFFER_SIZE -
            // SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE * 10) {
            // size = SpeechConstant.DEFAULT_RECORDER_BUFFER_SIZE -
            // SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE * 10;
            // }
            if (size < SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE) {
                Log.d(TAG, "clearBuffer return for size too small " + size);
                return;
            }
            byte[] data = new byte[size];
            int len = mDataInBuffer.read(data);
            Log.d(TAG, this.getClass().getSimpleName() + " clearBuffer **** " + len);
        } catch (Exception e) {
        }
    }

    protected abstract int createImp();
    protected abstract void releaseImp();
    protected abstract boolean startImp(boolean updateIntent);
    protected abstract void recognizeImp(byte[] data, int size);    
    protected abstract void stopImp();
    protected abstract void setResult(String str);
}
