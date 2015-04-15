package com.shaubert.network.service;

public interface RSCache {

    /**
     * Put event in cache;
     * @param event event;
     */
    void put(RSEvent event);

    /**
     * Get response from cache; Called before executing request (if request is not forced).
     * @param request request to execute;
     * @return response from cache to prevent execution or null to execute request;
     */
    Response get(Request request);

}
