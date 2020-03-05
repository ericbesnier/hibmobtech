package com.hibernatus.hibmobtech.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;
import java.util.Set;

/**
 * Created by Eric on 27/12/2015.
 */
public class MroRequest implements ClusterItem {

    public static final String MRO_REQUEST_ID_KEY = "mroRequestId";

    public enum Status {
        OPENED,
        ASSIGNED,       // operator has been assigned to the job
        PLANNED,        // work is planned
        IN_PROGRESS,    // work on site is on going
        FINISHED,       // work on site is done
        CLOSED,         // billing done
        CANCELED
    }

    private Long id;
    private Status status;
    private Date requestDate;
    private Date closedDate;
    private Date takenDate;

    private Machine machine;
    private Site site;
    private Set<Problem> problems;
    private String note;
    private Set<Operator> operators;
    private Operation operation;
    private Long entityVersion;

    private AccessInfos accessInfos;
    private String realizedQuotationNumber;
    private Long takenBy;
    private Boolean sparepartNeeded;

    public int profilePhoto;
    private LatLng latLng;
    private String name;
    private Boolean quotationRequested;

    public enum MroType {
        PREVISIT,
        INSTALLATION,
        MAINTENANCE,
        DELIVERY,
    }

    private MroType mroType;

    public MroRequest() {
    }

    public MroRequest(LatLng position, String name, int pictureResource) {
        this.name = name;
        this.profilePhoto = pictureResource;
        this.latLng = position;
    }

    public MroRequest(LatLng position) {
        this.latLng = position;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    // G e t t e r s   &   S e t t e r s
    // ---------------------------------

    public AccessInfos getAccessInfos() {
        return accessInfos;
    }

    public void setAccessInfos(AccessInfos accessInfos) {
        this.accessInfos = accessInfos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Set<Problem> getProblems() {
        return problems;
    }

    public void setProblems(Set<Problem> problems) {
        this.problems = problems;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<Operator> getOperators() {
        return operators;
    }

    public void setOperators(Set<Operator> operators) {
        this.operators = operators;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public String getRealizedQuotationNumber() {
        return realizedQuotationNumber;
    }

    public void setRealizedQuotationNumber(String realizedQuotationNumber) {
        this.realizedQuotationNumber = realizedQuotationNumber;
    }

    public Long getTakenBy() {
        return takenBy;
    }

    public void setTakenBy(Long takenBy) {
        this.takenBy = takenBy;
    }

    public void setProfilePhoto(int profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProfilePhoto() {
        return profilePhoto;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public Boolean getQuotationRequested() {
        return quotationRequested;
    }

    public void setQuotationRequested(Boolean quotationRequested) {
        this.quotationRequested = quotationRequested;
    }

    public MroType getMroType() {
        return mroType;
    }

    public void setMroType(MroType mroType) {
        this.mroType = mroType;
    }

    public Boolean getSparepartNeeded() {
        return sparepartNeeded;
    }

    public void setSparepartNeeded(Boolean sparepartNeeded) {
        this.sparepartNeeded = sparepartNeeded;
    }

    public Date getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Date takenDate) {
        this.takenDate = takenDate;
    }
}
