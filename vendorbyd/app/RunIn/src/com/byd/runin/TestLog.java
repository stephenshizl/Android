package com.byd.runin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.os.Message;

public class TestLog
{
    private static final String TAG = "TestLog";

    private static TestLog _instance = null;
    FileOutputStream mFileOS = null;

    private static final String FILE_PATH = "/data/data/com.byd.runin/";
    private static final int TIME_FLUSH_DELAY = 5 * 1000;
    private static final int FILE_MAX_SIZE = 5 * 1024 * 1024;
    private static final int FILE_MAX_COUNT = 10;

    private int m_file_index = 0;

    private File mFile = null; //oprator current file

    private static final String SUFFIX_NAME = ".txt";
    private static final String STORAGE_FILE_NAME = "log";


    private TestLog()
    {
        init();
        createFile();
        //mHandler.sendEmptyMessageDelayed(0, TIME_FLUSH_DELAY);

    }

    private Handler mHandler = new Handler()
    {
        @Override public void handleMessage(Message msg)
        {
            if (mFileOS == null)
                return ;

            try
            {
                mFileOS.flush();
            }
            catch (IOException e)
            {
                android.util.Log.e(TAG, "flush exception " + e);
            }
            mHandler.sendEmptyMessageDelayed(0, TIME_FLUSH_DELAY);
        }
    };

    public static TestLog getInstance()
    {
        if (_instance == null)
        {
            _instance = new TestLog();
        }

        return _instance;
    }

    private String getTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        return format.format(new Date());
    }

    private void init()
    {
        File file = new File(FILE_PATH);
        if (!file.exists())
        {

            if (file.mkdirs())
            {
                android.util.Log.d(TAG, "INIT CREATE");
            }
            else
            {
                android.util.Log.d(TAG, "INIT fail");
            }
        }
        else if (file.isDirectory())
        {
            m_file_index = file.listFiles().length;
        }

        android.util.Log.d(TAG, "INIT AAAAAAAAAA");
    }

    private void writeFileData(byte[]fileData)
    {
        int freeSpaceSize = getFileFreeSpaceSize();
        int textSize = fileData.length;

        if (freeSpaceSize > 0)
        {
            freeSpaceSize = ((textSize - freeSpaceSize) > 0 ? freeSpaceSize :
                textSize);

            try
            {
                byte[]canWriteSize = new byte[freeSpaceSize];
                System.arraycopy(fileData, 0, canWriteSize, 0, freeSpaceSize);
                mFileOS = new FileOutputStream(mFile, true);
                mFileOS.write(canWriteSize);
                mFileOS.flush();
                mFileOS.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        int remainSize = textSize - freeSpaceSize;

        int tempIndex = m_file_index;
        int loop = 0;

        try
        {
            while (remainSize > 0)
            {
                tempIndex = m_file_index;
                //create File and replace before file
                if (m_file_index < FILE_MAX_COUNT)
                {
                    do
                    {
                        if (tempIndex == 1)
                        {
                            File renameFile = getFile(tempIndex - 1);
                            File to = getFile(tempIndex);
                            if (renameFile.renameTo(to)){}
                            else
                            {
                                System.out.println("m_file_index fail");
                            }
                        }
                        else
                        {
                            System.out.println("tempIndex = " + tempIndex);
                            File renameFile = getFile(tempIndex - 1);
                            File to = getFile(tempIndex);
                            if (renameFile.renameTo(to))
                            {
                                System.out.println("success");
                            }
                            else
                            {
                                System.out.println("fail");
                            }
                        }

                    }
                    while (--tempIndex > 0);

                }
                else
                {
                    do
                    {
                        if (tempIndex == FILE_MAX_COUNT)
                        {
                            File writeFile = getFile(tempIndex - 1);
                            if (writeFile.exists() && writeFile.delete())
                            {
                                File renameFile = getFile(tempIndex - 2);
                                renameFile.renameTo(getFile(tempIndex - 1));
                            }
                        }
                        else
                        {
                            File renameFile = getFile(tempIndex - 1);
                            renameFile.renameTo(getFile(tempIndex));
                        }

                    }
                    while (--tempIndex > 0);
                }

                createFile()
                    ;
                //Always create the first file
                //The remaining size
                int startSrcIndex = freeSpaceSize + loop * FILE_MAX_SIZE;
                ++loop;
                int remainDataSize = (remainSize - FILE_MAX_SIZE) >= 0 ?
                    FILE_MAX_SIZE : remainSize;
                byte[]remainData = new byte[remainDataSize];
                System.arraycopy(fileData, startSrcIndex, remainData, 0,
                    remainDataSize);
                remainSize = (remainSize - FILE_MAX_SIZE) > 0 ? (remainSize -
                    FILE_MAX_SIZE): 0;
                mFileOS = new FileOutputStream(mFile, true);
                mFileOS.write(remainData);
                mFileOS.flush();
                mFileOS.close();

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File getFile(int fileIndex)
    {
        File file = null;
        if (fileIndex > 0)
        {
            file = new File(FILE_PATH + STORAGE_FILE_NAME + fileIndex +
                SUFFIX_NAME);
        }
        else
        {
            file = new File(FILE_PATH + STORAGE_FILE_NAME + SUFFIX_NAME);
        }
        return file;
    }


    private void createFile()
    {
        File file = new File(FILE_PATH + STORAGE_FILE_NAME + SUFFIX_NAME);
        if (!file.exists())
        {
            try
            {
                if (file.createNewFile())
                {
                    ++m_file_index;
                }
            }
            catch (IOException e){}
        }

        mFile = file;
    }

    private int getUseSpaceSize()
    {
        int availableSize = 0;

        try
        {
            FileInputStream fis = new FileInputStream(mFile);
            availableSize = fis.available();
            fis.close();
        }
        catch (IOException e){

        }

        return availableSize;
    }

    private int getFileFreeSpaceSize()
    {
        int availableSize = getUseSpaceSize();

        return FILE_MAX_SIZE - availableSize;
    }

    private synchronized void d(String tag, String msg)
    {
        String message = getTime() + " D/" + tag + ":" + msg + "\n";
        writeFileData(message.getBytes());
    }

    private synchronized void e(String tag, String msg)
    {
        String message = getTime() + " E/" + tag + ":" + msg + "\n";
        writeFileData(message.getBytes());
    }

    private synchronized void w(String tag, String msg)
    {
        String message = getTime() + " W/" + tag + ":" + msg + "\n";
        writeFileData(message.getBytes());
    }

    private synchronized void i(String tag, String msg)
    {
        String message = getTime() + " I/" + tag + ":" + msg + "\n";
        writeFileData(message.getBytes());
    }

    private synchronized void v(String tag, String msg)
    {
        String message = getTime() + " V/" + tag + ":" + msg + "\n";
        writeFileData(message.getBytes());
    }

    public static class Log
    {
        public static void d(String tag, String msg)
        {
            TestLog.getInstance().d(tag, msg);
            android.util.Log.d(tag, msg);
        }

        public static void e(String tag, String msg)
        {
            TestLog.getInstance().e(tag, msg);
            android.util.Log.e(tag, msg);
        }

        public static void w(String tag, String msg)
        {
            TestLog.getInstance().w(tag, msg);
            android.util.Log.w(tag, msg);
        }

        public static void i(String tag, String msg)
        {
            TestLog.getInstance().i(tag, msg);
            android.util.Log.i(tag, msg);
        }

        public static void v(String tag, String msg)
        {
            TestLog.getInstance().v(tag, msg);
            android.util.Log.v(tag, msg);
        }

    }
}
