package com.shootit.shootitapp;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePlanActivity extends AppCompatActivity{


    private TextView locationNameTextView;
    private ImageView locationImageView;
    private Button backButton, confirmButton;

    private ShootLocation location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        // Get location for plan
        if(getIntent().getExtras() != null) {
            location = getIntent().getParcelableExtra("location");
        }

        locationNameTextView = (TextView) findViewById(R.id.locationNameTextView);
        locationImageView = (ImageView) findViewById(R.id.locationImageView);
        backButton = (Button) findViewById(R.id.back_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        locationNameTextView.setText(location.getTitle());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }
}