package com.hibernatus.hibmobtech.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eric on 09/02/2016.
 */
public abstract class ContactOwner {

    Long id;

    Set<ContactDetail> contactDetails = new HashSet<>(0);

    public ContactOwner() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(Set<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }


}
