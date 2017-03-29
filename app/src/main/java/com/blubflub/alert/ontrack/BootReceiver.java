package com.blubflub.alert.ontrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Blub on 12/5/2016.
 */

public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i = new Intent(context, AlarmBroadcastReceiver.class);
        boolean isWorking = (PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_NO_CREATE) != null);


        // Set the alarm to start every midnight
        long alarm = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis())
        {
            alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY);
        } else
        {
            alarm = calendar.getTimeInMillis();
        }

        if (!isWorking)

        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, alarm,
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
