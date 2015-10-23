package com.bpatech.trucktracking.Service;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anita on 10/14/2015.
 */
public class AddUserObjectParsing {

    public List<NameValuePair> userCreationObject(String phone_number,String company_name,String active_status,String download_status,String user_name)
    {
        // TODO Auto-generated method stub
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
        nameValuePairs.add(new BasicNameValuePair("phone_number",phone_number));
        nameValuePairs.add(new BasicNameValuePair("Name",user_name));
        nameValuePairs.add(new BasicNameValuePair("company_name",company_name));

        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return nameValuePairs;

    }
    public List<NameValuePair> addDriverPhone(String driver_phone_number,String owner_phone_number)
    {
        // TODO Auto-generated method stub
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("driver_phone_number",driver_phone_number));
            nameValuePairs.add(new BasicNameValuePair("vehicle_owner_phone_number",owner_phone_number));


        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return nameValuePairs;

    }
    public List<NameValuePair> getDriverPhone(String owner_phone_number)
    {
        // TODO Auto-generated method stub
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("vehicle_owner_phone_number",owner_phone_number));

        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return nameValuePairs;

    }
    public List<NameValuePair> AddtripObject(String vehicle_registration_number,String destination_station,
                                             String vehicle_owner_phone_number,String customer_company_name,
                                             String customer_name,String customer_phone_number,String driver_phone_number)
    {
        // TODO Auto-generated method stub
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("vehicle_registration_number",vehicle_registration_number));
            nameValuePairs.add(new BasicNameValuePair("destination_station",destination_station));
            nameValuePairs.add(new BasicNameValuePair("driver_phone_number",driver_phone_number));
            nameValuePairs.add(new BasicNameValuePair("vehicle_owner_phone_number",vehicle_owner_phone_number));
            nameValuePairs.add(new BasicNameValuePair("customer_company_name",customer_company_name));
            nameValuePairs.add(new BasicNameValuePair("customer_name",customer_name));
            nameValuePairs.add(new BasicNameValuePair("customer_phone_number",customer_phone_number));


        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return nameValuePairs;

    }
}
