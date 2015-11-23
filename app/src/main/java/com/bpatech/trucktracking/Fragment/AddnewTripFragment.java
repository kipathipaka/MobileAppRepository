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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AddnewTripFragment extends Fragment {
	MySQLiteHelper db;
	SessionManager session;
	private Spinner phonespinner;
	Button addbtn;
	Request request;
	ProgressBar progressBar;
	String responseStrng, responsevalue;
	String owner_phone_number;
	HttpResponse response;
	AddUserObjectParsing obj;
	String source = "chennai";
	List dropdowndriverphonenolist;
	ArrayList<AddTrip> currenttripdetails;
	AddTrip addtrip;
	EditText editdestination, editride, customer_company, customer_name, customer_phoneno;
	TextView txt_contTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.addnewtrip_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		db = new MySQLiteHelper(getActivity().getApplicationContext());
		request = new Request(getActivity());
		currenttripdetails = new ArrayList<AddTrip>();
		progressBar = (ProgressBar) view.findViewById(R.id.addprogresbar);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.VISIBLE);
		txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
		txt_contTitle.setText("Add Trips");
		addbtn = (Button) view.findViewById(R.id.addbtn);
		editdestination = (EditText) view.findViewById(R.id.editdestination);
		editride = (EditText) view.findViewById(R.id.edittruckno);
		dropdowndriverphonenolist = new ArrayList();
		addtrip = new AddTrip();
		customer_company = (EditText) view.findViewById(R.id.editcustomercompany);
		customer_name = (EditText) view.findViewById(R.id.editcustomername);
		customer_phoneno = (EditText) view.findViewById(R.id.editcustomerphoneno);
		phonespinner = (Spinner) view.findViewById(R.id.phonnospinner);
		session = new SessionManager(getActivity().getApplicationContext());
		obj = new AddUserObjectParsing();
		new GetdriverPhonelist().execute("", "", "");
		//addItemsOnSpinner2();
		addbtn.setOnClickListener(new MyaddButtonListener());

		return view;
	}

	private class MyaddButtonListener implements OnClickListener {


		@Override
		public void onClick(View v) {
			progressBar.setVisibility(View.VISIBLE);
			try {

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (editdestination.getText().toString().trim().equalsIgnoreCase("") || editride.getText().toString().trim().equalsIgnoreCase("") ||
						String.valueOf(phonespinner.getSelectedItem()).toString().trim().equalsIgnoreCase("Choose Phone number")
						|| String.valueOf(phonespinner.getSelectedItem()).toString().trim().equalsIgnoreCase("Add Phone number")
						|| customer_company.getText().toString().trim().equalsIgnoreCase("") ||
						customer_name.getText().toString().trim().equalsIgnoreCase("") || customer_phoneno.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);

				} else if (String.valueOf(phonespinner.getSelectedItem()).toString().trim().equalsIgnoreCase(customer_phoneno.getText().toString().trim()) ||
						String.valueOf(phonespinner.getSelectedItem()).toString().trim().equalsIgnoreCase(session.getPhoneno().toString().trim()) ||
						session.getPhoneno().toString().trim().equalsIgnoreCase(customer_phoneno.getText().toString().trim())) {
					Toast.makeText(getActivity().getApplicationContext(), "Entered Owner phonenumber,Customer phone number and driver phone are same.. Please Check ",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				} else if (customer_phoneno.getText().toString().length() == 10) {
					addtrip.setDestination(editdestination.getText().toString());
					addtrip.setTruckno(editride.getText().toString());
					addtrip.setDriver_phone_no(String.valueOf(phonespinner.getSelectedItem()));
					addtrip.setCustomer_company(customer_company.getText().toString());
					addtrip.setCustomer_name(customer_name.getText().toString());
					addtrip.setCustomer_phoneno(customer_phoneno.getText().toString());
					addtrip.setSource(source);
					new AddTripDetail().execute("", "", "");
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "enter the valid phone number!",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				}
			} catch (Exception e) {
				Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
						Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.INVISIBLE);
				e.printStackTrace();
			}
		}
	}

	/*public void addItemsOnSpinner2() {
		progressBar.setVisibility(View.VISIBLE);
		ArrayList<User> ownerlist = new ArrayList<User>();
		ownerlist.addAll(db.getOwnerphoneno());
		if (ownerlist != null && ownerlist.size() > 0) {
			owner_phone_number = ownerlist.get(0).getPhone_no();
			if (session.getDriverlist().size() > 0) {
				dropdowndriverphonenolist.add("Choose Phone number");
				dropdowndriverphonenolist.addAll(session.getDriverlist());
				ArrayAdapter dataAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, dropdowndriverphonenolist);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				phonespinner.setAdapter(dataAdapter);
			} else {

				List list = new ArrayList();
				list.add("Add Phone number");
				ArrayAdapter dataAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				phonespinner.setAdapter(dataAdapter);

			}


		}
		progressBar.setVisibility(View.INVISIBLE);
	}*/

	private class AddTripDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {

			//progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				List<NameValuePair> addtriplist = new ArrayList<NameValuePair>();
				addtriplist.addAll(obj.AddtripObject(addtrip.getTruckno(), addtrip.getDestination(), session.getPhoneno(),
						addtrip.getCustomer_company(), addtrip.getCustomer_name(), addtrip.getCustomer_phoneno(), addtrip.getDriver_phone_no()));
				HttpResponse response = request.requestPostType(
						ServiceConstants.ADD_TRIP, addtriplist, ServiceConstants.BASE_URL);
				responsevalue = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					CurrentTripFragment currenttripfrag = new CurrentTripFragment();
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, currenttripfrag);

					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();

				}
			} catch (Exception e) {

				e.printStackTrace();

			}

			return responsevalue;

		}

	}



		private class GetdriverPhonelist extends
				AsyncTask<String, Void, String> {
			@Override
			protected void onPostExecute(String result) {
				progressBar.setVisibility(View.INVISIBLE);

			}

			protected String doInBackground(String... params) {

				try {
					List<String> driverphonenolist = new ArrayList<String>();
					String get_driver_url = ServiceConstants.GET_DRIVER + session.getPhoneno();
					HttpResponse response = request.requestGetType(get_driver_url, ServiceConstants.BASE_URL);
					responseStrng = "" + response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode() == 200) {
						JSONArray responsejSONArray = request.responseArrayParsing(response);
						GetDriverListParsing getDriverListParsing = new GetDriverListParsing();
						driverphonenolist.addAll(getDriverListParsing.driverPhonenumberlist(responsejSONArray));
						session.setDriverlist(driverphonenolist);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									ArrayList<User> ownerlist = new ArrayList<User>();
									ownerlist.addAll(db.getOwnerphoneno());
									if (ownerlist != null && ownerlist.size() > 0) {
										owner_phone_number = ownerlist.get(0).getPhone_no();
										if (session.getDriverlist().size() > 0) {
											dropdowndriverphonenolist.add("Choose Phone number");
											dropdowndriverphonenolist.addAll(session.getDriverlist());
											ArrayAdapter dataAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, dropdowndriverphonenolist);
											dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
											phonespinner.setAdapter(dataAdapter);
										} else {

											List list = new ArrayList();
											list.add("Add Phone number");
											ArrayAdapter dataAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
											dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
											phonespinner.setAdapter(dataAdapter);

										}


									}
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

