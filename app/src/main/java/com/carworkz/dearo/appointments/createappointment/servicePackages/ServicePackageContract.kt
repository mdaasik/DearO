package com.carworkz.dearo.appointments.createappointment.servicePackages

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentPost
import com.carworkz.dearo.domain.entities.Category
import com.carworkz.dearo.domain.entities.ServicePackage

/**
 * Created by Kush Singh Chibber on 11/25/2017.
 */
interface ServicePackageContract {

    interface View : BaseView<Presenter> {

        fun displayPackages(packages: List<ServicePackage>?)

        fun displayFilteredPackages(filterType: String)

        fun displayFilter(filter: Category?)

        fun displayPackageError(error: String)

        fun moveToNextScreen(appointment: Appointment)
    }

    interface Presenter : BasePresenter {

        fun getPackages(appointmentId: String)

        fun filterPackages(filterType: String)

        fun savePackages(appointmentId: String, appointmentPost: AppointmentPost)
    }
}