package com.bpatech.trucktracking.Fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Service.UpdateLocationReceiver;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ExceptionHandler;
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
import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class CurrentTripFragment  extends Fragment  {
	private static Bundle b;
	SessionManager session;
	LinearLayout triplist_ll,footer_addtrip_ll;
	TextView txt_contTitle,triplistsize_view;
	Request request;
	String responseStrng;
	RelativeLayout currenttripLayout;
	ArrayList<AddTrip> currenttriplist;
	ArrayList<AddTrip> currenttripdetails;
	ListView listView;
	View view;
	private static ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		b= savedInstanceState;
		 view = inflater.inflate(R.layout.currenttriplist_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		AlarmManager alarmManager=(AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		Intent intentR = new Intent( getActivity().getApplicationContext(), UpdateLocationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast( getActivity().getApplicationContext(), 0, intentR, 0);
		//intentR.setAction(UpdateLocationReceiver.);//the same as up
		//boolean isWorking = (PendingIntent.getBroadcast(getActivity(), 1001, intentR, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
		//Log.d(TAG, "alarm is " + (isWorking ? "" : "not") + " working...");
		//System.out.println("********************************isWorking************** sync call end ..."+isWorking);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+60 * 1000,20 * 60 * 1000,
				pendingIntent);
		session = new SessionManager(getActivity());
		request= new Request(getActivity());
		currenttripdetails=new ArrayList<AddTrip>();
		progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.INVISIBLE);
		/*DateFormat dateFormat = new SimpleDateFormat("MMM,dd h:mm a");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
		Date date = new Date(System.currentTimeMillis());
		System.out.println("+++++++++++++time  ++++++++++++++"+dateFormat.format(date).toString());*/
		new GetMytripDetail().execute("", "", "");
		txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
		txt_contTitle.setText("Current Trips");
		triplistsize_view=(TextView)view.findViewById(R.id.triplistsize_view);
		triplist_ll = (LinearLayout) view.findViewById(R.id.currenttriplist_view);
		currenttripLayout = (RelativeLayout) view.findViewById(R.id.currenttriplayout);
		currenttripLayout.setOnClickListener(new Layoutclicklistener());
		triplist_ll.setVisibility(view.GONE);
		listView = (ListView)view.findViewById(R.id.listview);
		View footerLayout =view.findViewById(R.id.footer);
		footer_addtrip_ll=(LinearLayout)footerLayout.findViewById(R.id.addtrip_ll);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				progressBar.setVisibility(View.VISIBLE);

				TextView vehicle_id = (TextView) view.findViewById(R.id.vechiletrip_no);
				String vechile_trip_id = vehicle_id.getText().toString();

				TaskDetailFragment taskdetailfrag = new TaskDetailFragment();
				Bundle bundle = new Bundle();
				bundle.putString(ServiceConstants.VECHILE_TRIP_ID, vechile_trip_id);
				bundle.putBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE, false);
				progressBar.setVisibility(View.INVISIBLE);
				taskdetailfrag.setArguments(bundle);
				FragmentManager fragmentmanager = getFragmentManager();
				FragmentTransaction fragmenttransaction = fragmentmanager
						.beginTransaction();
				fragmenttransaction.replace(R.id.viewers, taskdetailfrag, "BackCurrentTrip");
				fragmenttransaction.addToBackStack(null);
				fragmenttransaction.commit();

			}
		});

		return view;


	}


	private class Layoutclicklistener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

		}
	}

	private class GetMytripDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				//System.out.println("********************************************** sync call...");
				progressBar.setVisibility(View.VISIBLE);
				String Gettrip_url = ServiceConstants.GET_TRIP + session.getPhoneno();
				HttpResponse response = request.requestGetType(Gettrip_url,ServiceConstants.BASE_URL);

				responseStrng = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetMytripListParsing mytripListParsing = new GetMytripListParsing();
					List<AddTrip> mytripdetailslist = new ArrayList<AddTrip>();
					mytripdetailslist.addAll(mytripListParsing.getmytriplist(responsejSONArray));
					session.setAddtripdetails(mytripdetailslist);
					//System.out.println("********************************************** sync call end ...");
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								String triplisttext = "Available (" + SessionManager.getAddtripdetails().size() + ")";
								triplistsize_view.setText(triplisttext);
								triplist_ll.setVisibility(View.VISIBLE);
								ArrayList<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
								currenttripdetailslist.addAll(session.getAddtripdetails());
								CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), currenttripdetailslist, b);
								listView.setAdapter(adapter);

								listView.setDividerHeight(5);
								//System.out.println("********************************************** after list set ...");
								// btnDone.setVisibility(View.VISIBLE);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

					new GetdriverPhonelist().execute("", "", "");
				}
			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}
	}

	private class GetdriverPhonelist extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {


		}

		protected String doInBackground(String... params) {

			try {
				List<String> driverphonenolist = new ArrayList<String>();
				String get_driver_url= ServiceConstants.GET_DRIVER+session.getPhoneno();
				HttpResponse response = request.requestGetType(get_driver_url, ServiceConstants.BASE_URL);
				responseStrng = ""+response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetDriverListParsing getDriverListParsing = new GetDriverListParsing();
					if(responsejSONArray!=null) {
						driverphonenolist.addAll(getDriverListParsing.driverPhonenumberlist(responsejSONArray));
						session.setDriverlist(driverphonenolist);
					}
					/* not require*/
					/*getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								if(session.getDriverlist()!=null && session.getDriverlist().size() > 0){
									footer_addtrip_ll.setEnabled(true);
									for (int i = 0; i < footer_addtrip_ll.getChildCount(); i++) {
										View child = footer_addtrip_ll.getChildAt(i);
										child.setEnabled(true);
									}
								}else{
									footer_addtrip_ll.setEnabled(false);
									//footer_addtrip_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.footerbutton_inactive));
									for (int i = 0; i < footer_addtrip_ll.getChildCount(); i++) {
										View child = footer_addtrip_ll.getChildAt(i);
										child.setEnabled(false);
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});*/



				}



			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

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
