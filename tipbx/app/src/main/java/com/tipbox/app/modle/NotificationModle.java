package com.tipbox.app.modle;

public class NotificationModle {
    private String date;

    private String price;

    private String name;

    private String time;

    public NotificationModle(String date, String price, String name, String time){
        this.date = date;
        this.price = price;
        this.name = name;
        this.time = time;
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
}