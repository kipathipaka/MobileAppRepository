package com.bpatech.trucktracking.Service;

import com.bpatech.trucktracking.DTO.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GetUserObjectParsing {
    public List<User> getuserlist(JSONObject userObject) {
        List<User> userList = new ArrayList<User>();
        User user;
        try {
            user=new User();

                user.setPhone_no(userObject.getString("phone_number"));
               user.setUserName(userObject.getString("name"));
                user.setCompanyName(userObject.getString("company_name"));



            userList.add(user);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userList;
    }
}
