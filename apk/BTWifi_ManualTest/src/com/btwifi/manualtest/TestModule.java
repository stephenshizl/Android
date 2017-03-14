package com.btwifi.manualtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.View;
/**
 * Base class for a test module
 * @author wangx
 *
 */
public abstract class TestModule extends Activity {
    public static String TAG = "BtWifi test";
    @Override
    protected void onResume(){
        SystemProperties.set("persist.service.daemon.enable", "0");
        super.onResume();
    }
    public void onCreate(Bundle savedInstanceState, String com) {
        super.onCreate(savedInstanceState);
    }
    public void runCommand(String com, String dir) {
        ShellExecute cmdexe = new ShellExecute();
        String re = "";
        String ARGS = "echo " + com + " > " + dir;
        try {
            re = cmdexe.execute(ARGS,"/");
        }catch(IOException e) {
        }
        Log.i(TAG, "cmd result:" + re);
    }
   @Override
    protected void onPause() {
        super.onPause();
        SystemProperties.set("persist.service.daemon.enable", "0");
    }

    @Override
    protected void onDestroy() {
        runCommand("0", "/data/citflag");
        super.onDestroy();
        
    }
    public class ShellExecute {
        public String execute (String command, String directory) throws IOException{
            String result = "";
            Log.i(TAG, "cmd ShellExecute start");
            List<String> cmds = new ArrayList<String>();
            cmds.add("sh");
            cmds.add("-c");
            cmds.add(command);
            try {
                ProcessBuilder builder = new ProcessBuilder(cmds);
                if(directory != null) {
                    builder.directory(new File(directory));
                }
                Log.i(TAG, "cmd ShellExecute cmds=" + cmds);
                builder.redirectErrorStream(true);
                Process process = builder.start();

                //get result
                InputStream is = process.getInputStream();
                byte[] buffer = new byte[1024];
                while(is.read(buffer) != -1) {
                    result = result + new String(buffer);
                }
                is.close();
            
            }catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public void OnClickToCommand () {
        String s = getCommand();
        if(s != null) {
            Log.i(TAG, "onClickCommand com=" + s);
            runCommand("20", "/data/citflag");
            runCommand(s, "/data/btwifi");
            SystemProperties.set("persist.service.daemon.enable", "1");
        }

    };
    public abstract String getCommand();
}