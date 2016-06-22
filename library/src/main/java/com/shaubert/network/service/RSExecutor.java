package com.shaubert.network.service;

public interface RSExecutor {

    /**
     * Executes request. {@link com.shaubert.network.service.ResultCallback ResultCallback}
     * have to be called in UI thread.
     * @param request request to execute.
     * @param resultCallback callback.
     */
    <T extends Response, F> void execute(Request<T, F> request, ResultCallback<T, F> resultCallback);

    /**
     * Cancel executing request.
     * @param request request to cancel.
     */
    <T extends Response, F> void cancel(Request<T, F> request);

}
