package com.example.geofences.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.geofences.R;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.fragments.BottomSheetFragmentMain;
import com.example.geofences.fragments.MapsFragment;
import com.example.geofences.permissions.ThesePermissions;
import com.example.geofences.utils.AllActionsUtils;

public class MainActivity extends AppCompatActivity {

    private ThesePermissions thesePermissions;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thesePermissions = new ThesePermissions(MainActivity.this);
        myDatabase = new MyDatabase(MainActivity.this);


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
                startActivity(new Intent(MainActivity.this,HistoryActivity.class));
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

    public void showBottomSheetFragment()
    {
        BottomSheetFragmentMain bottomSheetFragmentMain = new BottomSheetFragmentMain();
        bottomSheetFragmentMain.show(getSupportFragmentManager(),"Main_bottom_sheet");

    }
}