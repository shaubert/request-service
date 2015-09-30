package com.shaubert.network.service.impl;

import android.text.TextUtils;
import com.shaubert.network.service.RSTimeTableEntry;
import com.shaubert.network.service.Request;

public class ExecutionTimeTableRequestEntry implements RSTimeTableEntry {
    private Class<?> requestClass;
    private Class<?> responseClass;
    private String qualifier;

    public ExecutionTimeTableRequestEntry(Request<?, ?> request) {
        requestClass = request.getClass();
        responseClass = request.getResponseClass();
        qualifier = request.getQualifier();
    }

    public ExecutionTimeTableRequestEntry(Class<?> requestClass, Class<?> responseClass, String qualifier) {
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.qualifier = qualifier;
    }

    public static ExecutionTimeTableRequestEntry parse(String key) {
        String[] values = key.split("\\-\\|\\-");
        if (values.length == 3) {
            try {
                Class<?> requestClass = Class.forName(values[0]);
                Class<?> responseClass = Class.forName(values[2]);
                String qualifier = null;
                if (!TextUtils.equals(values[1], "null")) {
                    qualifier = values[1];
                }
                return new ExecutionTimeTableRequestEntry(requestClass, responseClass, qualifier);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    @Override
    public String getKey() {
        return requestClass.getName() + "-|-" + qualifier + "-|-" + responseClass.getName();
    }

    @Override
    public Class<?> getRequestClass() {
        return requestClass;
    }

    @Override
    public Class<?> getResponseClass() {
        return responseClass;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

}
