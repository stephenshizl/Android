package com.lenovo.freeanswer;

import android.media.AudioFormat;

public class SpeechConstant {
    public static final long ENGINE_MAX_RUNNING_TIME = 10 * 60 * 1000;
    
    public static final String ENGINE_TYPE_KEY = "engine_type";
    public static final String ENGINE_TYPE_UNKNOWN = "";
    public static final String ENGINE_TYPE_LOCAL_WAKEUP = "local_wakeup";
    public static final String ENGINE_TYPE_LOCAL_WAKEUP_RECOGNIZE = "local_wakeup_recognize";
    public static final String ENGINE_WAKEUP_WORD_KEY = "wakeup_word";

    public static final String FREE_DIAL_MESSAGE_START = "com.lenovo.freeanswer.message.start";
    public static final String FREE_DIAL_MESSAGE_STOP  = "com.lenovo.freeanswer.message.stop";

    public static final String FREE_DIAL_SETTINGS_ANSWER = "com.lenovo.freedial.enable.answer";
    public static final String FREE_DIAL_SETTINGS_HANDSFREE_ANSWER = "com.lenovo.freedial.handsfree.answer";
    public static final String FREE_ANSWER_STATISTIC_ANSWER = "com.lenovo.freeanswer.message.answer";
    public static final String FREE_ANSWER_STATISTIC_HANGUP = "com.lenovo.freeanswer.message.hangup";

    public static final String SPEECH_WAKEUP_END_CALL_WORD = "挂断";
    public static final String SPEECH_WAKEUP_ACCEPT_CALL_WORD = "接听";

    public static final int DEFAULT_RECORDER_BUFFER_SIZE = 10 * 32 * 1024;
    public static final int DEFAULT_RECORDER_SLOT_SIZE = 320;
    public static final int MIN_RECORDER_SLOT_SIZE = 4;

    public static final int DEFAULT_SAMPLE_RATE = 16000;
    public static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public static final int SPEECH_ERROR_OK = 0;

    public static final int SPEECH_ENGINE_INIT_OK = SPEECH_ERROR_OK;
    public static final int SPEECH_ERROR_STARTING = 1000;
    public static final int SPEECH_ERROR_BEGIN_OF_SPEECH = 1001;
    public static final int SPEECH_ERROR_END_OF_SPEECH = 1002;
    public static final int SPEECH_ERROR_STOPED = 1005;

    public static final int SPEECH_RECORDER_INIT_OK = SPEECH_ERROR_OK;
    public static final int SPEECH_ENGINE_INIT_FAILED = 2001;
    public static final int SPEECH_ENGINE_OPEN_FAILED = 2002;
    public static final int SPEECH_ENGINE_ADD_INTENT_FAILED = 2003;

    public static final int SPEECH_RECORDER_INIT_FAILED = 3001;
    public static final int SPEECH_RECORDER_RUNNING_FAILED = 3002;
}
