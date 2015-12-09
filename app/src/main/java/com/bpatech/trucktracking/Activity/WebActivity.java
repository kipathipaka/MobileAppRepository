package com.bpatech.trucktracking.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bpatech.trucktracking.R;

import java.util.List;

public class WebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_web);
        Intent i = getIntent();
        final String action = i.getAction();
//Log.d(CData.LOGTAG, "acción del intent " + action);
        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = i.getData().getPathSegments();
          Toast.makeText(this.getApplicationContext(), " Contenido de la URL interceptada [" + segments.get(3) + "]", Toast.LENGTH_LONG).show();
            //Log.d(CData.LOGTAG, "Contenido nº segmentos URL interceptada " + segments.size());
            if (segments.size() > 0) {
                String tripid = segments.get(segments.size() - 1);
                System.out.println("Contenido de la URL interceptada [" + segments.get(segments.size() - 1) + "]");

// pass tripid to home activity
                Intent homepage = new Intent(this.getApplicationContext(), HomeActivity.class);
                Bundle tripvalue_bundle = new Bundle();
                tripvalue_bundle.putString("vechile_trip_id", tripid); //Your id

                homepage.putExtras(tripvalue_bundle);
               // homepage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homepage);
                // idTapa = Long.parseLong( segments.get(0) );
            }
        }else{
            // idTapa = i.getExtras().getLong(TapaDao.ID, -1);
            System.out.println( "DetalleTapaFA.onCreate idTapa = ");
            Toast.makeText(this.getApplicationContext(),"DetalleTapaFA.onCreate idTapa = ", Toast.LENGTH_LONG).show();
        }

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
   @Override
   public void onBackPressed() {
       super.onBackPressed();
   }

}
