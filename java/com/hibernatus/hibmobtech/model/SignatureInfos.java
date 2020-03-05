package com.hibernatus.hibmobtech.model;

import java.util.Date;

/**
 * Created by Eric on 24/02/2017.
 */

public class SignatureInfos {
    protected String signerName;
    protected String signerRole;
    protected Date timestamp;

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public String getSignerRole() {
        return signerRole;
    }

    public void setSignerRole(String signerRole) {
        this.signerRole = signerRole;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
