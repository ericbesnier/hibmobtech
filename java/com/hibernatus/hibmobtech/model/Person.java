package com.hibernatus.hibmobtech.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eric on 07/11/2015.
 */
public class Person implements Parcelable {
    private int orderNumber;
    private String role;
    private int id;
    private String title;
    private String firstname;
    private String lastname;
    private String fullname;
    private String primaryPhoneNumber;
    private String mobilePhoneNumber;
    private String email;

    protected Person(Parcel in) {
        orderNumber = in.readInt();
        role = in.readString();
        id = in.readInt();
        title = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        fullname = in.readString();
        primaryPhoneNumber = in.readString();
        mobilePhoneNumber = in.readString();
        email = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderNumber);
        dest.writeString(role);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(fullname);
        dest.writeString(primaryPhoneNumber);
        dest.writeString(mobilePhoneNumber);
        dest.writeString(email);
    }
}
