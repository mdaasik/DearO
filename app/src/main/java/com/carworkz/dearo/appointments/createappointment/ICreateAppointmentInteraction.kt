package com.carworkz.dearo.appointments.createappointment

/**
 * Created by kush on 24/11/17.
 */
interface ICreateAppointmentInteraction {

    fun onSuccess()

    fun onFailure()

    fun getSelectedServicePackages(): List<String>?


}