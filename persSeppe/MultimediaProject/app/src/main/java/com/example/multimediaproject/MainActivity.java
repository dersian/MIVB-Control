package com.example.multimediaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;

import android.content.IntentSender;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.util.Log;

import org.json.JSONObject;

import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_lOCATION = 99;
    private static final int DISTANCE_RADIUS = 500;

    // UI elements
    TextView textGPS;

    //---Location Stuff:---//
    // Config file for all settings related to FusedLocationProviderContent
    LocationRequest locationRequest;
    // Google API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    // Necessary for a function
    LocationCallback locationCallBack;

    // Station vars
    private List<StationSample> stationData = new ArrayList<>();
    private List<NearbyStations> nearbyStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI elements
        textGPS = findViewById(R.id.textGPS);

        // Read CSV file
        readStationData();

        // set all properties of LocationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // event that is triggered whenever the update interval is met
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // save the location
                updateUI(locationResult.getLastLocation());
            }
        };

        // First call this function so that the locationRequest object is made an permission is checked
        updateGPS();

        // Start constant location update when app is launched
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
        updateGPS();


    } // end Oncreate

    private void startLocationUpdates() {
        Toast.makeText(getApplicationContext(), "Location is being tracked", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        switch (requestCode) {
            case PERMISSION_FINE_lOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS() {
        // get permissions from the user to track GPS
        // get current location from the fused client
        // update UI
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        // If permission is granted from the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permission, put values of location in to the UI
                    updateUI(location);

                    // check nearby stations everytime location is updated
                    checkNearbyStations(location);
                }
            });
        }
        else {
            // permission not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // check OS version
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_lOCATION);
            }
        }
        
    }

    // Update the UI
    private void updateUI(Location location) {
        // update text view with new location
        textGPS.setText(String.valueOf(location.getLatitude()) + "\n" + String.valueOf(location.getLongitude()));
    }

    // Read CSV file and put into a list of created object
    private void readStationData() {
        InputStream inputStream = getResources().openRawResource(R.raw.stops_data);
        BufferedReader lineReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        );

        String line = "";
        int i = 0;
        try {
            while ((line = lineReader.readLine()) != null){
                // Skip header
                if(i == 0){
                    i++;
                    continue;
                }
                // Split by ';'
                String[] tokens = line.split(",");
                // Read the data
                StationSample sample = new StationSample();
                sample.setLatitude(Double.parseDouble(tokens[0]));
                sample.setLongitude(Double.parseDouble(tokens[1]));
                sample.setStation(tokens[2]);
                stationData.add(sample);

                //Log.d("MyActivity", "Just created:" + sample.toString());
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    private  void checkNearbyStations(Location currentLocation){
        // First clear previous List
        nearbyStations.clear();
        // Loop over List with station objects
        for (int i = 0; i < stationData.size(); i++){
            // Create Location object and add data from stationData List -> use build in .distanceTo function
            Location stationLocation = new Location(stationData.get(i).getStation());
            stationLocation.setLatitude(stationData.get(i).getLatitude());
            stationLocation.setLongitude(stationData.get(i).getLongitude());
            currentLocation.setLatitude(50.824232);
            currentLocation.setLongitude(4.396758);
            // Calculate distance
            double distance = currentLocation.distanceTo(stationLocation);
            Log.d("MyActivity", "Distance:" + distance);
            // Check if distance is in radius (m)
            if (distance < DISTANCE_RADIUS) {
                // Create nearbyStation object
                NearbyStations nearbyStation = new NearbyStations();
                nearbyStation.setLatitude(stationData.get(i).getLatitude());
                nearbyStation.setLongitude(stationData.get(i).getLongitude());
                nearbyStation.setStation(stationData.get(i).getStation());
                nearbyStation.setDistance(distance);
                // Add it to the list
                nearbyStations.add(nearbyStation);
                Log.d("MyActivity", "Just created:" + nearbyStation.toString());
            }
        }
    }
}