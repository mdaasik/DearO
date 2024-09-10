package com.carworkz.dearo.addjobcard.quickjobcard

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.domain.entities.WhatsAppTemplate
import com.carworkz.dearo.utils.Constants.ApiConstants
import timber.log.Timber
import javax.inject.Inject

class QuickJobCardPresenter @Inject constructor(var view: QuickJobCardContract.View?, val dataRepository: DearODataRepository) : QuickJobCardContract.Presenter {

    override fun saveJobCard(jobCardId: String, jobCard: JobCard, screenToStart: String?) {
        view?.showProgressIndicator()

        // don't send notify flag if whatsApp enabled, which is used by server to send sms
        val notifyWithWhatsApp = SharedPrefHelper.isNotifyWhatsappEnabled() && jobCard.estimate.notifyCustomer

        if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
            jobCard.estimate.notifyCustomer = false
        }
        dataRepository.saveQuickJobCard(jobCardId, jobCard, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                if (screenToStart != null) {
                    // SCREEN_NONE to not close the screen.
                    view?.startScreen(screenToStart, obj)
                } else {
                    // when job card is created.
                    if (jobCard.status == JobCard.STATUS_IN_PROGRESS) {
                        if (notifyWithWhatsApp) {
                            view?.showProgressIndicator()
                            dataRepository.getWhatsAppTemplate(ApiConstants.KEY_WHATSAPP_JOB_CARD_CREATE_JC, jobCardId, object : DataSource.OnResponseCallback<WhatsAppTemplate> {
                                override fun onSuccess(obj: WhatsAppTemplate) {
                                    view?.dismissProgressIndicator()
                                    view?.launchWhatsapp(obj.mobile, obj.text)
                                    view?.moveToNextScreen(true)
                                }

                                override fun onError(error: ErrorWrapper) {
                                    view?.dismissProgressIndicator()
                                    view?.moveToNextScreen(true)
                                }
                            })
                        } else {
                            view?.moveToNextScreen(true)
                        }
                    } else {
                        // job complete case
                        view?.moveToNextScreen(true)
                    }
                }
                Timber.d("job card saved $obj")
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                error.errorMessages?.let { messages ->
                    view?.showGenericError(messages["kmsReading"]?.get(0) ?: error.errorMessage)
                } ?: run {
                    view?.showGenericError(error.errorMessage)
                }
            }
        })
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getData(jobCardId: String) {
        view?.showProgressIndicator()
        val includeList = ArrayList<String>()
        includeList.add("vehicle")
        includeList.add("customer")
        includeList.add("invoice")
        dataRepository.getJobCardDetails(jobCardId, includeList.toTypedArray(), object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(jobCard: JobCard) {
                dataRepository.getJobsData(jobCardId, object : DataSource.OnResponseCallback<JobAndVerbatim> {
                    override fun onSuccess(obj: JobAndVerbatim) {
                        view?.dismissProgressIndicator()
                        view?.displayData(jobCard, obj)
                    }

                    override fun onError(error: ErrorWrapper) {
                        view?.dismissProgressIndicator()
                        view?.showGenericError(error.errorMessage)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getJobCardById(jobCardId: String) {
        view?.showProgressIndicator()
        val includeList = ArrayList<String>()
        includeList.add("vehicle")
        includeList.add("customer")
        includeList.add("invoice")
        dataRepository.getJobCardDetails(jobCardId, includeList.toTypedArray(), object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.displayJobCard(obj)
                Timber.d("getjobcard id $obj")
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getJobs(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.getJobsData(jobCardId, object : DataSource.OnResponseCallback<JobAndVerbatim> {
            override fun onSuccess(obj: JobAndVerbatim) {
                view?.dismissProgressIndicator()
                view?.displayWithJobAndVerbatim(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun completeJobCard(jobCardId: String, jobCard: JobCard, notify: Boolean) {
        view?.showProgressIndicator()
        val shouldNotify = if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
            false
        } else {
            notify
        }
        dataRepository.saveQuickJobCard(jobCardId, jobCard, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                dataRepository.completeJobCard(jobCardId, shouldNotify, null,object : DataSource.OnResponseCallback<NetworkPostResponse> {
                    override fun onSuccess(obj: NetworkPostResponse) {
                        view?.dismissProgressIndicator()
                        if (notify && SharedPrefHelper.isNotifyWhatsappEnabled()) {
                            view?.showProgressIndicator()
                            dataRepository.getWhatsAppTemplate(ApiConstants.KEY_WHATSAPP_JOB_CARD_COMPLETED, jobCardId, object : DataSource.OnResponseCallback<WhatsAppTemplate> {
                                override fun onSuccess(obj: WhatsAppTemplate) {
                                    view?.dismissProgressIndicator()
                                    view?.launchWhatsapp(obj.mobile, obj.text)
                                    view?.moveToNextScreen(true)
                                }

                                override fun onError(error: ErrorWrapper) {
                                    view?.dismissProgressIndicator()
                                    view?.moveToNextScreen(true)
                                }
                            })
                        } else {
                            view?.moveToNextScreen(true)
                        }
                    }

                    override fun onError(error: ErrorWrapper) {
                        view?.dismissProgressIndicator()
                        view?.showGenericError(errorMsg = error.errorMessage)
                    }
                })
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