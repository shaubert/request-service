package com.shaubert.network.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.shaubert.network.service.RSEvent.Status.CANCELLED;
import static com.shaubert.network.service.RSEvent.Status.RUNNING;
import static com.shaubert.network.service.RSEvent.Status.SUCCESS;

public class RequestService extends Service {

    public static boolean LOGGING = true;

    public static final String CANCELLED_REQUEST_ID_EXTRA = "cancelled_request_id_extra";
    public static final String CANCEL_ALL__EXTRA = "cancel_all_extra";
    public static final String PARCELABLE_REQUEST_EXTRA = "parcelable_request_extra";

    private static final String TAG = "RequestService";

    private ServiceConfig serviceConfig;

    private Map<Request, Long> requestTimes = new HashMap<>();
    private Map<String, Request> requests = new HashMap<>();
    private Map<String, Queue<Request>> requestQueues =
            new HashMap<String, Queue<Request>>();
    private RequestPreferences requestPreferences;

    @SuppressWarnings("unchecked")
    public static void start(Request request, Context context) {
        start(request, context, ServiceConfig.get());
    }

    @SuppressWarnings("unchecked")
    private static void start(Request request, Context context, ServiceConfig config) {
        Intent intent = new Intent(context, RequestService.class);
        intent.putExtra(PARCELABLE_REQUEST_EXTRA, (Parcelable) request);
        context.startService(intent);

        RSEvent event = request.produceEvent(RUNNING, null);
        event.setRequest(request);
        putInCacheAndBus(config, event);
    }

    public static void cancel(Context context, String requestId) {
        Intent intent = new Intent(context, RequestService.class);
        intent.putExtra(CANCELLED_REQUEST_ID_EXTRA, requestId);
        context.startService(intent);
    }

    public static void cancelAll(Context context) {
        Intent intent = new Intent(context, RequestService.class);
        intent.putExtra(CANCEL_ALL__EXTRA, true);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        requestPreferences = new RequestPreferences(this);
        serviceConfig = ServiceConfig.get();

        info("Starting service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(CANCEL_ALL__EXTRA, false)) {
                for (Request request : requests.values()) {
                    cancelRequest(request.getId());
                }
            }

            String requestId = intent.getStringExtra(CANCELLED_REQUEST_ID_EXTRA);
            if (!TextUtils.isEmpty(requestId)) {
                cancelRequest(requestId);
            }

            Request request = intent.getParcelableExtra(PARCELABLE_REQUEST_EXTRA);
            if (request != null) {
                execute(request);
            }
        }
        stopSelfIfNeeded();
        return START_STICKY;
    }

    @SuppressWarnings("unchecked")
    private void cancelRequest(String requestId) {
        requestPreferences.setCancelled(requestId);
        Request request = requests.get(requestId);
        if (request != null) {
            request.onCancelled();

            RSEvent event = request.produceEvent(CANCELLED, null);
            event.setRequest(request);
            putInCacheAndBus(event);
        }
    }

    @SuppressWarnings("unchecked")
    protected void execute(Request request) {
        if (!handleCancelledRequest(request)
                && !returnResultFromCache(request)) {
            putRequestInQueueIfNeeded(request);
            if (isInFrontOfQueue(request)) {
                if (LOGGING) debug(">>>: " + request);
                long requestStartTime = SystemClock.uptimeMillis();
                requestTimes.put(request, requestStartTime);
                requests.put(request.getId(), request);
                serviceConfig.getInjector().inject(this, request);
                serviceConfig.getExecutor().execute(request, createCallback(request));
            }
        } else {
            stopSelfIfNeeded();
        }
    }

    private boolean returnResultFromCache(Request request) {
        if (request.isForced()) return false;

        Response response = serviceConfig.getCache().get(request);
        if (response != null) {
            RSEvent event = request.produceEvent(SUCCESS, response);
            //noinspection unchecked
            event.setRequest(request);
            event.setFromCache(true);
            putInCacheAndBus(event);
            return true;
        }

        return false;
    }

    private static void info(String text) {
        if (LOGGING) Log.i(TAG, text);
    }

    private static void debug(String text) {
        if (LOGGING) Log.d(TAG, text);
    }

    private static void warn(String text) {
        if (LOGGING) warn(text, null);
    }

    private static void warn(String text, Throwable t) {
        if (LOGGING) Log.w(TAG, text, t);
    }

    private void putRequestInQueueIfNeeded(Request request) {
        Queue<Request> queue = getQueue(request);
        if (queue != null && !queue.contains(request)) {
            queue.add(request);
        }
    }

    private Queue<Request> getQueue(Request request) {
        String syncGroup = request.getRequestSyncGroup();
        if (TextUtils.isEmpty(syncGroup)) {
            return null;
        }

        Queue<Request> queue = requestQueues.get(syncGroup);
        if (queue == null) {
            queue = new LinkedList<>();
            requestQueues.put(syncGroup, queue);
        }
        return queue;
    }

    private void removeRequestFromQueueAndExecuteNext(Request request) {
        Queue<Request> queue = getQueue(request);
        if (queue != null) {
            queue.remove(request);
            Request nextRequest = queue.peek();
            if (nextRequest != null) {
                execute(nextRequest);
            }
        }
        stopSelfIfNeeded();
    }

    private boolean isInFrontOfQueue(Request request) {
        Queue<Request> queue = getQueue(request);
        if (queue == null) {
            return true;
        }

        Request firstRequest = queue.peek();
        return request.equals(firstRequest);
    }

    private ResultCallback createCallback(Request request) {
        return new BaseCallback(request);
    }

    private void stopSelfIfNeeded() {
        if (requests.isEmpty()) {
            stopSelf();
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean handleCancelledRequest(Request request) {
        if (requestPreferences.isCancelled(request.getId())) {
            info("skipping cancelled request: " + request);

            RSEvent event = request.produceEvent(CANCELLED, null);
            event.setRequest(request);
            putInCacheAndBus(event);
            return true;
        } else {
            return false;
        }
    }

    private void logRequestExecutionTime(Request request) {
        Long startTime = requestTimes.get(request);
        if (startTime != null) {
            long time = SystemClock.uptimeMillis() - startTime;
            serviceConfig.getTracker().logRequestExecutionTime(request, time);
        }
    }

    @SuppressWarnings("unchecked")
    private void putInCacheAndBus(RSEvent event) {
        putInCacheAndBus(serviceConfig, event);
    }

    @SuppressWarnings("unchecked")
    private static void putInCacheAndBus(ServiceConfig config, RSEvent event) {
        config.getCache().put(event);
        config.getBus().post(event);
    }

    private void cleanUpForRequest(Request request) {
        requests.remove(request.getId());
        requestTimes.remove(request);
        requestPreferences.remove(request.getId());
        removeRequestFromQueueAndExecuteNext(request);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void logFailure(RSEvent event) {
        String errorLabel = "Failure: ";
        Object failure = event.getFailure();
        if (failure != null) {
            errorLabel += failure.toString();
        } else {
            errorLabel += "null";
        }
        serviceConfig.getTracker().logRequestError(event, errorLabel);
    }

    private class BaseCallback implements ResultCallback {

        protected final Request request;

        protected BaseCallback(Request request) {
            this.request = request;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleError(Object failure) {
            if (!handleCancelledRequest(request)) {
                warn("request " + request.getName() + " failed: " + failure);

                RSEvent event = request.produceEvent(RSEvent.Status.FAILURE, failure);
                event.setRequest(request);

                logFailure(event);
                putInCacheAndBus(event);
                cleanUpForRequest(request);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleSuccess(Response response) {
            if (!handleCancelledRequest(request)) {
                logRequestExecutionTime(request);

                response.onParsed();
                RSEvent event = request.produceEvent(RSEvent.Status.SUCCESS, response);
                event.getSuccess().setQualifier(request.getQualifier());
                event.setRequest(request);

                if (LOGGING) debug("<<<: " + event.getSuccess());

                putInCacheAndBus(event);
                cleanUpForRequest(request);
            }
        }
    }

}