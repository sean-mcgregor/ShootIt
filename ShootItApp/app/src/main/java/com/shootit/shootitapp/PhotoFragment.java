package com.shootit.shootitapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class PhotoFragment extends Fragment {

    Uri photoUri;
    ImageView imageView;

    public PhotoFragment(Uri uri){
        // require a empty public constructor
        this.photoUri = uri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photo, null);

        imageView = (ImageView) v.findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setImageURI(photoUri);

        return v;
    }
}