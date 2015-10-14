package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
    * Created by Anita on 9/14/2015.
            */
    public class TaskDetailFragment extends Fragment   {
       // public GoogleMap googleMap;
       protected Context context;
    TextView truck, place, phone, txt_contTitle,customer,customer_name,customer_no,lastlocation,updatetime;
    Button Startbtn;
    boolean isstarttrip=true,isendtrip=false;
    TableRow locationrow,lasttimerow;
    ImageButton whatsup;
    boolean startclick;
    MapView mapView;
    public GoogleMap googleMap;
    LatLng LOCATION;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));

        View view = inflater.inflate(R.layout.taskdetail_layout, container, false);
        Bundle taskdetail = this.getArguments();
        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        googleMap=mapView.getMap();
        if(googleMap!=null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            //googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setMyLocationEnabled(true);
            MapsInitializer.initialize(this.getActivity());
            String addressname = taskdetail.getString(ServiceConstants.ADD_TRIP_SOURCE).toString();
            Geocoder geoCoder = new Geocoder(this.getActivity());
            List<Address> listAddress;
            //GeoPoint geoPoint;
            try {
                listAddress = geoCoder.getFromLocationName(addressname, 1);
                if (listAddress == null || listAddress.size() == 0) {
                    Toast.makeText(this.getActivity(), "No Location found", Toast.LENGTH_SHORT).show();
                    //return null;
                }
                Address location = listAddress.get(0);
                LatLng locationlatlng = new LatLng(location.getLatitude(), location.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(
                        locationlatlng).title(""));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 10));
                // googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


       /* if(googleMap!=null)
        {
            String addressname=taskdetail.getString(ServiceConstants.CUURENT_TRIP_PLACE).toString();
            Geocoder geoCoder = new Geocoder(this.getActivity());
            List<Address> listAddress;
            //GeoPoint geoPoint;
            try {
                listAddress = geoCoder.getFromLocationName(addressname, 1);
                if (listAddress == null || listAddress.size()==0) {
                    Toast.makeText(this.getActivity(), "No Location found", Toast.LENGTH_SHORT).show();
                    //return null;
                }
                Address location = listAddress.get(0);
                LatLng locationlatlng = new LatLng(location.getLatitude(), location.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(
                        locationlatlng).title(""));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 10));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            }catch (IOException e) {
                e.printStackTrace();
            }

        }*/
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
{
    @Override
    public void onMapClick(LatLng latLng) {
        DisplayMapFragment displayMapFragment= new DisplayMapFragment();
        Double latitude=latLng.latitude;
        Double longitude=latLng.longitude;
        Bundle bundle=new Bundle();
        bundle.putDouble("latitude",latitude);
        bundle.putDouble("longitude",longitude);
        displayMapFragment.setArguments(bundle);
        FragmentManager fragmentmanager = getFragmentManager();
        FragmentTransaction fragmenttransaction = fragmentmanager
                .beginTransaction();
        fragmenttransaction.replace(R.id.viewers, displayMapFragment,"BackCurrentTrip");

        fragmenttransaction.addToBackStack(null);
        fragmenttransaction.commit();

    }
});


        txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
        txt_contTitle.setText(ServiceConstants.TASK_DETAIL_TITLE);
        Startbtn = (Button)view.findViewById(R.id.startbtn);
        whatsup=(ImageButton)view.findViewById(R.id.whatsup);
       whatsup.setOnClickListener(new WhatsupButtonListener());
        truck = (TextView) view.findViewById(R.id.truckvalu);
        place = (TextView) view.findViewById(R.id.tovalue);
        phone = (TextView) view.findViewById(R.id.phoneno);
        customer = (TextView) view.findViewById(R.id.customerval);
        customer_name = (TextView) view.findViewById(R.id.customenameval);
        customer_no = (TextView) view.findViewById(R.id.customenoval);
        lastlocation=(TextView) view.findViewById(R.id.lastlocationvalue);
        updatetime=(TextView) view.findViewById(R.id.updateval);
        lasttimerow=(TableRow)view.findViewById(R.id.updatetextRow);
        locationrow=(TableRow)view.findViewById(R.id.last_locationrow);
        locationrow.setVisibility(view.GONE);
        lasttimerow.setVisibility(view.GONE);
        // String place=taskdetail.getString(ServiceConstants.CUURENT_TRIP_Place);
        place.setText(taskdetail.getString(ServiceConstants.CUURENT_TRIP_PLACE));
        truck.setText(taskdetail.getString(ServiceConstants.CUURENT_TRIP_TRUCK));
        phone.setText(taskdetail.getString(ServiceConstants.CUURENT_TRIP_PHONE));
        customer.setText(taskdetail.getString(ServiceConstants.ADD_TRIP_CUSTOMER));
        customer_name.setText(taskdetail.getString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME));
        customer_no.setText(taskdetail.getString(ServiceConstants.ADD_TRIP_CUSTOMER_NO));

        //startclick=taskdetail.getBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE);
        /*if(startclick==true){
            //mapDestroyOnDemand();
            System.out.println("click on it");
            Startbtn.setText("End Tracking");
            Startbtn.setBackgroundColor(Color.RED);
        }else{

            System.out.println("disbale the click button");
            Startbtn.setText("Start Tracking");
            Startbtn.setBackgroundColor(Color.parseColor("#3090C7"));
        }*/

         Startbtn.setOnClickListener(new StartTrackButtonListener());
        // Toast.makeText(getActivity().getApplicationContext(), place, Toast.LENGTH_LONG).show();
        return view;
    }

    private class StartTrackButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(startclick==true){
               // mapDestroyOnDemand();
                CurrentTripFragment currenttripfrag=new CurrentTripFragment();
                FragmentManager fragmentmanager = getFragmentManager();
                FragmentTransaction fragmenttransaction = fragmentmanager
                        .beginTransaction();
                fragmenttransaction.replace(R.id.viewers,currenttripfrag);

                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }else {
                System.out.println("click on it");
                Startbtn.setText("End Tracking");
                Startbtn.setBackgroundColor(Color.RED);
                lastlocation.setText("Chennai");
                DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                Date date = new Date();

                updatetime.setText(dateFormat.format(date).toString());
                locationrow.setVisibility(View.VISIBLE);
                lasttimerow.setVisibility(View.VISIBLE);
                startclick=true;
              /*  mapDestroyOnDemand();
                //onDestroyView();
                TaskDetailFragment taskdetailfrag = new TaskDetailFragment();
                Bundle taskdetails=new Bundle();

                taskdetails.putString(ServiceConstants.CUURENT_TRIP_PLACE, place.getText().toString());
                taskdetails.putString(ServiceConstants.CUURENT_TRIP_TRUCK, truck.getText().toString());
                taskdetails.putString(ServiceConstants.CUURENT_TRIP_PHONE, phone.getText().toString());
                taskdetails.putString(ServiceConstants.ADD_TRIP_CUSTOMER, customer.getText().toString());
                taskdetails.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME, customer_name.getText().toString());
                taskdetails.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NO, customer_no.getText().toString());
                taskdetails.putBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE, true);
                taskdetailfrag.setArguments(taskdetails);
                FragmentManager fragmentmanager = getFragmentManager();
                FragmentTransaction fragmenttransaction = fragmentmanager
                        .beginTransaction();
                fragmenttransaction.replace(R.id.viewers, taskdetailfrag,"BackCurrentTrip");

                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();*/
            }

        }
    }

   /* public void GetLatLangFromAddress() {
        double latitude;
        double longitude;
        if (googleMap != null) {


        }

    }*/


    private class WhatsupButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            System.out.println("whatsup");

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            if (sendIntent!= null) {
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Whatsup text msg");
                startActivity(Intent.createChooser(sendIntent, "Share with"));
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }


        }
        }


/*
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub

        super.onDestroyView();

        Fragment fragment = (getFragmentManager()
                .findFragmentById(R.id.map_view));
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
    }

    public void mapDestroyOnDemand() {
        Fragment fragment = (getFragmentManager()
                .findFragmentById(R.id.map_view));
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
    }*/
@Override
public void onResume() {
    mapView.onResume();
    super.onResume();
}

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
