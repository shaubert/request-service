package com.shaubert.network.service;

public interface ResultCallback<T extends Response, F> {

    void handleError(F error);

    void handleSuccess(T response);

}
