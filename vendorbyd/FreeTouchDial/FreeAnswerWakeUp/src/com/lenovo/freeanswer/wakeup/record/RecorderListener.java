
package com.lenovo.freeanswer.wakeup.record;

public interface RecorderListener {

    public void onStart(int code);

    public void onReadyForSpeech();

    // public void onBeginningOfSpeech();
    public void onBufferReceived(byte[] data, int offset, int size);

    // public void onEndOfSpeech();
    public void onRmsChanged(float rms);

    public void onError(int error);

    public void onStop();

    public void onAbandon();

}
