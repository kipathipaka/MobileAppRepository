package com.bpatech.trucktracking.Fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class AddphoneFragment extends Fragment {
	Button addbtn;
	EditText edityournum;
	AddUserObjectParsing obj;
	Request request;
	SessionManager session;
	ProgressBar progressBar;
	String phonenumber;
	String responseStrng = null;
	TextView txt_contTitle;
	RelativeLayout addPhoneLayout;
	boolean checkdriverStatus = false;
	JSONArray responsejSONArray;
	String add_Driver_Message;
	String number;
	List<String> driverphonenolist;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.addphone_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		progressBar = (ProgressBar) view.findViewById(R.id.addphoneprogresbar);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.INVISIBLE);
		txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
		txt_contTitle.setText("Add Driver");
		addbtn = (Button) view.findViewById(R.id.addbtn);
		addPhoneLayout = (RelativeLayout) view.findViewById(R.id.addphone_layout);
		edityournum = (EditText) view.findViewById(R.id.edityournum);
		obj = new AddUserObjectParsing();
		session = new SessionManager(getActivity().getApplicationContext());
		request = new Request(getActivity());
		addPhoneLayout.setOnClickListener(new AddPhoneLayoutclicklistener());
		addbtn.setOnClickListener(new MyaddButtonListener());
		return view;
	}

	private class AddPhoneLayoutclicklistener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

		}
	}

	private class MyaddButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			progressBar.setVisibility(View.VISIBLE);
			try {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (edityournum.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				}
				else if(session.getPhoneno().toString().trim().equalsIgnoreCase(edityournum.getText().toString()))
				{
					//System.out.println("number" + session.getPhoneno());
					Toast.makeText(getActivity().getApplicationContext(), "Owner cannot be a Driver!",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				}
				else if (edityournum.getText().toString().length() == 10) {
					progressBar.setVisibility(View.VISIBLE);
					phonenumber = edityournum.getText().toString();
		/*String number="+91"+phonenumber;
		String smsmessage = ServiceConstants.MESSAGE_FOR_ADDPHONE;
		SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, smsmessage, null, null);
		Log.d("Sms", "sendSMS " + smsmessage);
		Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!"+number,
				Toast.LENGTH_SHORT).show();*/
					new GetdriverPhonelist().execute("", "", "");
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "enter the valid phone number!",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				}
			} catch (Exception e) {
				Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
				progressBar.setVisibility(View.INVISIBLE);
			}
		}
	}

	private class AddUserPhone extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			//progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				List<NameValuePair> driverphonelist = new ArrayList<NameValuePair>();
				//System.out.println("+++++++++++++++sizeeeeeeee+++++++++++++++" + session.getDriverlist().size());
				driverphonelist.addAll(obj.addDriverPhone(phonenumber, session.getPhoneno()));
				HttpResponse response = request.requestPostType(
						ServiceConstants.ADD_DRIVER_PHONE, driverphonelist, ServiceConstants.BASE_URL);
				responseStrng = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					CurrentTripFragment currenttripfrag = new CurrentTripFragment();
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, currenttripfrag);
					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();
					//new GetdriverPhonelist().execute("", "", "");
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
			progressBar.setVisibility(View.INVISIBLE);

		}

		protected String doInBackground(String... params) {

			try {
				driverphonenolist = new ArrayList<String>();
				//driverphonenolist.addAll(obj.getDriverPhone(session.getPhoneno()));
				String get_driver_url= ServiceConstants.GET_DRIVER+session.getPhoneno();
				HttpResponse response = request.requestGetType(get_driver_url, ServiceConstants.BASE_URL);
				responseStrng = ""+response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					 responsejSONArray = request.responseArrayParsing(response);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									checkdriverStatus=false;
									if (responsejSONArray != null) {
										GetDriverListParsing getDriverListParsing = new GetDriverListParsing();
										driverphonenolist.addAll(getDriverListParsing.driverPhonenumberlist(responsejSONArray));
										session.setDriverlist(driverphonenolist);
										//System.out.println("+++++++++++++++sizeeeeeeee+++++++++++++++"+session.getDriverlist().size());
										if (session.getDriverlist() != null && session.getDriverlist().size() > 0) {
											List<String> driverlist = new ArrayList<String>();
											driverlist.addAll(session.getDriverlist());
											for (int i = 0; i < driverlist.size(); i++) {
												//System.out.println("+++++++++++++++sizeeeeeeee+++list++++++++++++"+driverlist.get(i).toString());
												if (driverlist.get(i).toString().equalsIgnoreCase(phonenumber)) {
													checkdriverStatus = true;

												}
											}
										} else {
											checkdriverStatus = false;
										}
									}else {
										checkdriverStatus = false;
									}
									if (checkdriverStatus == false) {
										String owner_phone_no = session.getUsername();
										if(session.getMessagelist().size()>0){
											add_Driver_Message=owner_phone_no + " "+session.getMessagelist().get(0).getAdd_driver_message();
											//edittexview1.setText(invite_message);
										}else{

											String sms1 = ServiceConstants.MESSAGE_INVITE;
											String sms2 = ServiceConstants.APP_NAME;
											String sms3 = ServiceConstants.TEXT_MESSAGE_URL;
											 add_Driver_Message = owner_phone_no + " " + sms1 + " " + sms2 + " " + sms3;
										}

										number = "+91" + phonenumber;
										SmsManager smsManager = SmsManager.getDefault();
										smsManager.sendTextMessage(number, null, add_Driver_Message, null, null);
										Log.d("Sms", "sendSMS " + add_Driver_Message);
										Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!" + number,
												Toast.LENGTH_LONG).show();
										new AddUserPhone().execute("", "", "");
									} else {
										Toast.makeText(getActivity().getApplicationContext(), "This Driver PhoneNumber Already Added",
												Toast.LENGTH_LONG).show();
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
