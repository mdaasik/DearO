package com.carworkz.dearo.appointments.createappointment.appointmentDetails

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

/**
 * Created by kush on 24/11/17.
 */
interface AppointmentDetailsContract {

    interface View : BaseView<Presenter> {
        fun moveToNextScreen(appointment: Appointment)

        fun newNextApiUpdate(enabled: Boolean,error: String)

        fun showEmailError(message: String)

        fun showMobileError(message: String)

        fun showError(message: String)

        fun displayMake(make: List<Make>)

        fun displayModel(model: List<Model>)

        fun displayVariant(variant: List<Variant>)

        fun displayVehicleData(vehicle: Vehicle)

        fun resetVehicleData()

        fun displayCustomerData(customer: Customer)

        fun resetCustomer()

        fun displayPincodeData(pincode: Pincode)
    }

    interface Presenter : BasePresenter {

        fun saveDetails(appointment: Appointment)

        fun newNextApiCall(phone : String,registration: String)

        fun getMake(vehicleType: String?)

        fun getModel(slug: String)

        fun getVariant(slug: String)

        fun getVehicleDetails(registrationNumber: String)

        fun getCustomerDetails(mobileNumber: String)

        fun getCityByPincode(pincode: Int)
    }
}