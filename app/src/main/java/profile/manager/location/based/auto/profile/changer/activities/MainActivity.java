package profile.manager.location.based.auto.profile.changer.activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.security.Permission;

import profile.manager.location.based.auto.profile.changer.R;
import profile.manager.location.based.auto.profile.changer.annotations.MyAnnotations;
import profile.manager.location.based.auto.profile.changer.annotations.PermissionCodes;
import profile.manager.location.based.auto.profile.changer.broadcasts.Admin;
import profile.manager.location.based.auto.profile.changer.database.MyDatabase;
import profile.manager.location.based.auto.profile.changer.fragments.BottomSheetFragmentMain;
import profile.manager.location.based.auto.profile.changer.fragments.MapsFragment;
import profile.manager.location.based.auto.profile.changer.interfaces.ClickListener;
import profile.manager.location.based.auto.profile.changer.permissions.ThesePermissions;
import profile.manager.location.based.auto.profile.changer.utils.AdminHelper;
import profile.manager.location.based.auto.profile.changer.utils.AllActionsUtils;

public class MainActivity extends AppCompatActivity implements ClickListener {

    private ThesePermissions thesePermissions;
    private MyDatabase myDatabase;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    BottomSheetFragmentMain bottomSheetFragmentMain;
    AdminHelper adminHelper;
    InterstitialAd mInterstitialAd;
    public ProgressDialog showDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        thesePermissions = new ThesePermissions(this);
        adminHelper = new AdminHelper(MainActivity.this);
        myDatabase = new MyDatabase(MainActivity.this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, Admin.class);
        bottomSheetFragmentMain = new BottomSheetFragmentMain(this);




        ImageView history_iv = findViewById(R.id.history_iv);
        ConstraintLayout bottomSheetStartCl = findViewById(R.id.bottomSheetStart_cl);

        if (bottomSheetFragmentMain.isHidden()) {
            bottomSheetStartCl.setVisibility(View.VISIBLE);
        } else {

            bottomSheetStartCl.setVisibility(View.VISIBLE);
        }
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial));
        loadInterstitial();


        bottomSheetStartCl.performClick();
        bottomSheetStartCl.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bottomSheetFragmentMain.show(getSupportFragmentManager(),
                            "Main_bottom_sheet");
                }
                return true;
            }
        });

    }



    public void Load_withAds(boolean isFragment,Fragment fragment,Activity activity) {
        try {

            showDialog = ProgressDialog.show(MainActivity.this,
                    getString(R.string.app_name),
                    "Please wait a seconds",
                    true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (showDialog != null) {
                    showDialog.dismiss();
                }
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    if (isFragment)
                    {
                        loadFragments(fragment);
                    }
                    else {
                        startActivity(new Intent(
                                MainActivity.this,activity.getClass()));
                    }
//                    loadInterstitial();
                }

                mInterstitialAd.setAdListener(
                        new AdListener() {
                            @Override
                            public void onAdLoaded() {
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                            }

                            @Override
                            public void onAdClosed() {
                                if (isFragment)
                                {
                                    loadFragments(fragment);
                                }
                                else {
                                    startActivity(new Intent(MainActivity.this,activity.getClass()));
                                }//

                                loadInterstitial();
                            }
                        });
            }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
             loadFun();
        }
    }

    public void loadFun() {
    }

    public void loadInterstitial() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!adminHelper.isActive()) {
                adminHelper.intentToAdmin();
            }
        }
    }

    public void lockPhone(/*View view*/) {
        boolean active = devicePolicyManager.isAdminActive(compName);
        if (active) {
            devicePolicyManager.lockNow();
        } else {
            Toast.makeText(this, "App is not activated for device admin", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        exitt();
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

    @Override
    public void click(boolean clicked) {
        if (clicked) {

            if (bottomSheetFragmentMain != null && bottomSheetFragmentMain.isVisible()) {
                bottomSheetFragmentMain.dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

         if (requestCode == PermissionCodes.REQ_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                    thesePermissions.doNoDisturbPermissionDialog();
                } else {
                    if (!adminHelper.isActive()) {
                        adminHelper.intentToAdmin();
                    }
                }

            } else {
                if (!adminHelper.isActive()) {
                    adminHelper.intentToAdmin();

                }
            }

        }
        else
        if (requestCode == PermissionCodes.REQ_CODE &&
                grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                    thesePermissions.doNoDisturbPermissionDialog();
                } else {
                    if (!adminHelper.isActive()) {
                        adminHelper.intentToAdmin();
                    }
                }

            } else {
                if (!adminHelper.isActive()) {
                    adminHelper.intentToAdmin();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!adminHelper.isActive()) {
        adminHelper.intentToAdmin();
    }
    }
}