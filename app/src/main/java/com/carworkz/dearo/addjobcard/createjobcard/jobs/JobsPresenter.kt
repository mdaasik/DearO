package com.carworkz.dearo.addjobcard.createjobcard.jobs

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.injection.FragmentScoped
import javax.inject.Inject

/**
 * Created by Farhan on 24/8/17.
 */
@FragmentScoped
class JobsPresenter @Inject constructor(private var view: JobContract.View?, private val dataRepository: DearODataRepository) : JobContract.Presenter {

    override fun getSavedJobs(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.getJobCardDetails(jobCardId, arrayOf("vehicle"), object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.displaySavedJobs(obj.jobs)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun saveJobsData(jobCardId: String, obj: Jobs) {
        dataRepository.saveJobsData(jobCardId, obj, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getJobsAndVerbatim(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.getJobsData(jobCardId, object : DataSource.OnResponseCallback<JobAndVerbatim> {
            override fun onSuccess(obj: JobAndVerbatim) {
                view?.displayJobsData(obj)
                view?.displayRemarks(obj.remarks)
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