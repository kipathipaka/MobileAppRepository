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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

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
    private Context context;
    String fullAddress;
    HttpResponse response;
    // The minimum time beetwen updates in milliseconds 15 * 60 * 1000.
    private static final long MIN_TIME_BW_UPDATES =20 * 60 * 1000;


    @Override
    public void onCreate() {
        request = new Request(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        db = new MySQLiteHelper(getApplicationContext());
        obj = new AddUserObjectParsing();
        user = new User();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Service : Start Command Call");
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
            if (locationManager!=null) {
                Timber.i("locationManager  : " +locationManager);
                System.out.println("++++++++++++++++++++++++++++++++++provider+++++++++++++++++++++++++++"+locationManager);
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
                    // locationEnable_popup();
                    Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
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
                                        Timber.i("onLocationChanged  : Network Provider latlng" + location.getLatitude() +
                                                "&" + location.getLongitude());
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
                        Timber.i("Network : Network enable");
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
                            Timber.i("Service Location Manager :GPS Enabled");
                            Log.d("GPS Enabled", "GPS Enabled");

                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                updateGPSCoordinates(location);
                            }
                        }
                    }


                }
            }
        } catch (Exception e) {
            Timber.i("Error : Location"+
                    "Impossible to connect to LocationManager"+e);
            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
        }
        return location;
    }

    public void updateGPSCoordinates(Location updateLocation) {
        Timber.i("Location Service :Enter updateGPSCoordinates");
        if (updateLocation != null) {
            latitude = updateLocation.getLatitude();
            longitude = updateLocation.getLongitude();
            Timber.i("Service :updateGPSCoordinates latitude :  " + latitude + "Longitude  :" + longitude);
            new GetAddressFromJson().execute("", "", "");
           /* Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    Timber.i("Service :updateGPSCoordinates Address List :  ",address );
                //   fullAddress = new StringBuilder();

                   *//* for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        if(address.getAddressLine(i)!=null) {
                            fullAddress.append(address.getAddressLine(i)).append(",");
                        }
                    }*//*
                    *//*sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());*//*
                    if (address.getSubLocality() == null) {
                        if (address.getLocality() == null) {
                            locationVal = "null";
                            fullAddress = "null";
                        } else {
                            fullAddress = address.getLocality().toString();
                            locationVal = address.getLocality().toString();
                        }
                    } else {
                        if (address.getLocality() == null) {
                            fullAddress = address.getSubLocality().toString();
                            locationVal = address.getSubLocality().toString();
                        } else {
                            fullAddress = address.getSubLocality().toString() + "," + address.getLocality().toString();
                            locationVal = address.getLocality().toString();
                        }

                    }
                    Timber.i("Service : Current Location  "+fullAddress);
                        new UpdateLocationApi().execute("", "", "");

                }


            } catch (IOException e) {
                Timber.i("Service : "+"Update Location IOException"+e);
                e.printStackTrace();


        }*/
        }
    }


    private class UpdateLocationApi extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

        }

        protected String doInBackground(String... params) {

            try {
                Timber.i("Service APi Call :  " + latitude + "Longitude  :" + longitude);
                //  System.out.println("++++++++++++++++++++++++++++++++++userphoneno+++++++++++++++++++++++++++" +
                //session.getPhoneno()+latitude.toString()+longitude.toString());
                if (session.getPhoneno() == null || String.valueOf(latitude) == null || String.valueOf(longitude) == null || locationVal.toString() == null) {

                    responsevalue = "noResult";
                    Timber.i("Service APi Call Result  :  " + responsevalue);
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
                    Timber.i("Location update API result :"+ response.getStatusLine().getStatusCode());
                    //System.out.println("++++++++++++++++++++++++++++++++++response+eee++++++++++++++++++++++++++"+response.getStatusLine().getStatusCode());
                }
            } catch (Exception e) {
                Timber.i("Location update API  Error :"+e);
                e.printStackTrace();

            }

            return responsevalue;

        }
    }


    private class GetAddressFromJson extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

        }

        protected String doInBackground(String... params) {
            InputStream is = null;
            String result = "";
            JSONObject jsonObj=null;
            try {
                Timber.i("Location Service: "+"GetAddressFromJsonAPI");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude + ","+longitude);
                HttpResponse response = httpclient.execute(httppost);
                responsevalue = "" + response.getStatusLine().getStatusCode();
                System.out.println("++++++++++++++++++++++++++++++++++Status+++++++++++++++++++++++++"+response.getStatusLine().getStatusCode());
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                jsonObj = new JSONObject(result);
                String Status = jsonObj.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray Results = jsonObj.getJSONArray("results");
                    JSONObject zero = Results.getJSONObject(0);
                    JSONArray address_components = zero.getJSONArray("address_components");
                    for (int i = 0; i < address_components.length(); i++) {
                        JSONObject zero2 = address_components.getJSONObject(i);
                        String long_name = zero2.getString("long_name");
                        //System.out.println("++++++++++++++++++++++++++++++++++long_name+++++++++++++++++++++++++"+long_name);
                        JSONArray mtypes = zero2.getJSONArray("types");
                        // System.out.println("++++++++++++++++++++++++++++++++++mtypes+++++++++++++++++++++++++"+mtypes+TextUtils.isEmpty(long_name));
                        String Type = mtypes.getString(0);
                        if (TextUtils.isEmpty(long_name) == false || long_name!=null || long_name.length() > 0 || long_name != "") {

                            if (Type.equalsIgnoreCase("sublocality_level_1")) {
                                fullAddress=long_name+",";

                            } else if (Type.equalsIgnoreCase("sublocality")) {
                                fullAddress=long_name+",";

                            }else if (Type.equalsIgnoreCase("locality")) {
                                // Address2 = Address2 + long_name + ", ";
                                locationVal = long_name;
                                if(fullAddress!=null) {
                                    fullAddress = fullAddress + long_name;
                                }else{
                                    fullAddress = long_name;
                                }

                            } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                                if(fullAddress == null ||locationVal==null ) {
                                    locationVal = long_name;
                                    fullAddress = long_name;

                                }
                            } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                if(fullAddress == null ||locationVal==null ){
                                    locationVal = long_name;
                                    fullAddress=long_name;
                                }
                            }
                        }

                        // JSONArray mtypes = zero2.getJSONArray("types");
                        // String Type = mtypes.getString(0);
                        // Log.e(Type,long_name);


                    }
                    Timber.i("Location Service: CurrentLocation"+fullAddress);
                    System.out.println("+++++++++++++++++++++++++++full+ddresss++" +
                            "+++++++++++++++++++++++" + fullAddress + "+local++" + locationVal);
                }
                new UpdateLocationApi().execute("", "", "");
            } catch (Exception e) {

                e.printStackTrace();

            }

            return responsevalue;

        }
    }
}