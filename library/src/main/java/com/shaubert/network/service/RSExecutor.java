package com.shaubert.network.service;

public interface RSExecutor {

    /**
     * Executes request. {@link com.shaubert.network.service.ResultCallback ResultCallback}
     * have to be called in UI thread.
     * @param request request to execute.
     * @param resultCallback callback.
     */
    void execute(Request request, ResultCallback resultCallback);

    /**
     * Cancel executing request.
     * @param request request to cancel.
     */
    void cancel(Request request);

}
