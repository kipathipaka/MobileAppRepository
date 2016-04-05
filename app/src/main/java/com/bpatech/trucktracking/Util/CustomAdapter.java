package com.bpatech.trucktracking.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {

	ProgressBar progressBar;
	private Context mContext;
	private ArrayList<AddTrip> mList;
	Bundle savedInstanceState;
	private int[] colors = new int[] { 0x300000FF,0x30FF0000  };

	@SuppressWarnings("unchecked")
	public CustomAdapter(Context context, ArrayList<AddTrip> list, final Bundle b) {

		super(context, R.layout.currenttrip_layout, list);
		mContext = context;
		mList = list;
		savedInstanceState=b;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view;
		final Context context = parent.getContext();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.currenttrip_layout, null);
			progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar1);
			progressBar.setProgress(20);
			progressBar.setMax(100);
			progressBar.setVisibility(View.VISIBLE);
		}
		else {
			view = convertView;
			progressBar=(ProgressBar)view.findViewById(R.id.listprogresbar1);
			progressBar.setProgress(20);
			progressBar.setMax(100);
			progressBar.setVisibility(View.VISIBLE);
		}

		TextView DestinationText=(TextView) view.findViewById(R.id.tovalue);

		TextView NowText=(TextView) view.findViewById(R.id.nowmsg);
		TextView Nowval=(TextView) view.findViewById(R.id.nowvalue);
		TextView UpdateText=(TextView) view.findViewById(R.id.updatedmsg);
		TextView UpdateVal=(TextView) view.findViewById(R.id.updatevalue);
		TextView Rideno = (TextView) view.findViewById(R.id.rideno);
		TextView Destination = (TextView) view.findViewById(R.id.place);
		TextView phoneno = (TextView) view.findViewById(R.id.phoneno);
		TextView vechile_trip_id = (TextView) view.findViewById(R.id.vechiletrip_no);

		DestinationText.setText("To :");

		NowText.setText("Now :");
		if(mList.get(position).getLocation().toString().equalsIgnoreCase("null") ) {
			Nowval.setText("Not Available");
		}else
		{
			if (mList.get(position).getLocation().toString().length() > 20) {
				String locationval = mList.get(position).getLocation().toString().substring(0, 19);
				Nowval.setText(locationval);
			} else {
				Nowval.setText(mList.get(position).getLocation().toString());
			}
		}

		UpdateText.setText("Time :");

		if(mList.get(position).getLast_sync_time().toString().equalsIgnoreCase("null")) {
			UpdateVal.setText("Not available");
		}else{
			UpdateVal.setText(mList.get(position).getLast_sync_time().toString());
		}


		Rideno.setText("#"+mList.get(position).getTruckno() );
		if( mList.get(position).getDestination().toString().equalsIgnoreCase("null")) {
			Destination.setText("Not available");
		}else{
			Destination.setText( mList.get(position).getDestination());
		}

		phoneno.setText( mList.get(position).getDriver_phone_no());

		vechile_trip_id.setText(String.valueOf(mList.get(position).getVehicle_trip_id()));
		view.setBackgroundColor(getContext().getResources().getColor(R.color.darkskyblue));
		progressBar.setVisibility(View.INVISIBLE);
		return view;
	}


}
