package com.bpatech.trucktracking.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.UpdateLocationDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anita on 2/16/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    MySQLiteHelper db;
    List<UpdateLocationDTO> locationDBlist;
    Context contextobj;
    @Override
    public void onReceive(final Context context, final Intent intent) {
       // Toast.makeText(context, "network fluctuating", Toast.LENGTH_LONG).show();
        contextobj=context;
        db = new MySQLiteHelper(context.getApplicationContext());
        locationDBlist = new ArrayList<UpdateLocationDTO>();
        locationDBlist.addAll(db.getTracktripDetails());
       // System.out.println("++++++++++++++++++++++++++++++network change+++++++++++++++++++++++++++");
        if (isConnectingToInternet()==true) {
            Toast.makeText(context, "network fluctuating", Toast.LENGTH_LONG).show();
            if (locationDBlist != null && locationDBlist.size() > 0) {
                context.startService(new Intent(context, UpdateLoctionByNetworkChange.class));
            }
        }

    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager)contextobj.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivity.getActiveNetworkInfo();

        return info != null && info.isConnected();

    }
}
