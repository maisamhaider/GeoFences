package com.example.geofences.activities;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thesePermissions = new ThesePermissions(MainActivity.this);
        myDatabase = new MyDatabase(MainActivity.this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, Admin.class);

        enablePhone();
        ImageView history_iv = findViewById(R.id.history_iv);
        Button showBottomSheet = findViewById(R.id.showBottomSheet);

        showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetFragment();
            }
        });
        history_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

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

    public void showBottomSheetFragment() {
        BottomSheetFragmentMain bottomSheetFragmentMain = new BottomSheetFragmentMain();
        bottomSheetFragmentMain.show(getSupportFragmentManager(), "Main_bottom_sheet");

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
        }
        else
        {
            Toast.makeText(this, "App is not activated for device admin", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkPer() {
        return thesePermissions.checkSystemWritePermission(MainActivity.this); }

    public void setPer() {
         thesePermissions.openAndroidPermissionsMenu(MainActivity.this);
    }
}