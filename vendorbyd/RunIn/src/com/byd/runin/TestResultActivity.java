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

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.byd.runin.TestLog.Log;
import com.byd.runin._2d._2dActivity;
import com.byd.runin._3d._3dActivity;
import com.byd.runin.audio.AudioActivity;
import com.byd.runin.battery.BatteryActivity;
import com.byd.runin.camera.CameraActivity;
import com.byd.runin.front_camera.FrontCameraActivity;
import com.byd.runin.cpu.CpuActivity;
import com.byd.runin.emmc.EmmcActivity;
import com.byd.runin.lcd.LcdActivity;
import com.byd.runin.memory.MemoryActivity;
import com.byd.runin.reboot.RebootActivity;
import com.byd.runin.s3.S3Activity;
import com.byd.runin.video.VideoActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class TestResultActivity extends ListActivity
{
    private static final String TAG = "TestResultActivity";

    private static final String FILE_PATH = "/data/data/com.byd.runin/";

    private static final int LENGTH_SIZE = 50;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_result);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayTestMessage();
        mkDir();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void displayTestMessage()
    {
        String result = "";
        ArrayList < String > list = new ArrayList < String > ();
        getItemMessage(list, RebootActivity.KEY_SHARED_PREF,
            RebootActivity.TITLE);
        getItemMessage(list, CpuActivity.KEY_SHARED_PREF, CpuActivity.TITLE);
        getItemMessage(list, EmmcActivity.KEY_SHARED_PREF, EmmcActivity.TITLE);
        getItemMessage(list, MemoryActivity.KEY_SHARED_PREF,
            MemoryActivity.TITLE);
        getItemMessage(list, S3Activity.KEY_SHARED_PREF, S3Activity.TITLE);
        //      getItemMessage(list, AudioActivity.KEY_SHARED_PREF, AudioActivity.TITLE);
        getItemMessage(list, VideoActivity.KEY_SHARED_PREF, VideoActivity.TITLE)
            ;
        getItemMessage(list, LcdActivity.KEY_SHARED_PREF, LcdActivity.TITLE);
        getItemMessage(list, _2dActivity.KEY_SHARED_PREF, _2dActivity.TITLE);
        getItemMessage(list, _3dActivity.KEY_SHARED_PREF, _3dActivity.TITLE);
        getItemMessage(list, CameraActivity.KEY_SHARED_PREF,
            CameraActivity.TITLE);
        getItemMessage(list, FrontCameraActivity.KEY_SHARED_PREF,
            FrontCameraActivity.TITLE);
        getItemMessage(list, BatteryActivity.KEY_SHARED_PREF,
            BatteryActivity.TITLE);

        //ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_main_list_item,
        //R.id.main_item, list);
        ListAdapter adapter = new mArrayAdapter(this,
            R.layout.activity_main_list_item, list);
        setListAdapter(adapter);
    }

    private String getTestMessage()
    {
        String result = "";
        ArrayList < String > list = new ArrayList < String > ();
        getItemMessage(list, RebootActivity.KEY_SHARED_PREF,RebootActivity.TITLE);
        getItemMessage(list, CpuActivity.KEY_SHARED_PREF, CpuActivity.TITLE);
        getItemMessage(list, EmmcActivity.KEY_SHARED_PREF, EmmcActivity.TITLE);
        getItemMessage(list, MemoryActivity.KEY_SHARED_PREF,MemoryActivity.TITLE);
        getItemMessage(list, S3Activity.KEY_SHARED_PREF, S3Activity.TITLE);
        //        getItemMessage(list, AudioActivity.KEY_SHARED_PREF, AudioActivity.TITLE);
        getItemMessage(list, VideoActivity.KEY_SHARED_PREF, VideoActivity.TITLE);
        getItemMessage(list, LcdActivity.KEY_SHARED_PREF, LcdActivity.TITLE);
        getItemMessage(list, _2dActivity.KEY_SHARED_PREF, _2dActivity.TITLE);
        getItemMessage(list, _3dActivity.KEY_SHARED_PREF, _3dActivity.TITLE);
        getItemMessage(list, CameraActivity.KEY_SHARED_PREF,CameraActivity.TITLE);
        getItemMessage(list, FrontCameraActivity.KEY_SHARED_PREF,FrontCameraActivity.TITLE);
        getItemMessage(list, BatteryActivity.KEY_SHARED_PREF,BatteryActivity.TITLE);

        int i;
        for (i = 0; i < list.size() - 1; i++)
        {
            result += list.get(i) + "\n";
        }
        if (list.size() > 0)
            result += list.get(list.size() - 1);

        Log.d(TAG, "result = " + result);

        return result;
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
        else if (status == TestActivity.TestStatus.ING)
        {
            list.add(title + " Fail");
        }
        else if (status == TestActivity.TestStatus.DONE)
        {
            list.add(title + " Success");
        }
    }

    private void mkDir()
    {

        try
        {
            File file = new File(FILE_PATH, "textResult.txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(file, true);
            String msg = getTestMessage();

            if (TextUtils.isEmpty(msg))
            {
                Log.w(TAG, "no test message");
                return ;
            }
            StringBuffer sb = new StringBuffer();
            sb.append(
                "\r\n\r\n\r\n#######################################\r\n\r\n");
            sb.append("**Test Name**" + "**Result**\r\n\r\n");
            sb.append(msg);
            os.write(sb.toString().getBytes("utf-8"));
            os.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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

        public mArrayAdapter(Context context, int textViewResourceId, ArrayList< String > list)
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
}
