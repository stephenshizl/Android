package com.tools.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import android.R.menu;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class BaseMenu  {
    public class MenuData {
        String menuName;
        String activityString;
        String packageName;
        int state = -1;
        public String getMenuName() {
            return menuName;
        }
        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }
        public String getActivityString() {
            return activityString;
        }
        public void setActivityString(String activityString) {
            this.activityString = activityString;
        }
        public String getPackageName() {
            return packageName;
        }
        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
        
        public void setState(int state) {
            this.state = state;
        }
        public int getState() {
            return state;
        }
    }
    public static ArrayList<MenuData> menuDatas = new ArrayList<MenuData>();
    int menu_config = R.raw.menu_config;
    int auto_menu_config = R.raw.auto_menu_config;
    public Context context;
    public BaseMenu(Context context) {
        this.context = context; 
    }
    public ArrayList<MenuData> initMenu(Boolean flag) {
        menuDatas.clear();
        //File outFile = new File(MENU_CONFIG);
        /*String languageString = Locale.getDefault().getLanguage();
       
        if(languageString.equals("zh") || flag) {
            menu_config = R.raw.menu_config_chinese;
        }
        */
        InputStream outFile1 = context.getResources().openRawResource(menu_config);
        if (outFile1.equals(null)) {
            return null;
        }
        else {
            return readConfigFile(outFile1);
        }
    }
        public ArrayList<MenuData> initAutoMenu(Boolean flag) {
        menuDatas.clear();
       /* String languageString = Locale.getDefault().getLanguage();
        if(languageString.equals("zh") || flag) {
            auto_menu_config = R.raw.auto_menu_config_chinese;
        }
        */
        InputStream outFile2 = context.getResources().openRawResource(auto_menu_config);
        if (outFile2.equals(null)) {
            return null;
        }
        else {
            return readConfigFile(outFile2);
        }
    }
    private ArrayList<MenuData> readConfigFile(InputStream file) {
        BufferedReader br = null;
        int isErro = 1;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(file, "GB2312");
            br = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = br.readLine()) != null) {
                if(parseLine(line) == -1) {
                    isErro = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if(isErro != -1) {
            return menuDatas;
        }
        else {
            return null;
        }
    }

//    private void addModule(ArrayList<MenuData> menuDatas2) {
//        
//        
//    }
    private int parseLine(String line) {
        line = line.trim();
        MenuData output = new MenuData();
        try {
            if (line.startsWith("[") && line.endsWith("]")) {
                String nameString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                String[] parseName = nameString.split("=");
                if(parseName[0].equals("parentname")) { //parentmenu
                    output.setActivityString("");
                    output.setPackageName("");
                }
                else { //childmenu
                    String[] parseActivity = parseName[2].split("/");
                    output.setPackageName(parseActivity[0].trim());
                    output.setActivityString(parseActivity[1].trim());
                }
                String[] parseMenu = parseName[1].split(",");
                output.setMenuName(parseMenu[0].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        if (output.getMenuName() != null) {
            menuDatas.add(output);
        }
        return 1;
    }
    
    public ArrayList<MenuData> getMenuDatas() {
        return menuDatas;
    }
    public void setMenuDatas(ArrayList<MenuData> menuDatas) {
        this.menuDatas = menuDatas;
    }
    
    
}

