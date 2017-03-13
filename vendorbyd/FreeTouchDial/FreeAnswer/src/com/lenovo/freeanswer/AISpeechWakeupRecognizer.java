package com.lenovo.freeanswer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.AIEngine;
import com.aispeech.AIEngineHelper;

public class AISpeechWakeupRecognizer extends Recognizer {

    private static final String TAG = "AISpeechWakeupRecognizer";

    private static final String PROVISION = "provision.file";
    private static final String ANSWER_NET_FILE_NAME =
            "answer.wakeup.net.bin"; // 解码网络文件(接听/挂断)
    private static final String ANSWER_RES_FILE_NAME =
            "answer.wakeup.res.bin"; // 资源文件(接听/挂断)

    private static final String SPEECH_WAKEUP_WORD_KEY = "wakeupWord";
    private static final String SPEECH_WAKEUP_VALUE_KEY = "wakeupValue";
    private static final String SPEECH_WAKEUP_RESULT_KEY = "result";
    private static final String SPEECH_WAKEUP_ERROR_ID = "errId";

    private long mRecognizer = 0;
    private String mDeviceId = null;
    private String mSerialNumber = null;
    private String mAppKey = "138156002600015b";
    private String mSecretKey = "6d1bd960816baaa364f3c756e4714f3a";

    private String mParam = "{" +
            // "\"vadEnable\": 0," +
            // PersistTool.getInt(PersistTool.RUNNING_MODE_KEY, 0) + "," +
            "\"volumeEnable\": 1, " +
            "\"coreProvideType\": \"native\", " +
            "\"app\": {\"userId\": \"user-id\"}, " +
            "\"audio\": {" +
            "\"audioType\": \"wav\", " +
            "\"channel\": 1, " +
            "\"sampleBytes\": 2, " +
            "\"sampleRate\": 16000, " +
            "\"compress\": \"raw\"" +
            "}, " +
            "\"request\": {\"coreType\": \"cn.wakeup\"}" +
            "}";
    
    public AISpeechWakeupRecognizer(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mResult.mEngineType = SpeechConstant.ENGINE_TYPE_LOCAL_WAKEUP;
        mResult.mResult = new String[1];
        mResult.mConfidence = new int[1];
    }
    
    private int mStatus = 0;
        private AIEngine.aiengine_callback mEngineCallback = new AIEngine.aiengine_callback() {
            @Override
            public int run(byte[] id, int type, byte[] data, int size) {
                if (type == AIEngine.AIENGINE_MESSAGE_TYPE_JSON) {
                    String result = new String(data, 0, size).trim();
                    try {
                        JSONObject json = new JSONObject(result);
                        if(json.has("vad_status") || json.has("volume")) {
                            int status = json.optInt("vad_status");
                            int volume = json.optInt("volume");
                            if(mStatus == 0 && status == 1) {
                                beginOfSpeech();
                            } else if(mStatus == 1 && status == 2) {
                                endOfSpeech();
                            }
                            mStatus = status;
                        }  else {
                            long end = System.currentTimeMillis();
                            Log.d(TAG, "Result : " + result);
                            String strResult = json.optString(SPEECH_WAKEUP_RESULT_KEY);
                            if(TextUtils.isEmpty(strResult)) {
                                int error = json.optInt(SPEECH_WAKEUP_ERROR_ID);
                                if (error == 41007) {
                                    onError(SpeechConstant.SPEECH_ENGINE_INIT_FAILED);
                                    return 0;
                                }
                                return 0;
                            }
                            JSONObject retJson = new JSONObject(strResult);
                            String word = retJson.optString(SPEECH_WAKEUP_WORD_KEY);
                            int wakeupValue = retJson.optInt(SPEECH_WAKEUP_VALUE_KEY);
                            if (wakeupValue != 0 && wakeupValue != 1) {
                                if (SpeechConstant.SPEECH_WAKEUP_END_CALL_WORD
                                        .equalsIgnoreCase(word)) {
                                    stopImp();
                                    Log.d(TAG, "Result ****** " + word);
                                    setResult(word);
                                    onSuccess();
                                } else if (SpeechConstant.SPEECH_WAKEUP_ACCEPT_CALL_WORD
                                        .equalsIgnoreCase(word)) {
                                    stopImp();
                                    Log.d(TAG, "Result ****** " + word);
                                    setResult(word);
                                    onSuccess();
                                } else {
                                    Log.d(TAG, "Result ****** REJECT (" + word + ")");
                                    // Junjie: Comment here
                                    // onError(SpeechConstant.SPEECH_ERROR_REJECT);
                                }
                            }
                        }
                        json = null;
                    } catch (JSONException e) {
    
                    }
                } else if(type == AIEngine.AIENGINE_MESSAGE_TYPE_BIN) {
                }
                return 0;
            }
        };
        

    @Override
    protected int createImp() {
        // TODO Auto-generated method stub
        /*
         * mWorkerThread.execute(new Runnable() {
         * @Override public void run() { initSpeechEngine(); } }); try {
         * Thread.sleep(6000); } catch (InterruptedException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */
        initSpeechEngine();
        if (mRecognizer == 0) {
            return SpeechConstant.SPEECH_ENGINE_INIT_FAILED;
        }
        return SpeechConstant.SPEECH_ENGINE_INIT_OK;
    }

    private boolean initSpeechEngine() {
        long cost = System.currentTimeMillis();
        if(mRecognizer == 0) {
            byte buf[] = new byte[1024];
            int size = AIEngine.aiengine_opt(mRecognizer, AIEngine.AIENGINE_OPT_GET_VERSION, buf,
                    1024);
            Log.d(TAG, "Version: " + new String(buf, 0, size).trim());
            size = AIEngine.aiengine_opt(mRecognizer, AIEngine.AIENGINE_OPT_GET_MODULES, buf, 1024);
            Log.d(TAG, "Modules: " + new String(buf, 0, size).trim());
            size = AIEngine.aiengine_get_device_id(buf, mContext);
            mDeviceId = new String(buf).trim();
            // AppLog.d(TAG, "DeviceId: " + new String(buf).trim());
            Log.d(TAG, "DeviceId: " + mDeviceId);

            String provisionPath = AIEngineHelper.extractResourceOnce(mContext, PROVISION, false);
            Log.d(TAG, "ProvisionPath: " + provisionPath);
            // AppLog.d(TAG, "VadResPath: " + vadResPath);

            // String wakeupResPath =
            // AIEngineHelper.extractResourceOnce(mContext,
            // WAKEUP_RES_FILE_NAME, false);
            // String wakeupBinPath =
            // AIEngineHelper.extractResourceOnce(mContext,
            // WAKEUP_NET_FILE_NAME, false);

            String wakeupBinPath = AIEngineHelper.extractResourceOnce(mContext,
                    ANSWER_NET_FILE_NAME, false);
            String wakeupResPath = AIEngineHelper.extractResourceOnce(mContext,
                    ANSWER_RES_FILE_NAME, false);
//            mSerialNumber = AIEngineHelper.registerDeviceOnce(mContext,
//                    mAppKey, mSecretKey, mDeviceId, mUserId);
            mSerialNumber = "";
            Log.d(TAG, "SerialNumber: " + mSerialNumber);

            // int tmp = wakeupBinPath.lastIndexOf("/");
            // if (tmp >= 0) {
            // wakeupBinPath = wakeupBinPath.substring(0, tmp + 1);
            // }

            String cfg = String.format("{\"appKey\": \"%s\", " +
                    "\"secretKey\": \"%s\", " +
                    "\"serialNumber\": \"%s\"," +
                    "\"provision\": \"%s\", " +
                    "\"native\": {" +
                    "\"cn.wakeup\": { " +
                    "\"netBinPath\": \"%s\"," +
                    "\"resBinPath\": \"%s\"," +
                    "\"wakeupRetMode\": 1," +
                    "\"useWavCache\": 1" +
                    "} " +
                    "}" +
                    "}",
                    // "\"vad\":{ \"res\": \"%s\"}}",
                    mAppKey, mSecretKey, mSerialNumber,
                    provisionPath,
                    wakeupBinPath,
                    wakeupResPath);

            Log.d(TAG, "AIEngine.aiengine_new begin " + cfg);
            mRecognizer = AIEngine.aiengine_new(cfg, mContext);
            Log.d(TAG, "Aiengine: " + mRecognizer + "," + Thread.currentThread().getId());
        }
        Log.d(TAG, "initSpeechEngine cost " + (System.currentTimeMillis() - cost));
        return true;
    }

    @Override
    public boolean addIntent(Intent intent) {
        // TODO Auto-generated method stub
        // Nothing
        return true;
    }

    @Override
    protected void recognizeImp(byte[] data, int size) {
        // TODO Auto-generated method stub
        try {
            if (mRecognizer == 0) {
                Log.e(TAG, "recognizeImp AIEngine invalid");
                return;
            }
            // if (mLogIndicator++ % 300 == 1) {AppLog.d(TAG,
            // "----aiengine_feed");}
            int ret = AIEngine.aiengine_feed(mRecognizer, data, size);
            if (ret != 0) {
                Log.d(TAG, "aiengine_feed " + ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void releaseImp() {
        // TODO Auto-generated method stub
        if (mRecognizer != 0) {
            int ret = AIEngine.aiengine_delete(mRecognizer);
            Log.d(TAG, "AIEngine.aiengine_delete " + ret);
            mRecognizer = 0;
        }
    }

    @Override
    protected void setResult(String str) {
        // TODO Auto-generated method stub
        mResult.mResult[0] = str;
    }

    @Override
    protected void stopImp() {
        // TODO Auto-generated method stub
        try {
            if (mRecognizer != 0) {
                long cost = System.currentTimeMillis();
                int ret = AIEngine.aiengine_stop(mRecognizer);
                cost = System.currentTimeMillis() - cost;
                Log.d(TAG, "aiengine_stop " + ret + "," + cost + "," + Thread.currentThread().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean startImp(boolean updateIntent) {
        // TODO Auto-generated method stub
        if (mRecognizer == 0) {
            Log.e(TAG, "startImp AIEngine invalid");
            return false;
        }
        
        mResult.mResult[0] = null;
        mResult.mVadStart = false;
        mResult.mVadEnd = false;

        try {
            byte[] id = new byte[64];
            long cost = System.currentTimeMillis();
            int ret = AIEngine.aiengine_start(mRecognizer, mParam, id, mEngineCallback);
            cost = System.currentTimeMillis() - cost;
            Log.d(TAG, "aiengine_start " + ret + "," + cost + ","
                    + Thread.currentThread().getId());
            return (ret == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
