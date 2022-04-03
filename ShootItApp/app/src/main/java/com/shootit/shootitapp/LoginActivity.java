package com.shootit.shootitapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Initialize Firebase Auth
    private FirebaseAuth mAuth;

    private String email;
    private String password;

    EditText emailInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        final Button registerButton = findViewById(R.id.register_button);
        final Button loginButton = findViewById(R.id.login_button);
        emailInput = findViewById(R.id.emailText);
        passwordInput = findViewById(R.id.usernameText);

        // If register button clicked
        registerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Launch register activity
                launchRegisterActivity();
            }
        });

        // If login button clicked
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Fetch user input and then attempt login
                updateCredentials();
                login(getEmail(), getPassword());
            }
        });
    }


    // Fetch user input
    public void updateCredentials() {

        setEmail(LoginActivity.this.emailInput.getText().toString());
        setPassword(LoginActivity.this.passwordInput.getText().toString());
    }


    // Launch register activity
    public void launchRegisterActivity() {

        Intent registerActivityLauncher = new Intent(this, RegisterActivity.class);
        startActivity(registerActivityLauncher);
    }


    // Login using user input
    public void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email.replaceAll("\\s+",""), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override

            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    launchMainActivity();
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Launch main activity
    private void launchMainActivity() {

        Intent MainActivityLauncher = new Intent(this, MainActivity.class);
        startActivity(MainActivityLauncher);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}