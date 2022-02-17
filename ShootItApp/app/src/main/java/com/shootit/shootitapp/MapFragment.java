package com.shootit.shootitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap googleMap;

    public MapFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        FloatingActionButton addPointButton = v.findViewById(R.id.addPointButton);

        // Click listener for floating add point button
        addPointButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                launchCreatePointActivity();
            }
        });

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return v;
    }


    // Access the googleMap object contained in the fragment, once ready
    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        // Creating a new marker on map
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

    }



    public void launchCreatePointActivity() {

        Intent pointCreatorLauncher = new Intent(getContext(), CreatePointActivity.class);
        startActivity(pointCreatorLauncher);
    }
}