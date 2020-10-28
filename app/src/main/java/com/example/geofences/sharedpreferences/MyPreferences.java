package com.example.geofences.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MyPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setDataString(String key, String data) {
        editor.putString(key, data).commit();
    }
    public void setBoolean(String key, boolean data) {
        editor.putBoolean(key, data).commit();
    }
    public String getString(String key,String defaultData)
    {
        return sharedPreferences.getString(key,defaultData);
    }
    public boolean getBoolean(String key,boolean defaultData)
    {
        return sharedPreferences.getBoolean(key,defaultData);
    }

}
