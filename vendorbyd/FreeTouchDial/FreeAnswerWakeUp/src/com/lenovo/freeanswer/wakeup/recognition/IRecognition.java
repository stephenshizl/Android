
package com.lenovo.freeanswer.wakeup.recognition;

import android.content.Intent;

import com.lenovo.freeanswer.wakeup.speech.SpeechException;

public interface IRecognition {

    /*
     * Create the recognition and initialize the resources.
     */
    public void create(Intent intent) throws SpeechException;

    /*
     * Close the recognition and release resources.
     */
    public void release() throws SpeechException;

    /*
     * Build the recognition intent, build the grammar.
     */
    public boolean addIntent(Intent intent);

    /*
     * Start the recognition action.
     */
    public boolean start(Intent intent) throws SpeechException;

    /*
     * Stop the recognition and the engine will be idle.
     */
    public void stop() throws SpeechException;

    /*
     * Put audio data to the recognition.
     */
    public void addData(byte[] data, int offset, int size);
}
