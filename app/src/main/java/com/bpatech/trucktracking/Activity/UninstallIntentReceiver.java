package com.bpatech.trucktracking.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Anita on 10/15/2015.
 */
public class UninstallIntentReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if(packageNames!=null){
            for(String packageName: packageNames){
                Toast.makeText(context, "hellloooooooooooo", Toast.LENGTH_SHORT).show();
                if(packageName!=null && packageName.equals("com.bpatech.trucktracking")){
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    new ListenActivities(context).start();

                }
            }
        }
    }

}