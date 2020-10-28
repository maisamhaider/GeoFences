package com.example.geofences.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofences.R;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.broadcasts.GeoFenceReceiver;
import com.example.geofences.models.HistoryModel;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemHolder> {

    Context context;
    ArrayList<HistoryModel> list;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_items_layout, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        int id = list.get(position).getId();
        String title = list.get(position).getTitle();
        String latitude = list.get(position).getLatitude();
        String longitude = list.get(position).getLongitude();
        String circleSize = list.get(position).getCircleSize();
        String state = list.get(position).getState();

        holder.title_tv.setText(title);
        holder.circleSize_tv.setText(circleSize + " meters");
        holder.latLong_tv.setText("lat: " + latitude + " " + "long: " + longitude);




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView title_tv, circleSize_tv, latLong_tv;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
            circleSize_tv = itemView.findViewById(R.id.circleSize_tv);
            latLong_tv = itemView.findViewById(R.id.latLong_tv);
        }
    }
}
