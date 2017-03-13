package com.android.freeanswer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.lenovo.freeanswer.WakeUpManager;
import com.lenovo.freeanswer.WakeUpManager.WakeUpCallBack;
import com.lenovo.freeanswer.SpeechConstant;
//add by wanghongyan for SALLY-3753 20150603 start
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
//add by wanghongyan for SALLY-3753 20150603 end

public class MainService extends Service {
    private String mResult = null;
    private Ilistener myListener = null;

    //add by wanghongyan for SALLY-3753 20150603 start
    private final BroadcastReceiver mReceiver = new PhoneAppBroadcastReceiver();
    private boolean mShouldRestartRecorder = false;
    //add by wanghongyan for SALLY-3753 20150603 end

    @Override
    public void onCreate() {
        Log.d("MainService", "onCreate()");
        //add by wanghongyan for SALLY-3753 20150603 start
        mShouldRestartRecorder = true;
        IntentFilter intentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
        //add by wanghongyan for SALLY-3753 20150603 end
    }

    //add by wanghongyan for SALLY-3753 20150603 start
    private class PhoneAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null && action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                String extra = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.d("MainService", "extra " + extra);
                if(extra != null && extra.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    mShouldRestartRecorder = true;
                } else {
                    mShouldRestartRecorder = false;
                }
            }
        }
    }
    //add by wanghongyan for SALLY-3753 20150603 end

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("MainService", "onStart()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("MainService", "[why] onStartCommand()");
		startListener();
        return START_REDELIVER_INTENT;
    }
	
	@Override
    public IBinder onBind(Intent intent) {
        // TODO: Currently, return only the LocalBinder instance.  If we
        // end up requiring support for a remote binder, we will need to 
        // return mBinder as well, depending upon the intent.
		Log.e("MainService", "[why] onBind()");
		startListener();
        return new MainServiceImpl();
    }
	
	private void startListener() {
        WakeUpManager.getInstance(this).startListener(
            new WakeUpCallBack() {
                @Override
                public void onResult(final String result) {
                    Log.d("MainService", "onResult " + result);
                    mResult = result;
                    try {
                    	myListener.change(result);
                    } catch(RemoteException e){}
                    
                    /*if(result != null) {
                        if(result.equals(getResources().getString(R.string.answer_call)) ||
                            result.equals(getResources().getString(R.string.hungup_call))) {
                            PhoneUtils.handleAutoAnswerOrHangUpCall(mCm, phone, result);
                        }
                    }*/
                }

            @Override
            public void onError(int error) {
                Log.d("MainService", "onError " + error + " mShouldRestartRecorder = " + mShouldRestartRecorder);
                //add by wanghongyan for SALLY-1549 20150325 start
                if (error == SpeechConstant.SPEECH_RECORDER_RUNNING_FAILED && mShouldRestartRecorder) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    startListener();
                }
                //add by wanghongyan for SALLY-1549 20150325 end
            }

            //add by wanghongyan for SALLY-3753 20150603 start
            @Override
            public boolean getStartRecorder() {
                return mShouldRestartRecorder;
            }
            //add by wanghongyan for SALLY-3753 20150603 end
        }, WakeUpManager.WAKE_UP_KEY_HUNGUP_ANSWER);
    }
	
	private void stopListener() {
        WakeUpManager.getInstance(this).stopListen();
    }
	
	@Override
	public void onDestroy() {
		Log.e("MainService", "[why] onDestroy()");
        //add by wanghongyan for SALLY-3753 20150603 start
        mShouldRestartRecorder = false;
        unregisterReceiver(mReceiver);
        //add by wanghongyan for SALLY-3753 20150603 endS
		stopListener();
	}
	
	public class MainServiceImpl extends IMainServiceT.Stub {
		public void register(Ilistener listen) throws RemoteException{
			Log.d("MainService", "register listen = " + listen);
			myListener = listen;
			
		}
	}
	

}
