package com.bpatech.trucktracking.Service;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.DTO.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Anita on 11/25/2015.
 */
public class GetUserObjectParsing {
    public List<User> getuserlist(JSONObject userObject) {
        List<User> userList = new ArrayList<User>();
        User user;
        try {
            user=new User();
               // JSONObject firstmytriparry = commentsArray.getJSONObject(i);
                user.setPhone_no(userObject.getString("phone_number"));
               user.setUserName(userObject.getString("name"));
                user.setCompanyName(userObject.getString("company_name"));
            /*if(userObject.getString("is_active").equalsIgnoreCase("Y") && userObject.getString("app_download_status").equalsIgnoreCase("Y")){
                user.setUserStaus(true);
            }else{
                user.setUserStaus(false);
            }*/


            userList.add(user);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userList;
    }
}
