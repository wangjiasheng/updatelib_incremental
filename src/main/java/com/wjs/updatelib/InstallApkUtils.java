package com.wjs.updatelib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class InstallApkUtils {
    public static void installApk(String urlPath, Context context) {
        File apkFile = new File(urlPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent apkIntent = new Intent(Intent.ACTION_VIEW);
            apkIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(context, "com.wjs.updatelib.provider", apkFile);
            apkIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            context.startActivity(apkIntent);
            //兼容8.0
        }else{
            Intent apkIntent = new Intent(Intent.ACTION_VIEW);
            apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(apkFile);
            apkIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(apkIntent);
        }
    }
}
