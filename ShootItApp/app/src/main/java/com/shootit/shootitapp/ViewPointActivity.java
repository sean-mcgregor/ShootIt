package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewPointActivity extends AppCompatActivity implements OnMapReadyCallback {


    Marker newPoint;
    private TextView titleView, descriptionView;
    private Button backButton;
    private GoogleMap googleMap;

    ShootLocation location;

    PhotoFragment photoToAdd;
    LinearLayout imageContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point);

        //in SecondActivity
        if(getIntent().getExtras() != null) {
            location = getIntent().getParcelableExtra("location");
        }

        titleView = (TextView) findViewById(R.id.locationName);
        descriptionView = (TextView) findViewById(R.id.locationDescription);
        backButton = (Button) findViewById(R.id.back_button);
        imageContainer = (LinearLayout) findViewById(R.id.imageContainer);

        titleView.setText(location.getTitle());
        descriptionView.setText(location.getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        location.getImages().forEach(uri -> {

            addPhoto(uri);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(mapFragment!=null){

            mapFragment.getMapAsync(this);
        }
    }


    private void addPhoto(Uri uri) {

        if (uri != null){

            photoToAdd = new PhotoFragment(uri, false);
            getSupportFragmentManager().beginTransaction().add(R.id.imageContainer, photoToAdd).commit();
        }

    }


    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));

        newPoint = googleMap.addMarker(new MarkerOptions()
                .position(location.getPosition()));
    }
}