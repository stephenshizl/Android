package com.tools.cit;

import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import java.util.Locale;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import java.util.HashMap;
import android.util.Log;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import java.util.Random;
import android.media.AudioManager;

public class MyTTS extends Activity implements OnInitListener, OnUtteranceCompletedListener,OnCheckedChangeListener{
    //private static final int STREAM_TTS = AudioManager.STREAM_MUSIC; 
    private TextToSpeech mTts; 
    private static final int REQ_CHECK_TTS_DATA = 110; 
    private boolean isSetting = false; 
    private boolean isRateChanged = false;
    private boolean isStopped = true;
    private float mSpeechRate = 1.0f;
    private int result = -1;
    private RadioButton radio0,radio1,radio2,radio3,radio4,radio5,radio6,radio7,radio8,radio9,fail;
    private String text;
    private int maxVolume;
    private int volume;
    private AudioManager audioManager;

    @Override 
     public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.mytts);
        mTts = new TextToSpeech(this, this);
        /*
        radio0 = (RadioButton)findViewById(R.id.zero);
        */
        radio1 = (RadioButton)findViewById(R.id.one);
        radio2 = (RadioButton)findViewById(R.id.two);
        radio3 = (RadioButton)findViewById(R.id.three);
        radio4 = (RadioButton)findViewById(R.id.four);
        radio5 = (RadioButton)findViewById(R.id.five);
        /*
        radio6 = (RadioButton)findViewById(R.id.six);
        radio7 = (RadioButton)findViewById(R.id.seven);
        radio8 = (RadioButton)findViewById(R.id.eight); 
        radio9 = (RadioButton)findViewById(R.id.nine); 
        */
        fail   = (RadioButton)findViewById(R.id.mic_fail); 
        /*
        radio0.setOnCheckedChangeListener(this);
        */
        radio1.setOnCheckedChangeListener(this);
        radio2.setOnCheckedChangeListener(this);
        radio3.setOnCheckedChangeListener(this);
        radio4.setOnCheckedChangeListener(this);
        radio5.setOnCheckedChangeListener(this);
        /*
        radio6.setOnCheckedChangeListener(this);
        radio7.setOnCheckedChangeListener(this);
        radio8.setOnCheckedChangeListener(this);
        radio9.setOnCheckedChangeListener(this);
        */
        fail.setOnCheckedChangeListener(this);    
        text = String.valueOf(new Random().nextInt(5) + 1);
        
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ;
        volume =  audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;
        
     }
    
    @Override 
    public void onInit(int status) { 
        if (status == TextToSpeech.SUCCESS) { 
            mTts.setSpeechRate(mSpeechRate);
            mTts.setOnUtteranceCompletedListener(this); 

        } 
    }

    @Override
    public void onUtteranceCompleted(String utteranceId){
         mTts.stop();
         mTts.shutdown();
    }

     private boolean checkTtsData() { 
         try { 
             Intent checkIntent = new Intent(); 
             checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
             Locale currentLocale = mTts.getLanguage();
             if (currentLocale != null) {
                 checkIntent.putExtra("language", currentLocale.getLanguage());
                 checkIntent.putExtra("country", currentLocale.getCountry());
                 checkIntent.putExtra("variant", currentLocale.getVariant());   
             }
             checkIntent.setPackage(mTts.getCurrentEngine());
             
             startActivityForResult(checkIntent, REQ_CHECK_TTS_DATA); 
             return true; 
         } catch (ActivityNotFoundException e) { 
             return false; 
         } 
     } 

     /** onActivityResult */ 
     @Override 
     protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
         if (requestCode == REQ_CHECK_TTS_DATA) { 
             switch (resultCode) { 
             case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                 if(isStopped)
                    
                    ttsPlay();
                 else {
                     mTts.stop();
                     mTts.shutdown();                    
                 }
                 break; 
             case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:  
             case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA: 
             case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
                
                 break; 
             case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
             default: 
                break; 
             } 
         } 
         
         super.onActivityResult(requestCode, resultCode, data); 
    }  



     private int ttsPlay() { 
         if (null != mTts) { 
             isStopped = false;
             Log.i("MyTTS","I'm playing");
             HashMap<String,String> p = new HashMap<String,String>();
             p.put(TextToSpeech.Engine.KEY_PARAM_VOLUME,"1");
             mTts.setLanguage(Locale.ENGLISH);
             mTts.speak(text, TextToSpeech.QUEUE_FLUSH, p); 
             return TextToSpeech.SUCCESS;
         } 
         return TextToSpeech.ERROR; 
     }      

     @Override 
     protected void onResume() { 
         audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);        
         checkTtsData();
         super.onResume();         
     }
     
     @Override 
     protected void onPause() { 
         audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0); 
         super.onPause();
     }
     

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
         Intent resultIntent = new Intent();
         switch(buttonView.getId()){
            /*
            case R.id.zero:
                result = 0;
                break;
                */
            case R.id.one:
                result = 1;
                break;                
            case R.id.two:
                result = 2;
                break;                
            case R.id.three:
                result = 3;
                break;                
            case R.id.four:
                result = 4;
                break;                
            case R.id.five:
                result = 5;
                break;  
                /*
            case R.id.six:
                result = 6;
                break;                
            case R.id.seven:
                result = 7;
                break;                
            case R.id.eight:
                result = 8;
                break;                
            case R.id.nine:
                result = 9;
                break;             
                */
            case R.id.mic_fail:
                result = -1;
                break;                
                default:
                    break;
         }
         Log.i("MyTTS","return");
         resultIntent. putExtra("result", result);
         resultIntent. putExtra("speak", text);
         setResult(Activity.RESULT_OK,resultIntent);
         finish();
    }
    
}
