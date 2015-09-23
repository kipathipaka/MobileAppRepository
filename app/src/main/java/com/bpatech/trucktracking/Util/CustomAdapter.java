package com.bpatech.trucktracking.Util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter {
	 private Context mContext;
	    private ArrayList<AddTrip> mList;
	private int[] colors = new int[] { 0x300000FF,0x30FF0000  };
	    
@SuppressWarnings("unchecked")
public CustomAdapter(Context context, ArrayList<AddTrip> list) {
	
	        super(context, R.layout.currenttrip_layout, list);
	        mContext = context;
	        mList = list;
	    }
	 
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	 
	        View view;

	        if (convertView == null) {
	        	   System.out.println("convertView IFFFF");
	            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	           view = inflater.inflate(R.layout.currenttrip_layout, null);
	           
	        }
	        else {
	            view = convertView;
	            System.out.println("convertView else");
	        }
	        int listsize= mList.size();
			System.out.println("size" + listsize);
			//LinearLayout listlayout_ll=(LinearLayout)view.findViewById(R.id.currenttriplist_ll);

			TextView DestinationText=(TextView) view.findViewById(R.id.tovalue);
			TextView rideText=(TextView) view.findViewById(R.id.ride);
			TextView NowText=(TextView) view.findViewById(R.id.nowmsg);
			TextView Nowval=(TextView) view.findViewById(R.id.nowvalue);
			TextView UpdateText=(TextView) view.findViewById(R.id.updatedmsg);
			TextView UpdateVal=(TextView) view.findViewById(R.id.updatevalue);
			TextView Rideno = (TextView) view.findViewById(R.id.rideno);
			TextView Destination = (TextView) view.findViewById(R.id.place);
			TextView phoneno = (TextView) view.findViewById(R.id.phoneno);
			TextView customer = (TextView) view.findViewById(R.id.customer);

			TextView customer_name = (TextView) view.findViewById(R.id.customername);
			TextView customer_no = (TextView) view.findViewById(R.id.customerno);
			DestinationText.setText("To :");
			rideText.setText("Ride :");
			NowText.setText("Now :");
			Nowval.setText("Asanol");
			UpdateText.setText("Update :");
			UpdateVal.setText("10.10 Am");
			Rideno.setText( mList.get(position).getTruckno() );
			Destination.setText( mList.get(position).getDestination() );
			phoneno.setText( mList.get(position).getPhone_no());
			customer.setText( mList.get(position).getCustomer());
			customer_name.setText( mList.get(position).getCustomer_name());
			customer_no.setText(mList.get(position).getCustomer_no());
			//listlayout_ll.setBackgroundColor(Color.BLUE);
			if(mList.get(position).getCustomer().toString().equalsIgnoreCase("driver")){
				view.setBackgroundColor(getContext().getResources().getColor(R.color.darkred));
				ImageView profileimage=(ImageView)view.findViewById(R.id.profile_image);
				profileimage.setImageResource(R.drawable.driverimg);
			}else if(mList.get(position).getCustomer().toString().equalsIgnoreCase("user")){
				view.setBackgroundColor(getContext().getResources().getColor(R.color.darkgreen));
				ImageView profileimage1=(ImageView)view.findViewById(R.id.profile_image);
				profileimage1.setImageResource(R.drawable.user);
			}else{
				view.setBackgroundColor(Color.BLUE);
				ImageView profileimage2=(ImageView)view.findViewById(R.id.profile_image);

				profileimage2.setImageResource(R.drawable.customer);
			}


	        return view;
	    }

}
