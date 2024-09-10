package com.carworkz.dearo.customerapproval

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.CostEstimate
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CustomerApprovalPresenter @Inject constructor(private var view: CustomerApprovalContract.View?, private val repository: DearODataRepository) : CustomerApprovalContract.Presenter, CoroutineScope {

    private val parentJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun getEstimation(jobCardId: String)
    {
        view?.showProgressIndicator()
        repository.getJobCardDetails(jobCardId, arrayOf("vehicle"), object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.dismissProgressIndicator()
                Timber.d("cost estimation " + obj.costEstimate)
                view?.onFetchJobCard(obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun saveEstimation(jobCardId: String, obj: CostEstimate) {
        view?.showProgressIndicator()
        repository.saveCostEstimation(jobCardId, obj, object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.dismissProgressIndicator()
                    view?.onSaveSuccess()

            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
        parentJob.cancel()
    }
}