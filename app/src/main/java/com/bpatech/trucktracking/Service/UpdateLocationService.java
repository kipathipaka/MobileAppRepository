package com.bpatech.trucktracking.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.UpdateLocationDTO;
import com.bpatech.trucktracking.DTO.User;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by Anita on 10/29/2015.
 */
public class UpdateLocationService extends Service

{
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    boolean isProviderEnabled=false;

    boolean canGetLocation = false;
    SessionManager session;
    Location location;
    Double latitude;
    Double longitude;
    String locationVal,bulklocationVal;
    //String provider="test";
    String responsevalue=null;
    MySQLiteHelper db;
    List<UpdateLocationDTO> locationDBlist;
    User user;
    AddUserObjectParsing obj;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10
    String userphoneno;
    Request request;
    private Context context;
    Double bulklatitude;
    Double bulklongitude;
    String fullAddress,bulkfullAddress;
    int count;
    int delete_id;
    int i;
    List<NameValuePair> updateBulklocationandadresslist;
    UpdateLocationDTO updateLocationDTO;
    Handler m_handler;
    String bulkupdatetime;
    HttpResponse response=null;
    // The minimum time beetwen updates in milliseconds 15 * 60 * 1000.
    private static final long MIN_TIME_BW_UPDATES =20* 60 * 1000;


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
        Timber.i("UpdateLocationService : Start Command Call");
        locationDBlist = new ArrayList<UpdateLocationDTO>();
        m_handler = new Handler();
        updateLocationDTO = new UpdateLocationDTO();
       // locationDBlist.addAll(db.getTracktripDetails());
        System.out.println("+++++Start service+++");
        //updateBulkLocation();
      new UpdateBulkLocationApi().execute("", "", "");
     //  getLocation();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   public Location getLocation() {
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
           /* if (locationManager != null) {
            Criteria crta = new Criteria();
            crta.setAccuracy(Criteria.ACCURACY_FINE);
            crta.setAltitudeRequired(false);
            crta.setBearingRequired(false);
            crta.setCostAllowed(true);
            crta.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(crta, true);
            System.out.println("++++++++++++++++++++++++++++++++++provider+++++++++++++++++++++++++++"+provider);
            if(provider!=null) {
                locationManager.requestLocationUpdates(
                        provider,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                System.out.println("++++++++++++++++++++++++++++++++++location+++++++++++++++++++++++++++" + location);
                                Timber.i("UpdateLocationService:onLocationChanged  : Network Provider latlng" + location.getLatitude() +
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
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(provider);
                    updateGPSCoordinates(location);
                }
            }
            }*/

            if (locationManager!=null) {
                Timber.i("UpdateLocationService:locationManager  : " + locationManager);
                //System.out.println("++++++++++++++++++++++++++++++++++provider+++++++++++++++++++++++++++" + locationManager);
                List<String> abc = locationManager.getAllProviders();
                for(String a:abc){
                    //System.out.println("++++++++++++++++++++++++++++++++++providers++++++++++++++++++++++++++"+a);

                    boolean status = locationManager.isProviderEnabled(a);
                   // System.out.println("++++++++++++++++++++++++++++++++++status+++++++++++++++++++++++++++" + status);
                   /* boolean status1=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    System.out.println("++++++++++++++++++++++++++++++++++status+++++++++++++++++++++++++++" + status1);
                    boolean status2=locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                    System.out.println("++++++++++++++++++++++++++++++++++status+++++++++++++++++++++++++++" + status2);*/
                }


                try {
                    //System.out.println("++++++++++++++++++++++++++++++++++LocationManager.NETWORK_PROVIDER+++++++++++++++++++++++++++" + LocationManager.NETWORK_PROVIDER);
                    isNetworkEnabled = locationManager
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    //System.out.println("++++++++++++++++++++++++++++++++++isNetworkEnabled1+++++++++++++++++++++++++++"+isNetworkEnabled);
                } catch (Exception ex) {
                    Timber.i("UpdateLocationService:locationManager Exception : " +ex.getMessage());
                }
                if (!isNetworkEnabled) {
                    // locationEnable_popup();
                    Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
                    Date date1 = new Date();
                    updateLocationDTO.setDriver_phone_no(session.getPhoneno());
                    updateLocationDTO.setLocation("Location Manager is not enabled");
                    updateLocationDTO.setLocation_latitude("0.00");
                    updateLocationDTO.setLocation_longitude("0.00");
                    updateLocationDTO.setFulladdress("Location Manager is not enabled");
                    updateLocationDTO.setUpdatetime(updateTimeFormat.format(date1).toString());
                    UpdateLocation(updateLocationDTO);
                    //Toast.makeText(getApplicationContext(), "Location is not enabled.. Please check", Toast.LENGTH_SHORT).show();
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                      //  System.out.println("++++++++++++++++++++++++++++++++++isNetworkEnabled+++++yesss++++++++++++++++++++++" + isNetworkEnabled);
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                      //  System.out.println("++++++++++++++++++++++++++++++++++isNetworkEnabled+++++onlocation changed++++++++++++++++++++++"+location.getLatitude()+location.getLongitude());
                                        Timber.i("UpdateLocationService:onLocationChanged  : Network Provider latlng" + location.getLatitude() +
                                                "&" + location.getLongitude());
                                        //updateGPSCoordinates(location);
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
                        Timber.i("UpdateLocationService:Network : Network enable");
                        Log.d("Network", "Network");

                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            updateGPSCoordinates(location);
                    }
     /*               if (isGPSEnabled) {
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
                            Timber.i("UpdateLocationService :GPS Enabled");
                            Log.d("GPS Enabled", "GPS Enabled");

                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                updateGPSCoordinates(location);
                            }
                        }
                    }*/

                }
            } else {
               // System.out.println("++++++++++++++++++++++++++++111+++no location found++++++");
                DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
                Date date1 = new Date();
                updateLocationDTO.setDriver_phone_no(session.getPhoneno());
                updateLocationDTO.setLocation("Location Manager is not enabled");
                updateLocationDTO.setLocation_latitude("0.00");
                updateLocationDTO.setLocation_longitude("0.00");
                updateLocationDTO.setFulladdress("Location Manager is not enabled");
                updateLocationDTO.setUpdatetime(updateTimeFormat.format(date1).toString());
                UpdateLocation(updateLocationDTO);
                Timber.i("UpdateLocationServic :LocationManager is not connected");
            }
        } catch (Exception e) {
            DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            Date date1 = new Date();
            updateLocationDTO.setDriver_phone_no(session.getPhoneno());
            updateLocationDTO.setLocation("Location Manager is not enabled");
            updateLocationDTO.setLocation_latitude("0.00");
            updateLocationDTO.setLocation_longitude("0.00");
            updateLocationDTO.setFulladdress("Location Manager is not enabled");
            updateLocationDTO.setUpdatetime(updateTimeFormat.format(date1).toString());
            UpdateLocation(updateLocationDTO);
            e.printStackTrace();
            Timber.i("UpdateLocationServiceException : Location"+
                    "Impossible to connect to LocationManager"+e.getMessage());
            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
        }
        return location;
    }

    public void updateGPSCoordinates(Location updateLocation) {
        Timber.i("UpdateLocationService :Enter updateGPSCoordinates");
        if (updateLocation != null) {
            latitude = updateLocation.getLatitude();
            longitude = updateLocation.getLongitude();
            Timber.i("UpdateLocationService :updateGPSCoordinates latitude :  " + latitude + "Longitude  :" + longitude);
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
        }else{
            DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            Date date1 = new Date();
            updateLocationDTO.setDriver_phone_no(session.getPhoneno());
            updateLocationDTO.setLocation("Location Manager is not enabled");
            updateLocationDTO.setLocation_latitude("0.00");
            updateLocationDTO.setLocation_longitude("0.00");
            updateLocationDTO.setFulladdress("Location Manager is not enabled");
            updateLocationDTO.setUpdatetime(updateTimeFormat.format(date1).toString());
            UpdateLocation(updateLocationDTO);
            //System.out.println("+++++++++++++++++++++++++++++++no location found++++++");
        }
    }


    private class UpdateLocationApi extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

        }

        protected String doInBackground(String... params) {

            try {
                //System.out.println("+++++++++++++++++++++++++++++++enter ++UpdateLocationApi++++");
                Timber.i("UpdateLocationService:Service APi Call :  " + latitude + "Longitude  :" + longitude);
               // System.out.println("++++++++++++++++++++++++++++++++++UpdateLocationService:Service APi Call +++++++++++++++++++++++++++");
                //  System.out.println("++++++++++++++++++++++++++++++++++userphoneno+++++++++++++++++++++++++++" +
                 //session.getPhoneno()+latitude.toString()+longitude.toString());
                if (session.getPhoneno() == null || String.valueOf(latitude) == null || String.valueOf(longitude) == null || locationVal.toString() == null) {

                    responsevalue = "noResult";
                    Timber.i("UpdateLocationService:Service APi Call Result  : " + responsevalue);
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
                 System.out.println("+++++++++++++++location Api++++++Status++++++++++++++++"+response.getStatusLine().getStatusCode());
                    if( response.getStatusLine().getStatusCode() == 200) {
                        //System.out.println("+++++++++++++++location if condition++++++++++++++++");
                                Timber.i("UpdateLocationService:Location update API result :" + response.getStatusLine().getStatusCode());
                        Timber.i("UpdateLocationService:Location Service  ApI updated");
                    }else {
                       // System.out.println("+++++++++++++++not 200++++++++++++++++" + response.getStatusLine().getStatusCode());
                        if (count > 0) {
                            count--;
                            m_handler.postDelayed(m_statusChecker,0);
                            /*String smsno = "+91"+session.getPhoneno();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(smsno, null,"You have Poor Net Connection.So,Your Last location didn't update to the Owner", null, null);*/
                            Timber.i("UpdateLocationService:Location update API failed result :" + response.getStatusLine().getStatusCode());

                        } else {
                            DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
                            Date date1 = new Date();
                            updateLocationDTO.setDriver_phone_no(session.getPhoneno());
                            updateLocationDTO.setLocation(locationVal);
                            updateLocationDTO.setLocation_latitude(latitude.toString());
                            updateLocationDTO.setLocation_longitude(longitude.toString());
                            updateLocationDTO.setFulladdress(fullAddress.toString());
                            updateLocationDTO.setUpdatetime(updateTimeFormat.format(date1).toString());
                            UpdateLocation(updateLocationDTO);

                        }
                    }
                }
            } catch (Exception e) {
                Timber.i("UpdateLocationService:Location update API  Exception :"+e.getMessage());
                e.printStackTrace();

            }

            return responsevalue;

        }
    }

    private class UpdateBulkLocationApi extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            getLocation();
        }

        protected String doInBackground(String... params) {

            try {
               // System.out.println("++++++enter++++++++ServiceBULK+Api+++++++++++++++++++");
               // Timber.i("UpdateLocationService:ServiceBULK APi Call :  " + latitude + "Longitude  :" + longitude);
                locationDBlist.addAll(db.getTracktripDetails());
                //System.out.println("++++&&+updateBulkLocation+++++++++list size++" + locationDBlist.size());
                if(locationDBlist!=null && locationDBlist.size()>0) {
                    for (i = 0; i < locationDBlist.size(); i++) {
                        bulklatitude=null;
                        bulklongitude=null;
                        if (locationDBlist.get(i).getLocation() != null && locationDBlist.get(i).getFulladdress() != null) {
                            List<NameValuePair> updateBulklocationlist;  updateBulklocationlist = new ArrayList<NameValuePair>();
                          //  System.out.println("++++&&+updateBulkLocation+++++++++enter first loop++");
                            //delete_id=locationDBlist.get(i).getUpdate_id();
                            updateBulklocationlist.add(new BasicNameValuePair("driver_phone_number", locationDBlist.get(i).getDriver_phone_no()));
                            updateBulklocationlist.add(new BasicNameValuePair("location", locationDBlist.get(i).getLocation()));
                            updateBulklocationlist.add(new BasicNameValuePair("fullAddress", locationDBlist.get(i).getFulladdress()));
                            updateBulklocationlist.add(new BasicNameValuePair("latitude", locationDBlist.get(i).getLocation_latitude()));
                            updateBulklocationlist.add(new BasicNameValuePair("longitude", locationDBlist.get(i).getLocation_longitude()));
                            updateBulklocationlist.add(new BasicNameValuePair("updateTime", locationDBlist.get(i).getUpdatetime()));
                            response = request.requestLocationServicePostType(
                                    ServiceConstants.UPDATE_BULK_LOCATION, updateBulklocationlist, ServiceConstants.BASE_URL);
                            responsevalue = "" + response.getStatusLine().getStatusCode();
                           // System.out.println("+++++++++++++++ServiceBULKApi++++++Status++++++++++++++++" + response.getStatusLine().getStatusCode());
                            if (response.getStatusLine().getStatusCode() == 200) {
                                int id=locationDBlist.get(i).getUpdate_id();
                               db.deleteLocation_byID(id);
                                //System.out.println("+++++++++++++++db deleted++++++++++++++");
                               // System.out.println("+++++++++++++++db deleted++++++++++++++");
                                Timber.i("UpdateLocationService:Location update API result :" + response.getStatusLine().getStatusCode());
                                Timber.i("UpdateLocationService:Location Service ApI updated");
                            }
                        }else{
                            //System.out.println("+++++++++++++++++++++++++++++++enter else con++++++id+++++++++++++++++"+locationDBlist.get(i).getUpdate_id());
                            bulklatitude=Double.parseDouble(locationDBlist.get(i).getLocation_latitude());
                            bulklongitude=Double.parseDouble(locationDBlist.get(i).getLocation_longitude());
                            //System.out.println("+++++++++++++++++bulklongitude++++++++bulklongitude+++++++++++++++++++++++++"+bulklatitude+bulklongitude);
                            //new GetBulkAddressFromJson().execute("", "", "");
                            InputStream is = null;
                            String result = "";
                            JSONObject jsonObj=null;
                            bulkfullAddress=null;
                            bulklocationVal=null;
                            try {
                                Timber.i("Location Service:GetBulkAddressFromJsonAPI");
                              //  System.out.println("+++++++++++++++++++++enter bulkjson++++++++++++++++++++++" + locationDBlist.get(i).getUpdate_id());
                                if (isConnectingToInternet()==true) {
                                    HttpClient httpclient = new DefaultHttpClient();
                                    HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + bulklatitude + "," + bulklongitude);
                                    HttpResponse response = httpclient.execute(httppost);
                                    responsevalue = "" + response.getStatusLine().getStatusCode();
                                    Timber.i("Location Service:GetBulkAddressFromJsonAPI :Status" + response.getStatusLine().getStatusCode());
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
                                    if (result != null) {
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
                                                if (TextUtils.isEmpty(long_name) == false || long_name != null || long_name.length() > 0 || long_name != "") {

                                                    if (Type.equalsIgnoreCase("sublocality_level_1")) {
                                                        if (bulkfullAddress == null) {
                                                            bulkfullAddress = long_name + ",";
                                                        }

                                                    } else if (Type.equalsIgnoreCase("sublocality")) {
                                                        if (bulkfullAddress == null) {
                                                            bulkfullAddress = long_name + ",";
                                                        }

                                                    } else if (Type.equalsIgnoreCase("locality")) {
                                                        // Address2 = Address2 + long_name + ", ";
                                                        bulklocationVal = long_name;
                                                        if (bulkfullAddress != null) {
                                                            bulkfullAddress = bulkfullAddress + long_name;
                                                        } else {
                                                            bulkfullAddress = long_name;
                                                        }

                                                    } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                                                        if (bulkfullAddress == null || bulklocationVal == null) {
                                                            bulklocationVal = long_name;
                                                            bulkfullAddress = long_name;

                                                        }
                                                    } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                                        if (bulkfullAddress == null || bulklocationVal == null) {
                                                            bulklocationVal = long_name;
                                                            bulkfullAddress = long_name;
                                                        }
                                                    }
                                                }
                                            }
                                            Timber.i("Location Service:GetBulkAddressFromJsonAPI  CurrentLocation  " + bulkfullAddress);
                                            System.out.println("+++++++++addresss++++++++++++++++full+ddresss++" +
                                                    "+++++++++++++++++++++++" + bulkfullAddress + "+local++" + bulklocationVal);
                                        }
                                        updateBulklocationandadresslist = new ArrayList<NameValuePair>();
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("driver_phone_number",session.getPhoneno()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("location",bulklocationVal));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("fullAddress",bulkfullAddress));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("latitude", bulklatitude.toString()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("longitude",bulklongitude.toString()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("updateTime", locationDBlist.get(i).getUpdatetime()));
                                       // new UpdateBulkLocationandAdressApi().execute("", "", "");
                                        try {
                                            //System.out.println("++++++enter++++++++UpdateBulkLocationandAdressApi+Api+++++++++++++++++++");
                                            Timber.i("UpdateLocationService:ServiceBULK APi Call :  " + bulklatitude + "Longitude  :" + bulklongitude);
                                            response = request.requestLocationServicePostType(
                                                    ServiceConstants.UPDATE_BULK_LOCATION, updateBulklocationandadresslist, ServiceConstants.BASE_URL);
                                            responsevalue = "" + response.getStatusLine().getStatusCode();
                                           // System.out.println("+++++++++++++++UpdateBulkLocationandAdressApi++++++Status++++++++++++++++" + response.getStatusLine().getStatusCode());
                                            if (response.getStatusLine().getStatusCode() == 200) {
                                                int deleteid=locationDBlist.get(i).getUpdate_id();
                                               // System.out.println("+++++++++++UpdateBulkLocationandAdressApi++++db deleted++++++++++++++"+locationDBlist.get(i).getUpdate_id());
                                                db.deleteLocation_byID(deleteid);
                                                System.out.println("+++++++++++UpdateBulkLocationandAdressApi++++db deleted++++++++++++++");
                                                Timber.i("UpdateLocationService:Location update API result :" + response.getStatusLine().getStatusCode());
                                                Timber.i("UpdateLocationService:Location Service  ApI updated");
                                            }

                                        } catch (Exception e) {
                                            Timber.i("UpdateLocationService:Location update API  Exception :"+e.getMessage());
                                            e.printStackTrace();

                                        }
                                    } else {
                                        bulklocationVal = null;
                                        bulkfullAddress = null;
                                    }
                                }
                            } catch (Exception e) {

                                e.printStackTrace();

                            }
                        }
                    }
                }
            } catch (Exception e) {
                Timber.i("UpdateLocationService:Location update API  Exception :"+e.getMessage());
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
           fullAddress=null;
            locationVal=null;
            try {
                Timber.i("Location Service:GetAddressFromJsonAPI");
               //System.out.println("++++++++++++++++++");
                 if (isConnectingToInternet()==true) {
                     HttpClient httpclient = new DefaultHttpClient();
                     HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude);
                     HttpResponse response = httpclient.execute(httppost);

                     responsevalue = "" + response.getStatusLine().getStatusCode();
                     System.out.println("+++++++++++++++++++++++++AddressApi+++++++++Status+++++++++++++++++++++++++" + response.getStatusLine().getStatusCode());
                     if(response.getStatusLine().getStatusCode()!=200){
                         DateFormat timeformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                         timeformat.setTimeZone(TimeZone.getTimeZone("IST"));
                         Date update1 = new Date();
                         updateLocationDTO=new UpdateLocationDTO();
                         updateLocationDTO.setDriver_phone_no(session.getPhoneno());
                         updateLocationDTO.setLocation_latitude(latitude.toString());
                         updateLocationDTO.setLocation_longitude(longitude.toString());
                         updateLocationDTO.setUpdatetime(timeformat.format(update1).toString());
                         UpdateLocation(updateLocationDTO);
                     }
                     //System.out.println("++++++++++++++++++++++++++++++++++Status+++++++++++++++++++++++++" + response.getStatusLine().getStatusCode());
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
                     if (result != null) {
                         jsonObj = new JSONObject(result);
                         String Status = jsonObj.getString("status");
                         if (Status.equalsIgnoreCase("OK")) {
                             JSONArray Results = jsonObj.getJSONArray("results");
                             JSONObject zero = Results.getJSONObject(0);
                            /* String list=zero.getString("formatted_address");
                             System.out.println("++++++++++++++++++++++formated add+++++++++++++++++++"+list);
                             String[] addresslist;
                             addresslist=list.split(",");
                             System.out.println("++++++++++formated add++++size++++++++++++"+addresslist.length);
                             int n=addresslist.length;
                             System.out.println("+++++++++++++++formated add++++size+++++++++++++++"+addresslist[n-3]+"++++++++++++"+addresslist[n-4]);*/
                             JSONArray address_components = zero.getJSONArray("address_components");
                             for (int i = 0; i < address_components.length(); i++) {
                                 JSONObject zero2 = address_components.getJSONObject(i);
                                 String long_name = zero2.getString("long_name");
                                 //System.out.println("++++++++++++++++++++++++++++++++++long_name+++++++++++++++++++++++++"+long_name);
                                 JSONArray mtypes = zero2.getJSONArray("types");
                                 // System.out.println("++++++++++++++++++++++++++++++++++mtypes+++++++++++++++++++++++++"+mtypes+TextUtils.isEmpty(long_name));
                                 String Type = mtypes.getString(0);
                                 if (TextUtils.isEmpty(long_name) == false || long_name != null || long_name.length() > 0 || long_name != "") {

                                     if (Type.equalsIgnoreCase("sublocality_level_1")) {
                                         if (fullAddress == null) {
                                             fullAddress = long_name + ",";
                                         }

                                     } else if (Type.equalsIgnoreCase("sublocality")) {
                                         if (fullAddress == null) {
                                             fullAddress = long_name + ",";
                                         }

                                     } else if (Type.equalsIgnoreCase("locality")) {
                                         // Address2 = Address2 + long_name + ", ";
                                         locationVal = long_name;
                                         if (fullAddress != null) {
                                             fullAddress = fullAddress + long_name;
                                         } else {
                                             fullAddress = long_name;
                                         }

                                     } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                                         if (fullAddress == null || locationVal == null) {
                                             locationVal = long_name;
                                             fullAddress = long_name;

                                         }
                                     } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                         if (fullAddress == null || locationVal == null) {
                                             locationVal = long_name;
                                             fullAddress = long_name;
                                         }
                                     }
                                 }

                                 // JSONArray mtypes = zero2.getJSONArray("types");
                                 // String Type = mtypes.getString(0);
                                 // Log.e(Type,long_name);


                             }
                             Timber.i("Location Service: CurrentLocation" + fullAddress);
                             System.out.println("+++++++++++++++++++++++++++full+ddresss++" +
                                     "+++++++++++++++++++++++" + fullAddress + "+local++" + locationVal);
                         }
                        count = 2;
                         m_handler.postDelayed(m_statusChecker, 0);
                        // new UpdateLocationApi().execute("", "", "");
                     } else {
                         locationVal = null;
                         fullAddress = null;
                     }
                 }else{
                    // System.out.println("+++++++++++++no internet else++++++++++++++" + latitude.toString() + longitude.toString());
                     DateFormat timeformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                     timeformat.setTimeZone(TimeZone.getTimeZone("IST"));
                     Date update = new Date();
                     updateLocationDTO=new UpdateLocationDTO();
		updateLocationDTO.setDriver_phone_no(session.getPhoneno());
		updateLocationDTO.setLocation_latitude(latitude.toString());
		updateLocationDTO.setLocation_longitude(longitude.toString());
		updateLocationDTO.setUpdatetime(timeformat.format(update).toString());
		UpdateLocation(updateLocationDTO);
                 }
            } catch (Exception e) {
                DateFormat timeformat2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                timeformat2.setTimeZone(TimeZone.getTimeZone("IST"));
                Date update2 = new Date();
                updateLocationDTO=new UpdateLocationDTO();
                updateLocationDTO.setDriver_phone_no(session.getPhoneno());
                updateLocationDTO.setLocation_latitude(latitude.toString());
                updateLocationDTO.setLocation_longitude(longitude.toString());
                updateLocationDTO.setUpdatetime(timeformat2.format(update2).toString());
                UpdateLocation(updateLocationDTO);
                e.printStackTrace();

            }

            return responsevalue;

        }
    }

    private	Runnable m_statusChecker = new Runnable() {
        @Override
        public void run() {
           // System.out.println("+++++++++++++++repeat+handler+++" + count);
            if (count > 0){
                new UpdateLocationApi().execute("", "", "");

        }else {
                DateFormat updateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                updateTimeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
                Date date1 = new Date();
                updateLocationDTO.setDriver_phone_no(session.getPhoneno());
                updateLocationDTO.setLocation(locationVal);
                updateLocationDTO.setLocation_latitude(latitude.toString());
                updateLocationDTO.setLocation_longitude(longitude.toString());
                updateLocationDTO.setFulladdress(fullAddress.toString());
                updateLocationDTO.setUpdatetime(updateTimeFormat.format(date1).toString());
                UpdateLocation(updateLocationDTO);
            }

            //System.out.println("+++++++++++++++++counter 1..."+counter
           // Enable_location_popup(); //this function can change value of m_interval.
            //counter--;
        }
    };
    public void UpdateLocation(UpdateLocationDTO Updatelocation) {

      db.addUpdateLocationDetails(Updatelocation);
        /*List<UpdateLocationDTO> tracklist=new  ArrayList<UpdateLocationDTO>();
        tracklist.addAll(db.getTracktripDetails());*/
        System.out.println("+++++++UpdateLocation+++++++list size++"+db.getUpdateLocationCount());
        Timber.i("UpdateLocation:Insert:Inserting ..");
        Log.d("Insert: ", "Inserting ..");
    }
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivity.getActiveNetworkInfo();

        return info != null && info.isConnected();

    }


}








