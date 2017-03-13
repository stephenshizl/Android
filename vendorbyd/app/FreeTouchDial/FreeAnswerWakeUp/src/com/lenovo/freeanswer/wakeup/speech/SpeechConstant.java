
package com.lenovo.freeanswer.wakeup.speech;

import android.media.AudioFormat;

public class SpeechConstant {

    public static final long ENGINE_STOP_DELAY = 1000;
    public static final long ENGINE_MAX_RUNNING_TIME = 10 * 60 * 1000; // 十分钟
    public static final long CALL_OUT_DELAY = 3000;
    public static final long ENGINE_SLOT_MIN = 100;
    public static final long ENGINE_MAX_ERROR_TIMES = 3;

    public static final String ENGINE_TYPE_KEY = "engine_type";
    public static final String ENGINE_TYPE_UNKNOWN = "";
    public static final String ENGINE_TYPE_LOCAL_WAKEUP = "local_wakeup";
    public static final String ENGINE_TYPE_LOCAL_RECOGNIZE = "local_recognize";
    public static final String ENGINE_TYPE_LOCAL_WAKEUP_RECOGNIZE = "local_wakeup_recognize";

    public static final String ENGINE_WAKEUP_WORD_KEY = "wakeup_word";

    public static final String SPEECH_CONTACT_LIST_KEY = "speech_contact_list_key";
    public static final String SPEECH_WAKED_OR_NOT_KEY = "speech_waked_or_not";
    public static final String SPEECH_CANCEL_CALLOUT_KEY = "speech_cancel_callout";
    public static final String SPEECH_MISS_RECOG_KEY = "speech_miss_recog_key";
    public static final String SPEECH_CONFIDENCE_KEY = "speech_confidence_key";

    public static final String FREECALL_UPDATE_KEY = "com.lenovo.freedial.update";
    public static final String FREECALL_FEEDBACK_KEY = "com.lenovo.freedial.feedback";

    // FreeAnswer统计消息
    public static final String FREE_ANSWER_STATISTIC_ANSWER = "com.lenovo.freeanswer.message.answer";
    public static final String FREE_ANSWER_STATISTIC_HANGUP = "com.lenovo.freeanswer.message.hangup";

    // 设置项中FreeDial是否开启的接口
    public static final String FREE_DIAL_SETTINGS_ENABLE = "com.lenovo.freedial.enable.all";
    // public static final String FREE_DIAL_SETTINGS_DIAL =
    // "com.lenovo.freedial.enable.dial";
    public static final String FREE_DIAL_SETTINGS_ANSWER = "com.lenovo.freedial.enable.answer";
    
    // 设置项中各个子项的接口
    public static final String FREE_DIAL_SETTINGS_HANDSFREE = "com.lenovo.freedial.handsfree";
    public static final String FREE_DIAL_SETTINGS_HANDSFREE_ANSWER = "com.lenovo.freedial.handsfree.answer";
    public static final String FREE_DIAL_SETTINGS_LOCKSCREEN_CALL = "com.lenovo.freedial.lockscreen.call";
    public static final String FREE_DIAL_SETTINGS_TTS_BEFORE_RING = "com.lenovo.freedial.tts_before_ring";
    
    // 通知栏接口
    public static final String FREE_DIAL_MESSAGE_STARTING = "com.lenovo.freedial.message.starting";
    public static final String FREE_DIAL_MESSAGE_START = "com.lenovo.freedial.message.start";
    public static final String FREE_DIAL_MESSAGE_STOP  = "com.lenovo.freedial.message.stop";

    public static final String SPEECH_WAKEUP_END_CALL_WORD = "挂断";
    public static final String SPEECH_WAKEUP_ACCEPT_CALL_WORD = "接听";

    public static final int DEFAULT_RECORDER_BUFFER_SIZE = 10 * 32 * 1024;
    public static final int DEFAULT_RECORDER_SLOT_SIZE = 320;
    public static final int MIN_RECORDER_SLOT_SIZE = 4;
    public static final int DEFAULT_OVERLAY_SLOT = 40;
    public static final int DEFAULT_SAMPLE_RATE = 16000;
    public static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public static final int SPEECH_ERROR_OK = 0;
    public static final int SPEECH_ERROR_STARTING = 1000;
    public static final int SPEECH_ERROR_BEGIN_OF_SPEECH = 1001;
    public static final int SPEECH_ERROR_END_OF_SPEECH = 1002;
    public static final int SPEECH_ERROR_NO_SPEECH = 1003;
    public static final int SPEECH_ERROR_REJECT = 1004;
    public static final int SPEECH_ERROR_STOPED = 1005;

    public static final int SPEECH_ENGINE_INIT_OK = SPEECH_ERROR_OK;
    public static final int SPEECH_ENGINE_INIT_FAILED = 2001;
    public static final int SPEECH_ENGINE_OPEN_FAILED = 2002;
    public static final int SPEECH_ENGINE_ADD_INTENT_FAILED = 2003;

    public static final int SPEECH_RECORDER_INIT_OK = SPEECH_ERROR_OK;
    public static final int SPEECH_RECORDER_INIT_FAILED = 3001;
    public static final int SPEECH_RECORDER_RUNNING_FAILED = 3002;

}
