package com.shootit.shootitapp;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

public class CreatePlanActivity extends AppCompatActivity{


    private TextView locationNameTextView;
    private ImageView locationImageView;
    private PhotoFragment photoFragment;
    private CardView cardView;
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
        cardView = (CardView) findViewById(R.id.cardView);

        locationNameTextView.setText(location.getTitle());
        photoFragment = new PhotoFragment(location.getImages().get(0), false);
        getSupportFragmentManager().beginTransaction().add(R.id.cardView, photoFragment).commit();

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