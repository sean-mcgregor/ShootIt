package com.shootit.shootitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreatePointActivity extends AppCompatActivity implements OnMapReadyCallback {

    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    Marker newPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_point);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(mapFragment!=null){

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        newPoint = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude))
                .title("New Point")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Set onclick listener for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                newPoint.setPosition(latLng);
            }
        });
    }
}