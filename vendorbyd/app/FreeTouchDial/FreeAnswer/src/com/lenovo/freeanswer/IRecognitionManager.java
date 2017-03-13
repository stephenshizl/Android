package com.lenovo.freeanswer;

import android.content.Intent;

public interface IRecognitionManager {

    public void start(Intent intent) throws SpeechException;

    public void stop() throws SpeechException;

    public void addData(byte[] data, int offset, int size);

}
