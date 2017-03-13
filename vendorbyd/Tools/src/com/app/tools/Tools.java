package com.app.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActivityManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import android.util.Log;
import android.os.SystemProperties;

/*
*
*    Date             Author         CR/PR ID             Headline
*
*
*
*/
public class Tools extends ListActivity {
private String TAG = "Tools";
    List<Program> list;
    PackageManager pm;
    String packageName;
    String activityName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getProgrameInfo();
        ListAdapter adapter = new ListAdapter(list,this);
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View adapter, int position,
                                    long id) {
                Intent intent = new Intent();
                Program p = list.get(position);
                packageName = p.getPackageName();
				if(packageName.equals("com.qualcomm.qualcommsettings")){
					activityName = "com.qualcomm.qualcommsettings.QualcommSettings";
				}else{
	                activityName = p.getActivityName();
				}
                intent = activityIntent(packageName,activityName);
                startActivity(intent);
            }
        });
    }
    public List<Program> getProgrameInfo() {
        PackagesInfo pi = new PackagesInfo(this);
        PackageManager pm =this.getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        List<Program> list = new ArrayList<Program>();
        for(PackageInfo pinfo:packs) {
            if(
                pinfo.packageName.equals("com.app.APLogSetting")||
                pinfo.packageName.equals("com.app.LogTool") ||
                pinfo.packageName.equals("com.android.development")||
                pinfo.packageName.equals("com.app.BTLogTool")||
                //wangbo add
                pinfo.packageName.equals("org.codeaurora.bluetooth.bttestapp")||
                pinfo.packageName.equals("com.quicinc.cne.settings") ||
                pinfo.packageName.equals("com.qualcomm.embms")||
                pinfo.packageName.equals("com.qualcomm.embmstest")||
                pinfo.packageName.equals("com.qualcomm.qti.modemtestmode")||
                pinfo.packageName.equals("com.android.MultiplePdpTest") ||
                pinfo.packageName.equals("com.qualcomm.qualcommsettings") ||
                pinfo.packageName.equals("org.codeaurora.snapcam") ||
                pinfo.packageName.equals("com.qualcomm.qti.app")||
                pinfo.packageName.equals("com.android.development")||
                pinfo.packageName.equals("com.android.quicksearchbox")||
                pinfo.packageName.equals("com.app.OpenUsbDebug")||
                pinfo.packageName.equals("com.tool.tcpdumptool")
            ) {
                Program  pr = new Program();
                pr.setIcon(pi.getInfo(pinfo.packageName).loadIcon(pm));
                if(pinfo.packageName.equals("com.app.APLogSetting")){
                    pr.setName("APLog Setting");
                }else if(pinfo.packageName.equals("com.app.LogTool")){
                    pr.setName("Save Log");
                }else if(pinfo.packageName.equals("com.app.BTLogTool")){
                    pr.setName("BTLog Setting"); //zhangjuan add 2015/02/14
                }else if(pinfo.packageName.equals("com.qualcomm.qualcommsettings")){
                    pr.setName("QualcommSettings"); //wangbo add 2015/03/03
                }else if(pinfo.packageName.equals("com.app.OpenUsbDebug")){
                    pr.setName("USB Debug Setting"); //zhangxiaohui add 2015/03/25
                }else if (pinfo.packageName.equals("com.tool.tcpdumptool")) {
                    pr.setName("TcpdumpLog Setting"); //hanliwei add
                }else if(pinfo.packageName.equals("com.qualcomm.embmstest")){
                    if("eng".equals(SystemProperties.get("ro.build.type"))){
                        pr.setName(pi.getInfo(pinfo.packageName).loadLabel(pm).toString());
                    }else{
                        continue;
                    }
                }else{
                    pr.setName(pi.getInfo(pinfo.packageName).loadLabel(pm).toString());
                }
                if(pi.getResolveInfo(pinfo.packageName)!=null) {

                    pr.setActivityName(pi.getResolveInfo(pinfo.packageName).activityInfo.name);
                    pr.setPackageName(pi.getInfo(pinfo.packageName).packageName);
                    list.add(pr);
                    Collections.sort(list, new Comparator<Program>() {
                         public int compare(Program arg0, Program arg1) {
                             return arg0.getPackageName().compareTo(arg1.getPackageName());
                           }
                    });
                }

             }
        }
        return list;
    }
    protected Intent activityIntent(String packageName2, String componentName) {
        Intent result = new Intent();
        result.setClassName(packageName2, componentName);
        return result;
    }

}
