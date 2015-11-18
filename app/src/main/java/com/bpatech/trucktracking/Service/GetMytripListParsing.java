package com.bpatech.trucktracking.Service;

import com.bpatech.trucktracking.DTO.AddTrip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Anita on 10/20/2015.
 */
public class GetMytripListParsing {

    public List<AddTrip> getmytriplist(JSONArray commentsArray) {
        List<AddTrip>  mytriplist = new ArrayList<AddTrip>();
        AddTrip mytrip;
        try {
            for (int i = 0; i < commentsArray.length(); i++) {
                mytrip = new AddTrip();
                JSONObject firstmytriparry = commentsArray.getJSONObject(i);
                mytrip.setVehicle_trip_id(firstmytriparry.getInt("vehicle_trip_header_id"));
                mytrip.setDestination(firstmytriparry.getString("destination_station"));
                mytrip.setCreate_time(firstmytriparry.getString("created_on"));
                mytrip.setUpdate_time(firstmytriparry.getString("updated_on"));
                mytrip.setStart_end_Trip(firstmytriparry.getString("travel_status"));
                JSONObject vehiclearray = firstmytriparry
                        .getJSONObject("vehicle");
                mytrip.setTruckno(vehiclearray.getString("vehicle_registration_number"));
                JSONObject vehicleownerarray = firstmytriparry
                        .getJSONObject("vehicleOwner");
                mytrip.setOwner_phone_no(vehicleownerarray.getString("phone_number"));
                if (vehicleownerarray.getString("is_active").equalsIgnoreCase("Y") && vehicleownerarray.getString("app_download_status").equalsIgnoreCase("Y")) {
                    mytrip.setOwner_status(true);
                } else {
                    mytrip.setOwner_status(false);
                }
                JSONObject driverarray = firstmytriparry
                        .getJSONObject("driver");
                mytrip.setDriver_phone_no(driverarray.getString("phone_number"));
                if (firstmytriparry.getString("travel_status").equalsIgnoreCase("STR")){
                    mytrip.setLocation(firstmytriparry.getString("location"));
                    mytrip.setLatitude(firstmytriparry.getString("latitude"));
                    mytrip.setLongitude(firstmytriparry.getString("longitude"));
                }else{
                    mytrip.setLocation(driverarray.getString("location"));
                    mytrip.setLatitude(driverarray.getString("latitude"));
                    mytrip.setLongitude(driverarray.getString("longitude"));
                }
                if (driverarray.getString("is_active").equalsIgnoreCase("Y") && driverarray.getString("app_download_status").equalsIgnoreCase("Y")) {
                    mytrip.setStartstatus(true);
                } else {
                    mytrip.setStartstatus(false);
                }
                JSONObject customerarray = firstmytriparry
                        .getJSONObject("customer");
                mytrip.setCustomer_phoneno(customerarray.getString("phone_number"));
                mytrip.setCustomer_company(customerarray.getString("company_name"));
                mytrip.setCustomer_name(customerarray.getString("name"));
                mytrip.setSource("Delhi");
              /* if(firstmytriparry.getString("location").toString().equalsIgnoreCase("null")) {
                   mytrip.setLocation("Delhi");
                   mytrip.setLatitude(firstmytriparry.getString("latitude"));
                   mytrip.setLongitude(firstmytriparry.getString("longitude"));
               }else{
                   mytrip.setLocation(firstmytriparry.getString("location"));
                   mytrip.setLatitude(firstmytriparry.getString("latitude"));
                   mytrip.setLongitude(firstmytriparry.getString("longitude"));
               }*/
                if(firstmytriparry.getString("last_sync_date_time").toString().equalsIgnoreCase("null")) {
                    DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+17:30"));
                    Date date = new Date(Long.parseLong(firstmytriparry.getString("created_on").toString()));
                    mytrip.setLast_sync_time(dateFormat.format(date).toString());
                }else {
                    DateFormat dateFormat1 = new SimpleDateFormat("h:mm a");
                    dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30
                    //System.out.println("++++++++++++++++++++++++++++++++++long value+++++++++++++++++++++++++++" + firstmytriparry.getString("last_sync_date_time").toString());
                    Date date = new Date(Long.parseLong(firstmytriparry.getString("last_sync_date_time").toString()));
                    mytrip.setLast_sync_time(dateFormat1.format(date).toString());
                   // System.out.println("++++++++++++++++++++++++++++++++++date+++++++++++++++++++++++++++"+mytrip.getLast_sync_time());
                }
                mytriplist.add(mytrip);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mytriplist;
    }
}
