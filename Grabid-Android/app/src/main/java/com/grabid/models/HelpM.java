package com.grabid.models;

/**
 * Created by graycell on 3/11/17.
 */

public class HelpM {
    public boolean Category;
    public String categoryType, categoryTitle, categorydescription;
    public boolean Isselected;

    public boolean isCategory() {
        return Category;
    }

    public boolean isselected() {
        return Isselected;
    }

    public void setIsselected(boolean isselected) {
        Isselected = isselected;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategorydescription() {
        return categorydescription;
    }

    public void setCategorydescription(String categorydescription) {
        this.categorydescription = categorydescription;
    }

    public void setCategory(boolean category) {
        Category = category;
    }


}
