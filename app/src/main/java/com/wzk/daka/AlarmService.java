package com.wzk.daka;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AlarmService extends Service {
    public static final String ACTION_COMPLETE_LAUNCHER = "com.action.complete.launcher";
    public static final String ACTION_CANCEL_ALARM = "com.action.cancel.alarm";
    public static final String AUTO = "auto";
    public static final String CANCEL_NOTIFICATION = "cancel_notification";
    public static final int notificationId = 0x1;
    private IntentDingdingBroadcast mBroadcast;
    private boolean mAuto;
    private long mStartTime;
    private PendingIntent mPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcast = new IntentDingdingBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentDingdingBroadcast.ACTION);
        registerReceiver(mBroadcast, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AlarmService", "onStartCommand: ");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String action = intent.getAction();
        if (ACTION_COMPLETE_LAUNCHER.equals(action)) {
            boolean cancelNotification = intent.getBooleanExtra(CANCEL_NOTIFICATION, false);
            if (cancelNotification) {
                if (!mAuto) {
                    stopForeground(true);
                    return super.onStartCommand(intent, flags, startId);
                } else {
                    //获取下一次打卡的时间
                    mStartTime = getNextDayWakeUpTime(mStartTime).getTimeInMillis();
                }
            }
        } else if (ACTION_CANCEL_ALARM.equals(action)) {
            manager.cancel(mPendingIntent);
            stopSelf();
        } else {
            mAuto = intent.getBooleanExtra(AUTO, false);
            mStartTime = intent.getLongExtra("time", 0l);
        }

        Context context = getBaseContext();
        Calendar calendar = getNextWakeUpTime(mStartTime);
        Notification notification = NotificationHelper.makeNotification(context, "定时打卡", "下次打卡时间:" + formatTime(calendar.getTime()), notificationId);
        startForeground(notificationId, notification);

        Intent launchIntent = new Intent(IntentDingdingBroadcast.ACTION);
        launchIntent.putExtra("auto", mAuto);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, launchIntent, PendingIntent.FLAG_ONE_SHOT);
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mPendingIntent);
        return START_STICKY;
    }

    private String formatTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(date);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Calendar getNextWakeUpTime(long millSeconds) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(millSeconds);
        calendar.add(Calendar.MINUTE, -randomMinute());
        return calendar;
    }

    public static Calendar getNextDayWakeUpTime(long millSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millSeconds);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return calendar;
    }

    private static int randomMinute() {
        Random random = new Random();
        return random.nextInt(20);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcast);
    }
}
