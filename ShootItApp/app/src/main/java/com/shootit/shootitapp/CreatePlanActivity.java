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

        // Get location for plan
        if(getIntent().getExtras() != null) {

            location = getIntent().getParcelableExtra("location");

            locationNameTextView.setText(location.getTitle());

            photoFragment = new PhotoFragment(location.getImages().get(0), false);
            getSupportFragmentManager().beginTransaction().add(R.id.cardView, photoFragment).commit();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dateSelected && timeSelected) {

                    pushPlanToDatabase();
                }

                finish();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timePickerDialog.show();
            }
        });
    }

    private void pushPlanToDatabase() {

        dateTextView.getText();
        timeTextView.getText();

        String randomID = UUID.randomUUID().toString();

        StringBuilder locationDatabaseID = new StringBuilder();
        locationDatabaseID.append(location.getLatitude()).append(location.getLongitude());

        mDatabase   .child("users")
                    .child(user.getUid())
                    .child("plans")
                    .child(randomID)
                    .child("location")
                    .setValue(locationDatabaseID.toString().replace(".", "dot"));

        mDatabase   .child("users")
                    .child(user.getUid())
                    .child("plans")
                    .child(randomID)
                    .child("date")
                    .setValue(dateTextView.getText());

        mDatabase   .child("users")
                    .child(user.getUid())
                    .child("plans")
                    .child(randomID)
                    .child("time")
                    .setValue(timeTextView.getText());
    }

    private void initTimePicker() {

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
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


    private void initDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
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
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
    }
}