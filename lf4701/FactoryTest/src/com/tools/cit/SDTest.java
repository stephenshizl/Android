package com.tools.cit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SDTest extends TestModule {

    private int requestCode; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState, R.layout.sd_test, R.drawable.lcd);
        Intent mIntent=new Intent();
        ComponentName comp = new ComponentName("com.lf4701.sdmemorytest", "com.lf4701.sdmemorytest.SDMemoryTest"); 
        mIntent.setComponent(comp);   
        mIntent.setAction("android.intent.action.VIEW"); 
        requestCode = 0; 
        startActivityForResult(mIntent, requestCode);
    }
        @Override  
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
            //String memoryFlag = data.getStringExtra("memory"); 
        int memoryFlag= Integer.parseInt(data.getStringExtra("memory")); 
        if(requestCode==0){
            switch (memoryFlag){
                case 0:setTestResult(FAIL);
                       break;
                case 1:setTestResult(PASS);
                        break;
                default:break;
            }           
        }
     } 
    @Override
    public String getModuleName() {
        return "SD";
    }

}
