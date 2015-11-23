package com.bpatech.trucktracking.Util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.AddTrip;

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
    public static final String KEY_Username = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_otpno = "otpno";
    public static List<AddTrip> addtripdetails;
    public static List<AddTrip> currenttripdetails = new ArrayList<AddTrip>();
    public static List<String> driverlist;
    public static String currentLoggedPage;

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
   
	
    /*public void createSession(String phoneval, int otp){
        // Storing login value as TRUE
      
        // Storing name in pref
        editor.putString(KEY_phoneno, phoneval);
        
        // Storing email in pref
        editor.putInt(KEY_otpno, otp);
        
        // commit changes
        editor.commit();
    }  */
   
   /* public void removesession(){
        
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
        
        // After logout redirect user to Login Activity
        
    }*/

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
    public void setOTPno(int otp) {
        editor.putInt(KEY_otpno, otp);
        editor.commit();
    }

    public int getOTPno() {

        return pref.getInt(KEY_otpno,0);
    }


    public static List<AddTrip> getAddtripdetails() {
        return addtripdetails;
    }



	/*public static List<AddTrip> getCurrenttripdetails() {
		return currenttripdetails;
	}*/

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
        //currenttripdetails.addAll(addtripdetails);
        System.out.println("addtripdetailsaddtripdetails"+currenttripdetails.size());
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

}
