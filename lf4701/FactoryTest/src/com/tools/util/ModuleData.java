
package com.tools.util;

import com.tools.cit.TestModule;

import android.graphics.drawable.Drawable;

public class ModuleData {

    private String moduleName;
    private Drawable icon;
    private int testStatus = TestModule.OTHER;
    private String packageName;
    private String activityName;

    public ModuleData() {
    }

    public ModuleData(ModuleData moduleData) {
        moduleName = moduleData.moduleName;
        testStatus = moduleData.testStatus;
        packageName = moduleData.packageName;
        activityName = moduleData.activityName;
    }

    public ModuleData(String _moduleName) {
        moduleName = _moduleName;
    }

    public ModuleData(String _moduleName, Drawable _icon, String _packageName, String _activityName) {

    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isValid() {
        if (moduleName == null || moduleName.equals(""))
            return false;
        if (packageName == null || packageName.equals(""))
            return false;

        if (activityName == null || activityName.equals(""))
            return false;

        return true;
    }

    public void clear() {
        moduleName = null;
        icon = null;
        testStatus = TestModule.OTHER;
        packageName = null;
        activityName = null;
    }
}
