package com.shaubert.network.service;

import android.content.Context;
import android.content.SharedPreferences;

class RequestPreferences {

    private final SharedPreferences preferences;

    public RequestPreferences(Context context) {
        preferences = context.getSharedPreferences("__request-service-state-prefs", Context.MODE_PRIVATE);
    }

    public void setCancelled(String requestId) {
        preferences.edit().putBoolean(getKey(requestId), true).commit();
    }

    private String getKey(String requestId) {
        return "request-queue-cancelled-request-" + requestId;
    }

    public boolean isCancelled(String requestId) {
        return preferences.getBoolean(getKey(requestId), false);
    }

    public void remove(String requestId) {
        preferences.edit().remove(getKey(requestId)).commit();
    }

}
