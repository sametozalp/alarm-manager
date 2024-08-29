package com.ozalp.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

// bildirimlerdeki aksiyonlara tıklanırsa burası çalışır
public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(0); // Bildirimi iptal eder

        AlarmReceiver.stopAlarm();
    }
}
