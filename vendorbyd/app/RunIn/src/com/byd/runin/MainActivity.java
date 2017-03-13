package com.byd.runin;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.runin.TestLog.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity
{
    private static final String TAG = "MainActivity";
    LinearLayout screen = null;
    public static String[]mTestPVTTextView =
    {
        "CPU Test",
        "2D Test",
        "3D Test",
        "Memory Test",
        "EMMC Test",
        "Sleep Test",
        "RLV Test",
        /*
        "LCD Test",
        "Rec Test",
        "Vibrator Test",*/
        "VS Test",
        /*
        "Video Test",
        "Spk Test",*/
        "MC Test",
        /*
        "MIC Test",
        "Camera Test",*/
        "Switch Camera Test",
        "Reboot Test"
    };
    private static String[]mTestPvtDefaultValue = {
        "240",
        "240",
        "240",
        "240",
        "240",
        "2000",
        "3300",
        "3600",
        "1440",
        "20",
        "200"
    };

    public static String[]mTestPVTUnit =
    {
        "Min",
        "Min",
        "Min",
        "Min",
        "Min",
        "Cycles",
        "Min",
        "Min",
        "Min",
        "Cycles",
        "Cycles"
    };

    private static String[]mTestSSUnit =
    {
        "Cycles",
        "Min",
        "Min",
        "Min",
        "Cycles",
        "Cycles"
    };
    private static String[]mTestSSTextView =
    {
        "Sleep Test",
        "RLV Test",
        /*
        "LCD Test",
        "Rec Test",
        "Vibrator Test",*/
        "VS Test",
        /*
        "Video Test",
        "Spk Test",*/
        "MC Test",
        /*
        "MIC Test",
        "Camera Test",
        "FrontCamera Test",*/
        "Switch Camera Test",
        "Reboot Test"
      };

     private static String[] mTestSSDefaultValue = {
         "2000",
         "1440",
         "720",
         "720",
         "20",
         "200"
        };

     //Begin zhang.lu7@byd.com add final Test
     public static String[] mFinalTest = {
         "24H",
         "16H",
         "8H",
         "4H",
         "2H",
         "1H",
         "0.5H"
     };

     public static String[] mTestFinalTextView = {
         "CPU Test",
         "Memory Test",
         "EMMC Test",
         "Sound Test",
         "2D Test",
         "3D Test",
         "video Test",
         "Sleep Test",
         "Switch Camera Test",
         "Reboot Test"
       };


     public static String[] mTestFinalUnitView = {
         "Min",
         "Min",
         "Min",
         "Min",
         "Min",
         "Min",
         "Min",
         "Cycles",
         "Cycles",
         "Cycles"
       };

     private static String[] mTest24hValue = {
         "480",
         "120",
         "120",
         "120",
         "180",
         "240",
         "180",
         "50",
         "20",
         "50"
       };

     private static String[] mTest16hValue = {
         "160",
         "90",
         "90",
         "90",
         "160",
         "160",
         "90",
         "32",
         "20",
         "32"
       };

     private static String[] mTest8hValue = {
         "80",
         "40",
         "40",
         "40",
         "80",
         "80",
         "40",
         "16",
         "20",
         "16"
       };

     private static String[] mTest4hValue = {
         "40",
         "20",
         "20",
         "20",
         "40",
         "40",
         "20",
         "8",
         "20",
         "8"
       };

     private static String[] mTest2hValue = {
         "20",
         "10",
         "10",
         "10",
         "20",
         "20",
         "10",
         "4",
         "20",
         "4"
       };

     private static String[] mTest1hValue = {
         "10",
         "5",
         "5",
         "5",
         "10",
         "10",
         "5",
         "4",
         "20",
         "4"
       };

     private static String[] mTestHalfValue = {
         "5",
         "2",
         "2",
         "2",
         "5",
         "5",
         "2",
         "2",
         "20",
         "2"
       };
     //End


         private boolean isPluggedMode = false;
         private PowerManager.WakeLock mScreenOnWakeLock = null;

         private ArrayList<TestListItem> mList = null;

         private ArrayList<String> mFinalList = null;
         private String mTestMode = null;
         private int index = -1;

         private static final String TEST24H = "24H";
         private static final String TEST16H = "16H";
         private static final String TEST8H = "8H";
         private static final String TEST4H = "4H";
         private static final String TEST2H = "2H";
         private static final String TEST1H = "1H";
         private static final String TESTHALFH = "0.5H";

         private void init(){
             mList = new ArrayList<TestListItem>();
	    Log.d(TAG, "mTestPVTTextView.length = "+ mTestPVTTextView.length);
	    if( mTestMode.equals(RuninTestMainActivity.PVT) ){
	    for(int i = 0; i < mTestPVTTextView.length;++i){
	        TestListItem tli = new TestListItem();
	        tli.testMode = mTestMode;
	        tli.testItemName = mTestPVTTextView[i];
	        tli.testItemValue = mTestPvtDefaultValue[i];
	        tli.testItemUnit = mTestPVTUnit[i];
	        mList.add(i, tli);
	    }
	    }else if(mTestMode.equals(RuninTestMainActivity.SS)){
	        for(int i = 0; i < mTestSSTextView.length;++i){
	        TestListItem tli = new TestListItem();
	        tli.testMode = mTestMode;
	        tli.testItemName = mTestSSTextView[i];
	        tli.testItemValue = mTestSSDefaultValue[i];
	        tli.testItemUnit = mTestSSUnit[i];
	        mList.add(i, tli);

            }
        }

        Log.d(TAG, "init:mTestMode size = " + mList.size());
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        mTestMode = intent.getStringExtra("testMode");

        SharedPref.saveTestMode(this, mTestMode);

        SystemProperties.set("persist.sys.runin.test", "true");

        this.registerReceiver(mBatteryInfoReceiver, new IntentFilter(
	        Intent.ACTION_BATTERY_CHANGED));

	        if ( !mTestMode.equals(RuninTestMainActivity.FINAL) ){
	        init();
            ListAdapter adapter = new MyListAdapter(this, R.layout.activity_main_list_item, mList);
            setListAdapter(adapter);

            Button btn_confirm = (Button)findViewById(R.id.btn_confirm);
            btn_confirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "isPluggedMode = "+ isPluggedMode);
                    if ( !checkAudioSource() ){
                        AlertDialog.Builder buidler = new AlertDialog.Builder(MainActivity.this);
                        buidler.setTitle("Information")
                            .setMessage("Audio file does not exist!")
                            .setPositiveButton("Confirm", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    ;
                                }
                            })
                            .create()
                            .show();
                        return;
                    }

                    if ( isPluggedMode ){
                        if ( isInputValueValid() ){
                            Intent intent = new Intent(MainActivity.this, AutoTestActivity.class);
                            intent.putParcelableArrayListExtra("testItemList", mList);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this, R.string.invalidString, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, R.string.usb_conn, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
                }else{
                if( mFinalList != null ){
                mFinalList.clear();
                mFinalList = null;
                }
                mFinalList = new ArrayList<String>();
                for(int i = 0;i < mFinalTest.length; ++i){
                mFinalList.add(mFinalTest[i]);
                }
                ListAdapter adapter = new MyListFinalAdapter(this, R.layout.activity_main_list_item, mFinalList);
                setListAdapter(adapter);

            Button btn_confirm = (Button)findViewById(R.id.btn_confirm);
            btn_confirm.setVisibility(View.INVISIBLE);
            Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
            btn_cancel.setVisibility(View.INVISIBLE);
	        }

        SharedPref.clearTestStatus(this);
    }

    private boolean isInputValueValid()
    {

        if (mList != null && mList.size() > 0)
        {
            int index = 0;
            for (TestListItem tli: mList)
            {
                tli = mList.get(index);
                if (tli.testItemValue == null || tli.testItemValue.equals(""))
                {
                    return false;
                }
                ++index;
            }
        }

        return true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(mBatteryInfoReceiver);
        SystemProperties.set("persist.sys.runin.test", "false");
    }

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,  -1);
                Log.d(TAG, "plugged = " + plugged + "   status = " + status);

                if (plugged != 0)
                {
                    isPluggedMode = true;
                }
                else
                {
                    isPluggedMode = false;
                }
            }
        }
    };

    private class MyListFinalAdapter extends ArrayAdapter<String>{
        private LayoutInflater mLayoutInflater;
        private List<String> mFinalStr;
        ViewHolder holder = null;

        public MyListFinalAdapter(Context context, int resource,
                List<String> finalStrList) {
            super(context, resource, finalStrList);
            // TODO Auto-generated constructor stub
            mFinalStr = finalStrList;
            mLayoutInflater = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFinalStr.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final int _position = position;
            if ( convertView == null ){
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.activity_main_list_item, null);

                holder.mTextView = (TextView)convertView.findViewById(R.id.main_item);
                holder.mTextView.setClickable(true);
                holder.mTextView.setFocusable(true);
                holder.mTextView.setHeight(100);
                holder.mTextView.setWidth(1000);
                holder.mTextView.setTextSize(30);

                screen = (LinearLayout)convertView.findViewById(R.id.screen);
                screen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (v.getId() == R.id.screen) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            //holder.mEditText.set
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });

                holder.mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "isPluggedMode = "+ isPluggedMode);
                        String finalString = mFinalStr.get(_position);
                        Log.d(TAG, "finalString = "+ finalString);
                        if ( !checkAudioSource() ){
                            AlertDialog.Builder buidler = new AlertDialog.Builder(MainActivity.this);
                            buidler.setTitle("Information")
                                .setMessage("Audio file does not exist!")
                                .setPositiveButton("Confirm", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        ;
                                    }
                                })
                                .create()
                                .show();
                            return;
                        }

                        if ( isPluggedMode ){
                            //String finalString = mFinalStr.get(_position);
                            Log.d(TAG, "finalString = "+ finalString);
                            if ( mList != null ){
                                mList.clear();
                                mList = null;
                            }

                            mList = new ArrayList<TestListItem>();
                            if ( finalString.equals(TEST24H) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTest24hValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }else if( finalString.equals(TEST16H) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTest16hValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }else if( finalString.equals(TEST8H) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTest8hValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }else if( finalString.equals(TEST4H) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTest4hValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }else if( finalString.equals(TEST2H) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTest2hValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }else if( finalString.equals(TEST1H) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTest1hValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }else if( finalString.equals(TESTHALFH) ){
                                for(int i = 0; i < mTestFinalTextView.length;++i){
                                    TestListItem tli = new TestListItem();
                                    tli.testMode = mTestMode;
                                    tli.testItemName = mTestFinalTextView[i];
                                    tli.testItemValue = mTestHalfValue[i];
                                    tli.testItemUnit = mTestFinalUnitView[i];
                                    mList.add(i, tli);
                                }
                            }

                            Intent intent = new Intent(MainActivity.this, AutoTestActivity.class);
                            intent.putParcelableArrayListExtra("testItemList", mList);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this, R.string.usb_conn, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            String tli = (String)mFinalStr.get(position);
            holder.mTextView.setText(tli);

            return convertView;
        }

    }

    private class MyListAdapter extends ArrayAdapter<TestListItem>{
        private LayoutInflater mLayoutInflater;
        private List < TestListItem > mList;
        ViewHolder holder = null;

        public MyListAdapter(Context context, int resource, List<TestListItem> list)
        {
            super(context, resource, list);
            // TODO Auto-generated constructor stub
            mList = list;
            mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return mList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.activity_main_list_item, null);

                holder.mTextView = (TextView)convertView.findViewById(R.id.main_item);
                holder.mEditText = (EditText)convertView.findViewById(R.id.main_editor);
                holder.mEditText.setVisibility(View.VISIBLE);

                screen = (LinearLayout)convertView.findViewById(R.id.screen);
                screen.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        // TODO Auto-generated method stub
                        if (v.getId() == R.id.screen)
                        {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            //holder.mEditText.set
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });
                holder.mEditText.setOnTouchListener(new View.OnTouchListener()
                {

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        // TODO Auto-generated method stub
                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            index = (Integer)v.getTag();
                        }
                        return false;
                    }
                });

                holder.mUnitTextView = (TextView)convertView.findViewById(R.id.unit);
                holder.mUnitTextView.setVisibility(View.VISIBLE);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            TestListItem tli = (TestListItem)mList.get(position);
            holder.mTextView.setText(tli.testItemName);
            holder.mEditText.setText(tli.testItemValue);
            holder.mUnitTextView.setText(tli.testItemUnit);
            holder.mEditText.setTag(position);
            holder.mEditText.addTextChangedListener(new MyTextWatcher(holder));

            holder.mEditText.clearFocus();
            if (index !=  - 1 && index == position)
            {
                holder.mEditText.requestFocus();
                holder.mEditText.setSelection(holder.mEditText.getText().length());

            }
            return convertView;
        }

    }

    class MyTextWatcher implements TextWatcher
    {

        private ViewHolder mHolder = null;

        public MyTextWatcher(ViewHolder holder)
        {
            mHolder = holder;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            // TODO Auto-generated method stub
            if (s != null && !"" .equals(s.toString()))
            {
                int position = (Integer)mHolder.mEditText.getTag();
                if (mHolder.mTextView.getText().equals(mList.get(position).testItemName))
                {
                    Log.d(TAG, "getView testItemValue ====== ");
                    mList.get(position).testItemValue = s.toString();
                        //change data
                }
            }
        }
    };

    class ViewHolder
    {
        TextView mTextView;
        EditText mEditText;
        TextView mUnitTextView;
    }


    private boolean checkAudioSource()
    {
        File filepath = null;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
        {
            File file = Environment.getExternalStorageDirectory();
            File temp = new File(file.getParent());
            filepath = new File(temp.getParent() + File.separator + "sdcard0" + File.separator + "Song_for_Run_in_Test.wav");
            Log.d(TAG, "runin filepath ====== "+filepath);
        }

        if (!filepath.exists())
        {
            return false;

        }

        return true;
    }




}
