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
    private List<String> images = new ArrayList<String>();


    public ShootLocation(){

        // Default constructor
    }


    public ShootLocation(String title, String description, LatLng position){

        this.title = title;
        this.description = description;
        this.position = position;
    }

    public boolean pushToDatabase() {

//        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();
//        mDatabase.child("locations").child(userID).child("username").setValue(username);
        StringBuilder sb = new StringBuilder();
        sb.append(this.position.latitude);
        sb.append(this.position.longitude);
        System.out.println(sb.toString());
        return true;
    }
}
