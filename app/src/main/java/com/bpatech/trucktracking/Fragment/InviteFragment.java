package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

/**
 * Created by Yugandhar on 9/28/2015.
 */
public class InviteFragment extends Fragment
{

    Button sndbtn;
    EditText phonenum,edittexview1;
    TextView txt_contTitle;
    SessionManager session;
    String invite_message=null;
    RelativeLayout inviteLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.invite_layout, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        session = new SessionManager(getActivity().getApplicationContext());
        txt_contTitle=(TextView)view.findViewById(R.id.txt_contTitle);
        txt_contTitle.setText("Invite");
        sndbtn=(Button)view.findViewById(R.id.sndbtn);
        phonenum=(EditText)view.findViewById(R.id.phonenum);
        inviteLayout = (RelativeLayout) view.findViewById(R.id.invite_layout);
        edittexview1=(EditText)view.findViewById(R.id.edittexview1);
        String phone =session.getUsername();
        if(session.getMessagelist()!=null) {
            if (session.getMessagelist().size() > 0) {
                invite_message = phone + " " +session.getMessagelist().get(0).getInvite_message();

            } else {
                String sms1 = ServiceConstants.MESSAGE_INVITE;
                String sms2 = ServiceConstants.APP_NAME;
                String sms3 = ServiceConstants.TEXT_MESSAGE_URL;
                invite_message = phone + " " + sms1 + " " + sms2 + " " + sms3;
            }
        }
        edittexview1.setText(invite_message);
        inviteLayout.setOnClickListener(new InviteLayoutclicklistener());
        sndbtn.setOnClickListener(new MyaddButtonListener());
        return view;
    }

    private class InviteLayoutclicklistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }
    private class MyaddButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (phonenum.getText().toString().trim().equalsIgnoreCase("") || edittexview1.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
                            Toast.LENGTH_LONG).show();

                } else if(phonenum.getText().toString().length()==10) {
                    String number = phonenum.getText().toString();

                    String smsno="+91"+number;
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(smsno, null, edittexview1.getText().toString(), null, null);
                    //Log.d("Sms", "sendSMS " + sms);
                    Toast.makeText(getActivity().getApplicationContext(), "SMS Sent!"+smsno,
                            Toast.LENGTH_LONG).show();

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
                            Toast.LENGTH_LONG).show();
                }
        }

        catch(Exception e)
        {
           Toast.makeText(getActivity().getApplicationContext(), "Value is not entered!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        }
    }
}
