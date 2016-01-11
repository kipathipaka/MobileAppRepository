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

        final String LOGGLY_TOKEN = "93781e47-4b58-4901-96f0-84137b938cf3";
        Timber.plant(new LogglyTree(LOGGLY_TOKEN));
    }
}