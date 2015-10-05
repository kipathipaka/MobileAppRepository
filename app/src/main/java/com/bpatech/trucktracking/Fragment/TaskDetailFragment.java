package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
    * Created by Anita on 9/14/2015.
            */
    public class TaskDetailFragment extends Fragment   {
        public GoogleMap googleMap;
        protected Context context;
        TextView truck, place, phone, txt_contTitle,customer,customer_name,customer_no;
        Button Startbtn;
    boolean isstarttrip=true,isendtrip=false;
    ImageButton whatsup;
    boolean startclick;

    LatLng LOCATION;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));

        View view = inflater.inflate(R.layout.taskdetail_layout, container, false);
        googleMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map_view)).getMap();
       // googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view)).getMap();
        //(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mmap);
        //supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        //googleMap= supportMapFragment.getMap();
        if(googleMap!=null)
        {
            double latitude=13.0827;
            double longitude= 80.2707;
            LatLng LOCATION = new LatLng(latitude,longitude);

            Marker marker = googleMap.addMarker(new MarkerOptions().position(
                    LOCATION).title(""));
           googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION,17));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14),2000,null);

        }
googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
{
    @Override
    public void onMapClick(LatLng latLng) {
        DisplayMapFragment displayMapFragment= new DisplayMapFragment();
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




       Bundle taskdetail = this.getArguments();

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
    }

}
