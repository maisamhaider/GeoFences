package profile.manager.location.based.auto.profile.changer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import profile.manager.location.based.auto.profile.changer.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        AdView adView = findViewById(R.id.adView);
        adView(adView);
        findViewById(R.id.about_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        findViewById(R.id.rateUs_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });
        findViewById(R.id.share_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUs();
            }
        });
        findViewById(R.id.moreApp_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=FreeBirdis")));
            }
        });
        findViewById(R.id.settingsBack_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
    }

    public void dialog() {
        View view = getLayoutInflater().inflate(R.layout.about_dialog_layout, null, false);
        TextView aboutUsCloseApp_tv = view.findViewById(R.id.aboutUsCloseApp_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true).setView(view);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView version_tv = view.findViewById(R.id.appVersion_tv);

        aboutUsCloseApp_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = packageInfo.versionName;
            version_tv.setText(version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void rateUs() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + new PackageInfo().packageName)));
    }

    public void shareUs() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + new PackageInfo().packageName);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
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
}