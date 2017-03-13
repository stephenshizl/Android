package com.app.tools;

import java.util.List;
import java.util.Collections;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
/*
*
*    Date             Author         CR/PR ID             Headline
*
*
*
*/
public class PackagesInfo {
    private List<ApplicationInfo> appList;
    private List<ResolveInfo> resolveAppList;
    PackageManager pm;
    public PackagesInfo(Context context) {
        //with PackageManeger get application info
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        pm = context.getApplicationContext().getPackageManager();
        appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        resolveAppList = pm.queryIntentActivities(intent, 0);
    }

    /**
     * with application name get the application info
     * @param name
     * @return  ApplicationInfo , ResolveInfo
     */

    public ApplicationInfo getInfo(String name) {
        if(name == null) {
            return null;
        }
        for(ApplicationInfo appinfo : appList) {
            if(name.equals(appinfo.processName)) {
                return appinfo;
            }
        }
        return null;
    }
    public ResolveInfo getResolveInfo(String name) {

        if(name == null) {
            return null;
        }
        for(ResolveInfo resolveinfo : resolveAppList) {
            if(name.equals(resolveinfo.activityInfo.applicationInfo.processName)) {
                return resolveinfo;
            }
        }
        return null;

    }
}
