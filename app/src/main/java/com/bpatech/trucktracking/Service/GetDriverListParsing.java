package com.bpatech.trucktracking.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anita on 10/16/2015.
 */
public class GetDriverListParsing {
    public List driverPhonenumberlist(JSONArray commentsArray) {
        List  driverlist = new ArrayList();
        try {
            for (int i = 0; i < commentsArray.length(); i++) {

                JSONObject firstInvitation = commentsArray.getJSONObject(i);
                JSONObject driverarray = firstInvitation
                        .getJSONObject("app_user_master");
                driverlist.add(driverarray.getString("phone_number"));

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return driverlist;
    }
}
