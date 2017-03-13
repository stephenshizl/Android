
package com.lenovo.freeanswer.wakeup.result;

import com.lenovo.freeanswer.wakeup.speech.SpeechConstant;


public class RecognitionResult extends Object {

    public String mEngineType = SpeechConstant.ENGINE_TYPE_UNKNOWN;
    public String[] mResult = null;
    public int[] mConfidence = null;
    public int mErrorCode = 0;
    public int mErrorString = 0;
    public boolean mVadStart = false;
    public boolean mVadEnd = false;
    public String mPcmName = null;

    @Override
    public RecognitionResult clone() {
    	RecognitionResult cl = new RecognitionResult();
    	cl.mEngineType = this.mEngineType;
    	if (this.mResult != null) {
    		cl.mResult = this.mResult.clone();
    	}
    	if (this.mConfidence != null) {
    		cl.mConfidence = this.mConfidence.clone();
    	}
    	cl.mErrorCode = this.mErrorCode;
    	cl.mErrorString = this.mErrorString;
    	cl.mVadStart = this.mVadStart;
    	cl.mVadEnd = this.mVadEnd;
    	cl.mPcmName = this.mPcmName;
    	return cl;
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("[mEngineType: ").append(mEngineType).append("\nmConfidence: ").append(mConfidence)
                .append("\nmErrorCode: ").append(mErrorCode).append("\nResult: ");
        for (String result : mResult) {
            sb.append(result).append("]\n");
        }
        return sb.toString();
    }
}
