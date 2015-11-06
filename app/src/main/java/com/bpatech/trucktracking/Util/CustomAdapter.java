package com.bpatech.trucktracking.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CustomAdapter extends ArrayAdapter {
	MapView mapView;
	//GoogleMap map;
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
	        int listsize= mList.size();
		//	System.out.println("size" + listsize);
	mapView = (MapView) view.findViewById(R.id.maplist_view);
	mapView.onCreate(savedInstanceState);
	mapView.onResume();

	if (isGoogleMapsInstalled()==true){
		progressBar.setVisibility(View.VISIBLE);
		//checkGooglePlayServicesAvailability();
		mapView.getMapAsync(
				new OnMapReadyCallback() {
					@Override
					public void onMapReady(GoogleMap googlemap) {
						final GoogleMap map = googlemap;

						if (map != null) {
							map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
							map.getUiSettings().setMyLocationButtonEnabled(false);
							map.getUiSettings().setMapToolbarEnabled(false);
							map.setMyLocationEnabled(true);
							MapsInitializer.initialize(context);
							String addressname = mList.get(position).getLocation().toString();
							Geocoder geoCoder = new Geocoder(getContext());
							try{
							List<Address> listAddress;

								listAddress = geoCoder.getFromLocationName(addressname, 1);
								if (listAddress == null || listAddress.size() == 0) {
									Toast.makeText(getContext(), "No Location found", Toast.LENGTH_SHORT).show();
									//return null;
								}else{
									Address location = listAddress.get(0);
									LatLng locationlatlng = new LatLng(location.getLatitude(), location.getLongitude());
									Marker marker = map.addMarker(new MarkerOptions().position(
											locationlatlng).title(""));
									map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 10));
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					/*map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					map.getUiSettings().setMyLocationButtonEnabled(false);
					map.getUiSettings().setMapToolbarEnabled(false);
					map.setMyLocationEnabled(true);
					MapsInitializer.initialize(context);

					//map.setMyLocationEnabled(true);
					double latitude=13.0827;
					double longitude= 80.2707;
					LatLng LOCATION = new LatLng(latitude,longitude);

					Marker marker = map.addMarker(new MarkerOptions().position(
							LOCATION).title(""));
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION, 10));*/
							// Zoom in, animating the camera.
							//map.animateCamera(CameraUpdateFactory.zoomTo(15),2000,null);


					}
				}
		);

		progressBar.setVisibility(View.INVISIBLE);
	}else {
		//System.out.println("+++++++++++map++++++++");
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Install Google Maps");
		builder.setCancelable(false);
		builder.setPositiveButton("Install", getGoogleMapsListener());
		AlertDialog dialog = builder.create();
		dialog.show();
		/*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage("Install Google Maps");
		builder.setCancelable(false);
		builder.setPositiveButton("Install", getGoogleMapsListener());
		AlertDialog dialog = builder.create();
		dialog.show()*/;
	}

			TextView DestinationText=(TextView) view.findViewById(R.id.tovalue);
			//TextView rideText=(TextView) view.findViewById(R.id.ride);
			TextView NowText=(TextView) view.findViewById(R.id.nowmsg);
			TextView Nowval=(TextView) view.findViewById(R.id.nowvalue);
			TextView UpdateText=(TextView) view.findViewById(R.id.updatedmsg);
			TextView UpdateVal=(TextView) view.findViewById(R.id.updatevalue);
			TextView Rideno = (TextView) view.findViewById(R.id.rideno);
			TextView Destination = (TextView) view.findViewById(R.id.place);
			TextView phoneno = (TextView) view.findViewById(R.id.phoneno);
			TextView customer = (TextView) view.findViewById(R.id.customer);
	        TextView vechile_trip_id = (TextView) view.findViewById(R.id.vechiletrip_no);
			TextView customer_name = (TextView) view.findViewById(R.id.customername);
			TextView customer_no = (TextView) view.findViewById(R.id.customerno);
			DestinationText.setText("To :");
			//rideText.setText("#");
			NowText.setText("Now :");
if(mList.get(position).getLocation().toString().length() >20) {
	String locationval=mList.get(position).getLocation().toString().substring(0,19);
	Nowval.setText(locationval);
}else{
	Nowval.setText(mList.get(position).getLocation().toString());
}

			UpdateText.setText("Update :");

	/*	DateFormat dateFormat = new SimpleDateFormat("h:mm a");
	dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		Date date = new Date(Long.parseLong("1445817600000"));*/
		UpdateVal.setText(mList.get(position).getLast_sync_time().toString());
	/*if(mList.get(position).getLast_sync_time().toString().equalsIgnoreCase("null")) {
		DateFormat dateFormat = new SimpleDateFormat("h:mm a");
		Date date = new Date();
		UpdateVal.setText(dateFormat.format(date).toString());
	}else {
		DateFormat dateFormat1 = new SimpleDateFormat("h:mm a");
		dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+17:30"));
		Date date = new Date(Long.parseLong(mList.get(position).getLast_sync_time().toString()));
		UpdateVal.setText(dateFormat1.format(date).toString());
	}*/
			Rideno.setText("#"+mList.get(position).getTruckno() );
			Destination.setText( mList.get(position).getDestination() );
			phoneno.setText( mList.get(position).getDriver_phone_no());
			customer.setText( mList.get(position).getCustomer_company());
			customer_name.setText( mList.get(position).getCustomer_name());
			customer_no.setText(mList.get(position).getCustomer_phoneno());
	vechile_trip_id.setText(String.valueOf(mList.get(position).getVehicle_trip_id()));
			view.setBackgroundColor(getContext().getResources().getColor(R.color.darkskyblue));

	        return view;
	    }
	public boolean isGoogleMapsInstalled()
	{

			int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
			if(result != ConnectionResult.SUCCESS) {
				return false;
			}else{
				return true;
			}

			//ApplicationInfo info = getContext().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);



	}
	public DialogInterface.OnClickListener getGoogleMapsListener()
	{
		return new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
				getContext().startActivity(intent);

				//Finish the activity so they can't circumvent the check
		//finish();
			}
		};
	}
}
