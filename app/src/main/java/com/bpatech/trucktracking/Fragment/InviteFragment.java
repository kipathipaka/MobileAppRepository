package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ExceptionHandler;

/**
 * Created by Yugandhar on 9/28/2015.
 */
public class InviteFragment extends Fragment
{

    Button sndbtn;
    EditText phonenum,edittexview1;
    TextView txt_contTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View view = inflater.inflate(R.layout.invite_layout, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
        txt_contTitle.setText("Invite");
        sndbtn=(Button)view.findViewById(R.id.sndbtn);
        phonenum=(EditText)view.findViewById(R.id.phonenum);
        edittexview1=(EditText)view.findViewById(R.id.edittexview1);

        sndbtn.setOnClickListener(new MyaddButtonListener());
        return view;
    }
    private class MyaddButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (phonenum.getText().toString().trim().equalsIgnoreCase("") || edittexview1.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
                            Toast.LENGTH_SHORT).show();

                } else if(phonenum.getText().toString().length()==10) {
                    String number = phonenum.getText().toString();
                    String sms = edittexview1.getText().toString();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, sms, null, null);
                    Log.d("Sms", "sendSMS " + sms);
                    Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!"+number,
                            Toast.LENGTH_SHORT).show();

                    CurrentTripFragment currenttripfrag = new CurrentTripFragment();
                    FragmentManager fragmentmanager = getFragmentManager();
                    FragmentTransaction fragmenttransaction = fragmentmanager
                            .beginTransaction();
                    fragmenttransaction.replace(R.id.viewers, currenttripfrag);

                    fragmenttransaction.addToBackStack(null);
                    fragmenttransaction.commit();


            }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter the valid phone number",
                            Toast.LENGTH_SHORT).show();
                }
        }

        catch(Exception e)
        {
           Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        }
    }
}
