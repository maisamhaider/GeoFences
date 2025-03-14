package profile.manager.location.based.auto.profile.changer.annotations;

import android.text.format.DateUtils;

public @interface NumbersAnnotation {
    int HOUR_IN_MILLIS = (int) DateUtils.HOUR_IN_MILLIS;
    float BY_WALK = 60;
    float BY_CYCLE = 100;
    float BY_BUS = 200;
    float BY_CAR = 250;
}
