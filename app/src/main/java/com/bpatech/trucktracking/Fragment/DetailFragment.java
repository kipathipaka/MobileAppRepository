package com.bpatech.trucktracking.Fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bpatech.trucktracking.Activity.HomeActivity;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.SessionManager;
import com.bpatech.trucktracking.Service.MySQLiteHelper;


public class DetailFragment extends Fragment {
	MySQLiteHelper db;
	private Button debtn;
	private EditText otpno,companyname,username;
	SessionManager session;
	CurrentTripFragment currentfragment = new CurrentTripFragment();
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {

	        View view = inflater.inflate(R.layout.companydetail_layout, container, false);
	        debtn=(Button)view.findViewById(R.id.detbtn);
			otpno=(EditText)view.findViewById(R.id.editotp);
			companyname=(EditText)view.findViewById(R.id.editcompanynamee);
			username=(EditText)view.findViewById(R.id.edityourname);
	        debtn.setOnClickListener(new MyNextButtonListener());
	        return view;
	    }
	 private class MyNextButtonListener implements OnClickListener {

	      
			@Override
			public void onClick(View v) {
				

				if(otpno.getText().toString().trim().equalsIgnoreCase("")){
					System.out.println("++++++++++++++"+otpno.getText().toString());
					Toast.makeText(getActivity().getApplicationContext(), "OTP Value is not empty!",
							Toast.LENGTH_LONG).show();
						DetailFragment detailfrag=new DetailFragment();
						FragmentManager fragmentmanager = getFragmentManager();
						FragmentTransaction fragmenttransaction = fragmentmanager
								.beginTransaction();
						fragmenttransaction.replace(R.id.viewers, detailfrag);
						
						fragmenttransaction.addToBackStack(null);
						fragmenttransaction.commit();
					}else{
					
				User user=new User();
				session=new SessionManager(getActivity().getApplicationContext());
				int	otpnumber= Integer.parseInt(otpno.getText().toString());
			int sessionstored_otp=session.getOTPno();
			if(otpnumber==sessionstored_otp){
			user.setOtp_no(sessionstored_otp);
			//user.setPhone_no(sharedpreferences.getString("phoneno", null).toString());
			user.setPhone_no(session.getPhoneno());
			user.setCompanyName(companyname.getText().toString());
			user.setUserName(username.getText().toString());
			InsertUser(user);
			//session.removesession();
			CurrentTripFragment currenttripfrag=new CurrentTripFragment();
			FragmentManager fragmentmanager = getFragmentManager();
			FragmentTransaction fragmenttransaction = fragmentmanager
					.beginTransaction();
			fragmenttransaction.replace(R.id.viewers, currenttripfrag);
			
			fragmenttransaction.addToBackStack(null);
			fragmenttransaction.commit();
			}
				 
			else{
				Toast.makeText(getActivity().getApplicationContext(), "OTP is not correct!",
						Toast.LENGTH_LONG).show();
				DetailFragment detailfrag=new DetailFragment();
				FragmentManager fragmentmanager = getFragmentManager();
				FragmentTransaction fragmenttransaction = fragmentmanager
						.beginTransaction();
				fragmenttransaction.replace(R.id.viewers,detailfrag,"BackCurrentTrip");
				
				fragmenttransaction.addToBackStack(null);
				fragmenttransaction.commit();
			}
			}
			
	 
			
			}
	 
	 }
	 void InsertUser(User user) {
		 
		 db=new MySQLiteHelper(getActivity().getApplicationContext());
		db.addUser(user);
		 Log.d("Insert: ", "Inserting ..");
	 }
	 


}
