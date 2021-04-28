package com.example.beetrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DataActivity extends AppCompatActivity {
    private TextView lat_view, lon_view, count_view, add_view, q1_view, q2_view, q3_view, time_view, weat_view;
    private ImageView img_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        lat_view = findViewById(R.id.data_text_view_lat);
        lon_view = findViewById(R.id.data_text_view_long);
        count_view = findViewById(R.id.data_text_view_country);
        add_view = findViewById(R.id.data_text_view_address);
        img_view = findViewById(R.id.data_image_view_upload);
        q1_view = findViewById(R.id.data_text_view_color);
        q2_view = findViewById(R.id.data_text_view_feature);
        q3_view = findViewById(R.id.data_text_view_status);
        time_view = findViewById(R.id.data_text_view_time);
        weat_view = findViewById(R.id.data_text_view_weather);

        String lat = getIntent().getStringExtra("LATITUDE");
        String lon = getIntent().getStringExtra("LONGITUDE");
        String add = getIntent().getStringExtra("ADDRESS");
        String count = getIntent().getStringExtra("COUNTRY");
        String image = getIntent().getStringExtra("IMAGE");
        String q1 = "Color: " + getIntent().getStringExtra("COLOR");
        String q2 = "Feature: " + getIntent().getStringExtra("FEATURE");
        String q3 = "Status: " + getIntent().getStringExtra("STATUS");
        String time = getIntent().getStringExtra("TIME");
        String weather = getIntent().getStringExtra("WEATHER");

        lat_view.setText(lat);
        lon_view.setText(lon);
        add_view.setText(add);
        count_view.setText(count);
        q1_view.setText(q1);
        q2_view.setText(q2);
        q3_view.setText(q3);
        time_view.setText(time);
        weat_view.setText(weather);
        Picasso.get()
                .load(image)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(img_view);

    }
}