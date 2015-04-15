package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSBus;
import com.shaubert.network.service.RSEvent;

public class VoidBus implements RSBus {
    @Override
    public void post(RSEvent event) {
        //do nothing
    }
}