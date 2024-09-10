package com.carworkz.dearo.addjobcard.addeditvehicle

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

/**
 * Created by kush on 18/8/17.
 */

interface VehicleDetailsContract {

    interface View : BaseView<Presenter> {

        fun moveToNextScreen()

        fun displayMake(makes: List<Make>)

        fun displayModel(models: List<Model>)

        fun displayVariant(variants: List<Variant>)

        fun displayCompanySuggestions(companyList: List<InsuranceCompany>)
    }

    interface Presenter : BasePresenter {
        fun getMake(vehicleType: String?)

        fun getModel(slug: String)

        fun getVariant(fuelType: String?, slug: String)

        fun addVehicle(vehicle: Vehicle)

        fun updateVehicle(vehicle: Vehicle)

        fun getCompanyList()
    }
}
