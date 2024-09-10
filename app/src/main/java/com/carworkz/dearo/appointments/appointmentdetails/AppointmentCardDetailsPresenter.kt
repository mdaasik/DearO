package com.carworkz.dearo.appointments.appointmentdetails

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Appointment
import javax.inject.Inject

class AppointmentCardDetailsPresenter @Inject constructor(var view: AppointmentCardDetailsContract.View?, val dearODataRepository: DearODataRepository) : AppointmentCardDetailsContract.Presenter {

    override fun getDetails(appointmentId: String) {
        view?.showProgressIndicator()
        dearODataRepository.getAppointmentsByID(appointmentId, object : DataSource.OnResponseCallback<Appointment> {
            override fun onSuccess(obj: Appointment) {
                view?.displayDetails(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}