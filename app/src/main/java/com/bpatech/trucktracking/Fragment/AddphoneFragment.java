package com.bpatech.trucktracking.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bpatech.trucktracking.R;


public class AddphoneFragment extends Fragment {
	TextView txt_contTitle;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
	        View view = inflater.inflate(R.layout.addphone_layout, container, false);
		 txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
		 txt_contTitle.setText("Add Phone");
	        return view;
	    }
}
