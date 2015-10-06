package com.bpatech.trucktracking.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
        Bundle maplist = this.getArguments();
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapview)).getMap();
        if(googleMap!=null)
        {
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
    public void onDestroyView() {
        // TODO Auto-generated method stub

        super.onDestroyView();

        Fragment fragment = (getFragmentManager()
                .findFragmentById(R.id.mapview));
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
    }

}
