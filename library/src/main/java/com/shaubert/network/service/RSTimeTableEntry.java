package com.shaubert.network.service;

/**
 * Interface to retrieve {@link String} key from provided {@link com.shaubert.network.service.Request request}.
 */
public interface RSTimeTableEntry {

    /**
     * Creates the unique key for containing parameters.
     * @return key
     */
    String getKey();

    /**
     * @return the {@link Request Request} class associated with this entry.
     */
    Class<?> getRequestClass();

    /**
     * @return the {@link Response Response} class associated with this entry.
     */
    Class<?> getResponseClass();

    /**
     * @return the qualifier from {@link Request#getQualifier() Request.getQualifier()}.
     */
    String getQualifier();

}