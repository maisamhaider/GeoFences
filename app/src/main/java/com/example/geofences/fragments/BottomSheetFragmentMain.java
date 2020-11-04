package com.example.geofences.fragments;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.geofences.R;
import com.example.geofences.activities.MainActivity;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.utils.AllActionsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragmentMain extends BottomSheetDialogFragment {

    private AllActionsUtils allActionsUtils;

    public BottomSheetFragmentMain() {
        // Required empty public constructor
    }


    public static BottomSheetFragmentMain newInstance() {

        return new BottomSheetFragmentMain();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetFragmentMain.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_main, container, false);
        allActionsUtils = new AllActionsUtils(getActivity());


        Button bluetoothOnOff = view.findViewById(R.id.bluetoothOnOff);
        Button audioButton = view.findViewById(R.id.audioButton);
        Button airplane = view.findViewById(R.id.airplane);
        Button lockScreen = view.findViewById(R.id.lockScreen);
        bluetoothOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allActionsUtils.setBluetoothOnOff(true);
            }
        });
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_SILENT);
            }
        });
        airplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allActionsUtils.SetAirplaneMode(true);
            }
        });
        airplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              }
        });
        lockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).lockPhone();
              }
        });
        return view;
    }


}