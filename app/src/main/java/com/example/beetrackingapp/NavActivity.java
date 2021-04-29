package com.example.beetrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        Button uploadBtn = findViewById(R.id.upload_main);

        //Logo
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.mipmap.ic_logo);

        //Instructions
        TextView step1 = findViewById(R.id.step1_view);
        String one = "To get started for uploading information click the 'Upload Bee Information' button below";
        step1.setText(one);

        TextView step2 = findViewById(R.id.step2_view);
        String two = "Location and an image from the gallery must be given to submit the data \n \n Questions about color, any distinct features and, the state of the bee are optional";
        step2.setText(two);

        TextView step3 = findViewById(R.id.step3_view);
        String three = "The map tab will give a visual overview of the data \n \n Clicking the markers will give a better view of the data";
        step3.setText(three);

        TextView step4 = findViewById(R.id.step4_view);
        String four = "The list tab shows the data in a list";
        step4.setText(four);

        //Set current as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        //When buttons get clicked
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_map:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_list:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavActivity.this, CameraActivity.class));
            }
        });
    }
}