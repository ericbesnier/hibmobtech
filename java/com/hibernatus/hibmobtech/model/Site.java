package com.hibernatus.hibmobtech.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.hibernatus.hibmobtech.model.contact.ContactRoleSite;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Site {
    public static final String SITE_ID_KEY = "currentSiteId";

    public static final String TAG = Site.class.getSimpleName();
    private Long id;
    private Set<ContactRoleSite> contactRoleSites = new HashSet<>();
    SiteCategory category;
    private String name;
    private String type;
    private String businessName;
    private String serviceName;
    private String comments;
    private HibmobtechAddress address;
    private LatLng geoloc;
    private Set<Machine> machines = new HashSet<>(0);
    private Business business;
    private Long entityVersion;

    public void setGeoloc (Context context) throws IOException {
        Log.d(TAG, "setGeoloc: let's call Geocoder !");
        if(geoloc == null) geoloc = new LatLng();
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address.getAdress(), 1);
            if (addresses.size()>0) {
                android.location.Address address = addresses.get(0);
                geoloc.setLat(address.getLatitude());
                geoloc.setLng(address.getLongitude());
                Log.d(TAG, "setGeoloc: address=" + address
                        + " address.getLatitude()=" + address.getLatitude()
                        + " address.getLongitude()=" + address.getLongitude());
            }
            else {
                geoloc.setLat(48.853); // latitude Notre-Dame de Paris
                geoloc.setLng(2.35); // longitude Notre-Dame de Paris
                Log.w(TAG, "setGeoloc: Unknown Adress ! geoloc set to PARIS Notre-Dame" + geoloc.getLat()
                        + " geoloc.getLng()=" + geoloc.getLng());
            }
        } catch (Exception e) {
            Log.e(TAG, "setGeoloc: ERROR" + e.getMessage());

        }
    }

    public static String getSiteIdKey() {
        return SITE_ID_KEY;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ContactRoleSite> getContactRoleSites() {
        return contactRoleSites;
    }

    public void setContactRoleSites(Set<ContactRoleSite> contactRoleSites) {
        this.contactRoleSites = contactRoleSites;
    }

    public SiteCategory getCategory() {
        return category;
    }

    public void setCategory(SiteCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public HibmobtechAddress getAddress() {
        return address;
    }

    public void setAddress(HibmobtechAddress address) {
        this.address = address;
    }

    public LatLng getGeoloc() {
        return geoloc;
    }

    public void setGeoloc(LatLng geoloc) {
        this.geoloc = geoloc;
    }

    public Set<Machine> getMachines() {
        return machines;
    }

    public void setMachines(Set<Machine> machines) {
        this.machines = machines;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }
}

