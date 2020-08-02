package com.wjs.updatelib;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageNotification {
    private NotificationManager mNotificationManager;
    private  RemoteViews remoteViews;
    private NotificationCompat.Builder mBuilder;
    private Context context;
    private int drawable;
    private final int NOTIFYCATIONID = 812330500;
    private final String NOTIFACATIONCHANNELID="ID812330500";
    private final String NOTOFACATIONCHANNELNAME = "NAME812330500";
    private String name;
    private String action;

    public MessageNotification(String name, Context context,String action, int drawable) {
        this.context = context;
        this.drawable = drawable;
        this.name = name;
        this.action=action;
        this.initNotifycation();
    }
    public void updateNotification(String notifacationText) {
        this.mBuilder.setContentText("重要消息");
        this.mBuilder.setContentTitle("提示");
        remoteViews.setTextViewText(R.id.message,notifacationText);
        Notification mNotification = this.mBuilder.build();
        mNotification.flags = 2;
        this.mBuilder.setDefaults(Notification.DEFAULT_ALL);
        this.mNotificationManager.notify(NOTIFYCATIONID, mNotification);
    }
    public void updateNotification(final String icon, final String notifacationText) {
        new Thread(){
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection= (HttpURLConnection) new URL(icon).openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedOutputStream outputStream= new BufferedOutputStream(new FileOutputStream(context.getExternalFilesDir("usericon")+File.separator+"icon.png"));
                    int len;
                    byte[] data=new byte[1024];
                    while((len=inputStream.read(data))!=-1){
                        outputStream.write(data,0,len);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    final Bitmap bitmap= BitmapFactory.decodeFile(context.getExternalFilesDir("usericon")+File.separator+"icon.png");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            remoteViews.setImageViewBitmap(R.id.app_icon,bitmap);
                            updateNotification(notifacationText);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void initNotifycation() {
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);

        remoteViews = new RemoteViews(context.getPackageName(),R.layout.message_notifacation_layout);
        remoteViews.setTextViewText(R.id.messagetitle,Util.getAppName(context));
        remoteViews.setImageViewResource(R.id.app_icon, Util.getAppIcon(context));
        //remoteViews.setOnClickPendingIntent(R.id.openActivity,pendingIntent);

        mNotificationManager = (NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this.context,NOTIFACATIONCHANNELID);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setSmallIcon(Util.getAppIcon(context));
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);//锁屏显示
        mBuilder.setContentIntent(mPendingIntent);//点击后跳转意图
        mBuilder.setAutoCancel(true);//点击后自动消失

        //this.mBuilder.setContent(remoteViews);//包含smaillNotifacationView和bigNotifacationView
        this.mBuilder.setCustomContentView(remoteViews);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFACATIONCHANNELID, NOTOFACATIONCHANNELNAME, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            this.mBuilder.setChannelId(NOTIFACATIONCHANNELID);
        }
    }
    public void cancleNotifacation() {
        this.mNotificationManager.cancel(NOTIFYCATIONID);
    }
}
