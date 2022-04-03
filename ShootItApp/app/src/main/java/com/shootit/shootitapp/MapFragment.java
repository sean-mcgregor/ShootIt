package com.shootit.shootitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {


    private GoogleMap googleMap;
    private DatabaseReference mDatabase;
    private DatabaseReference mLocations;
    private String databaseURL = "https://shootit-886f2-default-rtdb.europe-west1.firebasedatabase.app/";
    private List<ShootLocation> locationsFromFirebase = new ArrayList<ShootLocation>();

    public MapFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();
        mLocations = FirebaseDatabase.getInstance(databaseURL).getReference().child("locations");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ValueEventListener locationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for (DataSnapshot locationSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    ShootLocation current = new ShootLocation();

                    current.setAuthor(locationSnapshot.child("author").getValue().toString());
                    current.setTitle(locationSnapshot.child("title").getValue().toString());
                    current.setDescription(locationSnapshot.child("description").getValue().toString());
                    current.setLatitude(locationSnapshot.child("latitude").getValue().toString());
                    current.setLongitude(locationSnapshot.child("longitude").getValue().toString());
                    current.setPosition(new LatLng(
                            Double.parseDouble(current.getLatitude()),
                            Double.parseDouble(current.getLongitude())
                    ));

                    locationSnapshot.child("images").getChildren().forEach(child -> {

                        Uri imageURI = Uri.parse(child.getValue().toString());
                        List<Uri> images = current.getImages();
                        images.add(imageURI);
                        current.setImages(images);
                    });

                    Log.d("images for this location", current.getImages().toString());
                    locationsFromFirebase.add(current);
                }

                Log.d("All locations", locationsFromFirebase.toString());
                locationsFromFirebase.forEach(location -> {

                    // Creating a new marker on map
                    googleMap.addMarker(new MarkerOptions()
                            .position(location.getPosition())
                            .title(location.getTitle()))
                            .setTag(location);
                });

                mLocations.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mLocations.addValueEventListener(locationsListener);

        FloatingActionButton addPointButton = v.findViewById(R.id.addPointButton);


        // Click listener for floating add point button
        addPointButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                launchCreatePointActivity();
            }
        });

        return v;
    }


    // Access the googleMap object contained in the fragment, once ready
    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        launchViewPointActivity((ShootLocation) marker.getTag());
    }


    public void launchCreatePointActivity() {

        Intent pointCreatorLauncher = new Intent(getContext(), CreatePointActivity.class);
        startActivity(pointCreatorLauncher);
    }


    public void launchViewPointActivity(ShootLocation location) {

        Intent pointViewerLauncher = new Intent(getContext(), ViewPointActivity.class);
        pointViewerLauncher.putExtra("location", (Parcelable) location);
        startActivity(pointViewerLauncher);
    }
}