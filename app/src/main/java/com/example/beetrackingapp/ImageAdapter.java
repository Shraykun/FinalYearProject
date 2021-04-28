package com.example.beetrackingapp;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Info> mInfo;

    public ImageAdapter(Context context, List<Info> info){
        mContext = context;
        mInfo = info;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.data_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Info infocurrent = mInfo.get(position);
        holder.textViewQ1.setText(infocurrent.getColor());
        holder.textViewQ2.setText(infocurrent.getFeature());
        holder.textViewQ3.setText(infocurrent.getStatus());
        holder.textViewTime.setText(infocurrent.getTime());
        holder.textViewWeather.setText(infocurrent.getWeather());
        holder.textViewLong.setText(infocurrent.getLongitude());
        holder.textViewLat.setText(infocurrent.getLatitude());
        holder.textViewCount.setText(infocurrent.getCountry());
        holder.textViewAdd.setText(infocurrent.getAddress());

        Picasso.get()
                .load(infocurrent.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mInfo.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewQ1;
        public TextView textViewQ2;
        public TextView textViewQ3;
        public TextView textViewTime;
        public TextView textViewWeather;
        public TextView textViewLong;
        public TextView textViewLat;
        public TextView textViewCount;
        public TextView textViewAdd;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewQ1 = itemView.findViewById(R.id.text_view_q1);
            textViewQ2 = itemView.findViewById(R.id.text_view_q2);
            textViewQ3 = itemView.findViewById(R.id.text_view_q3);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewWeather = itemView.findViewById(R.id.text_view_weather);
            textViewLong = itemView.findViewById(R.id.text_view_long);
            textViewLat = itemView.findViewById(R.id.text_view_lat);
            textViewCount = itemView.findViewById(R.id.text_view_country);
            textViewAdd = itemView.findViewById(R.id.text_view_address);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
