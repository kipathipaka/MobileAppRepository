package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;
import com.bpatech.trucktracking.Service.MySQLiteHelper;

import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {
	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneNo;
	ImageButton imageButtonopen;
	TextView destination,truck,phoneno,txt_contTitle;
	LinearLayout listlayout_ll;
	SessionManager session;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
		 View view;
		 db=new MySQLiteHelper(getActivity().getApplicationContext());
		 //boolean value=db.checkPhonenumber(phoneno);
		 int phonecount=db.getUserCount();

		 if (phonecount > 0) {
			 view = inflater.inflate(R.layout.currenttriplist_layout, container, false);
			 txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
			 txt_contTitle.setText("Current Trips");
			 listlayout_ll=(LinearLayout)view.findViewById(R.id.currenttriplist_ll);

			 ListView listView = (ListView) view.findViewById(R.id.listview);
			 if (SessionManager.getAddtripdetails() != null && SessionManager.getAddtripdetails().size() > 0) {
				// System.out.println("list"+SessionManager.getAddtripdetails().get(0));
				 int listsize=SessionManager.getAddtripdetails().size();

				 ArrayList<AddTrip> currenttripdetails = new ArrayList<AddTrip>();
				 currenttripdetails.addAll(SessionManager.getAddtripdetails());
				 CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), currenttripdetails);
				 listView.setAdapter(adapter);
				 listView.setDivider(null);
				 listView.setDividerHeight(0);

			 }


			 listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				 @Override
				 public void onItemClick(AdapterView<?> parent, View view, int position,
										 long id) {
					 TextView place=(TextView)view.findViewById(R.id.place);
					 TextView Truck=(TextView)view.findViewById(R.id.rideno);
					 TextView phone=(TextView)view.findViewById(R.id.phoneno);
					 TextView customer=(TextView)view.findViewById(R.id.customer);
					 TextView customer_name=(TextView)view.findViewById(R.id.customername);
					 TextView customer_no=(TextView)view.findViewById(R.id.customerno);

					 String placeval = place.getText().toString();
					 String Truckval = Truck.getText().toString();
					 String phoneval = phone.getText().toString();
					 String customerval = customer.getText().toString();
					 String customer_nameval = customer_name.getText().toString();
					 String customer_noval = customer_no.getText().toString();
					 TaskDetailFragment taskdetailfrag=new TaskDetailFragment();
					 Bundle bundle=new Bundle();
					 bundle.putString(ServiceConstants.CUURENT_TRIP_PLACE,placeval);
					 bundle.putString(ServiceConstants.CUURENT_TRIP_TRUCK,Truckval);
					 bundle.putString(ServiceConstants.CUURENT_TRIP_PHONE, phoneval);
					 bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER, customerval);
					 bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME, customer_nameval);
					 bundle.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NO, customer_noval);
					 bundle.putBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE, false);
					 taskdetailfrag.setArguments(bundle);
					 //Toast.makeText(getActivity().getApplicationContext(), item, Toast.LENGTH_LONG).show();

					 FragmentManager fragmentmanager = getFragmentManager();
					 FragmentTransaction fragmenttransaction = fragmentmanager
							 .beginTransaction();
					 fragmenttransaction.replace(R.id.viewers, taskdetailfrag,"BackCurrentTrip");

					 fragmenttransaction.addToBackStack(null);
					 fragmenttransaction.commit();

				 }
			 });


		 }else {
			 view = inflater.inflate(R.layout.activity_home, container, true);

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
				session=new SessionManager(getActivity().getApplicationContext());  
				  String savephoneno = phoneNo.getText().toString();
				 // boolean exitnumber=Isexitphonenumber(phoneNo);
				  System.out.println("phoneNo "+phoneNo);
				  String sms="OTP to Verifying your Mobile Number for My Trip: ";
				  Random random = new Random();
					  int otpno = generateValue();
				  System.out.println("randomInteger"+otpno);
				try {

					session.setPhoneno(savephoneno);
					session.setOTPno(otpno);
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(savephoneno, null, sms+otpno, null, null);
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

	int generateValue()
	{
		Random random = new Random();
		int otpno = random.nextInt(900000)+100000;
		return otpno;
	}
}