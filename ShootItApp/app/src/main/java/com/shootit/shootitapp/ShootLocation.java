package com.shootit.shootitapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShootLocation {

    private DatabaseReference mDatabase;
    private String databaseURL = "https://shootit-886f2-default-rtdb.europe-west1.firebasedatabase.app/";

    private String title;
    private String description;
    private String latitude;
    private String longitude;
    private LatLng position;
    private String author;
    private List<String> images = new ArrayList<String>();


    public ShootLocation(){

        // Default constructor
    }


    public ShootLocation(String title, String description, LatLng position, String author){

        this.title = title;
        this.description = description;
        this.position = position;
        this.latitude = Double.toString(position.latitude);
        this.longitude = Double.toString(position.longitude);
        this.author = author;
    }

    public boolean pushToDatabase() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.position.latitude);
        sb.append(this.position.longitude);
        String id = sb.toString().replace(".", "dot");
        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();

        // Creating location in firebase
        mDatabase.child("locations").child(id).child("title").setValue(this.title);
        mDatabase.child("locations").child(id).child("description").setValue(this.description);
        mDatabase.child("locations").child(id).child("latitude").setValue(this.latitude);
        mDatabase.child("locations").child(id).child("longitude").setValue(this.longitude);
        mDatabase.child("locations").child(id).child("author").setValue(this.author);

        // Creating a reference to the point under firebase user object
        mDatabase.child("users").child(this.author).child("shootlocations").child(id).setValue(id);
        return true;
    }
}
