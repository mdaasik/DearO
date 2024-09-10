package com.carworkz.dearo.appointments.reschedule

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Reschedule
import com.carworkz.dearo.domain.entities.Slot

interface RescheduleContract {

    interface View : BaseView<Presenter> {
        fun moveToNextScreen()

        fun dateError(errorMsg: String)

        fun slotError(errorMsg: String)

        fun displayTimeSlot(timeSlot: List<Slot>)

        fun launchWhatsapp(contactNumber: String, message: String)

        fun showClosedWorkshopView()
    }

    interface Presenter : BasePresenter {

        fun rejectAppointment(appointmentId: String, reason: Reschedule)

        fun rescheduleAppointment(appointmentId: String, reschedule: Reschedule)

        fun getTimeSlot(appointmentId: String, date: String)

        fun cancelInvoice(invoiceId: String, reason: Reschedule)

        fun cancelAMC(id: String,reason:String)
    }
}