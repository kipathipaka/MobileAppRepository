package com.bpatech.trucktracking.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.MessageDTO;
import com.bpatech.trucktracking.Fragment.AddnewTripFragment;
import com.bpatech.trucktracking.Fragment.AddphoneFragment;
import com.bpatech.trucktracking.Fragment.CurrentTripFragment;
import com.bpatech.trucktracking.Fragment.InviteFragment;
import com.bpatech.trucktracking.Fragment.TaskDetailFragment;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Service.UpdateLocationReceiver;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;
import com.crittercism.app.Crittercism;
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
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity  implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneno;
	SessionManager session;
	Location mLastLocation;
	public static final String MyPREFERENCES = "MyPrefs";
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
		Crittercism.initialize(getApplicationContext(), "5653ff028d4d8c0a00d08333");
		db = new MySQLiteHelper(this.getApplicationContext());
		 phonecount = db.getUserCount();
         session=new SessionManager(this.getApplicationContext());
		obj = new AddUserObjectParsing();
		request= new Request(this);
		m_handler = new Handler();
		m_handler.postDelayed(m_statusChecker,0);

			googleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
			googleApiClient.connect();

			locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
		setContentView(R.layout.currenttrip_fragment);
			}else{
			setContentView(R.layout.home_fragment);
			}
		}

	}

private	Runnable m_statusChecker = new Runnable()
	{
		@Override
		public void run() {
			//System.out.println("+++++++++++++++++counter 1..."+counter);
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
		//addphonefragment.sett
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

		//System.out.println("*****************home***mgr*********************** ..." + mgr.getBackStackEntryCount());
		if (mgr.getBackStackEntryCount() == 0) {
			//Intent intent = new Intent(Intent.ACTION_MAIN);
			//intent.addCategory(Intent.CATEGORY_HOME);
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//startActivity(intent);
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
					fragmenttransaction.replace(R.id.viewers, currentfrag);
					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();
				}
			} else {
				//Intent intent = new Intent(Intent.ACTION_MAIN);
				//intent.addCategory(Intent.CATEGORY_HOME);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//startActivity(intent);
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
		//final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
		switch (requestCode) {
			case REQUEST_CHECK_SETTINGS:
				switch (resultCode) {
					case Activity.RESULT_OK:
						// All required changes were successfully made
						if (googleApiClient.isConnected()) {
							//startLocationUpdates();
							//System.out.println("++++++++++++++++++++connected++++++++++++++++++..");
							m_handler.postDelayed(m_statusChecker, m_interval);
						}
						break;
					case Activity.RESULT_CANCELED:
						//System.out.println("++++++++++++++++++++cancel++++++++++++++++++..");
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
	public void onStart()
	{
		super.onStart();
	}
	@Override
	public void onResume() {
		super.onResume();
		Toast.makeText(this.getApplicationContext(),"onresume activity",Toast.LENGTH_LONG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onConnected(Bundle bundle) {
		//Toast.makeText(this.getApplicationContext(), "enter location connected method", Toast.LENGTH_LONG).show();
		/*mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				googleApiClient);
		if (mLastLocation != null) {
			//System.out.println("+++++++++++++++++++++++++++++++++Latitude and longitude++++++++++++++++++++++++++" + String.valueOf(mLastLocation.getLatitude())+"------"+String.valueOf(mLastLocation.getLongitude()));
			//Toast.makeText(this.getApplicationContext(), "Latitude and longtitude......" + String.valueOf(mLastLocation.getLatitude()) + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();
			//mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
			//mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
			Geocoder geocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
			if (geocoder != null) {
				try {
					List<Address> addressList = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(), 1);
					if (addressList != null && addressList.size() > 0) {
						Address address = addressList.get(0);

					}

				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		}

*/
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
						counter=5;
						m_handler.postDelayed(m_statusChecker,m_interval);
						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						// Location settings are not satisfied. But could be
						// fixed by showing the user
						// a dialog.
						try {
							//System.out.println("++++++++++++++++++++Resolution++++++++++++++++++..");
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
						//System.out.println("++++++++++++++++++++change+++unavailable+++++++++++++++..");
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
				//System.out.println("++++++++++++++message++++++apil call++++++++++++++++++..");
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
