package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.GetDriverListParsing;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurrentTripFragment  extends Fragment {
	SessionManager session;
LinearLayout triplist_ll;
	TextView txt_contTitle,triplistsize_view;
	Request request;
	String responseStrng;
	ArrayList<AddTrip> currenttriplist;
	ArrayList<AddTrip> currenttripdetails;
	ListView listView;
	ProgressBar progressBar;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {

	        View view = inflater.inflate(R.layout.currenttriplist_layout, container, false);
	        txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
	        txt_contTitle.setText("Current Trips");
		 triplistsize_view=(TextView)view.findViewById(R.id.triplistsize_view);
		 triplist_ll=(LinearLayout)view.findViewById(R.id.currenttriplist_view);
		 triplist_ll.setVisibility(view.GONE);
		 progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar);
		 progressBar.setProgress(10);
		 progressBar.setMax(100);
		 progressBar.setVisibility(View.INVISIBLE);
		 currenttripdetails=new ArrayList<AddTrip>();
		 request= new Request(getActivity());
		 session = new SessionManager(getActivity().getApplicationContext());
		  listView = (ListView)view.findViewById(R.id.listview);

		 try{
			 new GetMytripDetail().execute("", "", "");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 System.out.println("+++++currenttriplist++++++list+++++++++++++"+session.getAddtripdetails().size());
		 if(session.getAddtripdetails()!=null && session.getAddtripdetails().size() > 0){
			 String triplisttext = "Available (" + SessionManager.getAddtripdetails().size() + ")";
			 triplistsize_view.setText(triplisttext);
			 triplist_ll.setVisibility(view.VISIBLE);

			 ArrayList<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
			 currenttripdetailslist.addAll(session.getAddtripdetails());
			 CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), currenttripdetailslist, savedInstanceState);
			 listView.setAdapter(adapter);

			 listView.setDividerHeight(5);

		 }


		 /*if(currenttriplist!=null && currenttriplist.size() > 0)
		 {
			 String triplisttext="Available Trips ("+ currenttriplist.size()+")";
			 triplistsize_view.setText(triplisttext);
			 triplist_ll.setVisibility(view.VISIBLE);
			 CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(),currenttriplist,savedInstanceState);
			 listView.setDividerHeight(5);
			 listView.setAdapter(adapter);

		 }*/

	/* if(SessionManager.getAddtripdetails()!=null && SessionManager.getAddtripdetails().size() > 0)
	        {
    String triplisttext="Available Trips ("+ SessionManager.getAddtripdetails().size()+")";
				triplistsize_view.setText(triplisttext);
				triplist_ll.setVisibility(view.VISIBLE);
		  ArrayList<AddTrip> currenttripdetails=new ArrayList<AddTrip>();
		  currenttripdetails.addAll(SessionManager.getAddtripdetails());
		  CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(),currenttripdetails,savedInstanceState);
				listView.setDividerHeight(5);
		  listView.setAdapter(adapter);

	        }*/

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
				 TextView source=(TextView)view.findViewById(R.id.nowvalue);
				 String sourcetxt=source.getText().toString();
				 String placeval = place.getText().toString();
				 String Truckval = Truck.getText().toString();
				 String phoneval = phone.getText().toString();
				 String customerval = customer.getText().toString();
				 String customer_nameval = customer_name.getText().toString();
				 String customer_noval = customer_no.getText().toString();
				 TaskDetailFragment taskdetailfrag=new TaskDetailFragment();
				 Bundle bundle=new Bundle();
				 bundle.putString(ServiceConstants.CUURENT_TRIP_PLACE,placeval);
				 bundle.putString(ServiceConstants.ADD_TRIP_SOURCE,sourcetxt);
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



	private class GetMytripDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				progressBar.setVisibility(View.VISIBLE);
				System.out.println("++++phone no++++++++" + session.getPhoneno());
				String Gettrip_url=ServiceConstants.GET_TRIP+session.getPhoneno();
				System.out.println("++++statuscode++++++++"+Gettrip_url);
				HttpResponse response = request.requestGetType(Gettrip_url,ServiceConstants.BASE_URL);

				responseStrng = ""+response.getStatusLine().getStatusCode();
				System.out.println("++++statuscode++++++++"+response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					System.out.println("+++++++++++responsejSONArray+++++++++++" + responsejSONArray.toString());
					GetMytripListParsing mytripListParsing= new GetMytripListParsing();
					currenttripdetails.addAll(mytripListParsing.getmytriplist(responsejSONArray));
					System.out.println("+++++++++++size111+++++++++++" + currenttripdetails.size());
					session.setAddtripdetails(currenttripdetails);

				}
			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}


	 
}
