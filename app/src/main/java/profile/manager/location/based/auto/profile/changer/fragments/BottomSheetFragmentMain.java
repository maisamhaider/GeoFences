package profile.manager.location.based.auto.profile.changer.fragments;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import profile.manager.location.based.auto.profile.changer.R;
import profile.manager.location.based.auto.profile.changer.activities.MainActivity;
import profile.manager.location.based.auto.profile.changer.annotations.MyAnnotations;
import profile.manager.location.based.auto.profile.changer.interfaces.ClickListener;
import profile.manager.location.based.auto.profile.changer.utils.AllActionsUtils;
import profile.manager.location.based.auto.profile.changer.utils.Utils;


public class BottomSheetFragmentMain extends BottomSheetDialogFragment {
    NotificationManager mNotificationManager;
    private AllActionsUtils allActionsUtils;

    private Utils utils;
    ClickListener clickListener;
    public BottomSheetFragmentMain(ClickListener clickListener) {
        // Required empty public constructor
        this.clickListener = clickListener;
    }

    public BottomSheetFragmentMain() {
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
        utils = new Utils(getActivity());

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

                if (utils.isAboveP())
                {
                  goToWifiSettings();
                }
                else
                {
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
//                        AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
//                        audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
                        if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_NORMAL)) {
                            ringModeIv.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                                    , R.drawable.ic_round_vibration,
                                    null));
                            allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_VIBRATE);

                        }
                        else
                            if (allActionsUtils.getRingerModeSilent().matches(MyAnnotations.RINGER_MODE_VIBRATE)) {
                            ringModeIv.setImageDrawable(ResourcesCompat.
                                    getDrawable(getResources()
                                            , R.drawable.ic_baseline_volume_mute,
                                            null));
                            allActionsUtils.setAudioMode(MyAnnotations.RINGER_MODE_SILENT);
//                            setAudioMode.

                        }
                            else {
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

                    }

                    else {
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
            if (!adView.isLoading())
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
    public void goToWifiSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Go to wifi settings").setMessage(MyAnnotations.GO_WIFI_SETTINGS_MESSAGE)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        clickListener.click(true);

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

    //TODO
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}