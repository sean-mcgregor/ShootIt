package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ViewPointActivity extends AppCompatActivity implements OnMapReadyCallback {


    Marker newPoint;
    private FirebaseUser user;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef, imagesRef;

    private DatabaseReference mDatabase;
    private String databaseURL = "https://shootit-886f2-default-rtdb.europe-west1.firebasedatabase.app/";

    private TextView titleView, descriptionView;
    private Button backButton;
    private String locationTitle, locationDescription;
    private LatLng locationCoords;
    private List<PhotoFragment> photosList = new ArrayList<PhotoFragment>();
    private GoogleMap googleMap;

    ShootLocation location;

    PhotoFragment photoToAdd;
    LinearLayout imageContainer;
    ActivityResultLauncher<String> mGetContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point);

        //in SecondActivity
        if(getIntent().getExtras() != null) {
            location = getIntent().getParcelableExtra("location");
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();

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
            photosList.add(photoToAdd);
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
                .draggable(true));
    }
}