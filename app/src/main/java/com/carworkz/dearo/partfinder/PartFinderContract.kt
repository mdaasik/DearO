package com.carworkz.dearo.partfinder

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.PartNumber

interface PartFinderContract {

    interface Presenter : BasePresenter {

        fun searchPart(query: String, make: String, model: String, variant: String, fuelType: String, vehicleType: String?)
    }

    interface View : BaseView<Presenter> {

        fun onSearchResultReceived(partNumbers: List<PartNumber>)
    }
}