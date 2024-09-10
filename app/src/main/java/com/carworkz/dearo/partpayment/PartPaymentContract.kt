package com.carworkz.dearo.partpayment

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Invoice

interface PartPaymentContract {

    interface View : BaseView<Presenter> {
        fun onPaymentFetched(invoice: Invoice)
        fun onPaymentUpdateFinished()
        fun launchWhatsApp(contactNumber: String, message: String)
    }

    interface Presenter : BasePresenter {
        fun getPayments(invoiceId: String)
        fun updatePayment(
            invoiceId: String,
            jobCardId: String,
            paymentMethod: String,
            amount: Double,
            transactionNumber:String,
            transactionDetails:String,
            bankName:String,
            cardNumber:String,
            drawnOnDate:String,
            chequeDate:String,
            chequeNumber:String,
            remarks: String,
            notifyCustomer: Boolean)
    }
}
