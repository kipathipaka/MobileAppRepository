package com.bpatech.trucktracking.Fragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DetailFragment extends Fragment implements LocationListener{
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
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	Location location;
	Double latitude;
	Double longitude;
	String locationVal;
	StringBuilder fullAddress;
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.companydetail_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
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
		getLocation();
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
							Toast.LENGTH_LONG).show();
					progressBar.setVisibility(View.INVISIBLE);
				} else {

					user.setPhone_no(session.getPhoneno());
					user.setCompanyName(companyname.getText().toString());
					user.setUserName(username.getText().toString());
					session.setUsername(username.getText().toString());
					System.out.println("+++++++username+++++"+session.getUsername());
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
				createuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),latitude.toString(),longitude.toString(),locationVal.toString(),fullAddress.toString(), "Y","Y", user.getUserName()));
				String Getuser_url=ServiceConstants.GET_USER+ session.getPhoneno();
				response = request.requestGetType(Getuser_url, ServiceConstants.BASE_URL);
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject responsejson = request.responseParsing(response);
					String Updateuser_url=ServiceConstants.UPDATE_USER+ session.getPhoneno();
					List<NameValuePair> updateuserlist = new ArrayList<NameValuePair>();
					updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),latitude.toString(),longitude.toString(),locationVal.toString(),fullAddress.toString(),"Y","Y",user.getUserName()));
					if(responsejson!=null) {
						response = request.requestPutType(ServiceConstants.UPDATE_USER,updateuserlist,ServiceConstants.BASE_URL);
						responseStrng = ""+response.getStatusLine().getStatusCode();
						if (response.getStatusLine().getStatusCode() == 200) {
							/*Intent intent = new Intent(getActivity(), HomeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);*/
							CurrentTripFragment currenttripfrag = new CurrentTripFragment();
							FragmentManager fragmentmanager = getFragmentManager();
							FragmentTransaction fragmenttransaction = fragmentmanager
									.beginTransaction();
							fragmenttransaction.replace(R.id.viewers,currenttripfrag);
							fragmenttransaction.addToBackStack(null);
							fragmenttransaction.commit();

						}

					}else{
						response = request.requestPostType(
								ServiceConstants.CREATE_USER, createuserlist,ServiceConstants.BASE_URL);
						responseStrng = ""+response.getStatusLine().getStatusCode();
						if (response.getStatusLine().getStatusCode() == 200) {
							/*Intent intent = new Intent(getActivity(), HomeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);*/
							CurrentTripFragment currenttripfrag = new CurrentTripFragment();
							FragmentManager fragmentmanager = getFragmentManager();
							FragmentTransaction fragmenttransaction = fragmentmanager
									.beginTransaction();
							fragmenttransaction.replace(R.id.viewers,currenttripfrag);
							fragmenttransaction.addToBackStack(null);
							fragmenttransaction.commit();


						}
					}
				}else{
					response = request.requestPostType(
							ServiceConstants.CREATE_USER, createuserlist,ServiceConstants.BASE_URL);
					responseStrng = ""+response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode() == 200) {
						/*Intent intent = new Intent(getActivity(), HomeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);*/
						CurrentTripFragment currenttripfrag = new CurrentTripFragment();
						FragmentManager fragmentmanager = getFragmentManager();
						FragmentTransaction fragmenttransaction = fragmentmanager
								.beginTransaction();
						fragmenttransaction.replace(R.id.viewers,currenttripfrag);
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
		try {
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

		} catch (Exception ex) {
		}

		try {
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		if (!isGPSEnabled && !isNetworkEnabled) {

		} else {
			if (isNetworkEnabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
				if (locationManager != null) {
					location = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					UpdateLocation(location);
					System.out.println("++++++++++++++++++++++++++++++++++location++++++++++++" +
							"+++++++++++++++"+location.getLatitude()+""+location.getLongitude());
				}
			}
			if (isGPSEnabled) {
				if (location == null) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						System.out.println("++++++++++++++++++++++++++++++++++location+++++++++++++++++++++" +
								"++++++"+location.getLatitude()+""+location.getLongitude());
						UpdateLocation(location);

					}
				}
			}
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
	public void UpdateLocation(Location updateLocation) {
		if (updateLocation != null) {
			latitude = updateLocation.getLatitude();
			longitude = updateLocation.getLongitude();
			Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
			try {
				List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
				if (addressList != null && addressList.size() > 0) {
					Address address = addressList.get(0);
					fullAddress = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						if(address.getAddressLine(i)!=null) {
							fullAddress.append(address.getAddressLine(i)).append(",");
						}
                    }
                  /*  sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());*/
					if (address.getSubLocality() == null || address.getLocality() == null) {
						locationVal = null;
					} else {
						locationVal = address.getSubLocality().toString()+","
								+ address.getLocality().toString();
						//Toast.makeText(getActivity().getApplicationContext(), fullAddress.toString(), Toast.LENGTH_SHORT).show();
						//new UpdateLocationApi().execute("", "", "");
					}
				}


			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}
}
