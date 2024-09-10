

/*
 * Created by ambab on 24/8/17.
 */
package com.carworkz.dearo.domain.entities;

import java.util.List;

import com.squareup.moshi.Json;

public class InspectionPostPOJO {

    @Json(name = "inspectionGroup")
    private String inspectionGroup;
    @Json(name = "inspection")
    private List<InspectionItem> inspectionItems = null;



    public List<InspectionItem> getInspectionItems() {
        return inspectionItems;
    }

    public void setInspectionItems(List<InspectionItem> suggestedJobs) {
        this.inspectionItems = suggestedJobs;
    }

    public String getInspectionGroup() {
        return inspectionGroup;
    }

    public void setInspectionGroup(String inspectionGroup) {
        this.inspectionGroup = inspectionGroup;
    }
}
