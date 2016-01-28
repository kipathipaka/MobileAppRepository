
        package com.bpatech.trucktracking.Service;

        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
        import android.widget.Toast;

        import com.bpatech.trucktracking.Util.SessionManager;

        import timber.log.Timber;

        /**
 * Created by Anita on 11/13/2015.
 */
public class UpdateLocationReceiver extends BroadcastReceiver{
            int alarmval=0;
            SessionManager  session;
    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("++++++++++++++++++++++++++++++++++UpdateLocationReceiver+++++++++++++++++++++++++++");
         //Toast.makeText(context, "Reciverrrrrrrrrrr: ", Toast.LENGTH_SHORT).show();
        Timber.i("UpdateLocationReceiver: Inside receiver **************************");

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // Set the alarm here.
            session = new SessionManager(context);
            Timber.i("UpdateLocationReceiver: Inside receiver boot completed condition**************************");
            Timber.i("UpdateLocationReceiver:Setting alarm on reboot**************************");
           // Toast.makeText(context, "Reciverrrrrrrrrrr:setting alarm on reboot ", Toast.LENGTH_LONG).show();
            AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intentR = new Intent( context, UpdateLocationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentR, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),20 * 60 * 1000,
                    pendingIntent);
            session.setAlaramcount(1);
        }

        context.startService(new Intent(context, UpdateLocationService.class));

         //System.out.println("++++++++++++++++++++++++++++++++++Reciverrrrrrrrrrr+++++++++++++++++++++++++++");
        // This method is called when this BroadcastReceiver receives an Intent broadcast.
        //Toast.makeText(context, "Reciverrrrrrrrrrr: ", Toast.LENGTH_SHORT).show();
    }
}
