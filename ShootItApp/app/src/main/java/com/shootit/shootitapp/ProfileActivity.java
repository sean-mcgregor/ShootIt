package com.shootit.shootitapp;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private Button logoutButton, deleteAccountButton;

    TextView emailText, usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        auth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.emailText);
        usernameText = findViewById(R.id.usernameText);
        logoutButton = findViewById(R.id.logout_button);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){

                    Log.d("Logged out", "success");
                    // Once signout is complete, reboot application to login screen
                    Intent intent = buildIntent();
                    ProcessPhoenix.triggerRebirth(getApplicationContext(), intent);
                }
                else {

                    Log.d("Logged out", "failure");
                }
            }
        };

        auth.addAuthStateListener(authStateListener);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
            }
        });

        updateUI();
    }

    private Intent buildIntent() {

        Intent intent = new Intent(this, LoginActivity.class);
        return intent;
    }

    
    private void updateUI() {

        StringBuilder emailField = new StringBuilder();
        emailField.append("Email: \n").append(user.getEmail());

        emailText.setText(emailField.toString());

        userRef.child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    StringBuilder usernameField = new StringBuilder();
                    usernameField.append("Username: \n").append(task.getResult().getValue());
                    usernameText.setText(usernameField.toString());
                }
            }
        });
    }

}