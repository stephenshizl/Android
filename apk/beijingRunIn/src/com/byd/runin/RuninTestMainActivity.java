
package com.byd.runin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.byd.runin.TestLog.Log;
import com.byd.runinjin.RuninTest;


public class RuninTestMainActivity extends Activity
{

    public static final String PVT = "PVT";
    public static final String SS = "SS";
    public static final String FINAL = "FINAL";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.runin_main);

        Button pvtButton = (Button)findViewById(R.id.pvt);
        pvtButton.setOnClickListener(mOnclickListener);

        Button ssButton = (Button)findViewById(R.id.ss);
        ssButton.setOnClickListener(mOnclickListener);

        Button finalButton = (Button)findViewById(R.id.finalTest);
        finalButton.setOnClickListener(mOnclickListener);
    }

    private OnClickListener mOnclickListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            // TODO Auto-generated method stub
            switch (v.getId())
            {
                case R.id.pvt:
                    onClickButton(PVT);
                    break;
                case R.id.ss:
                    onClickButton(SS);
                    break;
                case R.id.finalTest:
                    onClickButton(FINAL);
                    break;
            }
        }
    };

    private void onClickButton(String testMode)
    {
        Intent intent = new Intent(RuninTestMainActivity.this,MainActivity.class);
        intent.putExtra("testMode", testMode);
        startActivity(intent);
    }
}
