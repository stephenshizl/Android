package com.lenovo.freeanswer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.media.AudioManager;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class WakeUpManager {
    private static final String TAG = "WakeUpManager";
    private static WakeUpManager mWakeUpManager = new WakeUpManager();
    private WakeUpCallBack mWakeUpCallBack;
    private AudioManager mAudioManager = null;

    public static final int WAKE_UP_KEY_HUNGUP = 1;
    public static final int WAKE_UP_KEY_HUNGUP_ANSWER = 2;

    public static final int CLEAR_NOTIFICATION = 0x1000000;

    private int mFlagHungup = -1;

    private static Context mContext = null;

    private boolean mStatusError = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch(what) {
                case CLEAR_NOTIFICATION:
                    clearNotification();
                    break;
                default:
                    break;
            }
        };
    };

    public static Context getContext() {
        return mContext;
    }

    public static WakeUpManager getInstance(Context context) {
        mContext = context;
        return mWakeUpManager;
    }

    public void startListener(WakeUpCallBack wakeUpCallBack, int keywordFlag) {
        mFlagHungup = keywordFlag;
        mWakeUpCallBack = wakeUpCallBack;
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
        }
        Log.d(TAG, "startListener() getStartRecorder = " + mSpeechResultListener.getStartRecorder());
        if(mSpeechResultListener.getStartRecorder()) {
            SpeechEngine.getInstance().setSpeechResultListener(mSpeechResultListener);
            startSpeechEngine();
            StatusManager.getInstance().displayStart(mContext, mFlagHungup);
        } else {
            stopListen();
        }
    }

    public void stopListen() {
            mWakeUpCallBack = null;
            stopSpeechEngine(false);
    }

    private void startSpeechEngine() {
        mHandler.removeCallbacks(mStopRunnable);
        int i = 0;
        while(mAudioManager != null && mAudioManager.getMode() != AudioManager.MODE_NORMAL) {
            Log.d(TAG, "###AudioManager Wait for mode switch " + mAudioManager.getMode());
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i ++ > 100) {
                break;
            }
        }

        mStatusError = false;
        Log.d(TAG, "startSpeechEngine() getStartRecorder = " + mSpeechResultListener.getStartRecorder());
        if(mSpeechResultListener.getStartRecorder()) {
            Intent intent = new Intent();
            intent.putExtra(SpeechConstant.ENGINE_TYPE_KEY, SpeechConstant.ENGINE_TYPE_LOCAL_WAKEUP_RECOGNIZE);
            SpeechEngine.getInstance().start(mContext, intent);

            mHandler.postDelayed(mStopRunnable, SpeechConstant.ENGINE_MAX_RUNNING_TIME);
        } else {
            stopListen();
        }
    }

    private void stopSpeechEngine(boolean delay) {
        mHandler.removeCallbacks(mStopRunnable);
        SpeechEngine.getInstance().stop(delay);
        mHandler.sendEmptyMessage(CLEAR_NOTIFICATION);
    }

    private void clearNotification() {
        StatusManager.getInstance().displayStop(mContext);
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.d(TAG, "mStopRunnable timeout");
            //stopSpeechEngine(false);
        }
    };

    private SpeechResultListener mSpeechResultListener = new SpeechResultListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            if(mStatusError) {
                Log.e(TAG, "onReadyForSpeech return for mStatusError : " + mStatusError);
                return;
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onError : " + error);
            if (error == SpeechConstant.SPEECH_RECORDER_RUNNING_FAILED) {
                mStatusError = true;
                stopSpeechEngine(false);
            }
            mHandler.sendEmptyMessage(CLEAR_NOTIFICATION);
            if (mWakeUpCallBack != null) {
                mWakeUpCallBack.onError(error);
            }
        }

        @Override
        public void onResults(Bundle results) {
            // TODO Auto-generated method stub
            String wakeKey = results.getString(SpeechConstant.ENGINE_WAKEUP_WORD_KEY, "");
            Log.d(TAG, "onResults wakeKey " + wakeKey);

            if (!TextUtils.isEmpty(wakeKey)) {
                if (wakeKey.equals(SpeechConstant.SPEECH_WAKEUP_END_CALL_WORD)) {
                    Intent intent = new Intent(SpeechConstant.FREE_ANSWER_STATISTIC_HANGUP);
                    mContext.sendBroadcast(intent);
                } else if (wakeKey.equals(SpeechConstant.SPEECH_WAKEUP_ACCEPT_CALL_WORD)) {
                    Intent intent = new Intent(SpeechConstant.FREE_ANSWER_STATISTIC_ANSWER);
                    mContext.sendBroadcast(intent);
                }

                if (mWakeUpCallBack != null) {
                    mWakeUpCallBack.onResult(wakeKey);
                }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStart(int code) {
            // TODO Auto-generated method stub
            if (code != SpeechConstant.SPEECH_ENGINE_INIT_OK) {
                Log.d(TAG, "onStart fail " + code);
                mStatusError = true;
                stopSpeechEngine(false);
            }
        }

        @Override
        public void onStop() {
            // TODO Auto-generated method stub
            if (mStatusError) {
                Log.d(TAG, "onStop return for mStatusError : " + mStatusError);
                return;
            }
        }

        @Override
        public void onAbandon() {
            // TODO Auto-generated method stub
            stopSpeechEngine(false);
        }

        //add by wanghongyan for SALLY-3753 20150603 start
        @Override
        public boolean getStartRecorder() {
            if(mWakeUpCallBack != null) {
                Log.d(TAG, "getStartRecorder = " + mWakeUpCallBack.getStartRecorder());
                return mWakeUpCallBack.getStartRecorder();
            } else {
                return false;
            }
        }
        //add by wanghongyan for SALLY-3753 20150603 end
    };

    public interface WakeUpCallBack {
        public void onResult(String result);
        public void onError(int error);
        public boolean getStartRecorder(); //add by wanghongyan for SALLY-3753 20150603
    };
}
