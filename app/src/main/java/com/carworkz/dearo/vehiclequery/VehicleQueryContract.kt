package com.carworkz.dearo.vehiclequery

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Make
import com.carworkz.dearo.domain.entities.Model
import com.carworkz.dearo.domain.entities.Variant

interface VehicleQueryContract {

    interface View : BaseView<Presenter> {
        fun displayMake(obj: List<Make>)
        fun displayModel(model: List<Model>)
        fun displayVariant(variant: List<Variant>)
    }

    interface Presenter : BasePresenter {
        fun getMake(vehicleType: String?)
        fun getModel(makeSlug: String)
        fun getVariant(modelSlug: String)
    }
}
