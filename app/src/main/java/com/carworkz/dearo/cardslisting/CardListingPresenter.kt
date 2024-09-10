package com.carworkz.dearo.cardslisting

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DataSource.OnResponseCallback
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.helpers.PaginationList
import com.carworkz.dearo.utils.Constants.ApiConstants
import javax.inject.Inject

/**
 * Created by farhan on 03/01/18.
 */
class CardListingPresenter @Inject constructor(
    private var view: CardListingContract.View?,
    private val dataRepo: DearODataRepository
) : CardListingContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun createJobCard(appointmentId: String) {
        view?.showProgressIndicator()
        dataRepo.upsertData(
            appointmentId,
            UpsertDetails(),
            object : DataSource.OnResponseCallback<UpsertDetails> {
                override fun onSuccess(obj: UpsertDetails) {
                    view?.dismissProgressIndicator()
                    view?.onCreateJobCardSuccess(obj)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    error.errorMessages?.let {
                        view?.onCreateJobCardError(
                            error.errorMessages.containsKey(ApiConstants.KEY_VARIANTCODE),
                            error.errorMessages.containsKey(ApiConstants.KEY_NAME),
                            error.errorMessages.containsKey(ApiConstants.KEY_PINCODE)
                        )
                    } ?: run {
                        view?.showGenericError(errorMsg = error.errorMessage)
                    }
                }
            })
    }

    override fun getAmcCrds(skip: Int, limit: Int, type: String,query: String) {
        view?.showProgressIndicator()
        dataRepo.getAMCList(
            type,
            limit,
            skip,query,
            object : DataSource.OnResponseCallback<PaginationList<AMC>> {
                override fun onSuccess(obj: PaginationList<AMC>) {
                    view?.dismissProgressIndicator()
                    /* if (obj.metaData!!.totalItemsCount == 0)
                         view?.noCardsFound()
                     else {
                         view?.displayAmcCards(obj, obj.metaData!!.totalItemsCount)
                     }*/
                    if (obj.size == 0)
                        view?.noCardsFound()
                    else {
                        view?.displayAmcCards(obj, obj.metaData!!.totalItemsCount)
                    }
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showError(error.errorMessage)
                }
            })
    }

    override fun getJobCards(skip: Int, limit: Int, vararg type: String) {
        view?.showProgressIndicator()
        dataRepo.getJobCards(
            type.toList(),
            limit,
            skip,
            object : DataSource.OnResponseCallback<PaginationList<JobCard>> {
                override fun onSuccess(obj: PaginationList<JobCard>) {
                    view?.dismissProgressIndicator()
                    if (obj.metaData!!.totalItemsCount == 0)
                        view?.noCardsFound()
                    else {
                        view?.displayJobCards(obj, obj.metaData!!.totalItemsCount)
                    }
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showError(error.errorMessage)
                }
            })
    }

    override fun completeJobCard(jobCardId: String, notify: Boolean) {
        view?.showProgressIndicator()
        val shouldNotify = if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
            false
        } else {
            notify
        }
        dataRepo.completeJobCard(
            jobCardId,
            shouldNotify,
            null,
            object : DataSource.OnResponseCallback<NetworkPostResponse> {
                override fun onSuccess(obj: NetworkPostResponse) {
                    view?.dismissProgressIndicator()

                    if (notify && SharedPrefHelper.isNotifyWhatsappEnabled()) {
                        view?.showProgressIndicator()
                        dataRepo.getWhatsAppTemplate(
                            ApiConstants.KEY_WHATSAPP_JOB_CARD_COMPLETED,
                            jobCardId,
                            object : DataSource.OnResponseCallback<WhatsAppTemplate> {
                                override fun onSuccess(obj: WhatsAppTemplate) {
                                    view?.dismissProgressIndicator()
                                    view?.launchWhatsapp(obj.mobile, obj.text)
                                    view?.completeJobCardDone(jobCardId)
                                }

                                override fun onError(error: ErrorWrapper) {
                                    view?.dismissProgressIndicator()
                                    view?.completeJobCardDone(jobCardId)
                                }
                            })
                    } else {
                        view?.completeJobCardDone(jobCardId)
                    }
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(error.errorMessage)
                }
            })
    }

    override fun getEstimateAndCompleteJC(jobCardId: String) {
        if (view != null) {
            view!!.showProgressIndicator()
            dataRepo.getJobCardById(jobCardId, null, object : OnResponseCallback<JobCard> {
                override fun onSuccess(obj: JobCard) {
                    if (view != null) {
                        view?.dismissProgressIndicator()
                        view?.setUpdatedJC(obj)
                    }
                }

                override fun onError(error: ErrorWrapper) {
                    if (view != null) {
                        view?.dismissProgressIndicator()
                        view?.showGenericError(error.errorMessage)
                    }
                }
            })
        }
    }

    override fun cancelJobCard(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepo.cancelJobCard(
            jobCardId,
            object : DataSource.OnResponseCallback<NetworkPostResponse> {
                override fun onSuccess(obj: NetworkPostResponse) {
                    view?.dismissProgressIndicator()
                    view?.cancelJobCardDone(jobCardId)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(errorMsg = error.errorMessage)
                }
            })
    }

    override fun closeJobCard(jobCardId: String) {
        /* view?.showProgressIndicator()
         dataRepo.closeJobCard(jobCardId,remi, object : DataSource.OnResponseCallback<NetworkPostResponse> {
             override fun onSuccess(obj: NetworkPostResponse) {
                 view?.dismissProgressIndicator()
             }

             override fun onError(error: ErrorWrapper) {
                 view?.dismissProgressIndicator()
                 view?.showError(error.errorMessage)
             }
         })*/
    }

//    override fun updatePayment(jobCardId: String, paymentType: String, method: String, amount: String, remarks: String, notifyCustomer: Boolean) {
//        view?.showProgressIndicator()
//        dataRepo.updatePayment(jobCardId, paymentType, method, amount, remarks, notifyCustomer, object : DataSource.OnResponseCallback<NetworkPostResponse> {
//            override fun onSuccess(obj: NetworkPostResponse) {
//                view?.dismissProgressIndicator()
//                view?.updateCardListing(jobCardId)
//            }
//
//            override fun onError(error: ErrorWrapper) {
//                view?.dismissProgressIndicator()
//                view?.showGenericError(error.errorMessage)
//            }
//        })
//    }

    override fun getInvoiceCards(invoiceType: String, skip: Int, limit: Int, type: String?) {
        view?.showProgressIndicator()
        dataRepo.getInvoices(
            invoiceType,
            skip,
            limit,
            type,
            object : DataSource.OnResponseCallback<PaginationList<Invoice>> {
                override fun onSuccess(obj: PaginationList<Invoice>) {
                    view?.dismissProgressIndicator()
                    if (obj.metaData!!.totalItemsCount == 0) view?.noCardsFound() else {
                        if (type == CardListingFragment.ARG_TYPE_OTC)
                            view?.displayOtcCards(obj, obj.metaData!!.totalItemsCount)
                        else
                            view?.displayInvoiceCards(obj, obj.metaData!!.totalItemsCount)
                    }
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(error.errorMessage)
                }
            })
    }

    override fun closeJobCard(
        invoiceId: String,
        paymentType: String,
        method: String,
        amount: String
    ) {
    }

    override fun getAppointmentCards(type: String, skip: Int, limit: Int) {
        view?.showProgressIndicator()
        dataRepo.getAppointments(
            type,
            skip,
            limit,
            object : DataSource.OnResponseCallback<PaginationList<Appointment>> {
                override fun onSuccess(obj: PaginationList<Appointment>) {
                    view?.dismissProgressIndicator()
                    if (obj.metaData!!.totalItemsCount == 0) view?.noCardsFound() else
                        view?.displayAppointmentCards(obj, obj.metaData!!.totalItemsCount)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(error.errorMessage)
                }
            })
    }

    override fun acceptAppointment(appointmentId: String) {
        view?.dismissProgressIndicator()
        dataRepo.acceptAppointment(
            appointmentId,
            object : DataSource.OnResponseCallback<Appointment> {
                override fun onSuccess(obj: Appointment) {
                    view?.dismissProgressIndicator()
                    view?.onAppointmentAcceptSuccess(appointmentId)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(error.errorMessage)
                }
            })
    }

    override fun cancelAppointment(appointmentId: String) {
        view?.showProgressIndicator()
        dataRepo.cancelAppointment(
            appointmentId,
            object : DataSource.OnResponseCallback<Appointment> {
                override fun onSuccess(obj: Appointment) {
                    view?.dismissProgressIndicator()
                    view?.onAppointmentAcceptSuccess(appointmentId)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(error.errorMessage)
                }
            })
    }

    override fun initReassignServiceAdvisor(
        appointmentId: String,
        selectedServiceAdvisor: String?
    ) {
        view?.showProgressIndicator()
        dataRepo.getServiceAdvisors(
            SharedPrefHelper.getWorkshopId(),
            object : DataSource.OnResponseCallback<List<WorkshopAdviser>> {
                override fun onSuccess(obj: List<WorkshopAdviser>) {
                    view?.dismissProgressIndicator()
                    view?.displayReassignServiceAdvisor(appointmentId, selectedServiceAdvisor, obj)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    view?.showGenericError(error.errorMessage)
                }
            })
    }

    override fun reassignAdvisor(
        appointmentId: String,
        serviceAdvisorId: String,
        callback: OnReassignCallback
    ) {
        view?.showProgressIndicator()
        dataRepo.reassignAppointment(
            appointmentId,
            serviceAdvisorId,
            object : DataSource.OnResponseCallback<NetworkPostResponse> {
                override fun onSuccess(obj: NetworkPostResponse) {
                    view?.run {
                        dismissProgressIndicator()
                        callback.onSuccess()
                    }
                }

                override fun onError(error: ErrorWrapper) {
                    view?.run {
                        dismissProgressIndicator()
                        callback.onFailure(error.errorMessage)
                    }
                }
            })
    }

    override fun getInsuranceCompanies(errorList: ArrayList<String>, jobCard: JobCard) {
        view?.showProgressIndicator()
        dataRepo.getCompanyNames(object : DataSource.OnResponseCallback<List<InsuranceCompany>> {
            override fun onSuccess(obj: List<InsuranceCompany>) {
                view?.dismissProgressIndicator()
                if (errorList.isNotEmpty()) {
                    view?.showAccidentalErrors(errorList, obj, jobCard)
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

    override fun updateMissingInfo(
        jobCardId: String,
        missingAccidentalDetails: MissingAccidentalDetails
    ) {
        view?.showProgressIndicator()
        dataRepo.saveMissingAccidentalDetails(
            jobCardId,
            missingAccidentalDetails,
            object : DataSource.OnResponseCallback<JobCard> {
                override fun onSuccess(obj: JobCard) {
                    view?.dismissProgressIndicator()
                    view?.onMissingUpdateSaveSuccess(obj)
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                    if (error.errorMessages != null && error.errorMessages.isNotEmpty()) {
                        var errorString = ""
                        for ((key, value1) in error.errorMessages) {
                            when (key) {
                                ApiConstants.KEY_INSURANCE_ERROR -> {
                                    run {
                                        for (value in value1) {
                                            if (value.contains(ApiConstants.KEY_CLAIM_ERROR)) {
                                                errorString = "$errorString\u2022 $value \n"
                                            }
                                            if (value.contains(ApiConstants.KEY_INSURANCE_POLICY)) {
                                                errorString = "$errorString\u2022 $value \n"
                                            }
                                            if (value.contains(ApiConstants.KEY_INSURANCE_EXPIRY)) {
                                                errorString = "$errorString\u2022 $value \n"
                                            }
                                        }
                                    }
                                    run {
                                        for (value in value1) {
                                            if (value.contains(ApiConstants.KEY_INSURANCE_COMPANY)) {
                                                errorString = "$errorString\u2022 $value \n"
                                            }
                                        }
                                    }
                                }
                                ApiConstants.KEY_COMPANY -> {
                                    for (value in value1) {
                                        if (value.contains(ApiConstants.KEY_INSURANCE_COMPANY)) {
                                            errorString = "$errorString\u2022 $value \n"
                                        }

                                        if (value.contains(ApiConstants.KEY_PINCODE_ERROR, true)) {
                                            errorString = "$errorString\u2022 $value \n"
                                        }
                                    }
                                }
                            }
                        }
                        view?.showGenericError(errorString)
                    } else {
                        view?.showGenericError("Something Went Wrong")
                    }
                }
            })
    }

    interface OnReassignCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }
}