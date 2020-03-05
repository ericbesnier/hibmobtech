package com.hibernatus.hibmobtech.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * Created by Eric on 08/01/2016.
 */
public class Operation {
    private Date plannedDate;  // pas utilisée
    private Date mroStartDate; // démarrage de l'inter
    private Date mroFinishDate; // terminaison par le tech de l'inter
    private Date date; // date de plannification
    Operator mroOperator;
    private Set<Operator> operators;
    private Set<Cause> causes;
    private Set<Task> tasks;
    private String note;
    private Float laborUnits;
    private Double laborCost;
    private Float visitUnit;
    private Double visitCost;
    private Float vat;
    private Double creditVoucher;

    private ArrayList<Picture> pictures;
    private Set<SpareParts> spareParts;

    public Operator getMroOperator() {
        return mroOperator;
    }

    public void setMroOperator(Operator mroOperator) {
        this.mroOperator = mroOperator;
    }

    public Set<SpareParts> getSpareParts() {
        return spareParts;
    }

    public void setSpareParts(Set<SpareParts> spareParts) {
        this.spareParts = spareParts;
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Operator> getOperators() {
        return operators;
    }

    public void setOperators(Set<Operator> operators) {
        this.operators = operators;
    }

    public Set<Cause> getCauses() {
        return causes;
    }

    public void setCauses(Set<Cause> causes) {
        this.causes = causes;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Float getLaborUnits() {
        return laborUnits;
    }

    public void setLaborUnits(Float laborUnits) {
        this.laborUnits = laborUnits;
    }

    public Double getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(Double laborCost) {
        this.laborCost = laborCost;
    }

    public Float getVisitUnit() {
        return visitUnit;
    }

    public void setVisitUnit(Float visitUnit) {
        this.visitUnit = visitUnit;
    }

    public Double getVisitCost() {
        return visitCost;
    }

    public void setVisitCost(Double visitCost) {
        this.visitCost = visitCost;
    }

    public Float getVat() {
        return vat;
    }

    public void setVat(Float vat) {
        this.vat = vat;
    }

    public Double getCreditVoucher() {
        return creditVoucher;
    }

    public void setCreditVoucher(Double creditVoucher) {
        this.creditVoucher = creditVoucher;
    }

    public Date getMroStartDate() {
        return mroStartDate;
    }

    public void setMroStartDate(Date mroStartDate) {
        this.mroStartDate = mroStartDate;
    }

    public Date getMroFinishDate() {
        return mroFinishDate;
    }

    public void setMroFinishDate(Date mroFinishDate) {
        this.mroFinishDate = mroFinishDate;
    }
}
