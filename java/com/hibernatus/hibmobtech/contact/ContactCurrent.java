package com.hibernatus.hibmobtech.contact;

import android.util.Log;

import com.hibernatus.hibmobtech.model.contact.Contact;


/**
 * Created by Eric on 15/01/2016.
 */
public class ContactCurrent {
    public static final String TAG = ContactCurrent.class.getSimpleName();
    private Contact contactCurrent;
    private ContactCurrent(){}
    private boolean isContactCheckable = false;

    private static ContactCurrent INSTANCE = new ContactCurrent();

    public void initContactCurrent() {
        Log.i(TAG, "initContactCurrent");
        contactCurrent = null;
        isContactCheckable = false;
    }

    public boolean isContactCheckable() {
        Log.i(TAG, "isContactCheckable=" + isContactCheckable);
        return isContactCheckable;
    }

    public void setIsContactCheckable(boolean isContactCheckable) {
        Log.i(TAG, "setIsContactCheckable:isContactCheckable=" + isContactCheckable);
        this.isContactCheckable = isContactCheckable;
    }

    public boolean isCurrentContact() {
        return contactCurrent != null;
    }

    public static ContactCurrent getInstance()
    {	return INSTANCE;
    }

    public Contact getContactCurrent() {
        Log.i(TAG, "getContactCurrent:currentRequest: id="
                + contactCurrent.getId()
                + " isContactCheckable="
                + isContactCheckable);
        return contactCurrent;
    }

    public void setContactCurrent(Contact contactCurrent) {
        this.contactCurrent = contactCurrent;
        if(contactCurrent != null)
            Log.i(TAG, "setContactCurrent:currentRequest: id="
                    + this.contactCurrent.getId()
                    + " isContactCheckable="
                    + isContactCheckable);
    }
}

