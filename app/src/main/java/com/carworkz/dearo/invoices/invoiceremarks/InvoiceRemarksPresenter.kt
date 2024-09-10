package com.carworkz.dearo.invoices.invoiceremarks

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.JobCard
import javax.inject.Inject

class InvoiceRemarksPresenter @Inject constructor(private var view: InvoiceRemarksContract.View?, private val dataRepo: DearODataRepository) : InvoiceRemarksContract.Presenter {

    override fun saveInvoiceRemarks(jobCardId: String, remarks: InvoiceRemarks) {
        view?.showProgressIndicator()
        dataRepo.saveRemarks(jobCardId, remarks, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
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

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}