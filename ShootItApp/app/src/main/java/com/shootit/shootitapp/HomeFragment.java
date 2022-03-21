package com.shootit.shootitapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

public class HomeFragment extends Fragment {

    private FirebaseUser user;
    private TextView welcomeBanner;
    private DatabaseReference mUser;
    private JsonObjectRequest jsonRequest;
    private String apiKey = BuildConfig.WEATHER_API_KEY;
    private FusedLocationProviderClient fusedLocationClient;

    public HomeFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeBanner = (TextView) v.findViewById(R.id.welcome);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        checkLocationPermissions();
        getUserLocation();

        updateWelcomeBanner();

        updateWeather();

        return v;
    }

    private void checkLocationPermissions() {



        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                });

        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            Log.d("Permissions", "granted");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

    private void getUserLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d("Location", location.toString());
                        }
                    }
                });

    }

    private void updateWeather() {

        String requestURL = buildRequestURL();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://reqres.in/api/users/2";

        // Request a string response from the provided URL.
        jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new com.android.volley.Response.Listener // CHANGES HERE
                        <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("data");
                            String i = response.getString("email");
                            Log.d("Email", i);
                            Log.d("jsonRequest", "cancelled");
                            jsonRequest.cancel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener () {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Crash", "Volle failed");
                        error.printStackTrace();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private String buildRequestURL() {

        String requestURL = "";
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.openweathermap.org/data/2.5/onecall?");

        return requestURL;
    }

    private void updateWelcomeBanner() {

        mUser.child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    welcomeBanner.setText("Welcome!");
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    welcomeBanner.setText("Welcome " + String.valueOf(task.getResult().getValue()) + "!");
                }
            }
        });;
    }

}