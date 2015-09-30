package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSTimeTableEntry;
import com.shaubert.network.service.RSTimeTableEntryBuilder;
import com.shaubert.network.service.Request;

public class DefaultTimeTableEntryBuilder implements RSTimeTableEntryBuilder {

    @Override
    public RSTimeTableEntry parse(String key) {
        return ExecutionTimeTableRequestEntry.parse(key);
    }

    @Override
    public RSTimeTableEntry create(Request<?, ?> request) {
        return new ExecutionTimeTableRequestEntry(request);
    }

}
