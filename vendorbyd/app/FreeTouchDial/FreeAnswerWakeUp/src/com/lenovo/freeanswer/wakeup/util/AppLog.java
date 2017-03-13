
package com.lenovo.freeanswer.wakeup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.os.Environment;
import android.util.Log;

public class AppLog implements Runnable {

    public static boolean DEBUG = false;
    static {
        // DEBUG |= isFileExist();
        // DEBUG = PersistTool.getBoolean(PersistTool.APP_LOG_KEY, true);
    }
    public static final String LOG_FILE_PATH = "/mnt/sdcard/debug_fa.txt";
    private static final String CHECK_FILE = "showlog.txt";
    private static java.io.FileWriter f;
    public static final String TAG = "lenovo_fa";

    public static void v(String TAG, String msg) {
        Log.v(TAG, msg);
        if (!DEBUG)
            return;
        // if (APP_TAG) {
        // Log.v("LvAppLog", TAG + ":" + msg);
        // }
        write2file("[" + TAG + "]" + msg);
    }

    public static void e(String TAG, String msg, Throwable t) {
        Log.e(TAG, msg, t);
        if (!DEBUG)
            return;
        // if (APP_TAG) {
        // Log.e("LvAppLog", TAG + ":" + msg, t);
        // }
        write2file("[" + TAG + "]" + msg);
    }

    public static void w(String TAG, String msg) {
        Log.w(TAG, msg);
        if (!DEBUG)
            return;
        // if (APP_TAG) {
        // Log.w("LvAppLog", TAG + ":" + msg);
        // }
        write2file("[" + TAG + "]" + msg);
    }

    public static void d(String TAG, String msg) {
        Log.d(TAG, msg);
        if (!DEBUG)
            return;
        // if (APP_TAG) {
        // Log.d("LvAppLog", TAG + ":" + msg);
        // }
        write2file("[" + TAG + "]" + msg);
    }

    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
        if (!DEBUG)
            return;
        // if (APP_TAG) {
        // Log.e("LvAppLog", TAG + ":" + msg);
        // }
        write2file("[" + TAG + "]" + msg);
    }

    public static void i(String TAG, String msg) {
        Log.i(TAG, msg);
        if (!DEBUG)
            return;
        // if (APP_TAG) {
        // Log.i("LvAppLog", TAG + ":" + msg);
        // }
        write2file("[" + TAG + "]" + msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void ex(Exception e) {

        java.io.StringWriter w = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(w);
        e.printStackTrace(pw);
        e(TAG, w.toString().replace("\n", "\r\n"));
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    private static Thread blinker = null;
    private static Object bMutex = new Object();

    public void stop() {
        synchronized (bMutex) {
            blinker = null;
        }
    }

    private static Queue<String> msgQueue = new LinkedList<String>();

    private static void write2file(String msg) {
        // write2file(msg, DEBUG);
    }

    static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ");

    private static void write2file(String msg, boolean defaultWriteFile) {
        if (defaultWriteFile) {
            synchronized (bMutex) {
                if (null == blinker) {
                    blinker = new Thread(new AppLog());
                    blinker.start();
                }
            }
            // msgQueue.offer(msg);
            msgQueue.offer(FORMAT.format(new Date()) + msg);
        }
    }

    @Override
    public void run() {
        try {
            int i = 1;
            while (true) {
                if (msgQueue.size() > 0) {
                    String msg = msgQueue.poll();
                    try {
                        if (null == f) {
                            f = new java.io.FileWriter(LOG_FILE_PATH, true);
                        }
                        f.write(msg + "\r\n");
                        if (i++ % 2 == 0) {
                            f.flush();
                        }
                    } catch (IOException e) {
                        try {
                            f.close();
                        } catch (IOException e1) {
                        }
                        f = null;
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            stop();
        }
    }

    /**
     * �?��sdcard下是否存在showlog.txt文件,里面是否存在true字段 .若存在则视为打印log,否则不打�?
     * 
     * @return 存在文件并且内容�?true" 则返回true 否则FALSE
     */
    private static boolean isFileExist() {
        // 打开文件
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + CHECK_FILE);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                // 文件存在
                FileInputStream inputStream;
                try {
                    inputStream = new FileInputStream(file);
                    byte[] b = new byte[inputStream.available()];
                    inputStream.read(b);
                    String content = new String(b);
                    inputStream.close();
                    return "true".equals(content);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
