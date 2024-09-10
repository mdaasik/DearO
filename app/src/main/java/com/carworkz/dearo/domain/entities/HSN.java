package com.carworkz.dearo.domain.entities;

import com.carworkz.dearo.searchabledialog.Searchable;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;

/**
 * Created by kush on 27/10/17.
 */

public class HSN extends RealmObject implements Searchable {

    public static final String SELECT = "select";
    public static final String HSN_CODE = "HSN CODE";

    @Json(name = "hsn")
    private String hsn;
    @Json(name = "name")
    private String name;
    @Json(name = "description")
    private String description;
    @Json(name = "cgst")
    private Double cgst = 0.0;
    @Json(name = "sgst")
    private Double sgst = 0.0;

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCgst() {
        return cgst;
    }

    public void setCgst(Double cgst) {
        this.cgst = cgst;
    }

    public Double getSgst() {
        return sgst;
    }

    public void setSgst(Double sgst) {
        this.sgst = sgst;
    }

    public String getDisplayText() {
        if (hsn == null || name == null) {
            return SELECT + " : " + HSN_CODE;
        } else {
            return hsn + " : " + name;
        }
    }

    @Override
    public String toString() {
        return "HSN :" + getHsn() ;
    }

    @NotNull
    @Override
    public String getText() {
        return getDisplayText();
    }
}

