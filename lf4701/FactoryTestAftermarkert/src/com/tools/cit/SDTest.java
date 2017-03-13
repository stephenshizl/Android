package com.tools.customercit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SDTest extends TestModule  implements android.view.View.OnClickListener {

    Button btn;
    private int requestCode; 
    private String PassFlag,FailFlag;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState, R.layout.sd_test, R.drawable.lcd);
        btn=(Button) this.findViewById(R.id.btn_SD);
        
        
        btn.setOnClickListener(this);
        
        ((Button) findViewById(R.id.btn_pass)).setEnabled(false);
    }

        @Override  
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
            //String memoryFlag = data.getStringExtra("memory"); 
            int memoryFlag= Integer.parseInt(data.getStringExtra("memory")); 
            if(requestCode==0){
                switch (memoryFlag){
                case 0:btn.setText("press Fail button");
                       setTestResult(FAIL);
                       break;
                case 1:btn.setText("press Pass button");
                       setTestResult(PASS);
                       ((Button) findViewById(R.id.btn_pass)).setEnabled(true);
                        break;
                default:break;

                }           
            }
     } 
    @Override
    public String getModuleName() {
        return "SD";
    }
        @Override
    public void onClick(View v) {

        switch (v.getId()) {

          case R.id.btn_SD:
            Intent mIntent=new Intent();
            ComponentName comp = new ComponentName("com.lf4701.sdmemorytest", "com.lf4701.sdmemorytest.SDMemoryTest"); 
            
            mIntent.setComponent(comp);   
            mIntent.setAction("android.intent.action.VIEW"); 
            requestCode = 0; 
            //startActivity(mIntent);
            startActivityForResult(mIntent, requestCode);  
                }
            }

}
