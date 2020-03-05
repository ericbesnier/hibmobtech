package com.hibernatus.hibmobtech.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Eric on 23/11/2015.
 */
public class Machine {
    private Long id;
    private String serialNumber;
    private Equipment equipment;
    private Site site;
    private Date purchaseDate;
    private Double purchasePrice;
    private Date installDate;
    private String comments;
    private String numMachine;
    private Long numero;
    private Long entityVersion;
    List<MroRequest> mros;

    public Machine() {
        serialNumber = "";
        equipment = new Equipment();
        site = new Site();
        comments = "";
        numMachine = "";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public List<MroRequest> getMros() {
        return mros;
    }

    public void setMros(List<MroRequest> mros) {
        this.mros = mros;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getNumMachine() {
        return numMachine;
    }

    public void setNumMachine(String numMachine) {
        this.numMachine = numMachine;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }
}
