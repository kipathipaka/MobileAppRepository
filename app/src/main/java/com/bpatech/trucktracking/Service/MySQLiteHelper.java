package com.bpatech.trucktracking.Service;

/**
 * Created by Anita on 9/10/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bpatech.trucktracking.DTO.User;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MyTripDB";

    // Contacts table name
    private static final String TABLE_USER = "userinfo";

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
                +  KEY_OTP_NO + " INTEGER, " +  KEY_COMPANY + " TEXT, "+  KEY_PH_NO + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d("After tabele: ", "created ..");
        // TODO Auto-generated method stub

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //Log.d("After Insert: ", "deleted ..");
        // Create tables again
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
        values.put(KEY_OTP_NO, user.getOtp_no());
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


}
