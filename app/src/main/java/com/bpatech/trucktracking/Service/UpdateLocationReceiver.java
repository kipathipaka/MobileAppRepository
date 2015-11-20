
        package com.bpatech.trucktracking.Service;

        import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
        import android.widget.Toast;

        /**
 * Created by Anita on 11/13/2015.
 */
public class UpdateLocationReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(final Context context, Intent intent) {
         //Toast.makeText(context, "Reciverrrrrrrrrrr: ", Toast.LENGTH_SHORT).show();

        context.startService(new Intent(context, UpdateLocationService.class));

         //System.out.println("++++++++++++++++++++++++++++++++++Reciverrrrrrrrrrr+++++++++++++++++++++++++++");
        // This method is called when this BroadcastReceiver receives an Intent broadcast.
        //Toast.makeText(context, "Reciverrrrrrrrrrr: ", Toast.LENGTH_SHORT).show();
    }
}
