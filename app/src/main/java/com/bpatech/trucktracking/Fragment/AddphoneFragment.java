package com.bpatech.trucktracking.Fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;


public class AddphoneFragment extends Fragment {
	Button addbtn;
	EditText edityournum;

	TextView txt_contTitle;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
	        View view = inflater.inflate(R.layout.addphone_layout, container, false);
		 txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
		 txt_contTitle.setText("Add Phone");
		 addbtn=(Button)view.findViewById(R.id.addbtn);
		 edityournum=(EditText)view.findViewById(R.id.edityournum);
		 addbtn.setOnClickListener(new MyaddButtonListener());
	        return view;
	    }
	private class MyaddButtonListener implements View.OnClickListener{
		@Override
		public void onClick(View v)
		{
			if(edityournum.getText().toString().trim().equalsIgnoreCase(""))
			{
				Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
						Toast.LENGTH_LONG).show();
				AddphoneFragment addfragment= new AddphoneFragment();
				FragmentManager fragmentmanager = getFragmentManager();
				FragmentTransaction fragmenttransaction = fragmentmanager
						.beginTransaction();
				fragmenttransaction.replace(R.id.viewers, addfragment,"BackCurrentTrip");

				fragmenttransaction.addToBackStack(null);
				fragmenttransaction.commit();
			}
			else
			{

				String phonenumber= edityournum.getText().toString();
				String smsmessage="message sent to the required user";
				SmsManager smsManager= SmsManager.getDefault();
				//smsManager.sendTextMessage(phonenumber, null, smsmessage, null, null);
				Log.d("Sms", "sendSMS " + smsmessage);
				Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!",
						Toast.LENGTH_LONG).show();
				CurrentTripFragment currenttripfrag=new CurrentTripFragment();
				FragmentManager fragmentmanager = getFragmentManager();
				FragmentTransaction fragmenttransaction = fragmentmanager
						.beginTransaction();
				fragmenttransaction.replace(R.id.viewers,currenttripfrag);

				fragmenttransaction.addToBackStack(null);
				fragmenttransaction.commit();
			}
		}
	}
}
