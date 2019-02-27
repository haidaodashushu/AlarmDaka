package com.wzk.daka;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class NotificationHelper {
    private static final String NOTIFICATION_CHANNEL_ID = "wzk";
    public static Notification makeNotification(Context context, String title, String content, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager, NOTIFICATION_CHANNEL_ID);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(getApplicationIcon(context));
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(NotificationManager notificationManager, String channelId) {
        NotificationChannel channel = new NotificationChannel(channelId,
                "定时打卡", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static synchronized int getApplicationIcon(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
            return applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return -1;
    }

}
