package com.carworkz.dearo.addjobcard.createjobcard.customercarsearch

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

/**
 * Created by kush on 11/8/17.
 */

interface CustomerCarSearchContract {

    interface View : BaseView<Presenter> {

        fun updateCustomerViews(obj: Customer) // used when customer found

        fun updateVehicleViews(obj: Vehicle) // used when vehicle found

        fun updateAmcViews(amcList: List<AMC>) // used when vehicle found

        fun updateCustomerNumberViews(obj: CustomerVehicleSearch, showUpdate: Boolean) // used when number mismatch found

        fun onUseAlternateNumberSuccess(message: String)

        fun onUseAlternateNumberError(error: String)

        fun errorToast(error: String)

        fun addCustomer()

        fun addVehicle()

        fun updateVehicle()

        fun moveToNextScreen(obj: JobCard)

        fun viewJC(obj: JobCard)

        fun updateIncompleteInfo()

        fun toggleJobCardType(visibility: Boolean)

        fun toggleJobCardBtn(enabled: Boolean)

        fun resetCustomer()

        fun displayHistory(searchObject: CustomerVehicleSearch)

        fun showHistory(details : CustomerVehicleDetails,isDearoHistory: Boolean)

        fun newSearchApiUpdate(enabled: Boolean,error: String)
    }

    interface Presenter : BasePresenter {

        fun newCarSearchApiCall(phone : String,registration: String)

        fun searchCarAndCustomer(phone: String, registration: String)

        fun addAlternateNumber(phone: String, customerId: String)

        fun updateNumber(phone: String, customerId: String)

        fun initiateJobCard(customerId: String, vehicleId: String, appointmentId: String?, vehicleAmcId: String?,type: String)

        fun getJobCardData(id: String,isDearoHistory: Boolean)

        fun getJobCard(id: String)
    }
}
