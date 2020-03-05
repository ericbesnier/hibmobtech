package com.hibernatus.hibmobtech.utils;

import retrofit.mime.TypedString;

/**
 * Created by Eric on 11/10/2016.
 */

public class TypedJsonString extends TypedString {
    public TypedJsonString(String body) {
        super(body);
    }

    @Override public String mimeType() {
        return "application/json";
    }
}
