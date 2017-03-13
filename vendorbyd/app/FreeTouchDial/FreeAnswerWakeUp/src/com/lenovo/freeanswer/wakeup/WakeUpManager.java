
package com.lenovo.freeanswer.wakeup;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.lenovo.freeanswer.wakeup.speech.SpeechConstant;
import com.lenovo.freeanswer.wakeup.speech.SpeechEngine;
import com.lenovo.freeanswer.wakeup.speech.SpeechResultListener;
import com.lenovo.freeanswer.wakeup.util.AppLog;
import com.lenovo.freeanswer.wakeup.util.NotificationController;
import com.lenovo.freeanswer.wakeup.util.StatusManager;
import com.lenovo.freeanswer.wakeup.util.SystemSettings;

public class WakeUpManager {
    private static final String TAG = "WakeUpManager";
    private static WakeUpManager mWakeUpManager = new WakeUpManager();
    private WakeUpCallBack mWakeUpCallBack;

    public static final int WAKE_UP_KEY_HUNGUP = 1;// 拨号过程中说挂断
    public static final int WAKE_UP_KEY_HUNGUP_ANSWER = 2;// 来电时说接听或挂断

    public static final int CLEAR_NOTIFICATION = 0x1000000;

    private int mFlagHungup = -1;

    private AudioManager mAudioManager = null;

    private static Context mContext = null;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case CLEAR_NOTIFICATION:
                    clearNotification();
                    break;
                default:
                    break;
            }
        };
    };

    private boolean mStatusError = false;

    public static Context getContext() {
        return mContext;
    }

    public static WakeUpManager getInstance(Context context) {
        mContext = context;
        return mWakeUpManager;
    }

    public void startListen(WakeUpCallBack wakeUpCallBack, int keywordFlag) {
        mFlagHungup = keywordFlag;
        mWakeUpCallBack = wakeUpCallBack;
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
        }
        SpeechEngine.getInstance().setSpeechResultListener(mSpeechResultListener);
        startSpeechEngine();
        StatusManager.getInstance().displayStart(mContext, mFlagHungup);
    }

    public void stopListen() {
        mWakeUpCallBack = null;
        stopSpeechEngine(false);
    }

    private void startSpeechEngine() {
        mHandler.removeCallbacks(mStopRunnable);

        int i = 0;
        while (mAudioManager != null && mAudioManager.getMode() != AudioManager.MODE_NORMAL) {
            AppLog.d(TAG, "#### AudioManager Wait for Mode Switch " + mAudioManager.getMode());
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (i++ > 100) {
                break;
            }
        }

        mStatusError = false;

        AppLog.d(TAG, "startSpeechEngine ");
        Intent intent = new Intent();
        intent.putExtra(SpeechConstant.ENGINE_TYPE_KEY,
                SpeechConstant.ENGINE_TYPE_LOCAL_WAKEUP_RECOGNIZE);
        SpeechEngine.getInstance().start(mContext, intent);

        // 最多运行十分钟
        mHandler.postDelayed(mStopRunnable, SpeechConstant.ENGINE_MAX_RUNNING_TIME);
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
            AppLog.d(TAG, "mStopRunnable timeout");
            stopSpeechEngine(false);
        }

    };

    private SpeechResultListener mSpeechResultListener = new SpeechResultListener() {

        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub
            if (mStatusError) {
                AppLog.d(TAG, "onReadyForSpeech return for mStatusError");
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
            AppLog.d(TAG, "onError " + error);
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
            AppLog.d(TAG, "onResults wakeKey " + wakeKey);

            if (!TextUtils.isEmpty(wakeKey)) {
                // 统计正确识别结果，freeanswer发广播，统计由freecall完成
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
                AppLog.d(TAG, "onStart fail " + code);
                mStatusError = true;
                stopSpeechEngine(false);
            }
        }

        @Override
        public void onStop() {
            // TODO Auto-generated method stub
            if (mStatusError) {
                AppLog.d(TAG, "onStop return for mStatusError");
                return;
            }
        }

        @Override
        public void onAbandon() {
            // TODO Auto-generated method stub
            stopSpeechEngine(false);
        }

    };

    private boolean isFreeDialOn() {
        int value = SystemSettings.getIntFromSystem(mContext.getContentResolver(),
                SpeechConstant.FREE_DIAL_SETTINGS_ENABLE, 0);
        return (value == 1);
    }

    // FreeAnswer是否开启
    public boolean isFreeAnswerOn() {
        int value = SystemSettings.getIntFromSystem(mContext.getContentResolver(),
                SpeechConstant.FREE_DIAL_SETTINGS_ANSWER, 0);
        return (value == 1);
    }

    // FreeDial打电话时免提开关
    public boolean isFreeDialHandsFreeOn() {
        boolean isFreeCallOn = isFreeDialOn();
        if (isFreeCallOn) {
            int value = SystemSettings.getIntFromSystem(mContext.getContentResolver(),
                    SpeechConstant.FREE_DIAL_SETTINGS_HANDSFREE, 0);
            return (value == 1);
        } else {
            return false;
        }
    }
    
    // FreeAnswer来电时免提开关
    public boolean isFreeAnswerHandsFreeOn() {
        boolean isFreeAnswerOn = isFreeAnswerOn();
        if (isFreeAnswerOn) {
            int value = SystemSettings.getIntFromSystem(mContext.getContentResolver(),
                    SpeechConstant.FREE_DIAL_SETTINGS_HANDSFREE_ANSWER, 0);
            return (value == 1);
        } else {
            return false;
        }
    }

    // FreeAnswer来电语音播报开关
    public boolean isTtsReportOn() {
        boolean isFreeAnswerOn = isFreeAnswerOn();
        if (isFreeAnswerOn) {
            int value = SystemSettings.getIntFromSystem(mContext.getContentResolver(),
                    SpeechConstant.FREE_DIAL_SETTINGS_TTS_BEFORE_RING, 0);
            return (value == 1);
        } else {
            return false;
        }

    }

    public interface WakeUpCallBack {
        public void onResult(String result);

        public void onError(int error);
    };
}
