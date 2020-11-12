package com.example.geofences.activities;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.geofences.R;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.annotations.PermissionCodes;
import com.example.geofences.broadcasts.Admin;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.fragments.BottomSheetFragmentMain;
import com.example.geofences.fragments.MapsFragment;
import com.example.geofences.permissions.ThesePermissions;
import com.example.geofences.utils.AllActionsUtils;

public class MainActivity extends AppCompatActivity {

    private ThesePermissions thesePermissions;
    private MyDatabase myDatabase;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    NotificationManager notificationManager;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thesePermissions = new ThesePermissions(MainActivity.this);
        myDatabase = new MyDatabase(MainActivity.this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, Admin.class);
        BottomSheetFragmentMain bottomSheetFragmentMain = new BottomSheetFragmentMain();

//        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                bottomSheetFragmentMain.dismiss();
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return false;
//            }
//        });

        enablePhone();// ask for admin permission
        ImageView history_iv = findViewById(R.id.history_iv);
        ConstraintLayout bottomSheetStartCl = findViewById(R.id.bottomSheetStart_cl);

        if (bottomSheetFragmentMain.isHidden()) {
            bottomSheetStartCl.setVisibility(View.VISIBLE);
        } else {
            bottomSheetStartCl.setVisibility(View.VISIBLE);

        }
        bottomSheetStartCl.performClick();
        bottomSheetStartCl.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bottomSheetFragmentMain.show(getSupportFragmentManager(), "Main_bottom_sheet");

                }
                return true;
            }
        });
//        bottomSheetStartCl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetFragmentMain.show(getSupportFragmentManager(), "Main_bottom_sheet");
//
//            }
//        });


    }

    public void loadFragments(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (thesePermissions.permission()) {
            loadFragments(new MapsFragment());
        }
    }


    public void enablePhone(/*View view*/) {
        boolean active = devicePolicyManager.isAdminActive(compName);
        if (active) {
//            devicePolicyManager.removeActiveAdmin( compName ) ;
//            btnEnable .setText( "Enable" ) ;
//            btnLock .setVisibility(View. GONE ) ;
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!");
            startActivity(intent /*, PermissionCodes.ADMIN_RESULT_ENABLE*/);
        }
    }

    public void lockPhone(/*View view*/) {
        boolean active = devicePolicyManager.isAdminActive(compName);
        if (active) {
//            devicePolicyManager.removeActiveAdmin( compName ) ;
//            btnEnable .setText( "Enable" ) ;
//            btnLock .setVisibility(View. GONE ) ;
            devicePolicyManager.lockNow();
        } else {
            Toast.makeText(this, "App is not activated for device admin", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        exitt();
        super.onBackPressed();
    }

    public void exitt() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint("InflateParams") final View dialogView =
                    layoutInflater.inflate(R.layout.exit_layout, null);
            ConstraintLayout yes_cl = dialogView.findViewById(R.id.yes_cl);
            ConstraintLayout no_cl = dialogView.findViewById(R.id.no_cl);
            ConstraintLayout rateUs_cl = dialogView.findViewById(R.id.rateUs_cl);


            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();

            yes_cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     alertDialog.cancel();
                    MainActivity.this.finishAffinity();
                }
            });

            no_cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//
                    alertDialog.dismiss();

                }
            });
            rateUs_cl.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 MainActivity.this.rate();
                                             }
                                         }
            );

        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    public void rate() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
    }

}