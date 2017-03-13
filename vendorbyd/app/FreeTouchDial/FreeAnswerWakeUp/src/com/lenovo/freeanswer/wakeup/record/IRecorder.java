
package com.lenovo.freeanswer.wakeup.record;

import com.lenovo.freeanswer.wakeup.speech.SpeechException;

public interface IRecorder {

    public void start() throws SpeechException;

    public void stop();

}
