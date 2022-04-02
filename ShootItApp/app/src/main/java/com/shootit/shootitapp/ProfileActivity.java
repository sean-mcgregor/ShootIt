package com.shootit.shootitapp;

import android.os.Bundle;
import android.util.Log;
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

public class ProfileActivity extends AppCompatActivity {

    // Initialize Firebase Auth
    private FirebaseUser user;
    private DatabaseReference userRef;

    TextView emailText, usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        emailText = findViewById(R.id.emailText);
        usernameText = findViewById(R.id.usernameText);

        updateUI();
    }

    private void updateUI() {

        StringBuilder emailField = new StringBuilder();
        emailField.append("Email: ").append(user.getEmail());

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
                    usernameField.append("Username: ").append(task.getResult().getValue());
                    usernameText.setText(usernameField.toString());
                }
            }
        });
    }

}