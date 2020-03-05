package com.hibernatus.hibmobtech.model.contact;

/**
 * Created by Eric on 23/01/2016.
 */
public class ContactRoleSite {
    //private ContactSiteId primaryKey;
    Contact contact;
    private int orderNumber;
    private String role;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

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
}
