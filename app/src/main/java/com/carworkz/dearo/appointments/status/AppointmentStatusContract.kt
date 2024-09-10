package com.carworkz.dearo.appointments.status

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentStatus

interface AppointmentStatusContract {
    interface View : BaseView<Presenter> {
        fun onFetchAppointment(appointment: Appointment)
        fun onSaveSuccess()
    }

    interface Presenter : BasePresenter {
        fun saveDetails(details: AppointmentStatus)
        fun getAppointmentById(appointmentId: String)
    }
}