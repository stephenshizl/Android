
package com.tools.customercit;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.util.Log;

public class TouchTest extends TestModule /* STEP 1 */implements OnTouchListener {
    public static final String TOUCH = "Touch";
    private Button btnPass;
    public static boolean isFinish = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.touch, R.drawable.touch);

        findViewById(R.id.txt_tap_to_start).setOnTouchListener(this);

        /*Begin add by liugang for W101HM 20130807*/
        btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(false); 
        /*End add*/
    }

    // STEP 3
    @Override
    public String getModuleName() {
        return TOUCH;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showTouchTest2();
            default:
                ;
        }
        return true;
    }

    public void onResume(){
        super.onResume();
        Log.i("liugang0","isFinish " + isFinish);
        if(isFinish){
            isFinish = false;
            //btnPass.setEnabled(true);
            setTestResult(PASS);
        }
    }

    private void showTouchTest2() {
        Intent intent = new Intent();
        //intent.setClassName("com.tools.customercit", "com.tools.customercit.TouchTest2");
        intent.setClassName("com.tools.customercit", "com.tools.customercit.NcitTP");
        /*Begin add by liugang for W101HM 20130807*/
        startActivityForResult(intent,100);
        /*End add*/
    }

    /*Begin add by liugang for W101HM 20130807*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
            case RESULT_OK:
                boolean isFinished = (Boolean)data.getExtra("result");
                if(isFinished)
                    btnPass.setEnabled(true);
                    setTestResult(PASS);
                break;
            default:
                break;
        }
                    
    }   
    /*End add*/
}
