package com.carworkz.dearo.addjobcard.createjobcard.inspection.pojo

import com.carworkz.dearo.domain.entities.InspectionItem

/**
 * Created by Farhan on 23/8/17.
 */
class InspectionViewPOJOCreator {

    companion object {

        fun getInspectionViewObj(inspectionItems: List<InspectionItem>): List<InspectionItemPOJO> {
            inspectionItems.sortedBy { it.position }
            val viewList = ArrayList<InspectionItemPOJO>()
            val map = inspectionItems.groupBy { it.parent }
            map.forEach { entry ->
                val list = ArrayList<InspectionSubItemPOJO>()
                entry.value.forEach {
                    list.add(InspectionSubItemPOJO(it.id, it.text, it.condition))
                }
                viewList.add(InspectionItemPOJO(entry.key, list))
            }
            return viewList
        }
    }
}