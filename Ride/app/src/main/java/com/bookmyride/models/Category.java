package com.bookmyride.models;

import java.io.Serializable;

/**
 * Created by vinod on 2017-01-08.
 */
public class Category implements Serializable {
    String id, name;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    boolean isSelected;
}
