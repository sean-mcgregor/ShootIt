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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String databaseURL = "https://shootit-886f2-default-rtdb.europe-west1.firebasedatabase.app/";

    private String username;
    private String email;
    private String password;
    private String password2;

    EditText usernameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText password2Input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(databaseURL).getReference();

        final Button registerButton = findViewById(R.id.register_button);
        final Button loginButton = findViewById(R.id.login_button);

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailText);
        passwordInput = findViewById(R.id.usernameText);
        password2Input = findViewById(R.id.password2Input);

        registerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Code here executes on main thread after user presses button
                updateCredentials();

                if (CheckInputs.isValidUsername(getUsername())) {

                    if( passwordsMatch(getPassword(), getPassword2())){

                        registerUser(getUsername(), getEmail(), getPassword());
                    } else {

                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Username is not valid", Toast.LENGTH_LONG).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Code here executes on main thread after user presses button
                launchLoginActivity();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            System.out.println("User logged in");
        }
    }


    private void updateCredentials() {

        setUsername(RegisterActivity.this.usernameInput.getText().toString());
        setEmail(RegisterActivity.this.emailInput.getText().toString());
        setPassword(RegisterActivity.this.passwordInput.getText().toString());
        setPassword2(RegisterActivity.this.password2Input.getText().toString());
    }

    private boolean passwordsMatch (String password, String password2) {

        if( password.equals(password2)) {

            return true;
        }
        else {

            Toast.makeText(RegisterActivity.this, "Passwords do not match!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private void registerUser(String username, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();

                    mDatabase.child("users").child(userID).child("username").setValue(username);
                    mDatabase.child("users").child(userID).child("email").setValue(email);
                    mDatabase.child("takenusernames").child(userID).setValue(username);

                    launchMainActivity();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void launchLoginActivity() {

        Intent loginActivityLauncher = new Intent(this, LoginActivity.class);
        startActivity(loginActivityLauncher);
    }


    private void launchMainActivity() {

        Intent mainActivityLauncher = new Intent(this, MainActivity.class);
        startActivity(mainActivityLauncher);
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPassword2() { return password2; }

    public void setPassword2(String password2) { this.password2 = password2; }
}