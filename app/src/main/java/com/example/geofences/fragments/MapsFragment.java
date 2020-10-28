package com.example.geofences.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.geofences.R;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.geofencer.GeoFencerHelper;
import com.example.geofences.permissions.ThesePermissions;
import com.example.geofences.sharedpreferences.MyPreferences;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    private GeofencingClient geofencingClient;
    private ThesePermissions thesePermissions;
    private ArrayList<Geofence> geofenceList;
    private GoogleMap map;
    private float geofenceCircle = 50;
    private GeoFencerHelper geoFencerHelper;
    private MyDatabase myDatabase;
    private TextView geoFencesAmountTv;
    private final long geoFencesLimit = 100;
    private MyPreferences preferences;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            enableUserCurrentLocation();
            if (myDatabase.retrieveRowsAmount()!=0)
            {

            if (preferences.getBoolean(MyAnnotations.IS_BOOT_COMPLETED, false)) {
                //add geo_fences again if user want to re-add undone geo_fences
                onBootComplete(getActivity(), true);
                geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - myDatabase.retrieveRowsAmount()));

            } else {
                //just set mark and circle.do not add geo_fences
                setFromDbToGeoFenceAgain(false);
            }
            }
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    View view = getActivity().getLayoutInflater()
                            .inflate(R.layout.custom_input_dialog_layout, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setView(view).setCancelable(true);

                    TextView titleTv = view.findViewById(R.id.dialogTitle_tv);
                    EditText messageEt = view.findViewById(R.id.input_et);
                    TextView negativeTv = view.findViewById(R.id.negative_tv);
                    TextView positiveTv = view.findViewById(R.id.positive_tv);
                    titleTv.setText("Area Name");
                    negativeTv.setText("Dismiss");
                    positiveTv.setText("Done");

                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    negativeTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    positiveTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String title = messageEt.getText().toString();

                            if (title.length() != 0) {
                                addMarkAndGeoFence(title, latLng.latitude, latLng.longitude,
                                        geofenceCircle, true);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(geoFencerHelper, "Please enter area title",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            });


            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (!isLocationOn()) {
                        showDialog("Location", "To move on your current location you have to ON the device location.");
                    }
                    return false;
                }
            });
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to remove the mark ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //TODO think on getID that is it the geofence id or not
                                    // just update in database when the mark is removed.

//                                    String id = geofence.getRequestId();
//                                    myDatabase.update(, MyAnnotations.DONE);
                                    marker.remove();
                                    map.clear();
                                    setFromDbToGeoFenceAgain(false);

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });

        }


    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        geoFencesAmountTv = view.findViewById(R.id.geo_fences_amount_tv);
        preferences = new MyPreferences(getActivity());

        thesePermissions = new ThesePermissions(getActivity());

        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geoFencerHelper = new GeoFencerHelper(getActivity());
        myDatabase = new MyDatabase(getActivity());
        // set listeners

        //set value
        geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - myDatabase.retrieveRowsAmount()));


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void addMarkAndGeoFence(String title, double latitude, double longitude,
                                    float geofenceCircle, boolean trueFalse) {
        if (myDatabase.retrieveRowsAmount() <= geoFencesLimit) {
            LatLng location = new LatLng(latitude, longitude);
            // if device is not restarted in this condition we need to add geo_fences to database
            if (trueFalse) {
                long insert = myDatabase.insert(title, String.valueOf(latitude),
                        String.valueOf(longitude), String.valueOf(geofenceCircle),
                        MyAnnotations.UN_DONE);

                if (insert != -1) {
                    addMarkAndMoveCamera(location, title);
                    addCircleOfGeofence(location, geofenceCircle);
                    addGeofence(location, geofenceCircle, String.valueOf(insert));
                    geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - myDatabase.retrieveRowsAmount()));

                } else {
                    Toast.makeText(geoFencerHelper, "Error", Toast.LENGTH_SHORT).show();
                }
            }
            //otherwise just add mark
            else {
                addMarkAndMoveCamera(location, title);
                addCircleOfGeofence(location, geofenceCircle);
            }

        } else {
            Toast.makeText(geoFencerHelper, "Limit is reached", Toast.LENGTH_SHORT).show();
        }
    }

    // add mark on map and move camera to the mark location
    private void addGeofence(LatLng latLng, float radius, String geofence_id) {

        Geofence geofence = geoFencerHelper.getGeofence(geofence_id
                , latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest geofencingRequest = geoFencerHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFencerHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            thesePermissions.permission();
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Geofence added Successful",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), geoFencerHelper.getErrorMessage(e),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addMarkAndMoveCamera(LatLng latLng, String title) {
        map.addMarker(new MarkerOptions().position(latLng).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    // add circle around the mark on map
    private void addCircleOfGeofence(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(225, 0, 100, 100));
        circleOptions.fillColor(Color.argb(60, 0, 10, 100));
        circleOptions.strokeWidth(5);
        map.addCircle(circleOptions);


    }

    private void enableUserCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            thesePermissions.permission();
            return;
        }
        map.setMyLocationEnabled(true);
    }

    // is location on or off
    public boolean isLocationOn() {
        return isLocationEnabled(getActivity()); // application context
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle(title).setMessage(message).setCancelable(true);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog dialog = adb.create();
        dialog.show();
    }


    private void onBootComplete(Context context, boolean bootCompleted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Alert").setMessage("If you want to recover all old selected areas it will reduce the limit of marks")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user want to add all geo_fences again
                        setFromDbToGeoFenceAgain(bootCompleted);
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void setFromDbToGeoFenceAgain(boolean trueFalse) {
        Cursor cursor = myDatabase.retrieve();
        while (cursor.moveToNext()) {

            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String latitude = cursor.getString(2);
            String longitude = cursor.getString(3);
            String radius = cursor.getString(4);
            String state = cursor.getString(5);
            if (state.matches(MyAnnotations.UN_DONE)) {
                if (myDatabase.retrieveRowsAmount() < geoFencesLimit) {


                    if (trueFalse) {
                        //insert as new because we have limits of 100 geofences.
                        addMarkAndGeoFence(title, Double.parseDouble(latitude)
                                , Double.parseDouble(longitude),
                                Float.parseFloat(radius), true);
                        myDatabase.update(id, MyAnnotations.DONE);
                    } else {
                        addMarkAndGeoFence(title, Double.parseDouble(latitude)
                                , Double.parseDouble(longitude),
                                Float.parseFloat(radius), false);
                    }


                } else {
                    Toast.makeText(geoFencerHelper, "Limit reached", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

    }

    public void removeGeoFence(List<String> list)
    {
        PendingIntent pending = geoFencerHelper.getPendingIntent();

                geofencingClient.removeGeofences(list)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        Toast.makeText(geoFencerHelper, "onSuccess", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                        Toast.makeText(geoFencerHelper, "onFailure", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}