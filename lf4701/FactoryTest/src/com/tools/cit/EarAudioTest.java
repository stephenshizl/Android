
package com.tools.cit;

import android.os.Bundle;
import com.tools.util.FTLog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
/**
 * @author liangzid
 */
public class EarAudioTest extends MicTest {

    public static final String Mic = "Mic2";
    private HeadsetReceiver headsetReceiver = new HeadsetReceiver();
    private Button btnStart;

    @Override
    public String getModuleName() {
        // TODO Auto-generated method stub
        FTLog.d(this, "In Mic2Test :" + Mic);
        return Mic;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        
        btnStart = (Button) findViewById(R.id.start);
        btnStart.setEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headsetReceiver);
    }

    private class HeadsetReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.getIntExtra("state", 0) == 1) {    
                    btnStart.setEnabled(true);
                }
                FTLog.i(this, "ACTION_HEADSET_PLUG " +
                        (intent.getIntExtra("state", -1) == 1 ? "plugged" : "unplugged") +
                        ", name: " + intent.getStringExtra("name") +
                        ", " + (intent.getIntExtra("microphone", -1) == 1 ? "with" : "without")
                        + " microphone"
                        );
                if (intent.getIntExtra("state", 0) == 1) {
                    if (intent.getIntExtra("microphone", -1) == 1){
                        ((TextView) findViewById(R.id.txt_headset_info)).setText(R.string.mic_hs_with_in_mic);
                        TestThread setmode = new TestThread("tools.mic_mode 2");
                        setmode.start();
                    }
                    else
                        ((TextView) findViewById(R.id.txt_headset_info))
                                .setText(R.string.mic_hs_without_mic);
                   
                } else {
                    ((TextView) findViewById(R.id.txt_headset_info)).setText(R.string.mic_no_hs);                     
                }
            }
        }
    }

}
