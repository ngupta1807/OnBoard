package com.bookmyride.models;

/**
 * Created by vinod on 3/15/2017.
 */
public class WaitingInfo {
    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTimeSwapBuff() {
        return timeSwapBuff;
    }

    public void setTimeSwapBuff(long timeSwapBuff) {
        this.timeSwapBuff = timeSwapBuff;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        this.mins = mins;
    }

    public int getSecs() {
        return secs;
    }

    public void setSecs(int secs) {
        this.secs = secs;
    }

    private long updatedTime, timeInMilliseconds, startTime, timeSwapBuff;

    private int mins, secs;
}
