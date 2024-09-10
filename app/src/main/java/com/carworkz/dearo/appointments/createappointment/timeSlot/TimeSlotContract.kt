package com.carworkz.dearo.appointments.createappointment.timeSlot

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

/**
 * Created by kush on 8/12/17.
 */
interface TimeSlotContract {
    interface View : BaseView<Presenter> {
        fun displayTimeSlots(timeSlot: TimeSlot?)

        fun moveToNextScreen(appointment: Appointment)

        fun showInventoryAlert(inventory: ArrayList<StockInventory>)

        fun displayWorkshopAdviser(adviser: List<WorkshopAdviser>)
    }

    interface Presenter : BasePresenter {

        fun getTimeSlots(appointmentId: String, date: String)

        fun saveTimeSlot(appointmentId: String, appointmentPost: AppointmentPost)

        fun getServiceAdviser()
    }
}