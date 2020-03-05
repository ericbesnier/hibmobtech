package com.hibernatus.hibmobtech.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eric on 09/11/2015.
 */
public class ContactDetail implements Parcelable{
    private long id;
    private String label;
    ContactType type;
    private String value;
    private Set<ContactOwner> owners = new HashSet<>();


    protected ContactDetail(Parcel in) {
        id = in.readLong();
        label = in.readString();
        value = in.readString();
    }

    public static final Creator<ContactDetail> CREATOR = new Creator<ContactDetail>() {
        @Override
        public ContactDetail createFromParcel(Parcel in) {
            return new ContactDetail(in);
        }

        @Override
        public ContactDetail[] newArray(int size) {
            return new ContactDetail[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(label);
        dest.writeString(value);
    }
}
