package com.carworkz.dearo.addjobcard.completejobcard

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Remark

interface CompleteJobCardContract
{
    interface View : BaseView<Presenter>
    {

        fun showDeliveryDateError(message: String?)

        fun showMinCostError(message: String?)

        fun showMaxCostError(message: String?)

        fun displayEstimate(jobCard: JobCard?)

        fun moveToNextScreen()

        fun onRedYellowInvoiceRemarkSuccess()

        fun displayRemarksAndJobs(remarks: List<Remark>, unApprovedJobs: List<RecommendedJob>?)

        fun showAccidentalError(error: String?)

        fun launchWhatsapp(contactNumber: String?, message: String?)
    }

    interface Presenter : BasePresenter
    {

        fun getRemarks(jobCardId: String)

        fun saveInvoiceRemarks(jobCardId: String, remarks: InvoiceRemarks)

        fun getEstimate(jobCardId: String)

        fun completeJobCard(jobCardId: String, dateTime: String, minEstimate: Int, maxEstimate: Int, notify: Boolean, reasonForDelay: String?)

        fun saveEstimate(jobCardId: String, dateTime: String, minEstimate: Int, maxEstimate: Int, status: String?, notify: Boolean)

    }
}