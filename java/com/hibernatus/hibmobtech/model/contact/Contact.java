package com.hibernatus.hibmobtech.model.contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Eric on 23/01/2016.
 */
public class Contact {
    Long id;
    String title;
    String name;
    String fullname; // as stored in files...
    String role;
    String notes;
    List<Phone> phones = new ArrayList<>(0);
    List<Email> emails = new ArrayList<>(0);
    private Set<ContactRoleSite> contactRoleSites = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public Set<ContactRoleSite> getContactRoleSites() {
        return contactRoleSites;
    }

    public void setContactRoleSites(Set<ContactRoleSite> contactRoleSites) {
        this.contactRoleSites = contactRoleSites;
    }
}
