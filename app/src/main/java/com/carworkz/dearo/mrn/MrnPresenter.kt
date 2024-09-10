package com.carworkz.dearo.mrn

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.JobCard
import javax.inject.Inject

class MrnPresenter @Inject constructor(private var view: MrnContract.View?, private val dataRepo: DearODataRepository) : MrnContract.Presenter {

    override fun getMrnParts(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepo.getJobCardDetails(jobCardId, arrayOf(), object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.displayMrn(obj.mrn?.parts?.filter { it.isIssued })
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
        throw UnsupportedOperationException()
    }

    override fun detachView() {
        view = null
    }
}