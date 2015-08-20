package com.shaubert.network.service.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.shaubert.network.service.RSTimeTable;
import com.shaubert.network.service.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionTimeTable implements RSTimeTable {

    public static final String TAG = ExecutionTimeTable.class.getSimpleName();

    private static final String REQUESTS_EXECUTION_TIMES = "__sh_requests-exec-times-prefs";

    private Map<String, Entry> cachedEntries = new HashMap<>();
    private Map<String, Long> executionTimes = new HashMap<>();
    private RequestDelaysTable requestDelaysTable = new RequestDelaysTable();

    private final SharedPreferences prefs;

    public ExecutionTimeTable(Context context) {
        this.prefs = context.getSharedPreferences(REQUESTS_EXECUTION_TIMES, Context.MODE_PRIVATE);
        loadExecutionTimes();
    }

    public void setRequestDelaysTable(RequestDelaysTable requestDelaysTable) {
        this.requestDelaysTable = requestDelaysTable;
    }

    @SuppressWarnings("unchecked")
    protected void loadExecutionTimes() {
        executionTimes.clear();
        executionTimes.putAll((Map<String, Long>) prefs.getAll());
    }

    @Override
    public boolean shouldExecute(Request<?, ?> request) {
        return request.isForced() || isRequestExceededDelay(request);
    }

    @Override
    public void updateExecutionTime(Request<?, ?> request) {
        put(new Entry(request), getClockValue());
    }

    @Override
    public void clear(ClearFunction clearFunction) {
        if (clearFunction == null) return;

        List<String> keys = new ArrayList<>(executionTimes.keySet());
        for (String key : keys) {
            Entry entry = cachedEntries.get(key);
            if (entry == null) {
                entry = Entry.parse(key);
                if (entry != null) {
                    cachedEntries.put(key, entry);
                } else {
                    remove(key);
                    continue;
                }
            }

            if (clearFunction.shouldClear(entry.requestClass, entry.responseClass, entry.qualifier)) {
                remove(key);
            }
        }
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public void clear() {
        executionTimes.clear();
        prefs.edit().clear().commit();
    }

    protected void put(Entry entry, long clockValue) {
        String key = entry.getKey();
        cachedEntries.put(key, entry);
        executionTimes.put(key, clockValue);
        prefs.edit().putLong(key, clockValue).apply();
    }

    protected void remove(String key) {
        cachedEntries.remove(key);
        executionTimes.remove(key);
        prefs.edit().remove(key).apply();
    }

    protected Long get(Entry entry) {
        return executionTimes.get(entry.getKey());
    }

    protected long getClockValue() {
        return System.currentTimeMillis();
    }

    protected boolean isRequestExceededDelay(Request<?, ?> request) {
        Long lastExecutionTime = get(new Entry(request));
        if (lastExecutionTime != null) {
            long diff = getClockValue() - lastExecutionTime;
            if (diff >= 0 //clocks moved back
                    && diff < requestDelaysTable.getDelay(request)) {
                return false;
            }
        }
        return true;
    }

    private static class Entry {
        private Class<?> requestClass;
        private Class<?> responseClass;
        private String qualifier;

        Entry(Request<?, ?> request) {
            requestClass = request.getClass();
            responseClass = request.getResponseClass();
            qualifier = request.getQualifier();
        }

        Entry(Class<?> requestClass, Class<?> responseClass, String qualifier) {
            this.requestClass = requestClass;
            this.responseClass = responseClass;
            this.qualifier = qualifier;
        }

        static Entry parse(String key) {
            String[] values = key.split("\\-\\|\\-");
            if (values.length == 3) {
                try {
                    Class<?> requestClass = Class.forName(values[0]);
                    Class<?> responseClass = Class.forName(values[2]);
                    String qualifier = null;
                    if (!TextUtils.equals(values[1], "null")) {
                        qualifier = values[1];
                    }
                    return new Entry(requestClass, responseClass, qualifier);
                } catch (ClassNotFoundException ignored) {
                }
            }
            return null;
        }

        String getKey() {
            return requestClass.getName() + "-|-" + qualifier + "-|-" + responseClass.getName();
        }
    }

}

