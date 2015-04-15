package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSEvent;
import com.shaubert.network.service.RSTracker;
import com.shaubert.network.service.Request;

public class VoidTracker implements RSTracker {
    @Override
    public void logRequestExecutionTime(Request request, long time) {
        //do nothing
    }

    @Override
    public void logRequestError(RSEvent event, String errorMessage) {
        //do nothing
    }
}
