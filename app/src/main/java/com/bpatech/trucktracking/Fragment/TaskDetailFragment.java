package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ServiceConstants;

/**
 * Created by Anita on 9/14/2015.
 */
public class TaskDetailFragment extends Fragment {
    TextView truck, place, phone, txt_contTitle,customer,customer_name,customer_no;
    Button Startbtn,whatsup;

    boolean startclick;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View view = inflater.inflate(R.layout.taskdetail_layout, container, false);
        txt_contTitle = (TextView) view.findViewById(R.id.txt_contTitle);
        Startbtn = (Button)view.findViewById(R.id.startbtn);
        whatsup=((Button)view.findViewById(R.id.whatsup));
       // whatsup.setOnClickListener(new WhatsupButtonListener());
        truck = (TextView) view.findViewById(R.id.truckvalu);
        place = (TextView) view.findViewById(R.id.tovalue);
        phone = (TextView) view.findViewById(R.id.phoneno);
        customer = (TextView) view.findViewById(R.id.customerval);
        customer_name = (TextView) view.findViewById(R.id.customenameval);
        customer_no = (TextView) view.findViewById(R.id.customenoval);

       Bundle taskdetail = this.getArguments();

        // String place=taskdetail.getString(ServiceConstants.CUURENT_TRIP_Place);
        place.setText(taskdetail.getString(ServiceConstants.CUURENT_TRIP_PLACE));
        truck.setText(taskdetail.getString(ServiceConstants.CUURENT_TRIP_TRUCK));
        phone.setText(taskdetail.getString(ServiceConstants.CUURENT_TRIP_PHONE));
        customer.setText(taskdetail.getString(ServiceConstants.ADD_TRIP_CUSTOMER));
        customer_name.setText(taskdetail.getString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME));
        customer_no.setText(taskdetail.getString(ServiceConstants.ADD_TRIP_CUSTOMER_NO));

        startclick=taskdetail.getBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE);
        if(startclick==true){
            txt_contTitle.setText(ServiceConstants.TASK_DETAIL_ENDTITLE);
            Startbtn.setText("End Tracking");
            Startbtn.setBackgroundColor(Color.RED);
        }else{
            txt_contTitle.setText(ServiceConstants.TASK_DETAIL_TITLE);
            Startbtn.setText("Start Tracking");
            Startbtn.setBackgroundColor(Color.GREEN);
        }

         Startbtn.setOnClickListener(new StartTrackButtonListener());
        // Toast.makeText(getActivity().getApplicationContext(), place, Toast.LENGTH_LONG).show();
        return view;
    }

    private class StartTrackButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(startclick==true){

                CurrentTripFragment currenttripfrag=new CurrentTripFragment();
                FragmentManager fragmentmanager = getFragmentManager();
                FragmentTransaction fragmenttransaction = fragmentmanager
                        .beginTransaction();
                fragmenttransaction.replace(R.id.viewers, currenttripfrag);

                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }else {

                TaskDetailFragment taskdetailfrag = new TaskDetailFragment();
                Bundle taskdetails=new Bundle();
                taskdetails.putString(ServiceConstants.CUURENT_TRIP_PLACE, place.getText().toString());
                taskdetails.putString(ServiceConstants.CUURENT_TRIP_TRUCK, truck.getText().toString());
                taskdetails.putString(ServiceConstants.CUURENT_TRIP_PHONE, phone.getText().toString());
                taskdetails.putString(ServiceConstants.ADD_TRIP_CUSTOMER, customer.getText().toString());
                taskdetails.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NAME, customer_name.getText().toString());
                taskdetails.putString(ServiceConstants.ADD_TRIP_CUSTOMER_NO, customer_no.getText().toString());
                taskdetails.putBoolean(ServiceConstants.TASK_DETAIL_ENDPAGE, true);
                taskdetailfrag.setArguments(taskdetails);
                FragmentManager fragmentmanager = getFragmentManager();
                FragmentTransaction fragmenttransaction = fragmentmanager
                        .beginTransaction();
                fragmenttransaction.replace(R.id.viewers, taskdetailfrag);

                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }

        }
    }

}
