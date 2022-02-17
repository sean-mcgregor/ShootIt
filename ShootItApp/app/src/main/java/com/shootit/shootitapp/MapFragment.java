package com.shootit.shootitapp;

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


    private Marker newPoint;
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
                Toast.makeText(getActivity(), "Clicked",
                        Toast.LENGTH_SHORT).show();

                toggleVisibility(newPoint);
            }
        });

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return v;
    }

    private void toggleVisibility(Marker newPoint) {

        if(newPoint.isVisible()) {

            newPoint.setVisible(false);
        } else {

            newPoint.setVisible(true);
            newPoint.setPosition(new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude));
        }
    }

    // Access the googleMap object contained in the fragment, once ready
    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;

        // Creating a new marker on map
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        newPoint = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(30,30))
                            .title("New Point")
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


        // Set onclick listener for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Print location clicked via toast
                Toast.makeText(getActivity(), latLng.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}