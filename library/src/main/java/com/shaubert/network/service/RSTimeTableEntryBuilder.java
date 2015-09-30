package com.shaubert.network.service;

/**
 * Interface to transform {@link com.shaubert.network.service.Request Request} and
 * key from  {@link RSTimeTableEntry#getKey() RSTimeTableEntry.getKey()} into
 * {@link com.shaubert.network.service.RSTimeTableEntry RSTimeTableEntry}.
 */
public interface RSTimeTableEntryBuilder {

    /**
     * Creates {@link RSTimeTableEntry RSTimeTableEntry} from resulted key.
     * @param key key from {@link RSTimeTableEntry#getKey() RSTimeTableEntry.getKey()} method
     * @return parsed {@link RSTimeTableEntry RSTimeTableEntry}.
     */
    RSTimeTableEntry parse(String key);

    /**
     * Creates {@link RSTimeTableEntry RSTimeTableEntry} from request.
     * @param request not null request.
     * @return created {@link RSTimeTableEntry RSTimeTableEntry}.
     */
    RSTimeTableEntry create(Request<?, ?> request);
    
}
