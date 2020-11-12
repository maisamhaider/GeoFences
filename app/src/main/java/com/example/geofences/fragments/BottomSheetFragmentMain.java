package com.example.geofences.fragments;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.geofences.R;
import com.example.geofences.activities.MainActivity;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.utils.AllActionsUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragmentMain extends BottomSheetDialogFragment {
    NotificationManager mNotificationManager;
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

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
             }
        });
        AdView adView =view.findViewById(R.id.adView);
        adView(adView);
        allActionsUtils = new AllActionsUtils(getActivity());
        mNotificationManager = (NotificationManager)
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        ImageView wifiIv = view.findViewById(R.id.wifi_iv);
        ImageView bluetoothIv = view.findViewById(R.id.bluetooth_iv);
        ImageView ringModeIv = view.findViewById(R.id.ringMode_iv);
        ImageView lockScreenIv = view.findViewById(R.id.lockScreen_iv);

        if (allActionsUtils.isWifiEnable()) {
            wifiIv.setBackgroundResource(R.drawable.rounded_rectangle_selected);
            wifiIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_round_wifi, null));
        } else {
            wifiIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_round_wifi_off, null));
            wifiIv.setBackgroundResource(R.drawable.ic_rounded_rectangle);
        }
        if (allActionsUtils.isBluetoothOn()) {
            bluetoothIv.setBackgroundResource(R.drawable.rounded_rectangle_selected);
            bluetoothIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                    , R.drawable.ic_baseline_bluetooth, null));
        } else {
            bluetoothIv.setBackgroundResource(R.drawable.ic_rounded_rectangle);
            bluetoothIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_bluetooth_disabled, null));
        }


        if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_NORMAL)) {
            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                    , R.drawable.ic_baseline_volume_up, null));
        } else if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_SILENT)) {
            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_volume_mute,
                    null));
        } else if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.ADJUST_MUTE)) {
            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_volume_mute,
                    null));
        } else {
            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_round_vibration, null));
        }


        wifiIv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (allActionsUtils.isWifiEnable()) {
                    allActionsUtils.setWifiOnOff(false);
                    wifiIv.setBackgroundResource(R.drawable.ic_rounded_rectangle);
                    wifiIv.setImageDrawable(getActivity().getResources()
                            .getDrawable(R.drawable.ic_round_wifi_off));

                } else {
                    allActionsUtils.setWifiOnOff(true);
                    wifiIv.setBackgroundResource(R.drawable.rounded_rectangle_selected);
                    wifiIv.setImageDrawable(getActivity().getResources()
                            .getDrawable(R.drawable.ic_round_wifi));
                }
            }
        });

        bluetoothIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!allActionsUtils.isBluetoothOn()) {
                    allActionsUtils.setBluetoothOnOff(true);
                    bluetoothIv.setBackgroundResource(R.drawable.rounded_rectangle_selected);
                    bluetoothIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_baseline_bluetooth, null));
                } else {
                    allActionsUtils.setBluetoothOnOff(false);
                    bluetoothIv.setBackgroundResource(R.drawable.ic_rounded_rectangle);
                    bluetoothIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_baseline_bluetooth_disabled, null));
                }
            }
        });
        ringModeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                        doNoDisturbPermissionDialog();
                    } else {
                        if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_NORMAL)) {
                            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                                    , R.drawable.ic_round_vibration,
                                    null));
                            allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_VIBRATE);

                        } else if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_VIBRATE)) {
                            ringModeIv.setImageDrawable(ResourcesCompat.
                                    getDrawable(getResources()
                                            , R.drawable.ic_baseline_volume_mute,
                                            null));
                            allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_SILENT);

                        } else {
                            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                                    , R.drawable.ic_baseline_volume_up,
                                    null));
                            allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_NORMAL);

                        }
                    }
                } else {
                    if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_NORMAL)) {
                        ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                                , R.drawable.ic_round_vibration,
                                null));
                        allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_VIBRATE);

                    } else if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_VIBRATE)) {
                        ringModeIv.setImageDrawable(ResourcesCompat.
                                getDrawable(getResources()
                                        , R.drawable.ic_baseline_volume_mute,
                                        null));
                        allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_SILENT);

                    } else {
                        ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                                , R.drawable.ic_baseline_volume_up,
                                null));
                        allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_NORMAL);

                    }
                }

            }
        });

        lockScreenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).lockPhone();
            }
        });
        return view;
    }

        public void adView(final AdView adView) {

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError var1) {
                adView.setVisibility(View.GONE);
            }



        });
    }
    public void doNoDisturbPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Do no disturb").setMessage(MyAnnotations.DO_NOT_DISTURB_MESSAGE)
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
}