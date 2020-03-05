package com.hibernatus.hibmobtech.model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class HibmobtechAddress implements Parcelable {
    public static final String TAG = HibmobtechAddress.class.getSimpleName();

    String building;
    String street;
    String postalCode;
    String city;
    String country;
    String bp;
    String cedex;
    Context context;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(building);
        dest.writeString(street);
        dest.writeString(postalCode);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(bp);
        dest.writeString(cedex);
    }

    public static final Creator<HibmobtechAddress> CREATOR = new Creator<HibmobtechAddress>() {
        @Override
        public HibmobtechAddress createFromParcel(Parcel in) {
            return new HibmobtechAddress(in);
        }

        @Override
        public HibmobtechAddress[] newArray(int size) {
            return new HibmobtechAddress[size];
        }
    };

    // C o n s t r u c t o r
    // ---------------------
    protected HibmobtechAddress(Parcel in) {
        building = in.readString();
        street = in.readString();
        postalCode = in.readString();
        city = in.readString();
        country = in.readString();
        bp = in.readString();
        cedex = in.readString();
    }

    // O t h e r s   m e t h o d s
    // ---------------------------
    public String getAdress() {
        String mbuilding = "";
        String mstreet = "";
        String mpostalCode = "";
        String mcity = "";

        if(!TextUtils.isEmpty(building)) mbuilding = building + " ";
        if(!TextUtils.isEmpty(street)) mstreet = street + " ";
        if(!TextUtils.isEmpty(postalCode)) mpostalCode = postalCode + " ";
        if(!TextUtils.isEmpty(city)) mcity = city + " ";

        return mbuilding + mstreet + mpostalCode + mcity;
    }

    public String getAdressPart1() {
        String mbuilding = "";
        String mstreet = "";
        if(!TextUtils.isEmpty(building)) mbuilding = building + " ";
        if(!TextUtils.isEmpty(street)) mstreet = street + " ";
        return mbuilding + mstreet;
    }

    public String getAdressPart2() {
        String mpostalCode = "";
        String mcity = "";
        if(!TextUtils.isEmpty(postalCode)) mpostalCode = postalCode + " ";
        if(!TextUtils.isEmpty(city)) mcity = city + " ";
        return mpostalCode + mcity;
    }

    // G e t t e r s   &   s e t t e r s
    // ---------------------------------
    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getCedex() {
        return cedex;
    }

    public void setCedex(String cedex) {
        this.cedex = cedex;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
