

/*===========================================================================

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110919    shenzhiyong    add TP test in CIT

===========================================================================*/

package com.tools.cit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.Window;
import android.view.View;

public class NcitTP2 extends Activity {
    
    private String TAG = "touchScreen";

    private boolean isback = true;
    NcitTP2view mview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        requestWindowFeature(Window.FEATURE_NO_TITLE);//delete title
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//delete title bar
       mview=new NcitTP2view(this);
    mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(mview);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
     TouchTest.isFinish = true;  
    }
// Begin shenzhiyong 20111202 fix

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return true;
           switch (keyCode) {
           case KeyEvent.KEYCODE_BACK:
               finish();
           isback = false;
           TouchTest.isFinish = false;  
               break;
           default:
               return super.onKeyDown(keyCode, event);
           }
           return true;
    }

//End 20111202
    @Override
    protected void onDestroy() {
    if(isback) {
        result();
    }
        super.onDestroy();
    }
    private void result(){
     //Begin shenzhiyong 20111202 fix
     //openActivity(TEST_TP+1);
     //openWaitActivity(TEST_TP);
     //End 20111202
      
        Log.i("liugang0","TouchTest.isFinish " + TouchTest.isFinish);
        finish();
        Log.d(TAG,"====== touch screen step 2 ====== PASS");
        Log.d(TAG,"====== touch screen final  ====== PASS");
    }

    //protected void onPause(){
    //    super.onPause();
    //    TouchTest.isFinish = true;
    //}
}

