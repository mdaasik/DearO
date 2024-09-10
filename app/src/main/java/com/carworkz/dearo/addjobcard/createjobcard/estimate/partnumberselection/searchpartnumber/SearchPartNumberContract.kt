package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.PartNumber

interface SearchPartNumberContract {

    interface Presenter : BasePresenter {
        fun searchPartNumber(query: String, partId: String?, jobCardId: String?, vehicleType: String?,packageId:String?)
        fun searchInStockPartNumber(query: String, jobCardId: String?, brandId: String?, vehicleType: String?)
    }

    interface View : BaseView<Presenter> {
        fun displaySearchResults(partNumbers: List<PartNumber>)
    }
}