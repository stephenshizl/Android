package com.tools.customercit;

import com.tools.util.FTLog;
import com.tools.util.FTUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileWriter;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import android.os.SystemProperties;
/**
 * Base class for a test module
 * @author wangx
 *
 */
public abstract class TestModule extends Activity{
    private WakeLock wl;
    public static final int RESULT_CODE = 0X11;
    /**
     * @param savedInstanceState
     * @param layout the layout resource ID
     */
    public void onCreate(Bundle savedInstanceState, int layout) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        init();
        /*Begin add by liugang for W101HM 20130808*/       
        /*Message autoFinished = testResultHandler.obtainMessage(EVENT_SET_TEST_RESULT_AUTO);
        Bundle bundle = new Bundle();
        bundle.putString(SFTT_TEST_TYPE, sftt_type);
        autoFinished.setData(bundle);        
        testResultHandler.sendMessageDelayed(autoFinished,100000);*/
        /*End add*/
    }
    
    /**
     * @param savedInstanceState
     * @param layout the layout resource ID
     * @param resID the resource ID of the icon which appears in the left of the title bar
     */
    public void onCreate(Bundle savedInstanceState, int layout, int resID) {
        super.onCreate(savedInstanceState);
        Window win=getWindow();
        win.requestFeature(Window.FEATURE_LEFT_ICON);
        setContentView(layout);
        win.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, resID);
        init();
        /*Begin add by liugang for W101HM 20130808*/       
        /*Message autoFinishmsg = testResultHandler.obtainMessage(EVENT_SET_TEST_RESULT_AUTO);
        Bundle bundle = new Bundle();
        bundle.putString(SFTT_TEST_TYPE, sftt_type);
        autoFinishmsg.setData(bundle);
        testResultHandler.sendMessageDelayed(autoFinishmsg,60000);*/
        /*End add*/        
    }
    
    public static final int PASS = 1;
    public static final int FAIL = 0;
    public static final int OTHER = -1;
    
    public static final String SFTT_SAVED_RESULT_NAME = "tools.SFTT.extra.SAVED_RESULT";
    public static final String SFTT_TEST_TYPE="tools.SFTT.extra.TEST_TYPE";
    public static final String SFTT_AUTO_TEST="AUTO";
    public static final String SFTT_MANUAL_TEST="MANUAL";
    
    private boolean resultSaved = false;
    private String sftt_type;
    
    public static final String SFTT_SEND_RESULT_ACTION = "tools.SFTT.action.SEND";
    public static final String SFTT_EXTRA_NAME = "tools.SFTT.extra.NAME";
    public static final String SFTT_EXTRA_RESULT = "tools.SFTT.extra.RESULT";
    
    protected TextView statusBar=null;//statusBar may be null. Caution when using it.
    
    
    protected void init(){
   wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getModuleName());
        wl.acquire();
        if(findViewById(R.id.bottom_bar)!=null){
             OnClickListener bottomBarOnClickListener=new OnClickListener() {
                 @Override
                 public void onClick(View v) {    
                     buttomBarOnClick(v);
                 }
             };
             ((Button) findViewById(R.id.btn_pass)).setOnClickListener(bottomBarOnClickListener);
             ((Button) findViewById(R.id.btn_fail)).setOnClickListener(bottomBarOnClickListener);
         }
    }
    /**
     * Call this function in onCreate() to add onClickListener for bottom-bar-buttons
     */
    @Deprecated
    protected void addButtomBarOnClickListener(){
         if(findViewById(R.id.bottom_bar)!=null){
             OnClickListener bottomBarOnClickListener=new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     buttomBarOnClick(v);
                 }
             };
             //((Button) findViewById(R.id.btn_home)).setOnClickListener(bottomBarOnClickListener);
             ((Button) findViewById(R.id.btn_pass)).setOnClickListener(bottomBarOnClickListener);
             ((Button) findViewById(R.id.btn_fail)).setOnClickListener(bottomBarOnClickListener);
             //((Button) findViewById(R.id.btn_next)).setOnClickListener(bottomBarOnClickListener);
             
             statusBar=(TextView)findViewById(R.id.status_bar);
             
             
             statusBar.setAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_up_in));
         }
    }
    
    protected void setTitleBarIcon(int resID){
        Window win=getWindow();
        win.requestFeature(Window.FEATURE_LEFT_ICON);
        win.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, resID);  
    }
    
    /**
     * 
     * @return the name of current module(e.g. "LCD")
     */
    public abstract String getModuleName();
    
    /**
     * Call back function when buttons on bottom bar were clicked. 
     * You can also choose to override this method.
     * @param v View that is clicked
     */
    protected void buttomBarOnClick(View v) {
        if(v.getId() == R.id.btn_pass){
          //  FTLog.i(this, "Test Passed: "+this.getModuleName());
            //setTestResult(PASS);
            Intent intent = new Intent();
            intent.putExtra("result", "1");
            setResult(RESULT_CODE, intent);
            saveResultToDATA(1);
            super.finish();
        }else if(v.getId() == R.id.btn_fail){
            //FTLog.i(this, "Test Failed: "+this.getModuleName());
            //setTestResult(FAIL);
            //statusBar.setText(R.string.fail_saved);
            Intent intent = new Intent();
            intent.putExtra("result", "0");
            saveResultToDATA(0);
            setResult(RESULT_CODE, intent);
            super.finish();
        }
    }

    private void gotoNext(){
        if(!resultSaved)
            promptToSave(MSG_GOTO_NEXT);
        else{
            setResult(RESULT_GOTO_NEXT);
            super.finish();
        }
    }
    
    private void goHome(){
        popfinish();
    }
    
    @Override
    protected void onDestroy() {
        FTLog.d(this, "onDestroy()");
        if(wl.isHeld()) wl.release();
        super.onDestroy();
        
    }

    public void popfinish() {
        FTLog.d(this, "finish()");
        if(!resultSaved)
            promptToSave(MSG_FINISH);
        else
            super.finish();
    }

    /**
     * Set test result of current module.
     * Can be called in Thread.
     * @param result 0(FAIL):fail, 1(PASS):pass, x(OTHER):other
     * @return
     */
    public boolean setTestResult(int result){
        Intent intent = new Intent();
        intent.putExtra("result", String.valueOf(result));
        setResult(RESULT_CODE,intent); 
        if(result == 1){
            saveResultToDATA(1);}
        else saveResultToDATA(0);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TestModule.super.finish();
                }
         }, 800);
    
        //super.finish();
        return true;
    }
    
    /**
     * Set test result with message of current module.
     * Can be called in Thread.
     * @param result 0(FAIL):fail, 1(PASS):pass, x(OTHER):other
     * @param msg Message indicates pass/fail reason. Will be displayed in status bar.
     * Message can be a String or resource id (int).
     * Recommended to be as short as possible.
     * @return
     */
    public  <T> boolean  setTestResult(int result, T reason){
        resultSaved=true;
        Bundle bundle = new Bundle();
        bundle.putInt(TEST_RESULT_KEY, result);
        bundle.putString(SFTT_TEST_TYPE, sftt_type);
        String _reason = "";
        if(reason instanceof Integer){
            _reason = getResources().getString(((Integer)reason).intValue());
        }else if(reason instanceof String){
            _reason=(String)reason;
        }
        Message message= testResultHandler.obtainMessage(EVENT_SET_TEST_RESULT_WITH_MSG);
        bundle.putString(TEST_MSG_KEY, _reason);
        message.setData(bundle);
        message.sendToTarget();
        return true;
    }
    
    private final int EVENT_SET_TEST_RESULT = 0;
    private final int EVENT_SET_TEST_RESULT_WITH_MSG = 1;
    private final int EVENT_SET_TEST_RESULT_AUTO = 2;
    
    private final String TEST_RESULT_KEY="TEST_RESULT";
    private final String TEST_MSG_KEY="TEST_MSG";
    
    private Handler testResultHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if(msg.what == EVENT_SET_TEST_RESULT){
                int result = data.getInt(TEST_RESULT_KEY);
                setTestResultInHandler(result);
            }else if(msg.what == EVENT_SET_TEST_RESULT_WITH_MSG){
                int result = data.getInt(TEST_RESULT_KEY);
                String message = data.getString(TEST_MSG_KEY);
                setTestResultInHandler(result , message);
            }else if(msg.what == EVENT_SET_TEST_RESULT_AUTO){
                Intent intent = new Intent();
                intent.putExtra("result", String.valueOf(-1));
                setResult(RESULT_CODE, intent);
                popfinish();
                //setTestResult(0);
            }else {
                super.handleMessage(msg);
            }
            String type=data.getString(SFTT_TEST_TYPE);
            FTLog.i(this, "type: "+type);
    /*         if(type.equals(SFTT_AUTO_TEST))
                {
                    gotoNext();
                }*/
             /*else if(type.equals(SFTT_MANUAL_TEST))
                {
                    goHome();
                }*/
                 if((data.getInt(TEST_RESULT_KEY))==1){
                      gotoNext();
            }else{
                    goHome();
            }
        }
        
        private boolean setTestResultInHandler(int result){
            
            Intent intent= new Intent(SFTT_SEND_RESULT_ACTION);
            intent.putExtra(SFTT_EXTRA_NAME, getModuleName());
            intent.putExtra(SFTT_EXTRA_RESULT, result);
            sendBroadcast(intent);
            if(result==PASS)
                statusBar.setText(R.string.pass_saved);
            else
                statusBar.setText(R.string.fail_saved);
            /*statusBar.setAnimation(AnimationUtils.loadAnimation(TestModule.this,
                    R.anim.push_up_in));*/
            /*statusBar.startAnimation(AnimationUtils.loadAnimation(TestModule.this,
                    R.anim.push_up_in));*/
            return true;
        }
        
        private boolean  setTestResultInHandler(int result, String msg){
            setTestResultInHandler(result);
            statusBar.append(" " + msg);
            return true;
        }
    };
    
    
    
    /**
     * Prompt tester to save result before going to Main screen or next.
     * @param what
     */
    protected void promptToSave(int what){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_warning)
               .setIcon(R.drawable.alert_dialog_icon)
               .setTitle(R.string.warning)
               .setCancelable(true)
               .setPositiveButton("Yes", new PositiveButtonListener(what))
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        FTLog.d(this, "SFTT configuration changed in TestModule");
    }

    private class PositiveButtonListener implements DialogInterface.OnClickListener{
        int what;
        public PositiveButtonListener(int what){
            super();
            this.what = what;
        }
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(what == MSG_FINISH){
                TestModule.super.finish();
                dialog.cancel();
            }else if(what == MSG_GOTO_NEXT){
                TestModule.this.setResult(RESULT_GOTO_NEXT);
                TestModule.super.finish();
                dialog.cancel();
            }
        }
    }
    
    private static final int MSG_FINISH = 0, MSG_GOTO_NEXT = 1;
    
    public static final int RESULT_MODULE_BACK = 100;
    public static final int RESULT_GOTO_NEXT = 101;

    /*Begin add by liugang for W101HM 20130808*/
    @Override
    protected void onPause() {
        super.onPause();
        testResultHandler.removeMessages(EVENT_SET_TEST_RESULT);
        testResultHandler.removeMessages(EVENT_SET_TEST_RESULT_WITH_MSG);
        testResultHandler.removeMessages(EVENT_SET_TEST_RESULT_AUTO);
        SystemProperties.set("persist.sys.citkey", "false");
    }
    /*End add*/

      @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if(keyCode == KeyEvent.KEYCODE_BACK) { //BACKBUTTON
            //setTestResult(-1);
             Intent intent = new Intent();
             intent.putExtra("result", String.valueOf(-1));
             setResult(RESULT_CODE, intent);
             popfinish();
            }
            return false;
    }

    private static final String SIMSTATUS_RESULT_PATH = "/data/simappstatus.txt";

//add by liutao
public void saveResultToDATA(int result){
        FileWriter fWriter = null;
        try {
            File simstatusResult = new File(SIMSTATUS_RESULT_PATH);
        if(!simstatusResult.exists()){
                   simstatusResult.createNewFile();
            }
            fWriter = new FileWriter(SIMSTATUS_RESULT_PATH);
            fWriter.write(String.valueOf(result));
            //if(result == 0){
            //    fWriter = new FileWriter(simstatusResult);
            //    fWriter.write(String.valueOf(result));
            //    fWriter.close();
            //}
                    fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( fWriter != null){
                try{
                    fWriter.close();
                } catch (Exception e){
                }
            }
        }
    }
//end
    @Override
    protected void onResume(){
        super.onResume();
        SystemProperties.set("persist.sys.citkey", "true");
    }
}