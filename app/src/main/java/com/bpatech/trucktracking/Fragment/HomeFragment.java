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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.ArrayList;

import timber.log.Timber;


public class HomeFragment extends Fragment {
	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneNo;
	Request request;
	HttpResponse response;
	String responseStrng = null;
	public ArrayList<AddTrip> currenttriplist;
	public ArrayList<AddTrip> currenttripdetails;
	ImageButton imageButtonopen;
	ImageView carlogo;
	User user;
	ProgressBar progressBar,progressBar1;
	TextView destination, truck, phoneno, txt_contTitle, triplistsize_view;
	LinearLayout listlayout_ll, triplist_ll,footer_addtrip_ll;
	ListView listView;
	SessionManager session;
	AddUserObjectParsing obj;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		///Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()))

		View view = inflater.inflate(R.layout.activity_home, container, false);
			Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
			progressBar=(ProgressBar)view.findViewById(R.id.loginprogressbar);
			progressBar.setProgress(10);
			progressBar.setMax(100);
			progressBar.setVisibility(View.INVISIBLE);
		session = new SessionManager(getActivity().getApplicationContext());
		request = new Request(getActivity());
			txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
			txt_contTitle.setText("Welcome");
			carlogo=(ImageView)view.findViewById(R.id.car_logo);
			carlogo.setVisibility(view.GONE);
			nbtn = (Button) view.findViewById(R.id.nextbtn);
			phoneNo = (EditText) view.findViewById(R.id.phoneno);
			nbtn.setOnClickListener(new MyButtonListener());


		return view;
	}

	private class MyButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			System.out.println("enter if main");
			progressBar.setVisibility(View.VISIBLE);
try {
	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
	inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	if (phoneNo.getText().toString().equals("")) {
		Toast.makeText(getActivity().getApplicationContext(), " Value is  empty!",
				Toast.LENGTH_LONG).show();
		progressBar.setVisibility(View.INVISIBLE);

	}  else if(phoneNo.getText().toString().length()==10){
		progressBar.setVisibility(View.VISIBLE);
		String savephoneno = phoneNo.getText().toString();
		System.out.println("phoneNo " + phoneNo);
		Timber.tag("USER : "+String.valueOf(phoneNo));
		session.setPhoneno(savephoneno);
		/*DetailFragment detailfrag = new DetailFragment();

		FragmentManager fragmentmanager = getFragmentManager();
		FragmentTransaction fragmenttransaction = fragmentmanager
				.beginTransaction();
		fragmenttransaction.replace(R.id.viewers, detailfrag, "BackCurrentTrip");
		fragmenttransaction.addToBackStack(null);
		fragmenttransaction.commit();*/
		new CheckUserDetail().execute("", "", "");

	}
	else
	{
		Toast.makeText(getActivity().getApplicationContext(), "enter the valid phone number!",
				Toast.LENGTH_LONG).show();
		progressBar.setVisibility(View.INVISIBLE);
	}
} catch(Exception e)
{
	Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
			Toast.LENGTH_LONG).show();
	e.printStackTrace();
	progressBar.setVisibility(View.INVISIBLE);
}
		}

	}
	private class CheckUserDetail extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {

			progressBar.setVisibility(View.INVISIBLE);
		}

		protected String doInBackground(String... params) {

			try {

				//List<NameValuePair> createuserlist = new ArrayList<NameValuePair>();
				//createuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),latitude.toString(),longitude.toString(),locationVal.toString(),fullAddress.toString(), "Y","Y", user.getUserName()));
				String Getuser_url= ServiceConstants.GET_USER+session.getPhoneno();
				response = request.requestGetType(Getuser_url,ServiceConstants.BASE_URL);
				responseStrng = "" + response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject responsejson = request.responseParsing(response);

				//	updateuserlist.addAll(obj.userCreationObject(session.getPhoneno(),user.getCompanyName(),latitude.toString(),longitude.toString(),locationVal.toString(),fullAddress.toString(),"Y","Y",user.getUserName()));
					if(responsejson!=null) {
						if(responsejson.getString("is_active").equalsIgnoreCase("Y") && responsejson.getString("app_download_status").equalsIgnoreCase("Y")) {
							user=new User();
							user.setPhone_no(session.getPhoneno());
							user.setCompanyName(responsejson.getString("company_name"));
							user.setUserName(responsejson.getString("name"));
							session.setUsername(responsejson.getString("name"));
							InsertUser(user);
							CurrentTripFragment currenttripfrag = new CurrentTripFragment();
							FragmentManager fragmentmanager = getFragmentManager();
							FragmentTransaction fragmenttransaction = fragmentmanager
									.beginTransaction();
							fragmenttransaction.replace(R.id.viewers, currenttripfrag);
							fragmenttransaction.addToBackStack(null);
							fragmenttransaction.commit();
						}else{
							DetailFragment detailfrag = new DetailFragment();
							FragmentManager fragmentmanager = getFragmentManager();
							FragmentTransaction fragmenttransaction = fragmentmanager
									.beginTransaction();
							fragmenttransaction.replace(R.id.viewers, detailfrag, "BackCurrentTrip");
							fragmenttransaction.addToBackStack(null);
							fragmenttransaction.commit();

						}

					}else{

						DetailFragment detailfrag = new DetailFragment();

						FragmentManager fragmentmanager = getFragmentManager();
						FragmentTransaction fragmenttransaction = fragmentmanager
								.beginTransaction();
						fragmenttransaction.replace(R.id.viewers, detailfrag, "BackCurrentTrip");
						fragmenttransaction.addToBackStack(null);
						fragmenttransaction.commit();



					}

				}else{

					DetailFragment detailfrag = new DetailFragment();

					FragmentManager fragmentmanager = getFragmentManager();
					FragmentTransaction fragmenttransaction = fragmentmanager
							.beginTransaction();
					fragmenttransaction.replace(R.id.viewers, detailfrag, "BackCurrentTrip");
					fragmenttransaction.addToBackStack(null);
					fragmenttransaction.commit();


				}


			} catch (Exception e) {

				e.printStackTrace();

			}

			return responseStrng;

		}

	}
	void InsertUser(User user) {

		db=new MySQLiteHelper(getActivity().getApplicationContext());
		db.addUser(user);
		Timber.i("HomeFragment : Create Table: "+"Inserting ..");
		Log.d("Insert: ", "Inserting ..");
	}
}




