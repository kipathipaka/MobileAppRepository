package com.bpatech.trucktracking.Fragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class DetailFragment extends Fragment implements LocationListener {
	MySQLiteHelper db;
	public Button debtn;
	AddUserObjectParsing obj;
	Request request;
	public EditText companyname, username;
	User user;
	ProgressBar progressBar;
	SessionManager session;
	ArrayList<AddTrip> currenttripdetails;
	HttpResponse response;
	String responseStrng = null;
	protected LocationManager locationManager;

	Location location;
	Double latitude=0.0;
	Double longitude=0.0;
	String locationVal =null;
	String fullAddress = null;
	String responsevalue;


	// flag for network status
	boolean isNetworkEnabled = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.companydetail_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		debtn = (Button) view.findViewById(R.id.detbtn);
		companyname = (EditText) view.findViewById(R.id.editcompanynamee);
		username = (EditText) view.findViewById(R.id.edityourname);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		progressBar.setProgress(10);
		progressBar.setMax(100);
		progressBar.setVisibility(View.VISIBLE);
		obj = new AddUserObjectParsing();
		request = new Request(getActivity());
		user = new User();
		session = new SessionManager(getActivity().getApplicationContext());
		getLocation();
		debtn.setOnClickListener(new MyNextButtonListener());
		return view;
	}



	private class MyNextButtonListener implements OnClickListener {


		@Override
		public void onClick(View v) {
			try {
				progressBar.setVisibility(View.VISIBLE);
				currenttripdetails = new ArrayList<AddTrip>();
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (companyname.getText().toString().trim().equalsIgnoreCase("") || username.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(getActivity().getApplicationContext(), " Value is  empty!",
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				} else {

					user.setPhone_no(session.getPhoneno());
					user.setCompanyName(companyname.getText().toString());
					user.setUserName(username.getText().toString());
					session.setUsername(username.getText().toString());



					new AddUserDetail().execute("", "", "");

				}


			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}


	void InsertUser(User user) {

		db = new MySQLiteHelper(getActivity().getApplicationContext());
		db.addUser(user);

		Log.d("Insert: ", "Inserting ..");
	}

	private class AddUserDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {

			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {

				List<NameValuePair> updateuserlist = new ArrayList<NameValuePair>();
				List<NameValuePair> createuserlist = new ArrayList<NameValuePair>();
				if(fullAddress==null || locationVal==null || latitude.toString()==null || latitude.toString()==null) {

					updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(),"null", "null","null", "null", "Y", "Y", user.getUserName()));
					createuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(),"null", "null", "null", "null", "Y", "Y", user.getUserName()));


				}else {

					createuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), locationVal, fullAddress, "Y", "Y", user.getUserName()));
					updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), locationVal, fullAddress, "Y", "Y", user.getUserName()));
				}
				String Getuser_url = ServiceConstants.GET_USER + session.getPhoneno();
				response = request.requestGetType(Getuser_url, ServiceConstants.BASE_URL);
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject responsejson = request.responseParsing(response);

					if (responsejson != null) {

						response = request.requestPutType(ServiceConstants.UPDATE_USER, updateuserlist, ServiceConstants.BASE_URL);
						responseStrng = "" + response.getStatusLine().getStatusCode();
						if (response.getStatusLine().getStatusCode() == 200) {
							InsertUser(user);
							CurrentTripFragment currenttripfrag = new CurrentTripFragment();
							FragmentManager fragmentmanager = getFragmentManager();
							FragmentTransaction fragmenttransaction = fragmentmanager
									.beginTransaction();
							fragmenttransaction.replace(R.id.viewers, currenttripfrag);
							fragmenttransaction.addToBackStack(null);
							fragmenttransaction.commit();

						}

					} else {

						response = request.requestPostType(
								ServiceConstants.CREATE_USER, createuserlist, ServiceConstants.BASE_URL);
						responseStrng = "" + response.getStatusLine().getStatusCode();
						if (response.getStatusLine().getStatusCode() == 200) {

							InsertUser(user);
							CurrentTripFragment currenttripfrag = new CurrentTripFragment();
							FragmentManager fragmentmanager = getFragmentManager();
							FragmentTransaction fragmenttransaction = fragmentmanager
									.beginTransaction();
							fragmenttransaction.replace(R.id.viewers, currenttripfrag);
							fragmenttransaction.addToBackStack(null);
							fragmenttransaction.commit();
						}
					}
				} else {

					response = request.requestPostType(
							ServiceConstants.CREATE_USER, createuserlist, ServiceConstants.BASE_URL);
					responseStrng = "" + response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode() == 200) {

						InsertUser(user);
						CurrentTripFragment currenttripfrag = new CurrentTripFragment();
						FragmentManager fragmentmanager = getFragmentManager();
						FragmentTransaction fragmenttransaction = fragmentmanager
								.beginTransaction();
						fragmenttransaction.replace(R.id.viewers, currenttripfrag);
						fragmenttransaction.addToBackStack(null);
						fragmenttransaction.commit();

					}
				}

			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}

	public void getLocation() {



		locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {



			try {
				isNetworkEnabled = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception ex) {

			}
			if (!isNetworkEnabled) {
				locationVal=null;
				fullAddress=null;
				latitude=0.0;
				longitude=0.0;




			} else {
				if (isNetworkEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

							if(location!=null) {
								UpdateLocation(location);
							}
						} else {

							Toast.makeText(getActivity().getApplicationContext(), "no location found", Toast.LENGTH_SHORT).show();
						}
					}
				}

			}
		}else{



			locationVal=null;
			fullAddress=null;
			latitude=0.0;
			longitude=0.0;

		}

		progressBar.setVisibility(View.INVISIBLE);
	}

	public void UpdateLocation(Location updateLocation) {


		if (updateLocation != null) {
			latitude = updateLocation.getLatitude();
			longitude = updateLocation.getLongitude();

			new GetAddressFromJson().execute("", "", "");

		}else{

			locationVal=null;
			fullAddress=null;
			latitude=0.0;
			longitude=0.0;
		}

	}
	private class GetAddressFromJson extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {
			InputStream is = null;
			String result = "";
			JSONObject jsonObj=null;
			try {



				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +latitude+ "," +longitude );
				HttpResponse response = httpclient.execute(httppost);
				responsevalue = "" + response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				if (result != null){
					jsonObj = new JSONObject(result);
					String Status = jsonObj.getString("status");
					if (Status.equalsIgnoreCase("OK")) {
						JSONArray Results = jsonObj.getJSONArray("results");
						JSONObject zero = Results.getJSONObject(0);
						JSONArray address_components = zero.getJSONArray("address_components");

						for (int i = 0; i < address_components.length(); i++) {
							JSONObject zero2 = address_components.getJSONObject(i);
							String long_name = zero2.getString("long_name");

							JSONArray mtypes = zero2.getJSONArray("types");

							String Type = mtypes.getString(0);
							if (TextUtils.isEmpty(long_name) == false || long_name != null || long_name.length() > 0 || long_name != "") {
								if (Type.equalsIgnoreCase("sublocality_level_1")) {
									if (fullAddress == null) {
										fullAddress = long_name + ",";
									}
								} else if (Type.equalsIgnoreCase("sublocality")) {
									if (fullAddress == null) {
										fullAddress = long_name + ",";
									}
								} else if (Type.equalsIgnoreCase("locality")) {

									locationVal = long_name;
									if (fullAddress != null) {
										fullAddress = fullAddress + long_name;
									} else {
										fullAddress = long_name;
									}
								} else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
									if (fullAddress == null || locationVal == null) {
										locationVal = long_name;
										fullAddress = long_name;

									}
								} else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
									if (fullAddress == null || locationVal == null) {
										locationVal = long_name;
										fullAddress = long_name;
									}
								}
							}



						}


					}
				}
			} catch (Exception e) {


				e.printStackTrace();

			}

			return responsevalue;

		}
	}


	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

}
