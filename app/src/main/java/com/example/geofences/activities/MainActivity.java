package com.example.geofences.activities;


 import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;

 import androidx.appcompat.app.AppCompatActivity;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.geofences.R;
 import com.example.geofences.annotations.MYAnnotations;
 import com.example.geofences.fragments.MapsFragment;
import com.example.geofences.permissions.ThesePermissions;
 import com.example.geofences.utils.AllActionsUtils;

public class MainActivity extends AppCompatActivity {

    ThesePermissions thesePermissions;
    AllActionsUtils allActionsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thesePermissions = new ThesePermissions(MainActivity.this);
        allActionsUtils = new AllActionsUtils(MainActivity.this);
        Button bluetoothOnOff = findViewById(R.id.bluetoothOnOff);
        Button audioButton = findViewById(R.id.audioButton);
        bluetoothOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allActionsUtils.setBluetoothOnOff();
            }
        });
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allActionsUtils.setAudioMode(MYAnnotations.RINGER_MODE_SILENT);
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
}