package com.shaubert.network.service;

public interface RSTracker {

    void logRequestExecutionTime(Request request, long time);

    void logRequestError(RSEvent event, String errorMessage);
}
