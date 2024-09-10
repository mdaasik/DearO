package com.carworkz.dearo.addjobcard.createjobcard.inspection.pojo;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Farhan on 23/8/17.
 */

public class InspectionItemPOJO extends ExpandableGroup<InspectionSubItemPOJO> {
    public InspectionItemPOJO(String title, List<InspectionSubItemPOJO> items) {
        super(title, items);
    }


}
