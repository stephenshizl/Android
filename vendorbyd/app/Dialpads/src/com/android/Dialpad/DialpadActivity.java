package com.android.Dialpad;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.media.ToneGenerator;
import android.view.KeyEvent;
import android.media.AudioManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.text.Editable;
import android.telephony.PhoneNumberUtils;
import android.text.TextWatcher;
import android.content.Intent;
import android.content.ComponentName;
import com.android.internal.telephony.TelephonyIntents; 
import android.content.Intent;
import android.text.method.DialerKeyListener;
import android.net.Uri;
import android.provider.Settings;
import android.os.Build;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.android.internal.telephony.ITelephony;
import android.os.SystemProperties;
/*Start add by cui.yuehua@byd.com for lenovo spce*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.FileInputStream;
/*End add by cui.yuehua@byd.com for lenovo spce*/



public class DialpadActivity extends Activity implements View.OnClickListener, TextWatcher, View.OnLongClickListener{
    private static final String TAG = "DialpadActivity";
    private View mDigitsContainer;
    private EditText mDigits;
    private View mDelete;
    
    private boolean mWasEmptyBeforeTextChange;
    private boolean mDigitsFilledByIntent = false;

    private boolean mDTMFToneEnabled;
    
    private static String PACKAGE_NAME = "com.byd.tools";
    private static String CLASS_NAME = "com.byd.tools.Tools";
    
    private ToneGenerator mToneGenerator;
    private Object mToneGeneratorLock = new Object();
    
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int TONE_RELATIVE_VOLUME = 80;
    private static final int TONE_LENGTH_MS = 150;

    private static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
    private static final String MMI_IMEI_DISPLAY = "*#06#";
    private static final String MMI_REGULATORY_INFO_DISPLAY = "*#07#";
    //add by nhl for sn
    private static final String MMI_SN_DISPLAY = "####2222#";
    /*Start add by cui.yuehua@byd.com for lenovo spce*/
    private static final String MMI_KERNEL_DISPLAY = "####8375#";
    private static final String DISPLAY_TP_LCD_INFORMATION = "####7599#";
    /*End add by cui.yuehua@byd.com for lenovo spce*/
    //add by huayanchun for ops control
    private static final String STRICT_OP_ENABLE = "####78748#";
    //end
    //add by nhl
    private static String RECODE_PATH = "/countrycode/cfg/countryflag";
    private static String RECODE_FLAG = "true";
    //add end
    private static QueryHandler sPreviousAdnQueryHandler;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialpad_activity);
        
        mDigitsContainer = findViewById(R.id.digits_container);
        mDigits = (EditText) findViewById(R.id.digits);
        mDigits.setOnClickListener(this);
        mDigits.setKeyListener(DialerKeyListener.getInstance());
        mDigits.addTextChangedListener(this);
        
        mDelete = findViewById(R.id.deleteButton);
        
        if (mDelete != null) {
            mDelete.setOnClickListener(this);
            mDelete.setOnLongClickListener(this);
        }

        View oneButton = findViewById(R.id.one);
        if (oneButton != null) {
            setupKeypad();
        }        
    }

    public void onResume() {
        super.onResume();
        mDTMFToneEnabled = Settings.System.getInt(getContentResolver(),
                Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
        // if the mToneGenerator creation fails, just continue without it.  It is
        // a local audio signal, and is not as important as the dtmf tone itself.
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    // we want the user to be able to control the volume of the dial tones
                    // outside of a call, so we use the stream type that is also mapped to the
                    // volume control keys for this activity
                    mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME);
                    setVolumeControlStream(DIAL_TONE_STREAM_TYPE);
                } catch (RuntimeException e) {
                    Log.w("Dialpad", "Exception caught while creating local tone generator: " + e);
                    mToneGenerator = null;
                }
            }
        }
    }

    public void onPause() {
        super.onPause();

        // Make sure we don't leave this activity with a tone still playing.
        stopTone();
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator != null) {
                mToneGenerator.release();
                mToneGenerator = null;
            }
        }
    }
    
    private void setupKeypad() {
        // Setup the listeners for the buttons
        View view = findViewById(R.id.one);
        view.setOnClickListener(this);

        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        
        findViewById(R.id.star).setOnClickListener(this);

        view = findViewById(R.id.zero);
        view.setOnClickListener(this);

        findViewById(R.id.pound).setOnClickListener(this);
    }
    
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one: {
                playTone(ToneGenerator.TONE_DTMF_1);
                keyPressed(KeyEvent.KEYCODE_1);
                return;
            }
            case R.id.two: {
                playTone(ToneGenerator.TONE_DTMF_2);
                keyPressed(KeyEvent.KEYCODE_2);
                return;
            }
            case R.id.three: {
                playTone(ToneGenerator.TONE_DTMF_3);
                keyPressed(KeyEvent.KEYCODE_3);
                return;
            }
            case R.id.four: {
                playTone(ToneGenerator.TONE_DTMF_4);
                keyPressed(KeyEvent.KEYCODE_4);
                return;
            }
            case R.id.five: {
                playTone(ToneGenerator.TONE_DTMF_5);
                keyPressed(KeyEvent.KEYCODE_5);
                return;
            }
            case R.id.six: {
                playTone(ToneGenerator.TONE_DTMF_6);
                keyPressed(KeyEvent.KEYCODE_6);
                return;
            }
            case R.id.seven: {
                playTone(ToneGenerator.TONE_DTMF_7);
                keyPressed(KeyEvent.KEYCODE_7);
                return;
            }
            case R.id.eight: {
                playTone(ToneGenerator.TONE_DTMF_8);
                keyPressed(KeyEvent.KEYCODE_8);
                return;
            }
            case R.id.nine: {
                playTone(ToneGenerator.TONE_DTMF_9);
                keyPressed(KeyEvent.KEYCODE_9);
                return;
            }
            case R.id.zero: {
                playTone(ToneGenerator.TONE_DTMF_0);
                keyPressed(KeyEvent.KEYCODE_0);
                return;
            }
            case R.id.pound: {
                playTone(ToneGenerator.TONE_DTMF_P);
                keyPressed(KeyEvent.KEYCODE_POUND);
                return;
            }
            case R.id.star: {
                playTone(ToneGenerator.TONE_DTMF_S);
                keyPressed(KeyEvent.KEYCODE_STAR);
                return;
            }
            case R.id.deleteButton: {
                keyPressed(KeyEvent.KEYCODE_DEL);
                return;
            }
            case R.id.digits: {
                if (!isDigitsEmpty()) {
                    mDigits.setCursorVisible(true);
                }
                return;
            }
        }
    }

    public boolean onLongClick(View view) {
        final Editable digits = mDigits.getText();
        final int id = view.getId();
        switch(id) {
            case R.id.deleteButton: {
                digits.clear();
                mDelete.setPressed(false);
                return true;
            }
        }
        return false;
    }
    
    private void keyPressed(int keyCode) {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);

        // If the cursor is at the end of the text we hide it.
        final int length = mDigits.length();
        if (length == mDigits.getSelectionStart() && length == mDigits.getSelectionEnd()) {
            mDigits.setCursorVisible(false);
        }
    }
    
    void playTone(int tone) {
        //if local tone playback is disabled, just return
        if(!mDTMFToneEnabled) {
            return;
        }
        // Also do nothing if the phone is in silent mode.
        // We need to re-check the ringer mode for *every* playTone()
        // call, rather than keeping a local flag that's updated in
        // onResume(), since it's possible to toggle silent mode without
        // leaving the current activity (via the ENDCALL-longpress menu.)
        AudioManager audioManager =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
            || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w("DialpadActivity", "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }

            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, TONE_LENGTH_MS);
        }
    }

    private void stopTone() {
        // if local tone playback is disabled, just return.
        if (!mDTMFToneEnabled) {
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w("DialpadActivity", "stopTone: mToneGenerator == null");
                return;
            }
            mToneGenerator.stopTone();
        }
    }
    
    private boolean isDigitsEmpty() {
        return mDigits.length() == 0;
    }
    
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mWasEmptyBeforeTextChange = TextUtils.isEmpty(s);
    }

    public void onTextChanged(CharSequence input, int start, int before, int changeCount) {
        // DTMF Tones do not need to be played here any longer -
        // the DTMF dialer handles that functionality now.
    }

    public void afterTextChanged(Editable input) {
        // When DTMF dialpad buttons are being pressed, we delay SpecialCharSequencMgr sequence,
        // since some of SpecialCharSequenceMgr's behavior is too abrupt for the "touch-down"
        // behavior.
        if (!mDigitsFilledByIntent &&
            handleChars(input.toString(), mDigits)) {
            // A special sequence was entered, clear the digits
            mDigits.getText().clear();
        }

        if (isDigitsEmpty()) {
            mDigitsFilledByIntent = false;
            mDigits.setCursorVisible(false);
        }
    }
    
    private boolean handleChars(String input, EditText textField) {
        //get rid of the separators so that the string gets parsed correctly
        String dialString = PhoneNumberUtils.stripSeparators(input);
        //add by nhl 130909 add handleInternalVersionCode P801_P001150
        if (handleIMEIDisplay(this, dialString, false)
                || handleRegulatoryInfoDisplay(this, dialString)
                || handlePinEntry(this, dialString)
                || handleAdnEntry(this, dialString, textField)
                || handleRuninDisplay(this, dialString)
                || handleCITCode(this, dialString)
                || handleLogCode(this, dialString)
                || handleSpkReceiverPlay(this, dialString)
                || handlePVTest(this, dialString)
                || handleSecretCode(this, dialString)
                //add by nhl for handleCountryCode
                || handleCountryCode(this, dialString)
                || handleCountryCodeInfo(this, dialString)
                || handleSnInfoDisplay(this, dialString)
                || handleInternalVersionCode(this, dialString)
                //add by wangbo
                || handleToolsCode(this, dialString)
                /*Start add by cui.yuehua@byd.com for lenovo spce*/
                ||handleFactoryDatareset(this,dialString)
                ||handleDebugMode(this,dialString)
                || handleTPAndLCDDisplay(this, dialString)
                /*End add by cui.yuehua@byd.com for lenovo spce*/
                //add by huayanchun for ops control
                || handleStrictOpEnable(this, dialString)
                //end
                || handleOutVersionCode(this, dialString))  {
            return true;
        }
        return false;
    }

     //Begin nhl@byd.com add for display sn/pn
    private static boolean handleSnInfoDisplay(Context context, String input){
        if ( input.equals(MMI_SN_DISPLAY) ){
            showSNInformationDisplay(context);
            return true;
        }
        return false;
    }
    //End

    //add by huayanchun for ops control
    private static boolean handleStrictOpEnable(Context context, String input){
        final Context mContext = context;
        String currentOpsState = SystemProperties.get("persist.sys.strict_op_enable");
        String Message = context.getResources().getString(R.string.strict_ops_current_state)+currentOpsState+"\n"
                                    +context.getResources().getString(R.string.strict_ops_message);

        if ( input.equals(STRICT_OP_ENABLE) ){
                AlertDialog alert = new AlertDialog.Builder(context)
                        .setTitle(R.string.strict_ops_title)
                        .setMessage(Message)
                        .setPositiveButton(R.string.strict_ops_enable, new DialogInterface.OnClickListener() {
                               @Override
                                public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.STRICT_OP_ENABLE");
                                intent.putExtra("strict_op_enable", "true");
                                mContext.sendBroadcast(intent);
                                }
                        })
                        .setNegativeButton(R.string.strict_ops_disable, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.STRICT_OP_ENABLE");
                                intent.putExtra("strict_op_enable", "false");
                                mContext.sendBroadcast(intent);
                                }
                        })
                        .show();
                return true;
        }
        return false;
    }
    //End

    /**
     * Cleanup everything around this class. Must be run inside the main thread.
     *
     * This should be called when the screen becomes background.
     */
    public static void cleanup() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Log.wtf(TAG, "cleanup() is called outside the main thread");
            return;
        }

        if (sPreviousAdnQueryHandler != null) {
            sPreviousAdnQueryHandler.cancel();
            sPreviousAdnQueryHandler = null;
        }
    }

    /**
     * Handles secret codes to launch arbitrary activities in the form of *#*#<code>#*#*.
     * If a secret code is encountered an Intent is started with the android_secret_code://<code>
     * URI.
     *
     * @param context the context to use
     * @param input the text to check for a secret code in
     * @return true if a secret code was encountered
     */
    static boolean handleSecretCode(Context context, String input) {
        // Secret codes are in the form *#*#<code>#*#*
        int len = input.length();
        if (len > 8 && input.startsWith("*#*#") && input.endsWith("#*#*")) {
            final Intent intent = new Intent(SECRET_CODE_ACTION,
                    Uri.parse("android_secret_code://" + input.substring(4, len - 4)));
            context.sendBroadcast(intent);
            return true;
        }

        return false;
    }

    /**
     * Handle ADN requests by filling in the SIM contact number into the requested
     * EditText.
     *
     * This code works alongside the Asynchronous query handler {@link QueryHandler}
     * and query cancel handler implemented in {@link SimContactQueryCookie}.
     */
    static boolean handleAdnEntry(Context context, String input, EditText textField) {
        /* ADN entries are of the form "N(N)(N)#" */

        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null
                || telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_GSM) {
            return false;
        }

        // if the phone is keyguard-restricted, then just ignore this
        // input.  We want to make sure that sim card contacts are NOT
        // exposed unless the phone is unlocked, and this code can be
        // accessed from the emergency dialer.
        KeyguardManager keyguardManager =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            return false;
        }

        int len = input.length();
        if ((len > 1) && (len < 5) && (input.endsWith("#"))) {
            try {
                // get the ordinal number of the sim contact
                int index = Integer.parseInt(input.substring(0, len-1));

                // The original code that navigated to a SIM Contacts list view did not
                // highlight the requested contact correctly, a requirement for PTCRB
                // certification.  This behaviour is consistent with the UI paradigm
                // for touch-enabled lists, so it does not make sense to try to work
                // around it.  Instead we fill in the the requested phone number into
                // the dialer text field.

                // create the async query handler
                QueryHandler handler = new QueryHandler (context.getContentResolver());

                // create the cookie object
                SimContactQueryCookie sc = new SimContactQueryCookie(index - 1, handler,
                        ADN_QUERY_TOKEN);

                // setup the cookie fields
                sc.contactNum = index - 1;
                sc.setTextField(textField);

                // create the progress dialog
                sc.progressDialog = new ProgressDialog(context);
                sc.progressDialog.setTitle(R.string.simContacts_title);
                sc.progressDialog.setMessage(context.getText(R.string.simContacts_emptyLoading));
                sc.progressDialog.setIndeterminate(true);
                sc.progressDialog.setCancelable(true);
                sc.progressDialog.setOnCancelListener(sc);
                sc.progressDialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

                // display the progress dialog
                sc.progressDialog.show();

                // run the query.
                long subId = SubscriptionManager.getDefaultVoiceSubId();
                Uri uri = Uri.parse("content://icc/adn/subId/" + subId);
                handler.startQuery(ADN_QUERY_TOKEN, sc, uri,
                        new String[]{ADN_PHONE_NUMBER_COLUMN_NAME}, null, null, null);

                if (sPreviousAdnQueryHandler != null) {
                    // It is harmless to call cancel() even after the handler's gone.
                    sPreviousAdnQueryHandler.cancel();
                }
                sPreviousAdnQueryHandler = handler;
                return true;
            } catch (NumberFormatException ex) {
                // Ignore
            }
        }
        return false;
    }

    static boolean handlePinEntry(Context context, String input) {
        if ((input.startsWith("**04") || input.startsWith("**05")) && input.endsWith("#")) {
            long subId = SubscriptionManager.getDefaultVoiceSubId();
            try {
                return ITelephony.Stub.asInterface(ServiceManager.getService(
                        Context.TELEPHONY_SERVICE)).handlePinMmiForSubscriber(subId, input);
            } catch(RemoteException ex) {
                Log.e(TAG, "Remote Exception "+ex);
                return false;
            }
        }
        return false;
    }

    static boolean handleIMEIDisplay(Context context, String input, boolean useSystemWindow) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null && input.equals(MMI_IMEI_DISPLAY)) {
            int phoneType;
            long subId = SubscriptionManager.getDefaultVoiceSubId();
            phoneType = telephonyManager.getCurrentPhoneType(subId);
            if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                showIMEIPanel(context, useSystemWindow, telephonyManager);
                return true;
            } else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                showMEIDPanel(context, useSystemWindow, telephonyManager);
                return true;
            }
        }
        /*Start add by cui.yuehua@byd.com for lenovo spce*/
        if (telephonyManager != null && input.equals(MMI_KERNEL_DISPLAY)) {
                showKernelVersionPanel(context, false, telephonyManager);
                return true;
        }
        /*End add by cui.yuehua@byd.com for lenovo spce*/


        return false;
    }

    // TODO: Combine showIMEIPanel() and showMEIDPanel() into a single
    // generic "showDeviceIdPanel()" method, like in the apps/Phone
    // version of SpecialCharSequenceMgr.java.  (This will require moving
    // the phone app's TelephonyCapabilities.getDeviceIdLabel() method
    // into the telephony framework, though.)

    private static void showIMEIPanel(Context context, boolean useSystemWindow,
            TelephonyManager telephonyManager) {
        String imeiStr = null;
        long subId = SubscriptionManager.getDefaultVoiceSubId();
        int slotId = SubscriptionManager.getSlotId(subId);
        imeiStr = telephonyManager.getDeviceId(slotId);
        // Added by sunhongzhe to support dsds.
        if(TelephonyManager.getDefault().isMultiSimEnabled()) {
            String imei = telephonyManager.getDeviceId(0);
            String imei2 = telephonyManager.getDeviceId(1);
            imeiStr = "SIM1: " + (imei == null ? "" : imei) + "\n"
                    + "SIM2: " + (imei2 == null ? "" : imei2);
        }
        // End

        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.imei)
                .setMessage(imeiStr)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .show();
    }

    private static void showMEIDPanel(Context context, boolean useSystemWindow,
            TelephonyManager telephonyManager) {
        String meidStr = null;
        long subId = SubscriptionManager.getDefaultVoiceSubId();
        int slotId = SubscriptionManager.getSlotId(subId);
        meidStr = telephonyManager.getDeviceId(slotId);
        // Added by sunhongzhe to support dsds.
        if(TelephonyManager.getDefault().isMultiSimEnabled()) {
            String imei = telephonyManager.getDeviceId(0);
            String imei2 = telephonyManager.getDeviceId(1);
            meidStr = "SIM1: " + (imei == null ? "" : imei) + "\n"
                    + "SIM2: " + (imei2 == null ? "" : imei2);
        }
        // End

        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.meid)
                .setMessage(meidStr)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .show();
    }
    //add by nhl for sn
    private static String readSN(){
        String strSN = "SN: ";
        String msn = SystemProperties.get("ro.lenovosn2");
        strSN += msn;
        return strSN;
    }
    private static String readPN(){
        String strPN = "PN: ";
        String mpn = SystemProperties.get("ro.product.pn");
        strPN += mpn;
        return strPN;
    }

    //add by nhl for sn
    private static void showSNInformationDisplay(Context context) {

        String sn = readSN();
        sn += "\n" + readPN();
        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle("SN")
                .setMessage(sn)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .show();
    }
    //End

    /*******
     * This code is used to handle SIM Contact queries
     *******/
    private static final String ADN_PHONE_NUMBER_COLUMN_NAME = "number";
    private static final String ADN_NAME_COLUMN_NAME = "name";
    private static final int ADN_QUERY_TOKEN = -1;

    /**
     * Cookie object that contains everything we need to communicate to the
     * handler's onQuery Complete, as well as what we need in order to cancel
     * the query (if requested).
     *
     * Note, access to the textField field is going to be synchronized, because
     * the user can request a cancel at any time through the UI.
     */
    private static class SimContactQueryCookie implements DialogInterface.OnCancelListener{
        public ProgressDialog progressDialog;
        public int contactNum;

        // Used to identify the query request.
        private int mToken;
        private QueryHandler mHandler;

        // The text field we're going to update
        private EditText textField;

        public SimContactQueryCookie(int number, QueryHandler handler, int token) {
            contactNum = number;
            mHandler = handler;
            mToken = token;
        }

        /**
         * Synchronized getter for the EditText.
         */
        public synchronized EditText getTextField() {
            return textField;
        }

        /**
         * Synchronized setter for the EditText.
         */
        public synchronized void setTextField(EditText text) {
            textField = text;
        }

        /**
         * Cancel the ADN query by stopping the operation and signaling
         * the cookie that a cancel request is made.
         */
        public synchronized void onCancel(DialogInterface dialog) {
            // close the progress dialog
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            // setting the textfield to null ensures that the UI does NOT get
            // updated.
            textField = null;

            // Cancel the operation if possible.
            mHandler.cancelOperation(mToken);
        }
    }

    /**
     * Asynchronous query handler that services requests to look up ADNs
     *
     * Queries originate from {@link #handleAdnEntry}.
     */
    private static class QueryHandler extends NoNullCursorAsyncQueryHandler {

        private boolean mCanceled;

        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * Override basic onQueryComplete to fill in the textfield when
         * we're handed the ADN cursor.
         */
        @Override
        protected void onNotNullableQueryComplete(int token, Object cookie, Cursor c) {
            try {
                sPreviousAdnQueryHandler = null;
                if (mCanceled) {
                    return;
                }

                SimContactQueryCookie sc = (SimContactQueryCookie) cookie;

                // close the progress dialog.
                sc.progressDialog.dismiss();

                // get the EditText to update or see if the request was cancelled.
                EditText text = sc.getTextField();

                // if the textview is valid, and the cursor is valid and postionable
                // on the Nth number, then we update the text field and display a
                // toast indicating the caller name.
                if ((c != null) && (text != null) && (c.moveToPosition(sc.contactNum))) {
                    String name = c.getString(c.getColumnIndexOrThrow(ADN_NAME_COLUMN_NAME));
                    String number = c.getString(c.getColumnIndexOrThrow(ADN_PHONE_NUMBER_COLUMN_NAME));

                    // fill the text in.
                    text.getText().replace(0, 0, number);

                    // display the name as a toast
                    Context context = sc.progressDialog.getContext();
                    name = context.getString(R.string.menu_callNumber, name);
                    Toast.makeText(context, name, Toast.LENGTH_SHORT)
                        .show();
                }
            } finally {
                MoreCloseables.closeQuietly(c);
            }
        }

        public void cancel() {
            mCanceled = true;
            // Ask AsyncQueryHandler to cancel the whole request. This will fails when the
            // query already started.
            cancelOperation(ADN_QUERY_TOKEN);
        }
    }
    // yuhongxia add for cit 20140303
    private static boolean handleCITCode(Context context,String input){
        Log.i("SpecialCharSequenceMgr","handleCITCode input="+input);
        if (input.equals("####1111#")) {
                Intent intent = new Intent();
                intent.setClassName("com.qti.factory", "com.qti.factory.Framework.Framework");
                context.startActivity(intent);
                return true;
        }
        return false;
    }
   //add by nhl for internal version Sally_S7_C000168
    private static boolean handleInternalVersionCode(Context context,String input){
        if (input.equals("####5993#")) {
            String propRegion = SystemProperties.get("ro.lenovo.region");
            if("row".equals(propRegion)){
                Intent intent1 = new Intent("com.byd.settings.START_COUNTRYCODE_INTENT");
                context.sendBroadcast(intent1);
                return true;
            }else{
                getInternalVersion(context);
                return true;
            }
        }
        return false;
    }

    private static void getInternalVersion(Context context){

       String internalversionStr = SystemProperties.get("ro.build.lenovo.version.in");
	   internalversionStr += "_CN";
        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.software_version)
                .setMessage(internalversionStr)
                .setPositiveButton(android.R.string.ok,null)
        .setCancelable(false)
        .show();
    }
    private static boolean handleOutVersionCode(Context context,String input){
        if (input.equals("####0000#")) {
            getOutVersion(context);
            return true;
        }
        return false;
    }
    private static void getOutVersion(Context context){

       String outversionStr = SystemProperties.get("ro.build.lenovo.version.ex");

        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.out_software_version)
                .setMessage(outversionStr)
                .setPositiveButton(android.R.string.ok,null)
        .setCancelable(false)
        .show();
    }
    //add end
    private static boolean handleLogCode(Context context,String input){
        Log.i("SpecialCharSequenceMgr","handleLogCode input="+input);
        if (input.equals("####3333#")) {
            Intent intent = new Intent();
            intent.setClassName("com.app.tools", "com.app.tools.Tools");
            context.startActivity(intent);
            return true;
        }
        return false;
    }
    //add end
    private static boolean handleDebugMode(Context context,String input){
        Log.i("SpecialCharSequenceMgr","handleDebugMode input="+input);
        if (input.equals("####33284#")) {
            Intent intent = new Intent();
            intent.setClassName("com.byd.debugmode", "com.byd.debugmode.DebugMode");
            context.startActivity(intent);
            return true;
        }
        return false;
    }
//add by nhl
  private static boolean handleCountryCode(Context context, String input){
         if (input.equals("####682#")) {
                Intent intent = new Intent();
                intent.setClassName("com.android.choosecountrycode", "com.android.choosecountrycode.ChooseCountryCodeActivity");
                context.startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return true;
        }
        return false;
    }
    //add end
    //add by nhl for SALLY-1330
  private static boolean handleCountryCodeInfo(Context context, String input){
         if (input.equals("####19491010#")) {
            String strCC = "CountryCode: ";
            String CountryCodeStr = SystemProperties.get("ro.lenovo.easyimage.code").toLowerCase();
            strCC += CountryCodeStr;
            AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle("CountryCodeValue")
                .setMessage(strCC)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .show();
           return true;
        }
         return false;
    }
    //add end
    //add by wangbo
    private static boolean handleToolsCode(Context context, String input){
         if (input.equals("####7979#")) {
                Intent intent = new Intent();
                intent.setClassName("com.app.tools", "com.app.tools.Tools");
                context.startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return true;
        }
        return false;
    }
    //add end
    private static boolean handleSpkReceiverPlay(Context context,String input){
        Log.i("SpecialCharSequenceMgr","SpkReceiverPlay input="+input);
        if (input.equals("####9327#")) {
                Intent intent = new Intent();
                intent.setClassName("com.byd.runin", "com.byd.runin.SpkReceiverPlay");
                context.startActivity(intent);
                return true;
        }
        return false;
    }

    private static boolean handleRuninDisplay(Context context,String input){
        Log.i("SpecialCharSequenceMgr","handleRuninDisplay input="+input);
        if (input.equals("####78646#")) {
                Intent intent = new Intent();
                intent.setClassName("com.byd.runin", "com.byd.runin.RuninTestMainActivity");
                context.startActivity(intent);
                return true;
        }
        return false;
    }
    private static boolean handlePVTest(Context context,String input){
        Log.i("SpecialCharSequenceMgr","handlePVTest input="+input);
        if (input.equals("####788378#")) {
                Intent intent = new Intent();
                intent.setClassName("com.byd.runin", "com.byd.runin.vibrator.VibratorActivity");
                context.startActivity(intent);
                return true;
        }
        return false;
    }
    /*Start add by cui.yuehua@byd.com for lenovo spce*/

    private static boolean handleFactoryDatareset(Context context,String input ){
        String inputStr="####7777#";
        final Context mContext = context;
        Log.i(TAG, "handleFactoryDatareset into");
        if(input.equals(inputStr)){
        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.facotry_mode_dialog_title)
                .setMessage(R.string.facotry_mode_dialog_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                        public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "handleFactoryDatareset click ok");
                                Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
                                intent.putExtra("fromSpecialChar", "true");
                                mContext.sendBroadcast(intent);
                        }
                })
                .setNegativeButton(com.android.internal.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "cancle FactoryDatareset");
                                }
                })
                .show();
                return true;
        } else {
                return false;
        }
    }

    private static boolean handleRegulatoryInfoDisplay(Context context, String input) {
        if(SystemProperties.get("ro.lenovo.region").equals("row")) {
            if(input.equals("*#07#")){
            AlertDialog alert = new AlertDialog.Builder(context)
                    .setMessage(R.string.head_and_body_sar)
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .show();
            return true;
            }else {
                    return false;
            }
            } else {
                    return false;
            }
    }

   private static boolean handleTPAndLCDDisplay(Context context, String input) {
        if (DISPLAY_TP_LCD_INFORMATION.equals(input)) {
            String information = getDeviceInfo(context);
            try {
                AlertDialog alert = new AlertDialog.Builder(context)
                    .setTitle(android.R.string.device_information)
                    .setMessage(information)
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .show();
                return true;
            }catch (Exception e) {
                Log.d(TAG, "[handleTPAndLCDDisplay]" + e.getMessage());
                return false;
            }
        }else {
            return false;
        }
    }

    private static String getDeviceInfo(Context context){
        String information = "";
        FileReader fr = null;
        BufferedReader br = null;
        String path = "/proc/hw_info/info";
        int flag = 0;
        try{
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            boolean isLcd = false;

            while(true){
                String load = br.readLine();
                Log.d(TAG, "[handleTPAndLCDDisplay] load 1  " + load);
                if ( load == null || load.equals("::::") )
                    continue;

                String[] str = load.split(":");
                Log.d(TAG, "[handleTPAndLCDDisplay] str  " + str);

                if ( !str[0].equals("") && !str[0].equalsIgnoreCase("NULL") ){
                    if ( str[0].equalsIgnoreCase("LCD") ){
                        information += context.getResources().getString(com.android.internal.R.string.lcd);
                        isLcd = true;
                        ++flag;
                    }else if(str[0].equalsIgnoreCase("TP")){
                        information += context.getResources().getText(com.android.internal.R.string.tp);
                        ++flag;
                    }
                }

                if ( !str[1].equals("") && !str[1].equalsIgnoreCase("NULL") ){
                    information += "    ";
                    information += context.getResources().getString(com.android.internal.R.string.ic);
                    information += str[1];
                    information += "\n";
                }

                if ( !str[2].equals("") && !str[2].equalsIgnoreCase("NULL") ){
                    information += "    ";
                    information += context.getResources().getString(com.android.internal.R.string.vendor);
                    information += str[2];
                    information += "\n";
                }

                if ( !str[3].equals("") && !str[3].equalsIgnoreCase("NULL") ){
                    information += "    ";
                    information += context.getResources().getString(com.android.internal.R.string.fw);
                    information += str[3];
                    information += "\n";
                }

                if ( isLcd ){
                    if ( !str[4].equals("") && !str[4].equalsIgnoreCase("NULL") ){
                       String[] ss = str[4].split("_");
                       information += "    ";
                       information += context.getResources().getString(com.android.internal.R.string.resolutionratio);
                       information += ss[0];
                       information += "\n";
                    }
                    isLcd = false;
                }

                if ( 2 == flag  ){
                    break;
                }

            }

            Log.d(TAG, "[handleTPAndLCDDisplay] information  " + information);
        }catch(IOException e){
            Log.d(TAG, "[handleTPAndLCDDisplay]" + e.getMessage());
            return null;
        }finally{
            try{
                if ( fr != null )
                    fr.close();

                if ( br != null )
                    br.close();
            }catch(IOException e){
                Log.d(TAG, "[handleTPAndLCDDisplay]" + e.getMessage());
                return null;
            }
        }

        return information;
    }

    private static String getFormattedKernelVersion() {
        String procVersionStr;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX =
                "\\w+\\s+" + /* ignore: Linux */
                "\\w+\\s+" + /* ignore: version */
                "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                "([^\\s]+)\\s+" + /* group 3: #26 */
                "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                "(.+)"; /* group 4: date  */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);
            String sBaseBand = SystemProperties.get("gsm.version.baseband"," ");
                Log.e(TAG, "sBaseBand =  " + sBaseBand);
            String hwVersion = SystemProperties.get("ro.build.lenovo.hw.version"," ");
            String branchName = SystemProperties.get("apps.setting.product.branch"," ");
            //end
            if (!m.matches()) {
                Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                        + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4)).append("\n")
                        .append(hwVersion).append("\n")
                        .append(sBaseBand).append("\n")
                        .append(branchName).toString());
            }
        } catch (IOException e) {
            Log.e(TAG,
                "IO Exception when getting kernel version for Device Info screen", e);
            return "Unavailable";
        }
    }

    private static void showKernelVersionPanel(Context context, boolean useSystemWindow,
        TelephonyManager telephonyManager) {
                String kernelversionStr = getFormattedKernelVersion();
                AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle("Kernel Version")
                .setMessage(kernelversionStr)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .show();
     }
    /*End add by cui.yuehua@byd.com for lenovo spce*/
}