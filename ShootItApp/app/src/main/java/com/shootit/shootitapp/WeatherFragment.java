package com.shootit.shootitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class WeatherFragment extends Fragment {

    Uri photoUri;
    ImageView iconContainer;
    TextView timeTextView;
    String timeStamp;

    public WeatherFragment(Uri uri, String timeStamp){

        this.photoUri = uri;
        this.timeStamp = timeStamp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define view
        View v = inflater.inflate(R.layout.fragment_weather, null);

        // Access image container
        iconContainer = (ImageView) v.findViewById(R.id.iconContainer);
        timeTextView = (TextView) v.findViewById(R.id.timeStamp);
        timeTextView.setText(timeStamp);
//        iconContainer.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load photo from URI into image container
        Glide.with(this).load(photoUri).into(iconContainer);

        return v;
    }

}