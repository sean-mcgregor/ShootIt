package com.shootit.shootitapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap googleMap;
    private DatabaseReference mDatabase;
    private DatabaseReference mLocations;
    private String databaseURL = "https://shootit-886f2-default-rtdb.europe-west1.firebasedatabase.app/";

    public MapFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();
        mLocations = FirebaseDatabase.getInstance(databaseURL).getReference().child("locations");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        FloatingActionButton addPointButton = v.findViewById(R.id.addPointButton);

        ValueEventListener locationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap<String, ShootLocation> allLocations = (HashMap<String, ShootLocation>) dataSnapshot.getValue();
                Log.d("Recieved from firebase", allLocations.toString());
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mLocations.addValueEventListener(locationsListener);

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