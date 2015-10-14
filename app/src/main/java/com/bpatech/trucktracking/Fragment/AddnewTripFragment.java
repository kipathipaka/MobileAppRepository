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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AddnewTripFragment extends Fragment {
	SessionManager session;
	private Spinner phonespinner;
	Button addbtn;
	String source="chennai";
	EditText editdestination,editride,customer,customer_name,customer_no;
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
	       // editphoneno=(EditText)view.findViewById(R.id.editphoneno);
		 //source=(EditText)view.findViewById(R.id.source_edittext);
		 customer=(EditText)view.findViewById(R.id.editcustomer);
		 customer_name=(EditText)view.findViewById(R.id.editcustomername);
		 customer_no=(EditText)view.findViewById(R.id.editcustomerno);
		 customer_no=(EditText)view.findViewById(R.id.editcustomerno);
		 phonespinner= (Spinner)view.findViewById(R.id.phonnospinner);
		 addItemsOnSpinner2();
	        addbtn.setOnClickListener(new MyaddButtonListener());
	        return view;
	    }
	 
	 private class MyaddButtonListener implements OnClickListener {


		 @Override
		 public void onClick(View v) {
			 try {
				 InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				 inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				 if (editdestination.getText().toString().trim().equalsIgnoreCase("") || editride.getText().toString().trim().equalsIgnoreCase("") ) {
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
					 addtrip.setPhone_no(String.valueOf(phonespinner.getSelectedItem()));
					 addtrip.setCustomer(customer.getText().toString());
					 addtrip.setCustomer_name(customer_name.getText().toString());
					 addtrip.setCustomer_no(customer_no.getText().toString());
					// addtrip.setSource(source.getText().toString());
					 addtrip.setSource(source);
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
	public void addItemsOnSpinner2() {

		List list = new ArrayList();
		list.add("Choose Phone number");

		list.add("+919962437832");

		list.add("+919943502341");

		list.add("+917786345217");
		list.add("+919432940987");
		ArrayAdapter dataAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		phonespinner.setAdapter(dataAdapter);

	}

	/*public void onBackPressed() {
		// TODO Auto-generated method stub
		FragmentManager mgr = getFragmentManager();
		mgr.popBackStack();
		}*/

	}

