package com.bpatech.trucktracking.Fragment;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	protected LocationListener locationListener;
	Location location;
	Double latitude;
	Double longitude;
	String locationVal =null;
	String fullAddress = null;
	String responsevalue;
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.companydetail_layout, container, false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
		Timber.i("Inside Company Details");
		debtn = (Button) view.findViewById(R.id.detbtn);
		companyname = (EditText) view.findViewById(R.id.editcompanynamee);
		username = (EditText) view.findViewById(R.id.edityourname);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
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
					//System.out.println("+++++++username+++++"+session.getUsername());
					//InsertUser(user);
					//getLocation();
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
		Timber.i("Insert: ", "Inserting ..");
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
				Timber.i("AddUserDetail: "+"Entering ..");
				List<NameValuePair> updateuserlist = new ArrayList<NameValuePair>();
				List<NameValuePair> createuserlist = new ArrayList<NameValuePair>();
				if(fullAddress!=null || locationVal!=null ) {
					//System.out.println("+++++++++++++++if+++++++++++");
					//createuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),"Y","Y", user.getUserName()));
					createuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), locationVal, fullAddress, "Y", "Y", user.getUserName()));
					updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), locationVal, fullAddress, "Y", "Y", user.getUserName()));
				}else {
					//System.out.println("+++++++++++++++else+++++++++++");
					updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), "null", "null", "Y", "Y", user.getUserName()));
					createuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), "null","null", "Y", "Y", user.getUserName()));
				}
					String Getuser_url = ServiceConstants.GET_USER + session.getPhoneno();
					response = request.requestGetType(Getuser_url, ServiceConstants.BASE_URL);
					if (response.getStatusLine().getStatusCode() == 200) {
						JSONObject responsejson = request.responseParsing(response);
						String Updateuser_url = ServiceConstants.UPDATE_USER + session.getPhoneno();
						/*List<NameValuePair> updateuserlist = new ArrayList<NameValuePair>();
						if(fullAddress!=null || locationVal!=null) {
							//updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),"Y","Y",user.getUserName()));
							updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), locationVal, fullAddress.toString(), "Y", "Y", user.getUserName()));
						}else {
							updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), latitude.toString(), longitude.toString(), "null", "null", "Y", "Y", user.getUserName()));
						}*/
							if (responsejson != null) {
							response = request.requestPutType(ServiceConstants.UPDATE_USER, updateuserlist, ServiceConstants.BASE_URL);
							responseStrng = "" + response.getStatusLine().getStatusCode();
							if (response.getStatusLine().getStatusCode() == 200) {
							/*Intent intent = new Intent(getActivity(), HomeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);*/
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
							/*Intent intent = new Intent(getActivity(), HomeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);*/
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
						/*Intent intent = new Intent(getActivity(), HomeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);*/
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
				Timber.i("Inside Company Details : API Exception"+e);
				e.printStackTrace();

			}

			return responseStrng;

		}

	}

	public void getLocation() {
		//Toast.makeText(getActivity().getApplicationContext(), "Enter get location method..", Toast.LENGTH_SHORT).show();
		locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {
		//	Toast.makeText(getActivity().getApplicationContext(), "location manager checking..." + locationManager.toString(), Toast.LENGTH_SHORT).show();
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
				//System.out.println("++++++++++++++++++++++++++++++++++enable location++++++++++++++++++++++++");
				//locationVal = "null";
				//fullAddress = "null";
				location = null;
				UpdateLocation(location);

			} else {
				if (isNetworkEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							//Toast.makeText(getActivity().getApplicationContext(), "Location value....." + "latitude" + String.valueOf(location.getLatitude()) + "longitude" + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
							if(location!=null) {
								UpdateLocation(location);
							}
						} else {
							Timber.i("Inside Company Details : no location found");
							Toast.makeText(getActivity().getApplicationContext(), "no location found", Toast.LENGTH_SHORT).show();
						}
					}
				}
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if(location!=null) {
								UpdateLocation(location);
							}

						} else {
							Toast.makeText(getActivity().getApplicationContext(), "no location found", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}


}

	public void UpdateLocation(Location updateLocation) {
		Timber.i("Company Detail: ", "UpdateLocation Entering ..");
		if (updateLocation != null) {
			latitude = updateLocation.getLatitude();
			longitude = updateLocation.getLongitude();
			Timber.i("Company Detail: ", "UpdateLocation latitude and longitude .." + latitude + "&" + longitude);
			new GetAddressFromJson().execute("", "", "");
			/*Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(),Locale.getDefault());
			//System.out.println("++++++++++++++++++++++++++++++++++address+address++List++++++++++++++++++++++++");
			if(geocoder!=null) {
				try {
					List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);

					if (addressList != null && addressList.size() > 0) {
						//System.out.println("++++++++++++++++++++++++++++++++++address+address++List++++++++++++++++++++++++"+addressList);
						Address address = addressList.get(0);
						//fullAddress = new StringBuilder();
						//System.out.println("++++++++++++++++++++++++++++++++++address+address++++++++++++++++++++++++++"+address);
						//Toast.makeText(getActivity().getApplicationContext(), "address......" +addressList, Toast.LENGTH_LONG).show();
						*//*if (address.getMaxAddressLineIndex() > 0) {
							//Toast.makeText(getActivity().getApplicationContext(), "address..if loop...." + address.getMaxAddressLineIndex(), Toast.LENGTH_SHORT).show();
							for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
								//Toast.makeText(getActivity().getApplicationContext(), "address..for loop...." + address.getMaxAddressLineIndex(), Toast.LENGTH_SHORT).show();
								if (address.getAddressLine(i) != null) {
									//Toast.makeText(getActivity().getApplicationContext(), "address.for ...if  loop...." + address.getAddressLine(i), Toast.LENGTH_SHORT).show();
									//fullAddress.append(address.getAddressLine(i)).append(",");
								}
							}
						}*//*
                  *//*  sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());*//*
						if (address.getSubLocality() == null) {
							if (address.getLocality() == null) {
								locationVal = "null";
								fullAddress = "null";
								Toast.makeText(getActivity().getApplicationContext(), "no address value", Toast.LENGTH_SHORT).show();
							} else {
								fullAddress = address.getLocality().toString();
								locationVal = address.getLocality().toString();
							}
						} else {
							 if (address.getLocality() == null) {
								 fullAddress = address.getSubLocality().toString();
								locationVal = address.getSubLocality().toString();
							} else {
								 fullAddress = address.getSubLocality().toString() + "," + address.getLocality().toString();
								 locationVal = address.getLocality().toString();
						}

						}
						Timber.i("Company Detail: Current Location", fullAddress);
						//System.out.println("++++++++++++++++++++++++++++++++++fulladdress+++++++++++++++++++++++++++"+fullAddress);
						//Toast.makeText(getActivity().getApplicationContext(), "fulladdress.."+fullAddress+".....location"+locationVal, Toast.LENGTH_SHORT).show();
					}

				} catch (IOException e) {
					Timber.i("Company Detail: ", "UpdateLocation IOException ..",e);
					e.printStackTrace();
}
				}*/

		}else{
			Timber.i("Company Detail: ", "Update Location Null");
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

		}

		protected String doInBackground(String... params) {
			InputStream is = null;
			String result = "";
			JSONObject jsonObj=null;
			try {
				Timber.i("Company Detail: ", "GetAddressFromJsonAPI");
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude + ","+longitude);
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
				jsonObj = new JSONObject(result);
				String Status = jsonObj.getString("status");
				if (Status.equalsIgnoreCase("OK")) {
					JSONArray Results = jsonObj.getJSONArray("results");
					JSONObject zero = Results.getJSONObject(0);
					JSONArray address_components = zero.getJSONArray("address_components");
					for (int i = 0; i < address_components.length(); i++) {
						JSONObject zero2 = address_components.getJSONObject(i);
						String long_name = zero2.getString("long_name");
						//System.out.println("++++++++++++++++++++++++++++++++++long_name+++++++++++++++++++++++++"+long_name);
						JSONArray mtypes = zero2.getJSONArray("types");
						// System.out.println("++++++++++++++++++++++++++++++++++mtypes+++++++++++++++++++++++++"+mtypes+TextUtils.isEmpty(long_name));
						String Type = mtypes.getString(0);
						if (TextUtils.isEmpty(long_name) == false || long_name!=null || long_name.length() > 0 || long_name != "") {
							if (Type.equalsIgnoreCase("sublocality_level_1")) {
								fullAddress=long_name+",";
							} else if (Type.equalsIgnoreCase("sublocality")) {
								fullAddress=long_name+",";
							}else if (Type.equalsIgnoreCase("locality")) {
								// Address2 = Address2 + long_name + ", ";
								locationVal = long_name;
								if(fullAddress!=null) {
									fullAddress = fullAddress + long_name;
								}else{
									fullAddress = long_name;
								}
							} else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
								if(fullAddress == null ||locationVal==null ) {
									locationVal = long_name;
									fullAddress = long_name;

								}
							} else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
								if(fullAddress == null ||locationVal==null ){
									locationVal = long_name;
									fullAddress=long_name;
								}
							}
						}

						// JSONArray mtypes = zero2.getJSONArray("types");
						// String Type = mtypes.getString(0);
						// Log.e(Type,long_name);
						Timber.i("Company Detail: CurrentLocation ", fullAddress);
						System.out.println("+++++++++++++++++++++++++++full+ddresss++" +
								"+++++++++++++++++++++++"+fullAddress+"+local++"+locationVal);
					}
				}
				//new UpdateLocationApi().execute("", "", "");
			} catch (Exception e) {

				e.printStackTrace();

			}

			return responsevalue;

		}
	}
	public void locationEnable_popup() {
		LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
		View promptsView = inflater.inflate(R.layout.location_enable_popup, null);

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext()).create();

		alertDialog.setView(promptsView);

		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();

		Button textbutton = (Button) promptsView.findViewById(R.id.btnYes);

		textbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().getApplicationContext().startActivity(intent);
				alertDialog.dismiss();
				progressBar.setVisibility(View.INVISIBLE);
			}

		});
		Button textbutton1=(Button)promptsView.findViewById(R.id.btnNo);
		textbutton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				progressBar.setVisibility(View.INVISIBLE);
			}
		});


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
