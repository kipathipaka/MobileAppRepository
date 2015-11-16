package com.bpatech.trucktracking.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Anita on 11/13/2015.
 */
public class UpdateLocationReceiver extends BroadcastReceiver{

    public UpdateLocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when this BroadcastReceiver receives an Intent broadcast.
        Toast.makeText(context, "Reciver: " + intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
