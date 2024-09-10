package com.carworkz.dearo.appointments.appointmentdetails

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Appointment

/**
 * Created by Farhan on 10/1/18.
 */
interface AppointmentCardDetailsContract {
    interface View : BaseView<Presenter> {
        fun displayDetails(appointment: Appointment)
    }

    interface Presenter : BasePresenter {
        fun getDetails(appointmentId: String)
    }
}