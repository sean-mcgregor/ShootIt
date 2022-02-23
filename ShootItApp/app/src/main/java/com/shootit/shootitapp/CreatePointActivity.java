package com.shootit.shootitapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

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

import java.util.ArrayList;
import java.util.List;

public class CreatePointActivity extends AppCompatActivity implements OnMapReadyCallback {

//    BottomNavigationView bottomNavigationView;
//    private FirebaseAuth firebaseAuth;
//    private FirebaseUser user;

    Marker newPoint;

    private EditText titleInput, descriptionInput;
    private Button addPhotoButton, confirmButton;
    private String locationTitle, locationDescription;
    private LatLng locationCoords;
    private List<Uri> photosList = new ArrayList<>();
    private GoogleMap googleMap;

    PhotoFragment photoToAdd;
    LinearLayout imageContainer;
    ActivityResultLauncher<String> mGetContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_point);

        titleInput = (EditText) findViewById(R.id.locationName);
        descriptionInput = (EditText) findViewById(R.id.locationDescription);
        addPhotoButton = (Button) findViewById(R.id.addPhotos);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        imageContainer = (LinearLayout) findViewById(R.id.imageContainer);

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Type of content user can select
                mGetContent.launch("image/*");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Gather information user has provided and create new ShootLocation
                locationTitle = titleInput.getText().toString();
                locationDescription = descriptionInput.getText().toString();
                locationCoords = newPoint.getPosition();

                ShootLocation newLocation = new ShootLocation(locationTitle, locationDescription, locationCoords);
                newLocation.pushToDatabase();

            }
        });


        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {

                        addPhoto(uri);
                    }
                });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(mapFragment!=null){

            mapFragment.getMapAsync(this);
        }
    }


    private void addPhoto(Uri uri) {

        if (uri != null){

            photoToAdd = new PhotoFragment(uri);
            getSupportFragmentManager().beginTransaction().add(R.id.imageContainer, photoToAdd).commit();
        }

    }


    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;
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