package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by kush on 8/11/17.
 */

public class BrandName {

    @Json(name = "id")
    private String id;
    @Json(name = "name")
    private String name;
    @Json(name = "partNumbers")
    private List<String> partNumbers = null;
    @Json(name = "ownerType")
    private String ownerType;
    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "updatedOn")
    private String updatedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPartNumbers() {
        return partNumbers;
    }

    public void setPartNumbers(List<String> partNumbers) {
        this.partNumbers = partNumbers;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

}

