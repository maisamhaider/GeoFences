package com.example.geofences.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.sharedpreferences.MyPreferences;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class OnBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        MyPreferences preferences = new MyPreferences(context);
        preferences.setBoolean(MyAnnotations.IS_BOOT_COMPLETED,true);
    }
}
