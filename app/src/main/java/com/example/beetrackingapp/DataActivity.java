package com.example.beetrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DataActivity extends AppCompatActivity {
    private TextView lat_view, lon_view, count_view, add_view;
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

        String lat = getIntent().getStringExtra("LATITUDE");
        String lon = getIntent().getStringExtra("LONGITUDE");
        String add = getIntent().getStringExtra("ADDRESS");
        String count = getIntent().getStringExtra("COUNTRY");
        String image = getIntent().getStringExtra("IMAGE");

        lat_view.setText(lat);
        lon_view.setText(lon);
        add_view.setText(add);
        count_view.setText(count);
        Picasso.get()
                .load(image)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(img_view);

    }
}