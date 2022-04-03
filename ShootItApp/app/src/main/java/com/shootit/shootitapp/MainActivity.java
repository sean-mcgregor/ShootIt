package com.shootit.shootitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Organise bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        firebaseAuth= FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            Intent loginActivityLauncher = new Intent(this, LoginActivity.class);
            startActivity(loginActivityLauncher);
        }
    }

    HomeFragment homeFragment = new HomeFragment();
    MapFragment mapFragment = new MapFragment();
    PlannedShootsFragment plannedShootsFragment = new PlannedShootsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.calender:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, plannedShootsFragment).commit();
                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;

            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
                return true;
        }
        return false;
    }

}