
/*===========================================================================

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110919    shenzhiyong    add TP test in CIT

===========================================================================*/

package com.tools.customercit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.Window;
import android.view.View;

public class NcitTP extends Activity {
    
    private String TAG = "touchScreen";

    private boolean isback = true;
    private NcitTPview mview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("short","====xxxxxx====oncreate ");

        requestWindowFeature(Window.FEATURE_NO_TITLE);//delete title
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//delete title bar
        mview=new NcitTPview(this);
        mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setContentView(mview);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }
//Begin shenzhiyong 20111202 fix

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return true;
           switch (keyCode) {
           case KeyEvent.KEYCODE_BACK:
              finish();
          isback = false;
               break;
           default:
               return super.onKeyDown(keyCode, event);
           }
           return true;
    }

//End 20111202
    @Override
    protected void onDestroy() {
    if(isback){
        result();
    }
        super.onDestroy();
    }
    private void result(){

        Intent intent = new Intent();
        intent.setClass(this, NcitTP2.class);
        startActivity(intent);
        finish();
        Log.i(TAG,"====== touch screen step 1 ====== PASS");
    }
    
}

