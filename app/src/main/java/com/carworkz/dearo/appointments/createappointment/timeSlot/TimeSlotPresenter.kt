package com.carworkz.dearo.appointments.createappointment.timeSlot

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import javax.inject.Inject

/**
 * Created by kush on 8/12/17.
 */
class TimeSlotPresenter @Inject constructor(private var view: TimeSlotContract.View?, private val dearODataRepository: DearODataRepository) : TimeSlotContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun getTimeSlots(appointmentId: String, date: String) {
        view?.showProgressIndicator()
        dearODataRepository.getTimeSlot(appointmentId, date, null, object : DataSource.OnResponseCallback<List<TimeSlot>> {
            override fun onSuccess(obj: List<TimeSlot>) {
                if (obj.isNotEmpty()) {
                    view?.dismissProgressIndicator()
                    view?.displayTimeSlots(obj[0])
                } else {
                    view?.dismissProgressIndicator()
                    view?.showGenericError("Something Went Wrong!")
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun saveTimeSlot(appointmentId: String, appointmentPost: AppointmentPost) {
        view?.showProgressIndicator()
        dearODataRepository.saveTimeSlot(appointmentId, appointmentPost, object : DataSource.OnResponseCallback<Appointment> {
            override fun onSuccess(obj: Appointment) {
                    view?.dismissProgressIndicator()
                if (obj.inventory?.isNotEmpty() == true && SharedPrefHelper.isInventoryEnabled()) {
                    view?.showInventoryAlert(obj.inventory as ArrayList<StockInventory>)
                } else {
                    view?.moveToNextScreen(obj)
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getServiceAdviser() {
        view?.showProgressIndicator()
        dearODataRepository.getServiceAdvisors(SharedPrefHelper.getWorkshopId(), object : DataSource.OnResponseCallback<List<WorkshopAdviser>> {
            override fun onSuccess(obj: List<WorkshopAdviser>) {
                view?.dismissProgressIndicator()
                view?.displayWorkshopAdviser(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }
}