package com.blubflub.alert.ontrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmBroadcastReceiver extends BroadcastReceiver
{
    private DatabaseHelper myDb;
    public static String NOTIFICATION = "Let's have another productive day!";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = preferences.edit();
        boolean notificationsOn = preferences.getBoolean("notif_on",true);

        if(notificationsOn)
        {
            showNotification(context);
        }

        //database
        myDb = new DatabaseHelper(context);

        //set weights based on recent performance
        setWeights(preferences, ed);

        //add today's data into database
        addData(preferences);

        //change progressbar and num to 0;
        ed.putInt("daily_total_minutes", 0);
        ed.putInt("todays_prating", 0);

        //reset times
        ed.putInt("time1", 0);
        ed.putInt("time2", 0);
        ed.putInt("time3", 0);
        ed.putInt("time4", 0);
        ed.putInt("time5", 0);
        ed.putInt("time6", 0);
        ed.apply();
    }

    private void showNotification(Context context)
    {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.calender)
                        .setContentTitle("OnTrack")
                        .setContentIntent(contentIntent)
                        .setContentText(NOTIFICATION);
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    public void addData(SharedPreferences pref)
    {
        //format today's date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = sdf.format(date);

        ContentValues cv = new ContentValues();
        SQLiteDatabase db = myDb.getWritableDatabase();
        cv.put("DATE", dateString);

        for (int i = 1; i <= 6; i++)
        {
            //check to see if column has data
            if (myDb.getGoalMinuteAverage(i) != 0) //if column has data
            {
                cv.put("GOAL" + i, pref.getInt("time" + i, 0));
            } else
            {
                if (pref.getInt("time" + i, 0) != 0)
                {
                    cv.put("GOAL" + i, pref.getInt("time" + i, 0));
                } else
                {
                    //check if goal is enabled
                    if (pref.getFloat("weight" + i, 0) != 0)
                    {
                        cv.put("GOAL" + i, pref.getInt("time" + i, 0));
                    } else
                    {
                        cv.put("GOAL" + i, (String) null);
                    }
                }
            }
        }

        cv.put("TOTAL_MINUTES", pref.getInt("daily_total_minutes", 0));
        cv.put("PRATING", pref.getInt("todays_prating", 0));


        db.insert("prating_table", null, cv);

    }

    private void setWeights(SharedPreferences pref, SharedPreferences.Editor ed)
    {
        for (int i = 1; i <= 6; i++)
        {
            float prevWeight = pref.getFloat("weight" + i, 0);
            //check if goal is enabled
            if (pref.getFloat("weight" + i, 0) != 0)
            {
                //if time is inputted keep weight
                if (pref.getInt("time" + i, 0) != 0)
                {
                    //
                    if (pref.getFloat("weight" + i, 0) > 1)
                    {
                        ed.putFloat("weight" + i, prevWeight - 1);
                    }
                    else
                    {
                        ed.putFloat("weight" + i, prevWeight);
                    }
                }
                //otherwise increase weight by 1.
                else
                {
                    ed.putFloat("weight" + i, prevWeight + 1);
                }
            }

        }
    }
}