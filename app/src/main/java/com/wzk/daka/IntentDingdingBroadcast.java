package com.wzk.daka;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class IntentDingdingBroadcast extends BroadcastReceiver {
    public static final String ACTION = "com.intent.dingding.action";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.setAction(AlarmService.ACTION_COMPLETE_LAUNCHER);
        serviceIntent.putExtra(AlarmService.CANCEL_NOTIFICATION, true);
        boolean auto = intent.getBooleanExtra(AlarmService.AUTO, false);
        serviceIntent.putExtra(AlarmService.AUTO, auto);
        context.startForegroundService(serviceIntent);

        Log.i("IntentDingdingBroadcast", "onReceive: " + auto);

        Intent intentDingding = createDingdingIntent();
        context.startActivity(intentDingding);
    }

    private Intent createDingdingIntent() {
        String packageStr = "com.alibaba.android.rimet";
        String launcher = "com.alibaba.android.rimet.biz.SplashActivity";
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = new ComponentName(packageStr, launcher);
        intent.setComponent(componentName);
        return intent;
    }
}
