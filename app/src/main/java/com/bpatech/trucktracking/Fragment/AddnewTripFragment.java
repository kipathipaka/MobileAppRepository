package com.bpatech.trucktracking.Fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AddnewTripFragment extends Fragment {
	SessionManager session;
	Button addbtn;
	EditText editdestination,editride,editphoneno,customer,customer_name,customer_no,source;
	TextView txt_contTitle;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
	        View view = inflater.inflate(R.layout.addnewtrip_layout, container, false);
	        
	        txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
	        txt_contTitle.setText("Add Trips");
	        addbtn=(Button)view.findViewById(R.id.addbtn);
	        editdestination=(EditText)view.findViewById(R.id.editdestination);
	        editride=(EditText)view.findViewById(R.id.edittruckno);
	        editphoneno=(EditText)view.findViewById(R.id.editphoneno);
		 source=(EditText)view.findViewById(R.id.source_edittext);
		 customer=(EditText)view.findViewById(R.id.editcustomer);
		 customer_name=(EditText)view.findViewById(R.id.editcustomername);
		 customer_no=(EditText)view.findViewById(R.id.editcustomerno);
		 customer_no=(EditText)view.findViewById(R.id.editcustomerno);

	        addbtn.setOnClickListener(new MyaddButtonListener());
	        return view;
	    }
	 
	 private class MyaddButtonListener implements OnClickListener {


		 @Override
		 public void onClick(View v) {

			 try {
				 InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				 inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				 if (editdestination.getText().toString().trim().equalsIgnoreCase("") || editride.getText().toString().trim().equalsIgnoreCase("") || editphoneno.getText().toString().trim().equalsIgnoreCase("")) {
					 Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
							 Toast.LENGTH_SHORT).show();
					/* AddnewTripFragment addfrag = new AddnewTripFragment();
					 FragmentManager fragmentmanager = getFragmentManager();
					 FragmentTransaction fragmenttransaction = fragmentmanager
							 .beginTransaction();
					 fragmenttransaction.replace(R.id.viewers, addfrag, "BackCurrentTrip");

					 fragmenttransaction.addToBackStack(null);
					 fragmenttransaction.commit();*/

				 } else {
					 AddTrip addtrip = new AddTrip();
					 List<AddTrip> currentDetailsList = new ArrayList<AddTrip>();
					 addtrip.setDestination(editdestination.getText().toString());
					 addtrip.setTruckno(editride.getText().toString());
					 addtrip.setPhone_no(editphoneno.getText().toString());
					 addtrip.setCustomer(customer.getText().toString());
					 addtrip.setCustomer_name(customer_name.getText().toString());
					 addtrip.setCustomer_no(customer_no.getText().toString());
					 addtrip.setSource(source.getText().toString());
					 currentDetailsList.add(addtrip);

					 SessionManager.setAddtripdetails(currentDetailsList);
					 CurrentTripFragment currenttripfrag = new CurrentTripFragment();
					 FragmentManager fragmentmanager = getFragmentManager();
					 FragmentTransaction fragmenttransaction = fragmentmanager
							 .beginTransaction();
					 fragmenttransaction.replace(R.id.viewers, currenttripfrag);

					 fragmenttransaction.addToBackStack(null);
					 fragmenttransaction.commit();

				 }
			 } catch (Exception e) {
				 Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
						 Toast.LENGTH_SHORT).show();
				 e.printStackTrace();
			 }
		 }
	 }

	/*public void onBackPressed() {
		// TODO Auto-generated method stub
		FragmentManager mgr = getFragmentManager();
		mgr.popBackStack();
		}*/

	}

