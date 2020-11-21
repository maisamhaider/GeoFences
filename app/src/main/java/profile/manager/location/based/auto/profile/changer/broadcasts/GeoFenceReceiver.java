package profile.manager.location.based.auto.profile.changer.broadcasts;

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

import profile.manager.location.based.auto.profile.changer.annotations.MyAnnotations;
import profile.manager.location.based.auto.profile.changer.database.MyDatabase;
import profile.manager.location.based.auto.profile.changer.notification.NotificationHelper;
import profile.manager.location.based.auto.profile.changer.utils.AdminHelper;
import profile.manager.location.based.auto.profile.changer.utils.AllActionsUtils;

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
    NotificationHelper notificationHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }


        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.i(TAG, geofence.getRequestId());
        }
        AllActionsUtils allActionsUtils = new AllActionsUtils(context);
        AdminHelper adminHelper = new AdminHelper(context);
        MyDatabase db = new MyDatabase(context);
        Cursor cursor = db.retrieveSingleRow(geofenceList.get(0).getRequestId());
        String title = "null";
        String type = "null";
        while (cursor.moveToNext()) {
            title = cursor.getString(1);
            type = cursor.getString(6);
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                if (type.matches(MyAnnotations.ENTER)) {
                    enter(false, context, allActionsUtils,
                            adminHelper, db
                            , geofenceList.get(0).
                                    getRequestId(),
                            title);

                }
                if (type.matches(MyAnnotations.BOTH)) {
                    enter(true, context, allActionsUtils,
                            adminHelper, db
                            , geofenceList.get(0).
                                    getRequestId(),
                            title);
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                exit(context, allActionsUtils, adminHelper, db, geofenceList.get(0).
                        getRequestId(), title);
                break;
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
            }
        });
    }

    public void audioFun(String ringingMode, Context context, AllActionsUtils allActionsUtils) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                allActionsUtils.setAudioMode(ringingMode);
            }


        } else {
            allActionsUtils.setAudioMode(ringingMode);
        }
    }

    public void enter(boolean isBoth, Context context, AllActionsUtils allActionsUtils,
                      AdminHelper adminHelper, MyDatabase db, String id, String title) {

        if (isBoth) {
            notificationHelper.sendHighPriorityNotification(title, "you are entered to "
                    + title + " area");
            Cursor cursor1 = db.retrieve(true, id);
            if (cursor1.moveToNext()) {
                String enSilent = cursor1.getString(3);
                String enBluetooth = cursor1.getString(2);
                String enWifi = cursor1.getString(4);
                String enLock = cursor1.getString(5);
                // set Audio
                if (!enSilent.matches(MyAnnotations.NULL)) {

                    audioFun(enSilent, context, allActionsUtils);
                }

                allActionsUtils.setBluetoothOnOff(enBluetooth.matches(MyAnnotations.ON));
                if (!enWifi.matches(MyAnnotations.NULL)) {

                    allActionsUtils.setWifiOnOff(enWifi.matches(MyAnnotations.ON));
                }
                if (enLock.matches(MyAnnotations.ON)) {
                    if (adminHelper.isActive()) {
                        adminHelper.lockPhone();
                    }

                }

            }
        } else
            notificationHelper.sendHighPriorityNotification(title, "you are entered to "
                    + title + " area");

        Cursor cursor1 = db.retrieve(true, id);
        if (cursor1.moveToNext()) {
            String enSilent = cursor1.getString(3);
            String enBluetooth = cursor1.getString(2);
            String enWifi = cursor1.getString(4);
            String enLock = cursor1.getString(5);
            // set Audio
            if (!enSilent.matches(MyAnnotations.NULL)) {

                audioFun(enSilent, context, allActionsUtils);
            }

            allActionsUtils.setBluetoothOnOff(enBluetooth.matches(MyAnnotations.ON));
            if (!enWifi.matches(MyAnnotations.NULL)) {

                allActionsUtils.setWifiOnOff(enWifi.matches(MyAnnotations.ON));
            }
            if (enLock.matches(MyAnnotations.ON)) {
                if (adminHelper.isActive()) {
                    adminHelper.lockPhone();
                }
            }
        }
        db.update(id, MyAnnotations.DONE);
        removeGeofence(context, id);
    }

    public void exit(Context context, AllActionsUtils allActionsUtils,
                     AdminHelper adminHelper, MyDatabase db, String id, String title) {

        notificationHelper.sendHighPriorityNotification(title, "you are exited from "
                + title + " area");
        Cursor cursor1 = db.retrieve(false, id);
        if (cursor1.moveToNext()) {
            String exSilent = cursor1.getString(3);
            String exBluetooth = cursor1.getString(2);
            String exWifi = cursor1.getString(4);
            String exLock = cursor1.getString(5);
            // set Audio
            if (!exSilent.matches(MyAnnotations.NULL)) {

                audioFun(exSilent, context, allActionsUtils);
            }

            allActionsUtils.setBluetoothOnOff(exBluetooth.matches(MyAnnotations.ON));
            if (!exWifi.matches(MyAnnotations.NULL)) {
                allActionsUtils.setWifiOnOff(exWifi.matches(MyAnnotations.ON));
            }
            if (exLock.matches(MyAnnotations.ON)) {
                if (adminHelper.isActive()) {
                    adminHelper.lockPhone();
                }
            }
        }

        db.update(id, MyAnnotations.DONE);
        removeGeofence(context, id);
    }


}
