package com.wjs.updatelib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

public class Util {
    public static int getVerCode(Context context)
    {
        int verCode=-1;
        try
        {
            PackageManager manager=context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            verCode= packageInfo.versionCode;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return verCode;
    }
    public static String getVerName(Context context)
    {
        String verName="";
        try
        {
            PackageManager manager=context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            verName = packageInfo.versionName;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return verName;
    }
    public static String getPackageName(Context context)
    {
        String verName="";
        try
        {
            PackageManager manager=context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            verName = packageInfo.packageName;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return verName;
    }
    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo.loadLabel(context.getPackageManager()).toString();
    }
    public static int getAppIcon(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo.icon;
    }
    public static boolean isNotNull(String str1) {
        return str1 != null && !str1.equalsIgnoreCase("");
    }
}
