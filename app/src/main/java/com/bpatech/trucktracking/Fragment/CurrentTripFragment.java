package com.bpatech.trucktracking.Fragment;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Service.UpdateLocationReceiver;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class CurrentTripFragment  extends Fragment  {
	private static Bundle b;
	SessionManager session;
	LinearLayout triplist_ll,footer_addtrip_ll,footerlayout;
	TextView txt_contTitle,triplistsize_view;
	Request request;
	String responseStrng;
	RelativeLayout currenttripLayout;

	ArrayList<AddTrip> currenttripdetails;
	ListView listView;

	View view;

	String trip_id;

	AddUserObjectParsing obj;
	private static ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		 view = inflater.inflate(R.layout.currenttriplist_layout, container, false);

		b= savedInstanceState;
		session = new SessionManager(getActivity());
		obj = new AddUserObjectParsing();


		if(session.getAlaramcount()== 0) {
			AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			Intent intentR = new Intent(getActivity().getApplicationContext(), UpdateLocationReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intentR,0);

			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),20 * 60 * 1000,
					pendingIntent);
			session.setAlaramcount(1);
		}
		request= new Request(getActivity());
			if(session.getVechil_trip_id()!=null) {
				trip_id = session.getVechil_trip_id();
				session.setVechil_trip_id(null);
				new SaveVechileTripId().execute("", "", "");

			}

		request= new Request(getActivity());
		currenttripdetails=new ArrayList<AddTrip>();
		progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.VISIBLE);

		new GetMytripDetail().execute("", "", "");
		txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
		txt_contTitle.setText("Current Trips");
		triplistsize_view=(TextView)view.findViewById(R.id.triplistsize_view);
		triplist_ll = (LinearLayout) view.findViewById(R.id.currenttriplist_view);
		currenttripLayout = (RelativeLayout) view.findViewById(R.id.currenttriplayout);
		footerlayout = (LinearLayout) view.findViewById(R.id.footerlayout);

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

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								String triplisttext ="My Account : "+session.getUsername()+" - "+session.getPhoneno();
								triplistsize_view.setText(triplisttext);
								triplist_ll.setVisibility(View.VISIBLE);
								ArrayList<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
								currenttripdetailslist.addAll(session.getAddtripdetails());
								CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), currenttripdetailslist, b);
								listView.setAdapter(adapter);

								listView.setDividerHeight(5);
								footerlayout.setVisibility(View.VISIBLE);



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



				}



			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}
	private class SaveVechileTripId extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {


		}

		protected String doInBackground(String... params) {

			try {
				List<NameValuePair> savetripiddetails = new ArrayList<NameValuePair>();
				savetripiddetails.addAll(obj.SaveTripId(trip_id,session.getPhoneno()));
				String get_driver_url= ServiceConstants.SAVE_TRIP_ID_URL;
				HttpResponse response = request.requestPostType(get_driver_url, savetripiddetails, ServiceConstants.BASE_URL);

				responseStrng = ""+response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					TaskDetailFragment taskdetailfrag = new TaskDetailFragment();
					Bundle bundle = new Bundle();
					bundle.putString(ServiceConstants.VECHILE_TRIP_ID, trip_id);
					taskdetailfrag.setArguments(bundle);
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, taskdetailfrag, "BackRefreshCurrentTrip");
					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();


				}



			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}
	@Override
	public void onResume() {
		super.onResume();

	}

}
