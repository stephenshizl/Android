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
import android.widget.RadioGroup;
public class BletestmodeActivity extends TestModule {
    private ArrayAdapter<String> patternitemsAdapter;
    private ArrayAdapter<String> channelitemsAdapter;

    
    private String[] patternItems;
    private String[] channelItems;
    
    private String[] patternitemsValue;
    private String[] channelitemsValue;
    
    Spinner patternSpinner;
    Spinner channelSpinner;

    
    String patternCommand;
    String channelCommand;

    OnItemSelectedListener spinnerSelect;
    String commandCode = "20";
    static boolean startorstop = true;
    String whichCommandString = "0x001E ";
    RadioGroup group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, commandCode);
        setContentView(R.layout.activity_bletest);
        Button sentButton = (Button)findViewById(R.id.send);
        Button stopButton = (Button)findViewById(R.id.stop);
        sentButton.setOnClickListener(sendOnClickListener);
        stopButton.setOnClickListener(sendOnClickListener);
        initData();
        whichCommandString = "0x001E ";
        group = (RadioGroup)findViewById(R.id.radioGroup); 
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){        
           @Override  
           public void onCheckedChanged(RadioGroup group, int checkedId){     
               switch(checkedId){
               case R.id.txtest:
                   whichCommandString = "0x001E ";
                   break;
               case R.id.rxtest:
                   whichCommandString = "0x001D ";
                   break;
               }
           }   
       });  
    }
    OnClickListener sendOnClickListener = new OnClickListener() {
         @Override
         public void onClick(View v) {
             if(v.getId() == R.id.send) {
                 startorstop = true;
             } else {
                 startorstop = false;
             }
             Toast.makeText(BletestmodeActivity.this, R.string.sendok, 2000).show();
             OnClickToCommand(); 
         }
    };
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public String getCommand() {
        if(startorstop) {//START TEST
            String command = "btconfig /dev/smd3 rawcmd 0x08 ";
            if(whichCommandString.equals("0x001E ")) {//TX
                command += whichCommandString;
                command += channelCommand + " ";
                command += "0x25 ";
                command += patternCommand + " ";
            } else if(whichCommandString.equals("0x001D ")){ //RX
                command += whichCommandString;
                command += channelCommand;
            }

            Log.i(TAG, "TX COMMAND:" + command);
            return command; 
        } else {//STOP TEST
            Log.i(TAG, "TX COMMAND:btconfig /dev/smd3 rawcmd 0x08 0x001F");
            return "btconfig /dev/smd3 rawcmd 0x08 0x001F";
        }
        
    }
    private void initData() {
        
        patternItems = this.getResources().getStringArray(R.array.pattern);
        channelItems = this.getResources().getStringArray(R.array.channel);
       
        patternitemsValue = this.getResources().getStringArray(R.array.patternvalue);
        channelitemsValue = this.getResources().getStringArray(R.array.channelvalue);
        
        patternitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, patternItems);
        channelitemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, channelItems);
        
        patternitemsAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        channelitemsAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
       
        patternSpinner = (Spinner) findViewById(R.id.pattern);
        channelSpinner = (Spinner) findViewById(R.id.channel);
        
        patternSpinner.setAdapter(patternitemsAdapter);
        channelSpinner.setAdapter(channelitemsAdapter);
        
        patternCommand = patternitemsValue[0];
        channelCommand = channelitemsValue[0];
       
        patternSpinner.setOnItemSelectedListener(spinerFunction(patternSpinner.getId()));
        channelSpinner.setOnItemSelectedListener(spinerFunction(channelSpinner.getId()));

    }

    private OnItemSelectedListener spinerFunction(int viewid) {
        final int viewId = viewid;
        spinnerSelect = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long id) {    
                Log.i(TAG, "select command viewId:" + viewId + " =" + R.id.pattern);
                switch(viewId) {
                    case R.id.pattern:
                        patternCommand = patternitemsValue[arg2];
                        Log.i(TAG, "select command patterCommand:" + patternitemsValue[arg2]);
                        break;
                    case R.id.channel:
                        channelCommand = channelitemsValue[arg2];
                        Log.i(TAG, "select command channelCommand:" + channelitemsValue[arg2]);
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
