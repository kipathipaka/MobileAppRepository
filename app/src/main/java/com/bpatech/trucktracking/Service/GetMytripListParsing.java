package com.bpatech.trucktracking.Service;

import com.bpatech.trucktracking.DTO.AddTrip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anita on 10/20/2015.
 */
public class GetMytripListParsing {

    public List<AddTrip> getmytriplist(JSONArray commentsArray) {
        List<AddTrip>  mytriplist = new ArrayList<AddTrip>();
        AddTrip mytrip;
        try {
            System.out.println("+++++++++++commentsArray.length()+++++++++++"+commentsArray.length());
            for (int i = 0; i < commentsArray.length(); i++) {
                System.out.println("+++++++++++mytriplist+++++++++++");
           mytrip=new AddTrip();
                JSONObject firstmytriparry = commentsArray.getJSONObject(i);
                mytrip.setDestination(firstmytriparry.getString("destination_station"));
                JSONObject vehiclearray = firstmytriparry
                        .getJSONObject("vehicle");
                System.out.println("+++++++++++vehiclearray+++++++++++" + vehiclearray.length());
                mytrip.setTruckno(vehiclearray.getString("vehicle_registration_number"));

                JSONObject driverarray = firstmytriparry
                        .getJSONObject("driver");
                mytrip.setDriver_phone_no(driverarray.getString("phone_number"));
                JSONObject customerarray = firstmytriparry
                        .getJSONObject("customer");
                mytrip.setCustomer_phoneno(customerarray.getString("phone_number"));
                mytrip.setCustomer_company(customerarray.getString("company_name"));
                mytrip.setCustomer_name(customerarray.getString("name"));
                mytrip.setSource("chennai");
                mytriplist.add(mytrip);
            }
            System.out.println("+++++++++++mytriplist+++++++++++" + mytriplist.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mytriplist;
    }
}
