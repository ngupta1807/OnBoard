package com.bookmyride.models;

import java.io.Serializable;

/**
 * Created by vinod on 2017-01-19.
 */
public class Cars implements Serializable {

    String id;
    String name;
    String icon;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(String selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    String selectedIcon;
    boolean isDefault;

    public boolean isFleetSelected() {
        return isFleetSelected;
    }

    public void setFleetSelected(boolean fleetSelected) {
        isFleetSelected = fleetSelected;
    }

    boolean isFleetSelected;

}
