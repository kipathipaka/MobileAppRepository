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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.AddUserObjectParsing;
import com.bpatech.trucktracking.Service.MySQLiteHelper;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.SessionManager;

import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {
	MySQLiteHelper db;
	private Button nbtn;
	private EditText phoneNo;
	Request request;
	String responseStrng;
	public ArrayList<AddTrip> currenttriplist;
	public ArrayList<AddTrip> currenttripdetails;
	ImageButton imageButtonopen;
	ImageView carlogo;
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

		session = new SessionManager(getActivity().getApplicationContext());
		String savephoneno = phoneNo.getText().toString();
		System.out.println("phoneNo " + phoneNo);
		session.setPhoneno(savephoneno);
		DetailFragment detailfrag = new DetailFragment();

		FragmentManager fragmentmanager = getFragmentManager();
		FragmentTransaction fragmenttransaction = fragmentmanager
				.beginTransaction();
		fragmenttransaction.replace(R.id.viewers, detailfrag, "BackCurrentTrip");
		fragmenttransaction.addToBackStack(null);
		fragmenttransaction.commit();


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

		int generateValue() {
			Random random = new Random();
			int otpno = random.nextInt(900000) + 100000;
			return otpno;
		}
	}


}




