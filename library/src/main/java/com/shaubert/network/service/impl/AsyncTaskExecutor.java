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
    public <T extends Response, F> void execute(final Request<T, F> request, final ResultCallback<T, F> resultCallback) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            F _error;
            boolean errorCalled;
            T _response;
            boolean successCalled;

            @Override
            protected Void doInBackground(Void[] params) {
                request.execute(new ResultCallback<T, F>() {
                    @Override
                    public void handleError(F error) {
                        _error = error;
                        errorCalled = true;
                    }

                    @Override
                    public void handleSuccess(T response) {
                        _response = response;
                        successCalled = true;
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                onTaskFinished(request.getId());
                if (successCalled) {
                    resultCallback.handleSuccess(_response);
                } else if (errorCalled) {
                    resultCallback.handleError(_error);
                } else {
                    throw new IllegalStateException("AsyncTaskExecutor is only for synchronous requests. execute() call for request "
                            + request.getClass() + " didn't return any value with callback.");
                }
            }

            @Override
            protected void onCancelled(Void o) {
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
    public <T extends Response, F> void cancel(Request<T, F> request) {
        AsyncTask task = asyncTaskMap.get(request.getId());
        if (task != null) {
            task.cancel(true);
        }
    }

}
