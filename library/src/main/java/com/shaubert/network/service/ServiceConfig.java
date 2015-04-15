package com.shaubert.network.service;

import android.content.Context;
import com.shaubert.network.service.impl.*;

/**
 * Configuration for {@link com.shaubert.network.service.RequestService RequestService}. You have to setup config with
 * {@link com.shaubert.network.service.ServiceConfig#newBuilder(android.content.Context) newBuilder()} before executing any request.
 * Best place for it in {@link android.app.Application#onCreate() Application.onCreate()}.
 */
public class ServiceConfig {
    private RSBus bus;
    private RSCache cache;
    private RSInjector injector;
    private RSExecutor executor;
    private RSTracker tracker;

    private static ServiceConfig instance;

    /**
     * @return true if configuration was successfully built and set via
     * {@link com.shaubert.network.service.ServiceConfig#newBuilder(android.content.Context) newBuilder()} method.
     */
    public static boolean isSet() {
        return instance != null;
    }

    /**
     * Default singleton instance of {@link com.shaubert.network.service.RequestService RequestService} config;
     * @return singleton instance
     * @throws NullPointerException if instance is null
     */
    public static ServiceConfig get() throws NullPointerException{
        if (instance == null) {
            throw new NullPointerException("You have to initialize ServiceConfig before first use!" +
                    "Use ServiceConfig.set() method from your Application.onCreate().");
        }
        return instance;
    }

    static void set(ServiceConfig config) {
        instance = config;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    private ServiceConfig(Builder builder) {
        bus = builder.bus;
        cache = builder.cache;
        injector = builder.injector;
        executor = builder.executor;
        tracker = builder.tracker;
    }

    public RSBus getBus() {
        return bus;
    }

    public RSCache getCache() {
        return cache;
    }

    public RSTracker getTracker() {
        return tracker;
    }

    public RSInjector getInjector() {
        return injector;
    }

    public RSExecutor getExecutor() {
        return executor;
    }


    public static final class Builder {
        private RSBus bus;
        private RSCache cache;
        private RSInjector injector;
        private RSExecutor executor;
        private RSTracker tracker;

        private Context context;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder bus(RSBus bus) {
            this.bus = bus;
            return this;
        }

        public Builder cache(RSCache cache) {
            this.cache = cache;
            return this;
        }

        public Builder injector(RSInjector injector) {
            this.injector = injector;
            return this;
        }

        public Builder executor(RSExecutor executor) {
            this.executor = executor;
            return this;
        }

        public Builder tracker(RSTracker tracker) {
            this.tracker = tracker;
            return this;
        }

        public ServiceConfig buildAndSet() {
            if (bus == null) bus = new VoidBus();
            if (cache == null) cache = new VoidCache();
            if (injector == null) injector = new VoidInjector();
            if (executor == null) executor = new DefaultMainThreadExecutor(context);
            if (tracker == null) tracker = new VoidTracker();

            ServiceConfig serviceConfig = new ServiceConfig(this);
            set(serviceConfig);
            return serviceConfig;
        }
    }
}
