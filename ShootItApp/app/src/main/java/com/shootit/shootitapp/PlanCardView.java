package com.shootit.shootitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanCardView extends Fragment {

    private ShootLocation location;
    private PhotoFragment photoContainer;
    private String date, time;
    private TextView locationTextView, dateTextView, timeTextView;
    private DatabaseReference planRef;

    public PlanCardView(ShootLocation location, String date, String time, DatabaseReference planRef){

        this.location = location;
        this.date = date;
        this.time = time;
        this.planRef = planRef;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define view
        View v = inflater.inflate(R.layout.cardview_plan, null);

        locationTextView = (TextView) v.findViewById(R.id.titleTextView);
        timeTextView = (TextView) v.findViewById(R.id.timeTextView);
        dateTextView = (TextView) v.findViewById(R.id.dateTextView);

        if (location.getTitle() != null) {

            locationTextView.setText(location.getTitle());
        } else {

            locationTextView.setText("Location Deleted or Inaccessible");
        }

        try {

            photoContainer = new PhotoFragment(location.getImages().get(0), false);
        } catch (Exception e){

            photoContainer = new PhotoFragment();
        }

        dateTextView.setText(date);
        timeTextView.setText(time);
        getChildFragmentManager().beginTransaction().add(R.id.locationImageView, this.photoContainer).commit();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create alert dialog to confirm deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete Plan?");
                builder.setMessage("Are you sure you want to delete this plan?");

                // If user confirms deletion process
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Delete image
                        view.setVisibility(View.GONE);
                        planRef.removeValue();
                    }
                });

                // If user cancels deletion process
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Pass
                    }
                });

                // Display prompt to user
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return v;
    }
}