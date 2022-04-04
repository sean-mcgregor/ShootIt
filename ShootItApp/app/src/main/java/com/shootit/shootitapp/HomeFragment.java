package com.shootit.shootitapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private FirebaseUser user;
    private TextView welcomeBanner;
    private TextView weatherText;
    private ImageView weatherImage;
    private ImageButton profileButton;
    private TextView sunText;
    private LinearLayout weatherContainer;
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
        weatherText = (TextView) v.findViewById(R.id.weatherTextView);
        sunText = (TextView) v.findViewById(R.id.sunRiseSetTextView);
        weatherImage = (ImageView) v.findViewById(R.id.weatherImageView);
        weatherContainer = (LinearLayout) v.findViewById(R.id.weatherContainer);
        profileButton = (ImageButton) v.findViewById(R.id.profileButton);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        // Launch profile activity
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchProfileActivity();
            }
        });

        // Prompt user for location access
        handleLocationPermissions();

        return v;
    }


    // Launch profile activity
    private void launchProfileActivity() {

        Intent ProfileActivityLauncher = new Intent(getContext(), ProfileActivity.class);
        startActivity(ProfileActivityLauncher);
    }


    // Request location access from user
    private void handleLocationPermissions() {

        // Configure request permission launcher
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {

                        // If user grants permission then populate UI with content
                        buildUI();
                    } else {

                        // Else explain that ShootIt relies heavily on location
                        Toast.makeText(getContext(), "Many features unavailable without permission being granted.", Toast.LENGTH_LONG).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // If permission already granted, proceed
            Log.d("Permissions", "granted");
            buildUI();
        } else {

            // Else request permission using the launcher previously configured
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }


    // Populate UI with data
    private void buildUI() {

        getUserLocation();
        updateWelcomeBanner();
    }


    // Gets user current location, for weather forecast
    @SuppressLint("MissingPermission")
    private void getUserLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            Log.d("Location", location.toString());
                            updateWeather(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }


    // Uses OpenWeatherMap api to get weather forecast and solar pattern
    private void updateWeather(double latitude, double longitude) {

        String requestURL = buildRequestURL(latitude, longitude);
        Log.d("url", requestURL);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL
        jsonRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new com.android.volley.Response.Listener // CHANGES HERE
                        <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // Access current and daily forecasts
                            JSONObject current = response.getJSONObject("current");
                            String sunriseTime = current.get("sunrise").toString();
                            String sunsetTime = current.get("sunset").toString();
                            JSONArray daily = response.getJSONArray("daily");

                            addCurrentWeather(current);
                            addDailyWeather(daily);
                            addSunTimes(sunriseTime, sunsetTime);

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
                        Toast.makeText(getContext(),"Connection issues. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }


    // Update solar pattern on UI
    private void addSunTimes(String sunriseTime, String sunsetTime) {

        StringBuilder sb = new StringBuilder();
        sb.append("Sunrise is at ");
        sb.append(formatTime(sunriseTime));
        sb.append(" and sunset is at ");
        sb.append(formatTime(sunsetTime));

        sunText.setText(sb.toString());
    }


    // Formats date from epoch, as provided by api, to human readable
    private String formatTime(String unformatted) {

        Date date = new Date(Long.parseLong(unformatted) * 1000);

        SimpleDateFormat DateFor = new SimpleDateFormat("h:mm a");
        String formatted= DateFor.format(date);

        return formatted;
    }


    // Populate UI with week-long forecast
    private void addDailyWeather(JSONArray daily) throws JSONException {

        // Loop through array of days
        for (int i = 1; i < daily.length(); i++) {

            // store each object in JSONObject
            JSONObject day = daily.getJSONObject(i);
            JSONArray weatherArray = day.getJSONArray("weather");
            JSONObject dayWeather = (JSONObject) weatherArray.get(0);
            String dayIcon = dayWeather.getString("icon");

            // get field value from JSONObject using get() method
            Log.d("DT", day.get("dt").toString());

            WeatherFragment dayObject = new WeatherFragment(getIconUri(dayIcon), day.get("dt").toString());
            getParentFragmentManager().beginTransaction().add(R.id.weatherContainer, dayObject).commit();
        }
    }


    // Update UI with current weather
    private void addCurrentWeather(JSONObject current) throws JSONException {

        JSONArray weatherSnapshot = current.getJSONArray("weather");
        JSONObject weather = (JSONObject) weatherSnapshot.get(0);

        String description = weather.getString("description");
        String weatherIconID = weather.getString("icon");

        weatherText.setText(new StringBuilder().append("Current weather: ").append(description).toString());
        Glide.with(getContext()).load(getIconUri(weatherIconID).toString()).into(weatherImage);
    }


    // Get resource for weather image
    private Uri getIconUri(String dayIcon) {

        StringBuilder iconAddress = new StringBuilder();
        iconAddress.append("https://openweathermap.org/img/wn/").append(dayIcon).append("@2x.png");
        return Uri.parse(iconAddress.toString());
    }


    // Build URL for weather api request
    private String buildRequestURL(double latitude, double longitude) {

        StringBuilder sb = new StringBuilder();
        sb.append("https://api.openweathermap.org/data/2.5/onecall?");
        sb.append("lat=").append(latitude);
        sb.append("&lon=").append(longitude);
        sb.append("&units=metric");
        sb.append("&exclude=minutely,hourly,alerts");
        sb.append("&appid=").append(apiKey);

        return sb.toString();
    }


    // Update with users name
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
        });
    }

}