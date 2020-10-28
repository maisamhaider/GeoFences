package com.example.geofences.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.geofences.annotations.MyAnnotations;

public class AllActionsUtils {
    private Context context;
    boolean isHeadphoneCtd = false;
    private AudioManager am;
    BluetoothAdapter bluetoothAdapter;
    Activity activity;

    public AllActionsUtils(Context context) {
        this.context = context;
    }

    public AllActionsUtils(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void setAudioMode(String whatToSet) {

        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //For Normal mode
        if (whatToSet.matches(MyAnnotations.RINGER_MODE_NORMAL)) {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else
        //For Silent mode
            if (whatToSet.matches(MyAnnotations.RINGER_MODE_SILENT)) {

                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else
                //For Vibrate mode
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);


    }

    public void setBluetoothOnOff() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device does not supported ", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        }


    }


    public void SetAirplaneMode(boolean enabled){
        //---toggle Airplane mode---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context))
            {
                Settings.System.putInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON, enabled ? 1 : 0);
                Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                i.putExtra("state", enabled);
                context.sendBroadcast(i);
            }
        }

    }

    public String getRingerModeSilent() {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                return "RINGER_MODE_SILENT";
            case AudioManager.RINGER_MODE_VIBRATE:
                return "RINGER_MODE_VIBRATE";
            case AudioManager.RINGER_MODE_NORMAL:
                return "RINGER_MODE_NORMAL";
        }
        return "null";

    }

    public boolean getBluetoothOnOff() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            return false;
        } else if (bluetoothAdapter.isEnabled()) {
            return true;

        } else
            return false;
    }

    public boolean getAirplaneModeOn() {

        return Settings.System.getInt(context.getContentResolver()
                , Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

}
