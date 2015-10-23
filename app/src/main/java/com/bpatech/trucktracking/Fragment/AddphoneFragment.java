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
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

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
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
	        View view = inflater.inflate(R.layout.addphone_layout, container, false);
		 progressBar=(ProgressBar)view.findViewById(R.id.addphoneprogresbar);
		 progressBar.setProgress(10);
		 progressBar.setMax(100);
		 progressBar.setVisibility(View.INVISIBLE);
		 txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
		 txt_contTitle.setText("Add Phone");
		 addbtn=(Button)view.findViewById(R.id.addbtn);
		 edityournum=(EditText)view.findViewById(R.id.edityournum);
		 obj = new AddUserObjectParsing();
		 request = new Request(getActivity());
		 addbtn.setOnClickListener(new MyaddButtonListener());
	        return view;
	    }
	private class MyaddButtonListener implements View.OnClickListener{
		@Override
		public void onClick(View v)
		{
			progressBar.setVisibility(View.VISIBLE);
try {
	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
	inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	if (edityournum.getText().toString().trim().equalsIgnoreCase("")) {
		Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
				Toast.LENGTH_SHORT).show();
		progressBar.setVisibility(View.INVISIBLE);
	}
	else if(edityournum.getText().toString().length()==10){

	phonenumber = edityournum.getText().toString();
		String smsmessage = "message sent to the required user";
		SmsManager smsManager = SmsManager.getDefault();
		session = new SessionManager(getActivity().getApplicationContext());
		smsManager.sendTextMessage(phonenumber, null, smsmessage, null, null);
		Log.d("Sms", "sendSMS " + smsmessage);

		Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!",
				Toast.LENGTH_SHORT).show();
		new AddUserPhone().execute("", "", "");
		/*CurrentTripFragment currenttripfrag = new CurrentTripFragment();
		FragmentManager fragmentmanager = getFragmentManager();
		FragmentTransaction fragmenttransaction = fragmentmanager
				.beginTransaction();
		fragmenttransaction.replace(R.id.viewers, currenttripfrag);

		fragmenttransaction.addToBackStack(null);
		fragmenttransaction.commit();*/
	}
	else
	{
		Toast.makeText(getActivity().getApplicationContext(), "enter the valid phone number!",
				Toast.LENGTH_SHORT).show();
	}
}catch (Exception e){
	Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
			Toast.LENGTH_SHORT).show();
	e.printStackTrace();
	progressBar.setVisibility(View.INVISIBLE);
}
		}
	}

	private class AddUserPhone extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				System.out.println("++++statuscode++++++++"+session.getPhoneno());
				List<NameValuePair> driverphonelist = new ArrayList<NameValuePair>();
				driverphonelist.addAll(obj.addDriverPhone(phonenumber,session.getPhoneno()));
				System.out.println("++++driverphonelist++++++++"+driverphonelist);
				HttpResponse response = request.requestPostType(
						ServiceConstants.ADD_DRIVER_PHONE,driverphonelist,ServiceConstants.BASE_URL);
				responseStrng = ""+response.getStatusLine().getStatusCode();
				System.out.println("++++statuscode++++++++"+response.getStatusLine().getStatusCode());
				JSONObject responsejson = request.responseParsing(response);
				System.out.println("++++responsejson++++++++" + responsejson);
				if (response.getStatusLine().getStatusCode() == 200) {
					CurrentTripFragment currenttripfrag = new CurrentTripFragment();
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers,currenttripfrag);

					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();

				}
			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}
}
