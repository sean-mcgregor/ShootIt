package com.shootit.shootitapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

public class PlannedShootsFragment extends Fragment {

    private LinearLayout planListLinearLayout;

    public PlannedShootsFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_planned_shoots, container, false);

        planListLinearLayout = (LinearLayout) v.findViewById(R.id.planListLinearLayout);

        return v;
    }
}