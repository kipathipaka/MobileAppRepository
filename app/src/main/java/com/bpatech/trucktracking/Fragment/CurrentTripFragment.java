package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import java.util.ArrayList;

public class CurrentTripFragment  extends Fragment {
	SessionManager session;
	Button openlink,openlink1;
	TextView destination,truck,phoneno,txt_contTitle;
	LinearLayout footer_addphone,footer_addtrip,footer_invite;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
	        View view = inflater.inflate(R.layout.currenttriplist_layout, container, false);
	        txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
	        txt_contTitle.setText("Current Trips");
	       // openlink = (Button)view.findViewById(R.id.openbtn);

		 ListView listView = (ListView)view.findViewById(R.id.listview);
	 if(SessionManager.getAddtripdetails()!=null && SessionManager.getAddtripdetails().size() > 0)
	        {

		  ArrayList<AddTrip> currenttripdetails=new ArrayList<AddTrip>();
		  currenttripdetails.addAll(SessionManager.getAddtripdetails());
		  CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(),currenttripdetails);
		  

		  listView.setAdapter(adapter);
	        	  
	        }

		 listView.setOnItemClickListener(new OnItemClickListener() {
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


		 return view;
	        
	        
	    }
	 
	 


	 
	
	 
}
