package com.example.geofences.geofencer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.broadcasts.GeoFenceReceiver;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeoFencerHelper extends ContextWrapper {

    private PendingIntent pendingIntent;

    public GeoFencerHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder().addGeofence(geofence)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER).build();
    }

    public Geofence getGeofence(String id, LatLng latLng, float radius, String transitionType, long expirationTime) {

        if (transitionType.matches(MyAnnotations.ENTER)) {
            return new Geofence.Builder()
                    .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                    .setRequestId(id)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setLoiteringDelay(5000)
                    .setExpirationDuration(expirationTime)
                    .build();

        } else if (transitionType.matches(MyAnnotations.EXIT)) {
            return new Geofence.Builder()
                    .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                    .setRequestId(id)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setLoiteringDelay(5000)
                    .setExpirationDuration(expirationTime)
                    .build();
        } else
            return new Geofence.Builder()
                    .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                    .setRequestId(id)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setLoiteringDelay(5000)
                    .setExpirationDuration(expirationTime)
                    .build();
    }

    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeoFenceReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public String getErrorMessage(Exception e) {
        if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            switch (exception.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
}
