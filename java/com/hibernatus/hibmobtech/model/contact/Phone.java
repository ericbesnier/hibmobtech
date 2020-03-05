package com.hibernatus.hibmobtech.model.contact;

/**
 * Created by Eric on 23/01/2016.
 */
public class Phone {
    public static enum Type {
        phone, mobile, fax
    }

    String tag;
    String number;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
