package com.example.geofences.adapters;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.TimeUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofences.R;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.broadcasts.GeoFenceReceiver;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.interfaces.SelectAll;
import com.example.geofences.models.HistoryModel;
import com.example.geofences.utils.TimeUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemHolder> implements SelectAll {

    Context context;
    ArrayList<HistoryModel> list;
    MyDatabase myDatabase;
    private ArrayList<String> sendingList;
    private SelectAll selectAll;
    private boolean isDone;
    boolean AllSelected = false;
    ItemHolder holder;
    boolean isLongClicked = false;
    boolean isDown = false;
    TimeUtil timeUtil;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> list, MyDatabase myDatabase, SelectAll selectAll, boolean isDone) {
        this.context = context;
        this.list = list;
        this.myDatabase = myDatabase;
        this.selectAll = selectAll;
        this.isDone = isDone;
        sendingList = new ArrayList<>();
        if (isDone) {
            for (HistoryModel h : list) {
                sendingList.add(String.valueOf(h.getId()));
            }
        }
        timeUtil = new TimeUtil(context);
    }

    public ArrayList<String> getSendingList() {
        return sendingList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_items_layout, parent, false);
        return new ItemHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        this.holder = holder;
        myDatabase = new MyDatabase(context);
        int id = list.get(position).getId();
        String title = list.get(position).getTitle();
        String latitude = list.get(position).getLatitude();
        String longitude = list.get(position).getLongitude();
        String circleSize = list.get(position).getCircleSize();
        String state = list.get(position).getState();
        long expirationTime = list.get(position).getExpirationTime();
        String expirationEnd = list.get(position).getExpirationEnd();
        String geofenceType = list.get(position).getGeofenceType();
        String dateNow = list.get(position).getDateNow();


        holder.title_tv.setText(title);
        holder.latitude_tv.setText(latitude);
        holder.longitude_tv.setText(longitude);
        holder.expirationTime_tv.setText(timeUtil.getFormattedTime(Long.parseLong(expirationEnd)));
        if (MILLISECONDS.toHours(expirationTime) == 1) {

            holder.expirationDuration_tv.setText(MILLISECONDS.
                    toHours(expirationTime) + "hour");
        } else {
            holder.expirationDuration_tv.setText(MILLISECONDS.
                    toHours(expirationTime) + "hours");
        }
        holder.geoFenceType_tv.setText(geofenceType);
        holder.circleSize_tv.setText(circleSize + " (m)");
        holder.state_tv.setText(state);

        if (isDone) {
            isLongClicked = true;
        }
        if (isLongClicked) {
            if (sendingList.contains(String.valueOf(id))) {
                imageShow();
                holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.ic_awesome_check_circle, null));
            } else {
                imageShow();
                holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.ic_material_radio_button_unchecked, null));
            }

        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isDown) {
                    isLongClicked = true;
                    for (HistoryModel h : list) {
                        holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                                R.drawable.ic_material_radio_button_unchecked, null));
                        holder.imageView.setVisibility(View.VISIBLE);
                        notifyDataSetChanged();
                    }

                }

                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLongClicked) {
                    String pos = String.valueOf(id);
                    if (sendingList.contains(pos)) {
                        sendingList.remove(pos);
                        selectAll.selected(false);
                        holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                                R.drawable.ic_material_radio_button_unchecked, null));
                    } else {

                        sendingList.add(pos);
                        selectAll.selected(list.size() == sendingList.size());

                        holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                                R.drawable.ic_awesome_check_circle, null));
                    }
                }
            }
        });
        holder.swiping_ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDown) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN /*&&
                            event.getAction() == MotionEvent.ACTION_HOVER_MOVE*/) {

                        isDown = false;
                        holder.clLayout_two.setVisibility(View.GONE);
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN /*&&
                            event.getAction() == MotionEvent.ACTION_HOVER_MOVE*/) {
                        isDown = true;

                        holder.clLayout_two.setVisibility(View.VISIBLE);
                    }

                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public SelectAll getSelectAll() {
        return this;
    }

    @Override
    public void selected(boolean selectedAll) {
        if (selectedAll) {
            selectAll();
        } else {
            deSelectAll();
        }
    }

    public void selectAll() {
        isLongClicked = true;
        if (!list.isEmpty()) {
            sendingList.clear();
        }
        for (HistoryModel h : list) {
            sendingList.add(String.valueOf(h.getId()));
            //setCheck box checked
            holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.ic_awesome_check_circle, null));
            imageShow();
            notifyDataSetChanged();

        }
    }

    public void deSelectAll() {
        if (!sendingList.isEmpty()) {
            sendingList.clear();
            //setCheck box Unchecked;
            holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.ic_material_radio_button_unchecked, null));
            imageShow();
            notifyDataSetChanged();
        }

    }

    public void changeImage(boolean selectOne) {
        if (selectOne) {
            for (HistoryModel h : list) {
                sendingList.add(String.valueOf(h.getId()));
                //setCheck box checked
                holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.ic_awesome_check_circle, null));
            }
        } else {
            for (HistoryModel h : list) {
                sendingList.add(String.valueOf(h.getId()));
                //setCheck box checked
                holder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.ic_material_radio_button_unchecked, null));
            }
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView title_tv, latitude_tv, longitude_tv, expirationTime_tv,
                geoFenceType_tv, circleSize_tv, state_tv, expirationDuration_tv;
        ImageView imageView;
        ConstraintLayout clLayout_two;
        LinearLayout swiping_ll;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title_tv = itemView.findViewById(R.id.title_tv);
            circleSize_tv = itemView.findViewById(R.id.circleSize_tv);
            latitude_tv = itemView.findViewById(R.id.latitude_tv);
            longitude_tv = itemView.findViewById(R.id.longitude_tv);
            expirationTime_tv = itemView.findViewById(R.id.expirationTime_tv);
            expirationDuration_tv = itemView.findViewById(R.id.expirationDuration_tv);
            geoFenceType_tv = itemView.findViewById(R.id.geoFenceType_tv);
            state_tv = itemView.findViewById(R.id.state_tv);
            clLayout_two = itemView.findViewById(R.id.clLayout_two);
            swiping_ll = itemView.findViewById(R.id.swiping_ll);
        }
    }

    public void deleteData() {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        List<String> idList = new ArrayList<>();
        if (!sendingList.isEmpty()) {
            for (String id : sendingList) {
                idList.add(id);
                deleteOnEntered(id);
                deleteOnExit(id);
                myDatabase.delete(id);
            }
            geofencingClient.removeGeofences(idList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(context, "onSuccess", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "onFailure", Toast.LENGTH_SHORT).show();

                }
            });
            imageHide();

            sendingList.clear();
            isLongClicked = false;
        } else {
            Toast.makeText(context, "No item selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void imageHide() {
        for (HistoryModel h : list) {
            holder.imageView.setVisibility(View.GONE);
        }

    }

    public void imageShow() {
        for (HistoryModel h : list) {
            holder.imageView.setVisibility(View.VISIBLE);

        }

    }

    public void deleteOnEntered(String foreign_key) {
        myDatabase.delete(true, foreign_key);
    }

    public void deleteOnExit(String foreign_key) {
        myDatabase.delete(false, foreign_key);
    }
}
