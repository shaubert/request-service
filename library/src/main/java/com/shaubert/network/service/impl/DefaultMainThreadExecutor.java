package com.shaubert.network.service.impl;

import android.content.Context;
import com.shaubert.network.service.RSExecutor;
import com.shaubert.network.service.Request;
import com.shaubert.network.service.ResultCallback;

public class DefaultMainThreadExecutor implements RSExecutor {

    private Context context;

    public DefaultMainThreadExecutor(Context context) {
        this.context = context;
    }

    @Override
    public void execute(Request request, ResultCallback resultCallback) {
        //noinspection unchecked
        request.execute(resultCallback);
    }

    @Override
    public void cancel(Request request) {
        request.cancel(context);
    }

}
