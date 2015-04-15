package com.shaubert.network.service.impl;

import android.os.AsyncTask;
import com.shaubert.network.service.RSExecutor;
import com.shaubert.network.service.Request;
import com.shaubert.network.service.Response;
import com.shaubert.network.service.ResultCallback;

import java.util.HashMap;
import java.util.Map;

public class AsyncTaskExecutor implements RSExecutor {

    private Map<String, AsyncTask> asyncTaskMap = new HashMap<>();

    @Override
    public void execute(final Request request, final ResultCallback resultCallback) {
        AsyncTask task = new AsyncTask() {
            Object _error;
            Response _response;

            @Override
            protected Object doInBackground(Object[] params) {
                //noinspection unchecked
                request.execute(new ResultCallback() {
                    @Override
                    public void handleError(Object error) {
                        _error = error;
                    }

                    @Override
                    public void handleSuccess(Response response) {
                        _response = response;
                    }
                });

                if (_response == null && _error == null) {
                    throw new IllegalStateException("AsyncTaskExecutor is only for synchronous requests. execute() call for request "
                            + request.getClass() + " didn't return any value with callback.");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                onTaskFinished(request.getId());
                if (_response != null) {
                    //noinspection unchecked
                    resultCallback.handleSuccess(_response);
                } else {
                    //noinspection unchecked
                    resultCallback.handleError(_error);
                }
            }

            @Override
            protected void onCancelled(Object o) {
                onTaskFinished(request.getId());
            }

            @Override
            protected void onCancelled() {
                onTaskFinished(request.getId());
            }
        };

        asyncTaskMap.put(request.getId(), task);

        //noinspection unchecked
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onTaskFinished(String id) {
        asyncTaskMap.remove(id);
    }

    @Override
    public void cancel(Request request) {
        AsyncTask task = asyncTaskMap.get(request.getId());
        if (task != null) {
            task.cancel(true);
            request.onCancelled();
        }
    }

}
