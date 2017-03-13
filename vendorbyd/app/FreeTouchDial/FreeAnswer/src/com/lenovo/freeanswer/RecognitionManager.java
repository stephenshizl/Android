package com.lenovo.freeanswer;

import android.content.Context;
import android.content.Intent;

import android.util.Log;


public class RecognitionManager implements IRecognitionManager {
    protected static final String TAG = "RecognitionManager";

    private Context mContext = null;
    private Intent mRecognitionIntent = null;
    private AISpeechWakeupRecognizer mWakeupEngine = null;
    private RecognizerListener mWakeupListener = null;

    public RecognitionManager(Context context) {
        mContext = context;
    }

    public void setWakeupListener(RecognizerListener listener) {
        mWakeupListener = listener;
    }

    @Override
    public void start(Intent intent) throws SpeechException {
        // TODO Auto-generated method stub
        mRecognitionIntent = intent;
        String engineType = null;
        if (intent != null) {
            engineType = intent.getStringExtra(SpeechConstant.ENGINE_TYPE_KEY);
            boolean isWakeupEnable = true;
            if (SpeechConstant.ENGINE_TYPE_LOCAL_WAKEUP_RECOGNIZE.equalsIgnoreCase(engineType)) {
                if (isWakeupEnable) {
                    mWakeupEngine = new AISpeechWakeupRecognizer(mContext);
                }
            } else if (SpeechConstant.ENGINE_TYPE_LOCAL_WAKEUP.equalsIgnoreCase(engineType)) {
                if (isWakeupEnable) {
                    mWakeupEngine = new AISpeechWakeupRecognizer(mContext);
                }
            }
        }

        if (mWakeupEngine != null) {
            mWakeupEngine.setRecognizerListener(mWakeupListener);
            mWakeupEngine.create(intent);
            mWakeupEngine.start(intent);
        }
    }

    @Override
    public void stop() throws SpeechException {
        // TODO Auto-generated method stub
        Log.d(TAG, "stop");
        try {
            if (mWakeupEngine != null) {
                mWakeupEngine.release();
                mWakeupEngine = null;
            }
        } catch (SpeechException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void addData(byte[] data, int offset, int size) {
        // TODO Auto-generated method stub
        if (mWakeupEngine != null) {
            mWakeupEngine.addData(data, offset, size);
        }
    }

    public void onRecorderInit() {
        if (mWakeupEngine != null) {
            mWakeupEngine.onRecorderInit();
        }
    }
}

