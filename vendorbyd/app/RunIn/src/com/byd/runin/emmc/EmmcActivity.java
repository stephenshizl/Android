
package com.byd.runin.emmc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.byd.runin.TestActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class EmmcActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_emmc_test";
    public static final String TITLE = "EMMC Test";

    private static final String FILE_PATH = "/data/data/com.byd.runin/text.txt";

    private static final String TAG = "EmmcActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_emcc_test_time";

    private static final String EMCC_TEST_FAIL_INFO = "emcc test fail: ";

    private static final int BUFFER_SIZE = 10240;

    private static final int MSG_READ_EMMC = 1;
    private static final int MSG_CLEAR_EMMC = 2;

    private static final int TIME_READ_EMMC = 100; // 100ms
    private static final int TIME_START_READ = 3; // 3s
    private static final int RANDOM_BUFFER_SIZE = 500;
    private StringBuffer mRandomBuffer = new StringBuffer(RANDOM_BUFFER_SIZE);

    private Random mRandom = new Random(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);

        startWriteEmmc();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mHandler.removeMessages(MSG_READ_EMMC);
        mHandler.removeMessages(MSG_CLEAR_EMMC);
        mHandler = null;
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_READ_EMMC:
                    randomReadEmmc();
                    break;
                case MSG_CLEAR_EMMC:
                    randomClearEmmc();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private void randomClearEmmc()
    {
        File file = new File(FILE_PATH);
        FileWriter fileWriter;
        FileInputStream fis = null;
        int available = 0;
        try
        {
            fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.close();
            fis = new FileInputStream(FILE_PATH);
            available = fis.available();
            mHandler.sendEmptyMessageDelayed(MSG_READ_EMMC, TIME_READ_EMMC);
        }
        catch (IOException e)
        {
            updateFailedText();
            Log.d(TAG, e.getMessage());
            dealwithError(mStatusText, EMCC_TEST_FAIL_INFO + e.getMessage());
        }
    }
    private void randomReadEmmc()
    {
        FileInputStream fis = null;
        InputStream is = null;
        FileOutputStream fos = null;
        int available = 0;
        try
        {
            File file = new File(FILE_PATH);
            fis = new FileInputStream(FILE_PATH);
            is = getApplicationContext().getResources().getAssets().open("emmc.txt");
            fos = new FileOutputStream(file);
            byte[]buffer = new byte[BUFFER_SIZE];

            while (true)
            {
                int result = is.read(buffer);
                if (result ==  - 1)
                    break;
                fos.write(buffer, 0, result);
                fos.write(buffer, 0, result);
            }
            available = fis.available();
            int random = mRandom.nextInt(available);
            fis.skip(random);
            int ch = fis.read();
            if (ch !=  - 1)
            {
                char cha = (char)ch;
                mRandomBuffer.insert(0, cha);

                mStatusText.setText(mRandomBuffer.toString());
            }

            mHandler.sendEmptyMessageDelayed(MSG_CLEAR_EMMC, TIME_READ_EMMC);
        }
        catch (FileNotFoundException e)
        {
            updateFailedText();
            Log.d(TAG, e.getMessage());
            dealwithError(mStatusText, EMCC_TEST_FAIL_INFO + e.getMessage());
        }
        catch (IOException e)
        {
            updateFailedText();
            Log.d(TAG, e.getMessage());
            dealwithError(mStatusText, EMCC_TEST_FAIL_INFO + e.getMessage());
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                updateFailedText();
                Log.d(TAG, e.getMessage());
                dealwithError(mStatusText, EMCC_TEST_FAIL_INFO + e.getMessage());
            }
        }
    }

    private void startWriteEmmc()
    {
        mStatusText.setText("Writing Emmc...");
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                writeEmmc();
            }
        }).start();
    }

    private void writeEmmc()
    {
        InputStream is = null;
        FileOutputStream fos = null;
        try
        {
            File file = new File(FILE_PATH);
            if (!file.exists())
            {
                file.mkdirs();
            }
            file.delete();

            file.createNewFile();

            is = getApplicationContext().getResources().getAssets().open("from.txt");
            fos = new FileOutputStream(file);
            byte[]buffer = new byte[BUFFER_SIZE];

            while (true)
            {
                int result = is.read(buffer);
                if (result ==  - 1)
                    break;
                fos.write(buffer, 0, result);
                fos.write(buffer, 0, result);
            }

            startReadEmmc();
        }
        catch (IOException e)
        {
            updateFailedText();
            Log.d(TAG, e.getMessage());
            dealwithError(mStatusText, EMCC_TEST_FAIL_INFO + e.getMessage());
        }
        finally
        {
            try
            {
                is.close();
                if (fos != null)
                {
                    fos.close();
                }
                else
                {
                      Log.d(TAG, "fos is null");
                }
            }
            catch (IOException e)
            {
                updateFailedText();
                Log.d(TAG, e.getMessage());
                dealwithError(mStatusText, EMCC_TEST_FAIL_INFO + e.getMessage());
            }
        }
    }

    private void startReadEmmc()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String text = "Random read Emmc after " + TIME_START_READ + "s";
                mStatusText.setText(text);

                if (mHandler != null)
                {
                    mHandler.sendEmptyMessageDelayed(MSG_READ_EMMC,
                        TIME_START_READ * 1000);
                }
            }
        });
    }

    private void updateFailedText()
    {
        updateStatusText("Failed");
    }

    private void updateStatusText(String text)
    {
        final String finalText = text;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mStatusText.setText(finalText);
            }
        });
    }
}
