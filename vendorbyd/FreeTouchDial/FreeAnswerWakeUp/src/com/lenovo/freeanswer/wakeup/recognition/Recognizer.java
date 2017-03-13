
package com.lenovo.freeanswer.wakeup.recognition;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;

import com.lenovo.freeanswer.wakeup.result.RecognitionResult;
import com.lenovo.freeanswer.wakeup.speech.SpeechConstant;
import com.lenovo.freeanswer.wakeup.speech.SpeechException;
import com.lenovo.freeanswer.wakeup.util.AppLog;

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
    private volatile boolean mWorking = false;
    private volatile boolean mRecognizitionEnd = false;

    private PipedOutputStream mDataOutBuffer = null;
    private PipedInputStream mDataInBuffer = null;

    protected ExecutorService mWorkerThread = Executors.newFixedThreadPool(1);

    protected Context mContext = null;
    protected Intent mRecognizationIntent = null;
    protected RecognitionResult mResult = new RecognitionResult();
    private boolean mIntentUpdated = false;

    protected RecognizerListener mRecognizerListener = null;

    public Recognizer(Context context) {
        mContext = context;
    }

    public final void setRecognizerListener(RecognizerListener listener) {
        mRecognizerListener = listener;
    }

    public synchronized void updateIntent() {
        AppLog.d(TAG, "updateIntent");
        mIntentUpdated = true;
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

    public boolean isPreparing() {
        return getStatus() == RecognitionStatus.PREPARING;
    }

    public boolean isRecognizing() {
        return getStatus() == RecognitionStatus.RECOGNIZING;
    }

    @Override
    public final void create(Intent intent) throws SpeechException {
        if (!isIdle()) {
            throw new SpeechException("Already Started!");
        }
        AppLog.d(TAG, "create " + this.getClass().getSimpleName());
        setStatus(RecognitionStatus.STARTING);
        mRecognizationIntent = intent;
        try {
            mDataOutBuffer = new PipedOutputStream();
            mDataInBuffer = new PipedInputStream(SpeechConstant.DEFAULT_RECORDER_BUFFER_SIZE);
            mDataInBuffer.connect(mDataOutBuffer);
            AppLog.d(TAG, "DataBuffer Ready " + this.getClass().getSimpleName());
            // mOverlayBuffer.init(SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE *
            // SpeechConstant.DEFAULT_OVERLAY_SLOT);
            mWorkerThread.execute(mWorkRunnable);
        } catch (Exception e) {
            AppLog.e(e.getMessage());
            release();
        }
        AppLog.d(TAG, "create END " + this.getClass().getSimpleName());
    }

    @Override
    public final void release() throws SpeechException {
        if (isIdle()) {
            throw new SpeechException("Already Stoped!");
        }

        AppLog.d(TAG, "release " + this.getClass().getSimpleName());
        mWorking = false;
        try {
            if (mDataOutBuffer != null) {
                mDataOutBuffer.close();
                mDataOutBuffer = null;
            }
            if (mDataInBuffer != null) {
                mDataInBuffer.close();
                mDataInBuffer = null;
            }
            // mOverlayBuffer.release();
        } catch (Exception e) {

        } finally {
            if (mDataOutBuffer != null) {
                try {
                    mDataOutBuffer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
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
        AppLog.d(TAG, "start " + this.getClass().getSimpleName());
        mWorking = true;
        return true;
    }

    @Override
    public final void stop() throws SpeechException {
        AppLog.d(TAG, "stop " + this.getClass().getSimpleName());
        mWorking = false;
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_STOPED;
    }

    protected volatile Runnable mWorkRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                if (!isStarting()) {
                    return;
                }
                int code = createImp();
                if (mRecognizerListener != null) {
                    mRecognizerListener.onStart(code);
                }
                if (!addIntent(mRecognizationIntent)) {
                    if (mRecognizerListener != null) {
                        mRecognizerListener.onStart(SpeechConstant.SPEECH_ENGINE_ADD_INTENT_FAILED);
                    }
                }

                AppLog.d(TAG, "Recognizing thread");
                setStatus(RecognitionStatus.RECOGNIZING);

                if (!startImp(false)) {
                    if (mRecognizerListener != null) {
                        mRecognizerListener.onStart(SpeechConstant.SPEECH_ENGINE_INIT_FAILED);
                    }
                }
                else {
                    mRecognizitionEnd = false;
                    mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_STARTING;

                    byte[] buf = new byte[SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE];
                    int len = readData(buf, 0, SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE);
                    while (len != -1 && isWorking()) {
                        if (len != SpeechConstant.DEFAULT_RECORDER_SLOT_SIZE) {
                            AppLog.d(TAG, "readData **** " + len);
                        }

                        if (len > 0) {
                            recognizeImp(buf, len);
                        }
                        else {
                            Thread.sleep(10);
                        }
                        if (mRecognizitionEnd) {
                            mRecognizitionEnd = false;
                            stopImp();

                            AppLog.d(TAG, "Restart recognizing ...... ");
                            Thread.sleep(10);
                            if (startImp(mIntentUpdated)) {
                                mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_STARTING;
                            }
                            else {
                                AppLog.d(TAG, "Restart Error!");
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
                // TODO Auto-generated catch block
                AppLog.ex(e);
                e.printStackTrace();
            }
            finally {
                AppLog.d(TAG, "WorkThread finished.");
            }
            setStatus(RecognitionStatus.IDLE);
            if (mRecognizerListener != null) {
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
                AppLog.d(TAG, "DataOutBuffer is not ready.");
            }
            // AppLog.d(TAG, "addData END " + this.getClass().getSimpleName());
        } catch (Exception e) {
            try {
                AppLog.d(TAG,
                        "addData Exception " + mDataInBuffer.available() + "," + e.getMessage());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    protected final int readData(byte[] data, int offset, int size) {
        try {
            // AppLog.d(TAG, "readData BEGIN " +
            // this.getClass().getSimpleName());
            if (mDataInBuffer != null) {
                for (int i = 0; i < 60 && mDataInBuffer.available() < size; i++) {
                    // AppLog.d(TAG, "readData WAIT for writing " +
                    // mDataInBuffer.available());
                    Thread.sleep(30);
                }
                // AppLog.d(TAG, "readData read");
                int len = mDataInBuffer.read(data, offset, size);
                // AppLog.d(TAG, "readData SUCCESS " + mDataInBuffer.available()
                // + "," + this.getClass().getSimpleName());
                if (len != size) {
                    AppLog.e(TAG, "readData partially " + len + "," + size + ","
                            + this.getClass().getSimpleName());
                }
                // 对于16BIT，如果读取的数据不是4倍数，会影响识别
                if (len % SpeechConstant.MIN_RECORDER_SLOT_SIZE != 0 && len < size) {
                    Thread.sleep(40);
                    int newLen = mDataInBuffer.read(data, offset + len, size - len);
                    AppLog.e(TAG, "readData AGAIN " + len + "," + newLen + "," + size);
                    return len + newLen;
                }
                return len;
            }
            else {
                AppLog.d(TAG, "DataInBuffer is not ready.");
            }
            // AppLog.d(TAG, "readData END  + this.getClass().getSimpleName()");
        } catch (Exception e) {
            AppLog.d(TAG, this.getClass().getSimpleName() + " readData Exception " + e.getMessage());
        }
        return 0;
    }

    protected boolean isWorking() {
        return mWorking;
    }

    protected void beginOfSpeech() {
        AppLog.d(TAG, "beginOfSpeech");
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_BEGIN_OF_SPEECH;
        if (mRecognizerListener != null) {
            mRecognizerListener.onBeginningOfSpeech();
        }
    }

    protected void endOfSpeech() {
        AppLog.d(TAG, "endOfSpeech");
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_END_OF_SPEECH;
        if (mRecognizerListener != null) {
            mRecognizerListener.onEndOfSpeech();
        }
    }

    protected void onSuccess() {
        AppLog.d(TAG, "onSuccess");
        mRecognizitionEnd = true;
        mResult.mErrorCode = SpeechConstant.SPEECH_ERROR_OK;
        if (mRecognizerListener != null) {
            mRecognizerListener.onResult(mResult);
        }
        AppLog.d(TAG, "onSuccess " + mResult.mResult[0]);
    }

    protected void onError(int error) {
        AppLog.d(TAG, "onError " + error);
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

    public void clearBuffer() {
        try {
            AppLog.d(TAG, "clearBuffer " + mDataInBuffer.available() + ","
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
                AppLog.d(TAG, "clearBuffer return for size too small " + size);
                return;
            }
            byte[] data = new byte[size];
            int len = mDataInBuffer.read(data);
            AppLog.d(TAG, this.getClass().getSimpleName() + " clearBuffer **** " + len);

        } catch (Exception e) {

        }
    }

    // 对于录音机的初始数据，可能有Fade-In，有的引擎需要做些额外处理
    public void onRecorderInit() {
        AppLog.d(TAG, "onRecorderInit");
    }

    protected abstract int createImp();

    protected abstract void releaseImp();

    protected abstract boolean startImp(boolean updateIntent);

    protected abstract void recognizeImp(byte[] data, int size);

    protected abstract void stopImp();

    protected abstract void setResult(String str);

    protected String getTag() {
        return "";
    }
}
