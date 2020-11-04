package com.example.geofences.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geofences.activities.MainActivity;
import com.example.geofences.annotations.PermissionCodes;


public class ThesePermissions {

    Context context;

    public ThesePermissions(Context context) {
        this.context = context;
    }


    public boolean permission() {
        int locationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int bluetoothAdminPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH_ADMIN);
        int bluetoothPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH);
        int courseLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int bLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        boolean b = locationPermission == PackageManager.PERMISSION_GRANTED
                && courseLocationPermission == PackageManager.PERMISSION_GRANTED
                && bluetoothAdminPermission == PackageManager.PERMISSION_GRANTED
                && bluetoothPermission == PackageManager.PERMISSION_GRANTED;

        String[] manifestPermissionArray = new String[0];


        manifestPermissionArray = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.BLUETOOTH_ADMIN
                , Manifest.permission.BLUETOOTH,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY};


        //if device is android 10 or higher we need to take background location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context
                    , Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showDialog("Permission", "without Geofence background location permission app can not access your entrance and exit from/to target location");

            }
            if (b) {
                return true;
            } else {
                ActivityCompat.requestPermissions((AppCompatActivity) context,
                        manifestPermissionArray, PermissionCodes.REQ_CODE);
                return false;
            }
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//
//        }
        else if (b){

            return true;
        } else
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    manifestPermissionArray,PermissionCodes.REQ_CODE);
        return false;

    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title).setMessage(message).setCancelable(true);
        adb.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]
                            {Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PermissionCodes.BACKGROUND_REQ_CODE);
                    dialog.dismiss();
                }


            }
        }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog dialog = adb.create();
        dialog.show();
    }

    public  boolean checkSystemWritePermission(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
             permission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_SETTINGS)
                    == PackageManager.PERMISSION_GRANTED;
        }
       return permission;
    }
    public void openAndroidPermissionsMenu(Activity context) {

        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.WRITE_SETTINGS},
                        PermissionCodes.CODE_WRITE_SETTINGS_PERMISSION);
            }


    }
}
