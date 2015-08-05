package com.shaubert.network.service;

public abstract class RSEvent<OK extends Response<OK>, FAIL> {

    public enum Status {
        RUNNING, SUCCESS, FAILURE, CANCELLED
    }

    private final Status status;
    private final OK success;
    private final FAIL failure;
    private final long creationTime;
    private boolean fromCache;

    private Request<OK, FAIL> request;

    @SuppressWarnings("unchecked")
    public RSEvent(Status status, Object responseOrFailure) {
        this.status = status;
        this.success = (status == Status.SUCCESS) ? (OK) responseOrFailure : null;
        this.failure = (status == Status.FAILURE) ? (FAIL) responseOrFailure : null;
        this.creationTime = System.currentTimeMillis();
    }

    public Status getStatus() {
        return status;
    }

    public boolean isRunning() {
        return status == Status.RUNNING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isFailure() {
        return status == Status.FAILURE;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public Request<OK, FAIL> getRequest() {
        return request;
    }

    public OK getSuccess() {
        return success;
    }

    public FAIL getFailure() {
        return failure;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }

    void setRequest(Request<OK, FAIL> request) {
        this.request = request;
    }

    @SuppressWarnings("unchecked")
    public <A> A getRequestSafe(Class<A> clazz) {
        if (request != null && clazz.isInstance(request)) {
            return (A) getRequest();
        }
        return null;
    }

    @Override
    public String toString() {
        return "RSEvent{" +
                "status=" + status +
                ", success=" + success +
                ", failure=" + failure +
                ", fromCache=" + fromCache +
                ", request=" + request +
                '}';
    }
}
