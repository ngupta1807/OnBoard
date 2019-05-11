package com.grabid.models;

/**
 * Created by graycell on 14/11/17.
 */

public class Jobs {
    String JobType, Asap, RoundTrip, totalqty, paymentmode;

    public String getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(String paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getJobType() {
        return JobType;
    }

    public void setJobType(String jobType) {
        JobType = jobType;
    }

    public String getAsap() {
        return Asap;
    }

    public void setAsap(String asap) {
        Asap = asap;
    }

    public String getRoundTrip() {
        return RoundTrip;
    }

    public void setRoundTrip(String roundTrip) {
        RoundTrip = roundTrip;
    }

    public String getTotalqty() {
        return totalqty;
    }

    public void setTotalqty(String totalqty) {
        this.totalqty = totalqty;
    }
}
