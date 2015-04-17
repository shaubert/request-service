package com.shaubert.network.service;

public interface RSCache {

    /**
     * Put event in cache. Event contains state, request and optional response or failure. Please note that
     * sometimes method could be called with data retrieved from cache in {@link
     * com.shaubert.network.service.RSCache#get(Request, com.shaubert.network.service.RSCache.Callback) get()} method.
     * @param event event;
     */
    void put(RSEvent event);

    /**
     * Get response from cache. Called before executing request (if request is not forced).
     * @param request request to execute;
     * @param callback callback to return response. Pass response from cache to prevent request execution
     *                 or null to execute request
     */
    void get(Request request, Callback callback);

    public interface Callback {
        /**
         * Callback to return result
         * @param request pass same request you got in {@link
         * com.shaubert.network.service.RSCache#get(Request, com.shaubert.network.service.RSCache.Callback) RSCache.get()} method
         * @param response response from cache to prevent request execution or null to execute request.
         */
        void onResultFromCache(Request request, Response response);
    }

}
