package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ShootLocation implements Parcelable {

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

    @Override
    public String toString() {
        return "ShootLocation{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", position=" + position +
                ", author='" + author + '\'' +
                ", images=" + images +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeParcelable(this.position, flags);
        dest.writeString(this.author);
        dest.writeTypedList(this.images);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.description = source.readString();
        this.latitude = source.readString();
        this.longitude = source.readString();
        this.position = source.readParcelable(LatLng.class.getClassLoader());
        this.author = source.readString();
        this.images = source.createTypedArrayList(Uri.CREATOR);
    }

    protected ShootLocation(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.author = in.readString();
        this.images = in.createTypedArrayList(Uri.CREATOR);
    }

    public static final Creator<ShootLocation> CREATOR = new Creator<ShootLocation>() {
        @Override
        public ShootLocation createFromParcel(Parcel source) {
            return new ShootLocation(source);
        }

        @Override
        public ShootLocation[] newArray(int size) {
            return new ShootLocation[size];
        }
    };
}
