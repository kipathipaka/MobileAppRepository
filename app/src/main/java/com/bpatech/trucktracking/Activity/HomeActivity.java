package com.bpatech.trucktracking.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class HomeActivity extends FragmentActivity  {

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
		int phonecount = db.getUserCount();
		//System.out.println("********************phonecount************************** sync call end ..." + phonecount);
		  if (phonecount > 0) {
			  setContentView(R.layout.currenttrip_fragment);
			/*  if (googleApiClient == null) {
				  googleApiClient = new GoogleApiClient.Builder(this)
						  .addConnectionCallbacks(this)
						  .addOnConnectionFailedListener(this)
						  .addApi(LocationServices.API)
						  .build();
				  googleApiClient.connect();

				  LocationRequest locationRequest = LocationRequest.create();
				  locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
				  locationRequest.setInterval(30 * 1000);
				  locationRequest.setFastestInterval(5 * 1000);
				  LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
						  .addLocationRequest(locationRequest);

				  // **************************
				  builder.setAlwaysShow(true); // this is the key ingredient
				  // **************************

				  PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
						  .checkLocationSettings(googleApiClient, builder.build());
				  result.setResultCallback(new ResultCallback<LocationSettingsResult>() {


					  @Override
					  public void onResult(LocationSettingsResult result) {
						  final Status status = result.getStatus();
						  final LocationSettingsStates state = result
								  .getLocationSettingsStates();
						  switch (status.getStatusCode()) {
							  case LocationSettingsStatusCodes.SUCCESS:
								  // All location settings are satisfied. The client can
								  // initialize location
								  // requests here.
								  break;
							  case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
								  // Location settings are not satisfied. But could be
								  // fixed by showing the user
								  // a dialog.
								  try {
									  // Show the dialog by calling
									  // startResolutionForResult(),
									  // and check the result in onActivityResult().
									  status.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
								  } catch (IntentSender.SendIntentException e) {
									  // Ignore the error.
								  }
								  break;
							  case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
								  // Location settings are not satisfied. However, we have
								  // no way to fix the
								  // settings so we won't show the dialog.
								  break;
						  }
					  }

				  });

			  }*/
		  }else{
			  setContentView(R.layout.home_fragment);
		  }
			/*AlarmManager alarmManager=(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			Intent intentR = new Intent(getApplicationContext(), UpdateLocationReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentR, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000,
					pendingIntent);*/
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

		System.out.println("*****************home***mgr*********************** ..." + mgr.getBackStackEntryCount());
		if (mgr.getBackStackEntryCount() == 0) {
			/*Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
		switch (requestCode) {
			case REQUEST_CHECK_SETTINGS:
				switch (resultCode) {
					case Activity.RESULT_OK:
						// All required changes were successfully made
						if (googleApiClient.isConnected() ) {
							//startLocationUpdates();
						}
						break;
					case Activity.RESULT_CANCELED:
						// The user was asked to change settings, but chose not to
						break;
					default:
						break;
				}
				break;
		}
	}*/

	@Override
	public void onResume() {
		super.onResume();
	}

}
