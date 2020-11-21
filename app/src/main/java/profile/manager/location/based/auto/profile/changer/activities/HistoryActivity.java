package profile.manager.location.based.auto.profile.changer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import profile.manager.location.based.auto.profile.changer.R;
import profile.manager.location.based.auto.profile.changer.adapters.HistoryFragmentsAdapter;
import profile.manager.location.based.auto.profile.changer.annotations.MyAnnotations;
import profile.manager.location.based.auto.profile.changer.database.MyDatabase;
import profile.manager.location.based.auto.profile.changer.fragments.DoneFragment;
import profile.manager.location.based.auto.profile.changer.fragments.UndoneFragment;
import profile.manager.location.based.auto.profile.changer.models.HistoryModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        HistoryFragmentsAdapter adapter =
                new HistoryFragmentsAdapter(HistoryActivity.this
                        , getSupportFragmentManager(), 1);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    public ArrayList<HistoryModel> getHistory(boolean done) {
        ArrayList<HistoryModel> doneList = new ArrayList<>();
        ArrayList<HistoryModel> unDoneList = new ArrayList<>();

        Cursor cursor = new MyDatabase(HistoryActivity.this).retrieve();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String latitude = cursor.getString(2);
            String longitude = cursor.getString(3);
            String circleSize = cursor.getString(4);
            String expirationEnd = cursor.getString(5);
            String geofenceType = cursor.getString(6);
            String expirationTime = cursor.getString(7);
            String state = cursor.getString(8);
            String dateNow = cursor.getString(9);

            if (state.matches(MyAnnotations.UN_DONE)) {
                unDoneList.add(new HistoryModel(Integer.parseInt(id),
                        title,
                        latitude,
                        longitude,
                        circleSize,
                        Long.parseLong(expirationTime),
                        state,expirationEnd,geofenceType,dateNow));
            } else {
                doneList.add(new HistoryModel(Integer.parseInt(id),
                        title,
                        latitude,
                        longitude,
                        circleSize,
                        Long.parseLong(expirationTime),
                        state,expirationEnd,geofenceType,dateNow));
            }

        }
        if (done) {
            return doneList;
        } else
            return unDoneList;
    }
}