package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class CreatePointActivity extends AppCompatActivity implements OnMapReadyCallback {


    Marker newPoint;
    private FirebaseUser user;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private DatabaseReference mDatabase;
    private String databaseURL = "https://shootit-886f2-default-rtdb.europe-west1.firebasedatabase.app/";

    private EditText titleInput, descriptionInput;
    private Button addPhotoButton, confirmButton, backButton;
    private String locationTitle, locationDescription;
    private LatLng locationCoords, startPosition;
    private List<PhotoFragment> photosList = new ArrayList<PhotoFragment>();
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
        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();

        titleInput = (EditText) findViewById(R.id.locationName);
        descriptionInput = (EditText) findViewById(R.id.locationDescription);
        addPhotoButton = (Button) findViewById(R.id.addPhotos);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        backButton = (Button) findViewById(R.id.back_button);
        imageContainer = (LinearLayout) findViewById(R.id.imageContainer);

        // When add photo button is pressed
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get an image from gallery
                mGetContent.launch("image/*");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Gather information user has provided and create new ShootLocation and remove whitespace
                locationTitle = titleInput.getText().toString().replaceAll("[\\s|\\t|\\r\\n]+", " ");
                locationDescription = descriptionInput.getText().toString().replaceAll("[\\s|\\t|\\r\\n]+", " ");
                locationCoords = newPoint.getPosition();

                if (CheckInputs.isValidPlaceTitle(locationTitle)) {

                    if (CheckInputs.isValidDescription(locationDescription)) {

                        if (CheckInputs.markerHasMoved(startPosition, locationCoords)) {

                            ShootLocation newLocation = new ShootLocation(locationTitle, locationDescription, locationCoords, user.getUid(), photosList);
                            pushToDatabase(newLocation);
                        } else {

                            Toast.makeText(getApplicationContext(), "Please move the marker to your location", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Toast.makeText(getApplicationContext(), "Description too long or short", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Title too long or short", Toast.LENGTH_LONG).show();
                }
            }
        });

        // If back button pressed finish activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // For fetching a user-selected image from gallery
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


    // Add photo to screen
    private void addPhoto(Uri uri) {

        if (uri != null){

            photoToAdd = new PhotoFragment(uri);
            photosList.add(photoToAdd);
            getSupportFragmentManager().beginTransaction().add(R.id.imageContainer, photoToAdd).commit();
        }

    }


    // Push new point and details to firebase
    public boolean pushToDatabase(ShootLocation newLocation) {

        StringBuilder sb = new StringBuilder();
        sb.append(newLocation.getLatitude());
        sb.append(newLocation.getLongitude());
        String id = sb.toString().replace(".", "dot");

        uploadPhotos(newLocation.getImages(), id);

        // Creating location in firebase
        mDatabase.child("locations").child(id).child("author").setValue(newLocation.getAuthor());
        mDatabase.child("locations").child(id).child("title").setValue(newLocation.getTitle());
        mDatabase.child("locations").child(id).child("description").setValue(newLocation.getDescription());
        mDatabase.child("locations").child(id).child("latitude").setValue(newLocation.getLatitude());
        mDatabase.child("locations").child(id).child("longitude").setValue(newLocation.getLongitude());

        // Creating a reference to the point under firebase user object
        mDatabase.child("users").child(newLocation.getAuthor()).child("locations").child(id).setValue(id);
        finish();
        return true;
    }


    // Upload photos to firestore
    public void uploadPhotos(List<Uri> photosList, String locationId) {

        // Gather Uris for images
        photosList.forEach(photo -> {

            StorageReference currentPhotoRef = storageRef.child("images/" + photo.getLastPathSegment());
            System.out.println(photo);

            UploadTask uploadTask = currentPhotoRef.putFile(photo);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return currentPhotoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String url = task.getResult().toString();
                        System.out.println("Image uploaded");
                        mDatabase.child("locations").child(locationId).child("images").child(url.replaceAll("[^a-zA-Z0-9]", "")).setValue(url);
                    }
                }
            });
        });
    }


    // Initialise map fragment
    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        newPoint = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude))
                .title("New Point")
                .draggable(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        startPosition = newPoint.getPosition();

        // Set onclick listener for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                newPoint.setPosition(latLng);
            }
        });
    }
}