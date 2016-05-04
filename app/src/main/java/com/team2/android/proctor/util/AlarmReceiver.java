
package com.team2.android.proctor.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.team2.android.proctor.R;
import com.team2.android.proctor.ui.LoginActivity;
import com.team2.android.proctor.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by roger on 4/25/2016.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    int notificationID=0;
    AlarmManager alarmManager;
    AlarmManager alarmManager1;
    ArrayList<AlarmManager> amgr=new ArrayList<AlarmManager>();
    @Override
    public void onReceive(final Context context, Intent intent) {
        int req= intent.getIntExtra("reqcode", -1);
        String course = intent.getExtras().getString("course");
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Proctor");
        mBuilder.setContentText("Time to " + course + " class!!");
        mBuilder.setSmallIcon(R.mipmap.proctor_symbol);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        System.out.println("into receive alarm receiver setting repeat alarm");
        Intent notificationIntent = new Intent(context,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context.getApplicationContext(),req, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent intent1 = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent1.putExtra("reqcode",req);
        intent1.putExtra("course",course);
        boolean alarmUp = (PendingIntent.getBroadcast(context.getApplicationContext(), req, intent1, PendingIntent.FLAG_NO_CREATE) != null);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), req, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 7* 24* 60 *60* 1000, pendingIntent);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationID = req;
        mNotificationManager.notify(req, mBuilder.build());
    }

    public void stopAlarm(Context context,ArrayList<PendingIntent> intentArrayList) {
        alarmManager =(AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        for(int i=0;i<intentArrayList.size();i++)
        {
            alarmManager.cancel(intentArrayList.get(i));
            intentArrayList.get(i).cancel();
        }

    }
}
