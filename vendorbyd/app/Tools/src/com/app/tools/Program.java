package com.app.tools;

import android.graphics.drawable.Drawable;
/*
*
*    Date             Author         CR/PR ID             Headline
*
*
*
*/
public class Program  {

    private Drawable icon;
    private String name;
    private String packageName;
    private String activityName;

    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public String getActivityName() {
        return activityName;
    }

}
