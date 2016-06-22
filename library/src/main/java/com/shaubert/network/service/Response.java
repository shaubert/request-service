package com.shaubert.network.service;

import android.os.Parcel;

public abstract class Response {

    private long creationTime;

    /**
     * Create or restore request
     * @param in parcel object to restore state or null to create new
     */
    protected Response(Parcel in) {
        if (in != null) {
            this.qualifier = in.readString();
            this.creationTime = in.readLong();
        } else {
            this.creationTime = System.currentTimeMillis();
        }
    }

    /**
     * Called after Response is created and parsed.
     */
    public void onParsed() {
    }

    private String qualifier;

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public long getCreationTime() {
        return creationTime;
    }

    protected void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.qualifier);
        dest.writeLong(this.creationTime);
    }

}