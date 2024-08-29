package com.ozalp.alarmmanager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification {

    public static String CHANNEL_IMPORTANT = "IMPORTANT";
    public static String CHANNEL_DEFAULT = "DEFAULT";

    public static void createNotificationChannel(Context context, String channelID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Bildirim Kanalını oluştur.
            CharSequence name;
            if (channelID.equals(CHANNEL_DEFAULT)) {
                name = context.getString(R.string.CHANNEL_DEFAULT);
            } else {
                name = context.getString(R.string.CHANNEL_IMPORTANT);
            }
            // String descriptionText = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelID, name, importance);
            mChannel.setDescription("Bildirimler");

            // Kanalı sistemle kaydet. Importance veya diğer bildirim davranışlarını daha sonra değiştiremezsiniz.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    public static void createNotification(Context context,
                                          Intent intent,
                                          String title,
                                          String content,
                                          String channelID) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Android 7 öncesi için uyumluluk
                .setContentIntent(pendingIntent); // Tıklandığında nereye gidecek

        Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        if (channelID.equals(CHANNEL_IMPORTANT)) {
            builder.setAutoCancel(true)
                    .setOngoing(false)
                    .addAction(R.drawable.ic_launcher_foreground, "Durdur", actionPendingIntent);
        } else {
            builder.setAutoCancel(false)
                    .setOngoing(true);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Eksik izinleri istemek için ActivityCompat#requestPermissions çağrısını düşünün
            // ve ardından izin verildiğinde durumu ele almak için
            // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // metodunu override edin. Detaylar için ActivityCompat#requestPermissions belgesine bakın.

            return;
        }

        // notificationId her bildirim için tanımlanması gereken benzersiz bir int'dir.
        notificationManagerCompat.notify(0, builder.build());
    }
}
