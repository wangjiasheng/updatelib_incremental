package com.wjs.updatelib;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;

public class InstallApkNotification {
    private NotificationManager mNotificationManager;
    private Builder mBuilder;
    private Context context;
    private int drawable;
    private final int NOTIFYCATIONID = 314232332;
    private final String NOTIFACATIONCHANNELID="ID314232332";
    private final String NOTOFACATIONCHANNELNAME = "NAME314232332";
    private String name;

    public InstallApkNotification(String name, Context context, int drawable) {
        this.context = context;
        this.drawable = drawable;
        this.name = name;
        this.initNotifycation();
    }

    private void initNotifycation() {
        this.mNotificationManager = (NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new Builder(this.context,NOTIFACATIONCHANNELID);
        this.mBuilder.setWhen(System.currentTimeMillis());
        this.mBuilder.setSmallIcon(this.drawable);
        this.mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),drawable));
        this.mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        this.mBuilder.setLights(0,0,0);
        this.mBuilder.setTicker("下载中...");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFACATIONCHANNELID, NOTOFACATIONCHANNELNAME, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            this.mBuilder.setChannelId(NOTIFACATIONCHANNELID);
        }
    }

    public void updateNotification(int progress) {
        Notification mNotification = this.mBuilder.build();
        mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        this.mBuilder.setProgress(100, progress, false);
        this.mBuilder.setContentText("下载中...").setContentTitle(this.name);
        this.mNotificationManager.notify(NOTIFYCATIONID, mNotification);
    }

    public void cancleNotifacation() {
        this.mNotificationManager.cancel(NOTIFYCATIONID);
    }
}
