package com.shootit.shootitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeMessage = findViewById(R.id.welcomeMessage);

        firebaseAuth= FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            Intent loginActivityLauncher = new Intent(this, LoginActivity.class);
            startActivity(loginActivityLauncher);
        }

        welcomeMessage.setText("Welcome " + user.getEmail().toString());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}