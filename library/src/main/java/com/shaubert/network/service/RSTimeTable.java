package com.shaubert.network.service;

/**
 * Interface to store request execution times.
 */
public interface RSTimeTable {

    /**
     * Will be called if {@link com.shaubert.network.service.RSCache#get(Request, com.shaubert.network.service.RSCache.Callback) RSCache.get()}
     * returned not null value.
     * @param request request to execute
     * @return true to execute, false to return cached data
     */
    boolean shouldExecute(Request<?, ?> request);

    /**
     * Call to update last execution time of request.
     * @param request executing request
     */
    void updateExecutionTime(Request<?, ?> request);

    /**
     * Reset execution times for matched entries.
     * @param clearFunction match function. If null nothing will be removed.
     */
    void clear(ClearFunction clearFunction);

    /**
     * Reset all execution times.
     */
    void clear();

    public interface ClearFunction {
        /**
         * @param requestClass request class
         * @param responseClass response class
         * @param qualifier request qualifier
         * @return true to clear entry, false otherwise
         */
        boolean shouldClear(Class<?> requestClass, Class<?> responseClass, String qualifier);
    }

}
