package com.example.geofences.annotations;

import android.text.format.DateUtils;

public @interface NumbersAnnotation {
    int HOUR_IN_MILLIS = (int) DateUtils.HOUR_IN_MILLIS;
    float BY_WALK = 50;
    float BY_CYCLE = 80;
    float BY_BUS = 100;
    float BY_CAR = 150;
}
