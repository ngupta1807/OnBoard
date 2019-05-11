package com.sample.app.param;

public class BWord {

    private String title;
    private int id;

    public BWord() {
    }

    public BWord(int id,String title) {
        this.id=id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
