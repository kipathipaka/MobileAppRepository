package com.bpatech.trucktracking.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.AddTrip;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Service.GetMytripListParsing;
import com.bpatech.trucktracking.Service.Request;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
    * Created by Anita on 9/14/2015.
            */
    public class TaskDetailFragment extends Fragment   {

    protected Context context;
    TextView truck, place, phone, txt_contTitle,customer_company,
            customer_name,customer_phone_no,lastlocation,updatetime;
    Button Startbtn;
    TableRow locationrow,lasttimerow;
    ImageButton whatsup,inbox;
    boolean startclick;
    String vechile_trip_no;
    private static MapView mapView;
    Request request;
    String responseStrng;
    ProgressBar progressBar;
    SessionManager session;
    EditText whatsuptext,message;
    String lastlocationtxt,lastupdate_time;
    private static GoogleMap googleMap;
    ArrayList<AddTrip> currenttripdetails;
    int trip_id;
    private static Bundle b;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        b=savedInstanceState;
        View view = inflater.inflate(R.layout.taskdetail_layout, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        Bundle taskdetail = this.getArguments();
        session = new SessionManager(getActivity().getApplicationContext());
        request= new Request(getActivity());
        vechile_trip_no=taskdetail.getString(ServiceConstants.VECHILE_TRIP_ID);
        progressBar=(ProgressBar)view.findViewById(R.id.taskdetailprogresbar);
        progressBar.setProgress(10);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
        txt_contTitle.setText(ServiceConstants.TASK_DETAIL_TITLE);
        Startbtn = (Button)view.findViewById(R.id.startbtn);
        Startbtn.setVisibility(View.GONE);
        inbox = (ImageButton) view.findViewById(R.id.inbox);
        inbox.setOnClickListener(new SendSmsButtonListener());
        whatsup=(ImageButton)view.findViewById(R.id.whatsup);
        truck = (TextView) view.findViewById(R.id.truckvalu);
        place = (TextView) view.findViewById(R.id.tovalue);
        phone = (TextView) view.findViewById(R.id.phoneno);
        customer_company = (TextView) view.findViewById(R.id.customer_company_val);
        customer_name = (TextView) view.findViewById(R.id.customenameval);
        customer_phone_no = (TextView) view.findViewById(R.id.customephonenoval);
        lastlocation=(TextView) view.findViewById(R.id.lastlocationvalue);
        updatetime=(TextView) view.findViewById(R.id.updateval);
        lasttimerow=(TableRow)view.findViewById(R.id.updatetextRow);
        locationrow=(TableRow)view.findViewById(R.id.last_locationrow);
        locationrow.setVisibility(view.GONE);
        lasttimerow.setVisibility(view.GONE);
        whatsup.setOnClickListener(new WhatsupButtonListener());
        mapView = (MapView)view.findViewById(R.id.map_view);
      mapView.onCreate(savedInstanceState);
       googleMap=mapView.getMap();
       new GetTrackDetail().execute("", "", "");

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




        Startbtn.setOnClickListener(new StartTrackButtonListener());
        return view;
    }

    private class StartTrackButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
           progressBar.setVisibility(View.VISIBLE);


          if(startclick==true){
               // mapDestroyOnDemand();
              // progressBar.setVisibility(View.VISIBLE);
              currenttripdetails=new ArrayList<AddTrip>();
              new UpdateEndTripdetail().execute("", "", "");

            }else {

             // progressBar.setVisibility(View.VISIBLE);
              new UpdateStartTripdetail().execute("", "", "");


           }

        }
    }

    private class WhatsupButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
            if (isWhatsappInstalled) {
               whats_up_dialog();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
                Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            }


        }
        }
    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
    private class SendSmsButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sms_dailog();

          /*  String number = customer_no.getText().toString();
            String smsmessage = ServiceConstants.MESSAGE_FOR_CUSTOMER;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, smsmessage, null, null);
            Log.d("Sms", "sendSMS " + smsmessage);
            Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!" + number,
                    Toast.LENGTH_SHORT).show();*/

        }
    }
/*
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub

        super.onDestroyView();

        Fragment fragment = (getFragmentManager()
        if (fragment != null) {
                .findFragmentById(R.id.map_view));
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

    private class UpdateStartTripdetail extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            Startbtn.setText("End Tracking");
            Startbtn.setBackgroundColor(Color.RED);
            lastlocation.setText(lastlocationtxt);
            if(lastupdate_time.equalsIgnoreCase("null")) {
                DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                Date date = new Date();
                //vechile_trip_id=Integer.parseInt(vechile_trip_no);
                updatetime.setText(dateFormat.format(date).toString());
            }else {
                updatetime.setText(lastupdate_time);
            }
            locationrow.setVisibility(View.VISIBLE);
            lasttimerow.setVisibility(View.VISIBLE);
            startclick = true;
            progressBar.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(String... params) {

            try {

                List<NameValuePair> upadatetripdetail = new ArrayList<NameValuePair>();
                upadatetripdetail.add(new BasicNameValuePair("vehicle_trip_header_id", vechile_trip_no));
                    HttpResponse response = request.requestPutType(ServiceConstants.START_TRIP,upadatetripdetail,ServiceConstants.BASE_URL);
                    responseStrng = "" + response.getStatusLine().getStatusCode();
            } catch (Exception e) {

                e.printStackTrace();

            }

            return responseStrng;

        }
    }

    private class UpdateEndTripdetail extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(String... params) {

            try {

                List<NameValuePair> upadatetripdetail = new ArrayList<NameValuePair>();
                upadatetripdetail.add(new BasicNameValuePair("vehicle_trip_header_id", vechile_trip_no));
                    HttpResponse response = request.requestPutType(ServiceConstants.END_TRIP, upadatetripdetail, ServiceConstants.BASE_URL);
                    responseStrng = "" + response.getStatusLine().getStatusCode();
                    if (response.getStatusLine().getStatusCode() == 200) {
                       // new updateMytripDetail().execute("", "", "");
                        CurrentTripFragment currenttripfrag = new CurrentTripFragment();
                        FragmentManager fragmentmanager = getFragmentManager();
                        FragmentTransaction fragmenttransaction = fragmentmanager
                                .beginTransaction();
                        fragmenttransaction.replace(R.id.viewers, currenttripfrag);
                        fragmenttransaction.addToBackStack(null);
                        fragmenttransaction.commit();
                    }


            } catch (Exception e) {

                e.printStackTrace();

            }

            return responseStrng;

        }
    }

    private class GetTrackDetail extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(String... params) {

            try {
                //progressBar.setVisibility(View.VISIBLE);
                String Gettrip_url = ServiceConstants.GET_TRIP + session.getPhoneno();
                HttpResponse response = request.requestGetType(Gettrip_url, ServiceConstants.BASE_URL);

                responseStrng = "" + response.getStatusLine().getStatusCode();
                if (response.getStatusLine().getStatusCode() == 200) {
                    JSONArray responsejSONArray = request.responseArrayParsing(response);
                    GetMytripListParsing mytripListParsing = new GetMytripListParsing();
                    List<AddTrip> mytripdetailslist = new ArrayList<AddTrip>();
                    mytripdetailslist.addAll(mytripListParsing.getmytriplist(responsejSONArray));
                    session.setAddtripdetails(mytripdetailslist);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(session.getAddtripdetails()!=null && session.getAddtripdetails().size() > 0){
                                    List<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
                                    currenttripdetailslist.addAll(session.getAddtripdetails());
                                    for(int i=0;i< currenttripdetailslist.size();i++){

                                        if(currenttripdetailslist.get(i).getVehicle_trip_id()== Integer.parseInt(vechile_trip_no)){
                                            trip_id=currenttripdetailslist.get(i).getVehicle_trip_id();
                                            System.out.println(trip_id);
                                            place.setText(currenttripdetailslist.get(i).getDestination());
                                            truck.setText(currenttripdetailslist.get(i).getTruckno());
                                            phone.setText(currenttripdetailslist.get(i).getDriver_phone_no());
                                            customer_company.setText(currenttripdetailslist.get(i).getCustomer_company());
                                            customer_name.setText(currenttripdetailslist.get(i).getCustomer_name());
                                            customer_phone_no.setText(currenttripdetailslist.get(i).getCustomer_phoneno());
                                            if(currenttripdetailslist.get(i).getOwner_phone_no().equalsIgnoreCase(session.getPhoneno())){
                                                if(currenttripdetailslist.get(i).isStartstatus()) {
                                                    if(currenttripdetailslist.get(i).getStart_end_Trip().equalsIgnoreCase("STR")){
                                                        Startbtn.setText("End Tracking");
                                                        Startbtn.setVisibility(View.VISIBLE);
                                                        Startbtn.setBackgroundColor(Color.RED);
                                                        lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                                                        lastlocation.setText(currenttripdetailslist.get(i).getLocation().toString());
                                                        updatetime.setText(currenttripdetailslist.get(i).getLast_sync_time().toString());
                                                        locationrow.setVisibility(View.VISIBLE);
                                                        lasttimerow.setVisibility(View.VISIBLE);
                                                        startclick = true;
                                                        //startclick = true;
                                                    }else{
                                                        Startbtn.setVisibility(View.VISIBLE);
                                                        lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                                                        lastupdate_time=currenttripdetailslist.get(i).getLast_sync_time().toString();
                                                    }
                                                }else{
                                                    Startbtn.setVisibility(View.GONE);
                                                    lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                                                }
                                            }else{
                                               if(currenttripdetailslist.get(i).isStartstatus()) {
                                                    if(currenttripdetailslist.get(i).getStart_end_Trip().equalsIgnoreCase("STR")){
                                                        //Startbtn.setText("End Tracking");
                                                        Startbtn.setVisibility(View.GONE);
                                                       // Startbtn.setBackgroundColor(Color.RED);
                                                        lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                                                        lastlocation.setText(currenttripdetailslist.get(i).getLocation().toString());
                                                        updatetime.setText(currenttripdetailslist.get(i).getLast_sync_time().toString());
                                                        locationrow.setVisibility(View.VISIBLE);
                                                        lasttimerow.setVisibility(View.VISIBLE);
                                                        //startclick = true;
                                                    }else{
                                                        Startbtn.setVisibility(View.GONE);
                                                        lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                                                        lastupdate_time=currenttripdetailslist.get(i).getLast_sync_time().toString();
                                                    }
                                                }else{
                                                    Startbtn.setVisibility(View.GONE);
                                                    lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                                                }
                                            }


                                        }


                                    }
                                    Load_map();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {

                e.printStackTrace();

            }

            return responseStrng;

        }
    }
    public void whats_up_dialog() {
        ((getActivity())).runOnUiThread(new Runnable() {
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View promptsView = inflater.inflate(R.layout.whatsup_daialog,
                        null);

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //promptsView.setBackgroundResource(R.color.);
                dialog.setContentView(promptsView);
                dialog.show();
              whatsuptext=(EditText) promptsView.findViewById(R.id.whatuptext);
                final String sms1=ServiceConstants.MESSAGE_SENDING_START;
                final String sms2=ServiceConstants.MESSAGE_URL+" "+"?"+" "+"trip ="+trip_id;
                final String sms3= ServiceConstants.MESSAGE_SENDING_END;
                final String sms = sms1 + sms2 + sms3;

                whatsuptext.setText(sms);


                Button btnOK = (Button) promptsView.findViewById(R.id.btnOK);
                btnOK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");
                        final String edittext=whatsuptext.getText().toString();
                        sendIntent.putExtra(Intent.EXTRA_TEXT,edittext);
                        startActivity(Intent.createChooser(sendIntent, "share with"));
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }

                });
            }
        });
    }
public void sms_dailog()
{
    (getActivity()).runOnUiThread(new Runnable() {
        public void run() {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View promptsView = inflater.inflate(R.layout.send_sms_popup, null);
           /*     EditText e1=*/
         final String num=customer_phone_no.getText().toString();

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //promptsView.setBackgroundResource(R.color.white);
             final EditText phnenum=(EditText) promptsView.findViewById(R.id.phonenum);
            phnenum.setText(num);
              message=(EditText)promptsView.findViewById(R.id.edittexview1);
            final String sms1=ServiceConstants.MESSAGE_SENDING_START;
            final String sms2=ServiceConstants.MESSAGE_URL+" "+"?"+" "+"trip ="+trip_id;
            final String sms3= ServiceConstants.MESSAGE_SENDING_END;
            final String sms = sms1 + sms2 + sms3;
            System.out.println("trip_id" + trip_id);
            message.setText(sms);
            dialog.setContentView(promptsView);
            dialog.show();

            Button textbutton = (Button) promptsView.findViewById(R.id.sndbtn);
            textbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String number="+91"+num;
                        SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null,message.getText().toString(), null, null);
                    Log.d("sms", "sms text is" + sms);

               dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "sms sent to" +number,Toast.LENGTH_SHORT)
                            .show();
                }

            });
        }
    });
}

    public void Load_map(){
        //mapView.onCreate(b);
      // googleMap=mapView.getMap();
        if(googleMap!=null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            //googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setMyLocationEnabled(true);
            MapsInitializer.initialize(getActivity().getApplicationContext());
            //GeoPoint geoPoint;
            try {
                String addressname = lastlocationtxt;
                Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext());
                List<Address> listAddress;
                listAddress = geoCoder.getFromLocationName(addressname, 1);
                if (listAddress == null || listAddress.size() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "No Location found", Toast.LENGTH_SHORT).show();
                    // return null;
                }else {
                    Address location = listAddress.get(0);
                    LatLng locationlatlng = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(
                            locationlatlng).title(""));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 10));
                }
                // googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

        }


    }
}
