package com.shaubert.network.service.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.shaubert.network.service.RSTimeTable;
import com.shaubert.network.service.RSTimeTableEntry;
import com.shaubert.network.service.RSTimeTableEntryBuilder;
import com.shaubert.network.service.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionTimeTable implements RSTimeTable {

    public static final String TAG = ExecutionTimeTable.class.getSimpleName();

    private static final String REQUESTS_EXECUTION_TIMES = "__sh_requests-exec-times-prefs";

    private Map<String, RSTimeTableEntry> cachedEntries = new HashMap<>();
    private Map<String, Long> executionTimes = new HashMap<>();
    private RequestDelaysTable requestDelaysTable = new RequestDelaysTable();

    private final SharedPreferences prefs;
    private RSTimeTableEntryBuilder entryBuilder;

    public ExecutionTimeTable(Context context) {
        this(context, new DefaultTimeTableEntryBuilder());
    }

    public ExecutionTimeTable(Context context, RSTimeTableEntryBuilder entryBuilder) {
        this.entryBuilder = entryBuilder;
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
        put(entryBuilder.create(request), getClockValue());
    }

    @Override
    public void clear(ClearFunction clearFunction) {
        if (clearFunction == null) return;

        List<String> keys = new ArrayList<>(executionTimes.keySet());
        for (String key : keys) {
            RSTimeTableEntry entry = cachedEntries.get(key);
            if (entry == null) {
                entry = entryBuilder.parse(key);
                if (entry != null) {
                    cachedEntries.put(key, entry);
                } else {
                    remove(key);
                    continue;
                }
            }

            if (clearFunction.shouldClear(entry)) {
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

    protected void put(RSTimeTableEntry entry, long clockValue) {
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

    protected Long get(RSTimeTableEntry entry) {
        return executionTimes.get(entry.getKey());
    }

    protected long getClockValue() {
        return System.currentTimeMillis();
    }

    protected boolean isRequestExceededDelay(Request<?, ?> request) {
        Long lastExecutionTime = get(entryBuilder.create(request));
        if (lastExecutionTime != null) {
            return isTimeExceededDelay(lastExecutionTime, getDelay(request));
        } else {
            return true;
        }
    }

    protected long getDelay(Request<?, ?> request) {
        return requestDelaysTable.getDelay(request);
    }

    protected boolean isTimeExceededDelay(long time, long delay) {
        long diff = getClockValue() - time;
        return diff < 0 //clocks moved back
                || diff > delay;
    }

}

