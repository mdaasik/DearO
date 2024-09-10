package com.carworkz.dearo.search

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.helpers.PaginationList
import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.utils.Constants
import javax.inject.Inject

/**
 * Created by kush on 24/8/17.
 */
@ActivityScoped
class SearchPresenter @Inject constructor(private var view: SearchContract.View?, val dataRepository: DearODataRepository) : SearchContract.Presenter {

    override fun searchJobs(query: String, vehicleType: String?) {
        view?.showProgressIndicator()
        dataRepository.getJobs(query, vehicleType, object : DataSource.OnResponseCallback<List<RecommendedJob>> {
            override fun onSuccess(obj: List<RecommendedJob>) {
                view?.dismissProgressIndicator()
                view?.displayJobs(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun searchParts(query: String, vehicleType: String?,vehicleAmcId: String?) {
        view?.showProgressIndicator()
        dataRepository.getParts(query, vehicleType,vehicleAmcId, object : DataSource.OnResponseCallback<List<Part>> {
            override fun onSuccess(obj: List<Part>) {
                view?.dismissProgressIndicator()
                view?.displayParts(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun searchPartNumber(query: String, vehicleType: String?) {
        view?.showProgressIndicator()
        dataRepository.searchPartNumber(query, null, null, null, null, null, null, false, vehicleType, VEHICLE_STRICT, null,object : DataSource.OnResponseCallback<List<PartNumber>> {
            override fun onSuccess(obj: List<PartNumber>) {
                view?.dismissProgressIndicator()
                view?.displayPartNumber(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun acceptAppointment(appointmentId: String) {
        view?.showProgressIndicator()
        dataRepository.acceptAppointment(appointmentId, object : DataSource.OnResponseCallback<Appointment> {
            override fun onSuccess(obj: Appointment) {
                view?.dismissProgressIndicator()
                view?.refresh()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun searchLabours(query: String, jobCardID: String, vehicleType: String?,vehicleAmcId: String?) {
        view?.showProgressIndicator()
        dataRepository.getLabours(jobCardID, query, vehicleType,vehicleAmcId, object : DataSource.OnResponseCallback<List<Labour>> {
            override fun onSuccess(obj: List<Labour>) {
                view?.dismissProgressIndicator()
                view?.displayLabours(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun searchJobCards(query: String, search: String) {
        view?.showProgressIndicator()
        dataRepository.searchJobCards(search, query, object : DataSource.OnResponseCallback<List<JobCard>> {
            override fun onSuccess(obj: List<JobCard>) {
                view?.dismissProgressIndicator()
                view?.displayJobCards(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun searchInvoiceCards(query: String, search: String, type: String) {
        view?.showProgressIndicator()
        dataRepository.searchInvoices(search, query, type, object : DataSource.OnResponseCallback<List<Invoice>> {
            override fun onSuccess(obj: List<Invoice>) {
                view?.dismissProgressIndicator()
                if (type == SearchActivity.ARG_INVOICE) {
                    view?.displayInvoiceCards(obj)
                } else {
                    view?.displayOtcCards(obj)
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
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
        dataRepository.completeJobCard(jobCardId, shouldNotify, null,object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                if (notify && SharedPrefHelper.isNotifyOnCompleteJC() && SharedPrefHelper.isNotifyWhatsappEnabled()) {
                    view?.showProgressIndicator()
                    dataRepository.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_COMPLETED, jobCardId, object : DataSource.OnResponseCallback<WhatsAppTemplate> {
                        override fun onSuccess(obj: WhatsAppTemplate) {
                            view?.dismissProgressIndicator()
                            view?.launchWhatsapp(obj.mobile, obj.text)
                            view?.refresh()
                        }

                        override fun onError(error: ErrorWrapper) {
                            view?.dismissProgressIndicator()
                            view?.refresh()
                        }
                    })
                } else {
                    view?.refresh()
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun closeJobCard(jobCardId: String) {
        /*view?.showProgressIndicator()
        dataRepository.closeJobCard(jobCardId,, object : DataSource.OnResponseCallback<NetworkPostResponse>{
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.refresh()
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })*/
    }

    override fun cancelJobCard(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.cancelJobCard(jobCardId, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                view?.refresh()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

 /*   override fun updatePayment(jobCardId: String, paymentType: String, method: String, amount: String, remarks: String) {
        view?.showProgressIndicator()
        dataRepository.updatePayment(jobCardId, paymentType, method, amount, remarks, false, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                view?.refresh()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }*/

    override fun searchMyCustomers(query: String) {
        view?.showProgressIndicator()
        dataRepository.getCustomerVehicleList(query, null, null, null, 0, 0, object : DataSource.OnResponseCallback<PaginationList<CustomerVehicleDetails>> {
            override fun onSuccess(obj: PaginationList<CustomerVehicleDetails>) {
                view?.displayMyCustomers(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun searchAMC(query: String) {
        view?.showProgressIndicator()
        dataRepository.getAMCList("", 0, 0, query,  object : DataSource.OnResponseCallback<PaginationList<AMC>> {
            override fun onSuccess(obj: PaginationList<AMC>) {
                view?.displayAMC(obj)
                view?.dismissProgressIndicator()
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
