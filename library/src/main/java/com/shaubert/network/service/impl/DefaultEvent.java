package com.shaubert.network.service.impl;

import com.shaubert.network.service.RSEvent;
import com.shaubert.network.service.Response;

public class DefaultEvent<OK extends Response<OK>> extends RSEvent<OK, Object> {
    public DefaultEvent(Status status, Object responseOrFailure) {
        super(status, responseOrFailure);
    }
}
