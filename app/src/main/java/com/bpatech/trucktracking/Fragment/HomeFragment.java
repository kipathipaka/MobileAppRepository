package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneNo;
	Request request;
	String responseStrng;
	public ArrayList<AddTrip> currenttriplist;
	public ArrayList<AddTrip> currenttripdetails;
	ImageButton imageButtonopen;
	ImageView carlogo;
	ProgressBar progressBar,progressBar1;
	TextView destination, truck, phoneno, txt_contTitle, triplistsize_view;
	LinearLayout listlayout_ll, triplist_ll,footer_addtrip_ll;
	ListView listView;
	SessionManager session;
	AddUserObjectParsing obj;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		///Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		View view;
		//view = inflater.inflate(R.layout.currenttrip_progressbar_layout, container, false);
	/*	progressBar1=(ProgressBar)view.findViewById(R.id.currentlistprogresbar);
		progressBar1.setProgress(10);
		progressBar1.setMax(10);
		progressBar1.setVisibility(View.VISIBLE);*/
		db = new MySQLiteHelper(getActivity().getApplicationContext());
		//boolean value=db.checkPhonenumber(phoneno);
		int phonecount = db.getUserCount();
		obj = new AddUserObjectParsing();
		request= new Request(getActivity());

		if (phonecount > 0) {
			//progressBar1.setVisibility(View.INVISIBLE);
			view = inflater.inflate(R.layout.currenttriplist_layout, container, false);
			Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
			progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar);
			progressBar.setProgress(10);
			progressBar.setMax(10);
			progressBar.setVisibility(View.INVISIBLE);
			session = new SessionManager(getActivity().getApplicationContext());
			currenttripdetails=new ArrayList<AddTrip>();
			txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
			txt_contTitle.setText("Current Trips");
			triplistsize_view = (TextView) view.findViewById(R.id.triplistsize_view);
			triplist_ll = (LinearLayout) view.findViewById(R.id.currenttriplist_view);
			triplist_ll.setVisibility(view.GONE);
			listView = (ListView) view.findViewById(R.id.listview);
			View footerLayout =view.findViewById(R.id.footer);
			footer_addtrip_ll=(LinearLayout)footerLayout.findViewById(R.id.addtrip_ll);
			try {
				new CheckdriverPhonelist().execute("", "", "");
				Thread.sleep(5000);
				//progressBar.setVisibility(View.VISIBLE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(session.getAddtripdetails()!=null && session.getAddtripdetails().size() > 0){
				String triplisttext = "Available (" + session.getAddtripdetails().size() + ")";
				triplistsize_view.setText(triplisttext);
				triplist_ll.setVisibility(view.VISIBLE);

				ArrayList<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
				currenttripdetailslist.addAll(session.getAddtripdetails());
				CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), currenttripdetailslist, savedInstanceState);
				listView.setAdapter(adapter);
				listView.setDividerHeight(5);

			}

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
					TextView source = (TextView) view.findViewById(R.id.nowvalue);
					TextView vehicle_id = (TextView) view.findViewById(R.id.vechiletrip_no);
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
					bundle.putString(ServiceConstants.ADD_TRIP_SOURCE, sourcetxt);
					bundle.putString(ServiceConstants.CUURENT_TRIP_PLACE, placeval);
					bundle.putString(ServiceConstants.CUURENT_TRIP_TRUCK, Truckval);
					bundle.putString(ServiceConstants.CUURENT_TRIP_PHONE, phoneval);
					bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER, customerval);
					bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME, customer_nameval);
					bundle.putString(ServiceConstants.VECHILE_TRIP_ID, vechile_trip_id);
					bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NO, customer_noval);
					bundle.putBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE, false);
					taskdetailfrag.setArguments(bundle);
					progressBar.setVisibility(View.INVISIBLE);
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, taskdetailfrag, "BackCurrentTrip");

					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();

				}
			});


		} else {
			//progressBar1.setVisibility(View.INVISIBLE);

			view = inflater.inflate(R.layout.activity_home, container, false);
			Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
			progressBar=(ProgressBar)view.findViewById(R.id.loginprogressbar);
			progressBar.setProgress(10);
			progressBar.setMax(100);
			progressBar.setVisibility(View.INVISIBLE);
			txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
			txt_contTitle.setText("Welcome");
			carlogo=(ImageView)view.findViewById(R.id.car_logo);
			carlogo.setVisibility(view.GONE);
			nbtn = (Button) view.findViewById(R.id.nextbtn);
			phoneNo = (EditText) view.findViewById(R.id.phoneno);
			nbtn.setOnClickListener(new MyButtonListener());
		}
		return view;
	}

	private class MyButtonListener implements OnClickListener {


		@Override
		public void onClick(View v) {
			System.out.println("enter if main");
			progressBar.setVisibility(View.VISIBLE);
try {
	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
	inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	if (phoneNo.getText().toString().equals("")) {
		Toast.makeText(getActivity().getApplicationContext(), " Value is  empty!",
				Toast.LENGTH_SHORT).show();
		progressBar.setVisibility(View.INVISIBLE);

	}  else if(phoneNo.getText().toString().length()==10){

		session = new SessionManager(getActivity().getApplicationContext());
		String savephoneno = phoneNo.getText().toString();
		System.out.println("phoneNo " + phoneNo);
		session.setPhoneno(savephoneno);
		new GetdriverPhonelist().execute("", "", "");


	}
	else
	{
		Toast.makeText(getActivity().getApplicationContext(), "enter the valid phone number!",
				Toast.LENGTH_SHORT).show();
		progressBar.setVisibility(View.INVISIBLE);
	}
} catch(Exception e)
{
	Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
			Toast.LENGTH_SHORT).show();
	e.printStackTrace();
	progressBar.setVisibility(View.INVISIBLE);
}
		}

		int generateValue() {
			Random random = new Random();
			int otpno = random.nextInt(900000) + 100000;
			return otpno;
		}
	}
	private class GettripDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				progressBar.setVisibility(View.VISIBLE);
				String Gettrip_url=ServiceConstants.GET_TRIP+session.getPhoneno();
				HttpResponse response = request.requestGetType(Gettrip_url,ServiceConstants.BASE_URL);
				responseStrng = ""+response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetMytripListParsing mytripListParsing= new GetMytripListParsing();
					currenttripdetails.addAll(mytripListParsing.getmytriplist(responsejSONArray));
					session.setAddtripdetails(currenttripdetails);
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

			//progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				List<String> driverphonenolist = new ArrayList<String>();
				//driverphonenolist.addAll(obj.getDriverPhone(session.getPhoneno()));
				String get_driver_url= ServiceConstants.GET_DRIVER+session.getPhoneno();
				HttpResponse response = request.requestGetType(get_driver_url, ServiceConstants.BASE_URL);
				responseStrng = ""+response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetDriverListParsing getDriverListParsing = new GetDriverListParsing();
					driverphonenolist.addAll(getDriverListParsing.driverPhonenumberlist(responsejSONArray));
					session.setDriverlist(driverphonenolist);
					DetailFragment detailfrag = new DetailFragment();

					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, detailfrag, "BackCurrentTrip");

					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}
	private class CheckdriverPhonelist extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
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
			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				List<String> driverphonenolist = new ArrayList<String>();
				//driverphonenolist.addAll(obj.getDriverPhone(session.getPhoneno()));
				String get_driver_url= ServiceConstants.GET_DRIVER+session.getPhoneno();
				HttpResponse response = request.requestGetType(get_driver_url, ServiceConstants.BASE_URL);
				responseStrng = ""+response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetDriverListParsing getDriverListParsing = new GetDriverListParsing();
					driverphonenolist.addAll(getDriverListParsing.driverPhonenumberlist(responsejSONArray));
					session.setDriverlist(driverphonenolist);
					new GettripDetail().execute("", "", "");

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}
}




