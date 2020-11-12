package com.example.geofences.broadcasts;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.utils.AdminHelper;
import com.example.geofences.utils.AllActionsUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceReceiver extends BroadcastReceiver {
    private static String TAG = "GeoFenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Toast.makeText(context, "geofencingEvent.hasError()", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.i(TAG, geofence.getRequestId());
        }
        Toast.makeText(context, "Enters", Toast.LENGTH_SHORT).show();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                action(context, String.valueOf(geofenceList.get(0).getRequestId()));
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                action(context, String.valueOf(geofenceList.get(0).getRequestId()));
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                action(context, String.valueOf(geofenceList.get(0).getRequestId()));
                break;
        }

    }

    private void action(Context context, String id) {


        AllActionsUtils allActionsUtils = new AllActionsUtils(context);
        AdminHelper adminHelper = new AdminHelper(context);
        MyDatabase db = new MyDatabase(context);
        Cursor cursor = db.retrieveSingleRow(id);
        String enSilent = "null";
        String enBluetooth = "null";
        String enWifi = "null";
        String enLock = "null";

        String exSilent = "null";
        String exBluetooth = "null";
        String exWifi = "null";
        String exLock = "null";
        while (cursor.moveToNext()) {
            String type = cursor.getString(6);
            if (type.matches(MyAnnotations.ENTER)) {
                Cursor cursor1 = db.retrieve(true, id);
                if (cursor1.moveToNext()) {
                    enSilent = cursor1.getString(3);
                    enBluetooth = cursor1.getString(2);
                    enWifi = cursor1.getString(4);
                    enLock = cursor1.getString(5);
                    // set Audio
                    if (!enSilent.matches(MyAnnotations.NULL)) {

                        audioFun(enSilent, context, allActionsUtils);
                    }

                    allActionsUtils.setBluetoothOnOff(enBluetooth.matches(MyAnnotations.ON));
                    allActionsUtils.setWifiOnOff(enWifi.matches(MyAnnotations.ON));
                    if (enLock.matches(MyAnnotations.ON)) {
                        if (adminHelper.isActive()) {
                            adminHelper.lockPhone();
                        } else {

//                            adminHelper.intentToAdmin();
                        }

                    }

                }
//                db.delete(id);
//                db.delete(true, id);
                db.update(id, MyAnnotations.DONE);
                removeGeofence(context, id);

            } else if (type.matches(MyAnnotations.EXIT)) {
                Cursor cursor1 = db.retrieve(false, id);
                if (cursor1.moveToNext()) {
                    exSilent = cursor1.getString(3);
                    exBluetooth = cursor1.getString(2);
                    exWifi = cursor1.getString(4);
                    exLock = cursor1.getString(5);
                    // set Audio
                    if (!enSilent.matches(MyAnnotations.NULL)) {

                        audioFun(exSilent, context, allActionsUtils);
                    }

                    allActionsUtils.setBluetoothOnOff(exBluetooth.matches(MyAnnotations.ON));
                    allActionsUtils.setWifiOnOff(exWifi.matches(MyAnnotations.ON));

                    if (exLock.matches(MyAnnotations.ON)) {
                        if (adminHelper.isActive()) {
                            adminHelper.lockPhone();
                        } /*else {

//                            adminHelper.intentToAdmin();
                        }*/

                    }

                }
//                db.delete(id);
//                db.delete(true, id);
                db.update(id, MyAnnotations.DONE);
                removeGeofence(context, id);


            } else {
                Cursor cursor1 = db.retrieve(true, id);
                if (cursor1.moveToNext()) {
                    enSilent = cursor1.getString(3);
                    enBluetooth = cursor1.getString(2);
                    enWifi = cursor1.getString(4);
                    enLock = cursor1.getString(5);
                    // set Audio
                    if (!enSilent.matches(MyAnnotations.NULL)) {

                        audioFun(enSilent, context, allActionsUtils);
                    }
                    allActionsUtils.setBluetoothOnOff(enBluetooth.matches(MyAnnotations.ON));
                    allActionsUtils.setWifiOnOff(enWifi.matches(MyAnnotations.ON));
                    if (enLock.matches(MyAnnotations.ON)) {
                        if (adminHelper.isActive()) {
                            adminHelper.lockPhone();
                        } else {

//                            adminHelper.intentToAdmin();
                        }
                    }
                }

                Cursor cursor2 = db.retrieve(false, id);
                if (cursor2.moveToNext()) {
                    exSilent = cursor1.getString(3);
                    exBluetooth = cursor1.getString(2);
                    exWifi = cursor1.getString(4);
                    exLock = cursor1.getString(5);

                    // set Audio
                    if (!enSilent.matches(MyAnnotations.NULL)) {

                        audioFun(exSilent, context, allActionsUtils);
                    }
                     allActionsUtils.setBluetoothOnOff(exBluetooth.matches(MyAnnotations.ON));
                    allActionsUtils.setWifiOnOff(exWifi.matches(MyAnnotations.ON));
                    if (exLock.matches(MyAnnotations.ON)) {
                        if (adminHelper.isActive()) {
                            adminHelper.lockPhone();
                        } /*else {

//                            adminHelper.intentToAdmin();
                        }*/

                    }
                }
//                db.delete(id);
//                db.delete(true, id);
                db.update(id, MyAnnotations.DONE);
                removeGeofence(context, id);
            }
        }

    }

    public void removeGeofence(Context context, String id) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        List<String> idList = new ArrayList<>();
        idList.add(String.valueOf(id));
        geofencingClient.removeGeofences(idList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "onFailure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void audioFun(String ringingMode, Context context, AllActionsUtils allActionsUtils) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                allActionsUtils.setAudioMode(ringingMode);
            }

        }
        else
        {
            allActionsUtils.setAudioMode(ringingMode);
        }
    }

}
