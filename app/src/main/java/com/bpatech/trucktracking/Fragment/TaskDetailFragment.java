package com.bpatech.trucktracking.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.bpatech.trucktracking.Util.URLShortner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Anita on 9/14/2015.
 */
public class TaskDetailFragment extends Fragment   {

    protected Context context;
    TextView truck, place, phone, txt_contTitle,customer_company,
            customer_name,customer_phone_no,lastlocation,updatetime,text_message;;
    Button Startbtn,refreshbutton,whatsup;
    TableRow locationrow,lasttimerow,textmessagerow;
    ImageButton inbox;
    boolean startclick;
    boolean driverdownloadstatus;
    LinearLayout strBtnLayout;
    String vechile_trip_no;
    private static MapView mapView;
    Request request;
    URLShortner shorturl;
    String responseStrng;
    ProgressBar progressBar;
    SessionManager session;
    EditText whatsuptext,message;
    String lastlocationtxt,lastupdate_time;
    private static GoogleMap googleMap;
    Bundle taskdetail;
    Double mapLatitude;
    Double latitude;
    Double longitude;
    Double maplongitude;
    ArrayList<AddTrip> currenttripdetails;
    int trip_id;
    String trip_url;
    boolean pingNotReceived= false;
    String pingDiff;
    String Share_msg;
    private static Bundle b;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        b=savedInstanceState;
        View view = inflater.inflate(R.layout.taskdetail_layout, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
      taskdetail = this.getArguments();
       // System.out.println("++++++++++++taskdetail+++++++++++++++"+taskdetail);
        /*if(taskdetail==null){
            taskdetail=getActivity().getIntent().getExtras();
            //System.out.println("++++++++++++taskdetail+++++++++++++++"+taskdetail);
        }*/
        session = new SessionManager(getActivity().getApplicationContext());
        request= new Request(getActivity());
        shorturl = new URLShortner();
        vechile_trip_no=taskdetail.getString(ServiceConstants.VECHILE_TRIP_ID);
        progressBar=(ProgressBar)view.findViewById(R.id.taskdetailprogresbar);
        progressBar.setProgress(10);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
        txt_contTitle.setText(ServiceConstants.TASK_DETAIL_TITLE);
        Startbtn = (Button)view.findViewById(R.id.startbtn);
        Startbtn.setVisibility(View.INVISIBLE);
        strBtnLayout = (LinearLayout)view.findViewById(R.id.buttn_ll);

       // Startbtn.setEnabled(false);
        inbox = (ImageButton) view.findViewById(R.id.inbox);
        inbox.setOnClickListener(new SendSmsButtonListener());
        whatsup=(Button)view.findViewById(R.id.whatsup);
        refreshbutton=(Button)view.findViewById(R.id.refreshbtn);
        truck = (TextView) view.findViewById(R.id.truckvalu);
        place = (TextView) view.findViewById(R.id.tovalue);
        phone = (TextView) view.findViewById(R.id.phoneno);
        text_message=(TextView)view.findViewById(R.id.Text_message_value);
        textmessagerow=(TableRow)view.findViewById(R.id.textmessage_row);
        customer_company = (TextView) view.findViewById(R.id.customer_company_val);
        customer_name = (TextView) view.findViewById(R.id.customenameval);
        customer_phone_no = (TextView) view.findViewById(R.id.customephonenoval);
        lastlocation=(TextView) view.findViewById(R.id.lastlocationvalue);
        updatetime=(TextView) view.findViewById(R.id.updateval);
        lasttimerow=(TableRow)view.findViewById(R.id.updatetextRow);
        locationrow=(TableRow)view.findViewById(R.id.last_locationrow);
        locationrow.setVisibility(view.GONE);
        lasttimerow.setVisibility(view.GONE);

        whatsup.setOnClickListener(new ShareButtonListener());
        refreshbutton.setOnClickListener(new RefreshButtonListener());
        mapView = (MapView)view.findViewById(R.id.map_view);
        if (isGoogleMapsInstalled()==true){
            mapView.onCreate(savedInstanceState);
            googleMap = mapView.getMap();
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng latLng) {
                    DisplayMapFragment displayMapFragment= new DisplayMapFragment();
                    if(mapLatitude==null || maplongitude==null) {
                        latitude=latLng.latitude;
                        longitude=latLng.longitude;
                    }else {
                        latitude = mapLatitude;
                        longitude = maplongitude;
                    }
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
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Please... Install Google Maps",
                    Toast.LENGTH_LONG).show();
            /*View rootView = inflater.inflate(R.layout.location_enable_popup, container, false);
            getDialog().setTitle("Simple Dialog");
            return rootView;*/
        }

        /*googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng) {
                DisplayMapFragment displayMapFragment= new DisplayMapFragment();
                if(mapLatitude==null || maplongitude==null) {
                     latitude=latLng.latitude;
                     longitude=latLng.longitude;
                }else {
                  latitude = mapLatitude;
                   longitude = maplongitude;
                }
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
        });*/

        new GetTrackDetail().execute("", "", "");
        //new GetShortURL().execute("", "", "");

        Startbtn.setOnClickListener(new StartTrackButtonListener());
        return view;
    }

    private class StartTrackButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            if (driverdownloadstatus == true) {

                if (startclick == true) {
                    // mapDestroyOnDemand();
                    // progressBar.setVisibility(View.VISIBLE);
                    currenttripdetails = new ArrayList<AddTrip>();
                    new UpdateEndTripdetail().execute("", "", "");

                } else {

                    // progressBar.setVisibility(View.VISIBLE);
                    new UpdateStartTripdetail().execute("", "", "");


                }

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Driver has not downloaded the app",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
    private class RefreshButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

           TaskDetailFragment taskdetailfrag = new TaskDetailFragment();
            taskdetailfrag.setArguments(taskdetail);
            FragmentManager fragmentmanager = getFragmentManager();
            FragmentTransaction fragmenttransaction = fragmentmanager
                    .beginTransaction();
            fragmenttransaction.replace(R.id.viewers, taskdetailfrag, "BackRefreshCurrentTrip");
            fragmenttransaction.addToBackStack(null);
            fragmenttransaction.commit();
        }
    }
    private class ShareButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
           // boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            prepareMessage();
            /*String name=session.getUsername();
            if(session.getMessagelist().size()>0){
                Share_msg=session.getMessagelist().get(0).getShare_message();
            }else{
                final String sms1=ServiceConstants.MESSAGE_SENDING_START;
                //final String sms2=ServiceConstants.MESSAGE_URL+"?"+"trip="+trip_id;
                final String sms2=trip_url;
                //final String sms2=ServiceConstants.MESSAGE_URL+trip_id;
                final String sms3= ServiceConstants.MESSAGE_SENDING_END;
                Share_msg = name+sms1 + sms2 + sms3;
            }*/
            /*final String sms1=ServiceConstants.MESSAGE_SENDING_START;
            //final String sms2=ServiceConstants.MESSAGE_URL+"?"+"trip="+trip_id;
            final String sms2=trip_url;
            //final String sms2=ServiceConstants.MESSAGE_URL+trip_id;
            final String sms3= ServiceConstants.MESSAGE_SENDING_END;
            final String message = name+sms1 + sms2 + sms3;
          //  final String edittext=whatsuptext.getText().toString();*/
            sendIntent.putExtra(Intent.EXTRA_TEXT,Share_msg);
            startActivity(Intent.createChooser(sendIntent, "Share Via"));
        }
    }

  /*  private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }*/
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

      /*  @Override
        public void onDestroyView() {
            // TODO Auto-generated method stub

            super.onDestroyView();

            Fragment fragment = (getFragmentManager().findFragmentById(R.id.map_view));
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

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }*/
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        System.gc();
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
    private class GetShortURL extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

        }

        protected String doInBackground(String... params) {

            try {
                System.out.println("trip_id. in shorturl asycn task....."+trip_id);
                if(trip_id!=0){
                    String id = Integer.toString(trip_id);
                    JSONObject obj = shorturl.getJSONFromUrl(id);
                    //trip_url = obj.getString("id");
                    System.out.println("response......"+obj);
                    System.out.println("trip_url......"+trip_url);
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
                String Gettrip_url = ServiceConstants.TRACK_TRIP+Integer.parseInt(vechile_trip_no);
                HttpResponse response = request.requestGetType(Gettrip_url,ServiceConstants.BASE_URL);
                responseStrng = "" + response.getStatusLine().getStatusCode();
                if (response.getStatusLine().getStatusCode() == 200) {
                    JSONObject responsejSONObject = request.responseParsing(response);
                     GetMytripListParsing mytripListParsing = new GetMytripListParsing();
                    List<AddTrip> mytripdetailslist = new ArrayList<AddTrip>();
                    mytripdetailslist.addAll(mytripListParsing.Gettrip(responsejSONObject));
                    session.setAddtripdetails(mytripdetailslist);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                driverdownloadstatus=false;
                                if(session.getAddtripdetails()!=null && session.getAddtripdetails().size() > 0){
                                    List<AddTrip> currenttripdetailslist = new ArrayList<AddTrip>();
                                    currenttripdetailslist.addAll(session.getAddtripdetails());
                                            trip_id=currenttripdetailslist.get(0).getVehicle_trip_id();
                                            trip_url=currenttripdetailslist.get(0).getTrip_url();
                                         if( currenttripdetailslist.get(0).getDestination().toString().equalsIgnoreCase("null")) {
                                          place.setText("Not available");
                                             }else{
                                         place.setText(currenttripdetailslist.get(0).getDestination());
                                            }
                                           // place.setText(currenttripdetailslist.get(0).getDestination());
                                            truck.setText(currenttripdetailslist.get(0).getTruckno());
                                            phone.setText(currenttripdetailslist.get(0).getDriver_phone_no());
                                            customer_company.setText(currenttripdetailslist.get(0).getCustomer_company());
                                            customer_name.setText(currenttripdetailslist.get(0).getCustomer_name());
                                            customer_phone_no.setText(currenttripdetailslist.get(0).getCustomer_phoneno());
                                            if(currenttripdetailslist.get(0).getOwner_phone_no().equalsIgnoreCase(session.getPhoneno())){
                                                if(currenttripdetailslist.get(0).isStartstatus()) {
                                                    if(currenttripdetailslist.get(0).getStart_end_Trip().equalsIgnoreCase("STR")){
                                                        Startbtn.setText("End Tracking");
                                                        Startbtn.setVisibility(View.VISIBLE);
                                                       // Startbtn.setEnabled(true);
                                                        driverdownloadstatus=true;
                                                        Startbtn.setBackgroundColor(Color.RED);
                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {
                                                            // EditText EtPotential does not contain a valid double
                                                            e.printStackTrace();
                                                        }
                                                        lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                        if(currenttripdetailslist.get(0).getFullAddress().toString().equalsIgnoreCase("null") ) {
                                                            lastlocation.setText("");
                                                            textmessagerow.setVisibility(View.GONE);
                                                        }else {
                                                            lastlocation.setText(currenttripdetailslist.get(0).getFullAddress().toString());
                                                        }
                                                        updatetime.setText(currenttripdetailslist.get(0).getLast_sync_time().toString());
                                                        String diffDate=makeDecisonPing(currenttripdetailslist.get(0).getLast_ping_Datetime().toString());
                                                        String ping_message=ServiceConstants.PING_DIFFERENCE_MESSAGE+" "+diffDate;
                                                        text_message.setText(ping_message);
                                                        text_message.setTextColor(getResources().getColor(R.color.red));
                                                        locationrow.setVisibility(View.VISIBLE);
                                                        lasttimerow.setVisibility(View.VISIBLE);
                                                        startclick = true;
                                                        //startclick = true;
                                                    }else{
                                                        Startbtn.setVisibility(View.VISIBLE);
                                                        textmessagerow.setVisibility(View.GONE);

                                                        //Startbtn.setEnabled(true);
                                                        driverdownloadstatus=true;
                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {
                                                            // EditText EtPotential does not contain a valid double
                                                            e.printStackTrace();
                                                        }
                                                        lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                        lastupdate_time=currenttripdetailslist.get(0).getLast_sync_time().toString();
                                                    }
                                                }else{
                                                   //Startbtn.setVisibility(View.GONE);
                                                    Startbtn.setVisibility(View.VISIBLE);
                                                    textmessagerow.setVisibility(View.GONE);

                                                    //Startbtn.setEnabled(false);
                                                    driverdownloadstatus=false;
                                                    Startbtn.setBackgroundColor(R.color.gray);
                                                    try {
                                                    mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                    maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                    } catch (NumberFormatException e) {
                                                        // EditText EtPotential does not contain a valid double
                                                        e.printStackTrace();
                                                    }
                                                    lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                }
                                            }else{
                                                if(currenttripdetailslist.get(0).isStartstatus()) {
                                                    if(currenttripdetailslist.get(0).getStart_end_Trip().equalsIgnoreCase("STR")){
                                                        //Startbtn.setText("End Tracking");
                                                       Startbtn.setVisibility(View.GONE);
                                                        strBtnLayout.setVisibility(View.GONE);
                                                       // Startbtn.setVisibility(View.VISIBLE);
                                                        //Startbtn.setEnabled(false);
                                                        //driverdownloadstatus=false;
                                                        //Startbtn.setBackgroundColor(R.color.gray);
                                                        // Startbtn.setBackgroundColor(Color.RED);
                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {
                                                            // EditText EtPotential does not contain a valid double
                                                            e.printStackTrace();
                                                        }
                                                        lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                        if(currenttripdetailslist.get(0).getFullAddress().toString().equalsIgnoreCase("null") ) {
                                                            lastlocation.setText("");
                                                            textmessagerow.setVisibility(View.GONE);
                                                        }else {
                                                            lastlocation.setText(currenttripdetailslist.get(0).getFullAddress().toString());
                                                        }
                                                        updatetime.setText(currenttripdetailslist.get(0).getLast_sync_time().toString());
                                                        String diffDate=makeDecisonPing(currenttripdetailslist.get(0).getLast_ping_Datetime().toString());
                                                       String ping_message=ServiceConstants.PING_DIFFERENCE_MESSAGE+" "+diffDate;
                                                        text_message.setText(ping_message);
                                                        text_message.setTextColor(getResources().getColor(R.color.red));
                                                        locationrow.setVisibility(View.VISIBLE);
                                                        lasttimerow.setVisibility(View.VISIBLE);
                                                        //startclick = true;
                                                    }else{
                                                        Startbtn.setVisibility(View.GONE);
                                                        strBtnLayout.setVisibility(View.GONE);
                                                        textmessagerow.setVisibility(View.GONE);
                                                       // Startbtn.setVisibility(View.VISIBLE);
                                                        //Startbtn.setEnabled(false);
                                                        //driverdownloadstatus=false;
                                                        //Startbtn.setBackgroundColor(R.color.gray);
                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {
                                                            // EditText EtPotential does not contain a valid double
                                                            e.printStackTrace();
                                                        }
                                                        lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                        lastupdate_time=currenttripdetailslist.get(0).getLast_sync_time().toString();
                                                    }
                                                }else{
                                                    Startbtn.setVisibility(View.GONE);
                                                    strBtnLayout.setVisibility(View.GONE);
                                                    textmessagerow.setVisibility(View.GONE);
                                                    //Startbtn.setVisibility(View.VISIBLE);
                                                   // Startbtn.setEnabled(false);
                                                    //driverdownloadstatus=false;
                                                    //Startbtn.setBackgroundColor(R.color.gray);
                                                    try {
                                                        mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                        maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                    } catch (NumberFormatException e) {
                                                        // EditText EtPotential does not contain a valid double
                                                        e.printStackTrace();
                                                    }
                                                    lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
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
   /* public void whats_up_dialog() {
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
                final String sms2=ServiceConstants.MESSAGE_URL+"?"+"trip="+trip_id;
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
    }*/
    public void sms_dailog()
    {
        (getActivity()).runOnUiThread(new Runnable() {
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View promptsView = inflater.inflate(R.layout.send_sms_popup, null);
           /*     EditText e1=*/
                final String num = customer_phone_no.getText().toString();

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //promptsView.setBackgroundResource(R.color.white);
                final EditText phnenum = (EditText) promptsView.findViewById(R.id.phonenum);
                phnenum.setText(num);
                message = (EditText) promptsView.findViewById(R.id.edittexview1);
                prepareMessage();

                System.out.println("trip_id" + trip_id);
                message.setText(Share_msg);
                dialog.setContentView(promptsView);
                dialog.show();

                Button textbutton = (Button) promptsView.findViewById(R.id.sndbtn);
                textbutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        String number = "+91" + num;
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, message.getText().toString(), null, null);
                        Log.d("sms", "sms text is" + Share_msg);

                        dialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "sms sent to" + number, Toast.LENGTH_SHORT)
                                .show();
                    }

                });
            }
        });
    }

    public void Load_map(){
            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                //googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.setMyLocationEnabled(true);
                MapsInitializer.initialize(getActivity().getApplicationContext());
                //GeoPoint geoPoint;
                try {
                  /*  String addressname = lastlocationtxt;
                    Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext());
                    List<Address> listAddress;
                    listAddress = geoCoder.getFromLocationName(addressname, 1);*/
                   /*if (mapLatitude == null || maplongitude == null) {
                        Toast.makeText(getActivity().getApplicationContext(), "No Location found", Toast.LENGTH_LONG).show();
                        // return null;
                    } else {*/
                        // Address location = listAddress.get(0);
                        LatLng locationlatlng = new LatLng(mapLatitude, maplongitude);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(
                                locationlatlng).title(""));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 10));
                   //}
                    //}
                    // googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    }

    public boolean isGoogleMapsInstalled()
    {

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if(result != ConnectionResult.SUCCESS) {
            return false;
        }else{
            System.gc();
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
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.google.android.apps.maps"));
                getActivity().getApplicationContext().startActivity(intent);

                //Finish the activity so they can't circumvent the check
                //finish();
            }
        };
    }
    public void locationEnable_popup() {
        LayoutInflater inflater = LayoutInflater.from(getActivity().getWindow().getContext());
        View promptsView = inflater.inflate(R.layout.location_enable_popup, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity().getWindow().getContext()).create();

        alertDialog.setView(promptsView);

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();

        Button textbutton = (Button) promptsView.findViewById(R.id.btnYes);

        textbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                alertDialog.dismiss();

            }

        });
        Button textbutton1 = (Button) promptsView.findViewById(R.id.btnNo);
        textbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
       /* final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {

                alertDialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 5000); // the timer will count 5 seconds....
*/

    }
    private String makeDecisonPing(String lastpingtime){
        Date lastPingDate = getDate(lastpingtime);
        Date currentDate = getCurrentDate();
        String diffDate = getLastPingDifference(lastPingDate, currentDate);
        // System.out.println("+***************///////////+make decision"+diffDate);
        return diffDate;
    }
    private Date getCurrentDate(){

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy MMM dd,h:mm a");
        dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30
        //System.out.println("++++++++++++++++++++++++++++++++++long value+++++++++++++++++++++++++++" + firstmytriparry.getString("last_sync_date_time").toString());
        Date date = new Date(System.currentTimeMillis());
        return date;
    }
    private Date getDate(String convertingDate){
        Date date =null;
        try{
            DateFormat dateFormat1 = new SimpleDateFormat("yyyy MMM dd,h:mm a");
            dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30

            date = dateFormat1.parse(convertingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public String getLastPingDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        if(elapsedDays > 0){
            pingNotReceived = true;
            text_message.setVisibility(View.VISIBLE);
            //pingDiff=elapsedDays+"days,"+elapsedHours+" H:"+elapsedMinutes+" Mins";
            pingDiff = getLapsedDateMessage(elapsedDays,elapsedHours,elapsedMinutes);

        }else if(elapsedHours > 0){
            pingNotReceived = true;
            text_message.setVisibility(View.VISIBLE);
            //pingDiff=elapsedDays+"days,"+elapsedHours+" H:"+elapsedMinutes+" Mins";
            pingDiff = getLapsedDateMessage(elapsedDays,elapsedHours,elapsedMinutes);
        }

        else if(elapsedMinutes > 20){
            pingNotReceived = true;
            text_message.setVisibility(View.VISIBLE);
           // pingDiff=elapsedDays+"days,"+elapsedHours+" H:"+elapsedMinutes+" Mins";
            pingDiff = getLapsedDateMessage(elapsedDays,elapsedHours,elapsedMinutes);
        }
        else {
            text_message.setVisibility(View.GONE);
        }

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
        return pingDiff;
    }
    private String getLapsedDateMessage(long days,long hours,long mins){
        String return_messsage= null;
        String day_m="";String hours_m="";String mins_m="";
        if(days>0){
            day_m =  +days+"days";
        }else if(hours>0){
            hours_m = hours+" hours ";
        }else if(mins>0){
            mins_m = mins+" mins";
        }
        return_messsage = day_m+hours_m+mins_m;
        return return_messsage;

    }
    private void prepareMessage(){
        String name=session.getUsername();
        if(session.getMessagelist().size()>0){
            Share_msg=name+" "+session.getMessagelist().get(0).getShare_message()+" "+trip_url+" "+ServiceConstants.MESSAGE_SENDING_END;
        }else{
            final String sms1=ServiceConstants.MESSAGE_SENDING_START;
            //final String sms2=ServiceConstants.MESSAGE_URL+"?"+"trip="+trip_id;
            final String sms2=trip_url;
            //final String sms2=ServiceConstants.MESSAGE_URL+trip_id;
            final String sms3= ServiceConstants.MESSAGE_SENDING_END;
            Share_msg = name+sms1 + sms2 + sms3;
        }
    }
}
