package com.bpatech.trucktracking.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

import com.bpatech.trucktracking.DTO.UpdateLocationDTO;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class UpdateLoctionByNetworkChange extends IntentService{

    Request request;
    SessionManager session;
    MySQLiteHelper db;
    List<UpdateLocationDTO> locationDBlist;
    Double bulklatitude;
    Double bulklongitude;
    HttpResponse response=null;
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
                locationDBlist.addAll(db.getTracktripDetails());
                if(locationDBlist!=null && locationDBlist.size()>0) {
                    for (i = 0; i < locationDBlist.size(); i++) {

                                        updateBulklocationandadresslist = new ArrayList<NameValuePair>();
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("driver_phone_number",session.getPhoneno()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("latitude", locationDBlist.get(i).getLocation_latitude()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("longitude",locationDBlist.get(i).getLocation_longitude()));
                                        updateBulklocationandadresslist.add(new BasicNameValuePair("updateTime",locationDBlist.get(i).getUpdatetime()));
                                         updateBulklocationandadresslist.add(new BasicNameValuePair("mobileDataStatus",locationDBlist.get(i).getMobildatastatus()));
                                        try {

                                            response = request.requestLocationServicePostType(
                                                    ServiceConstants.UPDATE_BULK_LOCATION, updateBulklocationandadresslist, ServiceConstants.BASE_URL);
                                            responsevalue = "" + response.getStatusLine().getStatusCode();
                                            if (response.getStatusLine().getStatusCode() == 200) {
                                                int deleteid=locationDBlist.get(i).getUpdate_id();
                                                db.deleteLocation_byID(deleteid);


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

}
