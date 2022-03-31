package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanCardView extends Fragment {

    private ShootLocation location;
    private PhotoFragment photoContainer;
    private String date, time;
    private TextView locationTextView, dateTextView, timeTextView;

    public PlanCardView(ShootLocation location, String date, String time){

        this.location = location;
        this.date = date;
        this.time = time;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define view
        View v = inflater.inflate(R.layout.cardview_plan, null);

        photoContainer = new PhotoFragment(location.getImages().get(0), false);
        locationTextView = (TextView) v.findViewById(R.id.titleTextView);
        timeTextView = (TextView) v.findViewById(R.id.timeTextView);
        dateTextView = (TextView) v.findViewById(R.id.dateTextView);

        if (location.getTitle() != null) {

            locationTextView.setText(location.getTitle());
        } else {

            locationTextView.setText("Location Data Null");
        }

        dateTextView.setText(date);
        timeTextView.setText(time);
        getChildFragmentManager().beginTransaction().add(R.id.locationImageView, this.photoContainer).commit();

        return v;
    }

}