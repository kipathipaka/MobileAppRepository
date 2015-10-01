package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bpatech.trucktracking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Yugandhar on 9/30/2015.
 */
public class DisplayMapFragment extends Fragment
{
    GoogleMap googleMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.map_layout, container, false);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();
        if(googleMap!=null)
        {
            double latitude=13.0827;
            double longitude= 80.2707;
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
}
