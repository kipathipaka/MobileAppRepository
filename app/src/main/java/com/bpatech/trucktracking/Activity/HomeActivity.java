package com.bpatech.trucktracking.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.MessageDTO;
import com.bpatech.trucktracking.Fragment.AddnewTripFragment;
import com.bpatech.trucktracking.Fragment.AddphoneFragment;
import com.bpatech.trucktracking.Fragment.CurrentTripFragment;
import com.bpatech.trucktracking.Fragment.InviteFragment;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ServiceConstants;
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

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity  implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	MySQLiteHelper db;
	SessionManager session;

	private GoogleApiClient googleApiClient;
	public static final int REQUEST_CHECK_SETTINGS = 1000;
	private int m_interval =3 * 60 * 1000; // 5 seconds by default, can be changed later
	private int counter=5;
	private Handler m_handler;
	Request request;
	AddUserObjectParsing obj;
	String responseStrng;
	String trip_id;
	int phonecount;

	LocationRequest locationRequest;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new MySQLiteHelper(this.getApplicationContext());
		 phonecount = db.getUserCount();
         session=new SessionManager(this.getApplicationContext());
		obj = new AddUserObjectParsing();
		request= new Request(this);
		m_handler = new Handler();
		m_handler.postDelayed(m_statusChecker, 0);
		googleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
			googleApiClient.connect();
			locationRequest = LocationRequest.create();
if(locationRequest.getPriority() == 102) {

	locationRequest.setPriority(102);//LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
}else{

	locationRequest.setPriority(100);//LocationRequest.PRIORITY_HIGH_ACCURACY
}
			locationRequest.setInterval(30 * 1000);
			locationRequest.setFastestInterval(5 * 1000);
			new GetandStroeMessages().execute("", "", "");
		Intent i = getIntent();
		final String action = i.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			final List<String> segments = i.getData().getPathSegments();
			if (segments.size() > 0) {
				 trip_id = segments.get(segments.size() - 1);
				session.setVechil_trip_id(trip_id);
				if (phonecount > 0) {
					setContentView(R.layout.currenttrip_fragment);
				}
			}
		}else{
		if(phonecount>0){
			try {

				setContentView(R.layout.currenttrip_fragment);
			} catch (RuntimeException e) {
				Intent intent = new Intent(HomeActivity.this,
						HomeActivity.class);
				startActivity(intent);
			}

			}else{

				setContentView(R.layout.home_fragment);

			}
		}

	}

private	Runnable m_statusChecker = new Runnable()
	{
		@Override
		public void run() {

			Enable_location_popup(); //this function can change value of m_interval.
             counter--;
		}
	};

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
		if (session.getDriverlist() != null && session.getDriverlist().size() > 0) {
			AddnewTripFragment addtripfragment = new AddnewTripFragment();
			pageRedirection(addtripfragment);
		} else {
			Toast.makeText(this.getApplicationContext(), "Add at least one driver!",
					Toast.LENGTH_LONG).show();
		}


	}

	public void addphoneclick(View v) {

		AddphoneFragment addphonefragment = new AddphoneFragment();

		pageRedirection(addphonefragment);
	}

	public void addinviteclick(View v) {
		InviteFragment invitefragment = new InviteFragment();
		pageRedirection(invitefragment);
	}


	public void pageRedirection(Fragment fragment) {
		FragmentManager fragmentmanager = getFragmentManager();
		FragmentTransaction fragmenttransaction = fragmentmanager
				.beginTransaction();
		fragmenttransaction.replace(R.id.viewers, fragment, "BackCurrentTrip");
		fragmenttransaction.addToBackStack(null);
		fragmenttransaction.commit();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		FragmentManager mgr = getFragmentManager();


		if (mgr.getBackStackEntryCount() == 0) {

			super.onBackPressed();
		} else {
			Fragment testfragment = mgr.findFragmentById(R.id.viewers);
			if (testfragment.getTag() != null) {
				if (testfragment.getTag().equalsIgnoreCase("BackCurrentTrip")) {
					mgr.popBackStack();
				}else
				if (testfragment.getTag().equalsIgnoreCase("BackRefreshCurrentTrip")) {
					CurrentTripFragment currentfrag = new CurrentTripFragment();
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers,currentfrag);
					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();
				}
			} else {

				super.onBackPressed();
			}
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		switch (requestCode) {
			case REQUEST_CHECK_SETTINGS:
				switch (resultCode) {
					case Activity.RESULT_OK:
						// All required changes were successfully made
						if (googleApiClient.isConnected()) {


							m_handler.postDelayed(m_statusChecker, m_interval);

						}
						break;
					case Activity.RESULT_CANCELED:

						if(counter>0) {
							m_handler.postDelayed(m_statusChecker, m_interval);

						}
						// The user was asked to change settings, but chose not to
						break;
					default:
						break;
				}
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

		View promptsView = inflater.inflate(R.layout.network_failure, null);

		final Dialog dialog = new Dialog(getApplicationContext());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		promptsView.setBackgroundResource(R.color.white);
		dialog.setContentView(promptsView);
		dialog.dismiss();


	}

	@Override
	public void onConnected(Bundle bundle) {

	}


	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}


public void Enable_location_popup()
{
	if (googleApiClient != null) {
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(locationRequest);



		builder.setAlwaysShow(true); // this is the key ingredient

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
						counter=5;
						m_handler.postDelayed(m_statusChecker,m_interval);
						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						// Location settings are not satisfied. But could be
						// fixed by showing the user
						// a dialog.
						try {

							//m_handler.postDelayed(m_statusChecker,0);
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

	}
}

	private class GetandStroeMessages extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {

		}

		protected String doInBackground(String... params) {

			try {

				List<MessageDTO> messageDTOList = new ArrayList<MessageDTO>();
				String get_driver_url = ServiceConstants.GET_MESSAGE_URL;
				HttpResponse response = request.requestGetType(get_driver_url, ServiceConstants.BASE_URL);
				responseStrng = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject responsejSONoject = request.responseParsing(response);
					GetDriverListParsing getDriverListParsing = new GetDriverListParsing();
					messageDTOList.addAll(getDriverListParsing.MessageDTOList(responsejSONoject));
					session.setMessagelist(messageDTOList);

				}




			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}


}
