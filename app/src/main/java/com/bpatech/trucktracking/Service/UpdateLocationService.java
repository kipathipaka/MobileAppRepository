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
import android.util.Log;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.UpdateLocationDTO;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anita on 10/29/2015.
 */
public class UpdateLocationService extends Service

{
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    boolean isProviderEnabled=false;
    boolean islocationchanged=false;
    boolean canGetLocation = false;
    SessionManager session;
    Location location;
    Double latitude;
    Double longitude;
    String responsevalue=null;
    MySQLiteHelper db;
    List<UpdateLocationDTO> locationDBlist;
    User user;
    AddUserObjectParsing obj;
    Request request;
    Double bulklatitude;
    int i;
    List<NameValuePair> updateBulklocationandadresslist;
    UpdateLocationDTO updateLocationDTO;
    Handler m_handler;
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

        locationDBlist = new ArrayList<UpdateLocationDTO>();
        m_handler = new Handler();
        updateLocationDTO = new UpdateLocationDTO();
        islocationchanged=false;
        isProviderEnabled=false;
        updateLocationDTO.setDriver_phone_no(session.getPhoneno());
        updateLocationDTO.setLocation_latitude("0.00");
        updateLocationDTO.setLocation_longitude("0.00");
        updateLocationDTO.setUpdatetime(session.getKeyLastUpdatDate());
        UpdateLocation(updateLocationDTO);
        session.setLastupdatid(db.getLastTracktripid());
        getLocation();
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

            if (locationManager!=null) {


                try {
                    isNetworkEnabled = locationManager
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {

                }
                if (!isNetworkEnabled) {
                    // locationEnable_popup();
                    if (!isProviderEnabled){
                        isProviderEnabled=true;
                        new UpdateBulkLocationApi().execute("", "", "");
                        Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if(!islocationchanged) {
                                    latitude=location.getLatitude();
                                    longitude= location.getLongitude();
                                    if(session.getLastupdatid()>0) {
                                        updateLocationDTO.setLocation_latitude(latitude.toString());
                                        updateLocationDTO.setLocation_longitude(longitude.toString());
                                        if (isConnectingToInternet()) {
                                            updateLocationDTO.setMobildatastatus("Y");
                                        } else {
                                            updateLocationDTO.setMobildatastatus("N");
                                        }
                                        db.UpdateLocationDetails(updateLocationDTO, session.getLastupdatid());
                                        session.setLastupdatid(0);
                                        islocationchanged = true;
                                        m_handler.postDelayed(m_statusChecker, 0);

                                    }
                                }

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
                        }, null);

                        Log.d("Network", "Network");

                    }


                }
            } else {


            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
        }
        return location;
    }




    private class UpdateBulkLocationApi extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            getLocation();
        }

        protected String doInBackground(String... params) {

            try {
                locationDBlist.addAll(db.getTracktripDetails());
                int deleteid = 0;
                if(locationDBlist!=null && locationDBlist.size()>0) {
                    for (i = 0; i < locationDBlist.size(); i++) {

                        deleteid=locationDBlist.get(i).getUpdate_id();
                        updateBulklocationandadresslist = new ArrayList<NameValuePair>();
                        updateBulklocationandadresslist.add(new BasicNameValuePair("driver_phone_number",session.getPhoneno()));
                        updateBulklocationandadresslist.add(new BasicNameValuePair("latitude",locationDBlist.get(i).getLocation_latitude()));
                        updateBulklocationandadresslist.add(new BasicNameValuePair("longitude",locationDBlist.get(i).getLocation_longitude()));
                        updateBulklocationandadresslist.add(new BasicNameValuePair("updateTime",locationDBlist.get(i).getUpdatetime()));
                        updateBulklocationandadresslist.add(new BasicNameValuePair("mobileDataStatus",locationDBlist.get(i).getMobildatastatus()));
                        try {

                            if(isConnectingToInternet()) {
                                response = request.requestLocationServicePostType(
                                        ServiceConstants.UPDATE_BULK_LOCATION, updateBulklocationandadresslist, ServiceConstants.BASE_URL);
                                responsevalue = "" + response.getStatusLine().getStatusCode();

                                if (response.getStatusLine().getStatusCode() == 200) {
                                    db.deleteLocation_byID(deleteid);

                                } else {
                                    if (locationDBlist.get(i).getMobildatastatus().equalsIgnoreCase("Y")) {
                                        db.updateStatus("N", deleteid);
                                    }

                                }
                            }else {
                                if (locationDBlist.get(i).getMobildatastatus().equalsIgnoreCase("Y")) {
                                    db.updateStatus("N", deleteid);
                                }

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }



                    }
                }

            } catch (Exception e) {

                e.printStackTrace();

            }

            return responsevalue;

        }
    }

    private	Runnable m_statusChecker = new Runnable() {
        @Override
        public void run() {

            new UpdateBulkLocationApi().execute("", "", "");

        }
    };
    public void UpdateLocation(UpdateLocationDTO Updatelocation) {
        db.addUpdateLocationDetails(Updatelocation);

        Log.d("Insert: ", "Inserting ..");
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivity.getActiveNetworkInfo();

        return info != null && info.isConnected();

    }
    
}








