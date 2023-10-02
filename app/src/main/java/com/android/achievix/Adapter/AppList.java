package com.android.achievix.Adapter;

import android.graphics.drawable.Drawable;

public class AppList {
    private final String name;
    Drawable icon;
    private String packageName;
    private String extra;

    public AppList(String name, Drawable icon,String packageName) {
        this.name = name;
        this.icon = icon;
        this.packageName=packageName;
    }
    public AppList(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }
    public AppList(String name, String packageName,Drawable icon) {
        this.name = name;
        this.icon = icon;
        this.packageName=packageName;
    }
    public AppList(String name, String packageName,Drawable icon,String extra) {
        this.name = name;
        this.icon = icon;
        this.packageName=packageName;
        this.extra=extra;
    }

    public String getPackageName(){
        return packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getExtra() {
        return extra;
    }
}