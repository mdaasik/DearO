package com.carworkz.dearo.appointments.status

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentStatus
import javax.inject.Inject

class AppointmentStatusPresenter @Inject constructor(var view: AppointmentStatusContract.View?, val dearODataRepository: DearODataRepository) : AppointmentStatusContract.Presenter {
    override fun saveDetails(details: AppointmentStatus) {
        view?.showProgressIndicator()
        dearODataRepository.saveAppointmentStatus(details,object : DataSource.OnResponseCallback<Any>{
            override fun onSuccess(obj: Any) {
                view?.dismissProgressIndicator()
                view?.onSaveSuccess()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getAppointmentById(appointmentId: String) {
        view?.showProgressIndicator()
        dearODataRepository.getAppointmentsByID(appointmentId,object :DataSource.OnResponseCallback<Appointment>{
            override fun onSuccess(obj: Appointment) {
                view?.dismissProgressIndicator()
                view?.onFetchAppointment(obj)
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