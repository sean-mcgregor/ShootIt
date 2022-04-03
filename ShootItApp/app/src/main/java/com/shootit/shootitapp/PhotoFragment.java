package com.shootit.shootitapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class PhotoFragment extends Fragment {

    Uri photoUri;
    ImageView imageView;
    boolean deletable = true;
    boolean deleted = false;

    public PhotoFragment() {

    }

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
        } else {

            // If user clicks photo fragment
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showImagePopup();
                }
            });
        }

        return v;
    }


    // Function to instantiate and display dialog builder for fullscreen photo viewing
    public void showImagePopup() {

        // Crate and configure dialog builder
        Dialog builder = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_DayNight);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;


        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(color));

        // Destroy dialog if dismissed
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                builder.cancel();
            }
        });

        // Create imageview and load photo with glide
        ImageView imageView = new ImageView(getContext());
        Glide.with(getContext()).load(photoUri).into(imageView);

        // Create and configure cancel button
        Button cancelButton = new Button(getContext());
        cancelButton.setBackgroundColor(getResources().getColor(R.color.BACK_BUTTON));
        cancelButton.setText("Back");

        // If cancel button clicked then destroy dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.cancel();
            }
        });

        // Add button and imageview to builder
        builder.addContentView(cancelButton, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Display builder
        builder.show();
    }

}