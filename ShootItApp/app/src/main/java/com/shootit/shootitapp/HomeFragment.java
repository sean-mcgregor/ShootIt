package com.shootit.shootitapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private FirebaseUser user;
    private TextView welcomeBanner;
    private DatabaseReference mUser;

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

        updateWelcomeBanner();

        updateWeather();

        return v;
    }

    private void updateWeather() {

//        final TextView textView = (TextView) findViewById(R.id.text);
// ...

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://reqres.in/api/users/2";

// Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new com.android.volley.Response.Listener // CHANGES HERE
                        <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("data");
                            String i = response.getString("email");
                            Log.d("Email", i);
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