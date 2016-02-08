package com.bpatech.trucktracking.Util;

/**
 * Created by Anita on 1/6/2016.
 */
import android.app.Application;

import com.github.tony19.timber.loggly.LogglyTree;

import timber.log.Timber;

/**
 * Created by Kiran on 1/5/2016.
 */
public class TruckIndia extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        final String LOGGLY_TOKEN = "4cd84179-cae6-4150-bb72-c984249ceedb";
        Timber.plant(new LogglyTree(LOGGLY_TOKEN));
    }
}