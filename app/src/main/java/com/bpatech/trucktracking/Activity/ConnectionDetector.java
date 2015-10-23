package com.bpatech.trucktracking.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Anita on 10/14/2015.
 */
public class ConnectionDetector {
    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();

    }

}
