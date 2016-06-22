package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSExecutor;
import com.shaubert.network.service.Request;
import com.shaubert.network.service.Response;
import com.shaubert.network.service.ResultCallback;

public class DefaultMainThreadExecutor implements RSExecutor {

    @Override
    public <T extends Response, F> void execute(Request<T, F> request, ResultCallback<T, F> resultCallback) {
        request.execute(resultCallback);
    }

    @Override
    public <T extends Response, F> void cancel(Request<T, F> request) {
    }

}
