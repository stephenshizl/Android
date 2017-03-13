
package com.lenovo.freeanswer.wakeup.speech;

import com.lenovo.freeanswer.wakeup.util.AppLog;

public class SpeechException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1940346508501838400L;

    public SpeechException(String msg) {
        // TODO Auto-generated constructor stub
        super(msg);
        AppLog.e(msg);
    }

}
