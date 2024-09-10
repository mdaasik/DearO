package com.carworkz.dearo.serviceremainder

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.utils.Constants
import java.lang.StringBuilder
import javax.inject.Inject

class ServiceReminderPresenter @Inject constructor(var view: ServiceReminderContract.View?, val dataRepo: DearODataRepository) : ServiceReminderContract.Presenter {

    override fun saveInformation(jobCardId: String, invoiceId: String?, remarks: InvoiceRemarks, reminderDate: String?, notify: Boolean, actionType: String) {
        view?.showProgressIndicator()
        dataRepo.saveRemarks(jobCardId, remarks, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                when (actionType) {
                    NewServiceReminderActivity.ACTION_INVOICE -> {
                        getInvoicePDF(invoiceId!!, notify, reminderDate)
                    }

                    NewServiceReminderActivity.ACTION_CLOSE -> {
                        closeJobCard(jobCardId, reminderDate, notify)
                    }
                }
//                view?.dismissProgressIndicator()
//                view?.moveToNextScreen(null)
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getRemarks(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepo.getRemarks(jobCardId, object : DataSource.OnResponseCallback<InvoiceRemarks> {
            override fun onSuccess(obj: InvoiceRemarks) {
                view?.displayRemarksAndJobs(obj.remarks, obj.jobs)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun saveInvoiceRemarks(jobCardId: String, remarks: InvoiceRemarks) {
        view?.showProgressIndicator()
        dataRepo.saveRemarks(jobCardId, remarks, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {

                view?.dismissProgressIndicator()
                view?.moveToNextScreen(null)
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getInvoicePDF(invoiceId: String, notify: Boolean?, reminderDate: String?) {
        view?.showProgressIndicator()
        val shouldNotify = if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
            false
        } else {
            notify
        }
        dataRepo.createInvoice(invoiceId, shouldNotify
                ?: false, reminderDate, object : DataSource.OnResponseCallback<List<PDF>> {
            override fun onSuccess(obj: List<PDF>) {
                if (notify == true && SharedPrefHelper.isNotifyOnCreateInvoice() && SharedPrefHelper.isNotifyWhatsappEnabled()) {
                    dataRepo.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_CREATE_INVOICE, invoiceId, object : DataSource.OnResponseCallback<WhatsAppTemplate> {
                        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                        override fun onSuccess(whatsAppTemplate: WhatsAppTemplate) {
                            view?.dismissProgressIndicator()
                            view?.moveToNextScreen(obj)
                            view?.launchWhatsapp(whatsAppTemplate.mobile, whatsAppTemplate.text)
                        }

                        override fun onError(error: ErrorWrapper) {
                            view?.dismissProgressIndicator()
                            view?.moveToNextScreen(obj)
                        }
                    })
                } else {
                    view?.dismissProgressIndicator()
                    view?.moveToNextScreen(obj)
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                
                //add message in bold
                if(error.errorMessages!=null) {
                    val builder = StringBuilder()
                    builder.append("\b ${error.errorMessage} \n\n\n")
                    error.errorMessages.entries.forEach() { entry ->
                        entry.value.forEach() {
                            //add this message
                            builder.append("\u25CF $it \n\n")
                        }
                    }
                    
                    view?.showGenericError(builder.toString())
                }
                else
                {
                    view?.showGenericError(error.errorMessage)
                }
            }
        })
    }

    override fun closeJobCard(jobCardId: String, reminderDate: String?, notify: Boolean) {
        view?.showProgressIndicator()
        dataRepo.closeJobCard(jobCardId, reminderDate, notify, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen(null)
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