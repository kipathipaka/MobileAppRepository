package com.bpatech.trucktracking.Fragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment {
	MySQLiteHelper db;
	public Button debtn;
	AddUserObjectParsing obj;
	Request request;
	public EditText companyname,username;
	User user;
	ProgressBar progressBar;
	SessionManager session;
	ArrayList<AddTrip> currenttripdetails;
	HttpResponse response;
	String responseStrng = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		View view = inflater.inflate(R.layout.companydetail_layout, container, false);
		debtn=(Button)view.findViewById(R.id.detbtn);
		companyname=(EditText)view.findViewById(R.id.editcompanynamee);
		username=(EditText)view.findViewById(R.id.edityourname);
		progressBar=(ProgressBar)view.findViewById(R.id.progressBar1);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.INVISIBLE);
		obj = new AddUserObjectParsing();
		request = new Request(getActivity());
		user = new User();
		session = new SessionManager(getActivity().getApplicationContext());
		debtn.setOnClickListener(new MyNextButtonListener());
		return view;
	}
	private class MyNextButtonListener implements OnClickListener {


		@Override
		public void onClick(View v) {
			try {
				progressBar.setVisibility(View.VISIBLE);
				currenttripdetails=new ArrayList<AddTrip>();
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (companyname.getText().toString().trim().equalsIgnoreCase("") || username.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(getActivity().getApplicationContext(), " Value is  empty!",
							Toast.LENGTH_SHORT).show();
					progressBar.setVisibility(View.INVISIBLE);
				} else {

					user.setPhone_no(session.getPhoneno());
					user.setCompanyName(companyname.getText().toString());
					user.setUserName(username.getText().toString());
					InsertUser(user);
					new AddUserDetail().execute("", "", "");
				}


			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}


	void InsertUser(User user) {

		db=new MySQLiteHelper(getActivity().getApplicationContext());
		db.addUser(user);
		Log.d("Insert: ", "Inserting ..");
	}

	private class AddUserDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {

			//progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {

				List<NameValuePair> createuserlist = new ArrayList<NameValuePair>();
				createuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), "Y","Y", user.getUserName()));
				String Getuser_url=ServiceConstants.GET_USER+ session.getPhoneno();
				response = request.requestGetType(Getuser_url, ServiceConstants.BASE_URL);
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject responsejson = request.responseParsing(response);
					String Updateuser_url=ServiceConstants.UPDATE_USER+ session.getPhoneno();
					List<NameValuePair> updateuserlist = new ArrayList<NameValuePair>();
					updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),"Y","Y",user.getUserName()));
					if(responsejson!=null) {
						response = request.requestPutType(ServiceConstants.UPDATE_USER,updateuserlist,ServiceConstants.BASE_URL);
						responseStrng = ""+response.getStatusLine().getStatusCode();
						if (response.getStatusLine().getStatusCode() == 200) {
							new GetMytripDetail().execute("", "", "");

						}

					}else{
						response = request.requestPostType(
								ServiceConstants.CREATE_USER, createuserlist,ServiceConstants.BASE_URL);
						responseStrng = ""+response.getStatusLine().getStatusCode();
						if (response.getStatusLine().getStatusCode() == 200) {
							new GetMytripDetail().execute("", "", "");


						}
					}
				}else{
					response = request.requestPostType(
							ServiceConstants.CREATE_USER, createuserlist,ServiceConstants.BASE_URL);
					responseStrng = ""+response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode() == 200) {
						new GetMytripDetail().execute("", "", "");

					}
				}

			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}

	private class GetMytripDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result)
		{

			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {
				String Gettrip_url = ServiceConstants.GET_TRIP + session.getPhoneno();
				HttpResponse response = request.requestGetType(Gettrip_url, ServiceConstants.BASE_URL);
				responseStrng = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONArray responsejSONArray = request.responseArrayParsing(response);
					GetMytripListParsing mytripListParsing = new GetMytripListParsing();
					currenttripdetails.addAll(mytripListParsing.getmytriplist(responsejSONArray));
					session.setAddtripdetails(currenttripdetails);
					CurrentTripFragment currenttripfrag = new CurrentTripFragment();
					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, currenttripfrag);

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
