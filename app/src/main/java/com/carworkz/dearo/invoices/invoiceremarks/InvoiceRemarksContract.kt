package com.carworkz.dearo.invoices.invoiceremarks

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Remark

interface InvoiceRemarksContract {

    interface View : BaseView<Presenter> {

        fun moveToNextScreen()
        fun displayRemarksAndJobs(remarks: List<Remark>, unApprovedJobs: List<RecommendedJob>?)
    }

    interface Presenter : BasePresenter {

        fun getRemarks(jobCardId: String)

        fun saveInvoiceRemarks(jobCardId: String, remarks: InvoiceRemarks)
    }
}