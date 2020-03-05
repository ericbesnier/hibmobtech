package com.hibernatus.hibmobtech.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonSites implements Parcelable {
    public static final String TAG = "PersonSites";
    int orderNumber;
    String role;
    Person person;

    public PersonSites(int orderNumber, String role, Person person) {

        this.orderNumber = orderNumber;
        this.role = role;
        this.person = person;
    }

    protected PersonSites(Parcel in) {
        orderNumber = in.readInt();
        role = in.readString();
    }

    public static final Creator<PersonSites> CREATOR = new Creator<PersonSites>() {
        @Override
        public PersonSites createFromParcel(Parcel in) {
            return new PersonSites(in);
        }

        @Override
        public PersonSites[] newArray(int size) {
            return new PersonSites[size];
        }
    };

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderNumber);
        dest.writeString(role);
    }
}
