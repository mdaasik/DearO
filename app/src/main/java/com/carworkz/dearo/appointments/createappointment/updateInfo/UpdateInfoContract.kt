package com.carworkz.dearo.appointments.createappointment.updateInfo

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.UpsertDetails
import com.carworkz.dearo.domain.entities.Variant

/**
 * Created by Farhan on 8/1/18.
 */
interface UpdateInfoContract {

    interface View : BaseView<Presenter> {

        fun cityError(error: String)

        fun displayVariant(variantList: List<Variant>)

        fun displayCity(city: String)

        fun moveToNextScreen(upsertDetails: UpsertDetails)
    }
    interface Presenter : BasePresenter {

        fun upsertData(appointmentId: String, upsertDetails: UpsertDetails)

        fun getCity(pincode: Int)

        fun getVariant(fuelType: String, slug: String)
    }
}