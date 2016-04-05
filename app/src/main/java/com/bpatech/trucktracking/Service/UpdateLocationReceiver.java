
        package com.bpatech.trucktracking.Service;

        import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bpatech.trucktracking.Util.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class UpdateLocationReceiver extends BroadcastReceiver{
            SessionManager  session;
            String updatedate;
    @Override
    public void onReceive(final Context context, Intent intent) {
        session = new SessionManager(context);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // Set the alarm here.

            AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intentR = new Intent( context, UpdateLocationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentR, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),20 * 60 * 1000,pendingIntent);
            session.setAlaramcount(1);
        }else {
            DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            Date date1 = new Date();
            updatedate=updateTimeFormat.format(date1).toString();

            session.setKeyLastUpdatDate(updatedate);
            context.startService(new Intent(context, UpdateLocationService.class));
        }

    }
}
