package com.carworkz.dearo.domain.entities;

import java.util.List;
import com.squareup.moshi.Json;

public class InspectionGroup {

    @Json(name = "id")
    private String id;
    @Json(name = "name")
    private String name;
    @Json(name = "inspectionIds")
    private List<String> inspectionIds = null;

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

    public List<String> getInspectionIds() {
        return inspectionIds;
    }

    public void setInspectionIds(List<String> inspectionIds) {
        this.inspectionIds = inspectionIds;
    }

}