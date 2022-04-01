package com.shootit.shootitapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.UUID;

public class CreatePlanActivity extends AppCompatActivity{

    private TextView locationNameTextView, dateTextView, timeTextView;
    private PhotoFragment photoFragment;
    private Button backButton, confirmButton, dateButton, timeButton;
    private ShootLocation location;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private boolean dateSelected, timeSelected;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        // Configure date and time pickers
        initDatePicker();
        initTimePicker();

        locationNameTextView = (TextView) findViewById(R.id.locationNameTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);

        backButton = (Button) findViewById(R.id.back_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get bundled location for plan
        if(getIntent().getExtras() != null) {

            location = getIntent().getParcelableExtra("location");

            locationNameTextView.setText(location.getTitle());

            // Add locations first photo to plan preview
            photoFragment = new PhotoFragment(location.getImages().get(0), false);

            // Add photofragment to cardview
            getSupportFragmentManager().beginTransaction().add(R.id.cardView, photoFragment).commit();
        }


        // When back button is clicked close fragment
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // End Activity
                finish();
            }
        });


        // When confirm button is clicked
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Check if user has indicated both a date and time
                if (dateSelected && timeSelected) {

                    // Save plan on firebase
                    pushPlanToDatabase();
                }

                // End activity
                finish();
            }
        });


        // When date selector button is clicked
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }
        });


        // When time selector button is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                timePickerDialog.show();
            }
        });
    }


    // Saving plan details on Firebase
    private void pushPlanToDatabase() {

        // Get date and time selected by user
        dateTextView.getText();
        timeTextView.getText();

        // Generate random ID to save plan under
        String randomID = UUID.randomUUID().toString();

        // Foreign key for location object
        StringBuilder locationDatabaseID = new StringBuilder();
        locationDatabaseID.append(location.getLatitude()).append(location.getLongitude());

        // Push location key
        mDatabase   .child("users")
                    .child(user.getUid())
                    .child("plans")
                    .child(randomID)
                    .child("location")
                    .setValue(locationDatabaseID.toString().replace(".", "dot"));

        // Push date
        mDatabase   .child("users")
                    .child(user.getUid())
                    .child("plans")
                    .child(randomID)
                    .child("date")
                    .setValue(dateTextView.getText());

        // Push time
        mDatabase   .child("users")
                    .child(user.getUid())
                    .child("plans")
                    .child(randomID)
                    .child("time")
                    .setValue(timeTextView.getText());
    }


    // Configure time picker dialog
    private void initTimePicker() {

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

            // Code to execute when time selected
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                StringBuilder sb = new StringBuilder();

                if (hour < 10) {

                    sb.append(0);
                }

                sb.append(hour).append(":");

                if (minute < 10) {

                    sb.append(0);
                }
                
                sb.append(minute);
                timeTextView.setText(sb.toString());
                timeSelected = true;
            }
        };

        timePickerDialog = new TimePickerDialog(this, timeSetListener, 12, 0, true);
    }


    // Configure date picker dialog
    private void initDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            // Code to execute when date selected
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                StringBuilder sb = new StringBuilder();
                sb.append(day).append("/");
                sb.append(month+1).append("/");
                sb.append(year);

                dateTextView.setText(sb.toString());
                dateSelected = true;
            }
        };

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        // Prevent users from selecting a date in the past
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
    }
}