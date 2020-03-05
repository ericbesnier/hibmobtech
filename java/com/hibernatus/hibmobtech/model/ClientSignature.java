package com.hibernatus.hibmobtech.model;

import android.util.Log;

/**
 * Created by Eric on 01/06/2016.
 */
public class ClientSignature {
    public static final String TAG = ClientSignature.class.getSimpleName();

    private Long id;
    private String parentKey;
    private Long idMroRequest;
    private byte[] signatureFile;
    private int resendNumber;
    private SignatureInfos infos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMroRequest() {
        return idMroRequest;
    }

    public void setIdMroRequest(Long idMroRequest) {
        this.idMroRequest = idMroRequest;
    }

    public byte[] getSignatureFile() {
        return signatureFile;
    }

    public void setSignatureFile(byte[] signatureFile) {
        this.signatureFile = signatureFile;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public SignatureInfos getInfos() {
        return infos;
    }

    public void setInfos(SignatureInfos infos) {
        this.infos = infos;
    }

    public int getResendNumber() {
        return resendNumber;
    }

    public void setResendNumber(int resendNumber) {
        Log.d(TAG, "setResendNumber: id="+ id + " resendNumber=" + resendNumber);
        this.resendNumber = resendNumber;
    }
}
