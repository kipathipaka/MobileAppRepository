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
                mytrip.setTrip_url(firstmytriparry.getString("trip_url"));
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

                mytrip.setFullAddress(driverarray.getString("fullAddress"));
                mytrip.setLocation(driverarray.getString("location"));
                mytrip.setLatitude(driverarray.getString("latitude"));
                mytrip.setLongitude(driverarray.getString("longitude"));

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
                if(driverarray.getString("last_sync_date_time").toString().equalsIgnoreCase("null") || driverarray.getString("location").toString().equalsIgnoreCase("null")) {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd,h:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+17:30"));
                    Date date = new Date(Long.parseLong(firstmytriparry.getString("created_on").toString()));

                    mytrip.setLast_sync_time("Not Available");
                }else {
                    DateFormat dateFormat1 = new SimpleDateFormat("MMM dd,h:mm a");
                    dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30

                    Date date = new Date(Long.parseLong(driverarray.getString("last_sync_date_time").toString()));
                    mytrip.setLast_sync_time(dateFormat1.format(date).toString());
                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy MMM dd,h:mm a");
                    dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30

                    mytrip.setLast_ping_Datetime(dateFormat2.format(date).toString());

                }
                mytriplist.add(mytrip);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mytriplist;
    }

    public List<AddTrip> Gettrip(JSONObject tripobject) {
        List<AddTrip>  mytriplist = new ArrayList<AddTrip>();
        AddTrip mytrip;
        try {
                mytrip = new AddTrip();
                mytrip.setVehicle_trip_id(tripobject.getInt("vehicle_trip_header_id"));
                mytrip.setDestination(tripobject.getString("destination_station"));
                mytrip.setCreate_time(tripobject.getString("created_on"));
                mytrip.setUpdate_time(tripobject.getString("updated_on"));
                mytrip.setStart_end_Trip(tripobject.getString("travel_status"));
                mytrip.setTrip_url(tripobject.getString("trip_url"));
                JSONObject vehiclearray = tripobject
                        .getJSONObject("vehicle");
                mytrip.setTruckno(vehiclearray.getString("vehicle_registration_number"));
                JSONObject vehicleownerarray = tripobject
                        .getJSONObject("vehicleOwner");
                mytrip.setOwner_phone_no(vehicleownerarray.getString("phone_number"));
                if (vehicleownerarray.getString("is_active").equalsIgnoreCase("Y") && vehicleownerarray.getString("app_download_status").equalsIgnoreCase("Y")) {
                    mytrip.setOwner_status(true);
                } else {
                    mytrip.setOwner_status(false);
                }
                JSONObject driverarray = tripobject
                        .getJSONObject("driver");
                mytrip.setDriver_phone_no(driverarray.getString("phone_number"));

                mytrip.setFullAddress(driverarray.getString("fullAddress"));
                mytrip.setLocation(driverarray.getString("location"));
                mytrip.setLatitude(driverarray.getString("latitude"));
                mytrip.setLongitude(driverarray.getString("longitude"));

                if (driverarray.getString("is_active").equalsIgnoreCase("Y") && driverarray.getString("app_download_status").equalsIgnoreCase("Y")) {
                    mytrip.setStartstatus(true);
                } else {
                    mytrip.setStartstatus(false);
                }
                JSONObject customerarray = tripobject
                        .getJSONObject("customer");
                mytrip.setCustomer_phoneno(customerarray.getString("phone_number"));
                mytrip.setCustomer_company(customerarray.getString("company_name"));
                mytrip.setCustomer_name(customerarray.getString("name"));
                if(driverarray.getString("last_sync_date_time").toString().equalsIgnoreCase("null") || driverarray.getString("location").toString().equalsIgnoreCase("null")) {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd,h:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+17:30"));
                    Date date = new Date(Long.parseLong(tripobject.getString("created_on").toString()));

                    mytrip.setLast_sync_time("Not Available");
                }else {
                    DateFormat dateFormat1 = new SimpleDateFormat("MMM dd,h:mm a");
                    dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30
                    Date date = new Date(Long.parseLong(driverarray.getString("last_sync_date_time").toString()));
                    mytrip.setLast_sync_time(dateFormat1.format(date).toString());
                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy MMM dd,h:mm a");
                    dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30

                    mytrip.setLast_ping_Datetime(dateFormat2.format(date).toString());


                }
                mytriplist.add(mytrip);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mytriplist;
    }
}
