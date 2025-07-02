package com.example.pjtfacerec.api;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesService {
    private final SharedPreferences sharedPreferences;
    public SharedPreferencesService(Context context) {
        this.sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public void setToken(String token) {
        this.sharedPreferences.edit().putString("token", token).apply();
    }

    public String getToken() {
        return this.sharedPreferences.getString("token", "");
    }
}
