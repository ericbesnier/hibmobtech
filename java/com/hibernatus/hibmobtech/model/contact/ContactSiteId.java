package com.hibernatus.hibmobtech.model.contact;

import com.hibernatus.hibmobtech.model.Site;

/**
 * Created by Eric on 23/01/2016.
 */
public class ContactSiteId {
    private static final long serialVersionUID = 392764452531137265L;
    private Contact contact;
    private Site site;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
