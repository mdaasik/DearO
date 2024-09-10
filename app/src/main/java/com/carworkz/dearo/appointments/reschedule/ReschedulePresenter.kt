package com.carworkz.dearo.appointments.reschedule

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.Reschedule
import com.carworkz.dearo.domain.entities.SoldAMCDetails
import com.carworkz.dearo.domain.entities.TimeSlot
import javax.inject.Inject

class ReschedulePresenter @Inject constructor(private var view: RescheduleContract.View?, private val dataRepo: DearODataRepository) : RescheduleContract.Presenter {
    override fun rescheduleAppointment(appointmentId: String, reschedule: Reschedule) {
        view?.showProgressIndicator()
        dataRepo.rescheduleAppointment(appointmentId, reschedule, object : DataSource.OnResponseCallback<Reschedule> {
            override fun onSuccess(obj: Reschedule) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getTimeSlot(appointmentId: String, date: String) {
        view?.showProgressIndicator()
        dataRepo.getTimeSlot(appointmentId, date, "Reschedule", object : DataSource.OnResponseCallback<List<TimeSlot>> {
            override fun onSuccess(obj: List<TimeSlot>) {
                view?.dismissProgressIndicator()
                if (obj[0].slots.isNotEmpty()) {
                    view?.displayTimeSlot(obj[0].slots)
                } else {
                    view?.showClosedWorkshopView()
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun cancelInvoice(invoiceId: String, reason: Reschedule) {
        view?.showProgressIndicator()
        dataRepo.cancelInvoice(invoiceId, reason, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(obj: Invoice) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun cancelAMC(id: String,reason:String) {
        view?.showProgressIndicator()
        dataRepo.cancelAMC(id,reason, object : DataSource.OnResponseCallback<SoldAMCDetails> {
            override fun onSuccess(obj: SoldAMCDetails) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun rejectAppointment(appointmentId: String, reason: Reschedule) {
        view?.showProgressIndicator()
        dataRepo.rejectAppointment(appointmentId, reason, object : DataSource.OnResponseCallback<Reschedule> {
            override fun onSuccess(obj: Reschedule) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
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