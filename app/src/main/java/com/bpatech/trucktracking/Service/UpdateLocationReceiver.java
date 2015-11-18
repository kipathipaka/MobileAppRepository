package com.bpatech.trucktracking.Service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bpatech.trucktracking.R;

/**
 * Created by Anita on 11/13/2015.
 */
public class UpdateLocationReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(final Context context, Intent intent) {
       // Toast.makeText(context, "Reciverrrrrrrrrrr: ", Toast.LENGTH_SHORT).show();

            context.startService(new Intent(context, UpdateLocationService.class));

       // System.out.println("++++++++++++++++++++++++++++++++++Reciverrrrrrrrrrr+++++++++++++++++++++++++++" );
        // This method is called when this BroadcastReceiver receives an Intent broadcast.
        //Toast.makeText(context, "Reciverrrrrrrrrrr: ", Toast.LENGTH_SHORT).show();
    }
}
