package com.bpatech.trucktracking.Util;

/**
 * Created by Anita on 9/10/2015.
 */
public class ServiceConstants {

    public static final boolean LOGGING = false;
    public static final String IS_LOGIN = "IsLoggedIn";
   // public static final String BASE_URL= "http://52.88.194.128:8080";
    public static final String BASE_URL= "http://52.88.194.128:2020/vehicletracking-spring";
    public static final String UPDATE_LOCATION="/api/trip/trackTrip";
    public static final String CREATE_USER="/api/user/addUser";
    public static final String ADD_DRIVER_PHONE="/api/drivers/addDriverPhone";
    public static final String ADD_TRIP="/api/trip/addTrip";
    public static final String GET_DRIVER="/api/drivers/";
    public static final String GET_USER="/api/user/";
    public static final String GET_TRIP="/api/trip/mytrips/";
    public static final String START_TRIP="/api/trip/startTrip";
    public static final String END_TRIP="/api/trip/endTrip";
    public static final String UPDATE_USER="/api/user/updateUser";
    public static final String PREVIOUS_PAGE = "previousPage";
    public static final String CUURENT_TRIP_FRAGMENT = "CurrentTripFragment";
    public static final String CUURENT_TRIP_PLACE = "Place";
    public static final String CUURENT_TRIP_TRUCK= "Truck";
    public static final String CUURENT_TRIP_PHONE= "phone";
    public static final String APP_CRASH_ADMIN_MAILID ="anitha@bpatech.com";
    public static final String PREF_NAME = "VehicleTracking";
    public static final String TASK_DETAIL_TITLE = " Trip Details";
    public static final String TASK_DETAIL_ENDPAGE = "Startclick";
    public static final String ADD_TRIP_CUSTOMER= "Customer";
    public static final String ADD_TRIP_CUSTOMER_NAME= "Customer_name";
    public static final String VECHILE_TRIP_ID= "vechile_trip_id";
    public static final String ADD_TRIP_CUSTOMER_NO = "Customer_no";
    public static final String ADD_TRIP_SOURCE = "source";
    public static final String MESSAGE_FOR_ADDPHONE="Owner has added u as Driver";
    public static final String MESSAGE_FOR_CUSTOMER="You have added as Driver";
    public static  String MESSAGE_SENDING_START="Welcome,\n To know your Truck current location Click below link";
    public static String MESSAGE_URL="\nhttp://52.88.194.128:2020/vehicletracking-spring/Home.html";
    public static String MESSAGE_SENDING_END="\nThank You";
    public static String TEXT_MESSAGE="hi, Welcome to vehicleTracking";

}
