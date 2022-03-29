package com.shootit.shootitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class PhotoFragment extends Fragment {

    Uri photoUri;
    ImageView imageView;
    boolean deletable = true;
    boolean deleted = false;

    public PhotoFragment(Uri uri, Boolean deletable) {

        this.photoUri = uri;
        this.deletable = deletable;
    }

    public PhotoFragment(Uri uri){
        // require a empty public constructor
        this.photoUri = uri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define view
        View v = inflater.inflate(R.layout.fragment_photo, null);

        // Access image container
        imageView = (ImageView) v.findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load photo from URI into image container
        Glide.with(this).load(photoUri).into(imageView);

        // If fragment is being used for photo upload in create point activity
        if (deletable) {

            // If user clicks photo fragment
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Create alert dialog to confirm deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Remove Image?");
                    builder.setMessage("Are you sure you want to remove this image?");

                    // If user confirms deletion process
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Delete image
                            view.setVisibility(View.GONE);
                            deleted = true;
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
        }

        return v;
    }

}