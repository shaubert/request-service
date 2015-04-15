package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSCache;
import com.shaubert.network.service.RSEvent;
import com.shaubert.network.service.Request;
import com.shaubert.network.service.Response;

public class VoidCache implements RSCache {
    @Override
    public void put(RSEvent event) {
        //do nothing
    }

    @Override
    public Response get(Request request) {
        return null;
    }
}
