package com.tools.cit;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.WindowManager;
public class SignalTest  extends TestModule{

    TextView tv=null;
    String single=null;
    String singleTitle;
    MyPhoneStateListener MyListener;
    TelephonyManager Tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState, R.layout.signal, R.drawable.lcd);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.signal);
        //((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        tv=(TextView) this.findViewById(R.id.signal_current);
        MyListener = new MyPhoneStateListener();
        singleTitle=this.getString(R.string.current_signal);
        Tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
    }

    private class MyPhoneStateListener extends PhoneStateListener{

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);
            //Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
            //single=String.valueOf(signalStrength.getGsmDbm());//
            //int i=signalStrength.getGsmSignalStrength();
            single=String.valueOf(signalStrength.getGsmSignalStrength());//-113dbm+2asu
            single = single+"asu"+String.valueOf(signalStrength.getGsmSignalStrength()*2-113)+"dBm";
            tv.setText(singleTitle+single);
            //tv.setText(single);
        }


}
    @Override
    public String getModuleName() {
        return "Signal";
    }


}
