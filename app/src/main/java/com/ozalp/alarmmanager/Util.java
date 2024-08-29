package com.ozalp.alarmmanager;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;

public class Util {

    // alarm kurulduğunda depolama alanına kaydediyoruz ki yeniden başlatıldığında alıp yeniden alarm kurulmasını sağlayabilelim.
    // birden fazla alarm varsa sqlite kullanılabilir
    public static void saveTime(Context context, String time) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("com.ozalp.alarmmanager", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("time", time);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // kaydettiğimiz alarmı burdan alabiliyoruz
    public static String savedTime(Context context) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("com.ozalp.alarmmanager", MODE_PRIVATE);
            return preferences.getString("time", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    // alarm kur
    public static void setAlarm(Context context, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // şu anki vakitten öncesini verince anında çalma problemini kaldırır
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        // alarm manager alarmı kurabildiğimiz servis
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Alarm set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        saveTime(context, "" + hour + ":" + minute);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        notification(context);
    }

    // kurduğumuz alarmı bildirimde gösteriyoruz
    public static void notification(Context context) {
        String time = savedTime(context);
        if (!time.equals("")) {
            Intent intent = new Intent(context, MainActivity.class);
            Notification.createNotification(context, intent, "Alarm is set", savedTime(context), Notification.CHANNEL_DEFAULT);
        }
    }
}
