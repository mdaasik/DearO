package com.carworkz.dearo.invoices.addItem.part

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.BrandName
import com.carworkz.dearo.domain.entities.HSN
import com.carworkz.dearo.domain.entities.Part
import com.carworkz.dearo.domain.entities.PartNumber

/**
 * Created by kush on 13/9/17.
 */

interface AddPartContract {
    interface Presenter : BasePresenter {

        fun getHSN()

        fun fetchBrandName(query: String, jobCardId: String, partNumber: String?, vehicleType: String?)

        fun fetchPartNumber(query: String, partId: String?, jobCardId: String, brandId: String?, vehicleType: String?)

        fun savePart(id: String, part: Part)

        fun deletePart(invoiceId: String, partId: String)
    }

    interface View : BaseView<Presenter> {

        fun displayHsnList(hsnList: MutableList<HSN>)

        fun displayBrandNames(brandNameList: List<BrandName>)

        fun displayPartNumbers(partNumberList: List<PartNumber>)

        fun brandError(error: String)

        fun partNumberError(error: String)

        fun moveToNextScreen()

        fun onPartSavedSuccess(part: Part)

        fun onPartDeleted()
    }
}
