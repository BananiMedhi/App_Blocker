package com.appblocker;

import java.io.Serializable;

/**
 * Created by banani on 5/30/2016.
 */
public class TimeModel {
    long startTime;
    long endTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
