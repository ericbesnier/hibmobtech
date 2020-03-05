package com.hibernatus.hibmobtech.network.restError;

/**
 * Created by tgo on 25/10/15.
 */
public class RestError
{
    private Integer code;

    private String errorMessage;

    public RestError(String message)
    {
        this.errorMessage = message;
    }

    //Getters and setters

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}