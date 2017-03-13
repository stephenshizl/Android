package com.tools.customercit;

import java.util.ArrayList;
import com.tools.customercit.BaseMenu.MenuData;
import com.tools.customercit.R.string;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Layout;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.view.KeyEvent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;
import android.util.DisplayMetrics;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.os.SystemProperties;
//add by qianyan for mute all touch sound
import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
//end add by qianyan
public class HomeActivity extends Activity {

    private float allow_mock_location = 0;
    private int firstItem = 0;
    Intent serverIntent = new Intent();
    private int curCheckPosition = 0;
    private BaseMenu menuData = new BaseMenu(this);
    private ArrayList<MenuData> menuDatas = new ArrayList<MenuData>();
    private String SHARE_NAME = "testresult";
    private static final String MANUAL_RESULT_PATH = "/data/manualresult.txt";
    public static final String ENGLISH_LANGUAGE="0";
    String resultPrference = "";
    MenuAdapter menuAdapter;
    public static Boolean FLAG = true;
    private static float SOUND_EFFECTS_ENABLED = 0;
    public String TAG = "Shut/Open Touchsound";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteResult();
       
        SystemProperties.set("persist.sys.cit", ENGLISH_LANGUAGE);
        requestWindowFeature(Window.FEATURE_NO_TITLE );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Hide all screen decorations 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);
        
        //read menuconfig txt file
        BaseMenu baseMenu = new BaseMenu(this);
        menuDatas = baseMenu.initMenu(FLAG);
        
        //read  result from  sharepreference file        
        //readResultfromPreference();     
        
        //bind and adapte listview
        menuAdapter = new MenuAdapter(HomeActivity.this, menuDatas);
        ListView menuListView = (ListView) findViewById(R.id.listmenu);
        menuListView.setAdapter(menuAdapter);
        menuListView.setDivider(new ColorDrawable(Color.rgb(89, 88, 88)));
        menuListView.setDividerHeight(1);

        menuListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                curCheckPosition = index;
                jumptoActivity(curCheckPosition);
            }
        });

        //note the first menu of current screen
        menuListView.setOnScrollListener(new OnScrollListener() {            
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                // TODO Auto-generated method stub
            }           
            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int arg2, int arg3) {
                // TODO Auto-generated method stub
                firstItem = firstVisibleItem;               
            }
        });

        //open gps..
        OpenGps();
        serverIntent.setClassName(HomeActivity.this, "com.tools.customercit.GpsServer");
        startService(serverIntent);
        //mute all touch sound ,so that the audio loop can work..
        shutTouchsound();
    }
    
    private void shutTouchsound() {
        ContentResolver contentResolver = this.getContentResolver();
        try {
            Log.i(TAG, "sound effects = " + SOUND_EFFECTS_ENABLED);
            SOUND_EFFECTS_ENABLED = Settings.System.getFloat(contentResolver, Settings.System.SOUND_EFFECTS_ENABLED);
            Settings.System.putFloat(contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, 0);
        } catch (Exception snfe) {
            Log.e(TAG, Settings.System.SOUND_EFFECTS_ENABLED + " not found");
        }
    }
    private void recoveryTouchsound() {
        ContentResolver contentResolver = this.getContentResolver();
        try { 
            Log.i(TAG, "recovery sound effects = " + SOUND_EFFECTS_ENABLED);
            Settings.System.putFloat(contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, SOUND_EFFECTS_ENABLED);
        } catch (Exception snfe) {
            Log.e(TAG, Settings.System.SOUND_EFFECTS_ENABLED + " not found");
        }
    }  
     private void OpenGps() {
        ContentResolver contentResolver = this.getContentResolver();
        try {
            Settings.Secure.setLocationProviderEnabled(contentResolver, "gps", true);
        } catch (Exception snfe) {
            Log.e("HomeActivity", " not found");
        }
    }
    
    private void recoveryGps() {
        ContentResolver contentResolver = this.getContentResolver();
        try { 
            //Settings.System.putFloat(contentResolver, Settings.System.ALLOW_MOCK_LOCATION, allow_mock_location);
            Log.i("HomeActivity", "Recovery GPS state!");
            //Settings.Secure.putString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "");
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
        } catch (Exception snfe) {
            Log.e("HomeActivity", Settings.System.LOCATION_PROVIDERS_ALLOWED + " not found");
        }
    }

     void deleteResult() {
         Log.i("qianyan", "qianyan deleteResult");
         //File filePref = new File("/data/data/" + getPackageName().toString() + "/shared_prefs/", SHARE_NAME + ".xml"); 
         File fileNv = new File(MANUAL_RESULT_PATH);
         //if(filePref.exists()){
         //   filePref.delete();
         //}
         if(fileNv.exists()){
             fileNv.delete();
         } 
       
     }
    public void switchLanguage(Locale locale) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale; 
        resources.updateConfiguration(config, dm);
    }
    
    //read  result from  sharepreference file
    private void readResultfromPreference() {
        SharedPreferences sharePreferences = HomeActivity.this.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        Editor editor = sharePreferences.edit();
        SharedPreferences settings = getSharedPreferences(SHARE_NAME, 0);
        String resultString = settings.getString("Result", "-1");
        if(!resultString.equals("-1")) {
            String[] splitResult = resultString.split(" ");
            int j = 0;
            for(int i = 0; i< menuDatas.size(); i++) {
                if(!menuDatas.get(i).getActivityString().equals("")) {
                    menuDatas.get(i).setState(Integer.valueOf(splitResult[j++]));
                }
                else {
                    menuDatas.get(i).setState(Integer.valueOf(-1));
                }
            }
        } else {
            for(int i = 0; i< menuDatas.size(); i++) {
                menuDatas.get(i).setState(Integer.valueOf(-1));
            }
        }
    }
    
    protected void jumptoActivity(int index) {
        if(!menuDatas.get(curCheckPosition).getActivityString().equals("")) {
            Intent intent = new Intent();
            intent.setClassName(menuDatas.get(curCheckPosition).getPackageName(), 
                    menuDatas.get(curCheckPosition).getPackageName() + "." + menuDatas.get(curCheckPosition).getActivityString());
            intent.putExtra("index", index);
            startActivityForResult(intent, 0x00);
        }
    }
    
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 0x00) {
            //Intent intent =  getIntent();
            String result = intent.getStringExtra("result");
            ListView menuListView = (ListView) findViewById(R.id.listmenu);
            if(!result.equals("-1")) {
                menuDatas.get(curCheckPosition).setState(Integer.valueOf(result));
                // save test result to the sharepreference file
                writeResultTopreference();
                menuAdapter = new MenuAdapter(HomeActivity.this, menuDatas);
                menuListView.setAdapter(menuAdapter);
                menuListView.setSelection(firstItem);


            }
        }
    }

    // save test result to the sharepreference file
    private void writeResultTopreference() {
        resultPrference = "";
        for(int i = 0;i < menuDatas.size(); i++) {
            if(!menuDatas.get(i).getActivityString().equals("")) {
                resultPrference += String.valueOf(menuDatas.get(i).getState()) + " ";
            }
        }
        SharedPreferences sharePreferences = HomeActivity.this.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        Editor editor = sharePreferences.edit();
        editor.putString("Result", resultPrference.trim());
        editor.commit();
        saveResultToTxt(resultPrference);

    }
    
    @Override
    protected void onResume() {
        super.onResume();
        SystemProperties.set("persist.sys.citkey", "true");
        try {
            Process process1 = Runtime.getRuntime().exec("touch /data/citflag");
            Process process2 = Runtime.getRuntime().exec("chmod 777 /data/citflag");
        } catch (Exception e ) { 
            e.printStackTrace () ; 
        }
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");             
        registerReceiver(mReceiver, myIntentFilter);

    }
    
    //language change,adapter the menu
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                BaseMenu baseMenu = new BaseMenu(context);
                menuDatas = baseMenu.initMenu(FLAG);
                readResultfromPreference();
                menuAdapter = new MenuAdapter(HomeActivity.this, menuDatas);
                ListView menuListView = (ListView) findViewById(R.id.listmenu);
                menuListView.setAdapter(menuAdapter);
             }
        }
    };

    @Override
    protected void onDestroy() {
        Log.i("gps service end","GPSserviceend");
        recoveryGps();
        stopService(serverIntent);
        recoveryTouchsound();
        super.onDestroy();
    }

    public void saveResultToTxt(String result){
        result = result.replace(" ", "");
        result = result.replace("-1", "0");
        FileWriter fWriter = null;
        try {
            File simstatusResult = new File(MANUAL_RESULT_PATH);
            if(!simstatusResult.exists()){
                   simstatusResult.createNewFile();
            }
            fWriter = new FileWriter(MANUAL_RESULT_PATH);
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
    @Override
    protected void onPause() {
        super.onPause();
        SystemProperties.set("persist.sys.citkey", "false");
    }
}

