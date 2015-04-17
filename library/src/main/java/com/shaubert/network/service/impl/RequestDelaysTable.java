package com.shaubert.network.service.impl;

import java.util.*;

public class RequestDelaysTable {

    public static final long DEFAULT_DELAY = 0L;

    private long defaultDelay;
    private List<RequestGroup> requestGroups = new ArrayList<>();
    private long maxDelay;

    protected RequestDelaysTable() {
        this(DEFAULT_DELAY);
    }

    protected RequestDelaysTable(long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public void addGroup(RequestGroup group) {
        if (!requestGroups.contains(group)) {
            requestGroups.add(group);
            refreshMaxDelay();
        }
    }

    public void removeGroup(RequestGroup group) {
        requestGroups.remove(group);
        refreshMaxDelay();
    }

    public long getMaxDelay() {
        return maxDelay;
    }

    private void refreshMaxDelay() {
        maxDelay = 0;
        for (RequestGroup group : requestGroups) {
            maxDelay = Math.max(maxDelay, group.getRefreshDelay());
        }
    }

    public long getDelay(Object request) {
        for (RequestGroup group : requestGroups) {
            if (group.contains(request.getClass())) {
                return group.getRefreshDelay();
            }
        }
        return defaultDelay;
    }

    public static class RequestGroup implements Comparable<RequestGroup> {
        private Set<Class<?>> requestClasses = new HashSet<>();
        private final long refreshDelay;

        private RequestGroup(long refreshDelay) {
            this.refreshDelay = refreshDelay;
        }

        public RequestGroup add(Class<?> request) {
            requestClasses.add(request);
            return this;
        }

        public long getRefreshDelay() {
            return refreshDelay;
        }

        public boolean contains(Class<?> request) {
            return requestClasses.contains(request);
        }

        @Override
        public int compareTo(RequestGroup another) {
            if (refreshDelay < another.refreshDelay) {
                return -1;
            } else if (refreshDelay > another.refreshDelay) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
