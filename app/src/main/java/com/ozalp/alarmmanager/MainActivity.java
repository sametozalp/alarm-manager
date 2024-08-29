package com.ozalp.alarmmanager;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import static com.ozalp.alarmmanager.Util.saveTime;
import static com.ozalp.alarmmanager.Util.savedTime;
import static com.ozalp.alarmmanager.Util.setAlarm;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.ozalp.alarmmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerPermissionLauncherForNotification(); // launcher initialize işlemleri

        setAlarmButton();
        nextAlarmButton();
        stopAlarmButton();

        // sürüm tiramisu üstündeyse bildirim gönderme izni almak zorundayız
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission();
        }

        // 2 tane bildirim kanalı oluşturdum.
        // default olan alarm ayarlandığında silinmeyen bildirim kanalı
        // important olan ise alarm çaldığında çıkan üzerine tıklandığında silinen bildirim tipi
        // ve bu kanaldaki bildirimler devreye girdiğinde önümüze pencere şeklinde düşüyor. default da pencere şeklinde düşmüyor ama panelde hep gözüküyor.
        Notification.createNotificationChannel(this, Notification.CHANNEL_DEFAULT);
        Notification.createNotificationChannel(this, Notification.CHANNEL_IMPORTANT);

        /* açıklaması sınıfın içinde, onDestroy mantığıyla çalışıyor ama bu da yemedi
            Intent serviceIntent = new Intent(this, MyService.class);
            startService(serviceIntent);
        */

    }

    private void nextAlarmButton() {
        binding.nextAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String time = savedTime(MainActivity.this);
                    if (!time.equals(""))
                        Toast.makeText(MainActivity.this, savedTime(MainActivity.this), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Bir alarm bulunamadı.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setAlarmButton() {
        binding.setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isBatteryOptimized(MainActivity.this)) {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Batarya optimizasyonuna izin vermelisiniz!").setMessage("Bildirimlerin sağlıklı çalışması için uygulamanın batarya kısıtlamasını kaldırmalısınız.").setPositiveButton("Ayarla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBatteryOptimizationSettings(MainActivity.this);
                        }
                    }).setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

                } else {

                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            setAlarm(MainActivity.this, hourOfDay, minute);

                        }
                    }, 0, 0, true);

                    timePickerDialog.show();


                }

            }
        });
    }

    private void stopAlarmButton() {
        binding.stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmReceiver.stopAlarm();
                saveTime(MainActivity.this, "");
                Toast.makeText(MainActivity.this, "Alarm is stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_DENIED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    POST_NOTIFICATIONS
            )) {
                Snackbar.make(
                        binding.getRoot(),
                        "Bildirimlerden haberdar olmak için izin vermelisiniz.",
                        Snackbar.LENGTH_INDEFINITE
                ).setAction("İzin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // izin iste
                        permissionLauncher.launch(POST_NOTIFICATIONS);
                    }
                }).show();
            } else {
                // izin iste
                permissionLauncher.launch(POST_NOTIFICATIONS);
            }
        } else {
            // işlemi yap

        }
    }

    private void registerPermissionLauncherForNotification() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            // izin verildi


                        } else {
                            // izin verilmedi
                            Toast.makeText(
                                    MainActivity.this,
                                    "Bildirim izni verilmedi. Daha sonra ayarlardan değiştirebilirsiniz.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
        );
    }

    public static void openBatteryOptimizationSettings(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            context.startActivity(intent);
        }
    }
    public static boolean isBatteryOptimized(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return pm.isIgnoringBatteryOptimizations(packageName);
        }
        // Android 6.0 (Marshmallow) ve öncesi sürümler için batarya optimizasyonu uygulanmaz
        return true;
    }


    private ActivityResultLauncher permissionLauncher;

}
