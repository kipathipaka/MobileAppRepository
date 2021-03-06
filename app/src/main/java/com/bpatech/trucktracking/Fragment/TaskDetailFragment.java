package com.bpatech.trucktracking.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import timber.log.Timber;


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
    RelativeLayout taskdeatlilayout,taskdetailbuttonlayout;
    SessionManager session;
    EditText message;
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

        session = new SessionManager(getActivity().getApplicationContext());
        request= new Request(getActivity());
        shorturl = new URLShortner();
        vechile_trip_no=taskdetail.getString(ServiceConstants.VECHILE_TRIP_ID);
        progressBar=(ProgressBar)view.findViewById(R.id.taskdetailprogresbar);
        progressBar.setProgress(10);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);

        txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
        taskdeatlilayout = (RelativeLayout) view.findViewById(R.id.taskdetaillayout);
        taskdeatlilayout.setOnClickListener(new Layoutclicklistener());
        taskdetailbuttonlayout = (RelativeLayout) view.findViewById(R.id.taskdetailbuttonlayout);
        taskdetailbuttonlayout.setOnClickListener(new Layoutclicklistener());
        txt_contTitle.setText(ServiceConstants.TASK_DETAIL_TITLE);
        Startbtn = (Button)view.findViewById(R.id.startbtn);
        Startbtn.setVisibility(View.INVISIBLE);
        strBtnLayout = (LinearLayout)view.findViewById(R.id.buttn_ll);


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

        }



        new GetTrackDetail().execute("", "", "");


        Startbtn.setOnClickListener(new StartTrackButtonListener());
        return view;
    }
    private class Layoutclicklistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }
    private class StartTrackButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            if (driverdownloadstatus == true) {

                if (startclick == true) {

                    currenttripdetails = new ArrayList<AddTrip>();
                    new UpdateEndTripdetail().execute("", "", "");

                } else {


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

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            prepareMessage();

            sendIntent.putExtra(Intent.EXTRA_TEXT,Share_msg);
            startActivity(Intent.createChooser(sendIntent, "Share Via"));
        }
    }

    private class SendSmsButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sms_dailog();


        }
    }


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
            if(lastlocationtxt.equalsIgnoreCase("null")) {
                lastlocation.setText("Not Available");
            }else{
                lastlocation.setText(lastlocationtxt);
            }
            if(lastupdate_time.equalsIgnoreCase("null")) {
                DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                Date date = new Date();
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

                                                        driverdownloadstatus=true;
                                                        Startbtn.setBackgroundColor(Color.RED);
                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {

                                                            e.printStackTrace();
                                                        }
                                                        lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                        if(currenttripdetailslist.get(0).getFullAddress().toString().equalsIgnoreCase("null") ) {
                                                            lastlocation.setText("Not Available");
                                                            textmessagerow.setVisibility(View.GONE);
                                                        }else {
                                                            lastlocation.setText(currenttripdetailslist.get(0).getFullAddress().toString());
                                                        }
                                                        updatetime.setText(currenttripdetailslist.get(0).getLast_sync_time().toString());

                                                        locationrow.setVisibility(View.VISIBLE);
                                                        lasttimerow.setVisibility(View.VISIBLE);
                                                        startclick = true;
                                                        String diffDate=makeDecisonPing(currenttripdetailslist.get(0).getLast_ping_Datetime().toString());
                                                        String ping_message=ServiceConstants.PING_DIFFERENCE_MESSAGE+" "+diffDate;
                                                        text_message.setText(ping_message);
                                                        text_message.setTextColor(getResources().getColor(R.color.red));

                                                    }else{
                                                        Startbtn.setVisibility(View.VISIBLE);
                                                        textmessagerow.setVisibility(View.GONE);

                                                        driverdownloadstatus=true;
                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {

                                                            e.printStackTrace();
                                                        }
                                                        lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                        lastupdate_time=currenttripdetailslist.get(0).getLast_sync_time().toString();
                                                    }
                                                }else{

                                                    Startbtn.setVisibility(View.VISIBLE);
                                                    textmessagerow.setVisibility(View.GONE);


                                                    driverdownloadstatus=false;
                                                    Startbtn.setBackgroundColor(R.color.gray);
                                                    try {
                                                    mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                    maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                    } catch (NumberFormatException e) {

                                                        e.printStackTrace();
                                                    }
                                                    lastlocationtxt=currenttripdetailslist.get(0).getFullAddress().toString();
                                                }
                                            }else{
                                                if(currenttripdetailslist.get(0).isStartstatus()) {
                                                    if(currenttripdetailslist.get(0).getStart_end_Trip().equalsIgnoreCase("STR")){

                                                       Startbtn.setVisibility(View.GONE);
                                                        strBtnLayout.setVisibility(View.GONE);





                                                        try {
                                                            mapLatitude=Double.parseDouble(currenttripdetailslist.get(0).getLatitude().toString());
                                                            maplongitude=Double.parseDouble(currenttripdetailslist.get(0).getLongitude().toString());
                                                        } catch (NumberFormatException e) {

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

                                                    }else{
                                                        Startbtn.setVisibility(View.GONE);
                                                        strBtnLayout.setVisibility(View.GONE);
                                                        textmessagerow.setVisibility(View.GONE);




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

    public void sms_dailog()
    {
        (getActivity()).runOnUiThread(new Runnable() {
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View promptsView = inflater.inflate(R.layout.send_sms_popup, null);

                final String num = customer_phone_no.getText().toString();

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                final EditText phnenum = (EditText) promptsView.findViewById(R.id.phonenum);
                phnenum.setText(num);
                message = (EditText) promptsView.findViewById(R.id.edittexview1);
                prepareMessage();


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


                googleMap.setMyLocationEnabled(true);
                MapsInitializer.initialize(getActivity().getApplicationContext());

                try {
                        LatLng locationlatlng = new LatLng(mapLatitude, maplongitude);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(
                                locationlatlng).title(""));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 10));
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


            }
        };
    }
    private String makeDecisonPing(String lastpingtime){
        Date lastPingDate = getDate(lastpingtime);
        Date currentDate = getCurrentDate();
        String diffDate = getLastPingDifference(lastPingDate, currentDate);

        return diffDate;
    }
    private Date getCurrentDate(){

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy MMM dd,h:mm a");
        dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//GMT+5:30

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

        if(session.getMessagelist()!=null) {
            if (session.getMessagelist().size() > 0) {
                Share_msg = name + " " + session.getMessagelist().get(0).getShare_message() + " " + trip_url + " " + ServiceConstants.MESSAGE_SENDING_END;
            } else {
                final String sms1 = ServiceConstants.MESSAGE_SENDING_START;

                final String sms2 = trip_url;

                final String sms3 = ServiceConstants.MESSAGE_SENDING_END;
                Share_msg = name + sms1 + sms2 + sms3;
            }
        }else{
            final String sms1 = ServiceConstants.MESSAGE_SENDING_START;

            final String sms2 = trip_url;

            final String sms3 = ServiceConstants.MESSAGE_SENDING_END;
            Share_msg = name + sms1 + sms2 + sms3;
        }
    }
}
