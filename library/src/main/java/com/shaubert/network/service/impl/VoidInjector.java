package com.shaubert.network.service.impl;

import android.content.Context;
import com.shaubert.network.service.RSInjector;

public class VoidInjector implements RSInjector {
    @Override
    public void inject(Context context, Object o) {
        //do nothing
    }
}
