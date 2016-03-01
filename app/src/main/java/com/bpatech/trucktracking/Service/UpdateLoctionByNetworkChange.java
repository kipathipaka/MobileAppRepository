package com.bpatech.trucktracking.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bpatech.trucktracking.DTO.UpdateLocationDTO;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Anita on 2/16/2016.
 */
public class UpdateLoctionByNetworkChange extends IntentService{

    Request request;
    SessionManager session;
    MySQLiteHelper db;
    List<UpdateLocationDTO> locationDBlist;
    Double bulklatitude;
    Double bulklongitude;
    HttpResponse response=null;
    String bulkfullAddress,bulklocationVal;
    int i;
    String responsevalue=null;
    List<NameValuePair> updateBulklocationandadresslist;

    public UpdateLoctionByNetworkChange() {
        super("UpdateLoctionByNetworkChange");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        request = new Request(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        db = new MySQLiteHelper(getApplicationContext());
        locationDBlist = new ArrayList<UpdateLocationDTO>();
        new UpdateBulkLocationApi().execute("", "", "");
    }
    private class UpdateBulkLocationApi extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            //getLocation();
        }

        protected String doInBackground(String... params) {

            try {
                // System.out.println("++++++enter++++++++ServiceBULK+Api+++++++++++++++++++");
                // Timber.i("UpdateLocationService:ServiceBULK APi Call :  " + latitude + "Longitude  :" + longitude);
                locationDBlist.addAll(db.getTracktripDetails());
                System.out.println("++++&&+updateBulkLocation+++++++++list size++" + locationDBlist.size());
                if(locationDBlist!=null && locationDBlist.size()>0) {
                    for (i = 0; i < locationDBlist.size(); i++) {
                        bulklatitude=null;
                        bulklongitude=null;
                        if (locationDBlist.get(i).getLocation() != null && locationDBlist.get(i).getFulladdress() != null) {
                            List<NameValuePair> updateBulklocationlist;  updateBulklocationlist = new ArrayList<NameValuePair>();
                            //  System.out.println("++++&&+updateBulkLocation+++++++++enter first loop++");
                            //delete_id=locationDBlist.get(i).getUpdate_id();
                            updateBulklocationlist.add(new BasicNameValuePair("driver_phone_number", locationDBlist.get(i).getDriver_phone_no()));
                            //updateBulklocationlist.add(new BasicNameValuePair("location", locationDBlist.get(i).getLocation()));
                            //updateBulklocationlist.add(new BasicNameValuePair("fullAddress", locationDBlist.get(i).getFulladdress()));
                            updateBulklocationlist.add(new BasicNameValuePair("latitude", locationDBlist.get(i).getLocation_latitude()));
                            updateBulklocationlist.add(new BasicNameValuePair("longitude", locationDBlist.get(i).getLocation_longitude()));
                            updateBulklocationlist.add(new BasicNameValuePair("updateTime", locationDBlist.get(i).getUpdatetime()));
                            updateBulklocationlist.add(new BasicNameValuePair("mobileDataStatus","N"));
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

                                Timber.i("Location Service:GetBulkAddressFromJsonAPI");
                                //  System.out.println("+++++++++++++++++++++enter bulkjson++++++++++++++++++++++" + locationDBlist.get(i).getUpdate_id());

                                        updateBulklocationandadresslist = new ArrayList<NameValuePair>();
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("driver_phone_number",session.getPhoneno()));
                                        //updateBulklocationandadresslist.add(new BasicNameValuePair("location",bulklocationVal));
                                        //updateBulklocationandadresslist.add(new BasicNameValuePair("fullAddress",bulkfullAddress));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("latitude", bulklatitude.toString()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("longitude",bulklongitude.toString()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("updateTime", locationDBlist.get(i).getUpdatetime()));
                                                   updateBulklocationandadresslist.add(new BasicNameValuePair("mobileDataStatus","N"));
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
                                            Timber.i("UpdateLocationService:Location update API  Exception :" + e.getMessage());
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

}
