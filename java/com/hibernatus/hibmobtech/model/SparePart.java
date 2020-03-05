package com.hibernatus.hibmobtech.model;

/**
 * Created by Eric on 30/06/2016.
 */
public class SparePart extends BasicRefEntity {

    private boolean active = true;
    private String reference;
    private String brand;

    // private Long id;
    // MroType Materiel
    // private String category;
    // designation length = 512
    // private String description;
    Double price;
    String formattedPrice;


    public String getFormattedPrice() {
        return formattedPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
