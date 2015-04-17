package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSCache;
import com.shaubert.network.service.RSEvent;
import com.shaubert.network.service.Request;

public class VoidCache implements RSCache {

    @Override
    public void put(RSEvent event) {
        //do nothing
    }

    @Override
    public void get(Request request, Callback callback) {
        callback.onResultFromCache(request, null);
    }

}
