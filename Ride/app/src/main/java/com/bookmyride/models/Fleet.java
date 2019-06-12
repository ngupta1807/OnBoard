package com.bookmyride.models;

import java.io.Serializable;

/**
 * Created by vinod on 6/28/2017.
 */
public class Fleet implements Serializable {
    int id, status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    String name;
}
