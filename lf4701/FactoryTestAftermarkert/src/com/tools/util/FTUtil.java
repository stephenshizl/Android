
package com.tools.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

public class FTUtil {
    // public static String TEST_TIME = getCurrentTime();
    public static final String TAG = "FactoryTest";
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 54321;

    public static String getCurrentTime() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }

    // close file
    public static void close(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Reader in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(OutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int indexInArray(int element, int[] array) {
        if (array == null || array.length == 0)
            return -1;
        for (int i = 0; i < array.length; i++)
            if (element == array[i])
                return i;
        return -1;
    }

    public static int indexInArray(String element, String[] array) {
        if (array == null || array.length == 0)
            return -1;
        for (int i = 0; i < array.length; i++)
            if (element.equals(array[i]))
                return i;
        return -1;
    }

    /**
     * Add color to text.
     *
     * @param text
     * @param color
     * @return colored string
     */
    public static CharSequence coloredString(String text, int color) {
        String string = TextUtils.htmlEncode(text == null ? "" : text);
        string = "<font color=\"#" + String.format("%06x", color & 0xFFFFFF) + "\">" + string
                + "</font>";
        return Html.fromHtml(string);
    }

    /**
     * Add color to text whose resource id is resourceId.
     *
     * @return color string
     */
    public static CharSequence coloredString(Context context, int resourceId, int color) {
        return coloredString(context.getResources().getString(resourceId), color);
    }

    public static String TCP_Send(String command)
    {
        String answer = "";
        Socket socket=null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            String s;
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);
            out.println(command);
            out.flush();
            while ((s = in.readLine()) != null)
            {
                answer += s;
                answer += "\n";
            }
            Log.i(TAG, "receive: " + answer);
        } catch (UnknownHostException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally
        {
            out.close();
            close(in);
            close(socket);
        }
        return answer;
    }

    public static boolean runTcmdCommand(String command) {
        // String command = "cp-logs " + module;
        String receive = TCP_Send(command);
        if ("" != receive && -1 != receive.indexOf("ret: 0", 0))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static class SendTcmdTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            if (FTUtil.runTcmdCommand(params[0]))
            {
                Log.d(TAG, params[0] + " has been run sucessfully");
            }
            else
            {
                Log.e(TAG, params[0] + " has been run unsucessfully");
            }
            return null;
        }

    }

    public static void waitforcheck()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
