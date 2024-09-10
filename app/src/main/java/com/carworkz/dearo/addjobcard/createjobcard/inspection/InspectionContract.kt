package com.carworkz.dearo.addjobcard.createjobcard.inspection

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.InspectionGroup
import com.carworkz.dearo.domain.entities.InspectionItem
import com.carworkz.dearo.domain.entities.InspectionPostPOJO

/**
 * Created by Farhan on 23/8/17.
 */
interface InspectionContract {

    interface View : BaseView<Presenter> {

        fun displayInspectionGroups(groups: List<InspectionGroup>)

        fun displayInspectionItems(items: List<InspectionItem>)

        fun displaySelectedInspectionItems(selectedGroupId: String?, selectedItems: List<InspectionItem>?)

        fun displayViewOnly(selectedGroupId: String?, selectedItems: List<InspectionItem>?)

        fun moveToNextScreen()
    }

    interface Presenter : BasePresenter {

        fun getInspectionGroups(vehicleType: String?)

        fun getInspectionItemByGroup(groupSlug: String)

        fun getSavedInspection(jobCardId: String)

        fun saveInspection(jobCardId: String, obj: InspectionPostPOJO)
    }
}