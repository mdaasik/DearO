package com.carworkz.dearo.addjobcard.completejobcard

import android.text.TextUtils
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DataSource.OnResponseCallback
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.domain.entities.WhatsAppTemplate
import com.carworkz.dearo.utils.Constants
import javax.inject.Inject

class CompleteJobCardPresenter @Inject constructor(private var view: CompleteJobCardContract.View?, private val dearODataRepository: DearODataRepository) : CompleteJobCardContract.Presenter
{
    override fun getRemarks(jobCardId: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.getRemarks(jobCardId, object : DataSource.OnResponseCallback<InvoiceRemarks> {
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

    override fun saveInvoiceRemarks(jobCardId: String, remarks: InvoiceRemarks)
    {
        view?.showProgressIndicator()
        dearODataRepository.saveRemarks(jobCardId, remarks, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.onRedYellowInvoiceRemarkSuccess()
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getEstimate(jobCardId: String)
    {
        if (view != null)
        {
            view!!.showProgressIndicator()
            dearODataRepository.getJobCardById(jobCardId, null, object : OnResponseCallback<JobCard>
            {
                override fun onSuccess(obj: JobCard)
                {
                    if (view != null)
                    {
                        view!!.dismissProgressIndicator()
                        view!!.displayEstimate(obj)
                    }
                }
                override fun onError(error: ErrorWrapper)
                {
                    if (view != null)
                    {
                        view!!.dismissProgressIndicator()
                        view!!.showGenericError(error.errorMessage)
                    }
                }

            })
        }
    }

    override fun completeJobCard(jobCardId: String, dateTime: String, minEstimate: Int, maxEstimate: Int, notify: Boolean, reasonForDelay: String?)
    {
        if (view == null) return

        view!!.showProgressIndicator()
        val shouldNotify: Boolean = if (SharedPrefHelper.isNotifyWhatsappEnabled())
        {
            false
        }
        else
        {
            notify
        }
        dearODataRepository.completeJobCard(jobCardId, shouldNotify,reasonForDelay, object : OnResponseCallback<NetworkPostResponse>
        {
            override fun onSuccess(obj: NetworkPostResponse)
            {
                if (view == null) return
                view!!.dismissProgressIndicator()
                if (notify && SharedPrefHelper.isNotifyWhatsappEnabled())
                {
                    view!!.showProgressIndicator()
                    dearODataRepository.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_COMPLETED, jobCardId, object : OnResponseCallback<WhatsAppTemplate>
                    {
                        override fun onSuccess(obj: WhatsAppTemplate)
                        {
                            view!!.dismissProgressIndicator()
                            view!!.moveToNextScreen()
                            view!!.launchWhatsapp(obj.mobile, obj.text)
                        }

                        override fun onError(error: ErrorWrapper)
                        {
                            view!!.dismissProgressIndicator()
                            view!!.moveToNextScreen()
                        }
                    })
                }
                else
                {
                    view!!.moveToNextScreen()
                }
            }

            override fun onError(error: ErrorWrapper)
            {
                if (view == null) return
                view!!.dismissProgressIndicator()
                view!!.showGenericError(error.errorMessage)
            }
        })
    }

    override fun saveEstimate(jobCardId: String, dateTime: String, minEstimate: Int, maxEstimate: Int, status: String?, notify: Boolean)
    {
        if (view == null) return
        val shouldNotify: Boolean
        shouldNotify = if (SharedPrefHelper.isNotifyWhatsappEnabled())
        {
            false
        }
        else
        {
            notify
        }

        view!!.showProgressIndicator()
        dearODataRepository.saveJobCardEstimate(jobCardId, dateTime, minEstimate, maxEstimate, status, shouldNotify, null,object : OnResponseCallback<NetworkPostResponse>
        {
            override fun onSuccess(obj: NetworkPostResponse)
            {
                if (view == null) return

                //status not null means jobcard is being created.
                if (status != null)
                {
                    if (notify && SharedPrefHelper.isNotifyWhatsappEnabled())
                    {
                        dearODataRepository.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_CREATE_JC, jobCardId, object : OnResponseCallback<WhatsAppTemplate>
                        {
                            override fun onSuccess(obj: WhatsAppTemplate)
                            {
                                if (view == null) return
                                view!!.dismissProgressIndicator()
                                view!!.launchWhatsapp(obj.mobile, obj.text)
                                view!!.moveToNextScreen()
                            }

                            override fun onError(error: ErrorWrapper)
                            {
                                if (view == null) return
                                view!!.dismissProgressIndicator()
                                view!!.moveToNextScreen()
                            }
                        })
                    }
                    else
                    {
                        view!!.moveToNextScreen()
                    }
                }
                view!!.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper)
            {
                if (view == null) return
                //                Timber.d("error estimate"+error.getErrorMessages().isEmpty());
                view!!.dismissProgressIndicator()
                if (error.errorMessages != null)
                {
                    for (key in error.errorMessages.keys)
                    {
                        val finalErrorMessage =
                            error.errorMessages[key]?.let { TextUtils.join(",", it) }
                        when (key)
                        {
                            Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_MIN_COST           -> view!!.showMinCostError(finalErrorMessage)
                            Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_MAX_COST           -> view!!.showMaxCostError(finalErrorMessage)
                            Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_DELIVERY_DATE_TIME -> view!!.showDeliveryDateError(finalErrorMessage)
                        }
                    }
                }
                else
                {
                    view!!.showGenericError(error.errorMessage)
                }
            }
        })
    }

    override fun start()
    {

    }

    override fun detachView()
    {
        view = null
    }
}