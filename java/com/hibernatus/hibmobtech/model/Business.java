package com.hibernatus.hibmobtech.model;

import android.os.Parcel;

public class Business extends ContactDetail {
    private String businessName;
    private String serviceName;
    private HibmobtechAddress hibmobtechAddress;
    private Long entityVersion;

    protected Business(Parcel in) {
        super(in);
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

    public HibmobtechAddress getHibmobtechAddress() {
        return hibmobtechAddress;
    }

    public void setHibmobtechAddress(HibmobtechAddress hibmobtechAddress) {
        this.hibmobtechAddress = hibmobtechAddress;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }
}
