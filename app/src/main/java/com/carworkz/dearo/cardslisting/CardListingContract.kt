package com.carworkz.dearo.cardslisting

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

/**
 * Created by farhan on 03/01/18.
 */
interface CardListingContract {

    interface View : BaseView<Presenter> {

        fun displayInvoiceCards(cards: List<Invoice>, totalItemCount: Int)

        fun displayJobCards(cards: List<JobCard>, totalItemCount: Int)

        fun displayAmcCards(cards: List<AMC>, totalItemCount: Int)

        fun displayAppointmentCards(cards: List<Appointment>, totalItemCount: Int)

        fun displayOtcCards(obj: List<Invoice>, totalItemsCount: Int)

        fun onCreateJobCardSuccess(upsertDetails: UpsertDetails)

        fun onCreateJobCardError(isVariantRequired: Boolean, isNameRequired: Boolean, isPincodeRequired: Boolean)

        fun completeJobCardDone(jobCardId: String)

        fun cancelJobCardDone(jobCardId: String)

        fun onJobCardStatusChangeSuccess()

        fun noCardsFound()

        fun showError(message: String)

        fun onAppointmentAcceptSuccess(appointmentId: String)

        fun onAppointmentDeclineSuccess(appointmentId: String)

        fun updateCardListing(jobCardId: String)

        fun displayReassignServiceAdvisor(appointmentId: String, selectedServiceAdvisor: String?, serviceAdvisors: List<WorkshopAdviser>)

        fun showAccidentalErrors(errorList: ArrayList<String>, companyList: List<InsuranceCompany>?, jobCard: JobCard)

        fun showAccidentalError(error: String?, jobCard: JobCard)

        fun onMissingUpdateSaveSuccess(jobCard: JobCard)

        fun launchWhatsapp(contactNumber: String, message: String)

        fun setUpdatedJC(jobCard: JobCard)
    }

    interface Presenter : BasePresenter {

        fun getAmcCrds(skip: Int, limit: Int, type: String,query: String)

        fun getJobCards(skip: Int, limit: Int, vararg type: String)

        fun completeJobCard(jobCardId: String, notify: Boolean)

        fun getEstimateAndCompleteJC(jobCardId: String)

        fun cancelJobCard(jobCardId: String)

        fun closeJobCard(jobCardId: String)

        // fun updatePayment(jobCardId: String, paymentType: String, method: String, amount: String, remarks: String, notifyCustomer: Boolean)

        fun getInvoiceCards(invoiceType: String, skip: Int, limit: Int, type: String?)

        fun closeJobCard(invoiceId: String, paymentType: String, method: String, amount: String)

        fun getAppointmentCards(type: String, skip: Int, limit: Int)

        fun cancelAppointment(appointmentId: String)

        fun acceptAppointment(appointmentId: String)

        fun createJobCard(appointmentId: String)

        fun initReassignServiceAdvisor(appointmentId: String, selectedServiceAdvisor: String?)

        fun reassignAdvisor(appointmentId: String, serviceAdvisorId: String, callback: CardListingPresenter.OnReassignCallback)

        fun getInsuranceCompanies(errorList: ArrayList<String>, jobCard: JobCard)

        fun updateMissingInfo(jobCardId: String, missingAccidentalDetails: MissingAccidentalDetails)
    }
}