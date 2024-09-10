package com.carworkz.dearo.partpayment

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.domain.entities.WhatsAppTemplate
import com.carworkz.dearo.utils.Constants
import javax.inject.Inject

class PartPaymentPresenter @Inject constructor(private var view: PartPaymentContract.View?, private val repository: DearODataRepository) : PartPaymentContract.Presenter {
    override fun getPayments(invoiceId: String) {
        view?.showProgressIndicator()
        repository.getInvoiceDetailsById(invoiceId, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(obj: Invoice) {
                view?.dismissProgressIndicator()
                view?.onPaymentFetched(invoice = obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

    override fun updatePayment(
        invoiceId: String,
        jobCardId: String,
        paymentMethod: String,
        amount: Double,
        transactionNumber: String,
        transactionDetails: String,
        bankName: String,
        cardNumber: String,
        drawnOnDate: String,
        chequeDate: String,
        chequeNumber: String,
        remarks: String,
        notifyCustomer: Boolean
    ) {
        view?.showProgressIndicator()
        val shouldNotifyBySms = if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
            false
        } else {
            notifyCustomer
        }
        repository.updatePayment(
            invoiceId=invoiceId,
            paymentType = "full",
            method =paymentMethod,
            amount = amount,
            transactionNumber = transactionNumber,
            transactionDetails = transactionDetails,
            bankName =bankName,
            cardNumber = cardNumber,
            drawnOnDate = drawnOnDate,
            chequeDate = chequeDate,
            chequeNumber = chequeNumber,
           remarks = remarks,
            notifyCustomer = shouldNotifyBySms,
            callback = object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()

                if (notifyCustomer && SharedPrefHelper.isNotifyWhatsappEnabled()) {
                    view?.showProgressIndicator()
                    repository.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_CLOSE, jobCardId, object : DataSource.OnResponseCallback<WhatsAppTemplate> {
                        override fun onSuccess(obj: WhatsAppTemplate) {
                            view?.onPaymentUpdateFinished()
                            view?.dismissProgressIndicator()
                            view?.launchWhatsApp(obj.mobile, obj.text)
                        }

                        override fun onError(error: ErrorWrapper) {
                            view?.dismissProgressIndicator()
                            view?.onPaymentUpdateFinished()
                        }
                    })
                } else {
                    view?.onPaymentUpdateFinished()
                }
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