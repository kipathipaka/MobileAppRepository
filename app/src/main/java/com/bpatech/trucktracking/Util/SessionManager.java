package com.bpatech.trucktracking.Util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.DTO.MessageDTO;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "MyTripPref";

    public static Activity getActivity;



    // User name (make variable public to access from outside)
    public static final String KEY_phoneno = "phoneno";
    public static final String KEY_ALARAM = "Alaramcount";
    public static final String KEY_Username = "username";
    public static final String KEY_LAST_UPDAT_ID = "updateid";
public static final  String KEY_LAST_UPDAT_DATE="lastupdatedate";

    // Email address (make variable public to access from outside)

    public static final String KEY_VEHICLE_TRIP_ID = "vehicle_trip_id";
    public static final String KEY_otpno = "otpno";
    public static List<AddTrip> addtripdetails;
    public static List<AddTrip> currenttripdetails = new ArrayList<AddTrip>();
    public static List<String> driverlist;
    public static List<MessageDTO> messagelist;
    public static String currentLoggedPage;

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
   
	


    public void setPhoneno(String phoneval) {

        editor.putString(KEY_phoneno, phoneval);
        editor.commit();
    }
    public String getPhoneno() {

        return pref.getString(KEY_phoneno, null);
    }


    public void setUsername(String username) {

        editor.putString(KEY_Username, username);
        editor.commit();
    }
    public String getUsername() {
        return pref.getString(KEY_Username, null);
    }

    public void setVechil_trip_id(String trip_id) {

        editor.putString(KEY_VEHICLE_TRIP_ID, trip_id);
        editor.commit();
    }
    public String getVechil_trip_id() {

        return pref.getString(KEY_VEHICLE_TRIP_ID, null);
    }

    public void setKeyLastUpdatDate(String updatetime) {

        editor.putString(KEY_LAST_UPDAT_DATE, updatetime);
        editor.commit();
    }
    public String getKeyLastUpdatDate() {

        return pref.getString(KEY_LAST_UPDAT_DATE, null);
    }

    public void setAlaramcount(int Alaramcount) {

        editor.putInt(KEY_ALARAM, Alaramcount);
        editor.commit();
    }
    public int getAlaramcount() {

        return pref.getInt(KEY_ALARAM, 0);
    }
   public void setLastupdatid(int id) {

        editor.putInt(KEY_LAST_UPDAT_ID, id);
        editor.commit();
    }
    public int getLastupdatid() {

        return pref.getInt(KEY_LAST_UPDAT_ID, 0);
    }


    public static List<AddTrip> getAddtripdetails() {
        return addtripdetails;
    }




    public void logoutUser() {
        // Clearing all data from Shared Preferences
        // editor.clear();
        // editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context,HomeActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    public static void setAddtripdetails(List<AddTrip> addtripdetails) {

        SessionManager.addtripdetails=addtripdetails;

    }

    public boolean isLoggedIn() {
        return pref.getBoolean(ServiceConstants.IS_LOGIN, false);
    }

    public static String getCurrentLoggedPage() {
        return currentLoggedPage;
    }

    public static void setCurrentLoggedPage(String currentLoggedPage) {
        SessionManager.currentLoggedPage = currentLoggedPage;
    }
    public static void setDriverlist(List<String> driverlist) {

        SessionManager.driverlist=driverlist;
    }
    public static List<String> getDriverlist() {
        return driverlist;
    }
    public static void setMessagelist(List<MessageDTO> messagelist) {

        SessionManager.messagelist=messagelist;
    }
    public static List<MessageDTO> getMessagelist() {
        return messagelist;
    }

}
