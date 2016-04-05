package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.ExceptionHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class DisplayMapFragment extends Fragment
{
    MapView mapView;
    public GoogleMap googleMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.map_layout, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        Bundle maplist = this.getArguments();
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        googleMap=mapView.getMap();
        if(googleMap!=null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            MapsInitializer.initialize(this.getActivity());
            double latitude=maplist.getDouble("latitude");
            double longitude= maplist.getDouble("longitude");
            LatLng LOCATION = new LatLng(latitude,longitude);

            Marker marker = googleMap.addMarker(new MarkerOptions().position(
                    LOCATION).title(""));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION,
                    17));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000,
                    null);

        }

        return view;

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
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
