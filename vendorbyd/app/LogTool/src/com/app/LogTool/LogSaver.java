/**
 * Copyright (C) 2010-2020 BYD Corporation.  All rights reserved.
 *
 * BYD Corporation and its licensors retain all intellectual property
 * and proprietary rights in and to this software, related documentation
 * and any modifications thereto.  Any use, reproduction, disclosure or
 * distribution of this software and related documentation without an express
 * license agreement from BYD Corporation is strictly prohibited.
 *
 *
 *
 *
 * General Description: auto SMS
 *
 *
 *        Date          Author            CR/PR                         Reference
 *    ==========       ========      ====================      ==========================
 */
package com.app.LogTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import android.os.Environment;
import android.os.StatFs;
import android.content.Intent;
import android.net.Uri;

public class LogSaver {
    static final boolean DBG = true;
    static final String TAG = "LogSaver";
    static final SimpleDateFormat LOG_FILE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd-HH-mm-ssZ");

    private Context mContext;
    private String partTargetPath;

    public LogSaver(Context context) {
        mContext = context;
    }
    public boolean saveall() {
        Log.d(TAG, "start saveall");

        boolean mediaMounted = false;
        mediaMounted = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            partTargetPath = "/sdcard/logs/All";


        File path = new File(partTargetPath + LOG_FILE_FORMAT.format(new Date()));
        //File androidPath = new File(path + "/androidLog");

        try {
            ZipHelper.zipDir(new File("/data/misc/logd"), path, "androidLog.zip");
            //ZipHelper.zipDir(new File("/data/bp_panic"), path, "cpPanic.zip");
            ZipHelper.zipDir(new File("/data/dontpanic"), path, "dontpanic.zip");
            //ZipHelper.zipDir(new File("/data/anr"), path, "anr.zip");
            ZipHelper.zipDir(new File("/sys/fs/pstore/ "), path, "dontpanic.zip");
        } catch (Exception e) {
            Log.e(TAG, "saveall exception:" + e);
            e.printStackTrace();
        }
            List<File> subFiles = new ArrayList<File>();
            scanLog(path, subFiles);

        Log.d(TAG, "complete saveall");

        return true;
    }

    private void scanLog(File searchPath, List<File> allSubFiles){
        if (searchPath.exists()){
            if (searchPath.isDirectory()) {
                File[] subFiles = searchPath.listFiles();
                int len = subFiles == null ? 0 : subFiles.length;
                for (int i = 0; i < len; i++) {
                    String logpath = subFiles[i].getAbsolutePath();
                    Intent logScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File allLogPath = new File(logpath);
                    Uri contentUri = Uri.fromFile(allLogPath);
                    logScanIntent.setData(contentUri);
                    mContext.sendBroadcast(logScanIntent);
                }
            }
        }
    }
    public boolean externalHasEnoughMemory() {
        if((getAvailableSize(Environment.getExternalStorageDirectory().toString())) > 100 ) {
            return true;
        } else {
            return false;
        }
    }

    /*private boolean sdcardHasEnoughMemory() {
        if((getAvailableSize(Environment.getInternalStorageDirectory().toString())) > 100) {
            Log.d(TAG,"SD CARD path = " + Environment.getInternalStorageDirectory().toString());
            return true;
        } else {
            return false;
        }
    }*/

    private long getAvailableSize(String path) {
        StatFs fileStats = new StatFs(path);
        fileStats.restat(path);
        Log.d(TAG,"the free space of disk "+ ((long) fileStats.getAvailableBlocks() * fileStats.getBlockSize())/(1024*1024));
        return ((long) fileStats.getAvailableBlocks() * fileStats.getBlockSize())/(1024*1024);
    }
}
