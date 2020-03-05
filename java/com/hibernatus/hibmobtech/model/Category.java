package com.hibernatus.hibmobtech.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private long id;
    private String name;
    private String description;
    private long color;

    public Category() {}
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected Category(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        color = in.readLong();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public long getId() {
        return id;
    }
    public long getColor() {
        return color;
    }
    public void setColor(long color) {
        this.color = color;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(color);
    }
}
