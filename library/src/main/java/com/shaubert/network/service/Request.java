package com.shaubert.network.service;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

/**
 * Base request class. Child classes have to implement Parcelable interface. Don not forget to add
 * <pre>
 * {@code public static final Creator<T> CREATOR = new Creator<T>() {
       public Request createFromParcel(Parcel source) {
          return new T(source);
       }

       public Request[] newArray(int size) {
          return new T[size];
       }
    };}
 * </pre>
 *
 * @param <T> Response class
 * @param <F> Failure class
 */
public abstract class Request<T extends Response<T>, F> implements Parcelable {

    private final String id;
    private boolean forced = true;
    private transient Class<T> responseClass;

    /**
     * Create or restore request
     * @param in parcel object to restore state or null to create new
     */
    protected Request(Parcel in) {
        if (in != null) {
            this.id = in.readString();
            this.forced = in.readByte() != 0;
        } else {
            id = UUID.randomUUID().toString();
        }
    }

    @SuppressWarnings("unchecked")
    public Class<T> getResponseClass() {
        if (responseClass == null) {
            responseClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return responseClass;
    }

    /**
     * Specify qualifier to differ responses of same class in cache
     * @return qualifier
     */
    public String getQualifier() {
        return null;
    }

    /**
     * Used for logging
     * @return request readable name
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * If you want to execute request in queue provide not null value. By default requests executed in parallel.
     * @return null to execute request on first available executor, or name of the request queue to put in.
     */
    public String getRequestSyncGroup() {
        return null;
    }

    /**
     * Control access to previously cached response. If cache contains response and
     * request not forced then cached value will be returned.
     * @param forced true if cached response should be ignored, false otherwise
     * @return current instance
     */
    public Request setForced(boolean forced) {
        this.forced = forced;
        return this;
    }

    /**
     * @return UUID of request
     */
    public String getId() {
        return id;
    }

    /**
     * @return true if cached response should be ignored, false otherwise
     */
    public boolean isForced() {
        return forced;
    }

    /**
     * @param status event status
     * @param responseOrFailure event value
     * @return event instance with status and responseOrFailure.
     * Return {@link com.shaubert.network.service.impl.DefaultEvent DefaultEvent} if bus is not supported in your project.
     */
    public abstract RSEvent<T, F> produceEvent(RSEvent.Status status, Object responseOrFailure);

    /**
     * Executes this request. The members of this task instance have been injected
     * prior to calling this method.
     * @param callback to report result to
     */
    public abstract void execute(ResultCallback<T, F> callback);

    /**
     * Called when request is cancelled
     */
    public abstract void onCancelled();

    /**
     * Start request service to execute this request. Request will
     * be put in Intent as Parcelable and sent to service.
     * NOTE event with {@link com.shaubert.network.service.RSEvent.Status#RUNNING RUNNING} state
     * will be posted in bus in this call.
     * @param context
     */
    public void startOnService(Context context) {
        RequestService.start(this, context);
    }

    /**
     * Cancel current request (by id)
     * @param context
     */
    public void cancel(Context context) {
        RequestService.cancel(context, getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByte(forced ? (byte) 1 : (byte) 0);
        writeChildToParcel(dest, flags);
    }

    public abstract void writeChildToParcel(Parcel dest, int flags);

}