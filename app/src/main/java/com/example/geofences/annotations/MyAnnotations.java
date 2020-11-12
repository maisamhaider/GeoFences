package com.example.geofences.annotations;

import android.media.AudioManager;
import android.util.Log;

public @interface MyAnnotations {

    String RINGER_MODE_SILENT_KEY = "RINGER_MODE_SILENT_KEY";
    String RINGER_MODE_SILENT = "RINGER_MODE_SILENT";
    String RINGER_MODE_VIBRATE = "RINGER_MODE_VIBRATE";
    String RINGER_MODE_NORMAL = "RINGER_MODE_NORMAL";
    String ADJUST_MUTE = "ADJUST_MUTE";
    String RINGING = "Ringing";
    String SILENT = "Silent";
    String VIBRATE = "Vibrate";

    String UN_DONE = "UN_DONE";
    String DONE = "DONE";
    String IS_BOOT_COMPLETED = "IS_BOOT_COMPLETED";
    String ON = "ON";
    String OFF = "OFF";
    String EXIT = "EXIT";
    String ENTER = "ENTER";
    String BOTH = "BOTH";
    String DO_NOT_DISTURB_MESSAGE = "If you want to use this feature you need allow the permission";
    String NULL = "NULL";

    String GEO_FENCE_LIMIT = "GEO_FENCE_LIMIT";

    String MY_PREFERENCES = "MY_PREFERENCES";

    String BY_WALK = "50x50 (m)";
    String BY_CYCLE = "90x90 (m)";
    String BY_BUS = "100x100 (m)";
    String BY_CAR = "150x150 (m)";


}
