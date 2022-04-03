package com.shootit.shootitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationCardView extends Fragment {

    private ShootLocation location;
    private PhotoFragment photoContainer;
    private TextView locationTextView;
    private DatabaseReference locationRef, userRef;
    private String locationID;

    public LocationCardView(ShootLocation location, DatabaseReference locationRef, String locationID){

        this.location = location;
        this.locationRef = locationRef;
        this.locationID = locationID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define view
        View v = inflater.inflate(R.layout.cardview_location, null);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        locationTextView = (TextView) v.findViewById(R.id.titleTextView);

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

        getChildFragmentManager().beginTransaction().add(R.id.locationImageView, this.photoContainer).commit();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create alert dialog to confirm deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete Location?");
                builder.setMessage("Are you sure you want to delete this location?");

                // If user confirms deletion process
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Delete image
                        view.setVisibility(View.GONE);
                        locationRef.removeValue();
                        userRef.child("locations").child(locationID).removeValue();
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