package com.bpatech.trucktracking.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.Util.SessionManager;

import java.io.File;

import timber.log.Timber;

/**
 * Created by Anita on 11/3/2015.
 */

public class CrashActivity extends Activity {

    SessionManager sm;
ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_activity);
        sm = new SessionManager(this.getApplicationContext());
        final TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText(R.string.AppCrashMessage);
        progressBar=(ProgressBar)findViewById(R.id.crashprogressbar);
        progressBar.setProgress(10);
        progressBar.setMax(100);
        progressBar.setVisibility(View.INVISIBLE);
        Timber.tag("Crash ACtivity Call");
        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                progressBar.setVisibility(View.VISIBLE);
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vm/" + ".errorTrace.txt";
                sendErrorMail(CrashActivity.this, filePath);
                //  finish();
                progressBar.setVisibility(View.INVISIBLE);
            }

        });


        findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sm.logoutUser();


            }
        });

    }

    private void sendErrorMail(Context _context, String filePath) {
        Timber.tag("Send Error Mail");
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error Description";
        String body = "Unfortunately Vehilce Tracking application crashed.\nPlease find the attached error log.";
        sendIntent.setType("plain/text");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ServiceConstants.APP_CRASH_ADMIN_MAILID});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        sendIntent.setType("message/rfc822");
        _context.startActivity(Intent.createChooser(sendIntent, ServiceConstants.PREF_NAME));

    }
}