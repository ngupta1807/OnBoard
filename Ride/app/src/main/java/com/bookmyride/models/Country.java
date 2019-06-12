package com.bookmyride.models;

import java.io.Serializable;

/**
 * Created by vinod on 2017-01-08.
 */
public class Country implements Serializable {
    // *********** Declare Used Variables *********//*
    public String id;
    public String name;
    public String code;
    //*********  used to set or get data ************//*

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
