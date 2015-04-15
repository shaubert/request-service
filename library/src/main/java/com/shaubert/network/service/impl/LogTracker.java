package com.shaubert.network.service.impl;

import android.util.Log;
import com.shaubert.network.service.RSEvent;
import com.shaubert.network.service.RSTracker;
import com.shaubert.network.service.Request;

public class LogTracker implements RSTracker {

    public static final String TAG = LogTracker.class.getSimpleName();

    @Override
    public void logRequestExecutionTime(Request request, long time) {
        Log.i(TAG, "execution time of " + request.getName() + " = " + time + "ms");
    }

    @Override
    public void logRequestError(RSEvent event, String errorMessage) {
        Log.e(TAG, "execution of request " + event.getRequest().getName() + " failed: " + errorMessage);
    }

}
