package com.bpatech.trucktracking.Activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bpatech.trucktracking.Fragment.AddnewTripFragment;
import com.bpatech.trucktracking.Fragment.AddphoneFragment;
import com.bpatech.trucktracking.Fragment.InviteFragment;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Service.UpdateLocationService;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;
import com.bpatech.app.uninstall.UninstallIntentReceiver;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity {

MySQLiteHelper db;
private Button nbtn;
private EditText phoneno;
SessionManager session;
public static final String MyPREFERENCES = "MyPrefs" ;
	/*private Callbacks mCallbacks;

	public interface Callbacks {
		public void onBackPressedCallback();
	}*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_fragment);
		nbtn=(Button)findViewById(R.id.nextbtn);
		phoneno=(EditText)findViewById(R.id.phoneno);
		UninstallIntentReceiver broadreceiver=new UninstallIntentReceiver();
		Intent intent = new Intent(this.getApplicationContext(), UpdateLocationService.class);
		startService(intent);
		/*IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
		intentFilter.addDataScheme("package");
		registerReceiver(broadreceiver, intentFilter);*/
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
			 if (mgr.getBackStackEntryCount() == 0) {
				 // No backstack to pop, so calling super
				 super.onBackPressed();
			 } else {
				 Fragment testfragment=mgr.findFragmentById(R.id.viewers);
				 if(testfragment.getTag()!=null) {
					 System.out.println("+++++testfragment.getTag()++++++"+testfragment.getTag());
					 if (testfragment.getTag().equalsIgnoreCase("BackCurrentTrip")) {
						 mgr.popBackStack();
					 }
				 }else{
					 super.onBackPressed();
				 }
			 }

		 }
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}




}
