package com.shaubert.network.service;

import android.content.Context;

public interface RSInjector {
    /**
     * Inject values to your requests
     * @param context application context
     * @param o object to inject into
     */
    void inject(Context context, Object o);
}
