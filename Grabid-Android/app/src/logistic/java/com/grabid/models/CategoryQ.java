package com.grabid.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by graycell on 22/11/17.
 */

public class CategoryQ  implements Parcelable{
    String id, name;

    public CategoryQ(Parcel in) {
        id = in.readString();
        name = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<CategoryQ> CREATOR = new Creator<CategoryQ>() {
        @Override
        public CategoryQ createFromParcel(Parcel in) {
            return new CategoryQ(in);
        }

        @Override
        public CategoryQ[] newArray(int size) {
            return new CategoryQ[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    boolean isSelected;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
