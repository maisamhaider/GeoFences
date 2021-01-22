package profile.manager.location.based.auto.profile.changer.annotations;

import android.media.AudioManager;
import android.util.Log;

import profile.manager.location.based.auto.profile.changer.R;

public @interface MyAnnotations {

    String RINGER_MODE_SILENT_KEY = "RINGER_MODE_SILENT_KEY";
    String RINGER_MODE_SILENT = "RINGER_MODE_SILENT";
    String RINGER_MODE_VIBRATE = "RINGER_MODE_VIBRATE";
    String RINGER_MODE_NORMAL = "RINGER_MODE_NORMAL";
    String ADJUST_MUTE = "ADJUST_MUTE";
    String RINGING = "Ringing";
    String SILENT = "Silent";
    String VIBRATE = "Vibrate";
    String HELP_FIRST_TIME = "HELP_FIRST_TIME";

    String UN_DONE = "UN_DONE";
    String DONE = "DONE";
    String IS_BOOT_COMPLETED = "IS_BOOT_COMPLETED";
    String ON = "ON";
    String OFF = "OFF";
    String ENTER = "ENTER";
    String EXIT = "EXIT";
    String BOTH = "BOTH";
    String DO_NOT_DISTURB_MESSAGE = "If you want to use this feature you need allow the permission";
    String GO_WIFI_SETTINGS_MESSAGE = "You have to turn ON/OFF from settings. Thank you";
    String NULL = "NULL";

    String GEO_FENCE_LIMIT = "GEO_FENCE_LIMIT";

    String MY_PREFERENCES = "MY_PREFERENCES";

    String BY_WALK = "100x100 (m)";
    String BY_CYCLE = "200x200 (m)";
    String BY_BUS = "250x250 (m)";
    String BY_CAR = "300x200 (m)";

    String CHANNEL_ID = "CHANNEL_ID";
    String CHANNEL_NAME = R.string.app_name+"channel";

    String IS_TERMS_CONDITION = "isTermCondition";
    String wait_message = "Please wait a moment";


    String back = "back";
    String done = "done";
    String next = "next";
}
