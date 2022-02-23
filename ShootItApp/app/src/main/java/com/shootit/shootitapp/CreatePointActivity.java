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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CreatePointActivity extends AppCompatActivity implements OnMapReadyCallback {


    Marker newPoint;
    private FirebaseUser user;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef, imagesRef;

    private EditText titleInput, descriptionInput;
    private Button addPhotoButton, confirmButton;
    private String locationTitle, locationDescription;
    private LatLng locationCoords;
    private List<PhotoFragment> photosList = new ArrayList<>();
    private GoogleMap googleMap;

    PhotoFragment photoToAdd;
    LinearLayout imageContainer;
    ActivityResultLauncher<String> mGetContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_point);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = storage.getReference();

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

                uploadPhotos();


//                ShootLocation newLocation = new ShootLocation(locationTitle, locationDescription, locationCoords, user.getUid());
//                newLocation.pushToDatabase();
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


    private void uploadPhotos() {

        // Gather Uris for images
        photosList.forEach(photoFragment -> {
            if (photoFragment.deleted == false) {

                StorageReference currentPhotoRef = storageRef.child("images/"+photoFragment.photoUri.getLastPathSegment());
                System.out.println(photoFragment.photoUri);

                UploadTask uploadTask = currentPhotoRef.putFile(photoFragment.photoUri);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(CreatePointActivity.this, "Image upload failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Toast.makeText(CreatePointActivity.this, "Image upload success.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
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