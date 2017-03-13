
package com.lenovo.freeanswer.wakeup.recognition;

import com.lenovo.freeanswer.wakeup.result.RecognitionResult;

public interface RecognizerListener {

    public void onStart(int code);

    // public void onReadyForSpeech();
    public void onBeginningOfSpeech();

    public void onEndOfSpeech();

    public void onResult(RecognitionResult result);

    public void onError(int error);

    public void onStop();

}
