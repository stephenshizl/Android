
package com.lenovo.freeanswer.wakeup.recognition;

import android.content.Intent;

import com.lenovo.freeanswer.wakeup.speech.SpeechException;

public interface IRecognitionManager {

    public void start(Intent intent) throws SpeechException;

    public void stop() throws SpeechException;

    public void addData(byte[] data, int offset, int size);

}
