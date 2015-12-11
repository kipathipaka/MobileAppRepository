package com.bpatech.trucktracking.Service;

import com.bpatech.trucktracking.DTO.MessageDTO;
import com.bpatech.trucktracking.DTO.User;

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
    public List MessageDTOList(JSONObject messageobject) {

                List<MessageDTO> MessageDTOlist = new ArrayList<MessageDTO>();

        MessageDTO messageobj;
        try {
            messageobj=new MessageDTO();

            messageobj.setInvite_message(messageobject.getString("invite_Message"));
            messageobj.setShare_message(messageobject.getString("share_Message"));
            messageobj.setAdd_driver_message(messageobject.getString("add_Driver_Message"));


            MessageDTOlist.add(messageobj);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return MessageDTOlist;
    }
}
