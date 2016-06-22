package com.shaubert.network.service;

public class VoidResponse extends Response {
    private static final VoidResponse INSTANCE = new VoidResponse();

    public static VoidResponse get() {
        return INSTANCE;
    }

    private VoidResponse() {
        super(null);
    }
}
