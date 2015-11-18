package com.bpatech.trucktracking.Activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.bpatech.trucktracking.Service.UpdateLocationService;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.SessionManager;

public class HomeActivity extends FragmentActivity {

	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneno;
	SessionManager session;
	public static final String MyPREFERENCES = "MyPrefs" ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new MySQLiteHelper(this.getApplicationContext());
		//boolean value=db.checkPhonenumber(phoneno);
		int phonecount = db.getUserCount();
		System.out.println("********************phonecount************************** sync call end ..."+phonecount);
		if (phonecount > 0) {
			setContentView(R.layout.currenttrip_fragment);
		}else {
			setContentView(R.layout.home_fragment);
		}
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		Intent intent = new Intent(this.getApplicationContext(), UpdateLocationService.class);
		startService(intent);

		/*Intent intent1=new Intent(this.getApplicationContext(), UpdateLocationReceiver.class);
		startService(intent1);*/

		/*AlertDialog alertDialog = new AlertDialog.Builder(
				getApplicationContext()).create();
		alertDialog.show();*/
		/*AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (20 * 1000), intent);
		//startService(intent);*/
		/*Intent broadintent = new Intent(this, UpdateLocationReceiver.class);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (20 * 1000), broadintent);*/
		//sendBroadcast(broadintent);
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
