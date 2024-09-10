package com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Variant
import com.carworkz.dearo.domain.entities.VehicleVariantBody

interface AddMissingVehicleInfoContract {

    interface View : BaseView<Presenter> {

        fun onUpdateSuccess()

        fun displayFuelTypes(fuelTypes: List<String>)

        fun displayDescriptions(descriptions: MutableList<String>)

        fun displayVariants(variants: MutableList<Variant>)

        fun displayTransmission(transmissions: MutableList<String>)
    }

    interface Presenter : BasePresenter {

        fun saveVariantDetails(vehicleId: String, variantBody: VehicleVariantBody)

        fun getFuelTypes(modelSlug: String)

        fun getDescriptions(fuelType: String)

        fun getVariants(description: String?, fuelType: String)

        fun getTransmissions(description: String?, fuelType: String)
    }
}