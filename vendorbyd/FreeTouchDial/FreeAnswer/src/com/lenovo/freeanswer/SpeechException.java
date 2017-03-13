package com.lenovo.freeanswer;

//import com.lenovo.freeanswer.wakeup.util.AppLog;
import android.util.Log;

public class SpeechException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1940346508501838400L;

    public SpeechException(String msg) {
        // TODO Auto-generated constructor stub
        super(msg);
        Log.e("SpeechException", "msg : " + msg);
    }

}
