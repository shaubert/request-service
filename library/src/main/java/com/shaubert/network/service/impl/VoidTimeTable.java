package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSTimeTable;
import com.shaubert.network.service.Request;

public class VoidTimeTable implements RSTimeTable {
    @Override
    public boolean shouldExecute(Request<?, ?> request) {
        return true;
    }

    @Override
    public void updateExecutionTime(Request<?, ?> request) {
    }

    @Override
    public void clear(ClearFunction clearFunction) {
    }

    @Override
    public void clear() {
    }
}