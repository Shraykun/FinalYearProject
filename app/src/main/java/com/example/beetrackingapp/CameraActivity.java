package com.example.beetrackingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    //Initialise variables
    ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int CAMERA_PERMISSION_CODE = 1;
    Button btnlocation, btnSubmit;
    TextView latitude, longitude, country, local, address;
    FusedLocationProviderClient fusedLocationProviderClient;

    //Database id
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://bee-tracking-app-9b4ff-default-rtdb.firebaseio.com/");
    //Inside database
    private DatabaseReference root = firebaseDatabase.getReference().child("Bee Data").push();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //LOCATION SETTINGS
        //Assign
        btnlocation = findViewById(R.id.btnLocation);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        country = findViewById(R.id.country);
        local = findViewById(R.id.local);
        address = findViewById(R.id.address);

        //Initialise fusedLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(CameraActivity.this);

        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check permission
                if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //When permission granted
                    getLocation();
                } else {
                    //When permission denied
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        //CAMERA SETTINGS
        //Check if device has a camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            Button btnCamera = (Button) findViewById(R.id.btnCamera);
            imageView = (ImageView) findViewById(R.id.imageView);

            //When clicking on button
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (ActivityNotFoundException e) {
                            // display error state to the user
                        }
                    } else {
                        requestCameraPermission();
                    }

                }
            });
        } else {
            Toast.makeText(CameraActivity.this, "There is no Camera feature available", Toast.LENGTH_SHORT).show();
        }

        //FIREBASE
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = latitude.getText().toString();
                String longi = longitude.getText().toString();
                String count = country.getText().toString();
                String loc = local.getText().toString();
                String add = address.getText().toString();

                HashMap<String , String> infoMap = new HashMap<>();

                infoMap.put("address", add);
                infoMap.put("country", count);
                infoMap.put("local", loc);
                infoMap.put("latitude", lat);
                infoMap.put("longitude", longi);

                root.setValue(infoMap);
            }
        });

    }

    //CAMERA SETTINGS
    //When picture is taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }

    //Checks if permission is given, if not as for permission.
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to access the camera and take pictures")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    //Text for when it is successful or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getLocation();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    //LOCATION SETTINGS
    private void getLocation() {
        //Initialise location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //When location is enabled
            //Get last location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialise location
                    Location location = task.getResult();
                    //Check condition
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(CameraActivity.this, Locale.getDefault());

                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            //Set latitude
                            latitude.setText(String.format(String.valueOf(addresses.get(0).getLatitude()))
                            );
                            //Set longitude
                            longitude.setText(String.format(String.valueOf(addresses.get(0).getLongitude()))
                            );
                            //Set country
                            country.setText(addresses.get(0).getCountryName()
                            );
                            //Set locality
                            local.setText(addresses.get(0).getLocality()
                            );
                            //Set address
                            address.setText(addresses.get(0).getAddressLine(0)
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                        //If location is null
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                Geocoder geocoder = new Geocoder(CameraActivity.this, Locale.getDefault());

                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);

                                    //Set latitude
                                    latitude.setText(Html.fromHtml(
                                            "<font color='#6200EE'><b>Latitude: </b><br></font>"
                                                    + addresses.get(0).getLatitude()
                                    ));
                                    //Set longitude
                                    longitude.setText(Html.fromHtml(
                                            "<font color='#6200EE'><b>Longitude: </b><br></font>"
                                                    + addresses.get(0).getLongitude()
                                    ));
                                    //Set country
                                    country.setText(Html.fromHtml(
                                            "<font color='#6200EE'><b>Country: </b><br></font>"
                                                    + addresses.get(0).getCountryName()
                                    ));
                                    //Set locality
                                    local.setText(Html.fromHtml(
                                            "<font color='#6200EE'><b>Locality: </b><br></font>"
                                                    + addresses.get(0).getLocality()
                                    ));
                                    //Set address
                                    address.setText(Html.fromHtml(
                                            "<font color='#6200EE'><b>Address: </b><br></font>"
                                                    + addresses.get(0).getAddressLine(0)
                                    ));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });

        } else {
            //When location service no available
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}