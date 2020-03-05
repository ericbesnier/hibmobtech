package com.hibernatus.hibmobtech.model;

import java.util.Date;

/**
 * Created by Eric on 23/11/2015.
 */
public class Invoice {
    private Long id;
    private String number; // numéro de facture. Exple : F-001-ABCD
    private Date issuedDate; // date d'émission
    private Long amount; // montant de la facture
    private boolean state; // état : payée > true / impayée > false
    private Date paidDate ; // date de règlement


    public Invoice() {}

    public Invoice(String number, Date issuedDate, Long amount, Date paidDate) {
        this.number = number;
        this.issuedDate = issuedDate;
        this.amount = amount;
        this.paidDate = paidDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }
}
