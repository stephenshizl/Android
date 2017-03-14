package com.btwifi.manualtest;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
public class RxtestActivity extends TestModule {
    private ArrayAdapter<String> datapatternitemsAdapter;
    private ArrayAdapter<String> packettypeitemsAdapter;
    private ArrayAdapter<String> whiteningitemsAdapter;
    private ArrayAdapter<String> poweritemsAdapter;
    private ArrayAdapter<String> rxgainitemsAdapter;
    private ArrayAdapter<String> lendataitemsAdapter;
    
    private String[] datapatternItems;
    private String[] packettypeItems;
    private String[] whiteningItems;
    private String[] powerItems;
    private String[] rxgainItems;
    private String[] lendataItems;
    
    private String[] datapatternitemsValue;
    private String[] packettypeitemsValue;
    private String[] whiteningitemsValue;
    private String[] poweritemsValue;
    private String[] rxgainitemsValue;
    private String[] lendataitemsValue;
    
    Spinner datapatternSpinner;
    Spinner packettypeSpinner;
    Spinner whiteningSpinner;
    Spinner powerSpinner;
    Spinner rxgainSpinner;
    Spinner lendataSpinner;
    
    String datapatternCommand;
    String packettypeCommand;
    String whiteningCommand;
    String powerCommand;
    String rxgainCommand;
    String lendataCommand;
    OnItemSelectedListener spinnerSelect;
    String commandCode = "20";
    static int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, commandCode);
        setContentView(R.layout.activity_txtest);
        Button sentButton = (Button)findViewById(R.id.send);
        sentButton.setOnClickListener(sendOnClickListener);
        initData();
    }
    OnClickListener sendOnClickListener = new OnClickListener() {
         @Override
         public void onClick(View v) {
             Toast.makeText(RxtestActivity.this, R.string.sendok, 2000).show();
             OnClickToCommand(); 
         }
    };
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public String getCommand() {
        i = i+1;
        //String command = "'echo " + i + " > /data/aaaaa.txt'";
        String command = "btconfig /dev/smd3 rawcmd 0x3F 0x0004 0x04 ";
        command += "0x27 0x27 0x27 0x27 0x27 ";
        command += datapatternCommand + " ";
        command += packettypeCommand + " ";
        command += whiteningCommand + " ";
        command += powerCommand + " ";
        command += rxgainCommand + " ";
        command += "0x9C 0x35 0xBD 0x9C 0x35 0xBD ";
        command += "0x00 ";
        command += lendataCommand + " ";
        command += "0x00 ";
        Log.i(TAG, "TX COMMAND:" + command);
        return command; 
    }
    private void initData() {
        
        datapatternItems = this.getResources().getStringArray(R.array.datapattern);
        packettypeItems = this.getResources().getStringArray(R.array.packettype);
        whiteningItems = this.getResources().getStringArray(R.array.whitening);
        powerItems = this.getResources().getStringArray(R.array.power);
        rxgainItems = this.getResources().getStringArray(R.array.rxgain);
        lendataItems = this.getResources().getStringArray(R.array.packettypelength);
        
        datapatternitemsValue = this.getResources().getStringArray(R.array.datapatternvalue);
        packettypeitemsValue = this.getResources().getStringArray(R.array.packettypevalue);
        whiteningitemsValue = this.getResources().getStringArray(R.array.Whiteningvalue);
        poweritemsValue = this.getResources().getStringArray(R.array.powervalue);
        rxgainitemsValue = this.getResources().getStringArray(R.array.rxgainvalue);
        lendataitemsValue = this.getResources().getStringArray(R.array.packettypelengthvalue);
        
        datapatternitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datapatternItems);
        packettypeitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, packettypeItems);
        whiteningitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, whiteningItems);
        poweritemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, powerItems);
        rxgainitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rxgainItems);
        lendataitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lendataItems);
        
        datapatternitemsAdapter.setDropDownViewResource (android.R.layout.simple_list_item_single_choice);
        packettypeitemsAdapter.setDropDownViewResource (android.R.layout.simple_list_item_single_choice);
        whiteningitemsAdapter.setDropDownViewResource (android.R.layout.simple_list_item_single_choice);
        poweritemsAdapter.setDropDownViewResource (android.R.layout.simple_list_item_single_choice);
        rxgainitemsAdapter.setDropDownViewResource (android.R.layout.simple_list_item_single_choice);
        lendataitemsAdapter.setDropDownViewResource (android.R.layout.simple_list_item_single_choice);
        
        datapatternSpinner = (Spinner) findViewById(R.id.datapattern);
        packettypeSpinner = (Spinner) findViewById(R.id.packettype);
        whiteningSpinner = (Spinner) findViewById(R.id.whitening);
        powerSpinner = (Spinner) findViewById(R.id.power);
        rxgainSpinner = (Spinner) findViewById(R.id.rxgain);
        lendataSpinner = (Spinner) findViewById(R.id.lendata);
        
        datapatternSpinner.setAdapter(datapatternitemsAdapter);
        packettypeSpinner.setAdapter(packettypeitemsAdapter);
        whiteningSpinner.setAdapter(whiteningitemsAdapter);
        powerSpinner.setAdapter(poweritemsAdapter);
        rxgainSpinner.setAdapter(rxgainitemsAdapter);
        lendataSpinner.setAdapter(lendataitemsAdapter);
        
        datapatternCommand = datapatternitemsValue[0];
        packettypeCommand = packettypeitemsValue[0];
        whiteningCommand = whiteningitemsValue[0];
        powerCommand = poweritemsValue[0];
        rxgainCommand = rxgainitemsValue[0];
        lendataCommand = lendataitemsValue[0];
        
        datapatternSpinner.setOnItemSelectedListener(spinerFunction(datapatternSpinner.getId()));
        packettypeSpinner.setOnItemSelectedListener(spinerFunction(packettypeSpinner.getId()));
        whiteningSpinner.setOnItemSelectedListener(spinerFunction(whiteningSpinner.getId()));
        powerSpinner.setOnItemSelectedListener(spinerFunction(powerSpinner.getId()));
        rxgainSpinner.setOnItemSelectedListener(spinerFunction(rxgainSpinner.getId()));
        lendataSpinner.setOnItemSelectedListener(spinerFunction(lendataSpinner.getId()));
    }

    private OnItemSelectedListener spinerFunction(int viewid) {
        final int viewId = viewid;
        spinnerSelect = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long id) {    
                Log.i(TAG, "select command viewId:" + viewId + " =" + R.id.datapattern);
                switch(viewId) {
                    case R.id.datapattern:
                        datapatternCommand = datapatternitemsValue[arg2];
                        Log.i(TAG, "select command datapatternCommand:" + datapatternitemsValue[arg2]);
                        break;
                    case R.id.packettype:
                        packettypeCommand = packettypeitemsValue[arg2];
                        Log.i(TAG, "select command packettypeCommand:" + packettypeitemsValue[arg2]);
                        break;
                    case R.id.whitening:
                        whiteningCommand = whiteningitemsValue[arg2];
                        Log.i(TAG, "select command whiteningCommand:" + whiteningitemsValue[arg2]);
                        break;
                    case R.id.power:
                        powerCommand = poweritemsValue[arg2];
                        Log.i(TAG, "select command powerCommand:" + poweritemsValue[arg2]);
                        break;
                    case R.id.rxgain:
                        rxgainCommand = rxgainitemsValue[arg2];
                        Log.i(TAG, "select command rxgainCommand:" + rxgainitemsValue[arg2]);
                        break;
                    case R.id.lendata:
                        lendataCommand = lendataitemsValue[arg2];
                        Log.i(TAG, "select command lendataCommand:" + lendataitemsValue[arg2]);
                        break;
                }                
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                           
            }
        };
        return spinnerSelect;
    }


}
