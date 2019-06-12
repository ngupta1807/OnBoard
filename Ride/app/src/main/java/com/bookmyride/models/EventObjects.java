package com.bookmyride.models;

import java.util.Date;

/**
 * Created by vinod on 6/6/2017.
 */
public class EventObjects {
    private int id;
    private String message;
    private Date date;

    public EventObjects(int id,String message, Date date) {
        this.date = date;
        this.message = message;
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
    public Date getDate() {
        return date;
    }
}
