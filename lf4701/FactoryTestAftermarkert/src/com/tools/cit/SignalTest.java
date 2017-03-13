package com.tools.customercit;

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

//add network modes by qianyan
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.telephony.ServiceState;
import com.android.internal.telephony.OperatorInfo;
import android.telephony.SubscriptionManager;
import android.os.Message;
import android.os.Handler;
import android.os.HandlerThread;
import com.qualcomm.qcrilhook.QcRilHook;
import com.qualcomm.qcrilhook.QcRilHookCallback;
import com.android.internal.telephony.Phone;
import java.io.IOException;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.TelephonyIntents;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
//end by qianyan
public class SignalTest  extends TestModule {

     //add network modes by qianyan
    private static final String ACTION_INCREMENTAL_NW_SCAN_IND
                        = "qualcomm.intent.action.ACTION_INCREMENTAL_NW_SCAN_IND";
    private String networkList = "Network Mode Name \t\t\t\t\t\t\t\t RSSI\n";
    private static final int QUERY_EXCEPTION = -1;
    private static final int QUERY_OK = 0;
    private static final int QUERY_PARTIAL = 1;
    private static final int QUERY_ABORT = 2;
    private static final int QUERY_REJ_IN_RLF = 3;
    private long mSubId;
    //private Phone mPhone;
    private TextView passButton = null;
    private TextView networkHint = null;     
    TextView netWorktxt = null;
    TextView waitHint = null;
    private Handler handler;
    private HashMap<String, String> mRatMap;
    private QcRilHook mQcRilHook = null;
    private QcRilHookCallback mQcRilHookCallback = new QcRilHookCallback() {       
        @Override        
        public void onQcRilHookReady() {            
            Log.i("signaltest QcRilHook", "onQcRilHookReady false" );            
            HandlerThread thread = new HandlerThread("network_scan");           
            thread.start();            
            new Handler(thread.getLooper()).post(new Runnable() {                
                @Override                
                public void run() {                    
                boolean success = mQcRilHook.qcRilPerformIncrManualScan(                                    
                    SubscriptionManager.getPhoneId(mSubId));                    
                Log.i("signaltest QcRilHook ", "start scan, result:" + success);                    
                }                
                });        
            }    
        };

    //end by qianyan
    
    TextView tv=null;
    String single=null;
    String singleTitle;
    //MyPhoneStateListener MyListener;
    TelephonyManager telManager;  
    boolean mobileData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState, R.layout.signal, R.drawable.lcd);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        telManager = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        mobileData = telManager.getDataEnabled();
        if(mobileData) {
           telManager.setDataEnabled(false);
        }
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.signal);
        //((Button) findViewById(R.id.btn_pass)).setEnabled(false);
        tv=(TextView) this.findViewById(R.id.signal_current);
        
        //((Button) findViewById(R.id.btn_pass)).setEnabled(true);

        //add network modes by qianyan
        passButton = (Button) findViewById(R.id.btn_pass);
        mSubId = getIntent().getLongExtra(PhoneConstants.SUBSCRIPTION_KEY,
                                SubscriptionManager.getDefaultSubId());
        netWorktxt = (TextView)this.findViewById(R.id.current_network);
        waitHint = (TextView)this.findViewById(R.id.wait_hint);
        waitHint.setText("Searching ,please wait ...");
        mSubId = getIntent().getLongExtra(PhoneConstants.SUBSCRIPTION_KEY,
                                SubscriptionManager.getDefaultSubId());
        Log.i("signaltest", "signaltest oncreate mSubId value" + mSubId);
        if(mSubId != -1) {
            if(mQcRilHook == null){
                try{
                    Log.i("signaltest", "sendHookbefore");
                    //mQcRilHook = new QcRilHook(SignalTest.this, mQcRilHookCallback); 
                    new Handler().postDelayed(new Runnable(){  
                        public void run() {                    
                            mQcRilHook = new QcRilHook(SignalTest.this, mQcRilHookCallback); 
                        }  
                    }, 1000);                   
                    Log.i("signaltest", "sendHookafter");
                } catch(Exception ex){
                    Log.i("signaltest","mContext_initial_qcnvitems error");
                }
            }
        } else {
            waitHint.setText("Please insert SIM Card first");
            waitHint.setVisibility(View.VISIBLE);
        }

        mRatMap = new HashMap<String, String>();
        initRatMap();
        IntentFilter intentFilter = new IntentFilter();        
        intentFilter.addAction(ACTION_INCREMENTAL_NW_SCAN_IND);        
        intentFilter.addAction(TelephonyIntents.ACTION_SIM_STATE_CHANGED);        
        registerReceiver(mBroadcastReceiver, intentFilter);
    
        //end by qianyan
    }


   //add newwork mode by qianyan  
    private void initRatMap() {
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_UNKNOWN), "Unknown");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_GPRS), "2G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_EDGE), "2G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_UMTS), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_IS95A), "2G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_IS95B), "2G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_1xRTT), "2G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_EVDO_0), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_EVDO_A), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_HSDPA), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_HSUPA), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_HSPA), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_EVDO_B), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_EHRPD), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_LTE), "4G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_HSPAP), "3G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_GSM), "2G");
        mRatMap.put(String.valueOf(ServiceState.RIL_RADIO_TECHNOLOGY_TD_SCDMA), "3G");
    }
   
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {        
       @Override        
        public void onReceive(Context context, Intent intent) {            
            Log.i("signaltest", "BroadcastReceiver intent" + intent.getAction());         
            if (ACTION_INCREMENTAL_NW_SCAN_IND.equals(intent.getAction())) {
                int result = intent.getIntExtra("scan_result", QUERY_EXCEPTION);                
                int phoneId = intent.getIntExtra("sub_id", SubscriptionManager.DEFAULT_PHONE_ID);                
                String[] scanInfo = intent.getStringArrayExtra("incr_nw_scan_data");                
                long[] subIds = SubscriptionManager.getSubId(phoneId); 
                Log.d("SignalTest","on scanned result received:subIdslength" + subIds.length);
                if (subIds != null && subIds.length > 0) {                    
                    Log.d("SignalTest", "on scanned result received, result:" + result + ", subscription:" + subIds[0] 
                               + ", scanInfo:" + (scanInfo == null ? 0 : scanInfo.length));                   
                    if (subIds[0] == mSubId) { 
                        onNetworksListLoadResult(scanInfo, result); 
                        netWorktxt.setText("List of available network modes: \n" + networkList);
                        waitHint.setVisibility(View.GONE);
                        passButton.setEnabled(true);
                    }                
                } else {                    
                        Log.d("SignalTest", "no valid sub_id for nw scan result");                
                }            
             } else if (TelephonyIntents.ACTION_SIM_STATE_CHANGED.equals(intent.getAction())) { 
                   Log.i("signaletest", "BroadcastReceiver intent:ACTION_SIM_STATE_CHANGED");
                   int simState = TelephonyManager.getDefault().getSimState(                                
                        SubscriptionManager.getSlotId(mSubId));                
                   if (simState != TelephonyManager.SIM_STATE_READY) {                    
                        Log.d("SignalTest", "SIM state is not ready: sim_state = " + simState + " mSubId"+ mSubId +"slot = " + SubscriptionManager.getSlotId(mSubId));                   
                        onNetworksListLoadResult(null, QUERY_EXCEPTION);
                        netWorktxt.setText("List of available network modes: \nSIM state is not ready" );
                        waitHint.setVisibility(View.GONE);
                        passButton.setEnabled(false);
                   }            
             }        
        }    
    };

    private void onNetworksListLoadResult(final String[] result, final int status) {
        Log.i("signal onNetworksListLoadResult", "onNetworksListLoadResult status" + status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                case QUERY_REJ_IN_RLF:
                    // Network scan did not complete due to a radio link
                    // failure recovery in progress
                    Toast.makeText(SignalTest.this, R.string.network_query_error,
                                Toast.LENGTH_SHORT).show();
                    break;
                case QUERY_ABORT:
                        // Network searching time out
                        // show the nw info if it bundled with this query
                    Toast.makeText(SignalTest.this, R.string.network_query_longtime,
                                Toast.LENGTH_SHORT).show();
                    break;
                case QUERY_OK:

                case QUERY_PARTIAL:
                int operator_info_entries_num = 0;
                if(result != null) {
                    if(result.length % 5 == 0){
                        operator_info_entries_num = 5;
                        if (result.length >= 5 && (result.length % 5 == 0)) {
                        //if receive the whole results with QUERY_OK at one time, will
                        //split with every four to show on UI
                        Log.d("SignalTest", " onNetworksListLoadResult result.length is: " + result.length + "operator_info_entries_num " + operator_info_entries_num);
                        for (int i = 0; i < result.length / operator_info_entries_num; i++) {
                            Log.d("SignalTest", " onNetworksListLoadResult for: ");
                            int j = operator_info_entries_num * i;
                            Log.i("signaltest result", "signaltest result:" + result[0 + j]+ "\t"+ result[1 + j] +  "\t"+result[2 + j]+"\t"+result[3 + j]+"\t" +"\t"+result[4 + j]);
                            OperatorInfo opInfo = new OperatorInfo(result[0 + j], result[1 + j],result[2 + j], result[3 + j]);
                            String title = mRatMap.get(opInfo.getRadioTech()); 
                            //String title = "";
                            //networkList += title + "\t\t\t\t\t\t\t" + result[4 + j] + "\n";
                            networkList += result[0 + j] + " " + title + "("+result[3 + j] + ")\t\t\t\t\t\t\t" + "  "  + result[4 + j] + "\n";
                        }
                    } else {
                        Log.d("SignalTest", "result.length is: " + result.length);
                        waitHint.setText("NetworksList Result's format are not right!");
                    }
                }
                }//end swicth
             } 

                   
            }
        });
        
    }

    @Override    
    protected void onDestroy() {        
        super.onDestroy();        
        unregisterReceiver(mBroadcastReceiver);
        telManager.setDataEnabled(mobileData);
        //mQcRilHook.dispose();
    }
    //end by qianyan
    
    @Override
    public String getModuleName() {
        return "Signal";
    }


}
