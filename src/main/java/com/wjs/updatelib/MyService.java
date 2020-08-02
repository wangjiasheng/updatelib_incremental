package com.wjs.updatelib;

import android.app.IntentService;
import android.content.Intent;

import com.nothome.delta.GDiffPatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
public class MyService extends IntentService {
    public static final String URLPath = "URL";
    public static final String INCREMENTAL="INCREMENTAL";
    InstallApkNotification notificationControl;
    public MyService()
    {
        super("MyService");
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        String urlPath=intent.getStringExtra(URLPath);
        boolean increment=intent.getBooleanExtra(INCREMENTAL,false);
        String filePath=null;
        filePath=createFilePath("Updata.patch");
        if(Util.isNotNull(filePath))
        {
            notificationControl = new InstallApkNotification(Util.getAppName(this),this, Util.getAppIcon(this));
            if(downApk(urlPath,filePath)) {
                String installapk = createApk(increment, getPackageCodePath(), filePath);
                if(installapk!=null)
                {
                    chmod(installapk);
                    InstallApkUtils.installApk(installapk, this);
                }
            }
            notificationControl.cancleNotifacation();
        }
    }
    public String createFilePath(String name)
    {
        String filePath = null;
        File file = getExternalFilesDir("/");
        if (file != null) {
            filePath = file.getAbsolutePath() + File.separator + name;
        } else {
            filePath = getFilesDir().getAbsolutePath() + File.separator + name;
        }
        return filePath;
    }
    public boolean downApk(String urlPath,String savePath)
    {
        boolean downsucess=false;
        InputStream stream = null;
        FileOutputStream fos = null;
        try
        {
            URL e = new URL(urlPath);
            URLConnection openConnection = e.openConnection();
            openConnection.setDoInput(true);
            openConnection.setDoOutput(false);
            //openConnection.setRequestProperty("Cookie", this.cookie);
            stream = openConnection.getInputStream();
            int available = openConnection.getContentLength();
            int totle = 0;
            fos = new FileOutputStream(new File(savePath));
            byte[] by = new byte[102400];
            boolean len = true;

            int current;
            while((current = stream.read(by)) != -1) {
                totle += current;
                fos.write(by, 0, current);
                notificationControl.updateNotification((int)(100.0F * (float)totle / (float)available));
            }

            fos.flush();
            downsucess=true;
        }
        catch (MalformedURLException var25) {
            var25.printStackTrace();
        } catch (IOException var26) {
            var26.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException var24) {
                    var24.printStackTrace();
                }
            }

            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException var23) {
                    var23.printStackTrace();
                }
            }

        }
        return downsucess;
    }
    public void chmod(String filepath)
    {
        String[] command = new String[]{"chmod", "777",filepath};
        ProcessBuilder builder = new ProcessBuilder(command);
        try
        {
            builder.start();
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }
    public static String createApk(boolean increment,String oldpath,String filePath)
    {
        File file=new File(filePath);
        if(filePath!=null&&!filePath.trim().equals(""))
        {
            String filepathname=filePath.substring(0,filePath.lastIndexOf("."))+".apk";
            if(increment)
            {
                try
                {
                    File installapk=mergeFile(oldpath,filePath,filepathname);
                    if(installapk.exists())
                    {
                        return filepathname;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                File installapk=new File(filepathname);
                file.renameTo(installapk);
                if(installapk.exists())
                {
                    return filepathname;
                }
            }
        }
        return null;
    }
    private static File mergeFile(final String source, final String patch, String target) throws Exception
    {
        GDiffPatcher patcher = new GDiffPatcher();
        File deffFile = new File(patch);
        File updatedFile = new File(target);
        patcher.patch(new File(source), deffFile, updatedFile);
        return updatedFile;
    }
}
