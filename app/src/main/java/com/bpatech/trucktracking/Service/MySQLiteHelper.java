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

import com.bpatech.trucktracking.DTO.User;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MyTripDB";
    AddUserObjectParsing obj;
    // Contacts table name
    private static final String TABLE_USER = "userinfo";
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

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS  " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " +  KEY_USER_NAME + " TEXT,"
                + KEY_COMPANY + " TEXT, "+  KEY_PH_NO + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
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

    private class UpdatuserStatus extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            //progressBar.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(String... params) {

            try {
                obj = new AddUserObjectParsing();
                request= new Request();
                user=new User();
                session = new SessionManager(context);
                List<NameValuePair> createuserlist = new ArrayList<NameValuePair>();
                createuserlist.addAll(obj.userCreationObject(session.getPhoneno(), user.getCompanyName(), "N", "N", "mathi"));
                     HttpResponse response = request.requestPutType(ServiceConstants.UPDATE_USER,createuserlist, ServiceConstants.BASE_URL);
                        responseStrng = ""+response.getStatusLine().getStatusCode();

                        if (response.getStatusLine().getStatusCode() == 200) {


                        }



            } catch (Exception e) {

                e.printStackTrace();

            }

            return responseStrng;

        }

    }

}
