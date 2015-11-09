package com.bpatech.trucktracking.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import com.bpatech.trucktracking.Util.CustomAdapter;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
    * Created by Anita on 9/14/2015.
            */
    public class TaskDetailFragment extends Fragment   {

    protected Context context;
    TextView truck, place, phone, txt_contTitle,customer,customer_name,customer_no, lastlocation,updatetime,vechile_trip_id;
    Button Startbtn;
    boolean isstarttrip=true,isendtrip=false;
    TableRow locationrow,lasttimerow;
    ImageButton whatsup,inbox;
    boolean startclick;
    String vechile_trip_no;
    MapView mapView;
    Request request;
    String responseStrng;
    ProgressBar progressBar;
    SessionManager session;
    Double latitude;
    Double longitude;
    String lastlocationtxt,lastupdate_time;
    String addresstxt;
    public GoogleMap googleMap;
    ArrayList<AddTrip> currenttripdetails;
    int trip_id;
    LatLng LOCATION;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        View view = inflater.inflate(R.layout.taskdetail_layout, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        Bundle taskdetail = this.getArguments();
        session = new SessionManager(getActivity().getApplicationContext());
        request= new Request(getActivity().getApplicationContext());
        progressBar=(ProgressBar)view.findViewById(R.id.taskdetailprogresbar);
        progressBar.setProgress(10);
        progressBar.setMax(100);
        progressBar.setVisibility(View.INVISIBLE);
        txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
        txt_contTitle.setText(ServiceConstants.TASK_DETAIL_TITLE);
        Startbtn = (Button)view.findViewById(R.id.startbtn);
        inbox = (ImageButton) view.findViewById(R.id.inbox);
        inbox.setOnClickListener(new SendSmsButtonListener());
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
        vechile_trip_no=taskdetail.getString(ServiceConstants.VECHILE_TRIP_ID);
        if(session.getAddtripdetails()!=null && session.getAddtripdetails().size() > 0){
            List<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
            currenttripdetailslist.addAll(session.getAddtripdetails());
            for(int i=0;i< currenttripdetailslist.size();i++){
                if(currenttripdetailslist.get(i).getVehicle_trip_id()== Integer.parseInt(vechile_trip_no)){
                    trip_id=currenttripdetailslist.get(i).getVehicle_trip_id();
                    System.out.println(trip_id);
                   if(currenttripdetailslist.get(i).getDriver_phone_no().equalsIgnoreCase(session.getPhoneno()) || currenttripdetailslist.get(i).getCustomer_phoneno().equalsIgnoreCase(session.getPhoneno()))
                   {
                       if(currenttripdetailslist.get(i).isStartstatus()) {
                           if(currenttripdetailslist.get(i).getStart_end_Trip().equalsIgnoreCase("STR")){

                               lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                               lastlocation.setText(currenttripdetailslist.get(i).getLocation().toString());

                               updatetime.setText(currenttripdetailslist.get(i).getLast_sync_time().toString());

                               locationrow.setVisibility(View.VISIBLE);
                               lasttimerow.setVisibility(View.VISIBLE);
                               //startclick = true;
                           }else{
                               lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                               lastupdate_time=currenttripdetailslist.get(i).getLast_sync_time().toString();
                           }
                       }else{
                           Startbtn.setVisibility(View.INVISIBLE);
                           lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                       }
                   }else{
                       if(currenttripdetailslist.get(i).isStartstatus()) {
                           if(currenttripdetailslist.get(i).getStart_end_Trip().equalsIgnoreCase("STR")){
                               Startbtn.setText("End Tracking");
                               Startbtn.setVisibility(View.VISIBLE);
                               Startbtn.setBackgroundColor(Color.RED);
                               lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                               lastlocation.setText(currenttripdetailslist.get(i).getLocation().toString());
                           /* if(currenttripdetailslist.get(i).getLast_sync_time().toString().equalsIgnoreCase("null")) {
                                DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                                Date date = new Date();*/
                               //vechile_trip_id=Integer.parseInt(vechile_trip_no);
                               updatetime.setText(currenttripdetailslist.get(i).getLast_sync_time().toString());
                          /*  }else {
                                DateFormat dateFormat1 = new SimpleDateFormat("h:mm a");
                                dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+17:30"));
                                Date date = new Date(Long.parseLong(currenttripdetailslist.get(i).getLast_sync_time().toString()));
                                updatetime.setText(dateFormat1.format(date).toString());
                            }*/
                               locationrow.setVisibility(View.VISIBLE);
                               lasttimerow.setVisibility(View.VISIBLE);
                               startclick = true;
                           }else{
                               Startbtn.setVisibility(View.VISIBLE);
                               lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                               lastupdate_time=currenttripdetailslist.get(i).getLast_sync_time().toString();
                           }
                       }else{
                           Startbtn.setVisibility(View.INVISIBLE);
                           lastlocationtxt=currenttripdetailslist.get(i).getLocation().toString();
                       }
                   }


                }


            }

        }
        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        googleMap=mapView.getMap();
        if(googleMap!=null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            //googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setMyLocationEnabled(true);
            MapsInitializer.initialize(this.getActivity());
            //GeoPoint geoPoint;
             try {
                String addressname = lastlocationtxt;
                Geocoder geoCoder = new Geocoder(this.getActivity());
                List<Address> listAddress;
                listAddress = geoCoder.getFromLocationName(addressname, 1);
                if (listAddress == null || listAddress.size() == 0) {
                    Toast.makeText(this.getActivity(), "No Location found", Toast.LENGTH_SHORT).show();
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
        }

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

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            //IntentSender sender =new IntentSender(IntentSender.CREATOR));
           // sender.sendIntent();
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            if (sendIntent!= null) {
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Whatsup text msg");
                startActivity(sendIntent);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }


        }
    }

   /* private class WhatsupButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
            if (isWhatsappInstalled) {
            Intent sendIntent = new Intent();
           sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Whatsup text msg");
                startActivity(Intent.createChooser(sendIntent, "share with"));
               whats_up_dialog();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
                Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            }


        }
        }*/
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
                if (response.getStatusLine().getStatusCode() == 200) {
                    new GetMytripDetail().execute("", "", "");
                }
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
                        new updateMytripDetail().execute("", "", "");
                    }


            } catch (Exception e) {

                e.printStackTrace();

            }

            return responseStrng;

        }
    }

    private class updateMytripDetail extends
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
                    currenttripdetails.addAll(mytripListParsing.getmytriplist(responsejSONArray));
                    session.setAddtripdetails(currenttripdetails);
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
    private class GetMytripDetail extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

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
                promptsView.setBackgroundResource(R.color.white);
                dialog.setContentView(promptsView);
                dialog.show();
                EditText whatsuptext=(EditText) promptsView.findViewById(R.id.whatuptext);
                whatsuptext.setText("start tracking have started");
                Button btnOK = (Button) promptsView.findViewById(R.id.btnOK);
                btnOK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
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
         final String num=customer_no.getText().toString();

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            promptsView.setBackgroundResource(R.color.white);
             final EditText phnenum=(EditText) promptsView.findViewById(R.id.phonenum);
            phnenum.setText(num);
             EditText message=(EditText)promptsView.findViewById(R.id.edittexview1);
            final String sms1=ServiceConstants.MESSAGE_SENDING_START;
            final String sms2=ServiceConstants.MESSAGE_URL+" "+"?"+" "+"trip ="+trip_id;
            final String sms3= ServiceConstants.MESSAGE_SENDING_END;
            final String sms=sms1+sms2+sms3;
            System.out.println("trip_id"+trip_id);
            message.setText(sms);
                    dialog.setContentView(promptsView);
            dialog.show();

            Button textbutton = (Button) promptsView.findViewById(R.id.sndbtn);
            textbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                   // String num=customer_no.getText().toString();

                   /* String smsmessage="hiiiiiiiii";*/
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(num, null,sms, null, null);
                    Log.d("sms", "sms text is" + sms);

               dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "sms sent to" +num, Toast.LENGTH_SHORT)
                            .show();
                }

            });
        }
    });
}
}
