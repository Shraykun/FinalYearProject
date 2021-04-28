package com.example.beetrackingapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private HashMap<Marker, Info> mHashMap = new HashMap<Marker, Info>();

    private GoogleMap mMap;

    private double longitude, latitude;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //For navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set current as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_map);

        //When buttons get clicked
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), NavActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_map:
                        return true;
                    case R.id.nav_list:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        Button btnCurrent = findViewById(R.id.btn_current);
        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

    }

    //When map first gets launched
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Move camera to england
        LatLng london = new LatLng(55, 3);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(london));

        //For loop of markers and set the key of each data as key in marker
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bee-tracking-app-9b4ff-default-rtdb.firebaseio.com/");
        DatabaseReference databaseReference = database.getReference().child("Bee Data");

        //Getting each data set from firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Info info = postSnapshot.getValue(Info.class);

                    //Make sure that the returned value in only in double form without other characters
                    String lat = info.getLatitude();
                    lat = lat.replaceAll("[^0-9?!\\.]","");

                    String lon = info.getLongitude();
                    lon = lon.replaceAll("[^0-9?!\\.]","");

                    LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                    //Set a marker for each data
                    Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(info.getID()));
                    mHashMap.put(marker, info);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //When the marker is clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Extract information from hash map with this marker
                Info info = mHashMap.get(marker);
                String q1 = info.getColor();
                String q2 = info.getFeature();
                String q3 = info.getStatus();
                String time = info.getTime();
                String weather = info.getWeather();
                String lat = info.getLatitude();
                String lon = info.getLongitude();
                String add = info.getAddress();
                String count = info.getCountry();
                String image = info.getImage();

                //Create an intent which contains the information needed
                Intent intent = new Intent(MapsActivity.this, DataActivity.class);
                intent.putExtra("LATITUDE", lat);
                intent.putExtra("LONGITUDE", lon);
                intent.putExtra("ADDRESS", add);
                intent.putExtra("COUNTRY", count);
                intent.putExtra("IMAGE", image);
                intent.putExtra("COLOR", q1);
                intent.putExtra("FEATURE", q2);
                intent.putExtra("STATUS", q3);
                intent.putExtra("TIME", time);
                intent.putExtra("WEATHER", weather);

                //Go to activity with the intent
                startActivity(intent);

                return false;
            }
        });
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null){
                    //Getting longitude and latitude
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();

                    moveMap();
                }
            }
        });
    }

    private void moveMap(){
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, "Moved to Current Location", Toast.LENGTH_LONG).show();
    }


}