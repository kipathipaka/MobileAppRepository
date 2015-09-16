package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.SessionManager;
import com.bpatech.trucktracking.Service.MySQLiteHelper;

import java.util.Random;

public class HomeFragment extends Fragment {
	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneno;
	SessionManager session;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {

	        View view = inflater.inflate(R.layout.activity_home, container, true);
	        nbtn=(Button)view.findViewById(R.id.nextbtn);
			phoneno=(EditText)view.findViewById(R.id.phoneno);
			nbtn.setOnClickListener(new MyButtonListener());
	        return view;
	    }
	 private class MyButtonListener implements OnClickListener {

	      
			@Override
			public void onClick(View v) {
				System.out.println("enter if main");
				session=new SessionManager(getActivity().getApplicationContext());  
				  String phoneNo = phoneno.getText().toString();
				  boolean exitnumber=Isexitphonenumber(phoneNo);
				  System.out.println("exitnumber "+exitnumber);
				  if(exitnumber==true){
					  CurrentTripFragment currenttripfrag=new CurrentTripFragment();
						FragmentManager fragmentmanager = getFragmentManager();
						FragmentTransaction fragmenttransaction = fragmentmanager
								.beginTransaction();
						fragmenttransaction.replace(R.id.viewers, currenttripfrag);
						
						fragmenttransaction.addToBackStack(null);
						fragmenttransaction.commit();

				  }
				  else{
					  
				  String sms="OTP to Verifying your Mobile Number for My Trip: ";
				  Random random = new Random();
					  int otpno = generateValue();
				  System.out.println("randomInteger"+otpno);
				try {

					session.setPhoneno(phoneNo);
					session.setOTPno(otpno);
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phoneNo, null, sms+otpno, null, null);
					Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!",
							Toast.LENGTH_LONG).show();
					
					DetailFragment detailfrag=new DetailFragment();
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, detailfrag);
					
					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();
					

					
				  } catch (Exception e) {
					Toast.makeText(getActivity().getApplicationContext(),
							"SMS faild, please try again later!",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				  }
				
				  }
			}
			}
boolean Isexitphonenumber(String phoneno) {
		 
		 db=new MySQLiteHelper(getActivity().getApplicationContext());
		boolean value=db.checkPhonenumber(phoneno);
		
		
		return value;
		
	 }
	int generateValue()
	{
		Random random = new Random();
		int otpno = random.nextInt(900000)+100000;
		return otpno;
	}
}
