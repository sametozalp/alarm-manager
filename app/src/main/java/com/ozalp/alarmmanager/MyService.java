package com.ozalp.alarmmanager;

import static com.ozalp.alarmmanager.Util.setAlarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Uygulama zorla durdurmada denedim burayı yeni apilerde çalışmıyor artık

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        String savedTime = Util.savedTime(getApplicationContext());
        if (!savedTime.equals("")) {

            int indexOf = savedTime.indexOf(":");
            int hour = Integer.parseInt(savedTime.substring(0, indexOf));
            int minute = Integer.parseInt(savedTime.substring(indexOf+1));

            setAlarm(getApplicationContext(), hour, minute);

        }
    }
}
