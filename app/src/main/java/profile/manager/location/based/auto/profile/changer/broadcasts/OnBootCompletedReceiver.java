package profile.manager.location.based.auto.profile.changer.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import profile.manager.location.based.auto.profile.changer.annotations.MyAnnotations;
import profile.manager.location.based.auto.profile.changer.sharedpreferences.MyPreferences;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class OnBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        MyPreferences preferences = new MyPreferences(context);
        preferences.setBoolean(MyAnnotations.IS_BOOT_COMPLETED,true);
    }
}
