
package com.tools.customercit;

import java.lang.reflect.Method;
import java.security.spec.EncodedKeySpec;

import org.apache.http.util.EncodingUtils;

import com.tools.util.FTLog;
import com.tools.util.TestResultProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.os.SystemProperties;

public class PhoneInfo extends TestModule  implements android.view.View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.phone_info);
        super.onCreate(savedInstanceState, R.layout.phone_info, R.drawable.lcd);
        FTLog.d(this, "SFTT PhoneInfo onCreate");
       // ((Button) findViewById(R.id.btn_auto_test)).setOnClickListener(this);
        //((Button) findViewById(R.id.btn_manual_test)).setOnClickListener(this);

        ((TextView) findViewById(R.id.product_name)).setText(Build.DEVICE);
        //((TextView) findViewById(R.id.build_ver)).setText(Build.VERSION.INCREMENTAL);
        ((TextView) findViewById(R.id.build_ver)).setText(SystemProperties.get("ro.build.version.in"));
        ((TextView) findViewById(R.id.build_ver_out)).setText(Build.DISPLAY);
         findViewById(R.id.btn_pass).setEnabled(true);
       // String RadioVersion=Build.getRadioVersion();
        //((TextView) findViewById(R.id.baseband_ver)).setText((RadioVersion==null||RadioVersion.equals(""))?
          //      getResources().getString(R.string.unknow):RadioVersion);
        //((TextView) findViewById(R.id.hardware)).setText(Build.DEVICE);
        //String imei=((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        //((TextView) findViewById(R.id.imei)).setText((imei==null||imei.equals(""))?
          //      getResources().getString(R.string.unknow):imei);
    }

    @Override
    public void onClick(View v) {
/*
        if (v.getId() == R.id.btn_auto_test) {
            FTLog.d(this, "PhoneInfo button auto test has been clicked");
            Intent intent = new Intent();
            intent.setClassName(this.getPackageName(), "com.tools.customercit.Main");
            intent.putExtra(TestModule.SFTT_TEST_TYPE, TestModule.SFTT_AUTO_TEST);
            startActivityForResult(intent, FTApp.REQUEST_START_MAIN);
        }
        else if (v.getId() == R.id.btn_manual_test) {
            FTLog.d(this, "PhoneInfo button manual test has been clicked");
            Intent intent = new Intent();
            intent.setClassName(this.getPackageName(), "com.tools.customercit.Main");
            intent.putExtra(TestModule.SFTT_TEST_TYPE, TestModule.SFTT_MANUAL_TEST);
            startActivityForResult(intent, FTApp.REQUEST_START_MAIN);
        }
  */  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FTApp.REQUEST_START_MAIN) {
            FTLog.i(this, "Refresh result table.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_quit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.about_title)
                        .setMessage(R.string.about_body)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(true);
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
     @Override
    public String getModuleName() {        
        return "PhoneInfo";
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        FTLog.d(this, "SFTT configuration changed in PhoneInfo");
    }
}
