package com.bpatech.trucktracking.Service;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
    String locationVal;
    //String provider="test";
    String responsevalue;
    MySQLiteHelper db;
    List<User> userlist;
    User user;
    AddUserObjectParsing obj;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10
    String userphoneno;
    Request request;
    Handler mhandler;
    private Context context;
    StringBuilder fullAddress;
    HttpResponse response;
    // The minimum time beetwen updates in milliseconds 15 * 60 * 1000.
    private static final long MIN_TIME_BW_UPDATES =20 * 60 * 1000;


    @Override
    public void onCreate() {
        mhandler = new Handler();
        request = new Request(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        db = new MySQLiteHelper(getApplicationContext());
        obj = new AddUserObjectParsing();
        user = new User();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userlist = new ArrayList<User>();
        getLocation();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Location getLocation() {
        try {

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            try {
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

            } catch (Exception ex) {
            }

            try {
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            // getting network status
           /* Criteria crta = new Criteria();
            crta.setAccuracy(Criteria.ACCURACY_FINE);
            crta.setAltitudeRequired(false);
            crta.setBearingRequired(false);
            crta.setCostAllowed(true);
            crta.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(crta, true);
            System.out.println("++++++++++++++++++++++++++++++++++provider+++++++++++++++++++++++++++"+provider);*/
            if (!isGPSEnabled && !isNetworkEnabled) {
                locationEnable_popup();
               /* Intent intent = new Intent(this.getApplicationContext(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);*/
                //Toast.makeText(getApplicationContext(), "Location is not enabled.. Please check", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //System.out.println("++++++++++++++++++++++++++++++++++isNetworkEnabled+++++++++++++++++++++++++++"+isNetworkEnabled);
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    // System.out.println("++++++++++++++++++++++isNetworkEnabled++++++++++++location onchange+++++++++++++++++++++++++++");
                                    //updateGPSCoordinates(location);
                                    // new UpdateLocationApi().execute("", "", "");
                                    //Toast.makeText(getApplicationContext(), location.getLatitude()+""+location.getLongitude(), Toast.LENGTH_SHORT).show();
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
                        updateGPSCoordinates(location);
                    }

                }
                if (isGPSEnabled) {
                    //System.out.println("++++++++++++++++++++++++++++++++++isGPSEnabled+++++++++++++++++++++++++++"+isGPSEnabled);
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        //System.out.println("++++++++++++++++++++++isGPSEnabled++++++++++++location onchange+++++++++++++++++++++++++++");
                                        // updateGPSCoordinates(location);
                                        //System.out.println("++++++++++++++++++++++isGPSEnabled++++++++++++location onchange+++++++++++++++++++++++++++");
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
                            updateGPSCoordinates(location);
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

    public void updateGPSCoordinates(Location updateLocation) {
        if (updateLocation != null) {
            latitude = updateLocation.getLatitude();
            longitude = updateLocation.getLongitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);

                   fullAddress = new StringBuilder();

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        if(address.getAddressLine(i)!=null) {
                            fullAddress.append(address.getAddressLine(i)).append(",");
                        }
                    }
                    /*sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());*/
                    if (address.getSubLocality() == null || address.getLocality() == null) {
                        locationVal = null;
                    } else {
                        locationVal = address.getSubLocality().toString() + "," + address.getLocality().toString();
                        //Toast.makeText(getApplicationContext(), locationVal, Toast.LENGTH_SHORT).show();
                        new UpdateLocationApi().execute("", "", "");
                    }
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

        }

        protected String doInBackground(String... params) {

            try {
                //  System.out.println("++++++++++++++++++++++++++++++++++userphoneno+++++++++++++++++++++++++++" +
                //session.getPhoneno()+latitude.toString()+longitude.toString());
                if (session.getPhoneno() == null || latitude.toString() == null || longitude.toString() == null || locationVal.toString() == null) {
                    responsevalue = "noResult";
                } else {
                    //System.out.println("++++++++++++++++++++++++++++++++++userphoneno+++++++++++++++++++++++++++" + session.getPhoneno());
                    List<NameValuePair> updatelocationlist = new ArrayList<NameValuePair>();
                    updatelocationlist.add(new BasicNameValuePair("driver_phone_number", session.getPhoneno()));
                    updatelocationlist.add(new BasicNameValuePair("location",locationVal));
                   updatelocationlist.add(new BasicNameValuePair("fullAddress",fullAddress.toString()));
                    updatelocationlist.add(new BasicNameValuePair("latitude", latitude.toString()));
                    updatelocationlist.add(new BasicNameValuePair("longitude", longitude.toString()));
                    response = request.requestLocationServicePostType(
                            ServiceConstants.UPDATE_LOCATION, updatelocationlist, ServiceConstants.BASE_URL);
                    responsevalue = "" + response.getStatusLine().getStatusCode();
                    //System.out.println("++++++++++++++++++++++++++++++++++response+eee++++++++++++++++++++++++++"+response.getStatusLine().getStatusCode());
                }
            } catch (Exception e) {

                e.printStackTrace();

            }

            return responsevalue;

        }
    }


    public void locationEnable_popup() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptsView = inflater.inflate(R.layout.location_enable_popup, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(this.getApplicationContext()).create();

        alertDialog.setView(promptsView);

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();

        Button textbutton = (Button) promptsView.findViewById(R.id.btnYes);

        textbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                alertDialog.dismiss();

            }

        });
        Button textbutton1=(Button)promptsView.findViewById(R.id.btnNo);
        textbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {

                alertDialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 5000); // the timer will count 5 seconds....


    }


}