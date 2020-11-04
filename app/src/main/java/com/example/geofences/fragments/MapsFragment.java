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
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.util.TimeUtils;
import androidx.fragment.app.Fragment;

import com.example.geofences.R;
import com.example.geofences.activities.MainActivity;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.annotations.NumbersAnnotation;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.geofencer.GeoFencerHelper;
import com.example.geofences.permissions.ThesePermissions;
import com.example.geofences.sharedpreferences.MyPreferences;
import com.example.geofences.utils.AdminHelper;
import com.example.geofences.utils.AllActionsUtils;
import com.example.geofences.utils.TimeUtil;
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

import java.util.Calendar;


public class MapsFragment extends Fragment {
    private GeofencingClient geofencingClient;
    private ThesePermissions thesePermissions;
    private MyPreferences preferences;
    private GoogleMap map;
    private MyDatabase myDatabase;
    private GeoFencerHelper geoFencerHelper;
    private TimeUtil timeUtils;
    private float geofenceCircle = 50;
    private TextView geoFencesAmountTv;
    private final long geoFencesLimit = 20;
    AlertDialog.Builder builder1;
    private MainActivity mainActivity;

    AlertDialog dialog1;
    AllActionsUtils allActionsUtils;

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
            if (myDatabase.retrieveRowsAmount() != 0) {

                if (preferences.getBoolean(MyAnnotations.IS_BOOT_COMPLETED, false)) {
                    //add geo_fences again if user want to re-add undone geo_fences
                    onBootComplete(getActivity(), true);
                    geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - getAmountOfUndoneInDate()));

                } else {
                    //just set mark and circle.do not add geo_fences
                    setFromDbToGeoFenceAgain(false);
                }
            }
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (getAmountOfUndoneInDate() < geoFencesLimit) {
                        addGeoDialog(latLng);
                    } else {
                        Toast.makeText(getActivity(), "Limit is reached", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
//                    if (!allActionsUtils.isWifiEnable()) {
//                        showWifiDialog("Wifi", "Connect to wifi or data if you want to use this option");
//                    }
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
        mainActivity = ((MainActivity) getActivity());
        thesePermissions = new ThesePermissions(getActivity());
        allActionsUtils = new AllActionsUtils(getActivity());
        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geoFencerHelper = new GeoFencerHelper(getActivity());
        myDatabase = new MyDatabase(getActivity());
        // set listeners

        //set value
        geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - getAmountOfUndoneInDate()));
        timeUtils = new TimeUtil();

        // check time if its 12am then change the time and renew the limit of geofences
        if (checkGeoFenceChangeDate()) {
            geoFencesAmountTv.setText(String.valueOf(geoFencesLimit));

        } else {

        }

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


    //add to database add mark to map draw circle and add geofence
    private void addMarkAndGeoFence(String title,
                                    double latitude,
                                    double longitude,
                                    float geofenceCircle,
                                    String formattedExpirationTime,
                                    long expirationTime,
                                    String geoType,
                                    String dateNow,
                                    String enSilent,
                                    String enBluetooth,
                                    String enWifi,
                                    String enScreenLock,
                                    String exSilent,
                                    String exBluetooth,
                                    String exWifi,
                                    String exScreenLock
    ) {
        if (getAmountOfUndoneInDate() < geoFencesLimit) {
            LatLng location = new LatLng(latitude, longitude);
            // if device is restarted or new geo-fence wanted to be added in this condition we need
            // to add geo_fences to database
            long insert = myDatabase.insert(title,
                    String.valueOf(latitude),
                    String.valueOf(longitude),
                    String.valueOf(geofenceCircle),
                    formattedExpirationTime,
                    geoType,
                    String.valueOf(expirationTime),
                    MyAnnotations.UN_DONE, dateNow);

            if (geoType.matches(MyAnnotations.ENTER)) {
                myDatabase.insert(MyAnnotations.ENTER,
                        String.valueOf(insert),
                        enBluetooth,
                        enSilent,
                        enWifi,
                        enScreenLock);
            } else if (geoType.matches(MyAnnotations.EXIT)) {
                myDatabase.insert(MyAnnotations.EXIT,
                        String.valueOf(insert),
                        exBluetooth,
                        exSilent,
                        exWifi,
                        exScreenLock);
            } else {
                //if both(Exit and entered selected)
                myDatabase.insert(MyAnnotations.ENTER,
                        String.valueOf(insert),
                        enBluetooth,
                        enSilent,
                        enWifi,
                        enScreenLock);
                myDatabase.insert(MyAnnotations.EXIT,
                        String.valueOf(insert),
                        exBluetooth,
                        exSilent,
                        exWifi,
                        exScreenLock);
            }

            if (insert != -1) {
                addMarkAndMoveCamera(location, title);
                addCircleOfGeofence(location, geofenceCircle);
                if (geoType.matches(MyAnnotations.ENTER)) {
                    addGeofence(location, geofenceCircle, MyAnnotations.ENTER, String.valueOf(insert), expirationTime);

                } else if (geoType.matches(MyAnnotations.EXIT)) {
                    addGeofence(location, geofenceCircle, MyAnnotations.EXIT, String.valueOf(insert), expirationTime);
                } else {
                    addGeofence(location, geofenceCircle, MyAnnotations.BOTH, String.valueOf(insert), expirationTime);
                }

                geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - getAmountOfUndoneInDate()));
            } else {
                Toast.makeText(geoFencerHelper, "Error", Toast.LENGTH_SHORT).show();
            }
            //otherwise just add mark
        } else {
            Toast.makeText(geoFencerHelper, "Limit is reached", Toast.LENGTH_SHORT).show();
        }
    }


    // add geo fence
    private void addGeofence(LatLng latLng, float radius, String transitionType, String geofence_id, long expirationTime) {
        Geofence geofence = geoFencerHelper.getGeofence(geofence_id
                , latLng, radius, transitionType, expirationTime);

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
        circleOptions.strokeColor(Color.argb(225, 0, 50, 50));
        circleOptions.fillColor(Color.argb(40, 0, 10, 70));
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

    public void showWifiDialog(String title, String message) {
        AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle(title).setMessage(message).setCancelable(true);
        adb.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                allActionsUtils.setWifiOnOff(true);
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

    private void setFromDbToGeoFenceAgain(boolean afterBooted) {
        Cursor cursor = myDatabase.retrieve();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String latitude = cursor.getString(2);
            String longitude = cursor.getString(3);
            String radius = cursor.getString(4);
            String expirationEnd = cursor.getString(5);
            String geofenceType = cursor.getString(6);
            String expirationTime = cursor.getString(7);
            String state = cursor.getString(8);
            String dateNow = cursor.getString(9);
            long now = timeUtils.getNowMillis();

            String enSilent = "null";
            String enBluetooth = "null";
            String enWifi = "null";
            String enLock = "null";

            String exSilent = "null";
            String exBluetooth = "null";
            String exWifi = "null";
            String exLock = "null";

            if (geofenceType.matches(MyAnnotations.ENTER)) {
                Cursor cursor1 = myDatabase.retrieve(true, id);
                if (cursor1.moveToNext()) {
                    enSilent = cursor1.getString(3);
                    enBluetooth = cursor1.getString(2);
                    enWifi = cursor1.getString(4);
                    enLock = cursor1.getString(5);
                }
            } else if (geofenceType.matches(MyAnnotations.EXIT)) {
                Cursor cursor1 = myDatabase.retrieve(false, id);
                if (cursor1.moveToNext()) {
                    exSilent = cursor1.getString(3);
                    exBluetooth = cursor1.getString(2);
                    exWifi = cursor1.getString(4);
                    exLock = cursor1.getString(5);
                }
            } else {
                Cursor cursor1 = myDatabase.retrieve(true, id);
                if (cursor1.moveToNext()) {
                    enSilent = cursor1.getString(3);
                    enBluetooth = cursor1.getString(2);
                    enWifi = cursor1.getString(4);
                    enLock = cursor1.getString(5);
                }
                Cursor cursor2 = myDatabase.retrieve(false, id);
                if (cursor2.moveToNext()) {
                    exSilent = cursor1.getString(3);
                    exBluetooth = cursor1.getString(2);
                    exWifi = cursor1.getString(4);
                    exLock = cursor1.getString(5);
                }

            }


            if (state.matches(MyAnnotations.UN_DONE)) {
                if (myDatabase.retrieveRowsAmount() < geoFencesLimit) {
                    if (afterBooted) {

                        if (now < Long.parseLong(expirationEnd)) {
                            //insert as new because we have limits of 100 geofences.
                            addMarkAndGeoFence(title,
                                    Double.parseDouble(latitude),
                                    Double.parseDouble(longitude),
                                    Float.parseFloat(radius),
                                    expirationEnd,
                                    Long.parseLong(expirationTime),
                                    geofenceType,
                                    dateNow,
                                    enSilent,
                                    enBluetooth,
                                    enWifi,
                                    enLock,
                                    exSilent,
                                    exBluetooth,
                                    exWifi,
                                    exLock);
                            //done old geofence
                            myDatabase.update(id, MyAnnotations.DONE);
                        }


                    } else {


                        if (now < Long.parseLong(expirationEnd)) {
                            LatLng location = new LatLng(Double.parseDouble(latitude),
                                    Double.parseDouble(longitude));
                            //just add mark and circles
                            addMarkAndMoveCamera(location, title);
                            addCircleOfGeofence(location, geofenceCircle);
                        } else {
                            myDatabase.update(id, MyAnnotations.DONE);
                        }

                    }


                } else {
                    Toast.makeText(geoFencerHelper, "Limit reached", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

    }


    public void addGeoDialog(LatLng latLng) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.custom_input_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setCancelable(true);
        AdminHelper adminHelper = new AdminHelper(getActivity());

        TextView titleTv = view.findViewById(R.id.dialogTitle_tv);
        EditText messageEt = view.findViewById(R.id.input_et);
        TextView negativeTv = view.findViewById(R.id.negative_tv);
        TextView positiveTv = view.findViewById(R.id.positive_tv);
        NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        Spinner spinner = view.findViewById(R.id.spinner);
        ConstraintLayout onEntered_ll = view.findViewById(R.id.onEntered_cl);
        ConstraintLayout onExit_ll = view.findViewById(R.id.onExit_cl);

        SwitchCompat enteredSilent_switch = view.findViewById(R.id.enteredSilent_switch);
        SwitchCompat enteredBluetooth_switch = view.findViewById(R.id.enteredBluetooth_switch);
        SwitchCompat enteredWifi_switch = view.findViewById(R.id.enteredWifi_switch);
        CheckBox enteredLock_cb = view.findViewById(R.id.enteredLock_cb);

        SwitchCompat exitSilent_switch = view.findViewById(R.id.exitSilent_switch);
        SwitchCompat exitBluetooth_switch = view.findViewById(R.id.exitBluetooth_switch);
        SwitchCompat exitWifi_switch = view.findViewById(R.id.exitWifi_switch);
        CheckBox exitLock_cb = view.findViewById(R.id.exitLock_cb);


        final boolean[] isSpinnerListened = {false};
        onEntered_ll.setVisibility(View.GONE);
        onExit_ll.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity()
                , R.array.geo_fences_actions, android.R.layout.simple_spinner_item);

//        spinner.setDropDownVerticalOffset(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        titleTv.setText("Area Name");
        negativeTv.setText("Dismiss");
        positiveTv.setText("Done");
        numberPicker.setMaxValue(24);
        numberPicker.setMinValue(1);


        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSpinnerListened[0] = true;
                if (position == 0) {
                    onEntered_ll.setVisibility(View.VISIBLE);
                    onExit_ll.setVisibility(View.GONE);

                } else if (position == 1) {
                    onEntered_ll.setVisibility(View.GONE);
                    onExit_ll.setVisibility(View.VISIBLE);
                } else {
                    onEntered_ll.setVisibility(View.VISIBLE);
                    onExit_ll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        enteredSilent_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mainActivity.checkPer()) {
                        enteredSilent_switch.setChecked(false);
                        mainActivity.setPer();
                    }
                }
            }
        });
        exitSilent_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mainActivity.checkPer()) {
                        exitSilent_switch.setChecked(false);
                        mainActivity.setPer();
                    }

                }


            }
        });
        enteredLock_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!adminHelper.isActive()) {
                    builder1 = new AlertDialog.Builder(getActivity())
                            .setTitle("Admin").setMessage("If you want to enable this feature you need to allow admin permission. Thank you")
                            .setCancelable(true).setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adminHelper.intentToAdmin();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    dialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            enteredLock_cb.setChecked(false);
                        }
                    });

                }

            }
        });
        exitLock_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!adminHelper.isActive()) {
                    builder1 = new AlertDialog.Builder(getActivity())
                            .setTitle("Admin").setMessage("If you want to enable this feature you need to allow admin permission. Thank you")
                            .setCancelable(true).setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adminHelper.intentToAdmin();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    dialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            exitLock_cb.setChecked(false);
                        }
                    });

                }

            }
        });
        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Button clickec", Toast.LENGTH_SHORT).show();
                String title = messageEt.getText().toString();
                String geofenceType = spinner.getSelectedItem().toString();
                if (!isSpinnerListened[0]) {
                    geofenceType = MyAnnotations.ENTER;
                }
                String enSilent, enBluetooth, enWifi, enLock;
                String exSilent, exBluetooth, exWifi, exLock;
//                for entered
                if (enteredSilent_switch.isChecked()) {
                    enSilent = MyAnnotations.ON;
                } else {
                    enSilent = MyAnnotations.OFF;
                }
                if (enteredBluetooth_switch.isChecked()) {
                    enBluetooth = MyAnnotations.ON;

                } else {
                    enBluetooth = MyAnnotations.OFF;

                }
                if (enteredWifi_switch.isChecked()) {
                    enWifi = MyAnnotations.ON;

                } else {
                    enWifi = MyAnnotations.OFF;

                }
                if (enteredLock_cb.isChecked()) {
                    enLock = MyAnnotations.ON;

                } else {
                    enLock = MyAnnotations.OFF;

                }

                // for exit
                if (exitSilent_switch.isChecked()) {
                    exSilent = MyAnnotations.ON;
                } else {
                    exSilent = MyAnnotations.OFF;
                }
                if (exitBluetooth_switch.isChecked()) {
                    exBluetooth = MyAnnotations.ON;

                } else {
                    exBluetooth = MyAnnotations.OFF;

                }
                if (exitWifi_switch.isChecked()) {
                    exWifi = MyAnnotations.ON;

                } else {
                    exWifi = MyAnnotations.OFF;

                }
                if (exitLock_cb.isChecked()) {
                    exLock = MyAnnotations.ON;

                } else {
                    exLock = MyAnnotations.OFF;

                }
                long expirationTime = (long) numberPicker.getValue() *
                        NumbersAnnotation.HOUR_IN_MILLIS;
                long expirationEnd = timeUtils.getFromNow(expirationTime);
                if (title.length() != 0) {
                    if (geofenceType.matches(MyAnnotations.ENTER)) {
                        addMarkAndGeoFence(title,
                                latLng.latitude,
                                latLng.longitude,
                                geofenceCircle,
                                String.valueOf(expirationEnd),
                                expirationTime, MyAnnotations.ENTER,
                                String.valueOf(timeUtils.getNowMillis()),
                                enSilent,
                                enBluetooth,
                                enWifi,
                                enLock, null, null, null, null);
                        dialog.dismiss();
                    } else if (geofenceType.matches(MyAnnotations.EXIT)) {
                        addMarkAndGeoFence(title,
                                latLng.latitude,
                                latLng.longitude,
                                geofenceCircle,
                                String.valueOf(expirationEnd),
                                expirationTime, MyAnnotations.EXIT,
                                String.valueOf(timeUtils.getNowMillis()),
                                null, null, null, null
                                , exSilent, exBluetooth, exWifi, exLock);
                        dialog.dismiss();
                    } else {
                        addMarkAndGeoFence(title,
                                latLng.latitude,
                                latLng.longitude,
                                geofenceCircle,
                                String.valueOf(expirationEnd),
                                expirationTime, MyAnnotations.BOTH,
                                String.valueOf(timeUtils.getNowMillis()),
                                enSilent, enBluetooth, enWifi, enLock
                                , exSilent, exBluetooth, exWifi, exLock);
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(geoFencerHelper, "Please enter area title",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


//        spinner.setOnI(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                if (position==0)
//                {
//                    onEntered_ll.setVisibility(View.VISIBLE);
//                    onExit_ll.setVisibility(View.GONE);
//
//                }else if (position==1)
//                {
//                    onEntered_ll.setVisibility(View.GONE);
//                    onExit_ll.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    onEntered_ll.setVisibility(View.VISIBLE);
//                    onExit_ll.setVisibility(View.VISIBLE);
//                }
//
//            }
//        });
    }

    public boolean checkGeoFenceChangeDate() {
        Cursor cursor = myDatabase.retrieveDate();
        if (cursor.getCount() == 0) {
            myDatabase.insert(String.valueOf(timeUtils.getNowMillis()));
            return true;
        } else {
            while (cursor.moveToNext()) {
                long time = Long.parseLong(cursor.getString(1));
                if (time < timeUtils.get12AmMillis()) {
                    myDatabase.updateDate("0", String.valueOf(timeUtils.getNowMillis()));
                    return true;
                }
            }

        }
        return false;

    }

    public long getGeoFenceChangeTime() {
        long time = 0;
        Cursor cursor = myDatabase.retrieveDate();
        if (cursor.getCount() == 0) {
            return 0;
        } else

            while (cursor.moveToNext()) {
                time = Long.parseLong(cursor.getString(1));
            }

        return time;

    }

    public long getAmountOfUndoneInDate() {
        long time = 0;
        Cursor cursor = myDatabase.retrieve();
        if (cursor.getCount() == 0) {
            return 0;
        } else

            while (cursor.moveToNext()) {
                long date = Long.parseLong(cursor.getString(9));
                if (DateUtils.isToday(date)) {

                    time += 1;
                }
            }

        return time;

    }

}