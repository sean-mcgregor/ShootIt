package com.shootit.shootitapp;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ShootLocation {

    private String title;
    private String description;
    private String latitude;
    private String longitude;
    private LatLng position;
    private String author;
    private List<Uri> images = new ArrayList<Uri>();


    public ShootLocation(){

        // Default constructor
    }


    public ShootLocation(String title, String description, LatLng position, String author, List<PhotoFragment> photosList){

        this.setTitle(title);
        this.setDescription(description);
        this.setPosition(position);
        this.setLatitude(Double.toString(position.latitude));
        this.setLongitude(Double.toString(position.longitude));
        this.setAuthor(author);

        photosList.forEach(photoFragment -> {

            if (photoFragment.deleted == false) {

                this.getImages().add(photoFragment.photoUri);
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Uri> getImages() {
        return images;
    }

    public void setImages(List<Uri> images) {
        this.images = images;
    }
}
