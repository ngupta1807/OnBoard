package com.tipbox.app.modle;

public class HistoryModle {
    private String date;

    private String price;

    private String name;

    private String time;
    private String t_id;
    private String type;

    public HistoryModle(String date,String price,String name,String time,String t_id,String type){
        this.date = date;
        this.price = price;
        this.name = name;
        this.time = time;
        this.t_id = t_id;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}