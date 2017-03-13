package com.lenovo.freeanswer;

import android.os.Bundle;
import android.speech.RecognitionListener;

public interface SpeechResultListener extends RecognitionListener {

    public void onReadyForSpeech(Bundle params);

    public void onBeginningOfSpeech();

    public void onRmsChanged(float rmsdB);

    public void onBufferReceived(byte[] buffer);

    public void onEndOfSpeech();

    public void onError(int error);

    public void onResults(Bundle results);

    public void onPartialResults(Bundle partialResults);

    public void onEvent(int eventType, Bundle params);

    public void onStart(int code);

     /**
     * The user has stopped the recognition.
     */
    public void onStop();

    /**
     * The recognition is abandon by other reason.
     */
    public void onAbandon();

    public boolean getStartRecorder(); //add by wanghongyan for SALLY-3753 20150603
}
