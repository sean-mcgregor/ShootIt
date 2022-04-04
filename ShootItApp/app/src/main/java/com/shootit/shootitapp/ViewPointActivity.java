package com.shootit.shootitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewPointActivity extends AppCompatActivity implements OnMapReadyCallback {


    Marker newPoint;
    private TextView titleView, descriptionView;
    private Button backButton, createPlanButton;
    private GoogleMap googleMap;
    private JsonObjectRequest jsonRequest;
    private String apiKey = BuildConfig.WEATHER_API_KEY;

    ShootLocation location;

    PhotoFragment photoToAdd;
    LinearLayout imageContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point);

        // Fetch bundled location
        if(getIntent().getExtras() != null) {
            location = getIntent().getParcelableExtra("location");
        }

        titleView = (TextView) findViewById(R.id.locationName);
        descriptionView = (TextView) findViewById(R.id.locationDescription);
        backButton = (Button) findViewById(R.id.back_button);
        createPlanButton = (Button) findViewById(R.id.createPlanButton);
        imageContainer = (LinearLayout) findViewById(R.id.imageContainer);

        titleView.setText(location.getTitle());
        descriptionView.setText(location.getDescription());

        updateWeather(location.getLatitude(), location.getLongitude());

        // If back button pressed finish activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // Launch create plan activity
        createPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchCreatePlanActivity((ShootLocation) location);
            }
        });

        // Populate screen with photos of image
        location.getImages().forEach(uri -> {

            addPhoto(uri);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(mapFragment!=null){

            mapFragment.getMapAsync(this);
        }
    }

    private void launchCreatePlanActivity(ShootLocation location) {

        Intent planCreatorLauncher = new Intent(getApplicationContext(), CreatePlanActivity.class);
        planCreatorLauncher.putExtra("location", (Parcelable) location);
        startActivity(planCreatorLauncher);
    }


    // Add photo to screen
    private void addPhoto(Uri uri) {

        if (uri != null){

            photoToAdd = new PhotoFragment(uri, false);
            getSupportFragmentManager().beginTransaction().add(R.id.imageContainer, photoToAdd).commit();
        }
    }


    // When map fragment ready, configure
    @Override
    public void onMapReady(GoogleMap tempMap) {

        googleMap = tempMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));

        newPoint = googleMap.addMarker(new MarkerOptions()
                .position(location.getPosition()));
    }


    // Add weather to screen
    private void updateWeather(String latitude, String longitude) {

        String requestURL = buildRequestURL(latitude, longitude);
        Log.d("url", requestURL);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        jsonRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new com.android.volley.Response.Listener // CHANGES HERE
                        <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // Access daily forecast
                            JSONArray daily = response.getJSONArray("daily");
                            addDailyWeather(daily);

                            Log.d("jsonRequest", "cancelled");
                            jsonRequest.cancel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener () {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Crash", "Volley failed");
                        error.printStackTrace();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    // Build URL for weather request
    private String buildRequestURL(String latitude, String longitude) {

        StringBuilder sb = new StringBuilder();
        sb.append("https://api.openweathermap.org/data/2.5/onecall?");
        sb.append("lat=").append(latitude);
        sb.append("&lon=").append(longitude);
        sb.append("&units=metric");
        sb.append("&exclude=minutely,hourly,alerts");
        sb.append("&appid=").append(apiKey);

        return sb.toString();
    }


    // Add weather to screen
    private void addDailyWeather(JSONArray daily) throws JSONException {

        // Loop through array of days
        for (int i = 0; i < daily.length(); i++) {

            // store each object in JSONObject
            JSONObject day = daily.getJSONObject(i);
            JSONArray weatherArray = day.getJSONArray("weather");
            JSONObject dayWeather = (JSONObject) weatherArray.get(0);
            String dayIcon = dayWeather.getString("icon");

            // get field value from JSONObject using get() method
            Log.d("DT", day.get("dt").toString());

            // Add fragment to UI
            WeatherFragment dayObject = new WeatherFragment(getIconUri(dayIcon), day.get("dt").toString());
            getSupportFragmentManager().beginTransaction().add(R.id.weatherContainer, dayObject).commit();
        }
    }


    // Get resource for weather image
    private Uri getIconUri(String dayIcon) {

        StringBuilder iconAddress = new StringBuilder();
        iconAddress.append("https://openweathermap.org/img/wn/").append(dayIcon).append("@2x.png");
        return Uri.parse(iconAddress.toString());
    }
}