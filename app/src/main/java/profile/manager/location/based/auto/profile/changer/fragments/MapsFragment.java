package profile.manager.location.based.auto.profile.changer.fragments;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import profile.manager.location.based.auto.profile.changer.R;
import profile.manager.location.based.auto.profile.changer.activities.HistoryActivity;
import profile.manager.location.based.auto.profile.changer.activities.MainActivity;
import profile.manager.location.based.auto.profile.changer.activities.MySplashActivity;
import profile.manager.location.based.auto.profile.changer.activities.SettingsActivity;
import profile.manager.location.based.auto.profile.changer.adapters.HelpPagerAdapter;
import profile.manager.location.based.auto.profile.changer.annotations.MyAnnotations;
import profile.manager.location.based.auto.profile.changer.annotations.NumbersAnnotation;
import profile.manager.location.based.auto.profile.changer.broadcasts.Admin;
import profile.manager.location.based.auto.profile.changer.database.MyDatabase;
import profile.manager.location.based.auto.profile.changer.geofencer.GeoFencerHelper;
import profile.manager.location.based.auto.profile.changer.permissions.ThesePermissions;
import profile.manager.location.based.auto.profile.changer.sharedpreferences.MyPreferences;
import profile.manager.location.based.auto.profile.changer.utils.AdminHelper;
import profile.manager.location.based.auto.profile.changer.utils.AllActionsUtils;
import profile.manager.location.based.auto.profile.changer.utils.TimeUtil;
import profile.manager.location.based.auto.profile.changer.utils.Utils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapsFragment extends Fragment implements LocationListener {
    private GeofencingClient geofencingClient;
    private GoogleMap map;
    private LocationManager locationManager;
    private ThesePermissions thesePermissions;
    private MyPreferences preferences;
    private MyDatabase myDatabase;
    private GeoFencerHelper geoFencerHelper;
    private TimeUtil timeUtils;
    private TextView geoFencesAmountTv;
    private final long geoFencesLimit = 20;
    AlertDialog.Builder builder1;
    private MainActivity mainActivity;
    NotificationManager mNotificationManager;
    AlertDialog dialog1;
    AllActionsUtils allActionsUtils;
    MyPreferences myPreferences;

    SearchView search_sv;
    TextView geoFenceType_tv;
    TextView circle_tv;
    TextView ringMode_tv, exRingMode_tv;
    ImageView meters_iv;
    private float geofenceCircle = NumbersAnnotation.BY_WALK;
    String geofenceType = MyAnnotations.ENTER;
    String enSilent = MyAnnotations.NULL;
    String exSilent = MyAnnotations.NULL;
    Context context;

    Utils utils;

    ConstraintLayout onEntered_cl;
    ConstraintLayout onExit_cl;
    AdminHelper adminHelper;
    View help_map_layout;
    View view;
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
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(context),
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
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveToCurrentLocation(latLng);
            }
            if (myDatabase.retrieveRowsAmount() != 0) {
                if (preferences.getBoolean(MyAnnotations.IS_BOOT_COMPLETED, false)) {
                    //add geo_fences again if user want to re-add undone geo_fences
                    onBootComplete(context, true);

                } else {
                    //just set mark and circle.do not add geo_fences
                    setFromDbToGeoFenceAgain(false);
                }
            }
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (getGeoFenceLimit() < geoFencesLimit) {
                        addGeoDialog(latLng);
                    } else {
                        Toast.makeText(context, "Limit is reached", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (!isLocationEnabled(context)) {
                        showDialog("Location", "To move on your current location you have to ON the device location.");
                    }
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
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        geoFencesAmountTv = view.findViewById(R.id.geo_fences_amount_tv);
        ImageView history_iv = view.findViewById(R.id.history_iv);
        search_sv = view.findViewById(R.id.search_sv);
        context = getContext();
        adminHelper = new AdminHelper(context);

        preferences = new MyPreferences(context);
        mainActivity = ((MainActivity) context);
        thesePermissions = new ThesePermissions(context);
        allActionsUtils = new AllActionsUtils(context);
        geofencingClient = LocationServices.getGeofencingClient(context);
        geoFencerHelper = new GeoFencerHelper(context);
        myDatabase = new MyDatabase(context);
        utils = new Utils(context);
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        myPreferences = new MyPreferences(context);
        // set listeners

        //set value
        timeUtils = new TimeUtil();
        // check time if its 12am then change the time and renew the limit of geofences

        if (checkGeoFenceChangeDate()) {
            geoFencesAmountTv.setText(String.valueOf(geoFencesLimit));
        } else {
            geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - getGeoFenceLimit()));
        }

        history_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupWindow = menuPopupMenu();
                popupWindow.showAsDropDown(history_iv, 0, 0);
            }
        });

        search_sv.setQueryHint("address");
        search_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String location) {

                if (allActionsUtils.isWifiEnable()) {
                    List<Address> addressList = new ArrayList<>();

                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(context);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (addressList != null) {
                            if (!addressList.isEmpty()) {
                                Address address = addressList.get(0);

                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                //                map.addMarker(new MarkerOptions().position(latLng).title(location));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//                Toast.makeText(getApplicationContext(), address.getLatitude() + " " +
//                        address.getLongitude(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(geoFencerHelper, "Not Found", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } else {
                        Toast.makeText(context, "Please type location name", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add to database add mark to map draw circle and add geofence
    private void addMarkAndGeoFence(boolean newAdd, int idd, String title,
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
                                    String exScreenLock) {
        if (getGeoFenceLimit() < geoFencesLimit) {
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
            int id = 0;
            Cursor cursor = myDatabase.retrieve();
            if (!newAdd) {
                id = idd;
            } else {
                if (insert != -1) {
                    if (cursor.getCount() != 0) {
                        cursor.moveToLast();
                        id = Integer.parseInt(cursor.getString(0));
                    }
                }

            }


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

//                if (geoType.matches(MyAnnotations.ENTER)) {
                addGeofence(id, title, location, geofenceCircle, geoType /*MyAnnotations.ENTER*/,
                        String.valueOf(insert), expirationTime);

//                } else if (geoType.matches(MyAnnotations.EXIT)) {
//                    addGeofence(id, location, geofenceCircle, MyAnnotations.EXIT,
//                    String.valueOf(insert), expirationTime);
//                } else {
//                    addGeofence(id, location, geofenceCircle, MyAnnotations.BOTH,
//                    String.valueOf(insert), expirationTime);
//                }

            } else {
                Toast.makeText(geoFencerHelper, "Error", Toast.LENGTH_SHORT).show();
            }
            //otherwise just add mark
        } else {
            Toast.makeText(geoFencerHelper, "Limit is reached", Toast.LENGTH_SHORT).show();
        }
    }


    // add geo fence
    private void addGeofence(int id, String title, LatLng latLng, float radius, String transitionType,
                             String geofence_id, long expirationTime) {
        Geofence geofence = geoFencerHelper.getGeofence(geofence_id
                , latLng, radius, transitionType, expirationTime);

        GeofencingRequest geofencingRequest = geoFencerHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFencerHelper.getPendingIntent(id);
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(context),
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
                        addMarkAndMoveCamera(latLng, title);
                        addCircleOfGeofence(latLng, geofenceCircle);
                        myPreferences.addLong(MyAnnotations.GEO_FENCE_LIMIT,
                                myPreferences.getLong(MyAnnotations.GEO_FENCE_LIMIT, 0)
                                        + 1);
                        geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - getGeoFenceLimit()));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, geoFencerHelper.getErrorMessage(e),
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

    private void moveToCurrentLocation(LatLng latLng) {
        //                map.addMarker(new MarkerOptions().position(latLng).title(location));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    }

    private void enableUserCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
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
    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title).setMessage(message).setCancelable(true);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
        AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(context)
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
                        myPreferences.setBoolean("IS_BOOT_COMPLETED", false);
                        geoFencesAmountTv.setText(String.valueOf(geoFencesLimit - getGeoFenceLimit()));

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
                            addMarkAndGeoFence(true, Integer.parseInt(id), title,
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
                            addCircleOfGeofence(location, Float.parseFloat(radius));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view).setCancelable(true);

        TextView titleTv = view.findViewById(R.id.dialogTitle_tv);
        EditText messageEt = view.findViewById(R.id.input_et);
        TextView negativeTv = view.findViewById(R.id.negative_tv);
        TextView positiveTv = view.findViewById(R.id.positive_tv);
        TextView enWifi_tv = view.findViewById(R.id.enWifi_tv);
        TextView exWifi_tv = view.findViewById(R.id.exWifi_tv);

        NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        onEntered_cl = view.findViewById(R.id.onEntered_cl);
        onExit_cl = view.findViewById(R.id.onExit_cl);

        CardView circleSize_cv = view.findViewById(R.id.circleSize_cv);
        circle_tv = view.findViewById(R.id.circle_tv);
        meters_iv = view.findViewById(R.id.meters_iv);

        CardView geofenceType_cl = view.findViewById(R.id.geofenceType_cl);
        geoFenceType_tv = view.findViewById(R.id.geoFenceType_tv);

        CardView enRingMode_cv = view.findViewById(R.id.enRingMode_cv);
        ringMode_tv = view.findViewById(R.id.ringMode_tv);
        CardView exRingMode_cv = view.findViewById(R.id.exRingMode_cv);
        exRingMode_tv = view.findViewById(R.id.exRingMode_tv);


        SwitchCompat enteredBluetooth_switch = view.findViewById(R.id.enteredBluetooth_switch);
        SwitchCompat enteredWifi_switch = view.findViewById(R.id.enteredWifi_switch);
        CheckBox enteredLock_cb = view.findViewById(R.id.enteredLock_cb);

        SwitchCompat exitBluetooth_switch = view.findViewById(R.id.exitBluetooth_switch);
        SwitchCompat exitWifi_switch = view.findViewById(R.id.exitWifi_switch);
        CheckBox exitLock_cb = view.findViewById(R.id.exitLock_cb);

        //
        messageEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        messageEt.setSingleLine(true);
        onEntered_cl.setVisibility(View.VISIBLE);
        onExit_cl.setVisibility(View.GONE);
        if (utils.isAboveP()) {
            exWifi_tv.setVisibility(View.GONE);
            enWifi_tv.setVisibility(View.GONE);
            exitWifi_switch.setVisibility(View.GONE);
            enteredWifi_switch.setVisibility(View.GONE);
        } else {
            exWifi_tv.setVisibility(View.VISIBLE);
            enWifi_tv.setVisibility(View.VISIBLE);
            exitWifi_switch.setVisibility(View.VISIBLE);
            enteredWifi_switch.setVisibility(View.VISIBLE);
        }


        titleTv.setText("Area Name");
        negativeTv.setText("Dismiss");
        positiveTv.setText("Done");

        numberPicker.setMaxValue(24);
        numberPicker.setMinValue(1);
        String[] values = new String[24];
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {

                values[i] = i + 1 + " hr";
            } else {
                values[i] = i + 1 + " hrs";
            }

        }
        numberPicker.setDisplayedValues(values);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerTextColor(numberPicker);

        //variables


        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setTop(100);
        dialog.getWindow().getDecorView().setBottom(100);
        dialog.show();

        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        circleSize_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupWindow = circleSizePopupMenu();
                popupWindow.showAsDropDown(circleSize_cv);
            }
        });

        geofenceType_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupWindow = geoFencePopupMenu();
                popupWindow.showAsDropDown(geofenceType_cl, 0, 0);
            }
        });

        enRingMode_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                        doNoDisturbPermissionDialog();
                    } else {


                        try {
                            PopupWindow popupWindow = ringingModePopupMenu(true);
                            popupWindow.showAsDropDown(enRingMode_cv, 0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
//                            PopupWindow popupWindow = ringingModePopupMenu(true);
//                            popupWindow.showAsDropDown(enRingMode_cv, 0, 0);
                        }
                    }

                } else {
                    try {
                        PopupWindow popupWindow = ringingModePopupMenu(true);
                        popupWindow.showAsDropDown(enRingMode_cv, 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        PopupWindow popupWindow = ringingModePopupMenu(true);
//                        popupWindow.showAsDropDown(enRingMode_cv, 0, 0);
                    }

                }
            }
        });
        exRingMode_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                        doNoDisturbPermissionDialog();
                    } else {

                        try {
                            PopupWindow popupWindow = ringingModePopupMenu(false);
                            popupWindow.showAsDropDown(exRingMode_cv, 0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
//                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            },2000);
//                            PopupWindow popupWindow = ringingModePopupMenu(false);
//                            popupWindow.showAsDropDown(exRingMode_cv, 0, 0);
                        }


                    }
                } else {
                    try {
                        PopupWindow popupWindow = ringingModePopupMenu(false);
                        popupWindow.showAsDropDown(exRingMode_cv, 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        PopupWindow popupWindow = ringingModePopupMenu(false);
//                        popupWindow.showAsDropDown(exRingMode_cv, 0, 0);
                    }

                }
            }
        });
//        enRingMode_cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
//                        doNoDisturbPermissionDialog();
//                    } else {
//
//                        PopupWindow popupWindow = ringingModePopupMenu(true);
//                        popupWindow.showAsDropDown(enRingMode_cv, 0, 0);
//
//
//                    }
//                }
//            }
//        });


        enteredLock_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!adminHelper.isActive()) {
                    builder1 = new AlertDialog.Builder(context)
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
                    dialog1 = builder1.create();
                    dialog1.show();
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
                    builder1 = new AlertDialog.Builder(getContext())
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
                    dialog1 = builder1.create();
                    dialog1.show();
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

                String title = messageEt.getText().toString();


                String enBluetooth, enWifi, enLock;
                String exBluetooth, exWifi, exLock;
//                for entered

                if (enteredBluetooth_switch.isChecked()) {
                    enBluetooth = MyAnnotations.ON;

                } else {
                    enBluetooth = MyAnnotations.OFF;

                }

                if (utils.isAboveP()) {
                    enWifi = MyAnnotations.NULL;
                } else {
                    if (enteredWifi_switch.isChecked()) {
                        enWifi = MyAnnotations.ON;

                    } else {
                        enWifi = MyAnnotations.OFF;

                    }
                }

                if (enteredLock_cb.isChecked()) {
                    enLock = MyAnnotations.ON;

                } else {
                    enLock = MyAnnotations.OFF;

                }

                // for exit
                if (exitBluetooth_switch.isChecked()) {
                    exBluetooth = MyAnnotations.ON;

                } else {
                    exBluetooth = MyAnnotations.OFF;

                }

                if (utils.isAboveP()) {
                    exWifi = MyAnnotations.NULL;

                } else {

                    if (exitWifi_switch.isChecked()) {
                        exWifi = MyAnnotations.ON;

                    } else {
                        exWifi = MyAnnotations.OFF;

                    }
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
                    if (thesePermissions.permission()) {
                        if (isLocationEnabled(getContext())) {
                            if (geofenceType.matches(MyAnnotations.ENTER)) {
                                addMarkAndGeoFence(true, 0, title,
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
                                addMarkAndGeoFence(true, 0, title,
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
                                addMarkAndGeoFence(true, 0, title,
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
                            showDialog("Location", "you need to allow location.");
                        }
                    }

                } else {
                    Toast.makeText(geoFencerHelper, "Please enter area title",
                            Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    public boolean checkGeoFenceChangeDate() {
        Cursor cursor = myDatabase.retrieveDate();
        if (cursor.getCount() == 0) {
            long i = myDatabase.insert(String.valueOf(timeUtils.get12AmMillis()));
            myPreferences.addLong(MyAnnotations.GEO_FENCE_LIMIT, (long) 0);
            return true;
        }

        while (cursor.moveToNext()) {
            long time = Long.parseLong(cursor.getString(1));
            long currentTime = timeUtils.getNowMillis();
            String stime = timeUtils.getFormattedTime(time);
            String scurrentTime = timeUtils.getFormattedTime(currentTime);
            if (currentTime > time) {

                String date12crrentTime = timeUtils.getFormattedTime(timeUtils.get12AmMillis());
                myDatabase.updateDate("0", String.valueOf(timeUtils.get12AmMillis()));
                myPreferences.addLong(MyAnnotations.GEO_FENCE_LIMIT, (long) 0);
                return true;
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

    public long getGeoFenceLimit() {

        return myPreferences.getLong(MyAnnotations.GEO_FENCE_LIMIT, 0);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }

    public void doNoDisturbPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(context))
                .setTitle("Do not disturb").setMessage(MyAnnotations.DO_NOT_DISTURB_MESSAGE)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                                // Check if the notification policy access has been granted for the app.
                                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                            }

                        }
                    }
                }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private PopupWindow menuPopupMenu() {
        PopupWindow menuPopupMenu = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menu_view, null);

        ConstraintLayout history_cl = view.findViewById(R.id.history_cl);
        ConstraintLayout settings_cl = view.findViewById(R.id.settings_cl);
        ConstraintLayout unInstall_cl = view.findViewById(R.id.unInstall_cl);
        ConstraintLayout help_cl = view.findViewById(R.id.help_cl);

        menuPopupMenu.setFocusable(true);
        menuPopupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        menuPopupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        menuPopupMenu.setContentView(view);
        menuPopupMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuPopupMenu.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        history_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(context, HistoryActivity.class));

                ((MainActivity) context).Load_withAds(false, null, new HistoryActivity());
                menuPopupMenu.dismiss();
            }
        });
        settings_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(context, SettingsActivity.class));
                ((MainActivity) context).Load_withAds(false, null, new SettingsActivity());
//                loadActivityWithAds(context, new SettingsActivity());

                menuPopupMenu.dismiss();
            }
        });
        unInstall_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Uninstall")
                        .setMessage("The application clear all data of this app and it will be uninstalled")
                        .setPositiveButton("Uninstall", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DevicePolicyManager devicePolicyManager = (DevicePolicyManager)
                                        getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
                                ComponentName compName = new ComponentName(getContext(), Admin.class);
                                try {

                                    devicePolicyManager.removeActiveAdmin(compName);
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }

                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                startActivity(intent);
                                menuPopupMenu.dismiss();

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
        });
        help_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpScreen();
                menuPopupMenu.dismiss();

            }
        });


        return menuPopupMenu;
    }


    private PopupWindow circleSizePopupMenu() {
        PopupWindow  circleSizePopupMenu = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.circle_size_popup_menu_view, null);

        ConstraintLayout byWalk_cl = view.findViewById(R.id.byWalk_cl);
        ConstraintLayout byCycle_cl = view.findViewById(R.id.byCycle_cl);
        ConstraintLayout byBus_cl = view.findViewById(R.id.byBus_cl);
        ConstraintLayout byCar_cl = view.findViewById(R.id.byCar_cl);

        circleSizePopupMenu.setFocusable(true);
        circleSizePopupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        circleSizePopupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        circleSizePopupMenu.setContentView(view);
        circleSizePopupMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        circleSizePopupMenu.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        byWalk_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_tv.setText(MyAnnotations.BY_WALK);
                geofenceCircle = NumbersAnnotation.BY_WALK;
                meters_iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_man, null));
                circleSizePopupMenu.dismiss();
            }
        });
        byCycle_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_tv.setText(MyAnnotations.BY_CYCLE);
                geofenceCircle = NumbersAnnotation.BY_CYCLE;
                meters_iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_bicycle, null));
                circleSizePopupMenu.dismiss();

            }
        });
        byBus_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_tv.setText(MyAnnotations.BY_BUS);
                geofenceCircle = NumbersAnnotation.BY_BUS;
                meters_iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_bus, null));
                circleSizePopupMenu.dismiss();

            }
        });
        byCar_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_tv.setText(MyAnnotations.BY_CAR);
                geofenceCircle = NumbersAnnotation.BY_CAR;
                meters_iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_car_alt, null));
                circleSizePopupMenu.dismiss();

            }
        });
        return circleSizePopupMenu;
    }



    private PopupWindow ringingModePopupMenu(boolean entered) {

        PopupWindow ringingPopupWindow = new PopupWindow(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ringging_mode_view, null);

        ConstraintLayout ringing_cl = view.findViewById(R.id.ringing_cl);
        ConstraintLayout silent_cl = view.findViewById(R.id.silent_cl);
        ConstraintLayout vibrate_cl = view.findViewById(R.id.vibrate_cl);

        ringingPopupWindow.setFocusable(true);
        ringingPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        ringingPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        ringingPopupWindow.setContentView(view);
        ringingPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ringingPopupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        ringing_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (entered) {

                    enSilent = MyAnnotations.RINGER_MODE_NORMAL;
                    ringMode_tv.setText(MyAnnotations.RINGING);
                } else {
                    exSilent = MyAnnotations.RINGER_MODE_NORMAL;
                    exRingMode_tv.setText(MyAnnotations.RINGING);

                }
                ringingPopupWindow.dismiss();
            }
        });
        silent_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entered) {

                    enSilent = MyAnnotations.RINGER_MODE_SILENT;
                    ringMode_tv.setText(MyAnnotations.SILENT);

                } else {
                    exSilent = MyAnnotations.RINGER_MODE_SILENT;
                    exRingMode_tv.setText(MyAnnotations.SILENT);

                }
                ringingPopupWindow.dismiss();
            }
        });
        vibrate_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entered) {
                    enSilent = MyAnnotations.RINGER_MODE_VIBRATE;
                    ringMode_tv.setText(MyAnnotations.VIBRATE);

                } else {
                    exSilent = MyAnnotations.RINGER_MODE_VIBRATE;
                    exRingMode_tv.setText(MyAnnotations.VIBRATE);
                }
                ringingPopupWindow.dismiss();

            }
        });

        return ringingPopupWindow;
    }


    private PopupWindow geoFencePopupMenu() {
        PopupWindow  geoFencePopupMenu = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.geo_fence_popup_view, null);

        NumberPicker numberPicker = view.findViewById(R.id.geo_fenceType_np);

        String[] type = new String[]{MyAnnotations.ENTER, MyAnnotations.EXIT, MyAnnotations.BOTH};
        numberPicker.setMaxValue(3);
        numberPicker.setMinValue(1);

        geoFencePopupMenu.setFocusable(true);
        geoFencePopupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        geoFencePopupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        geoFencePopupMenu.setContentView(view);
        geoFencePopupMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        geoFencePopupMenu.setElevation(5.0f);
        geoFencePopupMenu.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        numberPicker.setDisplayedValues(type);
        setNumberPickerTextColor(numberPicker);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int s = picker.getValue();
                if (s == 1) {
                    onEntered_cl.setVisibility(View.VISIBLE);
                    onExit_cl.setVisibility(View.GONE);
                    geofenceType = MyAnnotations.ENTER;
                    geoFenceType_tv.setText(MyAnnotations.ENTER);

                } else if (s == 2) {
                    onEntered_cl.setVisibility(View.GONE);
                    onExit_cl.setVisibility(View.VISIBLE);
                    geofenceType = MyAnnotations.EXIT;
                    geoFenceType_tv.setText(MyAnnotations.EXIT);

                } else {
                    onEntered_cl.setVisibility(View.VISIBLE);
                    onExit_cl.setVisibility(View.VISIBLE);
                    geofenceType = MyAnnotations.BOTH;
                    geoFenceType_tv.setText(MyAnnotations.BOTH);

                }
            }
        });


        return geoFencePopupMenu;
    }


    public void setNumberPickerTextColor(NumberPicker numberPicker) {
        //change color of of number picker
        int color = Color.parseColor("#ffffff");

        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField =
                            numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                } catch (NoSuchFieldException e) {
                    Log.e("setNumberPickerColor1", "" + e);
                } catch (IllegalAccessException e) {
                    Log.e("setNumberPickerColor2", "" + e);
                } catch (IllegalArgumentException e) {
                    Log.e("setNumberPickerColor3", "" + e);
                }
            }
        }
    }

    public void helpScreen() {
        help_map_layout = view.findViewById(R.id.help_map_layout);
        help_map_layout.setVisibility(View.VISIBLE);
        ViewPager viewPager = view.findViewById(R.id.helpViewPager);
        viewPager.setAdapter(new HelpPagerAdapter(getContext()));

        TextView next_tv = view.findViewById(R.id.next_tv);
        TextView help_one_tv = view.findViewById(R.id.help_one_tv);
        TextView help_two_tv = view.findViewById(R.id.help_two_tv);
        TextView help_three_tv = view.findViewById(R.id.help_three_tv);
        TextView back_tv = view.findViewById(R.id.back_tv);
        back_tv.setVisibility(View.INVISIBLE);
        //initial stage
        final int[] screen = {0};
        help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.black_circle_bg, null));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    back_tv.setVisibility(View.INVISIBLE);
                    next_tv.setText(MyAnnotations.next);
                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    screen[0] = 0;

                } else if (position == 1) {
                    screen[0] = 1;
                    back_tv.setVisibility(View.VISIBLE);
                    next_tv.setText(MyAnnotations.next);
                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));


                } else {
                    back_tv.setVisibility(View.VISIBLE);
                    screen[0] = 2;
                    next_tv.setText(MyAnnotations.done);
                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // after action
        next_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screen[0] == 0) {
                    viewPager.setCurrentItem(1);
                    back_tv.setVisibility(View.VISIBLE);
                    screen[0] = 1;
                    next_tv.setText(MyAnnotations.next);
                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));

                } else if (screen[0] == 1) {
                    viewPager.setCurrentItem(2);

                    screen[0] = 2;
                    next_tv.setText(MyAnnotations.done);
                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));

                } else {
                    if (next_tv.getText().toString().matches(MyAnnotations.done)) {
                        help_map_layout.setVisibility(View.GONE);
                    }
                }
            }
        });
        back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screen[0] == 2) {
                    screen[0] = 1;
                    viewPager.setCurrentItem(1);

                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));


                } else if (screen[0] == 1) {
                    screen[0] = 0;
                    viewPager.setCurrentItem(0);
                    back_tv.setVisibility(View.INVISIBLE);
                    help_one_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.black_circle_bg, null));
                    help_two_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));
                    help_three_tv.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.trans_black_circle_bg, null));


                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getContext();
    }
}