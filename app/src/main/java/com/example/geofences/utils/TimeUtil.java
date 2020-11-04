package com.example.geofences.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class TimeUtil {
    Context context;

    public TimeUtil() {
    }

    public TimeUtil(Context context) {
        this.context = context;
    }

    public long getFromNow(long millis) {
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
//        SimpleDateFormat format1 = new SimpleDateFormat("dd:MM:yyyy h:mm:a");;
//        String date1 =format1.format(calendar.getTime());
        calendar.setTimeInMillis(nowMillis + millis);
//        SimpleDateFormat format = new SimpleDateFormat("dd:MM:yyyy h:mm:a");
//        String date = format.format(calendar.getTime());
        return calendar.getTimeInMillis();
    }

    public long getNowMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public long get12AmMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM, 1);
        return calendar.getTimeInMillis();
     }
}
