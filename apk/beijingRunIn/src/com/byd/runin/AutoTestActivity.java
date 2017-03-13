
package com.byd.runin;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import com.byd.runin.TestLog.Log;

public class AutoTestActivity extends Activity
{
    private static final String TAG = "AutoTestActivity";

    private TextView text = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        text = new TextView(this);
        text.setTextSize(15);
        setContentView(R.layout.runin_test_result);
        text = (TextView)findViewById(R.id.test_result);

        text.setText("test .....");

        Intent intent = getIntent();

        ArrayList < TestListItem > tliList = intent.getParcelableArrayListExtra
            ("testItemList");

        Log.d(TAG, "onCreate : size = " + tliList.size());

        //AutoTest.getInstance().readyToTest(this, text);

        AutoTest.getInstance().readyToTest(this, tliList, text);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        text.invalidate();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("AutoTestActivity", "on destroy------test");
        AutoTest.getInstance().release();


    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder buidler = new AlertDialog.Builder(this);
        buidler.setTitle("Confirm to exit").setMessage(
            "Testing...\nDo you confirm to exit ?").setPositiveButton("Confirm",
            new OnClickListener()
        {

            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                Log.i("AutoTestActivity", "onBackPressed"); finish();
            }
        }
        ).setNegativeButton("Cancel", null).create().show();
    }

}
