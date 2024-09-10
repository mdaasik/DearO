package com.carworkz.dearo.search

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

/**
 * Created by kush on 24/8/17.
 */

interface SearchContract {
    interface View : BaseView<Presenter> {
        fun displayJobs(jobsList: List<RecommendedJob>)

        fun displayParts(list: List<Part>)

        fun displayLabours(list: List<Labour>)

        fun displayPartNumber(list: List<PartNumber>)

        fun displayJobCards(list: List<JobCard>)

        fun displayInvoiceCards(list: List<Invoice>)

        fun displayMyCustomers(list: List<CustomerVehicleDetails>)

        fun displayOtcCards(obj: List<Invoice>)

        fun displayAMC(obj: List<AMC>)

        fun refresh()

        fun launchWhatsapp(contactNumber: String, message: String)
    }

    interface Presenter : BasePresenter {
        fun completeJobCard(jobCardId: String, notify: Boolean)

        fun closeJobCard(jobCardId: String)

        fun cancelJobCard(jobCardId: String)

//        fun updatePayment(jobCardId: String, paymentType: String, method: String, amount: String, remarks: String)

        fun acceptAppointment(appointmentId: String)

        fun searchJobs(query: String, vehicleType: String?)

        fun searchParts(query: String, vehicleType: String?,vehicleAmcId: String?)

        fun searchPartNumber(query: String, vehicleType: String?)

        fun searchLabours(query: String, jobCardID: String, vehicleType: String?,vehicleAmcId: String?)

        fun searchJobCards(query: String, search: String)

        fun searchInvoiceCards(query: String, search: String, type: String)

        fun searchMyCustomers(query: String)

        fun searchAMC(query: String)
    }
}
