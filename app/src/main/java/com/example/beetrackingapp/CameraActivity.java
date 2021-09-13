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
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Console;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CameraActivity extends AppCompatActivity {
    //Weather API
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = ""; //Get from website
    //Initialise variables
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private TextView latitude;
    private TextView longitude;
    private TextView country;
    private TextView address;
    private TextView weath;
    private ProgressBar progressBar;
    private Spinner colorSpn, featureSpn, statusSpn;

    FusedLocationProviderClient fusedLocationProviderClient;

    private Uri imageUri;
    private StorageReference mStorageRef;

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
        Button btnlocation = findViewById(R.id.btnLocation);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        country = findViewById(R.id.country);
        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.uploadProg);
        colorSpn = findViewById(R.id.colorScheme_Spinner);
        featureSpn = findViewById(R.id.feature_Spinner);
        statusSpn = findViewById(R.id.status_Spinner);
        weath = findViewById(R.id.weather);
        TextView qTitle = findViewById(R.id.questionTitle);
        TextView cTitle = findViewById(R.id.colorTitle);
        TextView fTitle = findViewById(R.id.featureTitle);
        TextView sTitle = findViewById(R.id.statusTitle);

        //Initialise fusedLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(CameraActivity.this);

        //Storage
        mStorageRef = FirebaseStorage.getInstance("gs://bee-tracking-app-9b4ff.appspot.com").getReference("uploads");

        //Location access
        String btnText = "Get Location";
        btnlocation.setText(btnText);
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

        String title = "Questions (Optional)";
        qTitle.setText(title);

        String cT = "Choose a color type that matches the bee(s):";
        cTitle.setText(cT);

        String fT = "Choose a distinct feature the bee has:";
        fTitle.setText(fT);

        String sT = "What state did you find the bee as:";
        sTitle.setText(sT);

        //Spinner options
        //Color settings
        List<String> color = new ArrayList<>();
        color.add("None of the above");
        color.add("Thorax: Ginger | Abdomen: Black | Tail: White");
        color.add("Thorax: Black | Abdomen: Black | Tail: Red/Orange");
        color.add("Thorax: Yellow | Abdomen: Yellow | Tail: White");
        color.add("Thorax: Brown | Abdomen: Ginger/Brown | Tail: --");
        color.add("Thorax: Brown | Abdomen: Orange | Tail: --");
        color.add("Thorax: Orange | Abdomen: Orange | Tail: --");
        color.add("Thorax: Grey/Ashy | Abdomen: Black | Tail: --");

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, color);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpn.setAdapter(colorAdapter);

        //Feature settings
        List<String> feature = new ArrayList<>();
        feature.add("None of the Above");
        feature.add("Black head");
        feature.add("Thick orange coat");
        feature.add("Monochrome color/Ashy color");

        ArrayAdapter<String> featureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, feature);
        featureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        featureSpn.setAdapter(featureAdapter);

        //Status settings
        List<String> status = new ArrayList<>();
        status.add("Do not know");
        status.add("Deceased");
        status.add("Alive");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, status);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpn.setAdapter(statusAdapter);


        //SELECTING A PICTURE
        Button btnCamera = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imageView);

        //When clicking the button
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        //FIREBASE
        ImageButton btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInfo();
            }
        });

    }

    //CAMERA SETTINGS
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //When picture is is uploaded, url is set as a preview in the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            imageView.setImageURI(imageUri);
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
                            latitude.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Latitude: </b><br></font>"
                                            + addresses.get(0).getLatitude())
                            );
                            //Set longitude
                            longitude.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Longitude: </b><br></font>"
                                            + addresses.get(0).getLongitude())
                            );
                            //Set country
                            country.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Country: </b><br></font>"
                                            + addresses.get(0).getCountryName())
                            );
                            //Set address
                            address.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Address: </b><br></font>"
                                            + addresses.get(0).getAddressLine(0))
                            );

                            getCurrentWeather(Double.toString(Math.round(location.getLatitude())), Double.toString(Math.round(location.getLongitude())));
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

    //Storage settings
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //FIREBASE SETTINGS
    private void uploadInfo(){
        //Location data
        String lat = latitude.getText().toString();
        String longi = longitude.getText().toString();
        String count = country.getText().toString();
        String add = address.getText().toString();
        String key = root.getKey();

        //Question data
        colorSpn = (Spinner) findViewById(R.id.colorScheme_Spinner);
        String color_data = colorSpn.getSelectedItem().toString();
        featureSpn = (Spinner) findViewById(R.id.feature_Spinner);
        String feature_data = featureSpn.getSelectedItem().toString();
        statusSpn = (Spinner) findViewById(R.id.status_Spinner);
        String status_data = statusSpn.getSelectedItem().toString();

        //Date and Time data
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MMM-yyyy hh:mm:ss a");
        String dataTime = simpleDateFormat.format(calendar.getTime());

        //Weather Data
        String weather = weath.getText().toString();


        if (imageUri != null && !lat.equals("")){
            //Image
            StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(CameraActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String url = uri.toString();

                                    //Uploading
                                    HashMap<String , String> infoMap = new HashMap<>();

                                    infoMap.put("ID", key);
                                    infoMap.put("address", add);
                                    infoMap.put("country", count);
                                    infoMap.put("latitude", lat);
                                    infoMap.put("longitude", longi);
                                    infoMap.put("image", url);
                                    infoMap.put("time", dataTime);
                                    infoMap.put("color", color_data);
                                    infoMap.put("feature", feature_data);
                                    infoMap.put("status", status_data);
                                    infoMap.put("weather", weather);

                                    root.setValue(infoMap);

                                    Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });


        } else {
            Toast.makeText(this, "No picture or location given", Toast.LENGTH_SHORT).show();
        }


    }

    //Weather
    private void getCurrentWeather(String lat, String lon){
        //To get weather as JSON
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Return the JSON object
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String stringWeather = "Weather: " + weatherResponse.weathers.get(0).description +
                            "\n" +
                            "Temperature(K): " + weatherResponse.main.temp +
                            "\n" +
                            "Min-Temperature(K): " +
                            weatherResponse.main.temp_min +
                            "\n" +
                            "Max-Temperature(K): " +
                            weatherResponse.main.temp_max +
                            "\n" +
                            "Humidity: " +
                            weatherResponse.main.humidity +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main.pressure;

                    weath.setText(stringWeather);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weath.setText(t.getMessage());
            }
        });
    }
}
