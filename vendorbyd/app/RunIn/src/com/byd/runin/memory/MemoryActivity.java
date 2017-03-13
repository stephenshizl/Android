
package com.byd.runin.memory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MemoryActivity extends TestActivity implements Runnable
{
    public static final String KEY_SHARED_PREF = "key_memory_test";
    public static final String TITLE = "Memory Test";

    private static final String TAG = "MemoryActivity";
    public static final String KEY_SHARED_PREF_TEST_TIME = "key_memory_test_time";

    private static final String MEMORY_TEST_FAIL_INFO = "memory test fail: ";

    private static final int BUFFER_SIZE = 10240;

    private static final int TIME_READ_AFTER_LOADING = 3; // 3s
    private static final int TIME_RANDOM_READ = 100; // 100ms

    private static final int MSG_RANDOM_READ = 1;

    private String m1MText = "";
    private static final int RANDOM_BUFFER_SIZE = 500;
    private StringBuffer mRandomBuffer = new StringBuffer(RANDOM_BUFFER_SIZE);

    private Random mRandom = new Random(System.currentTimeMillis());
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;

        super.onCreate(savedInstanceState);

        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (thread != null)
        {
            thread = null;
        }
        mHandler.removeMessages(MSG_RANDOM_READ);
        mHandler = null;
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_RANDOM_READ:
                    randomRead();
                    if (mHandler != null)
                    {
                        mHandler.sendEmptyMessageDelayed(MSG_RANDOM_READ,TIME_RANDOM_READ);
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };

    private void randomRead()
    {
        int text_size = m1MText.length();
        int random = mRandom.nextInt(text_size);
        char ch = random >= text_size ? m1MText.charAt(0): m1MText.charAt(random);
        mRandomBuffer.insert(0, ch);
        if (mRandomBuffer.length() > RANDOM_BUFFER_SIZE)
        {
            mRandomBuffer.deleteCharAt(mRandomBuffer.length() - 1);
        }

        mStatusText.setText(mRandomBuffer.toString());
    }

    private void updateLoadingText()
    {
        int size = m1MText.getBytes().length;
        String text = "loading " + size + "bytes";
        final String finalText = text;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mStatusText.setText(finalText);
            }
        }
        );
    }

    private void readWriteRAM()
    {
        InputStream is = null;
        InputStreamReader isr = null;
        try
        {
            // get assets resource
            is = getApplicationContext().getResources().getAssets().open("from.txt");
            isr = new InputStreamReader(is);
            char[]buffer = new char[BUFFER_SIZE];
            while (true)
            {
                int result = isr.read(buffer);
                if (result ==  - 1)
                    break;
                m1MText = m1MText.concat(new String(buffer, 0, result));
                updateLoadingText();
            }

        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
            dealwithError(mStatusText, MEMORY_TEST_FAIL_INFO + e.getMessage());
        }
        finally
        {
            try
            {
                is.close();
                isr.close();
            }
            catch (Exception e)
            {
                Log.d(TAG, e.getMessage());
                dealwithError(mStatusText, MEMORY_TEST_FAIL_INFO + e.getMessage());
            }
        }

        startReadTextRandom();
    }

    private void startReadTextRandom()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                 String read = "Random read after " + TIME_READ_AFTER_LOADING + "s"; mStatusText.append("\n" + read);

                if (mHandler != null)
                {
                    mHandler.sendEmptyMessageDelayed(MSG_RANDOM_READ,TIME_READ_AFTER_LOADING * 1000);
                }
            }
        });
    }

    @Override
    public void run()
    {
        readWriteRAM();
    }
}
