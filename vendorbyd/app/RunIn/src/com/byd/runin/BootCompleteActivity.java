package com.byd.runin;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.byd.runin.TestLog.Log;
import com.byd.runin._2d._2dActivity;
import com.byd.runin._3d._3dActivity;
import com.byd.runin.battery.BatteryTest;
import com.byd.runin.camera.CameraActivity;
import com.byd.runin.camera.SwitchCameraActivity;
import com.byd.runin.front_camera.FrontCameraActivity;
import com.byd.runin.cpu.CpuActivity;
import com.byd.runin.emmc.EmmcActivity;
import com.byd.runin.lcd.LcdActivity;
import com.byd.runin.memory.MemoryActivity;
import com.byd.runin.mic.MicActivity;
import com.byd.runin.reboot.RebootActivity;
import com.byd.runin.receiver.ReceiverActivity;
import com.byd.runin.s3.S3Activity;
import com.byd.runin.speaker.SpeakerActivity;
import com.byd.runin.vibrator.VibratorActivity;
import com.byd.runin.video.VideoActivity;
import com.byd.runinjin.RuninTest;

import android.os.SystemProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class BootCompleteActivity extends ListActivity
{
    private static final String TAG = "BootCompleteActivity";
	private static final int RUNIN_TEST_WRITE_FLAG = 0;
	private static final String SAVE_TEST_RESULT_FILE_PATH = "/data/data/com.byd.runin/runin_test_result.dat";
	private boolean writeFlag = true;

	private BatteryTest mBatteryTest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Log.d(TAG, "boot complete, on create");
        //SystemProperties.set("service.cit.enable", "1");

        mBatteryTest = new BatteryTest(this, null);
        mBatteryTest.start();

        if (checkRebootAgain()) {
            finish();
        } else {
            SystemProperties.set("persist.sys.runin.test", "false");
            SharedPref.clearRebootSettings(this);
            /*Intent mIntent = new Intent(this,TestResultActivity.class);
            startActivity(mIntent);*/
            setContentView(R.layout.test_result);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            displayTestMessage();
            int value = (writeFlag == true ? 1 : 0);
            saveTestResult(value);
        }
    }

    private boolean checkRebootAgain()
    {
        long done_times = SharedPref.getRebootDoneTimes(this);
        long times = SharedPref.getRebootTimes(this);
        if (times < 0 || done_times < 0)
        {
            Log.e(TAG, "reboot times and done_times error with times = " + times + " done times = " + done_times);
            return false;
        }

        Log.d(TAG, "reboot times = " + times + " done times = " + done_times);

        if (done_times < times)
        {
            SharedPref.saveRebootDoneTimes(this, done_times + 1);

            Intent intent = new Intent(this, RebootActivity.class);
            startActivity(intent);
        }

        return done_times < times;
    }

    private void displayTestMessage()
    {
        String result = "";
        ArrayList < String > list = new ArrayList < String > ();
        String testMode = SharedPref.getTestMode(this);

        if ( testMode.equals(RuninTestMainActivity.FINAL) ){
            getItemMessage(list, CpuActivity.KEY_SHARED_PREF, CpuActivity.TITLE);
            getItemMessage(list, MemoryActivity.KEY_SHARED_PREF, MemoryActivity.TITLE);
            getItemMessage(list, EmmcActivity.KEY_SHARED_PREF, EmmcActivity.TITLE);
            getItemMessage(list, SpeakerActivity.KEY_SHARED_PREF, SpeakerActivity.TITLE);
            getItemMessage(list, _2dActivity.KEY_SHARED_PREF, _2dActivity.TITLE);
            getItemMessage(list, _3dActivity.KEY_SHARED_PREF, _3dActivity.TITLE);
            getItemMessage(list, VideoActivity.KEY_SHARED_PREF, VideoActivity.TITLE);
            getItemMessage(list, S3Activity.KEY_SHARED_PREF, S3Activity.TITLE);
            getItemMessage(list, SwitchCameraActivity.KEY_SHARED_PREF, SwitchCameraActivity.TITLE);
            getItemMessage(list, RebootActivity.KEY_SHARED_PREF, RebootActivity.TITLE);

        }else{
            if ( testMode.equals(RuninTestMainActivity.PVT) ){
                getItemMessage(list, CpuActivity.KEY_SHARED_PREF, CpuActivity.TITLE);
                getItemMessage(list, _2dActivity.KEY_SHARED_PREF, _2dActivity.TITLE);
                getItemMessage(list, _3dActivity.KEY_SHARED_PREF, _3dActivity.TITLE);
                getItemMessage(list, MemoryActivity.KEY_SHARED_PREF, MemoryActivity.TITLE);
                getItemMessage(list, EmmcActivity.KEY_SHARED_PREF, EmmcActivity.TITLE);
            }
            getItemMessage(list, S3Activity.KEY_SHARED_PREF, S3Activity.TITLE);
            getItemMessage(list, LcdActivity.KEY_SHARED_PREF, LcdActivity.TITLE);
            getItemMessage(list, VideoActivity.KEY_SHARED_PREF, VideoActivity.TITLE);
            getItemMessage(list, CameraActivity.KEY_SHARED_PREF, CameraActivity.TITLE);
            getItemMessage(list, SwitchCameraActivity.KEY_SHARED_PREF, SwitchCameraActivity.TITLE);
            getItemMessage(list, RebootActivity.KEY_SHARED_PREF, RebootActivity.TITLE);
        }



        ListAdapter adapter = new mArrayAdapter(this,
            R.layout.activity_main_list_item, list);
        setListAdapter(adapter);
    }

     private void getItemMessage(ArrayList < String > list, String key, String title)
    {
        int status = SharedPref.getTestStatus(this, key);

        if (key == RebootActivity.KEY_SHARED_PREF && status ==
            TestActivity.TestStatus.ING)
        {
            status = TestActivity.TestStatus.DONE;
        }

        if (status == TestActivity.TestStatus.NONE)
            return ;
        else if (status == TestActivity.TestStatus.ING || status ==
            TestActivity.TestStatus.ERROR)
        {
            list.add(title + " Fail");
            writeFlag = false;
        }
        else if (status == TestActivity.TestStatus.DONE)
        {
            list.add(title + " Success");
        }
    }

    private void saveTestResult(int value)
    {
        /*
        int rec = new RuninTest().runinTestFlag(RUNIN_TEST_WRITE_FLAG, value);
        if ( rec == 1 ){
        //SystemProperties.set("service.cit.enable", "0");
        }*/

        File file = new File(SAVE_TEST_RESULT_FILE_PATH);
        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            fw.write(String.valueOf(value));
            fw.close();

        }
        catch (IOException e)
        {
            Log.d(TAG, "saveTestResult:fail");
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            finish();
            return false;
        }
        return false;
    }

    private class mArrayAdapter extends ArrayAdapter < String >
    {

        private LayoutInflater mLayoutInflater;
        private ArrayList < String > mList;
        private TextView mTitle;

        public mArrayAdapter(Context context, int textViewResourceId, ArrayList
            < String > list)
        {
            super(context, textViewResourceId, list);
            mList = list;
            mLayoutInflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup
            parent)
        {

            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate
                    (R.layout.activity_main_list_item, null);
                mTitle = (TextView)convertView.findViewById(R.id.main_item);
            }
            else
            {
                mTitle = (TextView)convertView.findViewById(R.id.main_item);
            }

            mTitle.setText(getItem(position));
            String mFail = mList.get(position);
            mFail = mFail.substring(mFail.length() - 4, mFail.length());
            if ("Fail" .equals(mFail))
            {
                mTitle.setTextColor(Color.RED);
            }

                return convertView;
             }
        }

        @Override
        protected void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            Log.d(TAG, "BootCompleteActivity onDestroy");
            if (mBatteryTest != null){
            mBatteryTest.stop();
            mBatteryTest = null;
        }
       }

         @Override
         protected void onPause() {
             // TODO Auto-generated method stub
             super.onPause();
             Log.d(TAG, "BootCompleteActivity onPause");
        if (mBatteryTest != null){
            mBatteryTest.stop();
            mBatteryTest = null;
        }
        }
}
