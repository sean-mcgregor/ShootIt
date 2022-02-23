package com.shootit.shootitapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ShootLocation {

    private String title;
    private String description;
    private String latitude;
    private String longitude;
    private LatLng position;
    private List<String> images = new ArrayList<String>();


    public ShootLocation(){

        // Empty constructor
    }

    public ShootLocation(String title, String description, LatLng position){

        this.title = title;
        this.description = description;
        this.position = position;
    }
}
