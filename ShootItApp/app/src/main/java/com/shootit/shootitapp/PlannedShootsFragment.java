package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PlannedShootsFragment extends Fragment {

    private TextView noPlansText;
    private LinearLayout planListLinearLayout;
    private DatabaseReference mShootPlans, mLocations;
    private FirebaseUser user;

    public PlannedShootsFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_planned_shoots, container, false);

        planListLinearLayout = (LinearLayout) v.findViewById(R.id.planListLinearLayout);
        noPlansText = (TextView) v.findViewById(R.id.noPlansText);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mShootPlans = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("plans");
        mLocations = FirebaseDatabase.getInstance().getReference().child("locations");

        ValueEventListener shootPlansListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot plansList) {
                // Get Post object and use the values to update the UI

                for (DataSnapshot plan : plansList.getChildren()) {

                    noPlansText.setVisibility(View.GONE);
                    String locationUID = plan.child("location").getValue().toString();
                    fetchLocationFromDatabase(locationUID, plan);
                }

                mShootPlans.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mShootPlans.addValueEventListener(shootPlansListener);

        return v;
    }


    private void addPlanToList(DataSnapshot plan, ShootLocation location, DatabaseReference planRef) {

        // TODO: handle the post
        String date = plan.child("date").getValue().toString();
        String time = plan.child("time").getValue().toString();

        PlanCardView fragment = new PlanCardView(location, date, time, planRef);
        getParentFragmentManager().beginTransaction().add(R.id.planListLinearLayout, fragment).commit();
    }

    private void fetchLocationFromDatabase(String locationUID, DataSnapshot plan) {

        ShootLocation location = new ShootLocation();

        mLocations.child(locationUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot locationSnapshot) {

                try {

                    location.setAuthor(locationSnapshot.child("author").getValue().toString());
                    location.setTitle(locationSnapshot.child("title").getValue().toString());
                    location.setDescription(locationSnapshot.child("description").getValue().toString());
                    location.setLatitude(locationSnapshot.child("latitude").getValue().toString());
                    location.setLongitude(locationSnapshot.child("longitude").getValue().toString());
                    location.setPosition(new LatLng(
                            Double.parseDouble(location.getLatitude()),
                            Double.parseDouble(location.getLongitude())
                    ));

                    locationSnapshot.child("images").getChildren().forEach(child -> {

                        Uri imageURI = Uri.parse(child.getValue().toString());
                        List<Uri> images = location.getImages();
                        images.add(imageURI);
                        location.setImages(images);
                    });



                } catch (Exception e) {

                    Log.d("Plans", "Failed to generate one or more plans");
                }

                addPlanToList(plan, location, mShootPlans.child(plan.getKey()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}