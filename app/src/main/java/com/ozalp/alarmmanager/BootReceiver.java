package com.ozalp.alarmmanager;

import static com.ozalp.alarmmanager.Util.setAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // uygulama yeniden başlatılırsa alarmı yeniden kur (alarmı kaydediyoruz her kuruşumuzda silinmiyor)

        String savedTime = Util.savedTime(context);
        if (!savedTime.equals("")) {

            int indexOf = savedTime.indexOf(":");
            int hour = Integer.parseInt(savedTime.substring(0, indexOf));
            int minute = Integer.parseInt(savedTime.substring(indexOf+1));

            setAlarm(context, hour, minute);

        }

    }
}
