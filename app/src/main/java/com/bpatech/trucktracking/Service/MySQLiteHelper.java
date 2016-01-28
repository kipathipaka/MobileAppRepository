package com.bpatech.trucktracking.Service;

/**
 * Created by Anita on 9/10/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bpatech.trucktracking.DTO.UpdateLocationDTO;
import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MyTripDB";
    AddUserObjectParsing obj;
    // Contacts table name
    private static final String TABLE_USER = "userinfo";
    private static final String LOCATION_UPDATE = "tracklocationdetails";
    Request request;
    SessionManager session;
    User user;

    Context context;
  String  responseStrng;
    // Contacts Table Columns names
    private static final String KEY_ID = "user_id";
    private static final String KEY_OTP_NO = "otp_no";
    private static final String KEY_PH_NO  = "phone_number";
    private static final String KEY_COMPANY = "companyName";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_DRIVER_NO = "driverphoneno";
    private static final String KEY_LATITUDE= "latitude";
    private static final String KEY_LONGITUDE= "longitude";
    private static final String KEY_LOCATION= "location";
    private static final String KEY_FULLADDRESS= "fulladdress";
    private static final String KEY_LAST_SYNC_TIME= "updatetime";
    private static final String KEY_UPDATE_ID = "location_id";
    private SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS  " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " +  KEY_USER_NAME + " TEXT,"
                + KEY_COMPANY + " TEXT, "+  KEY_PH_NO + " TEXT" +")";
        String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS  " + LOCATION_UPDATE + "("
                + KEY_UPDATE_ID + " INTEGER PRIMARY KEY, " +  KEY_DRIVER_NO + " TEXT,"
                + KEY_LATITUDE + " TEXT," +  KEY_LONGITUDE + " TEXT," + KEY_LOCATION + " TEXT,"
                + KEY_FULLADDRESS + " TEXT," + KEY_LAST_SYNC_TIME + " TEXT" + ")";
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        System.out.println("++++++++++++++++After tabele: Table created "+CREATE_LOCATION_TABLE);
        Timber.i("MySQLiteHelper After tabele: Table created**************************");
        Log.d("After tabele: ", "created ..");
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //Log.d("After Insert: ", "deleted ..");
        // Create tables again
        Log.d("After tabele: ", "deleted ..");

        onCreate(db);
        // TODO Auto-generated method stub
        Timber.i(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

    }
    public void addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        // onUpgrade(db,0,0);
        onCreate(db);
        ContentValues values = new ContentValues();
        // user Name
        values.put(KEY_USER_NAME, user.getUserName());
        values.put(KEY_COMPANY, user.getCompanyName());
        values.put(KEY_PH_NO, user.getPhone_no());


        // Contact Phone
        // Inserting Row

        db.insert(TABLE_USER, null, values);

        Timber.i("After Insert: ", "Inserted ..");
        Log.d("After Insert: ", "Inserted ..");
        db.close(); // Closing database connection
    }

    public void addUpdateLocationDetails(UpdateLocationDTO updatelocation) {

        SQLiteDatabase db = this.getWritableDatabase();
        // onUpgrade(db,0,0);
        onCreate(db);
        ContentValues values = new ContentValues();
        values.put(KEY_DRIVER_NO, updatelocation.getDriver_phone_no());
        values.put(KEY_LATITUDE,updatelocation.getLocation_latitude());
        values.put(KEY_LONGITUDE,updatelocation.getLocation_longitude());
       // values.put(KEY_LONGITUDE,"80.34455");
        values.put(KEY_LOCATION,updatelocation.getLocation());
        values.put(KEY_FULLADDRESS,updatelocation.getFulladdress());
        values.put(KEY_LAST_SYNC_TIME,updatelocation.getUpdatetime());
        // Contact Phone
        // Inserting Row*/
        db.insert(LOCATION_UPDATE, null, values);
        System.out.println("+++++++++++++++++Inserted 1+++");
        Timber.i("After Insert: ", "Inserted ..");
        Log.d("After Insert: ", "Inserted ..");
        db.close(); // Closing database connection
    }

    public int getUserCount() {
        String countQuery = " SELECT  * FROM  " +  TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
         int countval=cursor.getCount();

        cursor.close();

        return countval;
    }
    public int getUpdateLocationCount() {
        String countQuery = " SELECT  * FROM  " +LOCATION_UPDATE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
      //  System.out.println("++++++++++location+++++cursor++"+cursor);
        int countval=cursor.getCount();
      //  System.out.println("++++++++++location+++++count++"+countval);
        cursor.close();

        return countval;
    }

    public ArrayList<User> getOwnerphoneno() {
        ArrayList<User> owener_list = new ArrayList<User>();
        String countQuery = " SELECT  * FROM  " +  TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                User user=new User();
                user.setUser_id(cursor.getInt(0));
                user.setUserName(cursor.getString(1));
                user.setCompanyName(cursor.getString(2));
                user.setPhone_no(cursor.getString(3));
                owener_list.add(user);
            } while (cursor.moveToNext());
        }
        return owener_list;
    }
    public ArrayList<UpdateLocationDTO> getTracktripDetails() {
        ArrayList<UpdateLocationDTO> track_trip_list = new ArrayList<UpdateLocationDTO>();
        String countQuery = "SELECT  * FROM " + LOCATION_UPDATE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                UpdateLocationDTO updateLocationDTO=new UpdateLocationDTO();
                updateLocationDTO.setUpdate_id(cursor.getInt(0));
                //System.out.println("++sql+++++++++phone no++++++++" + cursor.getString(1));
                updateLocationDTO.setDriver_phone_no(cursor.getString(1));
                updateLocationDTO.setLocation_latitude(cursor.getString(2));
                updateLocationDTO.setLocation_longitude(cursor.getString(3));
                updateLocationDTO.setLocation(cursor.getString(4));
                updateLocationDTO.setFulladdress(cursor.getString(5));
                updateLocationDTO.setUpdatetime(cursor.getString(6));
                track_trip_list.add(updateLocationDTO);
            } while (cursor.moveToNext());
        }
        return track_trip_list;
    }

    public void deleteLocation_byID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+LOCATION_UPDATE+" where "+KEY_UPDATE_ID+"= "+id+" ");
       // sqLiteDatabase.delete(LOCATION_UPDATE,KEY_UPDATE_ID+" = "+id, null);
    }
  /*  public boolean checkPhonenumber(String phoneno) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exitphonenumber=false;
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_USER_NAME,KEY_COMPANY, KEY_PH_NO}, KEY_PH_NO + "=?", new String[]{String.valueOf(phoneno)}, null, null, null, null);
        System.out.println("count cursor"+cursor.getCount());
        //  Log.w("After count: ",cursor.getCount() );
        if (cursor.getCount() > 0){
            Log.d("After check: ", "checked..true");
            exitphonenumber = true;
        }else{
            Log.d("After check: ", "checked.. false");
            exitphonenumber = false;
        }

        Log.d("After check: ", "checked..");
        return exitphonenumber;
    }*/



}
