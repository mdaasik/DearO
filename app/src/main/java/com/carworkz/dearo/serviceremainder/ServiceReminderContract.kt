package com.carworkz.dearo.serviceremainder

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Remark

interface ServiceReminderContract {
    interface View : BaseView<Presenter> {

        fun moveToNextScreen(pdfs: List<PDF>?)

        fun displayRemarksAndJobs(remarks: List<Remark>, unApprovedJobs: List<RecommendedJob>?)

        fun launchWhatsapp(contactNumber: String, message: String)
    }

    interface Presenter : BasePresenter {

        fun getRemarks(jobCardId: String)

        fun saveInvoiceRemarks(jobCardId: String, remarks: InvoiceRemarks)

        fun getInvoicePDF(invoiceId: String, notify: Boolean?, reminderDate: String?)

        fun closeJobCard(jobCardId: String, reminderDate: String?, notify: Boolean)

        fun saveInformation(jobCardId: String, invoiceId: String?, remarks: InvoiceRemarks, reminderDate: String?, notify: Boolean, actionType: String)
    }
}