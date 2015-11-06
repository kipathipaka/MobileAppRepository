package com.bpatech.trucktracking.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Anita on 10/29/2015.
 */
public class UpdateLocationService extends Service

{
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;
    SessionManager session;
    Location location;
    Double latitude;
    Double longitude;
    String locationval;
    String responsevalue;
    MySQLiteHelper db;
    List<User>userlist;
    User user;
    AddUserObjectParsing obj;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10
    String userphoneno;
    Request request;
    Handler mhandler;
    // The minimum time beetwen updates in milliseconds 15 * 60 * 1000.
    private static final long MIN_TIME_BW_UPDATES = 0;

    /*public UpdateLocationService() {
        super("HelloService");
    }
*/
    @Override
    public void onCreate() {
        mhandler = new Handler();
        request = new Request(getBaseContext());
        session = new SessionManager(getApplicationContext());
        db = new MySQLiteHelper(getApplicationContext());
        obj = new AddUserObjectParsing();
        user=new User();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userlist=new ArrayList<User>();
        onHandleIntent(intent);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* @Override*/
    protected void onHandleIntent(Intent intent) {

        mhandler.postDelayed(ToastRunnable, 15 * 60 * 1000);

    }

    final Runnable ToastRunnable = new Runnable() {
        public void run() {
            userlist.addAll(db.getOwnerphoneno());
            if(userlist!=null && userlist.size()>0) {
                userphoneno=userlist.get(0).getPhone_no();
            }
            getLocation();
            updateGPSCoordinates();
             new UpdateLocationApi().execute("", "", "");


        }

    };


    public Location getLocation() {
        try {

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText(getApplicationContext(), "Location is not enabled.. Please check", Toast.LENGTH_SHORT).show();
            }
            else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    //updateGPSCoordinates();
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                }

                                @Override
                                public void onProviderEnabled(String provider) {

                                }

                                @Override
                                public void onProviderDisabled(String provider) {

                                }
                            });

                    Log.d("Network", "Network");

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                       // updateGPSCoordinates();
                    }

                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {

                                    }

                                    @Override
                                    public void onStatusChanged(String provider, int status, Bundle extras) {

                                    }

                                    @Override
                                    public void onProviderEnabled(String provider) {

                                    }

                                    @Override
                                    public void onProviderDisabled(String provider) {

                                    }
                                });

                        Log.d("GPS Enabled", "GPS Enabled");

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                           //updateGPSCoordinates();
                        }
                    }
                }



            }
        } catch (Exception e) {
            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
        }
        return location;
    }

    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            String result = null;
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                  /*  StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());*/

                    locationval = address.getSubLocality().toString()+","+address.getLocality().toString();
                   // Toast.makeText(getApplicationContext(), locationval, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private class UpdateLocationApi extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            mhandler.postDelayed(ToastRunnable,15 * 60 * 1000);
        }

        protected String doInBackground(String... params) {

            try {
                if(userphoneno==null) {
                    responsevalue = null;
                }else {

                    List<NameValuePair> updatelocationlist = new ArrayList<NameValuePair>();
                    updatelocationlist.add(new BasicNameValuePair("driver_phone_number",userphoneno));
                    updatelocationlist.add(new BasicNameValuePair("location", locationval));
                    updatelocationlist.add(new BasicNameValuePair("latitude", latitude.toString()));
                    updatelocationlist.add(new BasicNameValuePair("longitude", longitude.toString()));
                    HttpResponse response = request.requestPostType(
                            ServiceConstants.UPDATE_LOCATION, updatelocationlist, ServiceConstants.BACKGROUND_BASE_URL);
                    responsevalue = "" + response.getStatusLine().getStatusCode();
                    if (response.getStatusLine().getStatusCode() == 200) {
                        new UpdateMytripDetail().execute("", "", "");
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();

            }

            return responsevalue;

        }
    }



    private class UpdateMytripDetail extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            //  progressBar.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(String... params) {

            try {
                ArrayList<AddTrip> updatetripdetails=new ArrayList<AddTrip>();


                    String Gettrip_url = ServiceConstants.GET_TRIP + session.getPhoneno();
                    HttpResponse response = request.requestGetType(Gettrip_url, ServiceConstants.BASE_URL);

                    responsevalue = "" + response.getStatusLine().getStatusCode();
                    if (response.getStatusLine().getStatusCode() == 200) {
                        JSONArray responsejSONArray = request.responseArrayParsing(response);
                        GetMytripListParsing mytripListParsing = new GetMytripListParsing();
                        updatetripdetails.addAll(mytripListParsing.getmytriplist(responsejSONArray));
                        session.setAddtripdetails(updatetripdetails);


                    }



            } catch (Exception e) {

                e.printStackTrace();

            }

            return responsevalue;

        }
    }


}