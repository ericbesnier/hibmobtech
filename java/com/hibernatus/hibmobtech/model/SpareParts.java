package com.hibernatus.hibmobtech.model;

/**
 * Created by Eric on 30/06/2016.
 */
public class SpareParts {
    private int quantity = 0;
    private SparePart sparePart;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public SparePart getSparePart() {
        return sparePart;
    }

    public void setSparePart(SparePart sparePart) {
        this.sparePart = sparePart;
    }
}
