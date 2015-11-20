package com.bpatech.trucktracking.Activity;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bpatech.trucktracking.Fragment.AddnewTripFragment;
import com.bpatech.trucktracking.Fragment.AddphoneFragment;
import com.bpatech.trucktracking.Fragment.InviteFragment;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.UpdateLocationReceiver;
import com.bpatech.trucktracking.Util.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


public class RegistrationActivity extends FragmentActivity  {

    MySQLiteHelper db;
    private Button nbtn;
    private EditText phoneno;
    SessionManager session;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private GoogleApiClient googleApiClient;
    public static final int REQUEST_CHECK_SETTINGS =1000 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MySQLiteHelper(this.getApplicationContext());
        //boolean value=db.checkPhonenumber(phoneno);
        int phonecount = db.getUserCount();
        //System.out.println("********************phonecount************************** sync call end ..." + phonecount);
        if (phonecount > 0) {
          //  setContentView(R.layout.home_fragment);
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            setContentView(R.layout.home_fragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void addtripclick(View v) {

        AddnewTripFragment addtripfragment = new AddnewTripFragment();
        pageRedirection(addtripfragment);

    }

    public void addphoneclick(View v) {

        AddphoneFragment addphonefragment = new AddphoneFragment();
        //addphonefragment.sett
        pageRedirection(addphonefragment);
    }
    public void addinviteclick(View v){
        InviteFragment invitefragment=new InviteFragment();
        pageRedirection(invitefragment);
    }


    public void pageRedirection(Fragment fragment) {
        FragmentManager fragmentmanager = getFragmentManager();
        FragmentTransaction fragmenttransaction = fragmentmanager
                .beginTransaction();
        fragmenttransaction.replace(R.id.viewers, fragment,"BackCurrentTrip");
        fragmenttransaction.addToBackStack(null);
        fragmenttransaction.commit();
    }


   @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        FragmentManager mgr = getFragmentManager();
       System.out.println("*****************registration***mgr*********************** ..." + mgr.getBackStackEntryCount());
        if (mgr.getBackStackEntryCount() == 0) {
           /* Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            super.onBackPressed();
        } else {
            Fragment testfragment=mgr.findFragmentById(R.id.viewers);
            if(testfragment.getTag()!=null) {
                if (testfragment.getTag().equalsIgnoreCase("BackCurrentTrip")) {
                    mgr.popBackStack();
                }
            }else{
                /*Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                super.onBackPressed();
            }
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
