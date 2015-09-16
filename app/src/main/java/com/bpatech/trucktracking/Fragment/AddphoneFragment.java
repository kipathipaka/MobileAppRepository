package com.bpatech.trucktracking.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bpatech.trucktracking.R;


public class AddphoneFragment extends Fragment {
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
	        View view = inflater.inflate(R.layout.addphone_layout, container, false);
	        
	        return view;
	    }
}
