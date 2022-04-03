package com.shootit.shootitapp;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef, takenUsernamesRef;
    private Button logoutButton, deleteAccountButton, editUsernameButton, editEmailButton;

    TextView emailText, usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        takenUsernamesRef = FirebaseDatabase.getInstance().getReference().child("takenusernames");
        auth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.emailText);
        usernameText = findViewById(R.id.usernameText);
        logoutButton = findViewById(R.id.logout_button);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        editEmailButton = findViewById(R.id.editEmailButton);
        editUsernameButton = findViewById(R.id.editUsernameButton);

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

        editUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeUsernamePrompt();
            }
        });

        editEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeEmailPrompt();
            }
        });

        // Populate screen with user-specific content
        updateUI();
    }

    private void changeEmailPrompt() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your password to re-authenticate yourself");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Password");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String password = input.getText().toString();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), password);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    getNewEmail();
                                    Log.d(TAG, "User re-authenticated.");
                                } else {

                                    Log.d(TAG, "User authentication failure");
                                    Toast.makeText(getApplicationContext(), "Your re-authentication has failed. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        // Configure cancel button for dialog window
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Display dialog window
        builder.show();
    }

    private void getNewEmail() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your new email address");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("New Email");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newEmail = input.getText().toString();

                // Attempt to update user email
                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG, "User email address updated.");
                            userRef.child("email").setValue(newEmail);
                            Toast.makeText(getApplicationContext(), "Your email has been updated.", Toast.LENGTH_LONG).show();
                            updateUI();
                        } else {

                            Toast.makeText(getApplicationContext(), "Email update failed. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        // Configure cancel button for dialog window
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Display dialog window
        builder.show();
    }


    // Dialog window to facilitate user changing their username
    private void changeUsernamePrompt() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter new username");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newUsername = input.getText().toString();

                // Get list of taken usernames from firebase
                takenUsernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot usernamesSnapshot) {

                        // Assume username available
                        boolean usernameAvailable = true;

                        // Iterate through and check all usernames
                        for (DataSnapshot userName : usernamesSnapshot.getChildren()) {

                            String usernameFromFirebase = userName.getValue().toString();

                            // If input username is already in use mark it as unavailable
                            if (newUsername.equals(usernameFromFirebase)) {

                                usernameAvailable = false;
                            }
                        }

                        // If username is available
                        if (usernameAvailable) {

                            // Update firebase values
                            userRef.child("username").setValue(newUsername);
                            takenUsernamesRef.child(user.getUid()).setValue(newUsername);

                            // Confirm that name is changed and update UI
                            Toast.makeText(getApplicationContext(), "Your username has been changed!", Toast.LENGTH_LONG).show();
                            updateUI();
                        } else {

                            // Otherwise provide user with feedback
                            Toast.makeText(getApplicationContext(), "This username is unavailable!", Toast.LENGTH_LONG).show();
                        }
                    }

                    // Handle firebase snapshot errors
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // Provide user feedback
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                        Log.d("Username", "snapshot cancelled");
                    }
                });
            }
        });

        // Configure cancel button for dialog window
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Display dialog window
        builder.show();
    }


    // Build intent to restart application from afresh when user logs out
    private Intent buildIntent() {

        Intent intent = new Intent(this, LoginActivity.class);
        return intent;
    }


    // Populate the screen with user-specific values
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