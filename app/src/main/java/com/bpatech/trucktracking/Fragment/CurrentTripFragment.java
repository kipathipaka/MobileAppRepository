package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.TextView;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;


import org.apache.http.HttpResponse;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class CurrentTripFragment  extends Fragment {
	private static View view;
	private static Bundle b;
	SessionManager session;
	LinearLayout triplist_ll,footer_addtrip_ll;
	TextView txt_contTitle,triplistsize_view;
	Request request;
	String responseStrng;
	ArrayList<AddTrip> currenttriplist;
	ArrayList<AddTrip> currenttripdetails;
	ListView listView;
	private static ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		b= savedInstanceState;
		view = inflater.inflate(R.layout.currenttriplist_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		session = new SessionManager(getActivity().getApplicationContext());
		currenttripdetails=new ArrayList<AddTrip>();
		new GetMytripDetail().execute("", "", "");
		request= new Request(getActivity().getApplicationContext());
		progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.VISIBLE);
		txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
		txt_contTitle.setText("Current Trips");
		triplistsize_view=(TextView)view.findViewById(R.id.triplistsize_view);
		triplist_ll = (LinearLayout) view.findViewById(R.id.currenttriplist_view);
		triplist_ll.setVisibility(view.GONE);
		listView = (ListView)view.findViewById(R.id.listview);
		View footerLayout =view.findViewById(R.id.footer);

		footer_addtrip_ll=(LinearLayout)footerLayout.findViewById(R.id.addtrip_ll);
		if(session.getDriverlist()!=null && session.getDriverlist().size() > 0){
			footer_addtrip_ll.setEnabled(true);
			for (int i = 0; i < footer_addtrip_ll.getChildCount(); i++) {
				View child = footer_addtrip_ll.getChildAt(i);
				child.setEnabled(true);
			}
		}else{
			//footer_addtrip_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.footerbutton_inactive));
			footer_addtrip_ll.setEnabled(false);
			for (int i = 0; i < footer_addtrip_ll.getChildCount(); i++) {
				View child = footer_addtrip_ll.getChildAt(i);
				child.setEnabled(false);
			}

		}
		if(session.getAddtripdetails()!=null && session.getAddtripdetails().size() > 0){
			String triplisttext = "Available (" + SessionManager.getAddtripdetails().size() + ")";
			triplistsize_view.setText(triplisttext);
			triplist_ll.setVisibility(view.VISIBLE);

			ArrayList<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
			currenttripdetailslist.addAll(session.getAddtripdetails());
			// CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), currenttripdetailslist, savedInstanceState);
			// listView.setAdapter(adapter);

			// listView.setDividerHeight(5);

		}


		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				progressBar.setVisibility(View.VISIBLE);
				TextView place = (TextView) view.findViewById(R.id.place);
				TextView Truck = (TextView) view.findViewById(R.id.rideno);
				TextView phone = (TextView) view.findViewById(R.id.phoneno);
				TextView customer = (TextView) view.findViewById(R.id.customer);
				TextView customer_name = (TextView) view.findViewById(R.id.customername);
				TextView customer_no = (TextView) view.findViewById(R.id.customerno);
				TextView vehicle_id = (TextView) view.findViewById(R.id.vechiletrip_no);
				TextView source = (TextView) view.findViewById(R.id.nowvalue);
				String sourcetxt = source.getText().toString();
				String placeval = place.getText().toString();
				String Truckval = Truck.getText().toString();
				String phoneval = phone.getText().toString();
				String customerval = customer.getText().toString();
				String customer_nameval = customer_name.getText().toString();
				String customer_noval = customer_no.getText().toString();
				String vechile_trip_id = vehicle_id.getText().toString();

				TaskDetailFragment taskdetailfrag = new TaskDetailFragment();
				Bundle bundle = new Bundle();
				bundle.putString(ServiceConstants.CUURENT_TRIP_PLACE, placeval);
				bundle.putString(ServiceConstants.ADD_TRIP_SOURCE, sourcetxt);
				bundle.putString(ServiceConstants.CUURENT_TRIP_TRUCK, Truckval);
				bundle.putString(ServiceConstants.CUURENT_TRIP_PHONE, phoneval);
				bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER, customerval);
				bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME, customer_nameval);
				bundle.putString(ServiceConstants.VECHILE_TRIP_ID, vechile_trip_id);
				bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NO, customer_noval);
				bundle.putBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE, false);

				taskdetailfrag.setArguments(bundle);
				//progressBar.setVisibility(View.INVISIBLE);
				FragmentManager fragmentmanager = getFragmentManager();
				FragmentTransaction fragmenttransaction = fragmentmanager
						.beginTransaction();
				fragmenttransaction.replace(R.id.viewers, taskdetailfrag, "BackCurrentTrip");

				fragmenttransaction.addToBackStack(null);
				fragmenttransaction.commit();

			}
		});
		progressBar.setVisibility(View.INVISIBLE);
		return view;


	}

	private class GetMytripDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {

		}

		protected String doInBackground(String... params) {

			try {
				System.out.println("********************************************** sync call...");
				//progressBar.setVisibility(View.VISIBLE);
				String Gettrip_url = ServiceConstants.GET_TRIP + session.getPhoneno();
				HttpResponse response = request.requestGetType(Gettrip_url, ServiceConstants.BASE_URL);

				responseStrng = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetMytripListParsing mytripListParsing = new GetMytripListParsing();
					List<AddTrip> mytripdetailslist = new ArrayList<AddTrip>();
					mytripdetailslist.addAll(mytripListParsing.getmytriplist(responsejSONArray));
					session.setAddtripdetails(mytripdetailslist);
					System.out.println("********************************************** sync call end ...");
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								String triplisttext = "Available (" + SessionManager.getAddtripdetails().size() + ")";
								triplistsize_view.setText(triplisttext);
								triplist_ll.setVisibility(view.VISIBLE);
								ArrayList<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
								currenttripdetailslist.addAll(session.getAddtripdetails());

								// ArrayList<AddTrip> mytripdetailslist2 = new ArrayList<AddTrip>();
								// mytripdetailslist2.addAll(session.getAddtripdetails());
								CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(),currenttripdetailslist , b);
								listView.setAdapter(adapter);

								listView.setDividerHeight(5);
								System.out.println("********************************************** after list set ...");
								// btnDone.setVisibility(View.VISIBLE);
								progressBar.setVisibility(View.INVISIBLE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});



				}
			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}
	}


}
